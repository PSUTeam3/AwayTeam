<?php
    include_once('/home/awayteam/api/pub/apiconfig.php');
    include_once('/home/awayteam/api/pub/models/TeamMemberUtilities.php');
    
    class TeamMembers
    {
        //Attributes
        public $teamMemberId;
        public $teamId;
        public $userId;
        public $manager;
        public $pendingApproval;
        
        public function __construct() {
            $this->initialize();
        }
        
        public function initialize() {
            $this->teamMemberId = -999;
            $this->teamId = -999;
            $this->userId = -999;
            $this->manager = false;
            $this->pendingApproval = false;
        }
        
        public function InsertTeamMember() {
            global $db;
            
            if (strtolower($this->pendingApproval) == "true")
            {
                $this->pendingApproval = 1;
            }
            else
            {
                $this->pendingApproval = 0;
            }

            
            $query = sprintf("insert into team_member (teamId, userId, manager, pendingApproval) values (%d,%d,%d,%d)",               
                myEsc($this->teamId),
                myEsc($this->userId),
                myEsc($this->manager),
                myEsc($this->pendingApproval));
            
                            
            mysql_query($query, $db);
            
            $id = mysql_insert_id();
            
            if($id >=0) {
                $this->teamMemberId = $id;
            }
            
            return $this->teamMemberId ;
        }
        
        public function AddTeamMember($teamId,$loginId) {
            global $db;
            
            $this->manager = 0;
            $query = "select userId from user where loginId = '" .myEsc($loginId) . "'";
            $sql = mysql_query($query,$db);
            
            if(mysql_num_rows($sql)) {
                while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                    $userId = $rlt['userId'];
                }
            }
            $query = "select teamManaged from team where teamId = " . myEsc($teamId);
            
            $sql = mysql_query($query, $db);
            if(mysql_num_rows($sql)) {
                while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                    $teamManaged = $rlt["teamManaged"];
                }               
            }
            
            if($teamManaged == 1) {
                $this->pendingApproval = 1;

                $query = sprintf("insert into team_member(teamId, userId, manager, pendingApproval) values (%d,%d,%d,%d)",
                    myEsc($teamId),
                    myEsc($userId),
                    myEsc($this->manager),
                    myEsc($this->pendingApproval));
            } else {
                $pendingApproval = 0;
                $query = sprintf("insert into team_member(teamId, userId, manager, pendingApproval) values (%d,%d,%d,%d)",
                    myEsc($this->teamId),
                    myEsc($userId),
                    myEsc($this->manager),
                    myEsc($this->pendingApproval)); 
            }
            
            mysql_query($query,$db);

            $id = mysql_insert_id();
            
            if($id>=0) {
                $this->teamMemberId = $id;
            }
            
            return $this->teamMemberId;           
        }
        
         public function AddFirstTeamMember($teamId, $loginId) {
            global $db;
            
            $manager = 0;
            $pendingApproval = 0;
            $query = "select userId from user where loginId = '" .myEsc($loginId) . "'";
            $sql = mysql_query($query,$db);
            
            if(mysql_num_rows($sql)) {
                while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                    $userId = $rlt['userId'];
                }
            }
            $query = "select teamManaged from team where teamId = " . myEsc($teamId);
            
            $sql = mysql_query($query, $db);
            if(mysql_num_rows($sql)) {
                while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                    $teamManaged = $rlt["teamManaged"];
                }               
            }
            
            if($teamManaged == 1) {
                $manager = 1;
                $query = sprintf("insert into team_member(teamId, userId, manager, pendingApproval) values (%d,%d,%d,%d)",
                    myEsc($teamId),
                    myEsc($userId),
                    myEsc($manager),
                    myEsc($pendingApproval));            
            } else {
                $manager = 0;
                $query = sprintf("insert into team_member(teamId, userId, manager, pendingApproval) values (%d,%d,%d,%d)",
                    myEsc($teamId),
                    myEsc($userId),
                    myEsc($manager),
                    myEsc($pendingApproval)); 
            }
            
            mysql_query($query,$db);
            $id = mysql_insert_id();
            
            if($id>=0) {
                $this->teamMemberId = $id;
            }
            
            return $this->teamMemberId;           
        }
        
        public function SelectTeamMemberFromId($id) {
            global $db;
            $aTeamMember = new TeamMembers;
            $teamMemberUtilities = new TeamMemberUtilities;

            if($id && $teamMemberUtilities->TeamMemberIdExists($id)) {
                $query = "select * from team_member where teamMemberId = " . myEsc($id);
                $sql = mysql_query($query, $db);
                if(mysql_num_rows($sql) > 0) {
                    $result = array();
                    while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                        $result[] = $rlt;
                    }
                    
                    foreach($result[0] as $column=>$value) {
                        $aTeamMember->$item = $value;
                    }
                    
                    return $aTeamMember;
                }
            }            
        }
        
        public function SelectTeamMemberFromTeamId($teamId) {
            global $db;
            $aTeamMember = new TeamMember;
            $teamMemberList = array();
            
            if($teamId) {
                $query = "select * from team_member where teamId = " . myEsc($teamId);
                $sql = mysql_query($query, $db);
                
                if(mysql_num_rows($sql) > 0) {
                    while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                        $teamMemberList = $rlt;
                    }
                }                
            }
            
            return $teamMemberList;
        }
        
        public function ModifyTeamMember() {
            global $db;
            $query = sprintf("update team_member set teamId='%d', userId='%d', manager='%s', pendingApproval='%s' where id=" . myEsc($id),
                    myEsc($this->teamId),
                    myEsc($this->userId),
                    myEsc($this->manager),
                    myEsc($this->pendingApproval));
            
            $sql = mysql_query($query, $db);
            return $sql;          
        }        

        public function ModifyManagerAttribute($teamMemberId, $newManagerValue) {
            global $db;
            if($teamMemberId == -999) {
                return false;
            } else if($newManagerValue && TeamMemberIdExists($teamMemberId)) {
                $query = "update team_member set manager=" .myEsc($newManagerValue) 
                        . " where id = " .myEsc($teamMemberId);
                $sql = mysql_query($query, $db);
                return $sql;
            } else {
                return false;
            }
        }
        
        public function ModifyPendingApproval($teamMemberId, $booleanValue) {
            global $db;
            if($teamMemberId== -999) {
                return false;
            } else if($booleanValue && TeamMemberIdExists($teamMemberId)) {
                $query = "update team_member set pendingApproval=" .myEsc($booleanValue)
                        . " where id = " .myEsc($teamMemberId);
                $sql = mysql_query($query, $db);
                return $sql;                
            } else {
                return false;
            }
        }
        
        public function ModifyTeamMemberTeamId($teamMemberId, $teamId) {
            global $db;
            if($teamMemberId == -999) {
                return false;
            } else if ($teamMemberId && TeamIdExists($teamId)) {
                $query = "update team_member set teamId=" . myEsc($teamId) 
                        . " where teamMemberId = " .myEsc($teamMemberId);
                $sql = mysql_query($query, $db);
                return $sql;
            } else {
                return false;
            }
        }
        
        public function VerifyTeamMemberExist($teamId, $userId) {
            global $db;
            if($teamId && $userId) {
                $query = "select count(teamMemberId) as num from team_member where teamId = " . myEsc($teamId) . " and userId = " . myEsc($userId);
                $sql = mysql_query($query, $db);
                $data = mysql_fetch_assoc($sql);
                
                if($data['num'] == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        
        public function DeleteTeamMember($teamMemberId) {
            global $db;
            
            if($teamMemberId) {
                $query = "delete from team_member where teamMemberId = " .myEsc($teamMemberId);
            }
            
            $sql = mysql_query($query, $db);
            return $sql;
        }
        
        
    }
?>
