using Lacuna.RestPki.Api;
using Lacuna.RestPki.Api.Bio.Enrollments;
using Lacuna.RestPki.Client;
using Microsoft.AspNetCore.Mvc;

namespace RestBioAspNetCoreSample.Controllers {

	[ApiController]
	[Route("/api/bio/session")]
	public class EnrollmentController(

		IRestBioService restBioService

	) : ControllerBase {

		[HttpPost("/api/bio/enrollment")]
		public async Task<BioEnrollmentResponse> EnrollSubjectAsync(BioEnrollmentRequest request) {
			// Optionally you can opt to check Liveness2d
			request.CheckLiveness2d = true;
			var response = await restBioService.EnrollAsync(request);

			if (response.Success == true) {
				_ = response.Result.SubjectId;
			} else {
				// Here you can see why the enrollment has Failed
				_ = response.Failure;

				if (response.Failure == BioEnrollmentFailures.FaceLiveness2dFailed) {
					// Liveness failed
				}

				if (response.Failure == BioEnrollmentFailures.FaceNotFoundWithSufficientQuality) {
					// Bad image
				}
			}

			return response;
		}
	}
}
