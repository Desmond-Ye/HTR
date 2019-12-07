package com.htr.loan.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class BeidouRecord extends BaseDomain {

    private String htrID; //HTR编号
    private String licensePlate;  //车牌号
    private String frameNumber;  //车架号
    private String vehicleType; //车辆类型
    private String vehicleNum;  //车辆型号
    private String businessOwner; //业户
    private String owner;  //车主
    private String phoneNum; //电话号码
    private String terminalNum; //终端号
    private String manufacturer; //终端制造商
    private String oldCardNum;  //原始卡号
    private String newCardNum;  //卡号
    private Boolean borrowCardFlow; //是否借流量
    @ManyToOne
    private BeidouBranch beidouBranch; //安装点
    @ManyToOne
    private User installer; //安装人
    private String installType; //安装类型
    @JsonFormat
    private Date joinTime;  //入网时间
    @JsonFormat
    private Date expireTime;  //失效时间
    private Double installationFee; //安装费
    private long leftDays;//剩余还款天数(可以不要这个属性,在前台动态计算. 但是不要这个属性不好实现按逾期天数排序)
    private String description; //备注

    public String getHtrID() {
        return htrID;
    }

    public void setHtrID(String htrID) {
        this.htrID = htrID;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
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

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleNum() {
        return vehicleNum;
    }

    public void setVehicleNum(String vehicleNum) {
        this.vehicleNum = vehicleNum;
    }

    public String getBusinessOwner() {
        return businessOwner;
    }

    public void setBusinessOwner(String businessOwner) {
        this.businessOwner = businessOwner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getTerminalNum() {
        return terminalNum;
    }

    public void setTerminalNum(String terminalNum) {
        this.terminalNum = terminalNum;
    }

    public String getOldCardNum() {
        return oldCardNum;
    }

    public void setOldCardNum(String oldCardNum) {
        this.oldCardNum = oldCardNum;
    }

    public String getNewCardNum() {
        return newCardNum;
    }

    public void setNewCardNum(String newCardNum) {
        this.newCardNum = newCardNum;
    }

    public Boolean getBorrowCardFlow() {
        return borrowCardFlow;
    }

    public void setBorrowCardFlow(Boolean borrowCardFlow) {
        this.borrowCardFlow = borrowCardFlow;
    }

    public BeidouBranch getBeidouBranch() {
        return beidouBranch;
    }

    public void setBeidouBranch(BeidouBranch beidouBranch) {
        this.beidouBranch = beidouBranch;
    }

    public User getInstaller() {
        return installer;
    }

    public void setInstaller(User installer) {
        this.installer = installer;
    }

    public String getInstallType() {
        return installType;
    }

    public void setInstallType(String installType) {
        this.installType = installType;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Double getInstallationFee() {
        return installationFee;
    }

    public void setInstallationFee(Double installationFee) {
        this.installationFee = installationFee;
    }

    public long getLeftDays() {
        return leftDays;
    }

    public void setLeftDays(long leftDays) {
        this.leftDays = leftDays;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
