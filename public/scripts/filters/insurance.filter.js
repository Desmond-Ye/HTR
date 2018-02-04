/**
 * Created by Xinlin on 2017/19/8.
 */
(function () {
    'use strict';

    insuranceRenewal.filter('convertInsuranceName', function () {
        return function (insuranceRenewal) {
            if (!insuranceRenewal) return;
            if(insuranceRenewal.insuranceName === insuranceRenewal.insurance.insuranceName) {
                return insuranceRenewal.insuranceName;
            } else {
                return insuranceRenewal.insuranceName + "-->" + insuranceRenewal.insurance.insuranceName;
            }
        };
    });

    insuranceRenewal.filter('convertInsuranceCompany', function () {
        return function (insuranceRenewal) {
            if (!insuranceRenewal) return;
            if(insuranceRenewal.insuranceCompany === insuranceRenewal.insurance.insuranceCompany) {
                return insuranceRenewal.insuranceCompany;
            } else {
                return insuranceRenewal.insuranceCompany + "-->" + insuranceRenewal.insurance.insuranceCompany;
            }
        };
    });

    insuranceRenewal.filter('convertInsuranceNum', function () {
        return function (insuranceRenewal) {
            if (!insuranceRenewal) return;
            if(insuranceRenewal.insuranceNum === insuranceRenewal.insurance.insuranceNum) {
                return insuranceRenewal.insuranceNum;
            } else {
                return insuranceRenewal.insuranceNum + "-->" + insuranceRenewal.insurance.insuranceNum;
            }
        };
    });

    insuranceRenewal.filter('convertInsuranceFee', function () {
        return function (insuranceRenewal) {
            if (!insuranceRenewal) return;
            if(insuranceRenewal.insuranceFee === insuranceRenewal.insurance.insuranceFee) {
                return insuranceRenewal.insuranceFee;
            } else {
                return "¥"+insuranceRenewal.insuranceFee + "-->¥" + insuranceRenewal.insurance.insuranceFee;
            }
        };
    });

})();