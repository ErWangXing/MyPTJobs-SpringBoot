package com.MyPTJobs;

import com.MyPTJobs.Class.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

@EnableScheduling
@EnableConfigurationProperties({
		FileStorageProperties.class
})
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })

public class MyPtJobsApplication {

	@PostConstruct
	void setDefaultTimezone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
	}
	public static void main(String[] args) {
		SpringApplication.run(MyPtJobsApplication.class, args);
	}
	@PostMapping(path = "/test")
	public @ResponseBody String test() {
		// This returns a JSON or XML with the users
		return "q23";
	}

}
