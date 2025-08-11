import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BioSessionService, BioSessionType, StartBioSessionResponse } from './services/bio-session.service';
import RestPkiWidget, { BioSessionInterruptedError, RestPkiWidgetError } from 'lacuna-restpki-widget';
import { firstValueFrom } from 'rxjs';

@Component({
	selector: 'app-root',
	standalone: true,
	imports: [CommonModule, FormsModule, HttpClientModule],
	templateUrl: './app.component.html',
	styleUrl: './app.component.scss'
})
export class AppComponent {
	title = 'Bio Sessions';

	// UI state
	sessionTypes: BioSessionType[] = ['Liveness', 'Enrollment', 'Authentication', 'IdentificationDocumentCapture'];
	selectedType = signal<BioSessionType>('Liveness');

	// Controlled flow flags
	isStartEnabled = signal<boolean>(true);
	isGetStatusEnabled = signal<boolean>(false);
	isCompleteEnabled = signal<boolean>(false);

	// Session data
	sessionId = signal<string | null>(null);
	sessionUrl = signal<string | null>(null);
	ticket = signal<string | null>(null);
	lastStatus = signal<unknown | null>(null);

	currentLabel = computed(() => this.selectedType());

	constructor(private readonly bio: BioSessionService) { }

	onStart(): void {
		const type = this.selectedType();
		this.resetStateForNewSession();

		if (type === 'Liveness') {
			this.bio.startLivenessSession().subscribe(async (res: StartBioSessionResponse) => {
				this.sessionId.set(res.sessionId);
				this.sessionUrl.set(res.sessionUrl);

				let widget = new RestPkiWidget();

				try {
					let ticket = (await widget.performBioSession(res.sessionUrl)).completeTicket;
					let result = await firstValueFrom(this.bio.completeLivenessSession(ticket));

					if (result.success) {
						// handle success
					} else {
						// hanlde failure ( success = false )
					}

					return result;
				} catch (error) {
					if (error instanceof BioSessionInterruptedError) {

						//this.messageService.showWarning(`${error.message} (${error.reason})`);
					} else {
						//this.messageService.showError('An unknown error has occurred');
						console.error(error);
					}
				}

				// Autofill the ticket field with the returned sessionId so user can fetch status
				this.ticket.set(res.sessionId);
				// Enable next step
				this.isStartEnabled.set(false);
				this.isGetStatusEnabled.set(true);
			});
			return;
		}

		// Future implementations
		if (type === 'Enrollment') {
			this.bio.startEnrollmentSession().subscribe(() => {
				// For consistency, when backend returns an id, set it to ticket
				// this.ticket.set(returnedId as string);
				this.isStartEnabled.set(false);
				this.isGetStatusEnabled.set(true);
			});
			return;
		}

		if (type === 'Authentication') {
			this.bio.startAuthenticationSession().subscribe(() => {
				// this.ticket.set(returnedId as string);
				this.isStartEnabled.set(false);
				this.isGetStatusEnabled.set(true);
			});
			return;
		}

		if (type === 'IdentificationDocumentCapture') {
			this.bio.startIdentificationDocumentCaptureSession().subscribe(() => {
				// this.ticket.set(returnedId as string);
				this.isStartEnabled.set(false);
				this.isGetStatusEnabled.set(true);
			});
			return;
		}
	}

	onGetStatus(): void {
		const type = this.selectedType();
		const id = this.ticket();
		if (!id) return;

		if (type === 'Liveness') {
			// No backend route yet; keep the flow but simulate placeholder
			// When implemented, replace with: this.bio.getLivenessSessionStatus(id)
			this.lastStatus.set({ placeholder: true, sessionId: id });
			// Enable completion (Widget would return a ticket at completion time)
			this.isGetStatusEnabled.set(false);
			this.isCompleteEnabled.set(true);
			return;
		}

		if (type === 'Enrollment') {
			this.bio.getEnrollmentSessionStatus(id).subscribe((status) => {
				this.lastStatus.set(status);
				this.isGetStatusEnabled.set(false);
				this.isCompleteEnabled.set(true);
			});
			return;
		}

		if (type === 'Authentication') {
			this.bio.getAuthenticationSessionStatus(id).subscribe((status) => {
				this.lastStatus.set(status);
				this.isGetStatusEnabled.set(false);
				this.isCompleteEnabled.set(true);
			});
			return;
		}

		if (type === 'IdentificationDocumentCapture') {
			this.bio.getIdentificationDocumentCaptureStatus(id).subscribe((status) => {
				this.lastStatus.set(status);
				this.isGetStatusEnabled.set(false);
				this.isCompleteEnabled.set(true);
			});
			return;
		}
	}

	onComplete(): void {
		const type = this.selectedType();
		const ticket = this.ticket();
		if (!ticket) return;

		if (type === 'Liveness') {
			this.bio.completeLivenessSession(ticket).subscribe((result) => {
				this.lastStatus.set(result);
				this.isCompleteEnabled.set(false);
			});
			return;
		}

		if (type === 'Enrollment') {
			this.bio.completeEnrollmentSession(ticket).subscribe((result) => {
				this.lastStatus.set(result);
				this.isCompleteEnabled.set(false);
			});
			return;
		}

		if (type === 'Authentication') {
			this.bio.completeAuthenticationSession(ticket).subscribe((result) => {
				this.lastStatus.set(result);
				this.isCompleteEnabled.set(false);
			});
			return;
		}

		if (type === 'IdentificationDocumentCapture') {
			this.bio.completeIdentificationDocumentCaptureSession(ticket).subscribe((result) => {
				this.lastStatus.set(result);
				this.isCompleteEnabled.set(false);
			});
			return;
		}
	}

	private resetStateForNewSession(): void {
		this.isStartEnabled.set(true);
		this.isGetStatusEnabled.set(false);
		this.isCompleteEnabled.set(false);
		this.sessionId.set(null);
		this.sessionUrl.set(null);
		this.ticket.set(null);
		this.lastStatus.set(null);
	}
}
