package cora.page;

import io.dropwizard.views.View;

public class CwPageView extends View {
	public static final String MESSAGE_PARAM = "message";
	
	String message_m;
	
	public CwPageView(String templateName) {
			super(templateName);
		}

	public CwPageView setMessage(String message) {
		message_m = message;
		return this;
	}

	public String getMessage() {
		return message_m;
	}

}
