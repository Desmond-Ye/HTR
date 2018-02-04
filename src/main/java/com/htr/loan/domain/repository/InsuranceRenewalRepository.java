package com.htr.loan.domain.repository;

import com.htr.loan.domain.Insurance;
import com.htr.loan.domain.InsuranceRenewal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InsuranceRenewalRepository extends JpaRepository<InsuranceRenewal, String>, JpaSpecificationExecutor<InsuranceRenewal> {

    List<InsuranceRenewal> findAllByInsuranceAndActiveTrue(Insurance insurance);
}
