package dev.shopsphere.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrderApplication {

	static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

}
