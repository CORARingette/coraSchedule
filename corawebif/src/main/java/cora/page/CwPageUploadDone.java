/**
 * 
 */
package cora.page;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.dropwizard.views.View;


@Path("/uploaddone")
@Produces(MediaType.TEXT_HTML)
public class CwPageUploadDone {
	static class PageView extends View {

	    public PageView() {
	        super("done.ftl");
	    }
	}

    public CwPageUploadDone() {
    }

    @GET
    public PageView getPage() {
        return new PageView();
    }
}
