package com.bank.loanApp.service;

import com.bank.loanApp.dto.RepaymentRequest;
import com.bank.loanApp.model.LoanRepayment;

public interface RepaymentService {

	public LoanRepayment makePayment(String email, RepaymentRequest request);
}
