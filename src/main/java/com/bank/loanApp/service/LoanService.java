package com.bank.loanApp.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

import com.bank.loanApp.dto.LoanRequest;
import com.bank.loanApp.model.LoanApplication;

public interface LoanService {
	public LoanApplication applyForLoan(String email, LoanRequest request);
	
	public LoanApplication approveLoan(Long loanId, boolean approve);
	
	public List<LoanApplication> getMyAllLoans(String email);

	public List<LoanApplication> getUserAllLoans(Long id);
	
	@Scheduled(cron = "0 0 1 * * ?")  // Every day at 1:00 AM
	public void updateOverdueLoans();
	   
}
