package cora.page;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cora.main.CwRunner;

@Path("/")
@PermitAll
public class CwPages {

	private static Logger logger_ms = LoggerFactory.getLogger(CwPages.class.getName());

	@GET
	@Path("/")
	public CwPageView getMain(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {
		return new CwPageView("main.ftl").setMessage(message);
	}

	@GET
	@Path("/uploadnewschedule")
	public CwPageView getUploadNewSchedule(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {
		return new CwPageView("upload_new_schedule.ftl").setMessage(message);
	}

	@POST
	@Path("/uploadnewschedule")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postNewSchedule(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		// Upload the file

		try {
			java.nio.file.Path outputPath = FileSystems.getDefault().getPath("/tmp", "xxx");
			Files.deleteIfExists(outputPath);
			Files.copy(uploadedInputStream, outputPath);
		} catch (IOException e) {
			logger_ms.error("Error in file upload", e);
			URI uri = UriBuilder.fromUri("/").queryParam(CwPageView.MESSAGE_PARAM, "Error uploading file").build();
			return Response.seeOther(uri).build();
		}

		try {
			// Launch the external process
			CwRunner runner = CwRunner.getGlobalRunner();
			if (!runner.isAvailable()) {
				runner.terminateRunningProcess();
			}

			runner.startRun();

		} catch (Exception e) {
			logger_ms.error("Error in schedule launch", e);
			URI uri = UriBuilder.fromUri("/").queryParam(CwPageView.MESSAGE_PARAM, "Error launching scheduling tool")
					.build();
			return Response.seeOther(uri).build();
		}

		// Redirect to the "waiting for completion" page
		URI uri = UriBuilder.fromUri("/uploadwait").build();
		return Response.seeOther(uri).build();
	}

	@GET
	@Path("/uploadwait")
	public Object getUploadWait(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {
		CwRunner runner = CwRunner.getGlobalRunner();
		return new CwPageViewWait("wait.ftl", 
				runner.readyToConfirm(), 
				runner.isDone(),
				!runner.isDone(),
				!(runner.isDone() || runner.readyToConfirm()),
				String.join("\n", runner.getLatestStdOut()),
				String.join("\n", runner.getLatestStdErr()))
				.setMessage(message);
	}

	@GET
	@Path("/uploadconfirm")
	public Response getUploadConfirm(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {
		CwRunner.getGlobalRunner().sendInput("YES");

		return Response.seeOther(UriBuilder.fromUri("/uploadwait").build()).build();
	}

	@GET
	@Path("/uploadcancel")
	public Response getUploadCancel(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {
		CwRunner runner = CwRunner.getGlobalRunner();
		if (runner.readyToConfirm()) {
			CwRunner.getGlobalRunner().sendInput("NO");
		}
		else {
			runner.terminateRunningProcess();
		}
		return Response.seeOther(UriBuilder.fromUri("/uploadwait").build()).build();
	}

	@GET
	@Path("/uploaddone")
	public CwPageView getUploadDone(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {

		return new CwPageView("done.ftl").setMessage(message);
	}

}
