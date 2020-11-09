/**
 * 
 */
package cora.page;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.dropwizard.views.View;


@Path("/")
@PermitAll
@Produces(MediaType.TEXT_HTML)
public class CwPageMain {
	static class PageView extends View {

	    public PageView() {
	        super("main.ftl");
	    }
	}
	
    public CwPageMain() {
    }

    @GET
    public PageView getPerson() {
        return new PageView();
    }
}
