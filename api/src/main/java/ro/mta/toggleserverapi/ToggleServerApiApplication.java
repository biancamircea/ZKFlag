package ro.mta.toggleserverapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ToggleServerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToggleServerApiApplication.class, args);
	}


}
