<?php
    include_once('/home/awayteam/api/pub/apiconfig.php');
    
    class TeamMember
    {
        //Attributes
        public $teamMemberId;
        public $teamId;
        public $userId;
        public $manager
        public $pendingApproval;
        
        public function_construct() {
            this->initialize();
        }
        
        public initialize() {
            $teamMemberId = -999;
            $teamId = -999;
            $userID = -999;
            $manager = false;
            $pendingApproval = false;
        }
        
        public function InsertTeamMember() {
            $query = sprintf("insert into team_member (teamMemberId, teamId, userId, manager, pendingApproval) values ('%d','%d','%s','%s')",
                myEsc($this->teamMemberId),
                myEsc($this->teamId),
                myEsc($this->userId),
                myEsc($this->manager),
                myEsc($this->pendingApproval));
            
                            
                mysql_query($query, $db);
                
                $id = mysql_insert_id();
                
                return $id;
        }
        
        public function SelectTeamMemberFromId($id) {
            global $db;
            $aTeamMember = new TeamMember;
            if($id && TeamMemberIdExists($id)) {
                $query = "select * from team_member where id = " . myEsc($id);
                $sql = mysql_query($query, $db);
                if(mysql_num_rows($sql) > 0) {
                    $result = array();
                    while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                        &result[] = $rlt;
                    }
                    
                    foreach($result[0] as $column=>$value) {
                        &aTeamMember->$item = $value;
                    }
                    
                    return $aTeamMember;
                }
            }            
        }
        
        public function ModifyTeamMember($id) {
            global $db;
            $query = sprintf("update team_member set teamId='%d', userId='%d', manager='%s', pendingApproval='%s' where id=" . myEsc($id),
                    myEsc($this->teamId),
                    myEsc($this->userId),
                    myEsc($this->manager),
                    myEsc($this->pendingApproval));
            
            $sql = mysql_query($query, $db);
            return $sql;          
        }        

        public function ModifyManagerAttribute($newManagerValue) {
            global $db;
            if($this->teamMemberId == -999) {
                return false;
            } else if($newManagerValue && TeamMemberIdExists($this->teamMemberId) {
                $query = "update team_member set manager=" .myEsc($newManagerValue) 
                        . " where id = " .myEsc($this->teamMemberId);
                $sql = mysql_query($query, $db);
                return $sql;
            } else {
                return false;
            }
        }
        
        public function ModifyPendingApproval($booleanValue) {
            global $db;
            if(this->teamMemberId == -999) {
                return false;
            } else if($booleanValue && TeamMemberIdExists($this->teamMemberId)) {
                $query = "update team_member set pendingApproval=" .myEsc($booleanValue)
                        . " where id = " .myEsc($this->teamMemberId);
                $sql = mysql_query($query, $db);
                return $sql;                
            } else {
                return false;
            }
        }
        
        public function ModifyTeamId($id) {
            global $db;
            if($this->teamMemberId == -999) {
                return false;
            } else if ($id && TeamIdExists($id) {
                $query = "update team_member set teamId=" . myEsc($id) 
                        . " where id = " .myEsc($this->teamMemberId);
                $sql = mysqul_query($query, $db);
                return $sql;
            } else {
                return false;
            }
        }
        
        public function VerifyTeamMemberExist($teamId, $userId) {
            global $db;
            if($teamId && $userId) {
                $query = "select count(teamMemberId) as num from team_member where 
                        teamId = " . myEsc($teamId) . " and userId = " . myEsc($userId);
                $data = mysql_fetch_assoc($sql);
                
                if($data['num'] == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        
        
    }
?>