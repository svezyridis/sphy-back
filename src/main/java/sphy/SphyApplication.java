package sphy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import sphy.image.storage.FileSystemStorageService;
import sphy.image.storage.StorageProperties;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class SphyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SphyApplication.class, args);
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowCredentials(true).allowedOrigins("http://localhost:3000")
                        .allowedHeaders("*")
                        .exposedHeaders("Access-Control-Expose-Headers", "Authorization", "Cache-Control", "Content-Type", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers", "Origin")
                        .allowCredentials(true)
                        .allowedMethods("GET", "OPTIONS", "POST", "PUT", "DELETE", "PATCH");
            }
        };
    }

    @Bean
    CommandLineRunner init(FileSystemStorageService storageService) {
        return (args) -> {
            storageService.init();
        };
    }
}
