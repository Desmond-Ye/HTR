package com.htr.loan.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class SubLoanRecord extends BaseDomain {

    @ManyToOne
    private User payee;  //收款人
    private Double receipts; //收到的金额
    private String receiptNumber; //收据编号

    @JsonFormat
    private Date receiptDate; //收款时间
    @ManyToOne
    private BankCard bankCard; //收款银行卡
    @ManyToOne
    private LoanInfo loanInfo; //档案信息
    @ManyToOne
    private LoanRecord loanRecord;
    private String description; //备注

    public User getPayee() {
        return payee;
    }

    public void setPayee(User payee) {
        this.payee = payee;
    }

    public Double getReceipts() {
        return receipts;
    }

    public void setReceipts(Double receipts) {
        this.receipts = receipts;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public BankCard getBankCard() {
        return bankCard;
    }

    public void setBankCard(BankCard bankCard) {
        this.bankCard = bankCard;
    }

    public String getDescription() {
        return description;
    }

    public LoanInfo getLoanInfo() {
        return loanInfo;
    }

    public void setLoanInfo(LoanInfo loanInfo) {
        this.loanInfo = loanInfo;
    }

    public LoanRecord getLoanRecord() {
        return loanRecord;
    }

    public void setLoanRecord(LoanRecord loanRecord) {
        this.loanRecord = loanRecord;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
