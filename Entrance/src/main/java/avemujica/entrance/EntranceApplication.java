package avemujica.entrance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "avemujica")
public class EntranceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EntranceApplication.class, args);
    }
}
