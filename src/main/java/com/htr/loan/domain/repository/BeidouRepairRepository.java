package com.htr.loan.domain.repository;

import com.htr.loan.domain.BeidouBranch;
import com.htr.loan.domain.BeidouRecord;
import com.htr.loan.domain.BeidouRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BeidouRepairRepository extends JpaRepository<BeidouRepair, String>, JpaSpecificationExecutor<BeidouRepair> {

    List<BeidouRepair> findAllByBeidouRecordAndActiveTrue(BeidouRecord beidouRecord);

    List<BeidouRepair> findAllByBeidouBranchAndActiveTrue(BeidouBranch beidouBranch);

    BeidouRepair findTopByBeidouRecordAndActiveTrue(BeidouRecord beidouRecord);
}
