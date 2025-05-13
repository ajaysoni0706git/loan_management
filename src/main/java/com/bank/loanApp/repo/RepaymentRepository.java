package com.bank.loanApp.repo;

import com.bank.loanApp.model.LoanRepayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepaymentRepository extends JpaRepository<LoanRepayment, Long> {

}
