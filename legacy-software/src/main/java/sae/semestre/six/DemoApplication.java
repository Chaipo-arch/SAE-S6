package sae.semestre.six;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class DemoApplication {

	static {
		System.setProperty("java.vm.args", "--add-opens java.base/java.lang=ALL-UNNAMED");
	}

	public static void main(String[] args) {
		if(!necessaryFoldersAreCreated()){
			System.err.println("Les dossiers C:\\hospital\\ et/ou C:\\hospital\\billing ne sont pas créés.");
		} else {
			SpringApplication.run(DemoApplication.class, args);
		}

	}

	public static boolean necessaryFoldersAreCreated() {
		Path pathHospital = Path.of("C:\\hospital\\");
		Path pathBilling = Path.of("C:\\hospital\\billing");

		return Files.exists(pathHospital) && Files.exists(pathBilling);
	}

}
