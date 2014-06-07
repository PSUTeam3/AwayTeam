<?php
    
    //Owner: David Vu
    
    include_once('/home/awayteam/api/pub/apiconfig.php');
    
    class Team
    {
        //class attributes
        public $teamId;
        public $teamName;
        public $teamLocationId;
        public $teamDescription;
        public $teamManaged;
        
        public function_construct() {
            $this->initialize();
        }
        
        public function initialize() {
            $this->teamId = -999;
            $this->teamName = "";
            $this->teamLocationId = "";
            $this->teamDescription = "";
            $this->teamManaged = False;            
        }
        
        //data functions
        public function UpdateTeam($id) {
            global $db;
            $query = sprintf("update team set teamName='%s', teamLocationId='%d', teamDescription='%s', teamManaged='%s' where teamId = %d",
                myEsc($this->teamName),
                myEsc($this->teamLocationId),
                myEsc($this->teamDescription),
                myEsc($this->teamManaged),
                        			
			//send back code if successful or not
			$sql = mysql_query($query, $db);	
			
			return $sql;
        }
        
        public function SelectTeamFromId($id) {
            global $db;
            if($id) {
                $query = "select from team where teamId =" . myEsc($id);
                $sql = mysql_query($query, $db)
                if(mysql_num_rows($sql) > 0) {
                    $result = array();
                    while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                        &result[] = $rlt;
                    }
                    
                    foreach($result[0] as $column=>$value) {
                        &tTeam->$item = $value;
                    }
                    return $tTeam;
                }
            } else {
                //What do we want to do with empty id field?
                //Error message
            }
        }
        
        public function SelectTeamFromTeamName($teamName) {
            global $db;
            if($teamName) {
                $query = "select from team where teamName =" . myEsc($teamName);
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
        
        public function DeleteTeam($teamId) {
            global $db;
            if($teamId) {
                $query = "delete from team where teamId = " . myEsc($teamId);
                $sql = mysql_query($query, $db);
                return $result;
            }            
        }
        
        public function InsertTeam() {
            $query = sprintf("insert into team (teamName,teamLocationId,teamDescription,teamManaged) values ('%s','%d','%s','%s')",
                myEsc($this->teamName),
                myEsc($this->teamLocationId),
                myEsc($this->teamDescription),
                myEsc($this->teamManaged),
                
            mysql_query($query, $db);
            
            $id = mysql_insert_id();
            
            return $id;
        }
 ?>
        
    
