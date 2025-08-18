import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
	selector: 'app-subject-identifier-input',
	standalone: true,
	imports: [
		CommonModule,
		FormsModule,
		MatFormFieldModule,
		MatInputModule,
		ReactiveFormsModule
	],
	templateUrl: './subject-identifier-input.component.html',
	styleUrl: './subject-identifier-input.component.scss'
})
export class SubjectIdentifierInputComponent {

	public static subjectIdentifier = "bio-sample-subject";

	get subjectIdentifier(): string {
		return SubjectIdentifierInputComponent.subjectIdentifier;
	}

	set subjectIdentifier(value: string) {
		SubjectIdentifierInputComponent.subjectIdentifier = value;
	}

}