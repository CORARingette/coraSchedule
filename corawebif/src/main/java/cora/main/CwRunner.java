package cora.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CwRunner {
	
	class CwStreamHandler extends Thread {

	    InputStream is_m;
	    String name_m;
	    List<String> stringList_m;

	    CwStreamHandler(InputStream is, List<String> stringList, String name) {
	        super("StreamHandler");
	        name_m = name;
	        is_m = is;
	        stringList_m = stringList;
	    }

	    @Override
	    public void run() {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(is_m))) {
				logger_ms.info("Reader for output for " + name_m + " started...");
				String line = null;
				while ((line = reader.readLine()) != null) {
					stringList_m.add(line);
					logger_ms.info("READ {}: {}", name_m, line);
				}
				logger_ms.info("Reader for output for " + name_m + " terminated");

			} catch (IOException e) {
				logger_ms.error("Error in collecting output for " + name_m, e);
			}
	    }
	}

	public static final String CONFIRM_STRING = "Confirm that you want to apply changes (enter YES)";

	private static Logger logger_ms = LoggerFactory.getLogger(CwRunner.class.getName());

	private static CwRunner globalRunner_ms;

	public static CwRunner getGlobalRunner() {
		return globalRunner_ms;
	}

	public static void makeGlobalRunner(String args[]) {
		globalRunner_ms = new CwRunner(args);
	}

	private String args_m[];
	private List<String> stdout_m;
	private List<String> stderr_m;
	private Process process_m;
	private boolean inputSent_m = false;
	
	private CwStreamHandler stdoutThread_m;
	
	private CwStreamHandler stderrThread_m;

	private CwRunner(String args[]) {
		args_m = args;
	}

	public boolean isDone() {
		if (process_m == null)
			return true;
		
		return !process_m.isAlive();
	}

	public int getExitCode() {
		return process_m.exitValue();
	}

	public boolean isAvailable() {
		if (process_m == null)
			return true;
		
		if (!process_m.isAlive()) {
			process_m = null;
			return true;
		}
		return false;
	}

	public void terminateRunningProcess() {
		if (process_m != null) {
			logger_ms.info("Stopping previous process");
			stdout_m.add("USER ABORTING PROCESS");
			process_m.destroyForcibly();
			for (int i = 0; i < 100; i++) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				if (!process_m.isAlive()) {
					process_m = null;
					logger_ms.info("Stopped previous process");
					return;
				}
			}
			logger_ms.warn("Process not terminated!");
			process_m = null;
		}
	}

	public boolean readyToConfirm() {
		if (inputSent_m)
			return false;

		for (String line : stdout_m) {
			if (line.contains(CONFIRM_STRING)) {
				return true;
			}
		}
		return false;
	}

	public void sendInput(String s) {
		inputSent_m = true;
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process_m.getOutputStream()))) {
			writer.write(s);
			writer.newLine();
		} catch (IOException e) {
			logger_ms.warn("Error in send input, terminating process", e);
			terminateRunningProcess();
		}
	}

	public void startRun() throws IOException {
		ProcessBuilder builder = new ProcessBuilder(args_m);
		logger_ms.info("Starting scheduler tool to run...");
		builder.directory(new File("/tmp"));
		process_m = builder.start();
		clear();
		
	}

	private void clear() {
		stdout_m = Collections.synchronizedList(new ArrayList<String>());
		stderr_m = Collections.synchronizedList(new ArrayList<String>());
		stdoutThread_m = new CwStreamHandler(process_m.getInputStream(), stdout_m, "stdout");
		stdoutThread_m.start();
		stderrThread_m = new CwStreamHandler(process_m.getErrorStream(), stderr_m, "stderr");
		stderrThread_m.start();
		inputSent_m = false;
	}

	public List<String> getLatestStdOut() {
		return stdout_m;
	}

	public List<String> getLatestStdErr() {
		return stderr_m;
	}

}
