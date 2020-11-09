/**
 * 
 */
package cora.page;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.dropwizard.views.View;


@Path("/uploadconfirm")
@Produces(MediaType.TEXT_HTML)
public class CwPageUploadConfirm {
	static class PageView extends View {

	    public PageView() {
	        super("confirm.ftl");
	    }
	}
    public CwPageUploadConfirm() {
    }

    @GET
    public PageView getPage() {
        return new PageView();
    }

}
