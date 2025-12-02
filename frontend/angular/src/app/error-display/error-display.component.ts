import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
	selector: 'app-error-display',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './error-display.component.html',
	styleUrl: './error-display.component.scss'
})
export class ErrorDisplayComponent {
	@Input() errorMessage: string | null = null;
	@Input() errorDetails: string | null = null;

	/**
	 * Sets error details by processing various error formats
	 * @param error The error object to process
	 */
	setErrorDetails(error: any): void {
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

		this.errorDetails = details || 'No additional error details available.';
	}

	/**
	 * Clears all error states
	 */
	clearErrors(): void {
		this.errorMessage = null;
		this.errorDetails = null;
	}
}
