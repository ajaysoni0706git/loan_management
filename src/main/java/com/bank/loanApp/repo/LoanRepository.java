package com.bank.loanApp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.loanApp.model.LoanApplication;
import com.bank.loanApp.model.LoanStatus;

public interface LoanRepository extends JpaRepository<LoanApplication, Long> {

	 List<LoanApplication> findByUserId(Long id);
	 List<LoanApplication> findByUserEmail(String email);
	List<LoanApplication> findByStatus(LoanStatus approved);
	
}
