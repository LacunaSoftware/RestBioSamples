import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import RestPkiWidget, { BioSessionInterruptedError } from 'lacuna-restpki-widget';
import { firstValueFrom } from 'rxjs';
import { BioSessionService, CompleteBioSessionResponse, StartBioSessionResponse } from '../services/bio-session.service';

@Component({
	selector: 'app-enrollment',
	standalone: true,
	imports: [CommonModule, FormsModule, HttpClientModule],
	templateUrl: './enrollment.component.html',
	styleUrl: './enrollment.component.scss'
})
export class EnrollmentComponent {
	// UI state
	isStartEnabled = signal<boolean>(true);
	isGetStatusEnabled = signal<boolean>(false);

	// Session data
	sessionId = signal<string | null>(null);
	lastStatus = signal<unknown | null>(null);
	isLoading = signal<boolean>(false);
	errorMessage = signal<string | null>(null);
	errorDetails = signal<string | null>(null);

	constructor(private readonly bio: BioSessionService) { }

	async onStart(): Promise<void> {
		this.resetStateForNewSession();
		this.isLoading.set(true);

		try {
			// Generate a unique subject identifier for this enrollment session
			const subjectIdentifier = 'user-' + Date.now();
			const captureIdentificationDocument = false;
			const dangerousOverrideIfAlreadyEnrolled = true;
			const res: StartBioSessionResponse = await firstValueFrom(this.bio.startEnrollmentSession(subjectIdentifier, captureIdentificationDocument, dangerousOverrideIfAlreadyEnrolled));
			this.sessionId.set(res.sessionId);

			// Enable status check
			this.isStartEnabled.set(false);
			this.isGetStatusEnabled.set(false);

			// Automatically perform bio session with widget
			const widget = new RestPkiWidget();

			try {
				const widgetResult = await widget.performBioSession(res.sessionUrl);
				const ticket = widgetResult.completeTicket;

				// Automatically complete the session
				const result: CompleteBioSessionResponse = await firstValueFrom(this.bio.completeEnrollmentSession(ticket));

				// Session completed automatically
				if (result.success) {
					console.log('Enrollment session completed', result);
					this.isGetStatusEnabled.set(true);

					// TODO: Get the sessionId from the backend
					this.sessionId.set(result.sessionId);
				} else {
					console.error('Failed to complete enrollment session', result);
					this.errorMessage.set('Failed to complete enrollment session. Please try again.');
					this.setErrorDetails(result);
				}
			} catch (error) {
				if (error instanceof BioSessionInterruptedError) {
					console.warn(`Bio session interrupted by user: ${error.message} (${error.reason})`);
				} else {
					console.error('Bio session error:', error);
					this.errorMessage.set('Bio session failed. Please try again.');
					this.setErrorDetails(error);
				}
			}

		} catch (error) {
			console.error('Failed to start enrollment session:', error);
			this.errorMessage.set('Failed to start enrollment session. Please try again.');
			this.setErrorDetails(error);
		} finally {
			this.isLoading.set(false);
		}
	}

	onGetStatus(): void {
		const id = this.sessionId();
		if (!id) return;

		// Placeholder for future status endpoint implementation
		this.lastStatus.set({ placeholder: true, sessionId: id, sessionType: 'Enrollment' });
		this.isGetStatusEnabled.set(false);
	}

	private resetStateForNewSession(): void {
		this.isStartEnabled.set(true);
		this.isGetStatusEnabled.set(false);
		this.sessionId.set(null);
		this.lastStatus.set(null);
		this.errorMessage.set(null);
		this.errorDetails.set(null);
	}

	private setErrorDetails(error: any): void {
		let details = '';

		if (error) {
			// Handle HTTP error responses
			if (error.error) {
				details += `Error: ${JSON.stringify(error.error, null, 2)}\n`;
			}

			// Handle standard error properties
			if (error.message) {
				details += `Message: ${error.message}\n`;
			}

			if (error.stack) {
				details += `Stack: ${error.stack}\n`;
			}

			// Handle inner exceptions or nested errors
			if (error.innerException) {
				details += `Inner Exception: ${JSON.stringify(error.innerException, null, 2)}\n`;
			}

			// For any other properties
			if (typeof error === 'object') {
				details += `Full Error: ${JSON.stringify(error, null, 2)}`;
			} else {
				details += `Error: ${error}`;
			}
		}

		this.errorDetails.set(details || 'No additional error details available.');
	}
}
