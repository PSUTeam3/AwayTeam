<?php
    
    //Owner: David Vu
    
    require_once('/home/awayteam/api/pub/models/User.php');
    require_once('/home/awayteam/api/pub/apiconfig.php');
    
    class TeamController extends Team
    {
        public function ModifyTeam ($teamParamatersArray) {
            $tTeam = new Team;
            $tTeam = arrayToObject($teamParametersArray);
            $retCode = $tTeam->UpdateTeam();
            
            return $retCode;
        }
        
        public function CreateTeam ($teamParamatersArray) {
            $tTeam = new Team;
            $tTeam = arrayToObject($teamParametersArray);
            
            $newTeamId = $tTeam->InsertTeam();
        }
        
        public function DeleteTeam($team) {
            return $this->DeleteTeam($team->teamId)
        }
        
        public function GetTeamFromID($teamId) {
            return $this->SelectTeamFromId($teamId);
        }
        
        public function GetTeamFromTeamNam($teamName) {
            $arr = $this->SelectTeamFromTeamName($teamName);
            return $arr;
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