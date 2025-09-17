package avemujica.entrance;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@ComponentScan(value = "avemujica.*")
@EnableCaching
public class EntranceApplication {
    public static void main(String[] args) {
//        System.out.println(new BCryptPasswordEncoder().encode("123456"));
        SpringApplication.run(EntranceApplication.class, args);
    }
}

