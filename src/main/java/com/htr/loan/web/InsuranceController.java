package com.htr.loan.web;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.WebUtil;
import com.htr.loan.domain.Insurance;
import com.htr.loan.service.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/insurance")
public class InsuranceController {

    @Autowired
    private InsuranceService insuranceService;

    @RequestMapping(value = "/{currentPage}/{pageSize}", method = RequestMethod.GET)
    public Page<Insurance> findAll(@PathVariable int currentPage,
                                @PathVariable int pageSize,
                                @RequestParam(defaultValue = "{}") String jsonFilter){
        Map<String, Object> filterParams = WebUtil.getParametersStartingWith(jsonFilter, Constants.SEARCH_PREFIX);
        filterParams.put("EQ_active", Constants.RECORD_EXIST);
        String sortData = "[{\"property\":\"leftDays\",\"direction\":\"ASC\"}]";
        return insuranceService.findAll(filterParams, WebUtil.buildPageRequest(currentPage, pageSize, sortData));
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public Insurance saveInsurance(@RequestBody Insurance Insurance){
        return insuranceService.saveInsurance(Insurance);
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> removeInsurance(@RequestBody List<Insurance> Insurances){
        boolean isDeleted = insuranceService.removeInsurances(Insurances);
        return WebUtil.buildDeleteMethodResult(isDeleted);
    }
}
