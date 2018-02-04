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
public class InsuranceRenewal extends BaseDomain {

    @ManyToOne
    private Insurance insurance; //保险
    private String insuranceName; //保险名称
    private String insuranceCompany; //保险公司
    private String insuranceNum; //保单号
    @JsonFormat
    private Date startInsuranceTime; //开始保险时间
    @JsonFormat
    private Date endInsuranceTime; //保险到期时间
    private Double insuranceFee; //保险额

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getInsuranceNum() {
        return insuranceNum;
    }

    public void setInsuranceNum(String insuranceNum) {
        this.insuranceNum = insuranceNum;
    }

    public Date getStartInsuranceTime() {
        return startInsuranceTime;
    }

    public void setStartInsuranceTime(Date startInsuranceTime) {
        this.startInsuranceTime = startInsuranceTime;
    }

    public Date getEndInsuranceTime() {
        return endInsuranceTime;
    }

    public void setEndInsuranceTime(Date endInsuranceTime) {
        this.endInsuranceTime = endInsuranceTime;
    }

    public Double getInsuranceFee() {
        return insuranceFee;
    }

    public void setInsuranceFee(Double insuranceFee) {
        this.insuranceFee = insuranceFee;
    }
}
