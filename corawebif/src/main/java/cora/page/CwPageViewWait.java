package cora.page;

public class CwPageViewWait extends CwPageView {
	String stdout_m;
	String stderr_m;
	boolean showConfirm_m = false;
	boolean showDone_m = false;
	boolean showCancel_m = false;
	private boolean doRefresh_m;
	
	public CwPageViewWait(String templateName, boolean showConfirm, boolean showDone, boolean showCancel, boolean doRefresh, String stdout, String stderr) {
		super(templateName);
		showConfirm_m = showConfirm;
		showDone_m = showDone;
		showCancel_m = showCancel;
		stdout_m = stdout;
		stderr_m = stderr;
		doRefresh_m = doRefresh;
	}
	
	public boolean getDoRefresh() {
		return doRefresh_m;
	}

	public String getStdout() {
		return stdout_m;
	}

	public String getStderr() {
		return stderr_m;
	}

	public boolean getShowConfirm() {
		return showConfirm_m;
	}
	
	public boolean getShowCancel() {
		return showCancel_m;
	}
	
	public boolean getShowDone() {
		return showDone_m;
	}
}
