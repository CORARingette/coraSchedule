/**
 * 
 */
package cora.page;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.dropwizard.views.View;


@Path("/logout")
@Produces(MediaType.TEXT_HTML)
public class CwPageLogout {
	static class PageView extends View {

	    public PageView() {
	        super("logout.ftl");
	    }
	}
	
    public CwPageLogout() {
    }

    @GET
    public PageView getPerson() {
        return new PageView();
    }
}
