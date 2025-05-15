package com.bank.loanApp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.loanApp.config.JwtUtil;
import com.bank.loanApp.dto.AuthRequest;
import com.bank.loanApp.dto.LoanRequest;
import com.bank.loanApp.dto.RepaymentRequest;
import com.bank.loanApp.dto.UpdateUserRequest;
import com.bank.loanApp.model.LoanApplication;
import com.bank.loanApp.model.LoanRepayment;
import com.bank.loanApp.model.User;
import com.bank.loanApp.repo.UserRepository;
import com.bank.loanApp.service.LoanService;
import com.bank.loanApp.service.RepaymentService;
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
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private LoanService loanService;
	
	@Autowired
	private RepaymentService repaymentService;

	@PostMapping("/register")
	public ResponseEntity<User> saveUser(@Valid @RequestBody User user) {
		User result = userService.saveUser(user);
		return ResponseEntity.ok(result);
		//	return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest request) {  	
		authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		Optional<User> userOpt = repo.findByEmail(request.getEmail());
		User user = userOpt.get();
		String token = jwtUtil.generateToken(user);
		//     return ResponseEntity.ok(new AuthResponse(token));
		Map<String, Object> response = new HashMap<>();
		response.put("token", token);
		response.put("user", Map.of(
				"id", user.getId(),
				"name", user.getName(),
				"email", user.getEmail()
				));

		return ResponseEntity.ok(response);
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

	
	@GetMapping("/getUser")
	public ResponseEntity<?> getUser(@RequestHeader("Authorization") String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); // Remove "Bearer "
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		String email = jwtUtil.extractEmail(token);
		Optional<User> userOpt = repo.findByEmail(email);

		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		return ResponseEntity.ok(userOpt.get());
	}
	 

	@GetMapping("/getUser/{id}")
	public ResponseEntity<User> getUser(@PathVariable Long id, Authentication authentication){
		String email = authentication.getName();

		User authUser = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("Auth user not found"));

		if (!authUser.getId().equals(id)) {
			return ResponseEntity.status(403).build();  // Forbidden
		}
		User user = userService.getUser(id);
		return ResponseEntity.ok(user);   	
	}

	@PostMapping("/loanApply")
	public ResponseEntity<?> applyLoan(@RequestHeader("Authorization") String authHeader,
			@RequestBody LoanRequest request ) {

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); // Remove "Bearer "
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		String email = jwtUtil.extractEmail(token);

		LoanApplication loan = loanService.applyForLoan(email, request);

		return ResponseEntity.ok(loan) ;
	}
	
	@GetMapping("/showMyLoans")
	public ResponseEntity<?> showUserAllLoans(@RequestHeader("Authorization") String authHeader){
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); // Remove "Bearer "
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		String email = jwtUtil.extractEmail(token);
		
		List<LoanApplication> loans = loanService.getMyAllLoans(email);
		if (loans.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No loans found for this user");
		}
		
		return ResponseEntity.ok(loans);
	}
	
	@GetMapping("/getUserLoans/{id}")
	public ResponseEntity<?> getUserLoans(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); // Remove "Bearer "
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}
		
		List<LoanApplication> loans = loanService.getUserAllLoans(id);
		return ResponseEntity.ok(loans);
	}
	
	@PutMapping("/approveLoan/{loanId}")
	public ResponseEntity<?> approveLoan(@RequestHeader("Authorization") String authHeader, @PathVariable Long loanId,
			@RequestBody Map<String, Boolean> request) {

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); // Remove "Bearer "
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		String role = jwtUtil.extractClaim(token, "role");
		
		boolean approve = request.get("approve");

		 // Check if the user has the role of ADMIN
	    if (!"ADMIN".equalsIgnoreCase(role)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only ADMIN can approve loans");
	    }
	           
		LoanApplication loan = loanService.approveLoan(loanId, approve);
		return ResponseEntity.ok(loan);
	}
	
	@PostMapping("/repayLoan")
	public ResponseEntity<?> repayLoan(@RequestHeader("Authorization") String authHeader,
			@RequestBody RepaymentRequest request) {

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); // Remove "Bearer "
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		String email = jwtUtil.extractEmail(token);

		LoanRepayment repayment = repaymentService.makePayment(email, request);

		return ResponseEntity.ok(repayment);
	}
	
	@PostMapping("/update_overdues")
	public ResponseEntity<?> triggerOverdueUpdate(
	        @RequestHeader("Authorization") String authHeader) {

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); // Remove "Bearer "
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		String role = jwtUtil.extractClaim(token, "role");

		 // Check if the user has the role of ADMIN
	    if (!"ADMIN".equalsIgnoreCase(role)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only ADMIN can perform this function");
	    }

	    loanService.updateOverdueLoans();
	    return ResponseEntity.ok("Overdue loans updated");
	}



}	
