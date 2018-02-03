package com.htr.loan.service.impl;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.DateUtils;
import com.htr.loan.Utils.DynamicSpecifications;
import com.htr.loan.Utils.SearchFilter;
import com.htr.loan.domain.SystemLog;
import com.htr.loan.domain.Vehicle;
import com.htr.loan.domain.repository.SystemLogRepository;
import com.htr.loan.domain.repository.VehicleRepository;
import com.htr.loan.service.VehicleService;
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
public class VehicleServiceImpl implements VehicleService {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleServiceImpl.class);

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Override
    public Vehicle saveVehicle(Vehicle vehicle) {
        try {

            SystemLog log = new SystemLog(Constants.MODULE_VEHICLE, vehicle.getLicensePlate());
            if (StringUtils.isNotBlank(vehicle.getUuid())) {
                log.setOperaType(Constants.OPERATYPE_UPDATE);
            } else {
                log.setOperaType(Constants.OPERATYPE_ADD);
            }
            if(null != vehicle.getReviewDate()){
                vehicle.setLeftDays(DateUtils.between(DateUtils.addOneYears(vehicle.getReviewDate()), LocalDate.now()));
            }
            vehicle = vehicleRepository.save(vehicle);
            log.setRecordId(vehicle.getUuid());
            systemLogRepository.save(log);
            return vehicle;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("save or update Vehicle" + vehicle.getLicensePlate() + " fail!");
        }
        return null;
    }

    @Override
    public boolean removeVehicles(List<Vehicle> vehicleList) {
        try {
            SystemLog log;
            for (Vehicle vehicle : vehicleList) {
                log = new SystemLog(Constants.MODULE_VEHICLE, vehicle.getLicensePlate(), vehicle.getUuid(), Constants.OPERATYPE_DELETE);
                vehicle.setActive(false);
                vehicleRepository.save(vehicle);
                systemLogRepository.save(log);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("delete Vehicle fail!");
        }
        return false;
    }

    @Override
    public boolean detainOrNotVehicles(List<Vehicle> vehicleList, boolean detain) {
        try {
            SystemLog log;
            for (Vehicle vehicle : vehicleList) {
                if(detain)
                    log = new SystemLog(Constants.MODULE_VEHICLE, vehicle.getLicensePlate(), vehicle.getUuid(), Constants.OPERATYPE_DETAIN);
                else log = new SystemLog(Constants.MODULE_VEHICLE, vehicle.getLicensePlate(), vehicle.getUuid(), Constants.OPERATYPE_NOTDETAIN);
                vehicle.setDetain(detain);
                vehicleRepository.save(vehicle);
                systemLogRepository.save(log);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("detain Vehicle fail!");
        }
        return false;
    }

    @Override
    public Page<Vehicle> findAll(Map<String, Object> filterParams, Pageable pageable) {
        Map<String, SearchFilter> filterMap = SearchFilter.parse(filterParams);
        return vehicleRepository.findAll(DynamicSpecifications
                .bySearchFilter(filterMap.values(), Vehicle.class), pageable);
    }
}
