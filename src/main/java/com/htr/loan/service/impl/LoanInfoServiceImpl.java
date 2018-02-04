package com.htr.loan.service.impl;

import com.htr.loan.Utils.Constants;
import com.htr.loan.Utils.DateUtils;
import com.htr.loan.Utils.DynamicSpecifications;
import com.htr.loan.Utils.LoanInfoHelper;
import com.htr.loan.Utils.MoneyCalculator;
import com.htr.loan.Utils.SearchFilter;
import com.htr.loan.domain.LoanInfo;
import com.htr.loan.domain.LoanRecord;
import com.htr.loan.domain.PhoneInfo;
import com.htr.loan.domain.SubLoanRecord;
import com.htr.loan.domain.SystemLog;
import com.htr.loan.domain.repository.LoanInfoRepository;
import com.htr.loan.domain.repository.SubLoanRecordRepository;
import com.htr.loan.domain.repository.SystemLogRepository;
import com.htr.loan.service.LoanInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
@Transactional
public class LoanInfoServiceImpl implements LoanInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(LoanInfoServiceImpl.class);

    @Autowired
    private LoanInfoRepository loanInfoRepository;

    @Autowired
    private SubLoanRecordRepository subLoanRecordRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Override
    public LoanInfo saveLoanInfo(LoanInfo loanInfo) {
        try {
            SystemLog log = new SystemLog(Constants.MODULE_LOANINFO, loanInfo.getLoanInfoNum());
            log.setOperaType(Constants.OPERATYPE_ADD);
            loanInfo = loanInfoRepository.save(loanInfo);
            LoanRecord nextRepay = LoanInfoHelper.checkTheNextRepay(loanInfo);
            loanInfo.setNextRepay(nextRepay);
            double totalPayment = 0d;
            for(LoanRecord loanRecord : loanInfo.getLoanRecords()){
                totalPayment = MoneyCalculator.add(totalPayment, loanRecord.getExpectMoney());
            }
            loanInfo.setTotalRepayment(totalPayment);
            loanInfo.setLeftDays(DateUtils.between(nextRepay.getExpectDate(), LocalDate.now()));
            loanInfo = loanInfoRepository.save(loanInfo);
            log.setRecordId(loanInfo.getUuid());
            systemLogRepository.save(log);
            return loanInfo;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("save or update LoanInfo" + loanInfo.getLoanInfoNum() + " fail!");
        }
        return null;
    }

    @Override
    public boolean removeLoanInfos(List<LoanInfo> loanInfoList) {
        try {
            SystemLog log;
            for (LoanInfo loanInfo : loanInfoList) {
                log = new SystemLog(Constants.MODULE_LOANINFO, loanInfo.getLoanInfoNum(), loanInfo.getUuid(), Constants.OPERATYPE_DELETE);
                loanInfo.setActive(false);
                loanInfo.getLoanRecords().forEach(loanRecord -> loanRecord.setActive(false));
                List<SubLoanRecord> subLoanRecords = subLoanRecordRepository.findAllByLoanInfoAndActiveTrue(loanInfo);
                subLoanRecords.forEach(subLoanRecord -> subLoanRecord.setActive(false));
                subLoanRecordRepository.save(subLoanRecords);
                loanInfoRepository.save(loanInfo);
                systemLogRepository.save(log);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("delete LoanInfo fail!");
        }
        return false;
    }

    @Override
    public Page<LoanInfo> findAll(Map<String, Object> filterParams, Pageable pageable) {
        Map<String, SearchFilter> filterMap = SearchFilter.parse(filterParams);
        return loanInfoRepository.findAll(DynamicSpecifications
                .bySearchFilter(filterMap.values(), LoanInfo.class), pageable);
    }

    @Override
    public HSSFWorkbook exportMonthReport() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("月报表");

        //写入表头
        createMonthReportTitle(workbook, sheet);

        List<LoanInfo> loanInfos = loanInfoRepository.findAllByActiveTrue();

        int rowNum = 1;
        for (LoanInfo loanInfo : loanInfos) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(rowNum);
            row.createCell(1).setCellValue(loanInfo.getLoanInfoNum());
            row.createCell(2).setCellValue(loanInfo.getVehicle().getHolder().getName());

            Double tempExpectMoney = 0d;
            Double tempExpectMoneyTotle = 0d;
            for (LoanRecord loanRecord : loanInfo.getLoanRecords()) {
                if (DateUtils.isThisMonth(loanRecord.getExpectDate())) {
                    tempExpectMoney = loanRecord.getExpectMoney();
                    tempExpectMoneyTotle = MoneyCalculator.add(tempExpectMoneyTotle, tempExpectMoney);
                } else if (loanRecord.getExpectDate().getTime() < new Date().getTime()) {
                    tempExpectMoneyTotle = MoneyCalculator.add(tempExpectMoneyTotle, loanRecord.getExpectMoney());
                }
            }
            row.createCell(4).setCellValue(tempExpectMoney);

            Double tempReceipts = 0d;
            Double tempReceiptsTotle = 0d;
            for (SubLoanRecord subLoanRecord : subLoanRecordRepository.findAllByLoanInfoAndActiveTrue(loanInfo)) {
                if (DateUtils.isThisMonth(subLoanRecord.getReceiptDate())) {
                    tempReceipts = MoneyCalculator.add(tempReceipts, subLoanRecord.getReceipts());
                }
                tempReceiptsTotle = MoneyCalculator.add(tempReceiptsTotle, subLoanRecord.getReceipts());
            }
            row.createCell(5).setCellValue(tempReceipts);
            row.createCell(6).setCellValue(MoneyCalculator.subtract(tempExpectMoney, tempReceipts));
            row.createCell(7).setCellValue(MoneyCalculator.subtract(tempExpectMoneyTotle, tempReceiptsTotle));
            row.createCell(8).setCellValue(tempReceiptsTotle);

            row.createCell(3).setCellValue(MoneyCalculator.subtract(tempReceiptsTotle, tempReceipts));

            rowNum++;
        }

        //合计
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        HSSFRow row = sheet.createRow(rowNum);
        HSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("合计");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula("SUM(D2:D" + rowNum + ")");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula("SUM(E2:E" + rowNum + ")");
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula("SUM(F2:F" + rowNum + ")");
        cell.setCellStyle(style);

        cell = row.createCell(6);
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula("SUM(G2:G" + rowNum + ")");
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula("SUM(H2:H" + rowNum + ")");
        cell.setCellStyle(style);

        cell = row.createCell(8);
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula("SUM(I2:I" + rowNum + ")");
        cell.setCellStyle(style);

        CellRangeAddress cellRangeAddress = new CellRangeAddress(rowNum, rowNum, 0, 2);
        sheet.addMergedRegion(cellRangeAddress);
        return workbook;
    }


    @Override
    public HSSFWorkbook exportLoanInfo() {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("客户欠款单");
        //写入表头
        createLoanInfoTitle(workbook, sheet);

        List<LoanInfo> loanInfos = loanInfoRepository.findAllByActiveTrueAndCompletedFalse();

        int rowNum = 1;
        for (LoanInfo loanInfo : loanInfos) {
            if(loanInfo.getLeftDays() >= 0) {
                continue;
            }

            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(rowNum);
            row.createCell(1).setCellValue(loanInfo.getLoanInfoNum());
            row.createCell(2).setCellValue(loanInfo.getVehicle().getHolder().getName());

            String phoneNumbers = "";
            for (PhoneInfo phoneInfo : loanInfo.getVehicle().getHolder().getPhoneInfos()) {
                phoneNumbers += phoneInfo.getDescription() + phoneInfo.getPhoneNum() + "/";
            }
            if(!phoneNumbers.equals("")){
                row.createCell(3).setCellValue(phoneNumbers.substring(0, phoneNumbers.length()-1));
            }
            row.createCell(4).setCellValue(loanInfo.getVehicle().getHolder().getAddress());
            row.createCell(5).setCellValue(loanInfo.getVehicle().getBrand());
            row.createCell(6).setCellValue(loanInfo.getVehicle().getLicensePlate());

            HSSFCellStyle style=workbook.createCellStyle();
            style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
            HSSFCell cell = row.createCell(7);
            cell.setCellValue(loanInfo.getVehicle().getRegistrationDate());
            cell.setCellStyle(style);

            row.createCell(8).setCellValue(loanInfo.getLoanAmount());
            row.createCell(9).setCellValue(loanInfo.getLoansNum());
            row.createCell(10).setCellValue(loanInfo.getNextRepay().getLoanNum() - 1);

            Double tempReceiptsTotle = 0d;
            for (SubLoanRecord subLoanRecord : subLoanRecordRepository.findAllByLoanInfoAndActiveTrue(loanInfo)) {
                tempReceiptsTotle = MoneyCalculator.add(tempReceiptsTotle, subLoanRecord.getReceipts());
            }
            row.createCell(11).setCellValue(tempReceiptsTotle);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(loanInfo.getNextRepay().getExpectDate());
            row.createCell(12).setCellValue("每月 " + calendar.get(Calendar.DATE) + " 日");
            row.createCell(13).setCellValue(loanInfo.getNextRepay().getExpectMoney());
            String tempMonths = "";
            for (LoanRecord loanRecord : loanInfo.getLoanRecords()) {
                if (!loanRecord.isCompleted() && (loanRecord.getExpectDate().getTime() < new Date().getTime())) {
                    Calendar expectDate = Calendar.getInstance();
                    expectDate.setTime(loanRecord.getExpectDate());
                    tempMonths += (expectDate.get(Calendar.MONTH) + 1) + "月,";
                }
            }
            row.createCell(14).setCellValue(tempMonths.substring(0, tempMonths.length() - 1));

            Double tempExpectMoneyTotle = 0d;
            for (LoanRecord loanRecord : loanInfo.getLoanRecords()) {
                if (DateUtils.isThisMonth(loanRecord.getExpectDate())) {
                    tempExpectMoneyTotle = MoneyCalculator.add(tempExpectMoneyTotle, loanRecord.getExpectMoney());
                } else if (loanRecord.getExpectDate().getTime() < new Date().getTime()) {
                    tempExpectMoneyTotle = MoneyCalculator.add(tempExpectMoneyTotle, loanRecord.getExpectMoney());
                }
            }
            row.createCell(15).setCellValue(MoneyCalculator.subtract(tempExpectMoneyTotle, tempReceiptsTotle));
            rowNum++;
        }

        //合计
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        HSSFRow row = sheet.createRow(rowNum);
        HSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("合计");
        cell.setCellStyle(style);

        cell = row.createCell(15);
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula("SUM(P2:P" + rowNum + ")");
        cell.setCellStyle(style);

        CellRangeAddress cellRangeAddress = new CellRangeAddress(rowNum, rowNum, 0, 14);
        sheet.addMergedRegion(cellRangeAddress);

        return workbook;
    }

    @Override
    public HSSFWorkbook exportLoanInfoDetail(String loanInfoID) {

        LoanInfo loanInfo = loanInfoRepository.findOne(loanInfoID);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("保利汽贸车辆信息表");
        //设置为居中加粗
        HSSFCellStyle styleTitle = workbook.createCellStyle();
        HSSFFont fontTitle = workbook.createFont();
        fontTitle.setBold(true);
        styleTitle.setWrapText(true);
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        fontTitle.setFontHeight((short)320);
        styleTitle.setFont(fontTitle);

        HSSFFont fontHeader1 = workbook.createFont();
        fontHeader1.setBold(true);
        fontHeader1.setFontHeight((short)220);
        HSSFCellStyle styleHeader1 = workbook.createCellStyle();
        styleHeader1.setWrapText(true);
        styleHeader1.setAlignment(HorizontalAlignment.CENTER);
        styleHeader1.setFont(fontHeader1);

        HSSFFont fontHeader2 = workbook.createFont();
        fontHeader2.setBold(true);
        fontHeader2.setFontHeight((short)200);
        HSSFCellStyle styleHeader2 = workbook.createCellStyle();
        styleHeader2.setWrapText(true);
        styleHeader2.setAlignment(HorizontalAlignment.CENTER);
        styleHeader2.setFont(fontHeader2);

        HSSFFont fontHeader3 = workbook.createFont();
        fontHeader3.setBold(true);
        fontHeader3.setFontHeight((short)180);
        HSSFCellStyle styleHeader3 = workbook.createCellStyle();
        styleHeader3.setAlignment(HorizontalAlignment.LEFT);
        styleHeader3.setFont(fontHeader3);


        HSSFFont fontContent = workbook.createFont();
        fontContent.setBold(false);
        fontContent.setFontHeight((short)180);
        HSSFCellStyle styleContent = workbook.createCellStyle();
        styleContent.setWrapText(true);
        styleContent.setAlignment(HorizontalAlignment.CENTER);
        styleContent.setFont(fontContent);

        sheet.setColumnWidth(0, 6 * 256);
        sheet.setColumnWidth(1, 11 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 11 * 256);
        sheet.setColumnWidth(4, 11 * 256);
        sheet.setColumnWidth(5, 11 * 256);
        sheet.setColumnWidth(6, 11 * 256);
        sheet.setColumnWidth(7, 11 * 256);
        sheet.setColumnWidth(8, 11 * 256);

        int rowNum = 0;

        HSSFRow row;
        row = sheet.createRow(rowNum);
        HSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("保利汽贸车辆信息表");
        cell.setCellStyle(styleTitle);
        CellRangeAddress cellRangeAddressRow1 = new CellRangeAddress(rowNum, rowNum + 1, 0, 8);
        sheet.addMergedRegion(cellRangeAddressRow1);

        rowNum = rowNum + 2;
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("档案号：" + loanInfo.getLoanInfoNum());
        cell.setCellStyle(styleHeader3);
        CellRangeAddress cellRangeAddressRow2 = new CellRangeAddress(rowNum, rowNum, 0, 8);
        sheet.addMergedRegion(cellRangeAddressRow2);

        rowNum = rowNum + 1;
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("客户信息");
        cell.setCellStyle(styleHeader1);
        CellRangeAddress cellRangeAddressRow3 = new CellRangeAddress(rowNum, rowNum + 2, 0, 0);
        sheet.addMergedRegion(cellRangeAddressRow3);

        cell = row.createCell(1);
        cell.setCellValue("姓名");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(2);
        cell.setCellValue("身份证号");
        cell.setCellStyle(styleHeader2);
        CellRangeAddress cellRangeAddressRow4 = new CellRangeAddress(rowNum, rowNum, 2, 3);
        sheet.addMergedRegion(cellRangeAddressRow4);

        cell = row.createCell(4);
        cell.setCellValue("配偶姓名");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(5);
        cell.setCellValue("配偶身份证号");
        cell.setCellStyle(styleHeader2);
        CellRangeAddress cellRangeAddressRow5 = new CellRangeAddress(rowNum, rowNum, 5, 6);
        sheet.addMergedRegion(cellRangeAddressRow5);

        cell = row.createCell(7);
        cell.setCellValue("电话号码");
        cell.setCellStyle(styleHeader2);
        CellRangeAddress cellRangeAddressRow6 = new CellRangeAddress(rowNum, rowNum, 7, 8);
        sheet.addMergedRegion(cellRangeAddressRow6);

        rowNum = rowNum + 1;
        row = sheet.createRow(rowNum);
        cell = row.createCell(1);
        cell.setCellValue(loanInfo.getVehicle().getHolder().getName());
        cell.setCellStyle(styleContent);

        cell = row.createCell(2);
        cell.setCellValue(loanInfo.getVehicle().getHolder().getIdNumber());
        cell.setCellStyle(styleContent);
        CellRangeAddress cellRangeAddressRow7 = new CellRangeAddress(rowNum, rowNum, 2, 3);
        sheet.addMergedRegion(cellRangeAddressRow7);

        cell = row.createCell(4);
        cell.setCellValue(loanInfo.getVehicle().getHolder().getSpouseName());
        cell.setCellStyle(styleContent);

        cell = row.createCell(5);
        cell.setCellValue(loanInfo.getVehicle().getHolder().getSpouseIdNumber());
        cell.setCellStyle(styleContent);
        CellRangeAddress cellRangeAddressRow8 = new CellRangeAddress(rowNum, rowNum, 5, 6);
        sheet.addMergedRegion(cellRangeAddressRow8);

        cell = row.createCell(7);
        StringBuilder phoneInfos = new StringBuilder();
        loanInfo.getVehicle().getHolder().getPhoneInfos().forEach(phoneInfo -> {
            if(null != phoneInfo.getDescription() && !"".equals(phoneInfo.getDescription())){
                phoneInfos.append(phoneInfo.getDescription());
                phoneInfos.append(":");
            }
            phoneInfos.append(phoneInfo.getPhoneNum());
            phoneInfos.append("/");
        });
        cell.setCellValue(phoneInfos.toString().substring(0, phoneInfos.length() - 1));
        cell.setCellStyle(styleContent);
        CellRangeAddress cellRangeAddressRow9 = new CellRangeAddress(rowNum, rowNum + 1, 7, 8);
        sheet.addMergedRegion(cellRangeAddressRow9);
//
        rowNum = rowNum + 1;
        row = sheet.createRow(rowNum);
        cell = row.createCell(1);
        cell.setCellValue("地址");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(2);
        cell.setCellValue(loanInfo.getVehicle().getHolder().getAddress());
        cell.setCellStyle(styleContent);
        CellRangeAddress cellRangeAddressRow10 = new CellRangeAddress(rowNum, rowNum, 2, 3);
        sheet.addMergedRegion(cellRangeAddressRow10);

        cell = row.createCell(4);
        cell.setCellValue("配偶地址");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(5);
        cell.setCellValue(loanInfo.getVehicle().getHolder().getSpouseAddress());
        cell.setCellStyle(styleContent);
        CellRangeAddress cellRangeAddressRow11 = new CellRangeAddress(rowNum, rowNum, 5, 6);
        sheet.addMergedRegion(cellRangeAddressRow11);

        rowNum = rowNum + 1;
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("保人信息");
        cell.setCellStyle(styleHeader1);
        CellRangeAddress cellRangeAddressRow12 = new CellRangeAddress(rowNum, rowNum + 2, 0, 0);
        sheet.addMergedRegion(cellRangeAddressRow12);

        cell = row.createCell(1);
        cell.setCellValue("保人一");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(2);
        cell.setCellValue("电话");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(3);
        cell.setCellValue("身份证号");
        cell.setCellStyle(styleHeader2);
        CellRangeAddress cellRangeAddressRow13 = new CellRangeAddress(rowNum, rowNum, 3, 4);
        sheet.addMergedRegion(cellRangeAddressRow13);

        cell = row.createCell(5);
        cell.setCellValue("保人二");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(6);
        cell.setCellValue("电话");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(7);
        cell.setCellValue("身份证号");
        cell.setCellStyle(styleHeader2);
        CellRangeAddress cellRangeAddressRow14 = new CellRangeAddress(rowNum, rowNum, 7, 8);
        sheet.addMergedRegion(cellRangeAddressRow14);

        rowNum = rowNum + 1;
        row = sheet.createRow(rowNum);
        cell = row.createCell(1);
        cell.setCellValue(loanInfo.getSurety().getName());
        cell.setCellStyle(styleContent);

        cell = row.createCell(2);
        StringBuilder stringBuilder = new StringBuilder();
        loanInfo.getSurety().getPhoneInfos().forEach(phoneInfo -> {
            if(null != phoneInfo.getDescription() && !"".equals(phoneInfo.getDescription())){
                stringBuilder.append(phoneInfo.getDescription());
                stringBuilder.append(":");
            }
            stringBuilder.append(phoneInfo.getPhoneNum());
            stringBuilder.append("/");
        });
        cell.setCellValue(stringBuilder.substring(0, stringBuilder.length() - 1).toString());
        cell.setCellStyle(styleContent);

        cell = row.createCell(3);
        cell.setCellValue(loanInfo.getSurety().getIdNumber());
        cell.setCellStyle(styleContent);
        CellRangeAddress cellRangeAddressRow15 = new CellRangeAddress(rowNum, rowNum, 3, 4);
        sheet.addMergedRegion(cellRangeAddressRow15);

        if(null != loanInfo.getSecondSurety()){
            cell = row.createCell(5);
            cell.setCellValue(loanInfo.getSecondSurety().getName());
            cell.setCellStyle(styleContent);

            cell = row.createCell(6);
            StringBuilder stringBuilder1 = new StringBuilder();
            loanInfo.getSecondSurety().getPhoneInfos().forEach(phoneInfo -> {
                if(null != phoneInfo.getDescription() && !"".equals(phoneInfo.getDescription())){
                    stringBuilder1.append(phoneInfo.getDescription());
                    stringBuilder1.append(":");
                }
                stringBuilder1.append(phoneInfo.getPhoneNum());
                stringBuilder1.append("/");
            });
            cell.setCellValue(stringBuilder1.substring(0, stringBuilder1.length() - 1).toString());
            cell.setCellStyle(styleContent);

            cell = row.createCell(7);
            cell.setCellValue(loanInfo.getSecondSurety().getIdNumber());
            cell.setCellStyle(styleContent);
            CellRangeAddress cellRangeAddressRow16 = new CellRangeAddress(rowNum, rowNum, 7, 8);
            sheet.addMergedRegion(cellRangeAddressRow16);
        }

        rowNum = rowNum + 1;
        row = sheet.createRow(rowNum);
        cell = row.createCell(1);
        cell.setCellValue("地址");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(2);
        cell.setCellValue(loanInfo.getSurety().getAddress());
        cell.setCellStyle(styleContent);
        CellRangeAddress cellRangeAddressRow17 = new CellRangeAddress(rowNum, rowNum, 2, 3);
        sheet.addMergedRegion(cellRangeAddressRow17);

        cell = row.createCell(4);
        cell.setCellValue("地址");
        cell.setCellStyle(styleHeader2);

        if(null != loanInfo.getSecondSurety()){
            cell = row.createCell(5);
            cell.setCellValue(loanInfo.getSecondSurety().getAddress());
            cell.setCellStyle(styleContent);
            CellRangeAddress cellRangeAddressRow18 = new CellRangeAddress(rowNum, rowNum, 5, 6);
            sheet.addMergedRegion(cellRangeAddressRow18);
        }

        rowNum = rowNum + 1;
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("车辆信息");
        cell.setCellStyle(styleHeader1);
        CellRangeAddress cellRangeAddressRow19 = new CellRangeAddress(rowNum, rowNum + 2, 0, 0);
        sheet.addMergedRegion(cellRangeAddressRow19);

        cell = row.createCell(1);
        cell.setCellValue("车辆品牌");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(2);
        cell.setCellValue("车牌号");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(3);
        cell.setCellValue("车架号");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(4);
        cell.setCellValue("上户日期");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(5);
        cell.setCellValue("挂车品牌");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(6);
        cell.setCellValue("挂车牌号");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(7);
        cell.setCellValue("挂车架号");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(8);
        cell.setCellValue("上户日期");
        cell.setCellStyle(styleHeader2);


        rowNum = rowNum + 1;
        row = sheet.createRow(rowNum);
        cell = row.createCell(1);
        cell.setCellValue(loanInfo.getVehicle().getBrand());
        cell.setCellStyle(styleContent);

        cell = row.createCell(2);
        cell.setCellValue(loanInfo.getVehicle().getLicensePlate());
        cell.setCellStyle(styleContent);

        cell = row.createCell(3);
        cell.setCellValue(loanInfo.getVehicle().getFrameNumber());
        cell.setCellStyle(styleContent);

        cell = row.createCell(4);
        cell.setCellValue(DateUtils.YYYYlMMlDD.format(loanInfo.getVehicle().getRegistrationDate()));
        cell.setCellStyle(styleContent);

        if(null != loanInfo.getTrailer()){
            cell = row.createCell(5);
            cell.setCellValue(loanInfo.getTrailer().getBrand());
            cell.setCellStyle(styleContent);

            cell = row.createCell(6);
            cell.setCellValue(loanInfo.getTrailer().getLicensePlate());
            cell.setCellStyle(styleContent);

            cell = row.createCell(7);
            cell.setCellValue(loanInfo.getTrailer().getEngineNumber());
            cell.setCellStyle(styleContent);

            cell = row.createCell(8);
            cell.setCellValue(DateUtils.YYYYlMMlDD.format(loanInfo.getTrailer().getRegistrationDate()));
            cell.setCellStyle(styleContent);
        }

        rowNum = rowNum + 1;
        row = sheet.createRow(rowNum);
        cell = row.createCell(1);
        cell.setCellValue("发动机号");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(2);
        cell.setCellValue(loanInfo.getVehicle().getEngineNumber());
        cell.setCellStyle(styleContent);

        cell = row.createCell(3);
        cell.setCellValue("贷款额");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(4);
        cell.setCellValue(loanInfo.getLoanAmount());
        cell.setCellStyle(styleContent);

        cell = row.createCell(5);
        cell.setCellValue("贷款期限");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(6);
        cell.setCellValue(loanInfo.getLoansNum());
        cell.setCellStyle(styleContent);

        cell = row.createCell(7);
        cell.setCellValue("放款时间");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(8);
        cell.setCellValue(DateUtils.YYYYlMMlDD.format(loanInfo.getLoanDate()));
        cell.setCellStyle(styleContent);


        rowNum = rowNum + 1;
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("期数");
        cell.setCellStyle(styleHeader1);

        cell = row.createCell(1);
        cell.setCellValue("应还时间");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(2);
        cell.setCellValue("应还月款");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(3);
        cell.setCellValue("实收时间");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(4);
        cell.setCellValue("实收金额");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(5);
        cell.setCellValue("逾期");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(6);
        cell.setCellValue("余额");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(7);
        cell.setCellValue("收款人");
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(8);
        cell.setCellValue("备注");
        cell.setCellStyle(styleHeader2);

        int i = 1;
        double left = loanInfo.getTotalRepayment();
        double receipts = 0d;
        for (LoanRecord loanRecord : loanInfo.getLoanRecords()) {
            rowNum = rowNum + 1;
            row = sheet.createRow(rowNum);
            cell = row.createCell(0);
            cell.setCellValue(i++);
            cell.setCellStyle(styleContent);

            cell = row.createCell(1);
            cell.setCellValue(DateUtils.YYYYlMMlDD.format(loanRecord.getExpectDate()));
            cell.setCellStyle(styleContent);

            cell = row.createCell(2);
            cell.setCellValue(loanRecord.getExpectMoney());
            cell.setCellStyle(styleContent);

            if(!loanRecord.isCompleted()){
                continue;
            }

            cell = row.createCell(3);
            StringBuilder date1 = new StringBuilder();
            StringBuilder pence1 = new StringBuilder();
            Set<String> names = new TreeSet<>();
            List<SubLoanRecord> subLoanRecords = subLoanRecordRepository.findAllByLoanRecordAndActiveTrue(loanRecord);
            for(SubLoanRecord subLoanRecord: subLoanRecords){
                date1.append(DateUtils.YYYYlMMlDD.format(subLoanRecord.getReceiptDate()));
                date1.append("\n");
                pence1.append(subLoanRecord.getReceipts());
                pence1.append("\n");
                names.add(subLoanRecord.getPayee().getUserName());
                left = MoneyCalculator.subtract(left, subLoanRecord.getReceipts());
                receipts = MoneyCalculator.add(receipts, subLoanRecord.getReceipts());
            }
            if(date1.length() > 0){
                cell.setCellValue(date1.substring(0, date1.length()-1));
                cell.setCellStyle(styleContent);
                cell = row.createCell(4);
                cell.setCellValue(pence1.substring(0, pence1.length() - 1));
                cell.setCellStyle(styleContent);
            } else {
                cell.setCellValue(DateUtils.YYYYlMMlDD.format(loanRecord.getActualDate()));
                cell.setCellStyle(styleContent);
                cell = row.createCell(4);
                cell.setCellValue(loanRecord.getExpectMoney());
                cell.setCellStyle(styleContent);
            }


            cell = row.createCell(5);
            if(loanRecord.getOverdueDays() >= 0){
                cell.setCellValue("未逾期");
            } else {
                cell.setCellValue(Math.abs(loanRecord.getOverdueDays()) + "天");
            }
            cell.setCellStyle(styleContent);


            cell = row.createCell(6);
            cell.setCellValue(left);
            cell.setCellStyle(styleContent);

            cell = row.createCell(7);
            cell.setCellValue(StringUtils.join(names.toArray(), ","));
            cell.setCellStyle(styleContent);
        }

        rowNum = rowNum + 1;
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("总额");
        cell.setCellStyle(styleHeader1);
        CellRangeAddress cellRangeAddressRow20 = new CellRangeAddress(rowNum, rowNum, 0, 1);
        sheet.addMergedRegion(cellRangeAddressRow20);

        cell = row.createCell(2);
        cell.setCellValue(loanInfo.getTotalRepayment());
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(4);
        cell.setCellValue(receipts);
        cell.setCellStyle(styleHeader2);

        cell = row.createCell(6);
        cell.setCellValue(left);
        cell.setCellStyle(styleHeader2);

        return workbook;
    }

    /***
     * 创建表头
     * @param workbook
     * @param sheet
     */
    private void createMonthReportTitle(HSSFWorkbook workbook, HSSFSheet sheet) {
        HSSFRow row = sheet.createRow(0);

        //设置为居中加粗
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);

        sheet.setColumnWidth(1, 17 * 256);
        sheet.setColumnWidth(2, 17 * 256);
        sheet.setColumnWidth(3, 17 * 256);
        sheet.setColumnWidth(4, 17 * 256);
        sheet.setColumnWidth(5, 17 * 256);
        sheet.setColumnWidth(6, 17 * 256);
        sheet.setColumnWidth(7, 17 * 256);
        sheet.setColumnWidth(8, 17 * 256);

        HSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("序号");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("档案号");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("姓名");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("上月累计收入");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("本期应收");
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue("本期实收");
        cell.setCellStyle(style);

        cell = row.createCell(6);
        cell.setCellValue("差额");
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue("累计差额");
        cell.setCellStyle(style);

        cell = row.createCell(8);
        cell.setCellValue("本月累计收入");
        cell.setCellStyle(style);
    }

    /***
     * 创建表头
     * @param workbook
     * @param sheet
     */
    private void createLoanInfoTitle(HSSFWorkbook workbook, HSSFSheet sheet) {
        HSSFRow row = sheet.createRow(0);

        //设置为居中加粗
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);

        HSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("序号");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("档案号");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("客户姓名");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("联系电话");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("地址");
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue("车型");
        cell.setCellStyle(style);

        cell = row.createCell(6);
        cell.setCellValue("车牌号");
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue("上户日期");
        cell.setCellStyle(style);

        cell = row.createCell(8);
        cell.setCellValue("贷款额");
        cell.setCellStyle(style);

        cell = row.createCell(9);
        cell.setCellValue("贷款期数");
        cell.setCellStyle(style);

        cell = row.createCell(10);
        cell.setCellValue("已还期数");
        cell.setCellStyle(style);

        cell = row.createCell(11);
        cell.setCellValue("已还总额");
        cell.setCellStyle(style);

        cell = row.createCell(12);
        cell.setCellValue("还款时间");
        cell.setCellStyle(style);

        cell = row.createCell(13);
        cell.setCellValue("每月还款金额");
        cell.setCellStyle(style);

        cell = row.createCell(14);
        cell.setCellValue("欠款月份");
        cell.setCellStyle(style);

        cell = row.createCell(15);
        cell.setCellValue("总欠款金额");
        cell.setCellStyle(style);

        cell = row.createCell(16);
        cell.setCellValue("备注");
        cell.setCellStyle(style);
    }
}
