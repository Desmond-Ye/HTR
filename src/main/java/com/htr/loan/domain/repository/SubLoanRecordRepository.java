package com.htr.loan.domain.repository;

import com.htr.loan.domain.BankCard;
import com.htr.loan.domain.LoanInfo;
import com.htr.loan.domain.LoanRecord;
import com.htr.loan.domain.SubLoanRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface SubLoanRecordRepository extends JpaRepository<SubLoanRecord, String>, JpaSpecificationExecutor<SubLoanRecord> {

    List<SubLoanRecord> findAllByBankCardAndActiveTrue(BankCard bankCard);

    List<SubLoanRecord> findAllByLoanRecordAndActiveTrue(LoanRecord loanRecord);

    List<SubLoanRecord> findAllByLoanInfoAndActiveTrue(LoanInfo loanInfo);

    SubLoanRecord findByReceiptNumberAndActiveTrue(String receiptNumber);
}
