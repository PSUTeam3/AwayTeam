/**
 * Created by Clay Parker on 7/30/2014.
 */

angular.module('taskService', [])
    .provider('taskService', function () {
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

                teamTasks: {},
                selectedTask: null,

                createTeamTask: function(loginId, teamId, title, description){
                    var postData = {loginId:loginId, taskTeamId:teamId, taskTitle:title, taskDescription:description};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/teamtasks/createtask",
                        method: "POST",
                        data: postData
                    });

                    return promise;
                },

                updateTeamTask: function(taskId, taskCompleted, taskDeletion, teamId, loginId){
                    var postData = {taskId:taskId, taskCompleted:taskCompleted, taskDeletion:taskDeletion, taskTeamId:teamId, loginId:loginId};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/teamtasks/updatetask",
                        method: "POST",
                        data: postData
                    });

                    return promise;
                },

                editTeamTasks: function(loginId, taskId, teamId, title, description){
                    var postData = {loginId:loginId, taskId:taskId, taskTeamId:teamId, taskTitle:title, taskDescription:description};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/Manager/TakeAction",
                        method: "POST",
                        data: postData
                    });

                    return promise;
                }

            };

            return wrappedService;
        };
    });
