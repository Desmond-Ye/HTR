package com.htr.loan.service;

import com.htr.loan.domain.InsuranceRenewal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface InsuranceRenewalService {

    InsuranceRenewal saveInsuranceRenewal(InsuranceRenewal insuranceRenewal);

    Page<InsuranceRenewal> findAll(Map<String, Object> filterParams, Pageable pageable);

    boolean backInsuranceRenewal(String insuranceID);

    List<InsuranceRenewal> findAllByInsurance(String insuranceID);
}
