package au.edu.aufonduebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication

@ComponentScan(basePackages = "au.edu.aufonduebackend")
@EntityScan(basePackages = "au.edu.aufonduebackend.model.entity")
@EnableJpaRepositories("au.edu.aufonduebackend.repository")
public class AufonduebackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(AufonduebackendApplication.class, args);
	}
}