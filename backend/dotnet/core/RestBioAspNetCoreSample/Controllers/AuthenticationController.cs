using Lacuna.RestPki.Api;
using Lacuna.RestPki.Api.Bio.Authentications;
using Lacuna.RestPki.Api.Bio.Sessions;
using Lacuna.RestPki.Client;
using Microsoft.AspNetCore.Mvc;

namespace RestBioAspNetCoreSample.Controllers {

	[ApiController]
	[Route("/api/bio/session")]
	public class AuthenticationController(

		IRestBioService restBioService

	) : ControllerBase {

		[HttpPost("/api/bio/authentication-2d")]
		public async Task<BioAuthenticationResponse> EnrollSubject2dAsync(BioAuthenticationRequest request) {
			// Optionally you can opt to check Liveness2d
			request.CheckLiveness2d = true;
			var response = await restBioService.AuthenticateAsync(request);

			if (response.Success) {
				// User successfully authenticated
			} else {
				_ = response.Failure;

				if (response.Failure == BioAuthenticationFailures.NoMatch) {
					// Face detected but didnt match subject!
				}
			}

			return response;
		}

		[HttpGet("authentication/status")]
		public async Task<BioAuthenticationSessionStatusModel> GetSessionStatusAsync(Guid sessionId) {
			return await restBioService.GetAuthenticationSessionStatusAsync(sessionId);
		}
	}
}
