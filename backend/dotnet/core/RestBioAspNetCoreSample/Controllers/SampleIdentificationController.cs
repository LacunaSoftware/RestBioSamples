using Lacuna.RestPki.Api.Bio.Identifications;
using Lacuna.RestPki.Api.Bio.Sessions;
using Lacuna.RestPki.Client;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using RestBioAspNetCoreSample.Configuration;

namespace RestBioAspNetCoreSample.Controllers {

	[ApiController]
	[Route("/sample-api/sessions")]
	public class SampleIdentificationController(

		IRestBioService restBioService,
		IOptions<ExampleConfig> exampleConfig

	) : ControllerBase {

		[HttpPost("identification2d")]
		public async Task<BioIdentificationResponse> Identification2DAsync(BioIdentificationRequest request, Guid? subscriptionId = null) {
			return await restBioService.IdentifyAsync(request, subscriptionId);
		}

		[HttpPost("identification")]
		public async Task<StartBioSessionResponse> StartIdentificationAsync() {

			// This is an example of how to start an identification session.
			// You must implement your own security measures to ensure that only users
			// you want to have access to this endpoint can call it.

			// The response of the following call contains the session URL that
			// will be loaded in the Widget to start the biometric session.

			var response = await restBioService.StartIdentificationSessionAsync(new StartBioIdentificationSessionRequest {
				TrustedOrigin = exampleConfig.Value.TrustedOrigin,
				// Additional properties for identification session:
				FaceCaptureProvider = Lacuna.RestPki.Api.FaceCaptureProviders.FaceTecLiveness3d
			});


			// Although not mandatory, you may want to save the SessionId in your database
			// along with your user/session information, as you can use this ID to retrieve
			// the status of the session by later using the GetIdentificationSessionCompletionAsync()
			// method. But at the end of the session, the Widget will return you a ticket
			// that you can use to retrieve the session status without needing to store
			// the SessionId.

			// Available properties of the response:
			_ = response.SessionUrl;    // The URL to be loaded in the Widget to start the biometric session.
			_ = response.SessionId;     // The ID of the session.
			_ = response.SessionType;   // The type of the session (IdentificationDocumentCapture).

			return response;
		}

		[HttpPost("identification/completion")]
		public async Task<BioIdentificationSessionStatusModel> GetIdentificationSessionCompletionAsync(CompleteBioSessionRequest request) {

			// This is an example of how to complete an identification session.
			// You must implement your own security measures to ensure that only users
			// you want to have access to this endpoint can call it.

			// By calling the following endpoint, you will get the final status of the
			// biometric session.
			var result = await restBioService.CompleteIdentificationSessionAsync(request.Ticket);

			// Available properties of the result:
			var sessionId = result.SessionId;       // The ID of the session.
			var success = result.Success;           // Whether the biometric session was successful or not.

			if (success == true) {
				// The biometric session was successful and the identification process is complete.
				// You can now trust that the person behind the camera is who they claim to be.

			} else if (success == false) {
				// The biometric session was completed, but the identification failed.
				// Here you may want to retry, or implement other security measures.

			} else {
				// The biometric session is still in progress. This should not happen here,
				// as the Widget will only provide a ticket when the session is finalized.
			}

			return result;
		}

		[HttpGet("identification/status")]
		public async Task<BioIdentificationSessionStatusModel> GetIdentificationSessionStatusAsync(Guid sessionId) {
			return await restBioService.GetIdentificationSessionStatusAsync(sessionId);
		}

	}
}
