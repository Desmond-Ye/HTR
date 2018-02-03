/**
 * Created by Xinlin on 2017/19/8.
 */
(function () {
    'use strict';

    loanInfo.filter('currentLoanNum', function () {
        return function (LoanNum) {
            if (LoanNum) {
                return '第 ' + LoanNum + ' 期';
            }
        };
    });

    loanInfo.filter('isCompleted', function () {
        return function (completed) {
            if (completed) {
                return "已完结";
            } else {
                return "进行中";
            }
        };
    });

    loanInfo.filter('isOverdue', function () {
        return function (overdue) {
            if (overdue >= 0) {
                return overdue + ' 天';
            } else {
                return '逾期 ' + Math.abs(overdue) + ' 天';
            }
        };
    });

    loanInfo.filter('Overdue', function () {
        return function (loanRecord) {
            if(!loanRecord.completed){
                return '';
            } else {
                if (loanRecord.overdueDays >= 0) {
                    return '未逾期';
                } else {
                    return Math.abs(loanRecord.overdueDays) + ' 天';
                }
            }

        };
    });

    loanInfo.filter('payees', function () {
        return function (subLoanRecords) {
            var listName = [];
            angular.forEach(subLoanRecords, function (subLoanRecord) {
                var isExist = false;
                angular.forEach(listName, function (name) {
                    if(name === subLoanRecord.payee.userName){
                        isExist = true;
                    }
                });
                if (!isExist) {
                    listName.push(subLoanRecord.payee.userName);
                }
            });

            return listName.join("/");
        };
    });

    loanInfo.filter('listLicensePlate', function () {
        return function (loanInfo) {
            if (!loanInfo) return;
            if(loanInfo.trailer){
                return loanInfo.vehicle.licensePlate + "/" + loanInfo.trailer.licensePlate;
            } else {
                return loanInfo.vehicle.licensePlate;
            }
        };
    });

    loanInfo.filter('listSurety', function () {
        return function (loanInfo) {
            if (!loanInfo) return;
            if(loanInfo.secondSurety){
                return loanInfo.surety.name + "/" + loanInfo.secondSurety.name;
            } else {
                return loanInfo.surety.name;
            }
        };
    });
})();