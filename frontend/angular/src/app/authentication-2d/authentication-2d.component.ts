import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, ElementRef, signal, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { RestBioService } from '../services/rest-bio.service';
import { SubjectIdentifierInputComponent } from "../subject-identifier-input/subject-identifier-input.component";
import { MatButtonModule } from '@angular/material/button';

@Component({
	selector: 'app-authentication-2d',
	standalone: true,
	imports: [
		CommonModule,
		FormsModule,
		HttpClientModule,
		SubjectIdentifierInputComponent,
		MatButtonModule
	],
	templateUrl: './authentication-2d.component.html',
	styleUrl: './authentication-2d.component.scss'
})
export class Authentication2dComponent {
	// UI state
	isGetStatusEnabled = signal<boolean>(false);

	// Session data
	sessionId = signal<string | null>(null);
	lastStatus = signal<unknown | null>(null);
	isBusy: boolean = false;
	errorMessage?: string;
	errorDetails = signal<string | null>(null);

	@ViewChild('authentication2dInput') authentication2dInput!: ElementRef<HTMLInputElement>;

	constructor(private readonly bio: RestBioService) { }

	doAuthentication2d(): void {
		this.authentication2dInput.nativeElement.click();
	}

	handleAuthentication2dFile(event: Event): void {
		const input = event.target as HTMLInputElement;

		if (input.files && input.files.length > 0) {
			const file = input.files[0];
			this.convertFileToBase64(file).then(base64 => {
				this.onStart(base64);
			});
		}
	}

	convertFileToBase64(file: File): Promise<string> {
		return new Promise((resolve, reject) => {
			const reader = new FileReader();

			reader.onload = () => {
				const base64 = (reader.result as string).split(',')[1];
				resolve(base64);
			};

			reader.onerror = (error) => reject(error);
			reader.readAsDataURL(file);
		});
	}

	async onStart(image: string): Promise<void> {
		this.resetStateForNewSession();
		this.isBusy = true;

		try {
			const subjectIdentifier = SubjectIdentifierInputComponent.subjectIdentifier;

			this.isGetStatusEnabled.set(false);

			const result = await firstValueFrom(this.bio.authentication2d(subjectIdentifier, image));

			this.sessionId.set(result.sessionId);
			this.isGetStatusEnabled.set(true);

			// Session completed automatically
			if (result.success) {
				console.log('Authentication session completed', result);
				this.isGetStatusEnabled.set(true);
			} else {
				console.error('Failed to complete authentication session', result);
				this.errorMessage = 'Failed to complete authentication session. Please try again.';
				this.setErrorDetails(result);
			}

		} catch (error) {
			console.error('Failed authentication-2d:', error);
			this.errorMessage = 'Failed to start authentication. Please try again.';
			this.setErrorDetails(error);
		} finally {
			this.isBusy = false;
		}
	}

	onGetStatus(): void {
		const id = this.sessionId();
		if (!id) return;

		// Placeholder for future status endpoint implementation
		this.lastStatus.set({ placeholder: true, sessionId: id, sessionType: 'Authentication2d' });
		this.isGetStatusEnabled.set(false);
	}

	private resetStateForNewSession(): void {
		this.isGetStatusEnabled.set(false);
		this.sessionId.set(null);
		this.lastStatus.set(null);
		delete this.errorMessage;
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
