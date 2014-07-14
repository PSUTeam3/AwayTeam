<?php
    
    require_once('/home/awayteam/api/pub/models/TeamMembers.php');
    require_once('/home/awayteam/api/pub/apiconfig.php');
    
    class TeamMemberController extends TeamMembers
    {
        public function CreateTeamMember($teamMemberParametersArray) {
            $tTeamMember = new TeamMembers;
            $tTeamMember = $this->arrayToObject($teamMemberParametersArray);
            $newTeamMemberId = $tTeamMember->InsertTeamMember();
            return $newTeamMemberId;
        }
        
        public function JoinTeam($teamId,$loginId) {
            $aTeamMember = new TeamMembers;
            $newTeamMemberId = $aTeamMember->AddTeamMember($teamId,$loginId);
            return $newTeamMemberId;
        }
        
        public function GetTeamMemberFromId($teamMemberId) {
            return $this->SelectTeamMemberFromId($teamMemberId);
        }
        
        public function GetTeamMemberFromTeamId($teamId) {
            return $this->SelectTeamMemberFromTeamId($teamID);
        }
        
        public function ModifyTeamMemberController($teamMemberParametersArray) {
            $tTeamMember = new TeamMembers;
            $tTeamMember = $this->arrayToObject($teamMemberParametersArray);
            $retCode = $tTeamMember->ModifyTeamMember();
            return $retCode;           
        }
        
        public function ModifyTeamMemberManager($teamMemberId, $newManagerValue) {
            return $this->ModifyManagerAttribute($teamMemberId, $newManagerValue);
        }
        
        public function ModifyPendingApproval($teamMemberId, $pendingValue) {
            return $this->ModifyPendingApproval($teamMemberId, $pendingValue);
        }
        
        public function ModifyTeamId($teamMemberId, $teamId) {
            return $this->ModifyTeamMemberTeamId($teamMemberId, $teamId);
        }
        
        public function RemoveTeamMember($teamMemberId) {
            return $this->DeleteTeamMember($teamMemberId);
        }
        
        private function arrayToObject($array) {
            $teamMember = new TeamMembers;
            //convertArray to User Object
            foreach($array as $item=>$value)
            {
                $teamMember->$item = $value;
            }
            
            return $teamMember;
        }
    }
?>
