package com.bank.loanApp.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.loanApp.dto.RepaymentRequest;
import com.bank.loanApp.model.LoanApplication;
import com.bank.loanApp.model.LoanRepayment;
import com.bank.loanApp.model.LoanStatus;
import com.bank.loanApp.model.User;
import com.bank.loanApp.repo.LoanRepository;
import com.bank.loanApp.repo.RepaymentRepository;
import com.bank.loanApp.repo.UserRepository;

@Service
public class RepaymentServiceImpl implements RepaymentService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private LoanRepository loanRepo;
	
	@Autowired
	private RepaymentRepository repaymentRepo;

	@Override
	public LoanRepayment makePayment(String email, RepaymentRequest dto) {
		// TODO Auto-generated method stub
		
	    User user = userRepo.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	        LoanApplication loan = loanRepo.findById(dto.getLoanId())
	            .orElseThrow(() -> new RuntimeException("Loan not found"));

	        if (!loan.getUser().getId().equals(user.getId())) {
	            throw new RuntimeException("Unauthorized repayment attempt");
	        }

	        if (loan.getStatus() != LoanStatus.APPROVED) {
	            throw new RuntimeException("Only approved loans can be repaid");
	        }
	        
			if (loan.getRemainingAmount() <= 0) {
				throw new RuntimeException("No outstanding amount to repay");
			}
			
		    LoanRepayment repayment = new LoanRepayment();
		    repayment.setLoan(loan);
		    repayment.setAmountPaid(dto.getAmountPaid());
		    repayment.setPaidDate(LocalDate.now());

		    // Deduct repayment from remaining balance
		    double remaining = loan.getRemainingAmount() - dto.getAmountPaid();
		    loan.setRemainingAmount(remaining);

		    // If fully paid
		    if (remaining <= 0) {
		        loan.setRemainingAmount(0.0);
		        loan.setStatus(LoanStatus.REPAID);
		    }

		    loanRepo.save(loan);
		    return repaymentRepo.save(repayment);
	}

}
