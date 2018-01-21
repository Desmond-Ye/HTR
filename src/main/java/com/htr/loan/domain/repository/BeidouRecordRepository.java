package com.htr.loan.domain.repository;

import com.htr.loan.domain.BeidouBranch;
import com.htr.loan.domain.BeidouRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BeidouRecordRepository extends JpaRepository<BeidouRecord, String>, JpaSpecificationExecutor<BeidouRecord> {

    List<BeidouRecord> findAllByBeidouBranchAndActiveTrue(BeidouBranch beidouBranch);
    List<BeidouRecord> findAllByActiveTrue();
    BeidouRecord findByLicensePlateAndActiveTrue(String licensePlate);
}
