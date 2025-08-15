package com.sprints.onlineVotingSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication

@ComponentScan(
		basePackages = "com.sprints.onlineVotingSystem",
		excludeFilters = @ComponentScan.Filter(
				type = FilterType.REGEX,
				pattern = "com\\.sprints\\.onlineVotingSystem\\.legacy\\..*"
		)
)
public class OnlineVotingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineVotingSystemApplication.class, args);
	}

}
