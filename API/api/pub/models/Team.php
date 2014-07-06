<?php

    include_once('/home/awayteam/api/pub/apiconfig.php');
    include_once('/home/awayteam/api/pub/models/TeamUtilities.php');
    include_once('/home/awayteam/api/pub/models/TeamMembers.php');
    include_once('/home/awayteam/api/pub/models/TeamMemberUtilities.php');
    include_once('/home/awayteam/api/pub/models/Location.php');
    
    class Team
    {
        //class attributes
        public $teamId;
        public $teamName;
        public $teamLocationId;
        public $teamDescription;
        public $teamManaged;
        public $teamScheduleId;
        
        public function __construct() {
            $this->initialize();
        }
        
        public function initialize() {
            $this->teamId = -999;
            $this->teamName = "";
            $this->teamLocationId = -999;
            $this->teamDescription = "";
            $this->teamManaged = False; 
            $this->teamScheduleId = -999;
        }
        
        public function InsertTeam() {
            global $db;
            
            if (strtolower($this->teamManaged) == "true")
            {
                $this->teamManaged = 1;
            }
            else
            {
                $this->teamManaged = 0;
            }

            $query = sprintf("insert into team (teamName,teamDescription,teamManaged) values ('%s','%s','%d')",
            // orig $query = sprintf("insert into team (teamName,teamLocationId,teamDescription,teamManaged,teamScheduleId) values ('%s',%d,'%s','%s',%d)",
                myEsc(strtolower($this->teamName)),
                myEsc($this->teamDescription),
                myEsc($this->teamManaged));
            logIt("insert team query");
            logIt(var_export($query, true));
                
            mysql_query($query, $db);
            
            $id = mysql_insert_id();
            
            if($id>=0) {
                $this->teamId = $id;
            }
            
            return $id;
        }
        
        public function SelectAllTeams() {
            global $db;
            $query = "select * from team";
            $sql = mysql_query($query, $db);
            $teamList = array();
            
            if(mysql_num_rows($sql) > 0) {
                $result = array();
                
                while($row = mysql_fetch_object($sql)) {
                    $tTeam = $row;
                    $teamList[] = $tTeam;
                }                               
                
                return $teamList;
            }
        }
        
        public function SelectTeamFromId($teamId, $loginId) {
            global $db;
            $aTeam = new Team;
            $tm = new TeamMembers;
            $tu = new TeamUtilities;
            
            $query = "select userId from user where loginId = '" . myEsc($loginId) ."'";
            $sql = mysql_query($query, $db);
            if(mysql_num_rows($sql) > 0) {
                $result = array();
                while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                    $result[] = $rlt;
                }
                
                $userId = $result[0]['userId'];
            }
            
            if($tm->VerifyTeamMemberExist($teamId, $userId) && $teamId && $tu->TeamIdExists($teamId)) {
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
                    return $aTeam;
                }                
            } else {
                logIt("not getting there");
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
            
            $teamList = array();
            $location = new Location;
            $query = "select teamId from teamMember where userId = " . myEsc($userId);
            $sql = mysql_query($query, $db);
            if(mysql_num_rows($sql) > 0) {
                $getTeamIdResult = array();
                while($getTeamIdResult = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                    $teamId = $getTeamIdResult['teamId'];
                    $query = "select * from team where teamId = " . myEsc($teamId);
                    while($getTeamInfoResult = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                        $teamInfoResult = array();
                        $teamInfoResult[] = $getTeamInfoResult;
                        
                        foreach($teamInfoResult[0] as $item=>$value) {
                            $tTeam->$item = $value;                            
                        }
                        $teamList[] = $tTeam;
                        
                        $location = SelectLocationFromLocationId($getTeamInfoResult['teamLocationId']);
                        $teamList[] = $location;
                        
                        $teamList[] = SelectTeamMemberFromTeamId($teamId);                        
                    }                    
                }              
            }
            
            return $teamList;            
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
        
        public function ModifyTeamNameModel($newTeamName)
        {
            global $db;
            
            if($this->teamId == -999) {
               return false;               
            } else if($newTeamName && TeamNameUsed($newTeamName)) {               
                $query = "update team set teamName=" .  myEsc($newTeamName) . 
                            " where teamId=" . myEsc($this->teamId);
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
