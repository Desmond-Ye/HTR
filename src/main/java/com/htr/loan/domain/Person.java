package com.htr.loan.domain;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Person extends BaseDomain {
    private String name; //姓名
    private String spouseName; //配偶姓名
    @OneToMany
    @Cascade(CascadeType.ALL)
    private List<PhoneInfo> phoneInfos; //电话号码
    private String address; //地址
    private String spouseAddress; //配偶地址
    private String idNumber;  //身份证号
    private String spouseIdNumber; //配偶身份证号
    private boolean surety;  //是否是担保人

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public List<PhoneInfo> getPhoneInfos() {
        return phoneInfos;
    }

    public void setPhoneInfos(List<PhoneInfo> phoneInfos) {
        this.phoneInfos = phoneInfos;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSpouseAddress() {
        return spouseAddress;
    }

    public void setSpouseAddress(String spouseAddress) {
        this.spouseAddress = spouseAddress;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getSpouseIdNumber() {
        return spouseIdNumber;
    }

    public void setSpouseIdNumber(String spouseIdNumber) {
        this.spouseIdNumber = spouseIdNumber;
    }

    public boolean isSurety() {
        return surety;
    }

    public void setSurety(boolean surety) {
        this.surety = surety;
    }
}
