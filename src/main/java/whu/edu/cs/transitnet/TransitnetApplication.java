package whu.edu.cs.transitnet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("whu.edu.cs.transitnet.*")
public class TransitnetApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransitnetApplication.class, args);
    }


}
