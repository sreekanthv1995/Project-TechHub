package com.tech_hub.techhub;

import com.tech_hub.techhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class TechHubApplication{


	@Autowired
	private UserRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	public static void main(String[] args) {
		SpringApplication.run(TechHubApplication.class, args);
	}


//	@Override
//	public void run(String... args) throws Exception {
//		UserEntity admin = new UserEntity();
//		admin.setFirstName("admin");
//		admin.setEmail("admin@gmail.com");
//		admin.setUsername("admin");
//		admin.setPassword(passwordEncoder.encode("admin"));
//		userRepository.save(admin);
//
//
//	}
}

