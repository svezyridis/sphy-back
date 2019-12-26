package sphy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import sphy.image.storage.FileSystemStorageService;
import sphy.image.storage.StorageProperties;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class SphyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SphyApplication.class, args);
    }

    @Bean
    CommandLineRunner init(FileSystemStorageService storageService) {
        return (args) -> {
            storageService.init();
        };
    }
}
