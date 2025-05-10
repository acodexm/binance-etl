package com.acodexm.dataloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DataLoaderApplication {

  public static void main(String[] args) {
    SpringApplication.run(DataLoaderApplication.class, args);
  }
}
