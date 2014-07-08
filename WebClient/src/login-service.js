angular.module('loginService', [])
.provider('loginService', function () {
  var errorState = 'error',
      logoutState = 'home';

  this.$get = function ($rootScope, $http, $q, $state) {

    /**
     * Low-level, private functions.
     */
    var setHeaders = function (token) {
     /* if (!token) {
        delete $http.defaults.headers.common['X-Token'];
        return;
      }
      $http.defaults.headers.common['X-Token'] = token.toString();*/
    };

    var setToken = function (token) {
      /*if (!token) {
        localStorage.removeItem('userToken');
      } else {
        localStorage.setItem('userToken', token);
      }
      setHeaders(token);*/
    };

    var getLoginData = function () {
/*      if (userToken) {
        setHeaders(userToken);
      } else {*/
        wrappedService.userRole = userRoles.admin; //currently setting all users to an admin until we define roles
        wrappedService.isLoggedIn = false;
        wrappedService.doneLoading = true;
     // }
    };

    var managePermissions = function () {
      // Register routing function.
      $rootScope.$on('$stateChangeStart', function (event, to, toParams, from, fromParams) {

        /**
         * $stateChangeStart is a synchronous check to the accessLevels property
         * if it's not set, it will setup a pendingStateChange and will let
         * the grandfather resolve do his job.
         *
         * In short:
         * If accessLevels is still undefined, it let the user change the state.
         * Grandfather.resolve will either let the user in or reject the promise later!
         */
        if (wrappedService.userRole === null) {
          wrappedService.doneLoading = false;
          wrappedService.pendingStateChange = {
            to: to,
            toParams: toParams
          };
          return;
        }

        // if the state has undefined accessLevel, anyone can access it.
        // NOTE: if `wrappedService.userRole === undefined` means the service still doesn't know the user role,
        // we need to rely on grandfather resolve, so we let the stateChange success, for now.
        if (to.accessLevel === undefined || to.accessLevel.bitMask & wrappedService.userRole.bitMask) {
          angular.noop(); // requested state can be transitioned to.
        } else {
          event.preventDefault();
          $rootScope.$emit('$statePermissionError');
          $state.go(errorState, { error: 'unauthorized' }, { location: false, inherit: false });
        }
      });

      /**
       * Gets triggered when a resolve isn't fulfilled
       * NOTE: when the user doesn't have required permissions for a state, this event
       *       it's not triggered.
       *
       * In order to redirect to the desired state, the $http status code gets parsed.
       * If it's an HTTP code (ex: 403), could be prefixed with a string (ex: resolvename403),
       * to handle same status codes for different resolve(s).
       * This is defined inside $state.redirectMap.
       */
      $rootScope.$on('$stateChangeError', function (event, to, toParams, from, fromParams, error) {
        /**
         * This is a very clever way to implement failure redirection.
         * You can use the value of redirectMap, based on the value of the rejection
         * So you can setup DIFFERENT redirections based on different promise errors.
         */
        var errorObj, redirectObj;
        // in case the promise given to resolve function is an $http request
        // the error is a object containing the error and additional informations
        error = (typeof error === 'object') ? error.status.toString() : error;
        // in case of a random 4xx/5xx status code from server, user gets loggedout
        // otherwise it *might* forever loop (look call diagram)
        if (/^[45]\d{2}$/.test(error)) {
          wrappedService.logoutUser();
        }
        /**
         * Generic redirect handling.
         * If a state transition has been prevented and it's not one of the 2 above errors, means it's a
         * custom error in your application.
         *
         * redirectMap should be defined in the $state(s) that can generate transition errors.
         */
        if (angular.isDefined(to.redirectMap) && angular.isDefined(to.redirectMap[error])) {
          if (typeof to.redirectMap[error] === 'string') {
            return $state.go(to.redirectMap[error], { error: error }, { location: false, inherit: false });
          } else if (typeof to.redirectMap[error] === 'object') {
            redirectObj = to.redirectMap[error];
            return $state.go(redirectObj.state, { error: redirectObj.prefix + error }, { location: false, inherit: false });
          }
        }
        return $state.go(errorState, { error: error }, { location: false, inherit: false });
      });
    };

    /**
     * High level, public methods
     */
    var wrappedService = {
      /**
       * Public properties
       */
      userRole: null,
      userIdentifier: null,
      userSecret: null,
      user: {},
      isLoggedIn: null,
      pendingStateChange: null,
      doneLoading: null,

      loginHandler: function (user, status, headers, config) {
        /**
         * Custom logic to manually set userRole goes here
         *
         * Commented example shows an userObj coming with a 'completed'
         * property defining if the user has completed his registration process,
         * validating his/her email or not.
         *
         * EXAMPLE:
         * if (user.hasValidatedEmail) {
         *   wrappedService.userRole = userRoles.registered;
         * } else {
         *   wrappedService.userRole = userRoles.invalidEmail;
         *   $state.go('app.nagscreen');
         * }
         */
        // setup token
       // setToken(user.token); //TODO don't currently have token handling
        // update user
        angular.extend(wrappedService.user, user.message);
        angular.extend($rootScope.editUserObj, user.message);
        // flag true on isLoggedIn
        wrappedService.isLoggedIn = true;
        // update userRole
        wrappedService.userRole = userRoles.admin; //TODO currently setting all users to admin until roles are defined
        return user;
      },
      loginUser: function (httpPromise, login, rememberUser) {
        httpPromise.success(function(user, status, headers, config) {
            if (user.response === "success") {
                wrappedService.userIdentifier = user.userIdentifier;
                wrappedService.userSecret = user.userSecret;

                if(rememberUser === "true"){
                    localStorage.setItem("awayteamLoginId", login.loginId);
                    localStorage.setItem("awayteamPassword", login.password);
                }

                var userPromise = $http({
                    url: "https://api.awayteam.redshrt.com/user/GetUser?loginId=" + login.loginId,
                    method: "GET"
                });

                userPromise.success(wrappedService.loginHandler);
            }
          });
      },
      logoutUser: function (httpPromise) {
        /**
         * De-registers the userToken remotely
         * then clears the loginService as it was on startup
         */
        setToken(null);
        this.userRole = userRoles.public;
        this.userIdentifier = "";
        this.userSecret = "";
        this.user = {};
        $rootScope.editUserObj = {};
        this.isLoggedIn = false;
        localStorage.removeItem("awayteamLoginId");
        localStorage.removeItem("awayteamPassword");
        $state.go(logoutState);
      },
      resolvePendingState: function (httpPromise) {
        var checkUser = $q.defer(),
            self = this,
            pendingState = self.pendingStateChange;

        // When the $http is done, we register the http result into loginHandler, `data` parameter goes into loginService.loginHandler
        httpPromise.success(self.loginHandler);

        httpPromise.then(
          function success(httpObj) {
            self.doneLoading = true;
            // duplicated logic from $stateChangeStart, slightly different, now we surely have the userRole informations.
            if (pendingState.to.accessLevel === undefined || pendingState.to.accessLevel.bitMask & self.userRole.bitMask) {
              checkUser.resolve();
            } else {
              checkUser.reject('unauthorized');
            }
          },
          function reject(httpObj) {
            checkUser.reject(httpObj.status.toString());
          }
        );
        /**
         * I setted up the state change inside the promises success/error,
         * so i can safely assign pendingStateChange back to null.
         */
        self.pendingStateChange = null;
        return checkUser.promise;
      }
    };

    getLoginData();
    managePermissions();

    return wrappedService;
  };
});
