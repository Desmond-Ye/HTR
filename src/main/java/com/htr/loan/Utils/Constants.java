package com.htr.loan.Utils;

/**
 * 常量定义工具类
 * Created by dsp on 2015/8/11.
 */
public interface Constants {
    public static final String RESPONSE_CODE="code";
    public static final String RESPONSE_MSG="message";
    public static final String CODE_SUCCESS="200";
    public static final String CODE_FAIL="400";
    public static final String CODE_ERROR="500";
    public static final String MSG_DELETE_FAIL="删除失败!";
    public static final String MSG_DELETE_SUCCESS="删除成功!";



    public static final String SESSION_USER_KEY="loginUser";


    public static final String INIT_ROLE_INSTALLER="安装员";

    /**
     * 资源类型
     */
    public static final String RESOURCE_TYPE_MENU="menu";
    public static final String RESOURCE_TYPE_BUTTON="button";


    /**
     * 默认页面显示条数
     */
    public final static Integer DEFAULT_PAGE_SIGE = 15;

    public final static Integer DEFAULT_PASSWORD_MAXLENGTH = 18;

    public static final String SEARCH_PREFIX = "search_";

    public static final Boolean RECORD_EXIST = true;

    /**
     * 系统模块名称
     */
    public static final String MODULE_PERSON = "person";
    public static final String MODULE_BANKCARD = "bankCard";
    public static final String MODULE_VEHICLE = "vehicle";
    public static final String MODULE_LOANINFO = "loanInfo";
    public static final String MODULE_BEIDOURECORD = "beidouRecord";
    public static final String MODULE_BEIDOUBRANCH = "beidouBranch";
    public static final String MODULE_BEIDOURENEWAL = "beidouRenewal";
    public static final String MODULE_BEIDOUREPAIR = "beidouRepair";
    public static final String MODULE_SUBLOANRECORD = "subLoanRecord";
    public static final String MODULE_LOANRECORD = "loanRecord";
    public static final String MODULE_USER = "user";
    public static final String MODULE_RESOURCE = "resource";
    public static final String MODULE_DEPARTMENT = "department";
    public static final String MODULE_ROLE = "role";

    /**
     * 操作类型
     */
    public static final String OPERATYPE_ADD = "新建";
    public static final String OPERATYPE_UPDATE = "修改";
    public static final String OPERATYPE_DELETE = "删除";
    public static final String OPERATYPE_STOP = "禁用";
    public static final String OPERATYPE_ACTIVE = "启用";
    public static final String OPERATYPE_DETAIN = "扣车";
    public static final String OPERATYPE_NOTDETAIN = "解扣";
    public static final String OPERATYPE_REPAYMENT = "还款";
    public static final String OPERATYPE_BACKREPAYMENT = "撤销还款";
    public static final String OPERATYPE_BACKRENEWAL = "撤销续期";
    public static final String OPERATYPE_BACKREPAIR = "撤销维修";
    public static final String OPERATYPE_CHANGEPWD = "修改密码";

    /**
     * 日期数据格式化
     */
    public static final String[] POSSIBLE_DATE_FORMATS = {
            "MM/dd/yyyy HH:mm:ss a",
            "EEE, dd MMM yyyy HH:mm:ss zzz", // RFC_822
            "EEE, dd MMM yyyy HH:mm:ss z", // RFC_822
            "EEE, dd MMM yyyy HH:mm zzzz",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:sszzzz",
            "yyyy-MM-dd'T'HH:mm:ss z",
            "yyyy-MM-dd'T'HH:mm:ssz", // ISO_8601
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HHmmss.SSSz",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd", "yyyy/MM/dd", "yyyy.MM.dd", "yyyy'年'MM'月'dd'日'",
            "EEE,dd MMM yyyy HH:mm:ss zzz", // 容错
            "EEE, dd MMM yyyy HH:mm:ss", // RFC_822
            "dd MMM yyyy HH:mm:ss zzz", // 容错
            "dd MM yyyy HH:mm:ss zzz", // 容错
            "EEE, dd MM yyyy HH:mm:ss", // RFC_822
            "dd MM yyyy HH:mm:ss", // 容错
            "EEE MMM dd HH:mm:ss zzz yyyy" // bokee 的时间格式 Tue Mar 28
            // 02:25:45 CST 2006
    };
}
