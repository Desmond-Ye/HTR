package com.htr.loan.service;

import com.htr.loan.domain.BeidouRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BeidouRecordService {

    BeidouRecord saveBeidouRecord(BeidouRecord beidouRecord);

    Page<BeidouRecord> findAll(Map<String, Object> filterParams, Pageable pageable);

    BeidouRecord findByLicensePlate(String uuid);

    boolean removeBeidouRecords(List<BeidouRecord> beidouRecordList);
}
