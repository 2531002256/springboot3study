package szy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("szy.mapper")
@SpringBootApplication
public class Springboot3StudyApplication {
    public static void main(String[] args) {
        SpringApplication.run(Springboot3StudyApplication.class, args);
    }
}
