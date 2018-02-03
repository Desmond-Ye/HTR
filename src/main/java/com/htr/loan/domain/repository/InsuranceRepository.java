package com.htr.loan.domain.repository;

import com.htr.loan.domain.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InsuranceRepository extends JpaRepository<Insurance, String>, JpaSpecificationExecutor<Insurance> {

    List<Insurance> findAllByActiveTrue();
}