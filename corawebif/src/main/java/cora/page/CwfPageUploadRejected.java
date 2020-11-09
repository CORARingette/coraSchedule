/**
 * 
 */
package cora.page;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.dropwizard.views.View;


@Path("/uploadrejected")
@Produces(MediaType.TEXT_HTML)
public class CwfPageUploadRejected {
	static class PageView extends View {

	    public PageView() {
	        super("rejected.ftl");
	    }
	}

    public CwfPageUploadRejected() {
    }

    @GET
    public PageView getPerson() {
        return new PageView();
    }
}
