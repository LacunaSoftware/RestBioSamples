using RestBioAspNetCoreSample.Configuration;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers()
	// The following line was added to support the serialization of enums as strings
	// in the JSON responses. This is useful for APIs that use enums, as it makes the API more
	// user-friendly and avoids using numeric enum values directly.
	.AddNewtonsoftJson(options => {
		options.SerializerSettings.Converters.Add(new Newtonsoft.Json.Converters.StringEnumConverter());
	});

// --------------------------------- ADDING RESTBIO TO ASP.NET CORE APPLICATION --------------------------------

// Configure the ExampleConfig from the appsettings.json file
// This will bind Example section of appsettings.json to the ExampleConfig class via dependency injection
// NOTE: This is an example only, you must implement your own configuration logic.
// See ExampleConig.cs for more information.
builder.Services.Configure<ExampleConfig>(builder.Configuration.GetSection("Example"));

// Register the RestPki service, that is the main service used to interact with the RestBio API.
builder.Services.AddRestPki((options) => {
	builder.Configuration.GetSection("RestPkiCore").Bind(options);
});

// -------------------------------------------------------------------------------------------------------------

var app = builder.Build();

//app.UseAuthorization();

app.MapControllers();

app.Run();
