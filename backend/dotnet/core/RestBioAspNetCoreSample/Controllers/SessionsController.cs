using Lacuna.RestPki.Api.Bio;
using Lacuna.RestPki.Api.Bio.Sessions;
using Lacuna.RestPki.Client;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using RestBioAspNetCoreSample.Configuration;

namespace RestBioAspNetCoreSample.Controllers {

	[ApiController]
	[Route("/api/bio/sessions")]
	public class SessionsController(

		IRestBioService restBioService,
		IOptions<ExampleConfig> exampleConfig

	) : ControllerBase {

		[HttpPost("liveness")]
		public async Task<StartBioSessionResponse> StartLivenessSessionAsync([FromQuery] bool captureIdentificationDocument = false) {

			// This is an example of how to start a liveness session.
			// You must implement your own security measures to ensure that only users
			// you want to have access to this endpoint can call it.

			// The response of the following call contains the session URL that
			// will be loaded in the Widget to start the biometric session.

			var response = await restBioService.StartLivenessSessionAsync(new() {
				TrustedOrigin = exampleConfig.Value.TrustedOrigin,
				CaptureIdentificationDocument = captureIdentificationDocument,
				// additional request properties
				// FaceLivenessProvider = Lacuna.RestPki.Api.FaceLivenessProviders.FortFaceSdkLiveness
			});

			// Although not mandatory, you may want to save the SessionId in your database
			// along with your user/session information, as you can use this ID to retrieve
			// the status of the session by later using the GetLivenessSessionStatusAsync()
			// method. But at the end of the session, the Widget will return you a ticket
			// that you can use to retrieve the session status without needing to store
			// the SessionId.

			// Avaliable properties of the response:
			_ = response.SessionUrl;    // The URL to be loaded in the Widget to start the biometric session.
			_ = response.SessionId;     // The ID of the session.
			_ = response.SessionType;   // The type of the session (Liveness).

			return response;
		}

		[HttpPost("liveness/completion")]
		public async Task<LivenessSessionStatusModel> CompleteLivenessSessionAsync(CompleteBioSessionRequest request) {

			// This is an example of how to complete a liveness session.
			// You must implement your own security measures to ensure that only users
			// you want to have access to this endpoint can call it.

			// By calling the following endpoint, you will get the final status of the
			// biometric session.

			var result = await restBioService.CompleteLivenessSessionAsync(request.Ticket);

			// Avaliable properties of the result:
			var sessionId = result.SessionId;       // The ID of the session.
			_ = result.FaceLivenessStatus.AttemptCount; // The number of face liveness attempts the user made.
			_ = result.FaceLivenessStatus.Success;      // Whether the user passed the face liveness test or not.

			// If you called the session with CaptureIdentificationDocument = true,
			// you can also check if the user captured an ID document:
			if (result.IdCaptureStatus != null) {
				_ = result.IdCaptureStatus.Success;     // Whether the user captured an ID document.
				_ = result.IdCaptureStatus.MatchedFace; // Whether the face on the ID document matched the user's face.
			}

			var success = result.Success;           // Whether the biometric session was successful or not.

			if (success == true) {
				// The biometric session was successful and the user passed the liveness test.

				if (result.ResultDataAvailable) {
					// When the session has ResultDataAvailable = true, it means that
					// you can now retrieve the images captured during the session.

					var resultData = await restBioService.GetSessionResultDataAsync(sessionId);

					// Avaliable properties of the resultData:
					_ = resultData.FaceData?.FaceImage.Content;             // The image of the user's face captured during the liveness test
					_ = resultData.FaceData?.FaceImage.ContentType;         // The content type of the face image (e.g. "image/jpeg")

					_ = resultData.DocumentData?.FrontImage.Content;        // The image of the front side of the ID document captured during the session
					_ = resultData.DocumentData?.FrontImage.ContentType;    // The content type of the front side image (e.g. "image/jpeg")


					_ = resultData.DocumentData?.FaceCropImage?.Content;    // The cropped image of the face on the ID document captured during the session.
					_ = resultData.DocumentData?.FaceCropImage?.ContentType;// The content type of the cropped face image (e.g. "image/jpeg")

					_ = resultData.DocumentData?.BackImage?.Content;        // The image of the back side of the ID document captured during the session (may be null)
					_ = resultData.DocumentData?.BackImage?.ContentType;    // The content type of the back side image (e.g. "image/jpeg") (may be null)
				}

			} else if (success == false) {
				// The biometric session was completed, but it was not successfull.
				// Here you may want to retry or increase an attempt counter of the user.

			} else {
				// The biometric session is still in progress. This should not happen here,
				// as the Widget will only provide a complete ticket when the session is completed
				// (either successfully or not).
			}

			return result;
		}

		[HttpGet("liveness/status")]
		public async Task<BioSessionResultDataModel> GetLivenessSessionStatusAsync(Guid sessionId) {
			return await restBioService.GetSessionResultDataAsync(sessionId);
		}

		[HttpPost("enrollment")]
		public async Task<StartBioSessionResponse> StartEnrollmentSessionAsync(
			[FromQuery] string subjectIdentifier,
			[FromQuery] bool? captureIdentificationDocument = false,
			[FromQuery] bool? dangerousOverrideIfAlreadyEnrolled = false
		) {

			// This is an example of how to start an enrollment session.
			// You must implement your own security measures to ensure that only users
			// you want to have access to this endpoint can call it.

			// The response of the following call contains the session URL that
			// will be loaded in the Widget to start the biometric session.

			var response = await restBioService.StartEnrollmentSessionAsync(new() {
				TrustedOrigin = exampleConfig.Value.TrustedOrigin,
				SubjectIdentifier = subjectIdentifier,
				// Additional properties for enrollment:
				CaptureIdentificationDocument = captureIdentificationDocument ?? false,
				DangerousOverrideIfAlreadyEnrolled = dangerousOverrideIfAlreadyEnrolled ?? false,

			});

			// Although not mandatory, you may want to save the SessionId in your database
			// along with your user/session information, as you can use this ID to retrieve
			// the status of the session by later using the GetEnrollmentSessionStatusAsync()
			// method. But at the end of the session, the Widget will return you a ticket
			// that you can use to retrieve the session status without needing to store
			// the SessionId.

			// Available properties of the response:
			_ = response.SessionUrl;    // The URL to be loaded in the Widget to start the biometric session.
			_ = response.SessionId;     // The ID of the session.
			_ = response.SessionType;   // The type of the session (Enrollment).

			return response;
		}

		[HttpPost("enrollment/completion")]
		public async Task<BioEnrollmentSessionStatusModel> CompleteEnrollmentSessionAsync(CompleteBioSessionRequest request) {

			// This is an example of how to complete an enrollment session.
			// You must implement your own security measures to ensure that only users
			// you want to have access to this endpoint can call it.

			// By calling the following endpoint, you will get the final status of the
			// biometric session.

			var result = await restBioService.CompleteEnrollmentSessionAsync(request.Ticket);

			// Available properties of the result:
			var sessionId = result.SessionId;       // The ID of the session.

			// Enrollment-specific properties (the exact properties may vary based on the actual model structure):
			// Note: The properties below are based on the liveness model and may need adjustment for enrollment
			// You should verify the actual properties available in BioEnrollmentSessionStatusModel

			var success = result.Success;           // Whether the biometric session was successful or not.

			if (success == true) {
				// The biometric session was successful and the user was enrolled.

				// You may want to store SubjectID to be used later
				_ = result.SubjectId;
			} else if (success == false) {
				// The biometric session was completed, but it was not successful.
				// Here you may want to retry or increase an attempt counter of the user.

			} else {
				// The biometric session is still in progress. This should not happen here,
				// as the Widget will only provide a complete ticket when the session is completed
				// (either successfully or not).
			}

			return result;
		}

		[HttpGet("enrollment/status")]
		public async Task<BioEnrollmentSessionStatusModel> GetEnrollmentSessionStatusAsync(Guid sessionId) {
			return await restBioService.GetEnrollmentSessionStatusAsync(sessionId);
		}

		[HttpPost("authentication")]
		public async Task<StartBioSessionResponse> StartAuthenticationSessionAsync([FromQuery] BioSubjectReference bioSubjectReference) {

			// This is an example of how to start an authentication session.
			// You must implement your own security measures to ensure that only users
			// you want to have access to this endpoint can call it.

			// The response of the following call contains the session URL that
			// will be loaded in the Widget to start the biometric session.

			var response = await restBioService.StartAuthenticationSessionAsync(new() {
				TrustedOrigin = exampleConfig.Value.TrustedOrigin,
				Subject = bioSubjectReference,
				// additional options for authenticationSession
			});

			// Although not mandatory, you may want to save the SessionId in your database
			// along with your user/session information, as you can use this ID to retrieve
			// the status of the session by later using the GetAuthenticationSessionStatusAsync()
			// method. But at the end of the session, the Widget will return you a ticket
			// that you can use to retrieve the session status without needing to store
			// the SessionId.

			// Available properties of the response:
			_ = response.SessionUrl;    // The URL to be loaded in the Widget to start the biometric session.
			_ = response.SessionId;     // The ID of the session.
			_ = response.SessionType;   // The type of the session (Authentication).

			return response;
		}

		[HttpPost("authentication/completion")]
		public async Task<BioAuthenticationSessionStatusModel> CompleteAuthenticationSessionAsync(CompleteBioSessionRequest request) {

			// This is an example of how to complete an authentication session.
			// You must implement your own security measures to ensure that only users
			// you want to have access to this endpoint can call it.

			// By calling the following endpoint, you will get the final status of the
			// biometric session.

			var result = await restBioService.CompleteAuthenticationSessionAsync(request.Ticket);

			// Available properties of the result:
			var sessionId = result.SessionId;       // The ID of the session.

			// Authentication-specific properties (the exact properties may vary based on the actual model structure):
			// Note: The properties below are based on the liveness model and may need adjustment for authentication
			// You should verify the actual properties available in BioAuthenticationSessionStatusModel

			var success = result.Success;           // Whether the biometric session was successful or not.

			if (success == true) {
				// The biometric session was successful and the user was authenticated.

			} else if (success == false) {
				// The biometric session was completed, but authentication failed.
				// Here you may want to retry, lock the account, or implement other security measures.

			} else {
				// The biometric session is still in progress. This should not happen here,
				// as the Widget will only provide a complete ticket when the session is completed
				// (either successfully or not).
			}

			return result;
		}

		[HttpPost("id-capture")]
		public async Task<StartBioSessionResponse> StartIdentificationDocumentCaptureSessionAsync() {

			// This is an example of how to start an identification document capture session.
			// You must implement your own security measures to ensure that only users
			// you want to have access to this endpoint can call it.

			// The response of the following call contains the session URL that
			// will be loaded in the Widget to start the biometric session.

			var response = await restBioService.StartIdentificationDocumentCaptureSessionAsync(new() {
				TrustedOrigin = exampleConfig.Value.TrustedOrigin
				// Additional properties for ID document capture:
				// DocumentTypes = new[] { "passport", "driver_license", "national_id" }, // Optional: allowed document types
				// ValidateDocument = true, // Optional: whether to validate the document authenticity
				// ExtractData = true // Optional: whether to extract data from the document using OCR
			});

			// Although not mandatory, you may want to save the SessionId in your database
			// along with your user/session information, as you can use this ID to retrieve
			// the status of the session by later using the GetIdentificationDocumentCaptureSessionStatusAsync()
			// method. But at the end of the session, the Widget will return you a ticket
			// that you can use to retrieve the session status without needing to store
			// the SessionId.

			// Available properties of the response:
			_ = response.SessionUrl;    // The URL to be loaded in the Widget to start the biometric session.
			_ = response.SessionId;     // The ID of the session.
			_ = response.SessionType;   // The type of the session (IdentificationDocumentCapture).

			return response;
		}

		[HttpPost("id-capture/completion")]
		public async Task<IdentificationDocumentCaptureSessionStatusModel> CompleteIdentificationDocumentCaptureSessionAsync(CompleteBioSessionRequest request) {

			// This is an example of how to complete an identification document capture session.
			// You must implement your own security measures to ensure that only users
			// you want to have access to this endpoint can call it.

			// By calling the following endpoint, you will get the final status of the
			// biometric session.

			var result = await restBioService.CompleteIdentificationDocumentCaptureSessionAsync(request.Ticket);

			// Available properties of the result:
			var sessionId = result.SessionId;       // The ID of the session.

			// ID document capture specific properties (the exact properties may vary based on the actual model structure):
			// Note: The properties below are based on the liveness model and may need adjustment for ID capture
			// You should verify the actual properties available in IdentificationDocumentCaptureSessionStatusModel

			var success = result.Success;           // Whether the biometric session was successful or not.

			if (success == true) {
				// The biometric session was successful and the ID document was captured.

			} else if (success == false) {
				// The biometric session was completed, but document capture failed.
				// Here you may want to retry or provide guidance to the user.

			} else {
				// The biometric session is still in progress. This should not happen here,
				// as the Widget will only provide a complete ticket when the session is completed
				// (either successfully or not).
			}

			return result;
		}

		[HttpGet("id-capture/status")]
		public async Task<IdentificationDocumentCaptureSessionStatusModel> GetIdentificationDocumentCaptureSessionStatusAsync(Guid sessionId) {
			return await restBioService.GetIdentificationDocumentCaptureSessionStatusAsync(sessionId);
		}
	}
}
