package com.bank.loanApp.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.loanApp.dto.UpdateUserRequest;
import com.bank.loanApp.model.User;
import com.bank.loanApp.repo.UserRepository;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserRepository repo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
    @Override
    public User saveUser(User user) {
        if (repo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        //encoding password
        String password = user.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        
        return repo.save(user);
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) {

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

	@Override
	public User updateUser(Long userId, UpdateUserRequest dto) {
		User user = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
		
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            user.setAddress(dto.getAddress());
        }
		if (dto.getPassword() != null) {
			String encodedPassword = passwordEncoder.encode(dto.getPassword());
			user.setPassword(encodedPassword);
		}

		return repo.save(user);
	}
    
    
}