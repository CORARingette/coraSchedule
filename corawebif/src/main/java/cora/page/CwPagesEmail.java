package cora.page;

import java.net.URI;
import java.util.Base64;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cora.email.CoraSendEmail;
import software.amazon.awssdk.regions.Region;

@Path("/corawebif")
public class CwPagesEmail {

	private static Logger logger_ms = LoggerFactory.getLogger(CwPagesEmail.class.getName());
	

	@GET
	@Path("/email")
	public CwPageViewEmail getEmail(@QueryParam("destemail") String destEmailCoded, @QueryParam(CwPageView.MESSAGE_PARAM) String message) {
		return new CwPageViewEmail("email.ftl").setMessage(message).setDestEmail(destEmailCoded);
	}

	@GET
	@Path("/email_result")
	public CwPageViewEmail getEmailResult(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {
		return new CwPageViewEmail("email_result.ftl").setMessage(message);
	}

	@POST
	@Path("/email")
	public Response validateLogin(@Context ContainerRequestContext requestContext, //
			@FormParam("sourceemail") String sourceemail,
			@FormParam("destemail") String destemail,//
			@FormParam("subject") String subject,
			@FormParam("message") String message) {

		if (sourceemail.isEmpty()) {
			URI uri = UriBuilder.fromUri("/corawebif/email")
					.queryParam(CwPageViewEmail.MESSAGE_PARAM, "You must specify your email address")
					.queryParam(CwPageViewEmail.DESTEMAIL_PARAM, destemail).build();
			return Response.seeOther(uri).build();
		}
			
		if (message.isEmpty()) {
			URI uri = UriBuilder.fromUri("/corawebif/email").queryParam(CwPageView.MESSAGE_PARAM, "The message must not be empty")
					.queryParam(CwPageViewEmail.DESTEMAIL_PARAM, destemail).build();
			return Response.seeOther(uri).build();			
		}
			
		if (subject.isEmpty()) {
			URI uri = UriBuilder.fromUri("/corawebif/email").queryParam(CwPageView.MESSAGE_PARAM, "The subject must not be empty")
					.queryParam(CwPageViewEmail.DESTEMAIL_PARAM, destemail).build();
			return Response.seeOther(uri).build();			
		}
			
		if (destemail.isEmpty()) {
			URI uri = UriBuilder.fromUri("/corawebif/email_result").queryParam(CwPageView.MESSAGE_PARAM, 
					"There is no destination email address set.  Email message not sent.  Report this to the webmaster at corawebnet@gmail.com")
					.queryParam(CwPageViewEmail.DESTEMAIL_PARAM, destemail).build();
			return Response.seeOther(uri).build();			
		}
		
		String decodedDestEmail;	
		try {
			decodedDestEmail = new String(Base64.getDecoder().decode(destemail));
			logger_ms.info("Sending email to {} from {}", decodedDestEmail, sourceemail);
		} catch (IllegalArgumentException e) {
			URI uri = UriBuilder.fromUri("/corawebif/email_result").queryParam(CwPageView.MESSAGE_PARAM, 
					"Invalid destination email address set.  Email message not sent.  Report this to the webmaster at corawebnet@gmail.com")
					.queryParam(CwPageViewEmail.DESTEMAIL_PARAM, destemail).build();
			return Response.seeOther(uri).build();			
		}
		
		// Send email
		try {
			// The HTML body of the email.
			String bodyHTML = "<html>" + "<head></head>" + "<body>" 
					+ "<h2>Message from CORA Website Contact Page:</h2>"
					+ "<p> Sender email address: " + sourceemail + "</p>"
					+ "<br>"
					+ "<p>" + StringEscapeUtils.escapeHtml4(message) + "</p>" + "</body>" + "</html>";

			CoraSendEmail coraMailer = new CoraSendEmail(Region.CA_CENTRAL_1);
			coraMailer.send(sourceemail, decodedDestEmail, subject, bodyHTML);
			logger_ms.info("Email with subject '{}' sent to '{}' from '{}'", subject, decodedDestEmail, sourceemail);
			
			URI uri = UriBuilder.fromUri("/corawebif/email_result").queryParam(CwPageView.MESSAGE_PARAM, "Email sent successfully").build();
			return Response.seeOther(uri).build();
		} catch (Exception e) {
			URI uri = UriBuilder.fromUri("/corawebif/email_result").queryParam(CwPageView.MESSAGE_PARAM, "Error sending email: " + e.toString()).build();
			logger_ms.warn("Exception from msg send:", e);
			return Response.seeOther(uri).build();
		}

	}


}
