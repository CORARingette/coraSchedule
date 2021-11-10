package cora.page;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cora.main.CwRunner;

@Path("/corawebif")
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
			String filePath = System.getenv("CW_FILE_PATH");
			java.nio.file.Path outputPath = FileSystems.getDefault().getPath(filePath, "working", "Master.xlsx");
			Files.deleteIfExists(outputPath);
			Files.copy(uploadedInputStream, outputPath);
			String date_tag = Instant.now().toString().replace( "-" , "" ).replace( ":" , "" ).replaceAll("\\..*", "");
			java.nio.file.Path outputPath2 = FileSystems.getDefault().getPath(filePath, "uploads", String.format("Master-%s.xlsx", date_tag));
			Files.copy(uploadedInputStream, outputPath2);
		} catch (IOException e) {
			logger_ms.error("Error in file upload", e);
			URI uri = UriBuilder.fromUri("/corawebif").queryParam(CwPageView.MESSAGE_PARAM, "Error uploading file").build();
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
			URI uri = UriBuilder.fromUri("/corawebif").queryParam(CwPageView.MESSAGE_PARAM, "Error launching scheduling tool")
					.build();
			return Response.seeOther(uri).build();
		}

		// Redirect to the "waiting for completion" page
		URI uri = UriBuilder.fromUri("/corawebif/uploadwait").build();
		return Response.seeOther(uri).build();
	}

	@GET
	@Path("/rerunschedule")
	@Produces(MediaType.TEXT_PLAIN)
	public Response postNewSchedule() {

		try {
			// Launch the external process
			CwRunner runner = CwRunner.getGlobalRunner();
			if (!runner.isAvailable()) {
				runner.terminateRunningProcess();
			}

			runner.startRun();

		} catch (Exception e) {
			logger_ms.error("Error in schedule launch", e);
			URI uri = UriBuilder.fromUri("/corawebif").queryParam(CwPageView.MESSAGE_PARAM, "Error launching scheduling tool")
					.build();
			return Response.seeOther(uri).build();
		}

		// Redirect to the "waiting for completion" page
		URI uri = UriBuilder.fromUri("/corawebif/uploadwait").build();
		return Response.seeOther(uri).build();
	}

	@GET
	@Path("/downloadlastschedule")
	@Produces(MediaType.TEXT_PLAIN)
	public Response downloadLastSchedule() {

        StreamingOutput fileStream =  new StreamingOutput() 
        {
            @Override
            public void write(java.io.OutputStream output) throws IOException 
            {
                try
                {
        			String filePath = System.getenv("CW_FILE_PATH");
        			java.nio.file.Path path = FileSystems.getDefault().getPath(filePath, "working", "Master.xlsx");

                    byte[] data = Files.readAllBytes(path);
                    output.write(data);
                    output.flush();
                } 
                catch (Exception e) 
                {
        			logger_ms.error("Error downloading file", e);
                    throw new WebApplicationException("File Not Found !!");
                }
            }
        };
        return Response
                .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = Master.xlsx")
                .build();
    }

	@GET
	@Path("/uploadwait")
	public Object getUploadWait(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {
		CwRunner runner = CwRunner.getGlobalRunner();
		boolean readyToConfirm = runner.readyToConfirm();
		boolean showError = false;
		if (runner.isDone())
			if (runner.getExitCode() != 0) // Can't call getExitCode() if not done!
				showError = true;
		return new CwPageViewWait("wait.ftl", 
				readyToConfirm, 
				runner.isDone(),
				showError,
				!runner.isDone(),
				!(runner.isDone() || readyToConfirm),
				String.join("\n", runner.mergeAndfilterStrings(runner.getLatestStdOut(), runner.getLatestStdErr(), readyToConfirm)),
				"")
				.setMessage(message);
	}

	@GET
	@Path("/uploadconfirm")
	public Response getUploadConfirm(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {
		CwRunner.getGlobalRunner().sendInput("YES");

		return Response.seeOther(UriBuilder.fromUri("/corawebif/uploadwait").build()).build();
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
		return Response.seeOther(UriBuilder.fromUri("/corawebif/uploadwait").build()).build();
	}

	@GET
	@Path("/uploaddone")
	public CwPageView getUploadDone(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {

		return new CwPageView("done.ftl").setMessage(message);
	}

}
