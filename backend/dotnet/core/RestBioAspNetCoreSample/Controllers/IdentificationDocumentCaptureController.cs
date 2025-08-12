using Lacuna.RestPki.Api.Bio.Sessions;
using Lacuna.RestPki.Client;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using RestBioAspNetCoreSample.Configuration;

namespace RestBioAspNetCoreSample.Controllers {

	[ApiController]
	[Route("/api/bio/session")]
	public class IdentificationDocumentCaptureController(

		IRestBioService restBioService,
		IOptions<ExampleConfig> exampleConfig

	) : ControllerBase {

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

		[HttpPost("id-capture/complete")]
		public async Task<IdentificationDocumentCaptureSessionStatusModel> CompleteIdentificationDocumentCaptureSessionAsync(CompleteBioSessionRequest request) {

			// This is an example of how to complete an identification document capture session.
			// You must implement your own security measures to ensure that only users
			// you want to have access to this endpoint can call it.

			// By calling the following endpoint, you will get the final status of the
			// biometric session.

			var result = await restBioService.CompleteIdentificationDocumentCaptureSessionAsync(request.Ticket);

			// Available properties of the result:
			var sessionId = result.SessionId;		// The ID of the session.

			// ID document capture specific properties (the exact properties may vary based on the actual model structure):
			// Note: The properties below are based on the liveness model and may need adjustment for ID capture
			// You should verify the actual properties available in IdentificationDocumentCaptureSessionStatusModel
			
			var success = result.Success;           // Whether the biometric session was successful or not.

			if (success == true) {
				// The biometric session was successful and the ID document was captured.

				if (result.ResultDataAvailable) {
					// When the session has ResultDataAvailable = true, it means that
					// you can now retrieve the images captured during the session.

					var resultData = await restBioService.GetSessionResultDataAsync(sessionId);

					// Available properties of the resultData:
					_ = resultData.DocumentData?.FrontImage.Content;		// The image of the front side of the ID document captured during the session
					_ = resultData.DocumentData?.FrontImage.ContentType;    // The content type of the front side image (e.g. "image/jpeg")

					_ = resultData.DocumentData?.FaceCropImage?.Content;	// The cropped image of the face on the ID document captured during the session.
					_ = resultData.DocumentData?.FaceCropImage?.ContentType;// The content type of the cropped face image (e.g. "image/jpeg")

					_ = resultData.DocumentData?.BackImage?.Content;        // The image of the back side of the ID document captured during the session (may be null)
					_ = resultData.DocumentData?.BackImage?.ContentType;    // The content type of the back side image (e.g. "image/jpeg") (may be null)

					// If face capture was enabled:
					_ = resultData.FaceData?.FaceImage.Content;				// The image of the user's face captured during the session
					_ = resultData.FaceData?.FaceImage.ContentType;			// The content type of the face image (e.g. "image/jpeg")

					// If OCR/data extraction was enabled, document data might be available:
					// _ = resultData.DocumentData?.ExtractedData; // Extracted text/data from the document
				}

				// For ID capture sessions, captured document information might be available:
				// Note: The exact property name may be different, check the actual model structure
				// if (result.Document != null) {
				//     _ = result.Document.Id;                   // The unique ID of the captured document
				//     _ = result.Document.DocumentType;        // The type of document that was captured
				//     _ = result.Document.CapturedOn;          // When the document was captured
				// }

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

	}
}
