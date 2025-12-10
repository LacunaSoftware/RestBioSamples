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

export interface BioSubjectEnrollmentRequest {
	subjectIdentifier: string;
	faceImage?: BlobModel;
}

export interface BioSubjectAuthenticationRequest {
	faceImage?: BlobModel;
	subject: {
		identifier?: string;
		id?: string;
	};
}

export interface BioSubjectEnrollmentResponse {
	enrollmentId: string;
	success: boolean;
	result?: {
		subjectId: string;
	};
}

export interface BioSubjectAuthenticationResponse {
	sessionId: string;
	success: boolean;
}

@Injectable({ providedIn: 'root' })
export class RestBioService {
	private readonly http = inject(HttpClient);

	// Implemented backend endpoints (only liveness for now)
	startLivenessSession(captureIdentificationDocument = false): Observable<StartBioSessionResponse> {
		return this.http.post<StartBioSessionResponse>(`/sample-api/sessions/liveness?captureIdentificationDocument=${captureIdentificationDocument}`, {});
	}

	completeLivenessSession(ticket: string): Observable<CompleteBioSessionResponse> {
		const body: CompleteBioSessionRequest = { ticket };
		return this.http.post<CompleteBioSessionResponse>(`/sample-api/sessions/liveness/completion`, body);
	}

	// Enrollment session endpoints
	startEnrollmentSession(subjectIdentifier: string, captureIdentificationDocument?: boolean, dangerousOverrideIfAlreadyEnrolled?: boolean): Observable<StartBioSessionResponse> {
		const body: any = {
			subjectIdentifier: subjectIdentifier
		};
		
		if (captureIdentificationDocument !== undefined) {
			body.captureIdentificationDocument = captureIdentificationDocument;
		}
		
		if (dangerousOverrideIfAlreadyEnrolled !== undefined) {
			body.dangerousOverrideIfAlreadyEnrolled = dangerousOverrideIfAlreadyEnrolled;
		}
		
		return this.http.post<StartBioSessionResponse>(`/sample-api/sessions/enrollment`, body);
	}

	getEnrollmentSessionStatus(sessionId: string): Observable<any> {
		return this.http.get(`/sample-api/sessions/enrollment/status?sessionId=${encodeURIComponent(sessionId)}`);
	}

	completeEnrollmentSession(ticket: string): Observable<CompleteBioSessionResponse> {
		const body: CompleteBioSessionRequest = { ticket };
		return this.http.post<CompleteBioSessionResponse>(`/sample-api/sessions/enrollment/completion`, body);
	}

	// Authentication session endpoints
	startAuthenticationSession(bioSubjectReference: BioSubjectReference): Observable<StartBioSessionResponse> {
		return this.http.post<StartBioSessionResponse>(
			`/sample-api/sessions/authentication`,
			bioSubjectReference
		);
	}

	enrollment2d(subjectIdentifier: string, image: string): Observable<BioSubjectEnrollmentResponse> {
		const body: BioSubjectEnrollmentRequest = {
			subjectIdentifier, faceImage: {
				content: image,
				contentType: "image/jpeg" // TODO: you must implement the correct file upload type
			}
		};
		return this.http.post<BioSubjectEnrollmentResponse>(`/api/bio/enrollments`, body);
	}

	authentication2d(subjectIdentifier: string, image: string): Observable<BioSubjectAuthenticationResponse> {
		const body: BioSubjectAuthenticationRequest = {
			subject: {
				identifier: subjectIdentifier,
			},
			faceImage: {
				content: image,
				contentType: "image/jpeg" // TODO: you must implement the correct file upload type
			}
		};
		return this.http.post<BioSubjectAuthenticationResponse>(`/api/bio/authentications`, body);
	}

	getAuthenticationSessionStatus(sessionId: string): Observable<any> {
		return this.http.get(`/sample-api/sessions/authentication/status?sessionId=${encodeURIComponent(sessionId)}`);
	}

	completeAuthenticationSession(ticket: string): Observable<CompleteBioSessionResponse> {
		const body: CompleteBioSessionRequest = { ticket };
		return this.http.post<CompleteBioSessionResponse>(`/sample-api/sessions/authentication/completion`, body);
	}

	getLivenessSessionStatus(sessionId: string): Observable<any> {
		return this.http.get(`/sample-api/sessions/liveness/status?sessionId=${encodeURIComponent(sessionId)}`);
	}
}

