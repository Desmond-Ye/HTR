package com.htr.loan.service;

import com.htr.loan.domain.LoanRecord;
import com.htr.loan.domain.SubLoanRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface SubLoanRecordService {

    SubLoanRecord repayment(SubLoanRecord subLoanRecord);

    Page<SubLoanRecord> findAll(Map<String,Object> filterParams, Pageable pageable);

    List<SubLoanRecord> findAllByLoanInfo(String loanInfoID);

    SubLoanRecord backRepayment(SubLoanRecord subLoanRecord);

    SubLoanRecord findByReceiptNumber(String receiptNumber);
}
