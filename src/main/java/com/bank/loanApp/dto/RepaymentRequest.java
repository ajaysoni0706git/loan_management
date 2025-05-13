package com.bank.loanApp.dto;

import lombok.Data;

@Data
public class RepaymentRequest {

    private Long loanId;
    private Double amountPaid;
    
	public Long getLoanId() {
		return loanId;
	}
	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}
	public Double getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}
    
    
}
