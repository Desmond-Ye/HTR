package com.htr.loan.web;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.WebUtil;
import com.htr.loan.domain.LoanInfo;
import com.htr.loan.domain.SubLoanRecord;
import com.htr.loan.domain.User;
import com.htr.loan.service.SubLoanRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subLoanRecord")
public class SubLoanRecordController {

    @Autowired
    private SubLoanRecordService subLoanRecordService;

    @RequestMapping(value = "/{currentPage}/{pageSize}", method = RequestMethod.GET)
    public Page<SubLoanRecord> findAll(@PathVariable int currentPage,
                                       @PathVariable int pageSize,
                                       @RequestParam(defaultValue = "{}") String jsonFilter) {
        Map<String, Object> filterParams = WebUtil.getParametersStartingWith(jsonFilter, Constants.SEARCH_PREFIX);
        filterParams.put("EQ_active", Constants.RECORD_EXIST);
        String sortData = "[{\"property\":\"receiptDate\",\"direction\":\"DESC\"}]";
        return subLoanRecordService.findAll(filterParams, WebUtil.buildPageRequest(currentPage, pageSize, sortData));
    }

    @RequestMapping(value = "/loanInfoID/{loanInfoID}", method = RequestMethod.GET)
    public List<SubLoanRecord> findAllByLoanInfo(@PathVariable String loanInfoID) {
        return subLoanRecordService.findAllByLoanInfo(loanInfoID);
    }

    @RequestMapping(value = "repayment", method = RequestMethod.POST)
    public Map<String, String> repayment(@RequestBody SubLoanRecord subLoanRecord, HttpSession session) {
        Map<String, String> result = null;
        SubLoanRecord tempSubLoanRecord = subLoanRecordService.findByReceiptNumber(subLoanRecord.getReceiptNumber());
        if(tempSubLoanRecord != null){
            result = new HashMap<>();
            result.put(Constants.RESPONSE_CODE, Constants.CODE_FAIL);
            result.put(Constants.RESPONSE_MSG, "收据编号不能重复!");
            return result;
        }

        User user = (User) session.getAttribute(Constants.SESSION_USER_KEY);
        subLoanRecord.setPayee(user);
        subLoanRecord = subLoanRecordService.repayment(subLoanRecord);
        if(subLoanRecord != null){
            result = new HashMap<>();
            result.put(Constants.RESPONSE_CODE, Constants.CODE_SUCCESS);
            result.put(Constants.RESPONSE_MSG, "还款成功!");
        }
        return result;
    }

    @RequestMapping(value = "backRepayment/{receiptNumber}", method = RequestMethod.GET)
    public Map<String, String> backRepayment(@PathVariable String receiptNumber) throws CloneNotSupportedException {
        Map<String, String> result = null;
        SubLoanRecord subLoanRecord = subLoanRecordService.findByReceiptNumber(receiptNumber);
        if(subLoanRecord == null){
            result = new HashMap<>();
            result.put(Constants.RESPONSE_CODE, Constants.CODE_FAIL);
            result.put(Constants.RESPONSE_MSG, "收据编号错误,不存在本次还款!");
            return result;
        } else {
            LoanInfo loanInfo = subLoanRecord.getLoanInfo();
            List<SubLoanRecord> subLoanRecords = subLoanRecordService.findAllByLoanInfo(loanInfo.getUuid());
            for(SubLoanRecord temp : subLoanRecords){
                if(temp.getReceiptDate().getTime() > subLoanRecord.getReceiptDate().getTime()) {
                    result = new HashMap<>();
                    result.put(Constants.RESPONSE_CODE, Constants.CODE_FAIL);
                    result.put(Constants.RESPONSE_MSG, "请先撤销上一次的还款!");
                    return result;
                }
            }
        }
        subLoanRecord = subLoanRecordService.backRepayment(subLoanRecord);
        if(!subLoanRecord.isActive()){
            result = new HashMap<>();
            result.put(Constants.RESPONSE_CODE, Constants.CODE_SUCCESS);
            result.put(Constants.RESPONSE_MSG, "还款成功!");
        }
        return result;
    }
}
