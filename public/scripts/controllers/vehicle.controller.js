/**
 * Created by Xinlin on 2017/12/8.
 */
(function () {
    'use strict';

    vehicle.controller('vehicleController', ['$scope', '$mdDialog', '$http', '$mdToast', '$location', 'auth',
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
                onPageChanged: findAllVehicle
            };

            function findAllVehicle() {
                var url = '/vehicle/' + $scope.paging.current + '/' + PAGESIZE;
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

            $scope.showNewVehicle = function (ev) {
                $mdDialog.show({
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

            $scope.showEditVehicle = function (ev) {

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
                    controller: 'newVehicleController',
                    templateUrl: 'views/new.vehicle.html',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: false,
                    locals: {
                        vehicle: $scope.selected[0]
                    }
                }).then(function (answer) {
                    if ('success' == answer) {
                        findAllVehicle();
                    }
                }, function () {
                });
            };

            $scope.removeVehicles = function (ev) {
                if ($scope.selected.length == 0) {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('请先选择要删除的数据!')
                            .position('top right')
                            .hideDelay(2000)
                    );
                } else {

                    var names = '';
                    angular.forEach($scope.selected, function (vehicle) {
                        names += vehicle.licensePlate + ', ';
                    });

                    var confirm = $mdDialog.confirm()
                        .title('确定要删除已选择的数据吗?')
                        .textContent(names.substr(0, names.length - 2))
                        .ariaLabel('remove peron')
                        .targetEvent(ev)
                        .ok('删除')
                        .cancel('取消');

                    $mdDialog.show(confirm).then(function () {
                        removeVehicle();
                    }, function () {
                    });
                }
            };

            function removeVehicle() {
                $http.post('/vehicle/delete', $scope.selected).then(function () {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('删除成功!')
                            .position('top right')
                            .hideDelay(2000)
                    );
                    findAllVehicle();
                });
            }

            $scope.detainVehicles = function (ev) {
                if ($scope.selected.length == 0) {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('请先选择要扣留的车辆数据!')
                            .position('top right')
                            .hideDelay(2000)
                    );
                } else {

                    var names = '';
                    angular.forEach($scope.selected, function (vehicle) {
                        names += vehicle.licensePlate + ', ';
                    });

                    var confirm = $mdDialog.confirm()
                        .title('确定要扣留已选车辆吗?')
                        .textContent(names.substr(0, names.length - 2))
                        .ariaLabel('remove peron')
                        .targetEvent(ev)
                        .ok('扣押')
                        .cancel('取消');

                    $mdDialog.show(confirm).then(function () {
                        detainVehicle();
                    }, function () {
                    });
                }
            };

            function detainVehicle() {
                $http.post('/vehicle/detain', $scope.selected).then(function () {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('扣车成功!')
                            .position('top right')
                            .hideDelay(2000)
                    );
                    findAllVehicle();
                });
            }

            $scope.unDetainVehicles = function (ev) {
                if ($scope.selected.length == 0) {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('请先选择要解扣的车辆数据!')
                            .position('top right')
                            .hideDelay(2000)
                    );
                } else {

                    var names = '';
                    angular.forEach($scope.selected, function (vehicle) {
                        names += vehicle.licensePlate + ', ';
                    });

                    var confirm = $mdDialog.confirm()
                        .title('确定要解扣已选车辆吗?')
                        .textContent(names.substr(0, names.length - 2))
                        .ariaLabel('remove peron')
                        .targetEvent(ev)
                        .ok('解扣')
                        .cancel('取消');

                    $mdDialog.show(confirm).then(function () {
                        unDetainVehicle();
                    }, function () {
                    });
                }
            };

            function unDetainVehicle() {
                $http.post('/vehicle/unDetain', $scope.selected).then(function () {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('解扣车辆成功!')
                            .position('top right')
                            .hideDelay(2000)
                    );
                    findAllVehicle();
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

            $scope.searchVehicles = findAllVehicle;

            $scope.openDatePopup = function(popup) {
                $scope.datePopup[popup] = true;
            };

            $scope.datePopup = {
                GTE_reviewDate: false,
                LTE_reviewDate: false
            };

        }])
        .controller('newVehicleController', ['$scope', '$mdDialog', '$http', '$timeout', '$q', 'vehicle',
            function ($scope, $mdDialog, $http, $timeout, $q, vehicle) {
                $scope.vehicle = vehicle ? angular.copy(vehicle) : {};

                if(angular.isDefined($scope.vehicle.registrationDate)){
                    $scope.vehicle.registrationDate = moment($scope.vehicle.registrationDate).toDate();
                    $scope.vehicle.reviewDate = moment($scope.vehicle.reviewDate).toDate();
                }

                $scope.persons = [];
                $scope.searchText = null;
                $scope.querySearch = querySearch;

                function findAllPerson() {
                    var url = '/person/1/' + 10000000;
                    url += '?jsonFilter=' + encodeURIComponent(JSON.stringify({'search_EQ_surety': '0'}));
                    var req = {
                        method: 'GET',
                        url: url
                    };

                    $http(req).then(function (responseData) {
                        $scope.persons = responseData.data.content;
                    });
                }

                findAllPerson();
                function querySearch(query) {
                    var results = query ? $scope.persons.filter(createFilterFor(query)) : $scope.persons;
                    var deferred = $q.defer();
                    $timeout(function () {
                        deferred.resolve(results);
                    }, Math.random() * 1000, false);
                    return deferred.promise;
                }

                function createFilterFor(query) {
                    return function filterFn(person) {
                        return (person.name.indexOf(query) >= 0);
                    };
                }

                $scope.saveVehicle = function () {
                    var req = {
                        method: 'POST',
                        url: '/vehicle/save',
                        data: $scope.vehicle
                    };
                    $http(req).then(function () {
                        $mdDialog.hide('success');
                    });

                };

                $scope.addNewPerson = function (ev) {
                    $mdDialog.show({
                        multiple: true,
                        controller: 'newPersonController',
                        templateUrl: 'views/new.person.html',
                        parent: angular.element(document.body),
                        targetEvent: ev,
                        clickOutsideToClose: false,
                        locals: {
                            person: {}
                        }
                    }).then(function (answer) {
                        if ('success' == answer) {
                            findAllPerson();
                        }
                    }, function () {
                    });
                };

                $scope.cancel = function () {
                    $mdDialog.cancel();
                };


                $scope.openDatePopup = function(popup) {
                    $scope.datePopup[popup] = true;
                };

                $scope.datePopup = {
                    registrationDate: false,
                    reviewDate: false
                };

            }]);
})();
