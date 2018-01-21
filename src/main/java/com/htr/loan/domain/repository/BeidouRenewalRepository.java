package com.htr.loan.domain.repository;

import com.htr.loan.domain.BeidouBranch;
import com.htr.loan.domain.BeidouRecord;
import com.htr.loan.domain.BeidouRenewal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BeidouRenewalRepository extends JpaRepository<BeidouRenewal, String>, JpaSpecificationExecutor<BeidouRenewal> {

    List<BeidouRenewal> findAllByBeidouRecordAndActiveTrue(BeidouRecord beidouRecord);

    List<BeidouRenewal> findAllByBeidouBranchAndActiveTrue(BeidouBranch beidouBranch);

    BeidouRenewal findTopByBeidouRecordAndActiveTrue(BeidouRecord beidouRecord);
}
