package com.htr.loan.service.impl;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.DateUtils;
import com.htr.loan.Utils.DynamicSpecifications;
import com.htr.loan.Utils.SearchFilter;
import com.htr.loan.domain.BeidouRecord;
import com.htr.loan.domain.BeidouRepair;
import com.htr.loan.domain.SystemLog;
import com.htr.loan.domain.repository.BeidouRecordRepository;
import com.htr.loan.domain.repository.BeidouRepairRepository;
import com.htr.loan.domain.repository.SystemLogRepository;
import com.htr.loan.service.BeidouRepairService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BeidouRepairServiceImpl implements BeidouRepairService {

    private static final Logger LOG = LoggerFactory.getLogger(BeidouRepairServiceImpl.class);

    @Autowired
    private BeidouRepairRepository beidouRepairRepository;

    @Autowired
    private BeidouRecordRepository beidouRecordRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Override
    public BeidouRepair saveBeidouRepair(BeidouRepair beidouRepair) {
        try {
            BeidouRecord beidouRecord = beidouRecordRepository.findOne(beidouRepair.getBeidouRecordId());
            beidouRepair.setBeidouRecord(beidouRecord);
            SystemLog log = new SystemLog(Constants.MODULE_BEIDOUREPAIR, beidouRepair.getBeidouRecord().getLicensePlate());
            log.setOperaType(Constants.OPERATYPE_ADD);
            //换新卡
            if (beidouRepair.getChangeCardType() == 0) {
                beidouRecord.setBorrowCardFlow(false);
                beidouRecord.setOldCardNum(beidouRecord.getNewCardNum());
                beidouRepair.setOldCardNum(beidouRecord.getNewCardNum());
                beidouRecord.setNewCardNum(beidouRepair.getNewCardNum());
            } else if (beidouRepair.getChangeCardType() == -1) {
                beidouRepair.setOldCardNum(beidouRecord.getNewCardNum());
                beidouRecord.setNewCardNum(beidouRepair.getNewCardNum());
                beidouRecord.setBorrowCardFlow(true);
            }
            //是否换终端
            if (beidouRepair.isChangeTerminal()) {
                beidouRepair.setOldTerminal(beidouRecord.getTerminalNum());
                beidouRecord.setTerminalNum(beidouRepair.getNewTerminal());
            }

            beidouRecord = beidouRecordRepository.save(beidouRecord);
            beidouRepair.setBeidouRecord(beidouRecord);
            beidouRepair = beidouRepairRepository.save(beidouRepair);
            log.setRecordId(beidouRepair.getUuid());
            systemLogRepository.save(log);
            return beidouRepair;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("save or update BeidouRepair" + beidouRepair.getBeidouRecord().getLicensePlate() + " fail!");
        }
        return null;
    }

    @Override
    public boolean backBeidouRepair(String beidouRecordID) {
        try {
            BeidouRecord beidouRecord = beidouRecordRepository.findOne(beidouRecordID);
            BeidouRepair beidouRepairTop1 = beidouRepairRepository.findTopByBeidouRecordAndActiveTrue(beidouRecord);
            SystemLog log = new SystemLog(Constants.MODULE_BEIDOURENEWAL, beidouRepairTop1.getBeidouRecord().getLicensePlate(),
                    beidouRepairTop1.getUuid(), Constants.OPERATYPE_BACKRENEWAL);

            if(beidouRepairTop1 == null) {
                return false;
            }

            List<BeidouRepair> beidouRepairs = beidouRepairRepository.findAllByBeidouRecordAndActiveTrue(beidouRecord);
            BeidouRepair beidouRepairTop2 = null;
            for (BeidouRepair tempRepair : beidouRepairs) {
                if (tempRepair.getUuid().equals(beidouRepairTop1.getUuid())) {
                    continue;
                }
                if (beidouRepairTop2 == null) {
                    beidouRepairTop2 = tempRepair;
                } else {
                    if (DateUtils.between(beidouRepairTop2.getCreatedDate(), tempRepair.getCreatedDate()) < 0) {
                        beidouRepairTop2 = tempRepair;
                    }
                }
            }

            if (beidouRepairTop2 == null || beidouRepairTop2.getChangeCardType() != -1) {
                if (beidouRepairTop1.getChangeCardType() == 0) {
                    beidouRecord.setNewCardNum(beidouRecord.getOldCardNum());
                    beidouRecord.setOldCardNum(beidouRepairTop1.getOldCardNum());
                } else if (beidouRepairTop1.getChangeCardType() == -1) {
                    beidouRecord.setBorrowCardFlow(false);
                    beidouRecord.setNewCardNum(beidouRepairTop1.getOldCardNum());
                }
            } else {
                if (beidouRepairTop1.getChangeCardType() == 0) {
                    beidouRecord.setBorrowCardFlow(true);
                    beidouRecord.setNewCardNum(beidouRecord.getOldCardNum());
                    beidouRecord.setOldCardNum(beidouRepairTop1.getOldCardNum());
                } else if (beidouRepairTop1.getChangeCardType() == -1) {
                    beidouRecord.setNewCardNum(beidouRepairTop1.getOldCardNum());
                }
            }

            //是否换终端
            if (beidouRepairTop1.isChangeTerminal()) {
                beidouRecord.setTerminalNum(beidouRepairTop1.getOldCardNum());
            }

            beidouRepairTop1.setActive(false);
            beidouRepairRepository.save(beidouRepairTop1);
            beidouRecordRepository.save(beidouRecord);
            systemLogRepository.save(log);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("back BeidouRepair fail!");
        }
        return false;
    }

    @Override
    public List<BeidouRepair> findAllByBeidouRecord(String beidouRecordID) {
        BeidouRecord beidouRecord = beidouRecordRepository.findOne(beidouRecordID);
        if (null != beidouRecord) {
            return beidouRepairRepository.findAllByBeidouRecordAndActiveTrue(beidouRecord);
        }
        LOG.error("Cannot find the match beidouRecord by UUID" + beidouRecordID);
        return null;
    }

    @Override
    public Page<BeidouRepair> findAll(Map<String, Object> filterParams, Pageable pageable) {
        Map<String, SearchFilter> filterMap = SearchFilter.parse(filterParams);
        return beidouRepairRepository.findAll(DynamicSpecifications
                .bySearchFilter(filterMap.values(), BeidouRepair.class), pageable);
    }
}
