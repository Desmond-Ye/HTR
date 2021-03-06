package com.htr.loan.web;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.CustomPageResult;
import com.htr.loan.Utils.WebUtil;
import com.htr.loan.domain.BeidouRecord;
import com.htr.loan.domain.BeidouRepair;
import com.htr.loan.domain.BeidouRepair;
import com.htr.loan.domain.User;
import com.htr.loan.service.BeidouRepairService;
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
@RequestMapping("/beidouRepair")
public class BeidouRepairController {

    @Autowired
    private BeidouRepairService beidouRepairService;

    @RequestMapping(value = "/{currentPage}/{pageSize}", method = RequestMethod.GET)
    public CustomPageResult<BeidouRepair> findAll(@PathVariable int currentPage,
                                       @PathVariable int pageSize,
                                       @RequestParam(defaultValue = "{}") String jsonFilter) {
        Map<String, Object> filterParams = WebUtil.getParametersStartingWith(jsonFilter, Constants.SEARCH_PREFIX);
        filterParams.put("EQ_active", Constants.RECORD_EXIST);
        String sortData = "[{\"property\":\"repairDate\",\"direction\":\"DESC\"}]";

        Page<BeidouRepair> results = beidouRepairService.findAll(filterParams, WebUtil.buildPageRequest(currentPage, pageSize, sortData));
        List<BeidouRepair> tempRecords = copyBeidouRepairList(results.getContent());
        CustomPageResult customPageResult = new CustomPageResult();
        customPageResult.setTotalPages(results.getTotalPages());
        customPageResult.setTotalElements(results.getTotalElements());
        customPageResult.setContent(tempRecords);

        return customPageResult;
    }

    @RequestMapping(value = "/beidouRecordID/{beidouRecordID}", method = RequestMethod.GET)
    public List<BeidouRepair> findAllByLoanInfo(@PathVariable String beidouRecordID) {
        return beidouRepairService.findAllByBeidouRecord(beidouRecordID);
    }

    @RequestMapping(value = "repair", method = RequestMethod.POST)
    public BeidouRepair repair(@RequestBody BeidouRepair beidouRepair) {
        return beidouRepairService.saveBeidouRepair(beidouRepair);
    }

    @RequestMapping(value = "backRepair/{beidouRecordID}", method = RequestMethod.GET)
    public Map<String, String> backRepair(@PathVariable String beidouRecordID) {
        Map<String, String> result = new HashMap<>();
        boolean backResult = beidouRepairService.backBeidouRepair(beidouRecordID);
        if(backResult){
            result.put(Constants.RESPONSE_CODE, Constants.CODE_SUCCESS);
        } else {
            result.put(Constants.RESPONSE_CODE, Constants.CODE_FAIL);
            result.put(Constants.RESPONSE_MSG, "没有查到维修记录!");
        }
        return result;
    }

    private List<BeidouRepair> copyBeidouRepairList(List<BeidouRepair> subLoanRecords){
        List<BeidouRepair> tempRecords = new ArrayList<>();
        BeidouRepair tempRecord;
        for (BeidouRepair subLoanRecord : subLoanRecords){
            tempRecord = new BeidouRepair();
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
