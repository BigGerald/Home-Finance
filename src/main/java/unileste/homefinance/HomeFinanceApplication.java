package unileste.homefinance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HomeFinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeFinanceApplication.class, args);
    }

}
