(function(app) {

    app.config(["$stateProvider", '$urlRouterProvider', '$httpProvider', function ($stateProvider, $urlRouterProvider, $httpProvider) {
        //intercepts all httpRequests
        $httpProvider.defaults.transformRequest = function(data){
            if (data === undefined) {
              return data;
            }
            return $.param(data);
        };
        $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8';
        $urlRouterProvider.otherwise('/home');

        $httpProvider.interceptors.push('identityInjector');
    }]);

    app.run(function ($rootScope) {
        /**
       * $rootScope.doingResolve is a flag useful to display a spinner on changing states.
       * Some states may require remote data so it will take awhile to load.
       */
      var resolveDone = function () { $rootScope.doingResolve = false; };
      $rootScope.doingResolve = false;

      $rootScope.$on('$stateChangeStart', function () {
        $rootScope.doingResolve = true;
      });
      $rootScope.$on('$stateChangeSuccess', resolveDone);
      $rootScope.$on('$stateChangeError', resolveDone);
      $rootScope.$on('$statePermissionError', resolveDone);

      $rootScope.editUserObj = {};
      

    });

    app.controller('AppController', ['$scope', '$state', '$stateParams', '$http', '$timeout', '$modal', '$log', 'loginService',
        function ($scope, $state, $stateParams, $http, $timeout, $modal, $log, loginService) {
             // Expose $state and $stateParams to the <body> tag
              $scope.$state = $state;
              $scope.$stateParams = $stateParams;

              // loginService exposed and a new Object containing login user/pwd
              $scope.ls = loginService;
              $scope.login = {
                working: false,
                wrong: false
              };
              $scope.attemptLogin = function () {
                // setup promise, and 'working' flag
                var userLogin = {loginId:$scope.login.loginId, password:$scope.login.password};
                var rememberCreds = $scope.login.rememberMe;
                var loginPromise = $http({
                  url: "http://api.awayteam.redshrt.com/user/AuthenticatePassword",
                  method: "POST", 
                  data: userLogin
                });

                $scope.login.working = true;
                $scope.login.wrong = false;

                loginService.loginUser(loginPromise, userLogin, rememberCreds);
                loginPromise.error(function () {
                  $scope.login.wrong = true;
                  $timeout(function () { $scope.login.wrong = false; }, 8000);
                });
                loginPromise.finally(function () {
                  $scope.login.working = false;
                });
              };
              $scope.logout = function () {
                loginService.logoutUser(); //TODO need to logout user remotely as well
              };

            $scope.setLoginForm = function(){
                if(localStorage.getItem("awayteamLoginId") != null){
                    $scope.login.loginId = localStorage.getItem("awayteamLoginId");
                    $scope.login.password = localStorage.getItem("awayteamPassword");
                    $scope.login.rememberMe = "true";
                    $scope.attemptLogin();
                }
            };



            $scope.changePWOpen = function () {

                var modalInstance = $modal.open({
                    templateUrl: 'changePassword/changePassword.tpl.html',
                    controller: 'ModalInstanceCtrl'
                });

                modalInstance.result.then(function (selectedItem) {
                }, function () {
                    $log.info('Modal dismissed at: ' + new Date());
                });
            };
              
    }]);

    app.factory('identityInjector', ['$q', '$injector', function($q, $injector){
        var identityInjector = {
            request: function(config) {
                var loginService = $injector.get('loginService');
                if (loginService != null && loginService.userIdentifier != null && config.data != null && loginService.user != null && loginService.isLoggedIn) {
                    var unixTimeStamp                   = Math.round((new Date()).getTime() / 1000);
                    var challenge = unixTimeStamp       + loginService.user.loginId.toLowerCase() + loginService.userIdentifier;
                    var encryptChallenge                = CryptoJS.HmacSHA256(challenge, loginService.userSecret).toString(CryptoJS.enc.Hex);
                    config.data.AWT_AUTH                = loginService.userIdentifier;
                    config.data.AWT_AUTH_CHALLENGE      = encryptChallenge;
                }
                return config;
            }
        };
        return identityInjector;
    }]);

// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $modal service used above.

    app.controller('ModalInstanceCtrl', ['$scope', '$modalInstance', '$http', 'loginService',  function ($scope, $modalInstance, $http, loginService) {
        $scope.changePassword = {};
        $scope.passwordWorking = false;

        $scope.ok = function () {
            $scope.changePassword.loginId = loginService.user.loginId;
            var passwordPromise = $http({
                url: "http://api.awayteam.redshrt.com/user/changepassword",
                method: "POST",
                data: $scope.changePassword
            });

            $scope.passwordWorking = true;

            passwordPromise.success(function(user, status, headers, config) {
                if (user.response === "success") {

                    $scope.passwordWorking = false;
                    $modalInstance.close();
                }
            });
            passwordPromise.error(function () {
                $scope.passwordWorking = false;
                $modalInstance.close();
            });
            passwordPromise.finally(function () {
                $scope.passwordWorking = false;
                $modalInstance.close();
            });

        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);


}(angular.module("AwayTeam", [
    'AwayTeam.home',
    'AwayTeam.about',
    'AwayTeam.editAccount',
    'AwayTeam.error',
    'AwayTeam.register',
    'AwayTeam.router',
    'AwayTeam.validators',
    'loginService',
    'templates-app',
    'templates-common',
    'ui.bootstrap',
    'ui.router.state',
    'ui.router'
])));
