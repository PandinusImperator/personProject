package com.wangwd.person;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.wangwd"})
@EnableAutoConfiguration
public class WangwdApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(WangwdApplication.class);
		app.setWebApplicationType( WebApplicationType.SERVLET );
		app.run(args);
	}
}
