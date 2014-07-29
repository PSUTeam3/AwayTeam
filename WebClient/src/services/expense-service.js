/**
 * Created by Clay Parker on 7/20/2014.
 */
angular.module('expenseService', [])
    .provider('expenseService', function () {
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

                teamExpenses: [],
                selectedExpense: null,

                createExpense: function(description, amount, expenseDate, teamId, userId, expenseType){
                    var postData = {description:description, amount:amount, expDate:expenseDate, teamId:teamId, userId:userId, expType:expenseType};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/expense/createexpense",
                        method: "POST",
                        data: postData
                    });

                    return promise;
                },
                modifyExpense: function(expenseId, description, amount, expenseDate, teamId, userId, expenseType){
                    var postData = {expenseId:expenseId, description:description, amount:amount, expDate:expenseDate, teamId:teamId, userId:userId, expType:expenseType};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/expense/modifyexpense",
                        method: "POST",
                        data: postData
                    });

                    return promise;
                },
                deleteExpense: function(expenseId){
                    var postData = {expenseId:expenseId};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/expense/deleteexpense",
                        method: "POST",
                        data: postData
                    });

                    return promise;
                },
                getTeamExpenses: function(userId, teamId){
                    var postData = {userId:userId, teamId:teamId};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/expense/getexpense",
                        method: "POST",
                        data: postData
                    });
                    promise.success(function(data){
                      //  if (data.status === "success") {
                            wrappedService.teamExpenses = data.response;
                     //   }
                    });

                    return promise;
                },
                getExpense: function(userId, teamId, expenseId){
                    var postData = {userId:userId, teamId:teamId, expenseId:expenseId};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/expense/getexpense",
                        method: "POST",
                        data: postData
                    });
                    return promise;
                },
                getTeamExpensesByType: function(userId, teamId, reqType){
                    var postData = {userId:userId, teamId:teamId, reqType:reqType};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/expense/getexpense",
                        method: "POST",
                        data: postData
                    });

                    return promise;
                },
                getExpensesByDate: function(userId, teamId, reqDate){
                    var postData = {userId:userId, teamId:teamId, reqDate:reqDate};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/expense/getexpense",
                        method: "POST",
                        data: postData
                    });

                    return promise;
                }
            };

            return wrappedService;
        };
    });
