package de.moduliertersingvogel.mqttcache;

import java.util.Scanner;

import javax.ws.rs.core.Application;

import org.apache.meecrowave.Meecrowave;

public class Main extends Application {
	public static void main(String[] args) {
		try (final Meecrowave meecrowave = new Meecrowave();final Scanner scanner=new Scanner(System.in);) {
			meecrowave.bake();
		    scanner.nextLine();
		}
	}
}
