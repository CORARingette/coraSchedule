/**
 * 
 */
package cora.page;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.dropwizard.views.View;


@Path("/uploadwait")
@Produces(MediaType.TEXT_HTML)
public class CwPageUploadWait {
	static class PageView extends View {

	    public PageView() {
	        super("wait.ftl");
	    }
	}

    public CwPageUploadWait() {
    }

    @GET
    public PageView getPage() {
        return new PageView();
    }
}
