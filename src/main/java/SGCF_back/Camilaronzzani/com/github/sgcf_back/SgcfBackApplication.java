package SGCF_back.Camilaronzzani.com.github.sgcf_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SgcfBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(SgcfBackApplication.class, args);
	}

}
