package com.htr.loan.web;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.CustomPageResult;
import com.htr.loan.Utils.WebUtil;
import com.htr.loan.domain.BeidouRecord;
import com.htr.loan.domain.BeidouRenewal;
import com.htr.loan.domain.BeidouRenewal;
import com.htr.loan.domain.User;
import com.htr.loan.service.BeidouRenewalService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/beidouRenewal")
public class BeidouRenewalController {

    @Autowired
    private BeidouRenewalService beidouRenewalService;

    @RequestMapping(value = "/{currentPage}/{pageSize}", method = RequestMethod.GET)
    public CustomPageResult<BeidouRenewal> findAll(@PathVariable int currentPage,
                                       @PathVariable int pageSize,
                                       @RequestParam(defaultValue = "{}") String jsonFilter) {
        Map<String, Object> filterParams = WebUtil.getParametersStartingWith(jsonFilter, Constants.SEARCH_PREFIX);
        filterParams.put("EQ_active", Constants.RECORD_EXIST);
        String sortData = "[{\"property\":\"renewalDate\",\"direction\":\"DESC\"}]";

        Page<BeidouRenewal> results = beidouRenewalService.findAll(filterParams, WebUtil.buildPageRequest(currentPage, pageSize, sortData));
        List<BeidouRenewal> tempRecords = copyBeidouRenewalList(results.getContent());
        CustomPageResult customPageResult = new CustomPageResult();
        customPageResult.setTotalPages(results.getTotalPages());
        customPageResult.setTotalElements(results.getTotalElements());
        customPageResult.setContent(tempRecords);

        return customPageResult;
    }

    @RequestMapping(value = "/beidouRecordID/{beidouRecordID}", method = RequestMethod.GET)
    public List<BeidouRenewal> findAllByLoanInfo(@PathVariable String beidouRecordID) {
        return beidouRenewalService.findAllByBeidouRecord(beidouRecordID);
    }

    @RequestMapping(value = "renewal", method = RequestMethod.POST)
    public BeidouRenewal renewal(@RequestBody BeidouRenewal beidouRenewal) {
        return beidouRenewalService.saveBeidouRenewal(beidouRenewal);
    }

    @RequestMapping(value = "backRenewal/{beidouRecordID}", method = RequestMethod.GET)
    public Map<String, String> backRenewal(@PathVariable String beidouRecordID) {
        Map<String, String> result = new HashMap<>();
        boolean backResult = beidouRenewalService.backBeidouRenewal(beidouRecordID);
        if(backResult){
            result.put(Constants.RESPONSE_CODE, Constants.CODE_SUCCESS);
        } else {
            result.put(Constants.RESPONSE_CODE, Constants.CODE_FAIL);
            result.put(Constants.RESPONSE_MSG, "没有查到续期记录!");
        }
        return result;
    }

    private List<BeidouRenewal> copyBeidouRenewalList(List<BeidouRenewal> subLoanRecords){
        List<BeidouRenewal> tempRecords = new ArrayList<>();
        BeidouRenewal tempRecord;
        for (BeidouRenewal subLoanRecord : subLoanRecords){
            tempRecord = new BeidouRenewal();
            BeanUtils.copyProperties(subLoanRecord,tempRecord);
            User user = new User();
            user.setUserName(tempRecord.getPayee().getUserName());
            tempRecord.setPayee(user);
            BeidouRecord tempBeidouRecord = new BeidouRecord();
            BeanUtils.copyProperties(tempRecord.getBeidouRecord(),tempBeidouRecord);
            tempBeidouRecord.setInstaller(null);
            tempRecord.setBeidouRecord(tempBeidouRecord);
            tempRecords.add(tempRecord);
        }
        return tempRecords;
    }
}
