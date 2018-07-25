package de.sadrab.testcode.Starter;

import de.sadrab.testcode.Parser.LogParser;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StarterConfig {
    @Bean
    public ApplicationRunner start(LogParser logParser){
        return args -> {
            logParser.parse().forEach(System.out::println);
        };
    }
}
