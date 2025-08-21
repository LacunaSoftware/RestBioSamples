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
		public async Task<BioSessionStatusModel> GetSessionStatusAsync(Guid sessionId) {
			return await restBioService.GetIdentificationDocumentCaptureSessionStatusAsync(sessionId);
		}

	}
}
