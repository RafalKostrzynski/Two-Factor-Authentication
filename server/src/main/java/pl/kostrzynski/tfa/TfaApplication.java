package pl.kostrzynski.tfa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TfaApplication {
    public static void main(String[] args) {
        SpringApplication.run(TfaApplication.class, args);
    }
}
