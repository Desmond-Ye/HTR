package com.htr.loan.web;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.WebUtil;
import com.htr.loan.domain.LoanInfo;
import com.htr.loan.service.LoanInfoService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/loanInfo")
public class LoanInfoController {

    @Autowired
    private LoanInfoService loanInfoService;

    @RequestMapping(value = "/{currentPage}/{pageSize}", method = RequestMethod.GET)
    public Page<LoanInfo> findAll(@PathVariable int currentPage,
                                @PathVariable int pageSize,
                                @RequestParam(defaultValue = "{}") String jsonFilter){
        Map<String, Object> filterParams = WebUtil.getParametersStartingWith(jsonFilter, Constants.SEARCH_PREFIX);
        filterParams.put("EQ_active", Constants.RECORD_EXIST);
        String sortData = "[{\"property\":\"completed\",\"direction\":\"ASC\"},{\"property\":\"leftDays\",\"direction\":\"ASC\"}]";
        return loanInfoService.findAll(filterParams, WebUtil.buildPageRequest(currentPage, pageSize, sortData));
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public LoanInfo saveLoanInfo(@RequestBody LoanInfo loanInfo){
        return loanInfoService.saveLoanInfo(loanInfo);
    }

    @RequestMapping(value = "exportMonthReport", method = RequestMethod.GET)
    public void exportMonthReport(HttpServletResponse response) throws IOException{
        HSSFWorkbook workbook = loanInfoService.exportMonthReport();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=Month Report.xls");//默认Excel名称
        response.flushBuffer();
        workbook.write(response.getOutputStream());

    }

    @RequestMapping(value = "exportLoanInfo", method = RequestMethod.GET)
    public void exportLoanInfo(HttpServletResponse response) throws IOException{
        HSSFWorkbook workbook = loanInfoService.exportLoanInfo();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=Arrears.xls");//默认Excel名称
        response.flushBuffer();
        workbook.write(response.getOutputStream());
    }

    @RequestMapping(value = "exportLoanInfoDetail/{loanInfoID}", method = RequestMethod.GET)
    public void exportLoanInfoDetail(@PathVariable String loanInfoID,HttpServletResponse response) throws IOException{
        HSSFWorkbook workbook = loanInfoService.exportLoanInfoDetail(loanInfoID);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=LoanInformation.xls");//默认Excel名称
        response.flushBuffer();
        workbook.write(response.getOutputStream());
    }


    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> removeLoanInfo(@RequestBody List<LoanInfo> loanInfos){
        boolean isDeleted = loanInfoService.removeLoanInfos(loanInfos);
        return WebUtil.buildDeleteMethodResult(isDeleted);
    }
}
