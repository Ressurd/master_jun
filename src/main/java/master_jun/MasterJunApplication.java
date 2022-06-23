package master_jun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MasterJunApplication {

	public static void main(String[] args) {
		SpringApplication.run(MasterJunApplication.class, args);
	}

}
