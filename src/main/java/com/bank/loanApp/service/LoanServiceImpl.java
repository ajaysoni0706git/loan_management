package com.bank.loanApp.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.loanApp.dto.LoanRequest;
import com.bank.loanApp.model.LoanApplication;
import com.bank.loanApp.model.LoanStatus;
import com.bank.loanApp.model.User;
import com.bank.loanApp.repo.LoanRepository;
import com.bank.loanApp.repo.UserRepository;


@Service
public class LoanServiceImpl implements LoanService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private LoanRepository loanRepo;

	public LoanApplication applyForLoan(String email, LoanRequest request) {
		User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		// Simple Interest Calculation
		double principal = request.getLoanAmount();
		double rate = request.getInterestRate();
		int months = request.getDurationInMonths();
		double interest = (principal * rate * months) / (12 * 100);

		LoanApplication loan = new LoanApplication();
		loan.setUser(user);
		loan.setLoanAmount(principal);
		loan.setInterestRate(rate);
		loan.setDurationInMonths(months);
		loan.setPurpose(request.getPurpose());
		loan.setTotalInterest(interest);
		loan.setStatus(LoanStatus.PENDING);

		return loanRepo.save(loan);
	}

	@Override
	public List<LoanApplication> getMyAllLoans(String email) {

		List<LoanApplication> loans = loanRepo.findByUserEmail(email);
		// TODO Auto-generated method stub
		return loans;
	}

	@Override
	public List<LoanApplication> getUserAllLoans(Long id) {
		//    User user = userRepo.findById(id)
		//            .orElseThrow(() -> new RuntimeException("User not found"));

		List<LoanApplication> loans = loanRepo.findByUserId(id);
		return loans;
	}


	@Override
	public LoanApplication approveLoan(Long loanId, boolean approve) {
		LoanApplication loan = loanRepo.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));

		if (loan.getStatus() != LoanStatus.PENDING) {
			throw new RuntimeException("Loan already reviewed");
		}

		//	loan.setStatus(approve ? LoanStatus.APPROVED : LoanStatus.REJECTED);


		if (!approve) {
			loan.setStatus(LoanStatus.REJECTED);
			return loanRepo.save(loan);
		}


		// If approved
		loan.setStatus(LoanStatus.APPROVED);
		loan.setApprovalDate(LocalDate.now());

		// Set due date based on duration
		loan.setDueDate(LocalDate.now().plusMonths(loan.getDurationInMonths()));

		// Set remaining amount = principal + total interest
		double totalPayable = loan.getLoanAmount() + loan.getTotalInterest();
		loan.setRemainingAmount(totalPayable);
		return loanRepo.save(loan);
	}


	public void updateOverdueLoans() {
		List<LoanApplication> loans = loanRepo.findByStatus(LoanStatus.APPROVED);
		LocalDate today = LocalDate.now();

		for (LoanApplication loan : loans) {
			if (loan.getDueDate().isBefore(today) && loan.getRemainingAmount() > 0) {
				loan.setStatus(LoanStatus.OVERDUE);
				loanRepo.save(loan);
			}
		}
	} 

}
