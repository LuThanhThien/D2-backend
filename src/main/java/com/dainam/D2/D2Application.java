package com.dainam.D2;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;

import java.util.Objects;

@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(
		basePackages = "com.dainam.D2"
)
public class D2Application {

	public static String BASE_PACKAGE_NAME;

	public static String HOST_URL;

	@Autowired
	private Environment environment;


	public static void main(String[] args) {
		SpringApplication.run(D2Application.class, args);
	}

	@PostConstruct
	public void init() {
		BASE_PACKAGE_NAME = this.getClass().getPackage().getName();
		HOST_URL = environment.getProperty("spring.domain");
	}

}
