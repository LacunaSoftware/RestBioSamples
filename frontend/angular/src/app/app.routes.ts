import { Routes } from '@angular/router';
import { AuthenticationComponent } from './authentication/authentication.component';
import { EnrollmentComponent } from './enrollment/enrollment.component';
import { LivenessComponent } from './liveness/liveness.component';
import { Enrollment2dComponent } from './enrollment-2d/enrollment-2d.component';
import { Authentication2dComponent } from './authentication-2d/authentication-2d.component';

export const routes: Routes = [
	{ path: '', redirectTo: '/liveness', pathMatch: 'full' },
	{ path: 'liveness', component: LivenessComponent },
	{ path: 'enrollment', component: EnrollmentComponent },
	{ path: 'enrollment-2d', component: Enrollment2dComponent },
	{ path: 'authentication', component: AuthenticationComponent },
	{ path: 'authentication-2d', component: Authentication2dComponent },
];
