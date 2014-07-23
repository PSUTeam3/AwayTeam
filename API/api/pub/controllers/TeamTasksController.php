<?php

    require_once('/home/awayteam/api/pub/models/TeamTasks.php');
    require_once('/home/awayteam/api/pub/apiconfig.php');
    
    class TeamTasksController extends TeamTasks {
    
        public function CreateTeamTasks($teamTasksParametersArray) {
            $aTeamTask = new TeamTasks;
            $aTeamTask = $this->arrayToObject($teamTasksParametersArray);
            $teamTaskId = $aTeamTask->InsertTeamTask();
            return $teamTaskId;
        }
        
        public function ModifyTeamTask($teamTasksParametersArray) {
            $teamTask = new TeamTasks;
            $teamTask = $this->arrayToObject($teamParametersArray);
            $retCode = $teamTask->ModifyTeamTask();
            return $retCode;
        }
        
        public function ChangeTeamTaskToComplete($taskId) {
            return $this->MarkTeamTaskComplete($taskId);
        }
        
        public function RemoveTeamTask($taskId) {
            return $this->DeleteTeamTask();
        }
        
        private function arrayToObject($array) {
            $teamTasks = new TeamTasks;
            //convertArray to User Object
            foreach($array as $item=>$value)
            {
                $teamTasks->$item = $value;
            }
            
            return $teamTasks;
        }
    }
    
?>