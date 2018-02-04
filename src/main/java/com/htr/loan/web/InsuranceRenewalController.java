package com.htr.loan.web;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.WebUtil;
import com.htr.loan.domain.InsuranceRenewal;
import com.htr.loan.service.InsuranceRenewalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/insuranceRenewal")
public class InsuranceRenewalController {

    @Autowired
    private InsuranceRenewalService insuranceRenewalService;

    @RequestMapping(value = "/{currentPage}/{pageSize}", method = RequestMethod.GET)
    public Page<InsuranceRenewal> findAll(@PathVariable int currentPage,
                                                   @PathVariable int pageSize,
                                                   @RequestParam(defaultValue = "{}") String jsonFilter) {
        Map<String, Object> filterParams = WebUtil.getParametersStartingWith(jsonFilter, Constants.SEARCH_PREFIX);
        filterParams.put("EQ_active", Constants.RECORD_EXIST);
        String sortData = "[{\"property\":\"createdDate\",\"direction\":\"DESC\"}]";

        return insuranceRenewalService.findAll(filterParams, WebUtil.buildPageRequest(currentPage, pageSize, sortData));
    }

    @RequestMapping(value = "/insuranceID/{insuranceID}", method = RequestMethod.GET)
    public List<InsuranceRenewal> findAllByLoanInfo(@PathVariable String insuranceID) {
        return insuranceRenewalService.findAllByInsurance(insuranceID);
    }

    @RequestMapping(value = "renewal", method = RequestMethod.POST)
    public InsuranceRenewal renewal(@RequestBody InsuranceRenewal insuranceRenewal) {
        return insuranceRenewalService.saveInsuranceRenewal(insuranceRenewal);
    }

    @RequestMapping(value = "backRenewal/{insuranceID}", method = RequestMethod.GET)
    public Map<String, String> backRenewal(@PathVariable String insuranceID) {
        Map<String, String> result = new HashMap<>();
        boolean backResult = insuranceRenewalService.backInsuranceRenewal(insuranceID);
        if(backResult){
            result.put(Constants.RESPONSE_CODE, Constants.CODE_SUCCESS);
        } else {
            result.put(Constants.RESPONSE_CODE, Constants.CODE_FAIL);
            result.put(Constants.RESPONSE_MSG, "没有查到续期记录!");
        }
        return result;
    }
}
