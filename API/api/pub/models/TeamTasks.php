<?php
    include_once('/home/awayteam/api/pub/apiconfig.php');
    
    class TeamTasks
    {
        //Attributes
        public $taskId;
        public $taskTitle;
        public $taskDescription;
        public $taskCompleted;
        public $taskTeamId;
        
        public function __construct() {
            $this->initialize();
        }
        
        public function initialize() {
            $this->taskId = -999;
            $this->taskTitle = "";
            $this->taskDescription = "";
            $this->taskCompleted =false;
            $this->taskTeamId = -999;            
        }
        
        public function VerifyTaskForTeam($taskId,$taskTeamId) {
            global $db;
            
            if($taskId && $taskTeamId) {
                $query = "select count(taskId) as num from team_tasks where taskId = " . myEsc($taskId) . " AND taskTeamId = " . myEsc($taskTeamId);
                
                $sql = mysql_query($query,$db);            
                $data = mysql_fetch_assoc($sql);
                    
                if ($data['num'] == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        
        public function InsertTeamTask() {
            global $db;           
    
            $this->taskCompleted = 0;
            
            $query = sprintf("insert into team_tasks (taskTitle, taskDescription, taskCompleted, taskTeamId) values ('%s', '%s',%d,%d)",
                    myEsc($this->taskTitle),
                    myEsc($this->taskDescription),
                    myEsc($this->taskCompleted),
                    myEsc($this->taskTeamId));
            
            mysql_query($query, $db);
            
            $id = mysql_insert_id();
            
            if($id >=0) {
                $this->taskId = $id;
            }
            
            return $id;
        }
        
        public function ModifyTeamTask() {
            global $db;
            
            if (strtolower($this->taskCompleted) == "true")
            {
                $this->taskCompleted = 1;
            }
            else
            {
                $this->taskCompleted = 0;
            }
            
            $query = sprintf("update team_tasks set taskTitle='%s', taskDescription ='s', taskCompleted=%d, where taskId = %d",
                    myEsc($this->taskTitle),
                    myEsc($this->taskDescription),
                    myEsc($this->taskCompleted));
                    
            $sql = mysql_query($query, $db);	
                
            return $sql;
        }
        
        public function MarkTeamTaskComplete($taskId) {
            global $db;
            $query = "update team_tasks set taskCompleted = true where taskId = " . myEsc($this->taskId);
            $sql = mysql_query($query,$db);
            return $sql;
        }
        
        public function DeleteTeamTask($taskId) {
            global $db;
            if($taskId) {
                $query = "delete from team_tasks where taskId = " .myEsc($taskId);
                $sql = mysql_query($query, $db);
                return $sql;
            }
        }
    }
?>