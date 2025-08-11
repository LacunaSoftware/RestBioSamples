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

@Injectable({ providedIn: 'root' })
export class BioSessionService {
  private readonly http = inject(HttpClient);

  // Implemented backend endpoints (only liveness for now)
  startLivenessSession(captureIdentificationDocument = false): Observable<StartBioSessionResponse> {
    return this.http.post<StartBioSessionResponse>(`/api/bio/session/liveness?captureIdentificationDocument=${captureIdentificationDocument}`, {});
  }

  completeLivenessSession(ticket: string): Observable<unknown> {
    const body: CompleteBioSessionRequest = { ticket };
    return this.http.post(`/api/bio/session/liveness/complete`, body);
  }

  // Placeholders for future endpoints
  startEnrollmentSession(): Observable<unknown> {
    return this.http.post(`/api/bio/session/enrollment`, {});
  }

  getEnrollmentSessionStatus(sessionId: string): Observable<unknown> {
    return this.http.get(`/api/bio/session/enrollment/${encodeURIComponent(sessionId)}/status`);
  }

  completeEnrollmentSession(ticket: string): Observable<unknown> {
    return this.http.post(`/api/bio/session/enrollment/complete`, { ticket });
  }

  startAuthenticationSession(): Observable<unknown> {
    return this.http.post(`/api/bio/session/authentication`, {});
  }

  getAuthenticationSessionStatus(sessionId: string): Observable<unknown> {
    return this.http.get(`/api/bio/session/authentication/${encodeURIComponent(sessionId)}/status`);
  }

  completeAuthenticationSession(ticket: string): Observable<unknown> {
    return this.http.post(`/api/bio/session/authentication/complete`, { ticket });
  }

  startIdentificationDocumentCaptureSession(): Observable<unknown> {
    return this.http.post(`/api/bio/session/id-capture`, {});
  }

  getIdentificationDocumentCaptureStatus(sessionId: string): Observable<unknown> {
    return this.http.get(`/api/bio/session/id-capture/${encodeURIComponent(sessionId)}/status`);
  }

  completeIdentificationDocumentCaptureSession(ticket: string): Observable<unknown> {
    return this.http.post(`/api/bio/session/id-capture/complete`, { ticket });
  }
}

