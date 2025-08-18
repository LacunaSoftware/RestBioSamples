import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export type BioSessionType = 'Liveness' | 'Enrollment' | 'Authentication' | 'IdentificationDocumentCapture';

export interface StartBioSessionResponse {
	sessionUrl: string;
	sessionId: string;
	sessionType: string;
}

export interface CompleteBioSessionRequest {
	ticket: string;
}

export interface CompleteBioSessionResponse {
	success: boolean;
	sessionId: string;
	result?: any;
}

export interface BioSubjectReference {
	id?: string;
	identifier?: string;
}

export interface BlobModel {
	content: string;
	contentType: string;
}

export interface BioSubjectEnrollment2dRequest {
	subjectIdentifier: string;
	faceImage?: BlobModel;
}

export interface BioSubjectAuthentication2dRequest {
	faceImage?: BlobModel;
	subject: {
		identifier?: string;
		id?: string;
	};
}

export interface BioSubjectEnrollment2dResponse {
	sessionId: string;
	success: boolean;
	result?: {
		subjectId: string;
	};
}

export interface BioSubjectAuthentication2dResponse {
	sessionId: string;
	success: boolean;
}

// TODO: Rename to RestBioService
@Injectable({ providedIn: 'root' })
export class RestBioService {
	private readonly http = inject(HttpClient);

	// Implemented backend endpoints (only liveness for now)
	startLivenessSession(captureIdentificationDocument = false): Observable<StartBioSessionResponse> {
		return this.http.post<StartBioSessionResponse>(`/api/bio/session/liveness?captureIdentificationDocument=${captureIdentificationDocument}`, {});
	}

	completeLivenessSession(ticket: string): Observable<CompleteBioSessionResponse> {
		const body: CompleteBioSessionRequest = { ticket };
		return this.http.post<CompleteBioSessionResponse>(`/api/bio/session/liveness/complete`, body);
	}

	// Enrollment session endpoints
	startEnrollmentSession(subjectIdentifier: string, captureIdentificationDocument?: boolean, dangerousOverrideIfAlreadyEnrolled?: boolean): Observable<StartBioSessionResponse> {
		const params = new URLSearchParams();
		params.append('subjectIdentifier', subjectIdentifier);
		if (captureIdentificationDocument !== undefined) {
			params.append('captureIdentificationDocument', captureIdentificationDocument.toString());
		}
		if (dangerousOverrideIfAlreadyEnrolled !== undefined) {
			params.append('dangerousOverrideIfAlreadyEnrolled', dangerousOverrideIfAlreadyEnrolled.toString());
		}
		const queryString = params.toString();
		return this.http.post<StartBioSessionResponse>(`/api/bio/session/enrollment?${queryString}`, {});
	}

	getEnrollmentSessionStatus(sessionId: string): Observable<any> {
		return this.http.get(`/api/bio/session/enrollment/${encodeURIComponent(sessionId)}/status`);
	}

	completeEnrollmentSession(ticket: string): Observable<CompleteBioSessionResponse> {
		const body: CompleteBioSessionRequest = { ticket };
		return this.http.post<CompleteBioSessionResponse>(`/api/bio/session/enrollment/complete`, body);
	}

	// Authentication session endpoints
	startAuthenticationSession(bioSubjectReference: BioSubjectReference): Observable<StartBioSessionResponse> {
		const params = new URLSearchParams();
		if (bioSubjectReference.id) {
			params.append('bioSubjectReference.id', bioSubjectReference.id);
		}
		if (bioSubjectReference.identifier) {
			params.append('bioSubjectReference.identifier', bioSubjectReference.identifier);
		}
		const queryString = params.toString();
		return this.http.post<StartBioSessionResponse>(`/api/bio/session/authentication?${queryString}`, {});
	}

	enrollment2d(subjectIdentifier: string, image: string): Observable<BioSubjectEnrollment2dResponse> {
		const body: BioSubjectEnrollment2dRequest = {
			subjectIdentifier, faceImage: {
				content: image,
				contentType: "image/jpeg" // TODO: make changeable
			}
		};
		return this.http.post<BioSubjectEnrollment2dResponse>(`/api/bio/enrollment-2d`, body);
	}

	authentication2d(subjectIdentifier: string, image: string): Observable<BioSubjectAuthentication2dResponse> {
		const body: BioSubjectAuthentication2dRequest = {
			subject: {
				identifier: subjectIdentifier,
			},
			faceImage: {
				content: image,
				contentType: "image/jpeg" // TODO: make changeable
			}
		};
		return this.http.post<BioSubjectAuthentication2dResponse>(`/api/bio/authentication-2d`, body);
	}

	getAuthenticationSessionStatus(sessionId: string): Observable<any> {
		return this.http.get(`/api/bio/session/authentication/${encodeURIComponent(sessionId)}/status`);
	}

	completeAuthenticationSession(ticket: string): Observable<CompleteBioSessionResponse> {
		const body: CompleteBioSessionRequest = { ticket };
		return this.http.post<CompleteBioSessionResponse>(`/api/bio/session/authentication/complete`, body);
	}

	// Identification Document Capture session endpoints
	startIdentificationDocumentCaptureSession(): Observable<StartBioSessionResponse> {
		return this.http.post<StartBioSessionResponse>(`/api/bio/session/id-capture`, {});
	}

	getIdentificationDocumentCaptureStatus(sessionId: string): Observable<any> {
		return this.http.get(`/api/bio/session/id-capture/${encodeURIComponent(sessionId)}/status`);
	}

	completeIdentificationDocumentCaptureSession(ticket: string): Observable<CompleteBioSessionResponse> {
		const body: CompleteBioSessionRequest = { ticket };
		return this.http.post<CompleteBioSessionResponse>(`/api/bio/session/id-capture/complete`, body);
	}
}

