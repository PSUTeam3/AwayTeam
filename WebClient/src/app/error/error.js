(function(app) {

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider.state('error', {
            url: '/error',
            views: {
                "main": {
                    templateUrl: 'error/error.tpl.html'
                }
            },
            data:{ pageTitle: 'Error' },
            accessLevel: accessLevels.public
        });
    }]);

    app.controller('AboutController', ['$scope', function ($scope) {

        var init = function() {
            // A definitive place to put everything that needs to run when the controller starts. Avoid
            //  writing any code outside of this function that executes immediately.
        };

        init();
    }]);

}(angular.module("AwayTeam.error", [
    'AwayTeam.router'
])));