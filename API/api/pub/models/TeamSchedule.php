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
            $this->teamScheduleId = -999;
            $this->teamScheduleEventId = -999;
            $this->teamScheduleStartTime = '2013-12-31';
            $this->teamScheduleEndTime = '2013-12-31';
        }
        
        public function InsertTeamSchedule() {
            global $db;
            
            $query = sprintf("insert into team _schedule (teamScheduleEventId, teamScheduleStartTime, teamScheduleEndTIme)",
                    myEsc($this->teamScheduleEventId),
                    myEsc($this->teamScheduleStartTime),
                    myEsc($this->teamScheduleEndTime));
            logIt("insert team schedule query");
            logIt(var_export($query,true));
            
            mysql_query($query, $db);
            
            $id = mysql_insert_id();
            
            if($id >= 0) {
                $this->teamId = $id;
            }
            
            return $id;       
        }
        
        public function SelectTeamSchedule($teamScheduleId) {
            global $db;
            
            $query = "select * from team_schedule where teamScheduleId = " .myEsc($teamScheduleId);
            $sql = mysql_query($query, $db);
            
            $teamSchedule = array();
            
            if(mysql_num_rows($sql) > 0) {
                $result = array();
                
                while($row = mysql_fetch_array($sql)) {
                    $result[] = $rlt;
                }
                
                
            }
            
        }
    }
    
?>