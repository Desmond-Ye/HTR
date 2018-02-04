package com.htr.loan.service.impl;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.DateUtils;
import com.htr.loan.Utils.DynamicSpecifications;
import com.htr.loan.Utils.SearchFilter;
import com.htr.loan.domain.Insurance;
import com.htr.loan.domain.InsuranceRenewal;
import com.htr.loan.domain.SystemLog;
import com.htr.loan.domain.repository.InsuranceRenewalRepository;
import com.htr.loan.domain.repository.InsuranceRepository;
import com.htr.loan.domain.repository.SystemLogRepository;
import com.htr.loan.service.InsuranceRenewalService;
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
public class InsuranceRenewalServiceImpl implements InsuranceRenewalService {

    private static final Logger LOG = LoggerFactory.getLogger(InsuranceRenewalServiceImpl.class);

    @Autowired
    private InsuranceRenewalRepository insuranceRenewalRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Override
    public InsuranceRenewal saveInsuranceRenewal(InsuranceRenewal insuranceRenewal) {
        try {
            Insurance oldInsurance = insuranceRepository.findOne(insuranceRenewal.getInsurance().getUuid());
            SystemLog log = new SystemLog(Constants.MODULE_INSURANCERENEWAL, insuranceRenewal.getInsurance().getVehicle().getLicensePlate());
            log.setOperaType(Constants.OPERATYPE_ADD);
            insuranceRenewal.setInsuranceName(oldInsurance.getInsuranceName());
            insuranceRenewal.setInsuranceCompany(oldInsurance.getInsuranceCompany());
            insuranceRenewal.setInsuranceNum(oldInsurance.getInsuranceNum());
            insuranceRenewal.setStartInsuranceTime(oldInsurance.getStartInsuranceTime());
            insuranceRenewal.setEndInsuranceTime(oldInsurance.getEndInsuranceTime());
            insuranceRenewal.setInsuranceFee(oldInsurance.getInsuranceFee());
            Insurance insurance = insuranceRenewal.getInsurance();
            insurance.setLeftDays(DateUtils.between(insurance.getEndInsuranceTime(), LocalDate.now()));
            insuranceRenewal = insuranceRenewalRepository.save(insuranceRenewal);
            insuranceRepository.save(insurance);
            log.setRecordId(insuranceRenewal.getUuid());
            systemLogRepository.save(log);
            return insuranceRenewal;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("save or update InsuranceRenewal" + insuranceRenewal.getInsurance().getVehicle().getLicensePlate() + " fail!");
        }
        return null;
    }

    @Override
    public boolean backInsuranceRenewal(String insuranceID) {
        try {
            Insurance insurance = insuranceRepository.findOne(insuranceID);
            SystemLog log = new SystemLog(Constants.MODULE_INSURANCERENEWAL, insurance.getVehicle().getLicensePlate(),
                    insurance.getUuid(), Constants.OPERATYPE_BACKRENEWAL);
            List<InsuranceRenewal> insuranceRenewals = insuranceRenewalRepository.findAllByInsuranceAndActiveTrue(insurance);
            InsuranceRenewal insuranceRenewal = null;
            for (InsuranceRenewal tempRenewal: insuranceRenewals) {
                if(null == insuranceRenewal){
                    insuranceRenewal = tempRenewal;
                } else {
                    if (insuranceRenewal.getCreatedDate().getTime() < tempRenewal.getCreatedDate().getTime()) {
                        insuranceRenewal = tempRenewal;
                    }
                }
            }

            insurance.setInsuranceName(insuranceRenewal.getInsuranceName());
            insurance.setInsuranceCompany(insuranceRenewal.getInsuranceCompany());
            insurance.setInsuranceNum(insuranceRenewal.getInsuranceNum());
            insurance.setStartInsuranceTime(insuranceRenewal.getStartInsuranceTime());
            insurance.setEndInsuranceTime(insuranceRenewal.getEndInsuranceTime());
            insurance.setInsuranceFee(insuranceRenewal.getInsuranceFee());

            insurance.setLeftDays(DateUtils.between(insurance.getEndInsuranceTime(), LocalDate.now()));
            insuranceRenewal.setActive(false);
            insuranceRenewalRepository.save(insuranceRenewal);
            insuranceRepository.save(insurance);
            systemLogRepository.save(log);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("back InsuranceRenewal fail!");
        }
        return false;
    }

    @Override
    public List<InsuranceRenewal> findAllByInsurance(String insuranceID) {
        Insurance insurance = insuranceRepository.findOne(insuranceID);
        if (null != insurance) {
            return insuranceRenewalRepository.findAllByInsuranceAndActiveTrue(insurance);
        }
        LOG.error("Cannot find the match insurance by UUID" + insuranceID);
        return null;
    }

    @Override
    public Page<InsuranceRenewal> findAll(Map<String, Object> filterParams, Pageable pageable) {
        Map<String, SearchFilter> filterMap = SearchFilter.parse(filterParams);
        return insuranceRenewalRepository.findAll(DynamicSpecifications
                .bySearchFilter(filterMap.values(), InsuranceRenewal.class), pageable);
    }
}
