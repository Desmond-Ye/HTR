package com.htr.loan.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Insurance extends BaseDomain {

    @ManyToOne
    private Vehicle vehicle; //车辆
    private String insuranceName; //保险名称
    private String insuranceCompany; //保险公司
    private String insuranceNum; //保单号
    @JsonFormat
    private Date startInsuranceTime; //开始保险时间
    @JsonFormat
    private Date endInsuranceTime; //保险到期时间
    private Double insuranceFee; //保险额
    private long leftDays;//距离到期天数

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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

    public long getLeftDays() {
        return leftDays;
    }

    public void setLeftDays(long leftDays) {
        this.leftDays = leftDays;
    }
}
