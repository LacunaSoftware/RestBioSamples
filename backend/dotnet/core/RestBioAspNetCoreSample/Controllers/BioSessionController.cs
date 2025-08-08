using Lacuna.RestPki.Api.Bio.Sessions;
using Lacuna.RestPki.Client;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using RestBioAspNetCoreSample.Configuration;

namespace RestBioAspNetCoreSample.Controllers {

	[ApiController]
	[Route("/api/bio/session")]
	public class BioSessionController(

		IRestBioService restBioService,
		IOptions<ExampleConfig> exampleConfig

	) : ControllerBase {

		[HttpPost("liveness")]
		public async Task<StartBioSessionResponse> StartLivenessSessionAsync([FromQuery] bool captureIdentificationDocument = false) {
			// TODO: Add pedantic comment to the the users that they must implement ther own security logic
			// because all session calls may be billed to their account.
			return await restBioService.StartLivenessSessionAsync(new() {
				TrustedOrigin = exampleConfig.Value.TrustedOrigin,
				CaptureIdentificationDocument = captureIdentificationDocument
			});
		}

	}
}
