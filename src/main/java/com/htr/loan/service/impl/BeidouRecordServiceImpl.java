package com.htr.loan.service.impl;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.DateUtils;
import com.htr.loan.Utils.DynamicSpecifications;
import com.htr.loan.Utils.SearchFilter;
import com.htr.loan.domain.BeidouRecord;
import com.htr.loan.domain.BeidouRenewal;
import com.htr.loan.domain.BeidouRepair;
import com.htr.loan.domain.SystemLog;
import com.htr.loan.domain.repository.BeidouRecordRepository;
import com.htr.loan.domain.repository.BeidouRenewalRepository;
import com.htr.loan.domain.repository.BeidouRepairRepository;
import com.htr.loan.domain.repository.SystemLogRepository;
import com.htr.loan.service.BeidouRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BeidouRecordServiceImpl implements BeidouRecordService {

    private static final Logger LOG = LoggerFactory.getLogger(BeidouRecordServiceImpl.class);

    @Autowired
    private BeidouRecordRepository beidouRecordRepository;

    @Autowired
    private BeidouRenewalRepository beidouRenewalRepository;

    @Autowired
    private BeidouRepairRepository beidouRepairRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Override
    public BeidouRecord saveBeidouRecord(BeidouRecord beidouRecord) {
        try {
            SystemLog log = new SystemLog(Constants.MODULE_BEIDOURECORD, beidouRecord.getLicensePlate());
            log.setOperaType(Constants.OPERATYPE_ADD);
            //新装设置新卡号和老卡号相同
            beidouRecord.setOldCardNum(beidouRecord.getNewCardNum());
            beidouRecord = beidouRecordRepository.save(beidouRecord);
            beidouRecord.setLeftDays(DateUtils.between(beidouRecord.getExpireTime(), LocalDate.now()));
            log.setRecordId(beidouRecord.getUuid());
            systemLogRepository.save(log);
            return beidouRecord;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("save or update BeidouRecord" + beidouRecord.getLicensePlate() + " fail!");
        }
        return null;
    }

    @Override
    public boolean removeBeidouRecords(List<BeidouRecord> beidouRecordList) {
        try {
            SystemLog log;
            for (BeidouRecord beidouRecord : beidouRecordList) {
                log = new SystemLog(Constants.MODULE_BEIDOURECORD, beidouRecord.getLicensePlate(), beidouRecord.getUuid(), Constants.OPERATYPE_DELETE);
                beidouRecord = beidouRecordRepository.findOne(beidouRecord.getUuid());
                beidouRecord.setActive(false);
                List<BeidouRenewal> beidouRenewals = beidouRenewalRepository.findAllByBeidouRecordAndActiveTrue(beidouRecord);
                beidouRenewals.forEach(beidouRenewal -> beidouRenewal.setActive(false));
                List<BeidouRepair> beidouRepairs = beidouRepairRepository.findAllByBeidouRecordAndActiveTrue(beidouRecord);
                beidouRepairs.forEach(beidouRepair -> beidouRepair.setActive(false));
                beidouRecordRepository.save(beidouRecord);
                beidouRenewalRepository.save(beidouRenewals);
                beidouRepairRepository.save(beidouRepairs);
                systemLogRepository.save(log);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("delete BeidouRecord fail!");
        }
        return false;
    }

    @Override
    public Page<BeidouRecord> findAll(Map<String, Object> filterParams, Pageable pageable) {
        Map<String, SearchFilter> filterMap = SearchFilter.parse(filterParams);
        return beidouRecordRepository.findAll(DynamicSpecifications
                .bySearchFilter(filterMap.values(), BeidouRecord.class), pageable);
    }

    @Override
    public BeidouRecord findByLicensePlate(String licensePlate){
        return beidouRecordRepository.findByLicensePlateAndActiveTrue(licensePlate);
    }
}
