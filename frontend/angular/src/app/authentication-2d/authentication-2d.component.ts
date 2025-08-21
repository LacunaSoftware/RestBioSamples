import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { firstValueFrom } from 'rxjs';
import { ErrorDisplayComponent } from '../error-display/error-display.component';
import { RestBioService } from '../services/rest-bio.service';
import { SubjectIdentifierInputComponent } from "../subject-identifier-input/subject-identifier-input.component";

@Component({
	selector: 'app-authentication-2d',
	standalone: true,
	imports: [
		CommonModule,
		FormsModule,
		HttpClientModule,
		SubjectIdentifierInputComponent,
		MatButtonModule,
		ErrorDisplayComponent
	],
	templateUrl: './authentication-2d.component.html',
	styleUrl: './authentication-2d.component.scss'
})
export class Authentication2dComponent {
	
	// Session data
	sessionId: string | null = null;
	lastStatus: unknown | null = null;
	isLoading: boolean = false;

	@ViewChild('authentication2dInput') authentication2dInput!: ElementRef<HTMLInputElement>;
	@ViewChild(ErrorDisplayComponent) errorDisplay!: ErrorDisplayComponent;

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
		this.isLoading = true;

		try {
			const subjectIdentifier = SubjectIdentifierInputComponent.subjectIdentifier;



			const result = await firstValueFrom(this.bio.authentication2d(subjectIdentifier, image));

			this.sessionId = result.sessionId;

			// Session completed automatically
			if (result.success) {
				console.log('Authentication session completed', result);

				this.sessionId = result.sessionId;

				// Get session status after successful completion
				try {
					const statusResult = await firstValueFrom(this.bio.getAuthenticationSessionStatus(result.sessionId));
					this.lastStatus = statusResult;
					console.log('Authentication 2D session status:', statusResult);
				} catch (statusError) {
					console.error('Failed to get session status:', statusError);
				}
			} else {
				console.error('Failed to complete authentication session', result);
				this.errorDisplay.errorMessage = 'Failed to complete authentication session. Please try again.';
				this.errorDisplay.setErrorDetails(result);
			}

		} catch (error) {
			console.error('Failed authentication-2d:', error);
			this.errorDisplay.errorMessage = 'Failed to start authentication. Please try again.';
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
