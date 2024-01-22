package id.co.workers.Litera;

import java.io.FileNotFoundException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraApplication {

  public static void main(String[] args) throws FileNotFoundException {
    SpringApplication.run(LiteraApplication.class, args);
  }
}
