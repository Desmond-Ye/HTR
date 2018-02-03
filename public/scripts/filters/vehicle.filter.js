/**
 * Created by Xinlin on 2017/19/8.
 */
(function () {
    'use strict';

    vehicle.filter('isDetain', function () {
            return function (detain) {
                if(detain){
                    return "已扣押";
                } else {
                    return "未扣押";
                }
            };
        });

    vehicle.filter('isTrailer', function () {
        return function (trailer) {
            if(trailer){
                return "挂车";
            } else {
                return "主车";
            }
        };
    });
})();