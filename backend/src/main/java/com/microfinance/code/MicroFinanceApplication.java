package com.microfinance.code;

import com.microfinance.code.model.Branch;
import com.microfinance.code.model.CIF;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class MicroFinanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicroFinanceApplication.class, args);
    }
}
