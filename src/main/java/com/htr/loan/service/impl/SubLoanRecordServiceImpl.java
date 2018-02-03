package com.htr.loan.service.impl;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.DateUtils;
import com.htr.loan.Utils.DynamicSpecifications;
import com.htr.loan.Utils.LoanInfoHelper;
import com.htr.loan.Utils.MoneyCalculator;
import com.htr.loan.Utils.SearchFilter;
import com.htr.loan.domain.LoanInfo;
import com.htr.loan.domain.LoanRecord;
import com.htr.loan.domain.SubLoanRecord;
import com.htr.loan.domain.SystemLog;
import com.htr.loan.domain.repository.LoanInfoRepository;
import com.htr.loan.domain.repository.SubLoanRecordRepository;
import com.htr.loan.domain.repository.SystemLogRepository;
import com.htr.loan.service.SubLoanRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SubLoanRecordServiceImpl implements SubLoanRecordService {

    private static final Logger LOG = LoggerFactory.getLogger(SubLoanRecordServiceImpl.class);

    @Autowired
    private SubLoanRecordRepository subLoanRecordRepository;

    @Autowired
    private LoanInfoRepository loanInfoRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Override
    public List<SubLoanRecord> findAllByLoanInfo(String loanInfoID) {
        LoanInfo loanInfo = loanInfoRepository.findOne(loanInfoID);
        if (null != loanInfo) {
            return subLoanRecordRepository.findAllByLoanInfoAndActiveTrue(loanInfo);
        }
        LOG.error("Cannot find the match LoanInfo by UUID" + loanInfoID);
        return null;
    }

    @Override
    public Page<SubLoanRecord> findAll(Map<String, Object> filterParams, Pageable pageable) {
        Map<String, SearchFilter> filterMap = SearchFilter.parse(filterParams);
        return subLoanRecordRepository.findAll(DynamicSpecifications
                .bySearchFilter(filterMap.values(), SubLoanRecord.class), pageable);
    }

    @Override
    public SubLoanRecord repayment(SubLoanRecord subLoanRecord) {
        try {
            LoanInfo loanInfo = subLoanRecord.getLoanInfo();
            SystemLog log = new SystemLog(Constants.MODULE_LOANINFO, loanInfo.getLoanInfoNum(), loanInfo.getUuid(), Constants.OPERATYPE_REPAYMENT);

            LoanRecord nextRepay = subLoanRecord.getLoanInfo().getNextRepay();

            double total = loanInfo.getBalance() == null ? 0d : loanInfo.getBalance();
            //获取最后一次还款日期
            Date repayDate = subLoanRecord.getReceiptDate();

            //总还款等于本期所有还款次和加上期结余额
            total = MoneyCalculator.add(total, subLoanRecord.getReceipts());

            boolean isExecute = false;
            //多次还款叠加大于本期需要还款的钱数
            while (total >= nextRepay.getExpectMoney()) {
                isExecute = true;
                total = MoneyCalculator.subtract(total, nextRepay.getExpectMoney());
                nextRepay.setCompleted(true);
                nextRepay.setActualDate(repayDate);
                nextRepay.setOverdueDays(DateUtils.between(nextRepay.getExpectDate(), repayDate));
                loanInfo.setBalance(total);

                //从列表中找出这次还款记录并替换
                List<LoanRecord> loanRecords = loanInfo.getLoanRecords();
                for (int i = 0; i < loanRecords.size(); i++) {
                    if (loanRecords.get(i).getUuid().equals(nextRepay.getUuid())) {
                        loanRecords.set(i, nextRepay);
                        break;
                    }
                }

                loanInfo.setLoanRecords(loanRecords);

                nextRepay = LoanInfoHelper.checkTheNextRepay(loanInfo);
                if (null == nextRepay) {
                    loanInfo.setCompleted(true);
                    loanInfo.setNextRepay(null);
                    break;
                } else {
                    loanInfo.setNextRepay(nextRepay);
                }
            }

            if (!isExecute) {
                loanInfo.setBalance(total);
            }

            //计算应还款总额
            double totalBalance = loanInfo.getTotalRepayment();
            for (LoanRecord loanRecord : loanInfo.getLoanRecords()) {
                if (loanRecord.isCompleted()) {
                    totalBalance = MoneyCalculator.subtract(totalBalance, loanRecord.getExpectMoney());
                }
            }
            loanInfo.setTotalBalance(MoneyCalculator.subtract(totalBalance, loanInfo.getBalance()));

            //计算下期还款天数
            if (loanInfo.isCompleted()) {
                loanInfo.setLeftDays(0);
            } else {
                loanInfo.setLeftDays(DateUtils.between(loanInfo.getNextRepay().getExpectDate(), LocalDate.now()));
            }
            subLoanRecord = subLoanRecordRepository.save(subLoanRecord);
            loanInfoRepository.save(loanInfo);
            systemLogRepository.save(log);
            return subLoanRecord;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("repayment LoanInfo" + subLoanRecord.getLoanInfo().getLoanInfoNum() + " fail!");
        }
        return null;
    }

    @Override
    public SubLoanRecord findByReceiptNumber(String receiptNumber) {
        return subLoanRecordRepository.findByReceiptNumberAndActiveTrue(receiptNumber);
    }

    @Override
    public SubLoanRecord backRepayment(SubLoanRecord subLoanRecord) {
        try {
            LoanInfo loanInfo = subLoanRecord.getLoanInfo();
            LoanRecord beforeRepay = LoanInfoHelper.findCurrentRepay(loanInfo, loanInfo.getNextRepay().getLoanNum() - 1);
            List<LoanRecord> tempLoanRecords = new ArrayList<>();
            while (beforeRepay != null && !beforeRepay.getUuid().equals(subLoanRecord.getLoanRecord().getUuid())){
                tempLoanRecords.add(beforeRepay);
                beforeRepay = LoanInfoHelper.findCurrentRepay(loanInfo, beforeRepay.getLoanNum() - 1);
            }
            subLoanRecord.setActive(false);
            SystemLog log = new SystemLog(Constants.MODULE_LOANINFO, loanInfo.getLoanInfoNum(), loanInfo.getUuid(), Constants.OPERATYPE_BACKREPAYMENT);

            LoanRecord nextRepay = subLoanRecord.getLoanInfo().getNextRepay();

            //如果还在本期还款内
            if (subLoanRecord.getLoanRecord().getUuid().equals(nextRepay.getUuid())) {
                loanInfo.setBalance(MoneyCalculator.subtract(loanInfo.getBalance(), subLoanRecord.getReceipts()));
            } else {

                loanInfo.getLoanRecords().forEach(loanRecord -> {
                    for(LoanRecord temp : tempLoanRecords){
                        if(temp.getUuid().equals(loanRecord.getUuid())){
                            loanRecord.setCompleted(false);
                            loanRecord.setActualDate(null);
                        }
                    }
                });
                nextRepay = subLoanRecord.getLoanRecord();
                nextRepay.setCompleted(false);
                nextRepay.setActualDate(null);
                loanInfo.setNextRepay(nextRepay);
                double total = loanInfo.getBalance();
                if(tempLoanRecords.size() > 0) {
                    for(LoanRecord tempLoanRecord : tempLoanRecords){
                        total = MoneyCalculator.add(total, tempLoanRecord.getExpectMoney());
                    }
                }
                total = MoneyCalculator.add(total, nextRepay.getExpectMoney());
                loanInfo.setBalance(MoneyCalculator.subtract(total, subLoanRecord.getReceipts()));
            }

            subLoanRecord = subLoanRecordRepository.save(subLoanRecord);
            loanInfo = loanInfoRepository.save(loanInfo);

            //计算应还款总额
            double totalBalance = loanInfo.getTotalRepayment();
            for (LoanRecord loanRecord : loanInfo.getLoanRecords()) {
                if (loanRecord.isCompleted()) {
                    totalBalance = MoneyCalculator.subtract(totalBalance, loanRecord.getExpectMoney());
                }
            }
            loanInfo.setTotalBalance(MoneyCalculator.subtract(totalBalance, loanInfo.getBalance()));

            if (loanInfo.isCompleted()) {
                loanInfo.setCompleted(false);
            }
            //计算下期还款天数
            loanInfo.setLeftDays(DateUtils.between(loanInfo.getNextRepay().getExpectDate(), LocalDate.now()));
            loanInfoRepository.save(loanInfo);
            systemLogRepository.save(log);
            return subLoanRecord;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("backRepayment LoanInfo" + subLoanRecord.getLoanInfo().getLoanInfoNum() + " fail!");
        }
        return null;
    }
}
