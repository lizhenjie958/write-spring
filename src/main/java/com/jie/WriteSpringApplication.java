package com.jie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class WriteSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(WriteSpringApplication.class, args);
	}

}
