<?php
    
    //Owner: David Vu
    
    require_once('/home/awayteam/api/pub/models/User.php');
    require_once('/home/awayteam/api/pub/apiconfig.php');
    
    class TeamController extends Team
    {    
        public function CreateTeam ($teamParamatersArray) {
            $tTeam = new Team;
            $tTeam = arrayToObject($teamParametersArray);            
            $newTeamId = $tTeam->InsertTeam();
        }
        
        public function GetAllTeams() {
            return $this->SelectAllTeams();
        }
        
        public function GetTeamFromID($teamId) {
            return $this->SelectTeamFromId($teamId);
        }
        
        public function GetTeamFromTeamName($teamName) {
            return $this->SelectTeamFromTeamName($teamName);          
        }        
                
        public function ModifyTeamName($teamParametersArray) {
            $tTeam = new Team;
            $tTeam = arrayToObject($teamParamatersArray);
            $retCode = $tTeam->ModifyTeamName();
            return $retCode;
        }
        
        public function ModifyTeam ($teamParamatersArray) {
            $tTeam = new Team;
            $tTeam = arrayToObject($teamParametersArray);
            $retCode = $tTeam->ModifyTeam();
            
            return $retCode;
        }
        
        public function DeleteTeam($team) {
            return $this->DeleteTeam($team->teamId)
        }
        
        private function arrayToObject($teamArray) {
            $tTeam = new Team;
            foreach($teamArray as $item=>$value) {
                $tTeam->item = $value;
            }
            
            return $tTeam;            
        }
    }
?>