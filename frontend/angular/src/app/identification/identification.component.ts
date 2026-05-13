import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import RestPkiWidget, { BioSessionInterruptedError } from 'lacuna-restpki-widget';
import { firstValueFrom } from 'rxjs';
import { ErrorDisplayComponent } from '../error-display/error-display.component';
import { CompleteBioSessionResponse, RestBioService, StartBioSessionResponse } from '../services/rest-bio.service';
import { SubjectIdentifierInputComponent } from '../subject-identifier-input/subject-identifier-input.component';

@Component({
  selector: 'app-identification',
  standalone: true,
  imports: [
			CommonModule,
			FormsModule,
			HttpClientModule,
			SubjectIdentifierInputComponent,
			MatButtonModule,
			ErrorDisplayComponent
  ],
  templateUrl: './identification.component.html',
  styleUrl: './identification.component.scss'
})
export class IdentificationComponent {

	// Session data
	identificationId: string | null = null;
	lastStatus: unknown | null = null;
	isLoading: boolean = false;

	@ViewChild(ErrorDisplayComponent) errorDisplay!: ErrorDisplayComponent;

	constructor(private readonly bio: RestBioService) { }

	async onStart(): Promise<void> {
		this.resetStateForNewSession();
		this.isLoading = true;

		try {

			const res = await firstValueFrom(this.bio.startIdentificationSession());
			this.identificationId = res.sessionId;

			// Automatically perform bio session with widget
			const widget = new RestPkiWidget();

			try {
				const widgetResult = await widget.performBioSession(res.sessionUrl);
				const ticket = widgetResult.completeTicket;

				// Automatically complete the session
				const result: CompleteBioSessionResponse = await firstValueFrom(this.bio.completeIdentificationSession(ticket));

				// Session completed automatically
				if (result.success) {
					console.log('Identification session completed', result);

					this.identificationId = result.sessionId;

					// Get session status after successful completion
					try {
						const statusResult = await firstValueFrom(this.bio.getIdentificationSessionStatus(result.sessionId));
						this.lastStatus = statusResult;
						console.log('Identification session status:', statusResult);
					} catch (statusError) {
						console.error('Failed to get session status:', statusError);
					}
				} else {
					console.error('Failed to complete identification session', result);
					this.errorDisplay.errorMessage = 'Failed to complete identification session. Please try again.';
					this.errorDisplay.setErrorDetails(result);
				}
			} catch (error) {
				if (error instanceof BioSessionInterruptedError) {
					console.warn(`Bio session interrupted by user: ${error.message} (${error.reason})`);
				} else {
					console.error('Bio session error:', error);
					this.errorDisplay.errorMessage = 'Bio session failed. Please try again.';
					this.errorDisplay.setErrorDetails(error);
				}
			}

		} catch (error) {
			console.error('Failed to start identification session:', error);
			this.errorDisplay.errorMessage = 'Failed to start identification session. Please try again.';
			this.errorDisplay.setErrorDetails(error);
		} finally {
			this.isLoading = false;
		}
	}

	private resetStateForNewSession(): void {
		this.identificationId = null;
		this.lastStatus = null;
		this.errorDisplay?.clearErrors();
	}
}
