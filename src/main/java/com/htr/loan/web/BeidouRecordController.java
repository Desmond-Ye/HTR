package com.htr.loan.web;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.CustomPageResult;
import com.htr.loan.Utils.WebUtil;
import com.htr.loan.domain.BeidouRecord;
import com.htr.loan.domain.BeidouRecord;
import com.htr.loan.domain.User;
import com.htr.loan.service.BeidouRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/beidouRecord")
public class BeidouRecordController {

    @Autowired
    private BeidouRecordService beidouRecordService;

    @RequestMapping(value = "/{currentPage}/{pageSize}", method = RequestMethod.GET)
    public CustomPageResult<BeidouRecord> findAll(@PathVariable int currentPage,
                                @PathVariable int pageSize,
                                @RequestParam(defaultValue = "{}") String jsonFilter){
        Map<String, Object> filterParams = WebUtil.getParametersStartingWith(jsonFilter, Constants.SEARCH_PREFIX);
        filterParams.put("EQ_active", Constants.RECORD_EXIST);
        String sortData = "[{\"property\":\"expireTime\",\"direction\":\"ASC\"}]";

        Page<BeidouRecord> results = beidouRecordService.findAll(filterParams, WebUtil.buildPageRequest(currentPage, pageSize, sortData));
        List<BeidouRecord> tempRecords = copyBeidouRecordList(results.getContent());
        CustomPageResult customPageResult = new CustomPageResult();
        customPageResult.setTotalPages(results.getTotalPages());
        customPageResult.setTotalElements(results.getTotalElements());
        customPageResult.setContent(tempRecords);

        return customPageResult;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public BeidouRecord saveBeidouRecord(@RequestBody BeidouRecord beidouRecord){
        return beidouRecordService.saveBeidouRecord(beidouRecord);
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> removeBeidouRecord(@RequestBody List<BeidouRecord> beidouRecords){
        boolean isDeleted = beidouRecordService.removeBeidouRecords(beidouRecords);
        return WebUtil.buildDeleteMethodResult(isDeleted);
    }

    private List<BeidouRecord> copyBeidouRecordList(List<BeidouRecord> beidouRecords){
        List<BeidouRecord> tempRecords = new ArrayList<>();
        BeidouRecord tempRecord;
        for (BeidouRecord beidouRecord : beidouRecords){
            tempRecord = new BeidouRecord();
            BeanUtils.copyProperties(beidouRecord,tempRecord);
            User user = new User();
            user.setUserName(tempRecord.getInstaller().getUserName());
            tempRecord.setInstaller(user);
            tempRecords.add(tempRecord);
        }
        return tempRecords;
    }
}
