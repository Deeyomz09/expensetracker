package com.example.expensetracker.service;

import com.example.expensetracker.entity.AppUser;
import com.example.expensetracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public void register(String email, String rawPassword) {
        if (repo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        String hash = encoder.encode(rawPassword);
        repo.save(new AppUser(email, hash));
    }
}
