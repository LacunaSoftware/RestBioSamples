import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import RestPkiWidget, { BioSessionInterruptedError } from 'lacuna-restpki-widget';
import { firstValueFrom } from 'rxjs';
import { ErrorDisplayComponent } from '../error-display/error-display.component';
import { CompleteBioSessionResponse, RestBioService, StartBioSessionResponse } from '../services/rest-bio.service';

@Component({
	selector: 'app-liveness',
	standalone: true,
	imports: [CommonModule, FormsModule, HttpClientModule, ErrorDisplayComponent, MatButtonModule],
	templateUrl: './liveness.component.html',
	styleUrl: './liveness.component.scss'
})
export class LivenessComponent {

	// Session data
	sessionId: string | null = null;
	lastStatus: unknown | null = null;
	isLoading: boolean = false;

	@ViewChild(ErrorDisplayComponent) errorDisplay!: ErrorDisplayComponent;

	constructor(private readonly bio: RestBioService) { }

	async onStart(): Promise<void> {
		this.resetStateForNewSession();
		this.isLoading = true;

		try {
			const res: StartBioSessionResponse = await firstValueFrom(this.bio.startLivenessSession());
			this.sessionId = res.sessionId;

			// Automatically perform bio session with widget
			const widget = new RestPkiWidget();

			try {
				const widgetResult = await widget.performBioSession(res.sessionUrl);
				const ticket = widgetResult.completeTicket;

				// Automatically complete the session
				const result: CompleteBioSessionResponse = await firstValueFrom(this.bio.completeLivenessSession(ticket));

				// Session completed automatically
				if (result.success) {
					console.log('Session completed', result);

					this.sessionId = result.sessionId;

					// Get session status after successful completion
					try {
						const statusResult = await firstValueFrom(this.bio.getLivenessSessionStatus(result.sessionId));
						this.lastStatus = statusResult;
						console.log('Liveness session status:', statusResult);
					} catch (statusError) {
						console.error('Failed to get session status:', statusError);
					}
				} else {
					console.error('Failed to complete session', result);
					this.errorDisplay.errorMessage = 'Failed to complete liveness session. Please try again.';
					this.errorDisplay.setErrorDetails(result);
				}
			} catch (error) {
				if (error instanceof BioSessionInterruptedError) {
					console.warn(`Bio session interrupted by user: ${error.message} (${error.reason})`);
					this.errorDisplay.errorMessage = `Bio session interrupted by user: ${error.message} (${error.reason})`;
					this.errorDisplay.setErrorDetails(error);
				} else {
					console.error('Bio session error:', error);
					this.errorDisplay.errorMessage = 'Bio session failed. Please try again.';
					this.errorDisplay.setErrorDetails(error);
				}
			}

		} catch (error) {
			console.error('Failed to start liveness session:', error);
			this.errorDisplay.errorMessage = 'Failed to start liveness session. Please try again.';
			this.errorDisplay.setErrorDetails(error);
		} finally {
			this.isLoading = false;
		}
	}



	private resetStateForNewSession(): void {
		this.sessionId = null;
		this.lastStatus = null;
		this.errorDisplay?.clearErrors();
	}


}
