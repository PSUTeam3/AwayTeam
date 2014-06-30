(function(app) {

    app.config(["$stateProvider", '$urlRouterProvider', '$httpProvider', function ($stateProvider, $urlRouterProvider, $httpProvider) {
        $httpProvider.defaults.transformRequest = function(data){
            if (data === undefined) {
              return data;
            }
            return $.param(data);
        };
        $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8';
        $urlRouterProvider.otherwise('/home');
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
      

    });

    app.controller('AppController', ['$scope', '$state', '$stateParams', '$http', '$timeout', 'loginService',
        function ($scope, $state, $stateParams, $http, $timeout, loginService) {
             // Expose $state and $stateParams to the <body> tag
              $scope.$state = $state;
              $scope.$stateParams = $stateParams;

              // loginService exposed and a new Object containing login user/pwd
              $scope.ls = loginService;
              $scope.login = {
                working: false,
                wrong: false
              };
              $scope.login = function () {
                // setup promise, and 'working' flag
                var userLogin = {loginId:$scope.login.loginId, password:$scope.login.password};
                var loginPromise = $http({
                  url: "http://api.awayteam.redshrt.com/user/AuthenticatePassword",
                  method: "POST", 
                  data: userLogin
                });

                $scope.login.working = true;
                $scope.login.wrong = false;

                loginService.loginUser(loginPromise, $scope.login.loginId);
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
              
    }]);

}(angular.module("AwayTeam", [
    'AwayTeam.home',
    'AwayTeam.about',
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
