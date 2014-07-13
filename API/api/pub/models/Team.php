<?php

    include_once('/home/awayteam/api/pub/apiconfig.php');
    include_once('/home/awayteam/api/pub/models/TeamUtilities.php');
    include_once('/home/awayteam/api/pub/models/TeamMembers.php');
    include_once('/home/awayteam/api/pub/models/TeamMemberUtilities.php');
    
    class Team
    {
        //class attributes
        public $teamId;
        public $teamName;
        public $teamLocationName;
        public $teamDescription;
        public $teamManaged;        
        
        public function __construct() {
            $this->initialize();
        }
        
        public function initialize() {
            $this->teamId = -999;
            $this->teamName = "";
            $this->teamLocationName = "";
            $this->teamDescription = "";
            $this->teamManaged = False; 
        }
        
        public function InsertTeam($loginId) {
            global $db;
            $teamMember = new TeamMembers;
            
            $idArray = array();

            if (strtolower($this->teamManaged) == "true")
            {
                $this->teamManaged = 1;
            }
            else
            {
                $this->teamManaged = 0;
            }

            $query = sprintf("insert into team (teamName,teamLocationName, teamDescription,teamManaged) values ('%s','%s','%s',%d)",           
                myEsc($this->teamName),
                myEsc($this->teamLocationName),
                myEsc($this->teamDescription),
                myEsc($this->teamManaged));
            logIt("insert team query");
            logIt(var_export($query, true));
                
            mysql_query($query, $db);
            
            $id = mysql_insert_id();
            
            if($id>=0) {
                $this->teamId = $id;
                $teamMemberId= $teamMember->AddFirstTeamMember($this->teamId,$loginId);
                $idArray = ["teamId" => $this->teamId, "teamMemberId" => $teamMemberId];
            }
            
            return $idArray;
        }
        
        public function SelectAllTeams() {
            global $db;
            
            $query = "select teamId, teamName, teamLocationName, teamDescription, teamManaged from team";
            $sql = mysql_query($query, $db);
            $teamList = array();
            
            if(mysql_num_rows($sql) > 0) {                
                while($row = mysql_fetch_object($sql)) {
                    $tTeam = $row;
                    $teamList[] = $tTeam;                  
                }            
            }
            
            return $teamList;
        }
        
        public function SelectTeamFromId($teamId, $loginId) {
            global $db;
            $aTeam = new Team;
            $tm = new TeamMembers;
            $tu = new TeamUtilities;
            logit("selecting userId in selectTeamFromId");
            $query = "select userId from user where loginId = '" . myEsc(strtolower($loginId)) ."'";
            $sql = mysql_query($query, $db);
            if(mysql_num_rows($sql) > 0) {  
                while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                    $userId = $rlt['userId'];
                }              
            }
            
            logit("verifying teamMemberExist in selectTeamFromId");
            if($tm->VerifyTeamMemberExist($teamId, $userId) && $tu->TeamIdExists($teamId)) {
                $query = "select * from team where teamId =" . myEsc($teamId);
                logIt(var_export($query, true));
                $sql = mysql_query($query, $db);
                if(mysql_num_rows($sql) > 0) {
                    $result = array();
                    while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                        $result[] = $rlt;
                    }
                    
                    foreach($result[0] as $column=>$value) {
                        $aTeam->$column = $value;
                    }
                    
                    //due to complex message, we need to stray from model    
                    $theTeam = get_object_vars($aTeam);

                    //convert int to true/false
                    if ($theTeam['teamManaged'] == 0)
                    {
                        $theTeam['teamManaged'] = "false";
                    }
                    else
                    {
                        $theTeam['teamManaged'] = "true";
                    }

                    //getTeamMembers
                    $query = sprintf("select user.userId, firstName, lastName, email, cellphone as phone, manager from user, team_member where user.userId=team_member.userId and team_member.teamId=%d", myEsc($teamId));
                    $members = array();
                    $sql = mysql_query($query, $db);
                    if (mysql_num_rows($sql) > 0)
                    {
                        while ($rlt = mysql_fetch_array($sql, MYSQL_ASSOC))
                        {
                            $members[] = $rlt;
                        }

                        // now get latest location and set manager true false
                        foreach($members as $user=>$value)
                        {
                            if ($members[$user]['manager'] == 0)
                            {
                                $members[$user]['manager'] = "false";
                            }
                            else
                            {
                                $members[$user]['manager'] = "true";
                            }

                            $location = array();
                            $queryLoc = sprintf("select locLatitude, locLongitude from location where locUserId = %d order by locId desc limit 1", myEsc($members[$user]['userId']));
                            logIt(var_export($queryLoc, true));
                            $sqlLoc = mysql_query($queryLoc, $db);
                            if (mysql_num_rows($sqlLoc) > 0)
                            {
                                while ($rlt  = mysql_fetch_array($sqlLoc, MYSQL_ASSOC))
                                {
                                    $location[] = $rlt;
                                }
                                
                                $members[$user]['locLatitude'] = $location[0]['locLatitude'];
                                $members[$user]['locLongitude'] = $location[0]['locLongitude'];
                            }
                            else
                            {
                                $members[$user]['locLatitude'] = null;
                                $members[$user]['locLongitude'] = null;
                            }
                        }
                        
                    }
                    else
                    {
                        //no members in team
                        $members = null;
                    }

                    $theTeam['members'] = $members;

                    //getTasks
                    $query = sprintf("select taskTitle, taskDescription, taskCompleted from team_tasks where taskTeamId=%d", myEsc($teamId));
                    $tasks = array();
                    $sql = mysql_query($query, $db);
                    if (mysql_num_rows($sql) > 0)
                    {   
                        while ($rlt = mysql_fetch_array($sql, MYSQL_ASSOC))
                        {   
                            $tasks[] = $rlt;
                        }   
                        
                        foreach($tasks as $task=>$value)
                        {
                            if ($tasks[$task]['taskCompleted'] == 0)
                            {
                                $tasks[$task]['taskCompleted'] = "false";
                            }
                            else
                            {
                                $tasks[$task]['taskCompleted'] = "true";
                            }
                        }

                    }
                    else
                    {
                        //no tasks for team
                        $tasks = null;
                    }

                    $theTeam['tasks'] = $tasks;

                    //getTeams
                    $query = sprintf("select teamEventName, teamEventDescription, teamEventLocationString, teamEventStartTime, teamEventEndTime from team_event where teamEventTeamId=%d", myEsc($teamId));                  
                    logIt($query);
                    $events = array();
                    $sql = mysql_query($query, $db);
                    if (mysql_num_rows($sql) > 0)
                    {
                        while ($rlt = mysql_fetch_array($sql, MYSQL_ASSOC))
                        {
                            $events[] = $rlt;
                        }
                        
                    }
                    else
                    {
                        //no tasks for team
                        $events = null;
                    }

                    $theTeam['events'] = $events;

                    return $theTeam;

                }                
            } else {
                logIt("team member doesn't exist");
                //What do we want to do with empty id field?
                //Error message
            }
        }
        
        public function SelectTeamFromTeamName($teamName, $loginId) {
            global $db;
            $tTeam = new Team;
            $query = "select userId from user where loginId = '" . myEsc($loginId) ."'";
            $sql = mysql_query($query, $db);
            if(mysql_num_rows($sql) > 0) {
                $result = array();
                while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                    $result[] = $rlt;
                }
                
                $userId = $result[0]['loginId'];
            }
            
            $query = "select teamId from team where teamName = '" . myEsc($teamName) ."'";
            $sql = mysql_query($query, $db);
            if(mysql_num_rows($sql) > 0) {
                $result = array();
                while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                    $result[] = $rlt;
                }
                
                $teamId = $result[0]['teamId'];
            }
            
            if(TeamMemberExist($teamId, $userId) && $teamName && TeamNameUsed($teamName)){
                $query = "select * from team where teamName =" . myEsc($teamName);
                $sql = mysql_query($query, $db);
                
                if(mysql_num_rows($sql) > 0) {
                    $result = array();
                    
                    while($row = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                        $result[] = $rlt;
                    }
                    
                    foreach($result[0] as $item=>$value) {
                        $tTeam->$item=$value;
                    }
                    
                    return $tTeam;                   
                }                
            } else {
                //What do we want to do with empty teamName field?
                //Error message
            }
        }

        public function GetTeamList($userId) {
            global $db;
            $teamInfoResult = array();
            $query = "select teamId from team_member where userId = " . myEsc($userId);
            $getTeamIdsSql = mysql_query($query, $db);
            if(mysql_num_rows($getTeamIdsSql) > 0) {
                $getTeamIdResult = array(); 
                while($getTeamIdResult = mysql_fetch_array($getTeamIdsSql, MYSQL_ASSOC)) {      
                    $query = "select teamId,teamName from team where teamId = " . myEsc($getTeamIdResult['teamId']);
                    $getTeamObjectsSql = mysql_query($query, $db);
                    while($getTeamInfoResult = mysql_fetch_object($getTeamObjectsSql)) {                    
                        $aTeam = $getTeamInfoResult;
                        $teamInfoResult[] = $aTeam;                                            
                    }                    
                }              
            }
            
            return $teamInfoResult;            
        }
       
        public function ModifyTeamModel() {
            global $db;
            $query = sprintf("update team set teamName='%s', teamLocationId=%d, teamDescription='%s', teamManaged='%s' where teamId = " . myEsc($this->teamId),
                myEsc($this->teamName),
                myEsc($this->teamLocationId),
                myEsc(strtolower($this->teamDescription)),
                myEsc($this->teamManaged));
                                        
            $sql = mysql_query($query, $db);
                
            return $sql;
            
        }
        
        public function ModifyTeamNameModel($teamId, $newTeamName)
        {
            global $db;
            
            if($teamId == -999) {
               return false;               
            } else if($newTeamName && TeamNameUsed($newTeamName)) {               
                $query = "update team set teamName=" .  myEsc($newTeamName) . 
                            " where teamId=" . myEsc($teamId);
                $sql = mysql_query($query, $db);
                return $sql;
            } else {
                return false;
            }
        }
        
        public function DeleteTeam($teamId) {
            global $db;
            if($teamId) {
                $query = "delete from team where teamId = " . myEsc($teamId);
                $sql = mysql_query($query, $db);
                return $sql;
            }            
        }
    }
?>    
