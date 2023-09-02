package cora.page;

import io.dropwizard.views.View;

public class CwPageViewEmail extends View {
	public static final String MESSAGE_PARAM = "message";
	public static final String DESTEMAIL_PARAM = "destemail";
	
	String message_m;
	String destEmail_m;
	
	public CwPageViewEmail(String templateName) {
			super(templateName);
		}

	public CwPageViewEmail setMessage(String message) {
		message_m = message;
		return this;
	}

	public String getMessage() {
		return message_m;
	}

	public CwPageViewEmail setDestEmail(String destEmail) {
		destEmail_m = destEmail;
		return this;
	}

	public String getDestEmail() {
		return destEmail_m;
	}

}
