package com.htr.loan.service.impl;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.DateUtils;
import com.htr.loan.Utils.DynamicSpecifications;
import com.htr.loan.Utils.SearchFilter;
import com.htr.loan.domain.BeidouRecord;
import com.htr.loan.domain.BeidouRenewal;
import com.htr.loan.domain.SystemLog;
import com.htr.loan.domain.repository.BeidouRecordRepository;
import com.htr.loan.domain.repository.BeidouRenewalRepository;
import com.htr.loan.domain.repository.SystemLogRepository;
import com.htr.loan.service.BeidouRenewalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BeidouRenewalServiceImpl implements BeidouRenewalService {

    private static final Logger LOG = LoggerFactory.getLogger(BeidouRenewalServiceImpl.class);

    @Autowired
    private BeidouRenewalRepository beidouRenewalRepository;

    @Autowired
    private BeidouRecordRepository beidouRecordRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Override
    public BeidouRenewal saveBeidouRenewal(BeidouRenewal beidouRenewal) {
        try {
            BeidouRecord beidouRecord = beidouRecordRepository.findOne(beidouRenewal.getBeidouRecordId());
            beidouRenewal.setBeidouRecord(beidouRecord);
            SystemLog log = new SystemLog(Constants.MODULE_BEIDOURENEWAL, beidouRenewal.getBeidouRecord().getLicensePlate());
            log.setOperaType(Constants.OPERATYPE_ADD);
            if (beidouRenewal.getChangeCardType() == 0) {
                beidouRecord.setBorrowCardFlow(false);
                beidouRenewal.setOldCardNum(beidouRecord.getOldCardNum());
                beidouRecord.setOldCardNum(beidouRecord.getNewCardNum());
                beidouRecord.setNewCardNum(beidouRenewal.getNewCardNum());
            } else if (beidouRenewal.getChangeCardType() == -1) {
                beidouRecord.setBorrowCardFlow(true);
                beidouRenewal.setOldCardNum(beidouRecord.getNewCardNum());
                beidouRecord.setNewCardNum(beidouRenewal.getNewCardNum());
            }
            //是否换终端
            if (beidouRenewal.isChangeTerminal()) {
                beidouRenewal.setOldTerminal(beidouRecord.getTerminalNum());
                beidouRecord.setTerminalNum(beidouRenewal.getNewTerminal());
            }

            //更新剩余天数
            Date expirTime = org.apache.commons.lang3.time.DateUtils.addMonths(beidouRecord.getExpireTime(), beidouRenewal.getMonths());
            beidouRecord.setExpireTime(expirTime);
            beidouRecord.setLeftDays(DateUtils.between(beidouRecord.getExpireTime(), LocalDate.now()));
            beidouRecord = beidouRecordRepository.save(beidouRecord);
            beidouRenewal.setBeidouRecord(beidouRecord);
            beidouRenewal = beidouRenewalRepository.save(beidouRenewal);
            log.setRecordId(beidouRenewal.getUuid());
            systemLogRepository.save(log);
            return beidouRenewal;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("save or update BeidouRenewal" + beidouRenewal.getBeidouRecord().getLicensePlate() + " fail!");
        }
        return null;
    }

    @Override
    public boolean backBeidouRenewal(String beidouRecordID) {
        try {
            BeidouRecord beidouRecord = beidouRecordRepository.findOne(beidouRecordID);
            BeidouRenewal beidouRenewalTop1 = beidouRenewalRepository.findTopByBeidouRecordAndActiveTrue(beidouRecord);
            SystemLog log = new SystemLog(Constants.MODULE_BEIDOURENEWAL, beidouRenewalTop1.getBeidouRecord().getLicensePlate(),
                    beidouRenewalTop1.getUuid(), Constants.OPERATYPE_BACKRENEWAL);

            if(beidouRenewalTop1 == null) {
                return false;
            }

            List<BeidouRenewal> beidouRenewals = beidouRenewalRepository.findAllByBeidouRecordAndActiveTrue(beidouRecord);
            BeidouRenewal beidouRenewalTop2 = null;
            for (BeidouRenewal tempRenewal : beidouRenewals) {
                if (tempRenewal.getUuid().equals(beidouRenewalTop1.getUuid())) {
                    continue;
                }
                if (beidouRenewalTop2 == null) {
                    beidouRenewalTop2 = tempRenewal;
                } else {
                    if (DateUtils.between(beidouRenewalTop2.getCreatedDate(), tempRenewal.getCreatedDate()) < 0) {
                        beidouRenewalTop2 = tempRenewal;
                    }
                }
            }

            if (beidouRenewalTop2 == null || beidouRenewalTop2.getChangeCardType() != -1) {
                if (beidouRenewalTop1.getChangeCardType() == 0) {
                    beidouRecord.setNewCardNum(beidouRecord.getOldCardNum());
                    beidouRecord.setOldCardNum(beidouRenewalTop1.getOldCardNum());
                } else if (beidouRenewalTop1.getChangeCardType() == -1) {
                    beidouRecord.setBorrowCardFlow(false);
                    beidouRecord.setNewCardNum(beidouRenewalTop1.getOldCardNum());
                }
            } else {
                if (beidouRenewalTop1.getChangeCardType() == 0) {
                    beidouRecord.setBorrowCardFlow(true);
                    beidouRecord.setNewCardNum(beidouRecord.getOldCardNum());
                    beidouRecord.setOldCardNum(beidouRenewalTop1.getOldCardNum());
                } else if (beidouRenewalTop1.getChangeCardType() == -1) {
                    beidouRecord.setNewCardNum(beidouRenewalTop1.getOldCardNum());
                }
            }

            //是否换终端
            if (beidouRenewalTop1.isChangeTerminal()) {
                beidouRecord.setTerminalNum(beidouRenewalTop1.getOldCardNum());
            }

            //更新剩余天数
            Date expirTime = org.apache.commons.lang3.time.DateUtils.addMonths(beidouRecord.getExpireTime(), 0 - beidouRenewalTop1.getMonths());
            beidouRecord.setExpireTime(expirTime);
            beidouRecord.setLeftDays(DateUtils.between(beidouRecord.getExpireTime(), LocalDate.now()));

            beidouRenewalTop1.setActive(false);
            beidouRenewalRepository.save(beidouRenewalTop1);
            beidouRecordRepository.save(beidouRecord);
            systemLogRepository.save(log);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("back BeidouRenewal fail!");
        }
        return false;
    }

    @Override
    public List<BeidouRenewal> findAllByBeidouRecord(String beidouRecordID) {
        BeidouRecord beidouRecord = beidouRecordRepository.findOne(beidouRecordID);
        if (null != beidouRecord) {
            return beidouRenewalRepository.findAllByBeidouRecordAndActiveTrue(beidouRecord);
        }
        LOG.error("Cannot find the match beidouRecord by UUID" + beidouRecordID);
        return null;
    }

    @Override
    public Page<BeidouRenewal> findAll(Map<String, Object> filterParams, Pageable pageable) {
        Map<String, SearchFilter> filterMap = SearchFilter.parse(filterParams);
        return beidouRenewalRepository.findAll(DynamicSpecifications
                .bySearchFilter(filterMap.values(), BeidouRenewal.class), pageable);
    }
}
