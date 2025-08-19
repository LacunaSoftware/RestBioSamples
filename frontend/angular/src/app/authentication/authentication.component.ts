import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import RestPkiWidget, { BioSessionInterruptedError } from 'lacuna-restpki-widget';
import { firstValueFrom } from 'rxjs';
import { ErrorDisplayComponent } from '../error-display/error-display.component';
import { BioSubjectReference, CompleteBioSessionResponse, RestBioService, StartBioSessionResponse } from '../services/rest-bio.service';
import { SubjectIdentifierInputComponent } from '../subject-identifier-input/subject-identifier-input.component';

@Component({
	selector: 'app-authentication',
	standalone: true,
	imports: [CommonModule, FormsModule, HttpClientModule, SubjectIdentifierInputComponent, ErrorDisplayComponent, MatButtonModule],
	templateUrl: './authentication.component.html',
	styleUrl: './authentication.component.scss'
})
export class AuthenticationComponent {
	// UI state
	

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
			// Create bio subject reference for authentication
			const bioSubjectReference: BioSubjectReference = {
				identifier: SubjectIdentifierInputComponent.subjectIdentifier,
			};

			const res: StartBioSessionResponse = await firstValueFrom(this.bio.startAuthenticationSession(bioSubjectReference));
			this.sessionId = res.sessionId;



			// Automatically perform bio session with widget
			const widget = new RestPkiWidget();

			try {
				const widgetResult = await widget.performBioSession(res.sessionUrl);
				const ticket = widgetResult.completeTicket;

				// Automatically complete the session
				const result: CompleteBioSessionResponse = await firstValueFrom(this.bio.completeAuthenticationSession(ticket));

				// Session completed automatically
				if (result.success) {
					console.log('Authentication session completed', result);

					this.sessionId = result.sessionId;
					
					// Get session status after successful completion
					try {
						const statusResult = await firstValueFrom(this.bio.getAuthenticationSessionStatus(result.sessionId));
						this.lastStatus = statusResult;
						console.log('Authentication session status:', statusResult);
					} catch (statusError) {
						console.error('Failed to get session status:', statusError);
					}
				} else {
					console.error('Failed to complete authentication session', result);
					this.errorDisplay.errorMessage = 'Failed to complete authentication session. Please try again.';
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
			console.error('Failed to start authentication session:', error);
			this.errorDisplay.errorMessage = 'Failed to start authentication session. Please try again.';
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
