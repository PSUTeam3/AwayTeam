/**
 * Created by Clay Parker on 7/20/2014.
 */
angular.module('memberService', [])
    .provider('memberService', function () {
        var errorState = 'error',
            logoutState = 'home';

        this.$get = function ($rootScope, $http, $q, $state, $log) {

            /**
             * Low-level, private functions.
             */


            /**
             * High level, public methods
             */
            var wrappedService = {
                /**
                 * Public properties
                 */
            };

            return wrappedService;
        };
    });
