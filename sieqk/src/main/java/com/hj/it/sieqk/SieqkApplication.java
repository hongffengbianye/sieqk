package com.hj.it.sieqk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;

@SpringBootApplication
@MapperScan("com.hj.it.sieqk.dao")
public class SieqkApplication {

	public static void main(String[] args) {
		SpringApplication.run(SieqkApplication.class, args);
	}

}
