package com.kalah;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.kalah.util.GameSettings;

@SpringBootApplication
public class KalahGameBackend implements
		ApplicationListener<ApplicationReadyEvent> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(KalahGameBackend.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

		};
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			int port = applicationContext.getBean(Environment.class)
					.getProperty("server.port", Integer.class, 8080);
			
			GameSettings.SERVER_HOSTNAME = ip;
			GameSettings.SERVER_PORT = port;
			
			System.out.printf("%s:%d", ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
