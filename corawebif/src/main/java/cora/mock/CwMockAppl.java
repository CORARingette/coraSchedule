package cora.mock;

import java.util.Scanner;

import cora.main.CwRunner;

// This is a mock application that simulates the behavior of the real "RingetteSchedule" application.
public class CwMockAppl {

	public static void main(String[] args) {
		System.out.println("Hello World!");
		try {
			Thread.sleep(1000);
			for (int i = 0; i < 5; i++) {
				Thread.sleep(500);
				System.out.println(String.format("Loop pre confirm %d", i));
			}
			System.out.println(CwRunner.CONFIRM_STRING);
			boolean yesRead;
			try (Scanner scanner = new Scanner(System.in)) {
				yesRead = scanner.nextLine().trim().equals("YES");
			}
			
			if (yesRead) {
			for (int i = 0; i < 5; i++) {
				Thread.sleep(500);
				System.out.println(String.format("Loop post confirm %d", i));
			}
			} else {
				Thread.sleep(500);
				System.out.println(String.format("Update rejected"));
				
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
