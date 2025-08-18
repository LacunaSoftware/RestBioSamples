import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';

@Component({
	selector: 'app-root',
	standalone: true,
	imports: [CommonModule, RouterOutlet],
	templateUrl: './app.component.html',
	styleUrl: './app.component.scss'
})
export class AppComponent {
	title = 'Bio Sessions';

	sessionTypes: string[] = ['Liveness', 'Enrollment', 'Enrollment2d', 'Authentication', 'IdentificationDocumentCapture'];

	constructor(private router: Router) { }

	onSessionTypeChange(type: string): void {
		const routeMap: Record<string, string> = {
			'Liveness': '/liveness',
			'Enrollment': '/enrollment',
			'Enrollment2d': '/enrollment-2d',
			'Authentication': '/authentication',
			'IdentificationDocumentCapture': '/identification-document-capture'
		};
		this.router.navigate([routeMap[type]]);
	}
}
