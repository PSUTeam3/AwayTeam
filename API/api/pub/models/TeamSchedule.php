<?php
    
    include_once('/home/awayteam/api/pub/apiconfig.php');
    
    class TeamSchedule {
        
        public $teamScheduleId;
        public $teamSchedulEventId;
        public $teamScheduleStartTime;
        public $teamScheduleEndTime;
        
        public function __construct() {
            $this->initialize();
        }
        
        public function initialize() {
            $this->teamScheduleId = -999
            $this->teamScheduleEventId = -999;           
        }       
    }
    
?>