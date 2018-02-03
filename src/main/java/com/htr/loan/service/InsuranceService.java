package com.htr.loan.service;

import com.htr.loan.domain.Insurance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface InsuranceService {

    Insurance saveInsurance(Insurance insurance);

    Page<Insurance> findAll(Map<String, Object> filterParams, Pageable pageable);

    boolean removeInsurances(List<Insurance> insuranceList);

}
