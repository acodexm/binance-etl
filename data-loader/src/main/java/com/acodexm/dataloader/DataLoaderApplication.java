package com.acodexm.dataloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class DataLoaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataLoaderApplication.class, args);
    }

}
