package com.htr.loan.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Date;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class LoanInfo extends BaseDomain {
    private String loanInfoNum; //档案号
    @OneToOne
    private Person surety; //主担保人
    @OneToOne
    private Person secondSurety; //次担保人
    @OneToOne
    private Vehicle vehicle; //车辆信息
    @OneToOne
    private Vehicle trailer; //挂车信息
    @ManyToOne
    private BankCard bankCard; //银行卡
    private Double loanAmount; //贷款额
    private Double totalRepayment; //应还款总额
    private Double balance; //当前已还款额
    private Double remainder; //上期多还的金额
    private Double totalBalance; //总余额
    @JsonFormat
    private Date loanDate; //放款日期
    private Integer loansNum; //贷款期数
    @OneToMany
    @Cascade(CascadeType.ALL)
    private List<LoanRecord> loanRecords; //还款记录
    private String receiptNum; //收据编号
    @OneToOne
    private LoanRecord nextRepay;//下次还款记录
    private long leftDays;//剩余还款天数(可以不要这个属性,在前台动态计算. 但是不要这个属性不好实现按逾期天数排序)
    private boolean completed; //是否已完成所有还款

    public String getLoanInfoNum() {
        return loanInfoNum;
    }

    public void setLoanInfoNum(String loanInfoNum) {
        this.loanInfoNum = loanInfoNum;
    }

    public Person getSurety() {
        return surety;
    }

    public void setSurety(Person surety) {
        this.surety = surety;
    }

    public Person getSecondSurety() {
        return secondSurety;
    }

    public void setSecondSurety(Person secondSurety) {
        this.secondSurety = secondSurety;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Vehicle getTrailer() {
        return trailer;
    }

    public void setTrailer(Vehicle trailer) {
        this.trailer = trailer;
    }

    public BankCard getBankCard() {
        return bankCard;
    }

    public void setBankCard(BankCard bankCard) {
        this.bankCard = bankCard;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Double getTotalRepayment() {
        return totalRepayment;
    }

    public void setTotalRepayment(Double totalRepayment) {
        this.totalRepayment = totalRepayment;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getRemainder() {
        return remainder;
    }

    public void setRemainder(Double remainder) {
        this.remainder = remainder;
    }

    public Double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Integer getLoansNum() {
        return loansNum;
    }

    public void setLoansNum(Integer loansNum) {
        this.loansNum = loansNum;
    }

    public List<LoanRecord> getLoanRecords() {
        return loanRecords;
    }

    public void setLoanRecords(List<LoanRecord> loanRecords) {
        this.loanRecords = loanRecords;
    }

    public String getReceiptNum() {
        return receiptNum;
    }

    public void setReceiptNum(String receiptNum) {
        this.receiptNum = receiptNum;
    }

    public LoanRecord getNextRepay() {
        return nextRepay;
    }

    public void setNextRepay(LoanRecord nextRepay) {
        this.nextRepay = nextRepay;
    }

    public long getLeftDays() {
        return leftDays;
    }

    public void setLeftDays(long leftDays) {
        this.leftDays = leftDays;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
