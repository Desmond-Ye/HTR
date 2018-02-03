package com.htr.loan.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.OneToOne;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Vehicle extends BaseDomain {
    private String brand;  //车辆品牌
    @OneToOne
    private Person holder;  //所有人
    private String licensePlate;  //车牌号
    private String frameNumber;  //车架号
    private String engineNumber; //发动机号
    private Double evaluation; //预估价
    @JsonFormat
    private Date registrationDate; //上户时间
    private Date reviewDate; //审车时间
    private boolean detain; //是否被扣留
    private long leftDays;//距离下次审车时间
    private boolean trailer; //是挂车吗

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Person getHolder() {
        return holder;
    }

    public void setHolder(Person holder) {
        this.holder = holder;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(String frameNumber) {
        this.frameNumber = frameNumber;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public Double getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Double evaluation) {
        this.evaluation = evaluation;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public boolean isDetain() {
        return detain;
    }

    public void setDetain(boolean detain) {
        this.detain = detain;
    }

    public long getLeftDays() {
        return leftDays;
    }

    public void setLeftDays(long leftDays) {
        this.leftDays = leftDays;
    }

    public boolean isTrailer() {
        return trailer;
    }

    public void setTrailer(boolean trailer) {
        this.trailer = trailer;
    }
}
