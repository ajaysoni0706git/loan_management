package com.bank.loanApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.loanApp.config.JwtUtil;
import com.bank.loanApp.dto.AuthRequest;
import com.bank.loanApp.dto.AuthResponse;
import com.bank.loanApp.dto.UpdateUserRequest;
import com.bank.loanApp.model.User;
import com.bank.loanApp.repo.UserRepository;
import com.bank.loanApp.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bankLoan")
public class UserController {	

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtUtil jwtUtil;
	
    @Autowired
    private AuthenticationManager authManager;

	@PostMapping("/addCustomer")
	public ResponseEntity<User> saveUser(@Valid @RequestBody User user) {
		User result = userService.saveUser(user);
		return ResponseEntity.ok(result);
		//	return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}
	
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = jwtUtil.generateToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }
    
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest dto, Authentication authentication) {
        String email = authentication.getName();

        // 2) Load the user from DB to verify identity
        User authUser = repo.findByEmail(email) 
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        // 3) Ensure the authenticated user is updating their own record
        if (!authUser.getId().equals(id)) {
            return ResponseEntity
                    .status(403)
                    .build();  // Forbidden
        }

        // 4) Perform the update
        User updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }
}	
