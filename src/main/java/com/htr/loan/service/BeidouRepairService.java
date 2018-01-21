package com.htr.loan.service;

import com.htr.loan.domain.BeidouRepair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BeidouRepairService {

    BeidouRepair saveBeidouRepair(BeidouRepair beidouRepair);

    Page<BeidouRepair> findAll(Map<String, Object> filterParams, Pageable pageable);

    boolean backBeidouRepair(String beidouRecordID);

    List<BeidouRepair> findAllByBeidouRecord(String beidouRecordID);
}
