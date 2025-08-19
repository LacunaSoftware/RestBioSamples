using Lacuna.RestPki.Api.Bio;
using Lacuna.RestPki.Api.Bio.Sessions;
using Lacuna.RestPki.Api.Bio.Subjects;
using Lacuna.RestPki.Client;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using RestBioAspNetCoreSample.Configuration;

namespace RestBioAspNetCoreSample.Controllers {

	[ApiController]
	[Route("/api/bio/session")]
	public class AuthenticationController(

		IRestBioService restBioService,
		IOptions<ExampleConfig> exampleConfig

	) : ControllerBase {

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

		[HttpPost("authentication/complete")]
		public async Task<BioAuthenticationSessionStatusModel> CompleteAuthenticationSessionAsync(CompleteBioSessionRequest request) {

			// This is an example of how to complete an authentication session.
			// You must implement your own security measures to ensure that only users
			// you want to have access to this endpoint can call it.

			// By calling the following endpoint, you will get the final status of the
			// biometric session.

			var result = await restBioService.CompleteAuthenticationSessionAsync(request.Ticket);

			// Available properties of the result:
			var sessionId = result.SessionId;		// The ID of the session.
			
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

        [HttpPost("/api/bio/authentication-2d")]
        public Task<BioSubjectAuthentication2dResponse> EnrollSubject2dAsync(BioSubjectAuthentication2dRequest request)
        {
            // Optionally you can opt to always check Liveness2d
            request.CheckLiveness2d = true;
            return restBioService.AuthenticateSubject2dAsync(request);
        }

        [HttpGet("authentication/status")]
        public async Task<BioSessionResultDataModel> GetSessionStatus(string sessionId) {
            return await restBioService.GetSessionResultDataAsync(Guid.Parse(sessionId));
        }
    }
}
