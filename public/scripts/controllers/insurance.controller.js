/**
 * Created by Xinlin on 2017/12/8.
 */
(function () {
    'use strict';

    insurance.controller('insuranceController', ['$scope', '$mdDialog', '$http', '$mdToast', '$location', 'auth',
        function ($scope, $mdDialog, $http, $mdToast, $location, auth) {

            $scope.currentButtons = {};
            auth.checkPermissions($location.path(), function (currentButtons) {
                $scope.currentButtons = currentButtons;
            });

            $scope.items = [];
            $scope.selected = [];

            $scope.facet = {};
            $scope.detains = [{detain: '', name: '全部'}, {detain: 0, name: '未扣押'}, {detain: 1, name: '已扣押'}];

            $scope.paging = {
                total: 0,
                current: 1,
                onPageChanged: findAllInsurance
            };

            function findAllInsurance() {
                var url = '/insurance/' + $scope.paging.current + '/' + PAGESIZE;
                url += '?jsonFilter=' + encodeURIComponent(JSON.stringify($scope.facet));
                var req = {
                    method: 'GET',
                    url: url
                };

                $http(req).then(function (responseData) {
                    $scope.items = responseData.data.content;
                    $scope.paging.total = responseData.data.totalPages;
                    $scope.paging.totalElements = responseData.data.totalElements;
                    $scope.selected = [];
                });
            }

            $scope.showNewInsurance = function (ev) {
                $mdDialog.show({
                    controller: 'newInsuranceController',
                    templateUrl: 'views/new.insurance.html',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: false,
                    locals: {
                        insurance: {}
                    }
                }).then(function (answer) {
                    if ('success' == answer) {
                        findAllInsurance();
                    }
                }, function () {
                });
            };

            $scope.showEditInsurance = function (ev) {

                if ($scope.selected.length != 1) {
                    var editMessage = '';
                    if ($scope.selected.length == 0) {
                        editMessage = '请选择一项修改!';
                    }
                    if ($scope.selected.length > 1) {
                        editMessage = '只能选择一项!';
                    }

                    $mdToast.show(
                        $mdToast.simple()
                            .textContent(editMessage)
                            .position('top right')
                            .hideDelay(2000)
                    );
                    return;
                }

                $mdDialog.show({
                    controller: 'newInsuranceController',
                    templateUrl: 'views/new.insurance.html',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: false,
                    locals: {
                        insurance: $scope.selected[0]
                    }
                }).then(function (answer) {
                    if ('success' == answer) {
                        findAllInsurance();
                    }
                }, function () {
                });
            };

            $scope.removeInsurances = function (ev) {
                if ($scope.selected.length == 0) {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('请先选择要删除的数据!')
                            .position('top right')
                            .hideDelay(2000)
                    );
                } else {

                    var names = '';
                    angular.forEach($scope.selected, function (insurance) {
                        names += insurance.insuranceNum + ', ';
                    });

                    var confirm = $mdDialog.confirm()
                        .title('确定要删除已选择的数据吗?')
                        .textContent(names.substr(0, names.length - 2))
                        .ariaLabel('remove peron')
                        .targetEvent(ev)
                        .ok('删除')
                        .cancel('取消');

                    $mdDialog.show(confirm).then(function () {
                        removeInsurance();
                    }, function () {
                    });
                }
            };

            function removeInsurance() {
                $http.post('/insurance/delete', $scope.selected).then(function () {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('删除成功!')
                            .position('top right')
                            .hideDelay(2000)
                    );
                    findAllInsurance();
                });
            }


            $scope.toggle = function (item, list) {
                var idx = list.indexOf(item);
                if (idx > -1) {
                    list.splice(idx, 1);
                }
                else {
                    list.push(item);
                }
            };

            $scope.exists = function (item, list) {
                return list.indexOf(item) > -1;
            };

            $scope.isIndeterminate = function () {
                return ($scope.selected.length !== 0 &&
                    $scope.selected.length !== $scope.items.length);
            };

            $scope.isChecked = function () {
                return $scope.selected.length === $scope.items.length;
            };

            $scope.toggleAll = function () {
                if ($scope.selected.length === $scope.items.length) {
                    $scope.selected = [];
                } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
                    $scope.selected = $scope.items.slice(0);
                }
            };

            $scope.searchInsurances = findAllInsurance;

        }])
        .controller('newInsuranceController', ['$scope', '$mdDialog', '$http', '$timeout', '$q', 'insurance',
            function ($scope, $mdDialog, $http, $timeout, $q, insurance) {
                $scope.insurance = insurance ? angular.copy(insurance) : {};

                if(angular.isDefined($scope.insurance.startInsuranceTime)){
                    $scope.insurance.startInsuranceTime = moment($scope.insurance.startInsuranceTime).toDate();
                    $scope.insurance.endInsuranceTime = moment($scope.insurance.endInsuranceTime).toDate();
                }

                $scope.vehicles = [];
                $scope.searchVehicle = null;
                $scope.querySearchVehicle = querySearchVehicle;

                function findAllVehicle() {
                    var url = '/vehicle/1/' + 10000000;
                    var req = {
                        method: 'GET',
                        url: url
                    };

                    $http(req).then(function (responseData) {
                        $scope.vehicles = responseData.data.content;
                    });
                }

                $scope.$watch('insurance.vehicle', function(){
                    if($scope.insurance.vehicle){
                        if(angular.isDefined($scope.insurance.vehicle.reviewDate)){
                            $scope.insurance.vehicle.reviewDate = moment($scope.insurance.vehicle.reviewDate).toDate();
                        }
                    }
                });

                findAllVehicle();

                function querySearchVehicle(query) {
                    var results = query ? $scope.vehicles.filter(createFilterForVehicle(query)) : $scope.vehicles;
                    var deferred = $q.defer();
                    $timeout(function () {
                        deferred.resolve(results);
                    }, Math.random() * 1000, false);
                    return deferred.promise;
                }

                function createFilterForVehicle(query) {
                    return function filterFn(vehicle) {
                        return (vehicle.licensePlate.indexOf(query) >= 0);
                    };
                }

                $scope.addNewVehicle = function (ev) {
                    $mdDialog.show({
                        multiple: true,
                        controller: 'newVehicleController',
                        templateUrl: 'views/new.vehicle.html',
                        parent: angular.element(document.body),
                        targetEvent: ev,
                        clickOutsideToClose: false,
                        locals: {
                            vehicle: {}
                        }
                    }).then(function (answer) {
                        if ('success' == answer) {
                            findAllVehicle();
                        }
                    }, function () {
                    });
                };

                $scope.saveInsurance = function () {
                    var req = {
                        method: 'POST',
                        url: '/insurance/save',
                        data: $scope.insurance
                    };
                    $http(req).then(function () {
                        $mdDialog.hide('success');
                    });

                };



                $scope.cancel = function () {
                    $mdDialog.cancel();
                };


                $scope.openDatePopup = function(popup) {
                    $scope.datePopup[popup] = true;
                };

                $scope.datePopup = {
                    startInsuranceTime: false,
                    endInsuranceTime: false,
                    reviewDate:false
                };

            }]);
})();
