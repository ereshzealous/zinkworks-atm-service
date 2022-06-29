package com.zinkworks.atm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
//@ComponentScan(basePackages = "com.zinkworks.atm")
public class AtmServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AtmServiceApplication.class, args);
  }

}
