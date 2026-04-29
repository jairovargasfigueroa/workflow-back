package com.jairo.workflowtramites;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WorkflowTramitesApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkflowTramitesApplication.class, args);
	}

}
