package com.htr.loan.service.impl;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.DateUtils;
import com.htr.loan.Utils.DynamicSpecifications;
import com.htr.loan.Utils.SearchFilter;
import com.htr.loan.domain.SystemLog;
import com.htr.loan.domain.Insurance;
import com.htr.loan.domain.Vehicle;
import com.htr.loan.domain.repository.SystemLogRepository;
import com.htr.loan.domain.repository.InsuranceRepository;
import com.htr.loan.domain.repository.VehicleRepository;
import com.htr.loan.service.InsuranceService;
import org.apache.commons.lang3.StringUtils;
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
public class InsuranceServiceImpl implements InsuranceService {

    private static final Logger LOG = LoggerFactory.getLogger(InsuranceServiceImpl.class);

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public Insurance saveInsurance(Insurance insurance) {
        try {

            SystemLog log = new SystemLog(Constants.MODULE_INSURANCE, insurance.getInsuranceNum());
            if (StringUtils.isNotBlank(insurance.getUuid())) {
                log.setOperaType(Constants.OPERATYPE_UPDATE);
            } else {
                log.setOperaType(Constants.OPERATYPE_ADD);
            }
            insurance.setLeftDays(DateUtils.between(insurance.getEndInsuranceTime(), LocalDate.now()));
            Vehicle vehicle = insurance.getVehicle();
            if(null != vehicle && null != vehicle.getReviewDate()){
                vehicle.setLeftDays(DateUtils.between(DateUtils.addOneYears(vehicle.getReviewDate()), LocalDate.now()));
                vehicleRepository.save(vehicle);
            }
            insurance = insuranceRepository.save(insurance);
            log.setRecordId(insurance.getUuid());
            systemLogRepository.save(log);
            return insurance;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("save or update Insurance" + insurance.getInsuranceNum() + " fail!");
        }
        return null;
    }

    @Override
    public boolean removeInsurances(List<Insurance> insuranceList) {
        try {
            SystemLog log;
            for (Insurance insurance : insuranceList) {
                log = new SystemLog(Constants.MODULE_INSURANCE, insurance.getInsuranceNum(), insurance.getUuid(), Constants.OPERATYPE_DELETE);
                insurance.setActive(false);
                insuranceRepository.save(insurance);
                systemLogRepository.save(log);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("delete Insurance fail!");
        }
        return false;
    }

    @Override
    public Page<Insurance> findAll(Map<String, Object> filterParams, Pageable pageable) {
        Map<String, SearchFilter> filterMap = SearchFilter.parse(filterParams);
        return insuranceRepository.findAll(DynamicSpecifications
                .bySearchFilter(filterMap.values(), Insurance.class), pageable);
    }
}
