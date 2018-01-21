package com.htr.loan.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class BeidouRepair extends BaseDomain {

    @ManyToOne
    private BeidouRecord beidouRecord; //档案
    @Transient
    private String beidouRecordId;
    private boolean changeTerminal; //是否换终端
    private String oldTerminal;  //原终端
    private String newTerminal;  //新终端号
    private boolean changeCard; //是否换卡
    private int changeCardType; //换卡类型
    private String oldCardNum;  //原卡号
    private String newCardNum;  //卡号
    private Double repairFee;  //续费金额
    @ManyToOne
    private BeidouBranch beidouBranch; //维修点
    @ManyToOne
    private User payee; //维修人
    @JsonFormat
    private Date repairDate; //维修日期
    private String description; //备注

    public BeidouRecord getBeidouRecord() {
        return beidouRecord;
    }

    public String getBeidouRecordId() {
        return beidouRecordId;
    }

    public void setBeidouRecordId(String beidouRecordId) {
        this.beidouRecordId = beidouRecordId;
    }

    public void setBeidouRecord(BeidouRecord beidouRecord) {
        this.beidouRecord = beidouRecord;
    }

    public boolean isChangeTerminal() {
        return changeTerminal;
    }

    public void setChangeTerminal(boolean changeTerminal) {
        this.changeTerminal = changeTerminal;
    }

    public String getOldTerminal() {
        return oldTerminal;
    }

    public void setOldTerminal(String oldTerminal) {
        this.oldTerminal = oldTerminal;
    }

    public String getNewTerminal() {
        return newTerminal;
    }

    public void setNewTerminal(String newTerminal) {
        this.newTerminal = newTerminal;
    }

    public boolean isChangeCard() {
        return changeCard;
    }

    public void setChangeCard(boolean changeCard) {
        this.changeCard = changeCard;
    }

    public int getChangeCardType() {
        return changeCardType;
    }

    public void setChangeCardType(int changeCardType) {
        this.changeCardType = changeCardType;
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

    public Double getRepairFee() {
        return repairFee;
    }

    public void setRepairFee(Double repairFee) {
        this.repairFee = repairFee;
    }

    public BeidouBranch getBeidouBranch() {
        return beidouBranch;
    }

    public void setBeidouBranch(BeidouBranch beidouBranch) {
        this.beidouBranch = beidouBranch;
    }

    public User getPayee() {
        return payee;
    }

    public void setPayee(User payee) {
        this.payee = payee;
    }

    public Date getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(Date repairDate) {
        this.repairDate = repairDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
