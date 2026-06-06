package com.example.demo.members.config;

import com.example.demo.members.domain.User;
import com.example.demo.members.repository.UserRepository;
import com.example.demo.members.service.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DemoUserConfig {

    @Bean
    public CommandLineRunner demoUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("cloudweb@kangnam.ac.kr").isPresent()) {
                return;
            }

            User user = new User();
            user.setUsername("cloudweb");
            user.setName("팀플");
            user.setEmail("cloudweb@kangnam.ac.kr");
            user.setPassword(passwordEncoder.encode("1234"));
            user.setSchoolId("cloudweb");
            user.setStudentId("202204000");
            user.setEmailVerified(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user);
        };
    }
}
