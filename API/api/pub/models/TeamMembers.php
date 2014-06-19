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
        
        public function SelectTeamMemberFromId($id) {
            global $db;
            $aTeamMember = new TeamMember;
            
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
            if($this->teamMemberId)
            {
                
            }
            else if(!empty($id)) {
                
            }
        }
        
        
    }
?>