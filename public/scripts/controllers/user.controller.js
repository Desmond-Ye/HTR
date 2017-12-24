/**
 * Created by Xinlin on 2017/12/8.
 */
(function () {
    'use strict';

    user.controller('userController', ['$scope', '$mdDialog', '$http', '$mdToast', '$location', 'auth',
        function ($scope, $mdDialog, $http, $mdToast, $location, auth) {

            $scope.currentButtons = {};
            auth.checkPermissions($location.path(), function (currentButtons) {
                $scope.currentButtons = currentButtons;
            });

            $scope.items = [];
            $scope.selected = [];

            function findAllUser() {
                var url = '/user/0';
                var req = {
                    method: 'GET',
                    url: url
                };

                $http(req).then(function (responseData) {
                    $scope.items = responseData.data;
                    $scope.selected = [];
                });
            }

            $scope.showNewUser = function (ev) {
                $mdDialog.show({
                    controller: 'newUserController',
                    templateUrl: 'views/new.user.html',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: false,
                    locals: {
                        user: {}
                    }
                }).then(function (answer) {
                    if ('success' == answer) {
                        findAllUser();
                    }
                }, function () {
                });
            };

            $scope.showEditUser = function (ev) {

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
                    controller: 'newUserController',
                    templateUrl: 'views/new.user.html',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: false,
                    locals: {
                        user: $scope.selected[0]
                    }
                }).then(function (answer) {
                    if ('success' == answer) {
                        findAllUser();
                    }
                }, function () {
                });
            };

            $scope.removeUsers = function (ev) {
                if ($scope.selected.length == 0) {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('请先选择要禁用的记录!')
                            .position('top right')
                            .hideDelay(2000)
                    );
                } else {

                    var names = '';
                    angular.forEach($scope.selected, function (user) {
                        names += user.userAccount + ', ';
                    });

                    var confirm = $mdDialog.confirm()
                        .title('确定要禁用已选择的记录吗?')
                        .textContent(names.substr(0, names.length - 2))
                        .ariaLabel('remove peron')
                        .targetEvent(ev)
                        .ok('禁用')
                        .cancel('取消');

                    $mdDialog.show(confirm).then(function () {
                        removeUser();
                    }, function () {
                    });
                }
            };

            function removeUser() {
                $http.post('/user/stop', $scope.selected).then(function () {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('禁用成功!')
                            .position('top right')
                            .hideDelay(2000)
                    );
                    findAllUser();
                });
            }

            $scope.activeUsers = function (ev) {
                if ($scope.selected.length == 0) {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('请先选择要启用的的记录!')
                            .position('top right')
                            .hideDelay(2000)
                    );
                } else {

                    var names = '';
                    angular.forEach($scope.selected, function (user) {
                        names += user.userAccount + ', ';
                    });

                    var confirm = $mdDialog.confirm()
                        .title('确定要启用已选择的记录吗?')
                        .textContent(names.substr(0, names.length - 2))
                        .ariaLabel('remove peron')
                        .targetEvent(ev)
                        .ok('启用')
                        .cancel('取消');

                    $mdDialog.show(confirm).then(function () {
                        activeUser();
                    }, function () {
                    });
                }
            };

            function activeUser() {
                $http.post('/user/active', $scope.selected).then(function () {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('启用成功!')
                            .position('top right')
                            .hideDelay(2000)
                    );
                    findAllUser();
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

            $scope.searchUsers = findAllUser;
            findAllUser();

        }]);

    user.controller('newUserController', ['$scope', '$mdDialog', '$http', 'user', function ($scope, $mdDialog, $http, user) {

        $scope.user = user ? angular.copy(user) : {};

        $scope.saveUser = function () {
            var req = {
                method: 'POST',
                url: '/user/save',
                data: $scope.user
            };
            $http(req).then(function () {
                $mdDialog.hide('success');
            });

        };

        $scope.roles = [];
        $scope.loadAllRoles = function () {
            var url = '/role/1';
            var req = {
                method: 'GET',
                url: url
            };

            $http(req).then(function (responseData) {
                $scope.roles = responseData.data;
            });
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        };
    }]);

    user.controller('changePasswordController', ['$scope', '$mdDialog', '$http', function ($scope, $mdDialog, $http) {

        $scope.password = {
            originalPassword: '',
            newPassword: '',
            repeatPassword: ''
        };

        $scope.savePassword = function () {
            var req = {
                method: 'POST',
                url: '/user/changePassword',
                data: $scope.password
            };
            $http(req).then(function (responseData) {
                if (responseData.data.code === '200') {
                    $mdDialog.hide('success');
                } else {
                    alert(responseData.data.message);
                }
            });

        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        };
    }]);
})();
