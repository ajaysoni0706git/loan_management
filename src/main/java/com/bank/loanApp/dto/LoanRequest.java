package com.bank.loanApp.dto;

import lombok.Data;

@Data
public class LoanRequest {
    private Double loanAmount;
    private Double interestRate;
    private Integer durationInMonths;
    private String purpose;
	public Double getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(Double loanAmount) {
		this.loanAmount = loanAmount;
	}
	public Double getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}
	public Integer getDurationInMonths() {
		return durationInMonths;
	}
	public void setDurationInMonths(Integer durationInMonths) {
		this.durationInMonths = durationInMonths;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
    
    
}
