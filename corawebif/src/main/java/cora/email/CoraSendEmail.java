/**
 * 
 */
package cora.email;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;

/**
 * @author andrewmcgregor
 *
 */
public class CoraSendEmail {

	/**
	 * Before running this AWS SDK for Java (v2) example, set up your development
	 * environment, including your credentials.
	 *
	 * For more information, see the following documentation topic:
	 *
	 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
	 */
	SesV2Client sesv2Client_m;

	public static void main(String[] args) {

		final String usage = "\n" + "Usage:\n" + "    <sender> <recipient> <subject> \n\n" + "Where:\n"
				+ "    sender - An email address that represents the sender. \n"
				+ "    recipient - An email address that represents the recipient. \n"
				+ "    subject - The subject line. \n";

		if (args.length != 3) {
			System.out.println(usage);
			System.exit(1);
		}

		String sender = args[0];
		String recipient = args[1];
		String subject = args[2];

		Region region = Region.CA_CENTRAL_1;
		CoraSendEmail mailer = new CoraSendEmail(region);
		// SesV2Client sesv2Client = SesV2Client.builder().region(region)
		// .credentialsProvider(ProfileCredentialsProvider.create()).build();

		// The HTML body of the email.
		String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
				+ "<p> See the list of customers.</p>" + "</body>" + "</html>";

		mailer.send(sender, recipient, subject, bodyHTML);
	}

	public CoraSendEmail(Region region) {
		// This should not be necessary (to copy env vars to properties, but it did not
		// work until I did this.  This is only required during development
		String x = System.getenv("AWS_ACCESS_KEY_ID");
		if (x != null)
			System.setProperty("aws.accessKeyId", x);

		x = System.getenv("AWS_SECRET_ACCESS_KEY");
		if (x != null)
			System.setProperty("aws.secretAccessKey", x);

		sesv2Client_m = SesV2Client.builder().region(region).build();

	}

	public void send(String sender, String recipient, String subject, String bodyHTML) {

		Destination destination = Destination.builder().toAddresses(recipient).build();

		Content content = Content.builder().data(bodyHTML).build();

		Content sub = Content.builder().data(subject).build();

		Body body = Body.builder().html(content).build();

		Message msg = Message.builder().subject(sub).body(body).build();

		EmailContent emailContent = EmailContent.builder().simple(msg).build();

		SendEmailRequest emailRequest = SendEmailRequest.builder().destination(destination).content(emailContent)
				.fromEmailAddress(sender).build();

		try {
			System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");
			sesv2Client_m.sendEmail(emailRequest);
			System.out.println("email was sent");

		} catch (SesV2Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}

}
