using Lacuna.RestPki.Api.Bio.Sessions;
using Lacuna.RestPki.Client;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using RestBioAspNetCoreSample.Configuration;

namespace RestBioAspNetCoreSample.Controllers {

	[ApiController]
	[Route("/api/bio/session")]
	public class LivenessController(

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

		[HttpPost("liveness/complete")]
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
		public async Task<BioSessionResultDataModel> GetSessionStatus(string sessionId) {
			return await restBioService.GetSessionResultDataAsync(Guid.Parse(sessionId));
		}

	}
}
