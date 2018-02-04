/**
 * Created by Xinlin on 2017/19/8.
 */
(function () {
    'use strict';

    systemLog.filter('translateModules', function () {
            return function (modules) {
                switch (modules){
                    case 'person':
                        return '人员';
                    case 'bankCard':
                        return '银行卡';
                    case 'user':
                        return '用户';
                    case 'role':
                        return '角色';
                    case 'vehicle':
                        return '车辆';
                    case 'loanInfo':
                        return '贷款档案';
                    case 'beidouBranch':
                        return '分部';
                    case 'beidouRecord':
                        return '北斗档案';
                    case 'beidouRenewal':
                        return '北斗续费';
                    case 'beidouRepair':
                        return '北斗维修';
                    case 'phoneInfo':
                        return '电话';
                    case 'resource':
                        return '权限';
                    case 'subLoanRecord':
                        return '贷款还款';
                    case 'insurance':
                        return '保险';
                    // default:
                    //     return '未知模块';
                }
            };
        });
})();