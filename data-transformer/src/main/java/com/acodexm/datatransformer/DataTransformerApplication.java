package com.acodexm.datatransformer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DataTransformerApplication {

  public static void main(String[] args) {
    SpringApplication.run(DataTransformerApplication.class, args);
  }
}
