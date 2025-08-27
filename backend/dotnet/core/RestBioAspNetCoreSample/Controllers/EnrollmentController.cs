using Lacuna.RestPki.Api;
using Lacuna.RestPki.Api.Bio.Sessions;
using Lacuna.RestPki.Api.Bio.Subjects;
using Lacuna.RestPki.Client;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using RestBioAspNetCoreSample.Configuration;

namespace RestBioAspNetCoreSample.Controllers {

	[ApiController]
	[Route("/api/bio/session")]
	public class EnrollmentController(

		IRestBioService restBioService,
		IOptions<ExampleConfig> exampleConfig

	) : ControllerBase {

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

		[HttpPost("enrollment/complete")]
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

		[HttpPost("/api/bio/enrollment-2d")]
		public async Task<BioEnrollmentSessionResponse> EnrollSubject2dAsync(BioSubjectEnrollment2dRequest request) {
			// Optionally you can opt to check Liveness2d
			request.CheckLiveness2d = true;
			var response = await restBioService.EnrollSubject2dAsync(request);

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

		[HttpGet("enrollment/status")]
		public async Task<BioEnrollmentSessionStatusModel> GetSessionStatusAsync(Guid sessionId) {
			return await restBioService.GetEnrollmentSessionStatusAsync(sessionId);
		}
	}
}
