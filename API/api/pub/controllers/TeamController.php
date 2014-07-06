<?php
    
    //Owner: David Vu
    
    require_once('/home/awayteam/api/pub/models/Team.php');
    require_once('/home/awayteam/api/pub/apiconfig.php');
    
    class TeamController extends Team
    {    
        public function CreateTeam ($teamParametersArray) {
            $tTeam = new Team;
            $tTeam = $this->arrayToObject($teamParametersArray);            
            $newTeamId = $tTeam->InsertTeam();
            return $newTeamId;
        }
        
        public function GetAllTeams() {
            return $this->SelectAllTeams();
        }
        
        public function GetTeamFromID($teamId,$loginId) {
            return $this->SelectTeamFromId($teamId,$loginId);
        }
        
        public function GetTeamFromTeamName($teamName) {
            return $this->SelectTeamFromTeamName($teamName);          
        }        
        
        public function GetTeamListForUser($loginId) {
            return $this->GetTeamList($loginId);
        }
        public function ModifyTeamName($teamParametersArray) {
            $tTeam = new Team;
            $tTeam = arrayToObject($teamParamatersArray);
            $retCode = $tTeam->ModifyTeamNameModel();
            return $retCode;
        }
        
        public function ModifyTeam($teamParamatersArray) {
            $tTeam = new Team;
            $tTeam = arrayToObject($teamParametersArray);
            $retCode = $tTeam->ModifyTeamModel();
            
            return $retCode;
        }
        
        public function DeleteTeam($team) {
            return $this->DeleteTeam($team->teamId);
        }
        
        private function arrayToObject($teamArray) {
            $tTeam = new Team;
            foreach($teamArray as $item=>$value) {
                $tTeam->$item = $value;
            }
            
            return $tTeam;            
        }
    }
?>
