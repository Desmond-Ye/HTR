(function () {
    'use strict';

// Declare app level module which depends on views, and components
    app.config(['$routeProvider', '$mdThemingProvider', '$locationProvider', '$mdIconProvider', '$httpProvider', 'IdleProvider',
        function ($routeProvider, $mdThemingProvider, $locationProvider, $mdIconProvider, $httpProvider, IdleProvider) {

            $routeProvider.otherwise({redirectTo: '/test'});
            $routeProvider.when('/test', {
                templateUrl: 'views/test.html',
                controller: 'testController'
            }).when('/login', {
                templateUrl: 'views/login.html',
                controller: 'loginController'
            }).when('/sys/roleList', {
                templateUrl: 'views/role.html',
                controller: 'roleController'
            }).when('/sys/userList', {
                templateUrl: 'views/user.html',
                controller: 'userController'
            }).when('/loan/vehicle', {
                templateUrl: 'views/vehicle.html',
                controller: 'vehicleController'
            }).when('/loan/person', {
                templateUrl: 'views/person.html',
                controller: 'personController'
            }).when('/loan/insurance', {
                templateUrl: 'views/insurance.html',
                controller: 'insuranceController'
            }).when('/loan/bankCard', {
                templateUrl: 'views/bankCard.html',
                controller: 'bankCardController'
            }).when('/sys/systemLog', {
                templateUrl: 'views/systemLog.html',
                controller: 'systemLogController'
            }).when('/loan/loanInfo', {
                templateUrl: 'views/loanInfo.html',
                controller: 'loanInfoController'
            }).when('/loan/subLoanRecord', {
                templateUrl: 'views/subLoanRecord.html',
                controller: 'subLoanRecordController'
            }).when('/beidou/beidouRecord', {
                templateUrl: 'views/beidouRecord.html',
                controller: 'beidouRecordController'
            }).when('/beidou/beidouBranch', {
                templateUrl: 'views/beidouBranch.html',
                controller: 'beidouBranchController'
            }).when('/beidou/beidouRenewal', {
                templateUrl: 'views/beidouRenewal.html',
                controller: 'beidouRenewalController'
            }).when('/beidou/beidouRepair', {
                templateUrl: 'views/beidouRepair.html',
                controller: 'beidouRepairController'
            });


            $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

            $mdThemingProvider.theme('default')
                .primaryPalette('blue')
                .accentPalette('green')
                .warnPalette('red')
                .backgroundPalette('grey');

            $mdThemingProvider.alwaysWatchTheme(true);
            $mdIconProvider.icon('md-toggle-arrow', 'img/icons/toggle-arrow.svg', 48);

            IdleProvider.idle(1801);
        }]);

    app.run(['auth', '$rootScope', '$location', 'Idle', function (auth, $rootScope, $location, Idle) {
        function enter() {
            if ($location.path() != auth.loginPath) {
                auth.path = $location.path();
                if (!auth.authenticated) {
                    $location.path(auth.loginPath);
                }
            }
        }

        // Guard route changes and switch to login page if unauthenticated
        $rootScope.$on('$routeChangeStart', function () {
            enter();
        });

        // Initialize auth module with the home page and login/logout path
        // respectively
        auth.init('/', '/login', '/logout');

        Idle.watch();
    }]);

})();