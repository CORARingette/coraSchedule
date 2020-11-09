/**
 * 
 */
package cora.page;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import io.dropwizard.views.View;


@Path("/uploadnewschedule")
@Produces(MediaType.TEXT_HTML)
public class CwPageUploadNewSchedule {
	static class PageView extends View {

	    public PageView() {
	        super("upload_new_schedule.ftl");
	    }
	}

    public CwPageUploadNewSchedule() {
    }

    @GET
    public PageView getPage() {
        return new PageView();
    }
    
    @POST
    public Object postNewSchedule() {
        URI uri = UriBuilder.fromUri("/uploadwait").build();
        return Response.seeOther(uri).build();
    }
}
