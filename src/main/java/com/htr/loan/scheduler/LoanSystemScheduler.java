package com.htr.loan.scheduler;

import com.htr.loan.Utils.DateUtils;
import com.htr.loan.Utils.LoanInfoHelper;
import com.htr.loan.domain.Insurance;
import com.htr.loan.domain.LoanInfo;
import com.htr.loan.domain.LoanRecord;
import com.htr.loan.domain.Vehicle;
import com.htr.loan.domain.repository.InsuranceRepository;
import com.htr.loan.domain.repository.LoanInfoRepository;
import com.htr.loan.domain.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class LoanSystemScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(LoanSystemScheduler.class);

    @Autowired
    private LoanInfoRepository loanInfoRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Scheduled(cron = "0 5 1 * * ?")
    public void checkLoanInfosNextRepay(){
        LOG.info("*********检查下次还款日期----开始***********");
        List<LoanInfo> loanInfos = loanInfoRepository.findAllByActiveTrueAndCompletedFalse();
        loanInfos.forEach(loanInfo -> {
            LoanRecord nextRepay = loanInfo.getNextRepay();
            loanInfo.setLeftDays(DateUtils.between(nextRepay.getExpectDate(), LocalDate.now()));
        });
        loanInfoRepository.save(loanInfos);
        LOG.info("*********检查下次还款日期----结束***********");
    }

    @Scheduled(cron = "0 10 1 * * ?")
    public void checkVehicleReviewLeftDays(){
        LOG.info("*********检查审车到期天数----开始***********");
        List<Vehicle> vehicles = vehicleRepository.findAllByActiveTrue();
        vehicles.forEach(vehicle -> vehicle.setLeftDays(DateUtils.between(DateUtils.addOneYears(vehicle.getReviewDate()), LocalDate.now())));
        vehicleRepository.save(vehicles);
        LOG.info("*********检查审车到期天数----结束***********");
    }

    @Scheduled(cron = "0 20 1 * * ?")
    public void checkVehicleInsuranceLeftDays(){
        LOG.info("*********检查审车到期天数----开始***********");
        List<Insurance> insurances = insuranceRepository.findAllByActiveTrue();
        insurances.forEach(insurance -> insurance.setLeftDays(DateUtils.between(insurance.getEndInsuranceTime(), LocalDate.now())));
        insuranceRepository.save(insurances);
        LOG.info("*********检查审车到期天数----结束***********");
    }
}
