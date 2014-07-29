angular.module('teamService', [])
    .provider('teamService', function () {
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
                allTeams: null,
                selectedTeam: {teamName:"Select a team"},
                userTeams : null,
                createTeam: function(loginId, name, description, locationName, managed){
                    var postData = {loginId:loginId, teamName:name, teamDescription:description, teamLocationName:locationName, teamManaged:managed};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/team/createteam",
                        method: "POST",
                        data: postData
                    });

                    return promise;
                },

                modifyTeam: function(loginId, teamId, name, locationName, description, managed){
                    var postData = {loginId:loginId, teamId:teamId, teamName:name, teamLocationName:locationName, teamDescription:description,  teamManaged:managed};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/team/modifyteam",
                        method: "POST",
                        data: postData
                    });

                    return promise;
                },

                loadAllTeams:function(){
                    var postData = {};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/team/getallteams",
                        method: "POST",
                        data: postData
                    });

                    promise.success(function (data) {
                        if (data.status === "success") {
                            wrappedService.allTeams = data.response;
                        }
                    });
                    promise.error(function () { //TODO handle getAllTeams errors
                        wrappedService.allTeams = {};
                    });

                    return promise;
                },

                getTeam: function(teamId, loginId) {
                    var postData = {teamId: teamId, loginId: loginId};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/team/getteam",
                        method: "POST",
                        data: postData
                    });

                    promise.success(function (data) {
                        if (data.status === "success") {
                            wrappedService.selectedTeam = data.response;
                        }
                    });
                    promise.error(function () { //TODO handle getTeam errors
                        wrappedService.selectedTeam = {};
                    });
                },

                loadUserTeams:  function(loginId) {
                    var postData = {loginId: loginId};
                    var promise = $http({
                        url: "https://api.awayteam.redshrt.com/team/getteamlist",
                        method: "POST",
                        data: postData
                    });
                    promise.success(function (data){
                        if(data.status === "success"){
                            wrappedService.userTeams = data.response;
                        }
                    });
                    promise.error(function(){
                        wrappedService.userTeams = {};
                        $log.error("Error getting user's teams.");
                    });
                    return promise;
                },

                joinTeam:  function(loginId, teamId){
                    var postData = {teamId:teamId, loginId:loginId};
                    var promise =$http({
                        url: "https://api.awayteam.redshrt.com/teammember/jointeam",
                        method: "POST",
                        data: postData
                    });
                    return promise;
                },

                getSelectedTeam: function(){
                    return wrappedService.selectedTeam;
                },

                getUserTeams: function(){
                    return wrappedService.userTeams;
                },

                getAllTeams: function(){
                    return wrappedService.allTeams;
                }
            };

            return wrappedService;
        };
    });
