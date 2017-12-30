package com.htr.loan.scheduler;

import com.htr.loan.Utils.DateUtils;
import com.htr.loan.domain.BeidouRecord;
import com.htr.loan.domain.repository.BeidouRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class BeidouSystemScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(BeidouSystemScheduler.class);

    @Autowired
    private BeidouRecordRepository beidouRecordRepository;

    @Scheduled(cron = "0 15 1 * * ?")
    public void checkBeidouRecordsNextRepay(){
        LOG.info("*********检查下次还款日期----开始***********");
        List<BeidouRecord> beidouRecords = beidouRecordRepository.findAllByActiveTrue();
        beidouRecords.forEach(beidouRecord ->
            beidouRecord.setLeftDays(DateUtils.between(beidouRecord.getExpireTime(), LocalDate.now())));
        beidouRecordRepository.save(beidouRecords);
        LOG.info("*********检查下次还款日期----结束***********");
    }
}
