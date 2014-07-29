<?php
    include_once('/home/awayteam/api/pub/apiconfig.php');
    
    class Event 
    {
        //Attributes
        public $teamEventId;
        public $teamEventName;
        public $teamEventDescription; 
        public $teamEventLocationString;
        public $teamEventStartTime;
        public $teamEventEndTime;
        public $teamEventTeamId;
        
        public function __construct() {
            $this->initialize();
        }
        
        public function initialize() {
            $this->teamEventId = -999;
            $this->teamEventName = "";
            $this->teamEventDescription = "";
            $this->teamEventLocationString = "";
            $this->teamEventStartTime = '2013-12-31';
            $this->teamEventEndTime = '2013-12-31';
            $this->teamEventTeamId = -999;
        }
        
        public function InsertEvent() {
            global $db;
            $query = sprintf("insert into event(teamEventName,teamEventDescription,teamEventLocationString,teamEventStartTime,teamEventEndTime,teamEventTeamId) 
                                values('%s','%s','%s','%s','%s',%d)",
                    myEsc(strtolower($this->teamEventName)),                    
                    myEsc($this->teamEventDescription),
                    myEsc($this->teamEventLocationName),
                    myEsc($this->teamEventStartTime),
                    myEsc($this->teamEventEndTime),
                    myEsc($this->teamEventTeamId));
            
            mysql_query($query, $db);
            
            $id = mysql_insert_id();
            
            if($id >= 0) {
                $this->teamEventId = $id;
            }
            
            return $id;
        }
        
        public function SelectAllEvents() {
            global $db;
            
            $query = "select * from event";            
            mysql_query($query, $db);
            $id = mysql_insert_id();
            
            if($id >= 0) {
                
            }
        }
        
        public function SelectEventFromEventId($eventId) {
            global $db;
            
            $anEvent = new Event;            
            $query = "select * from event where eventId = " . myEsc($eventID);
            $sql = mysql_query($query, $db);
            if(mysql_num_rows($sql) > 0) {
                $result = array();
                while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                    $result[] = $rlt;
                }
                
                foreach($result[0] as $column=>$value) {
                    $anEvent->$item = $value;
                }
                
                return $anEvent;                
            }
        }
        
        public function SelectEventFromEventName($eventName) {
            global $db;
            
            $anEvent = new Event;
            $query = "select * from event where eventName = " . myEsc($eventName);
            $sql = mysql_query($query, $db);
            if(mysql_num_rows($sql) > 0) {
                $result = array();
                while($rlt = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                    $result[] = $rlt;
                }
                
                foreach($result[0] as $column->$value) {
                    $anEvent->$item = $value;
                }
                
                return $anEvent;
            }
        }       
        
        public function ModifyEvent() {
            global $db;
            $query = sprintf("update event set teamEventName='%s', teamEventDescription='%s',teamEventLocationString='%s', teamEventStartTime='%s',teamEventEndTime='%s'
                                where teamEventId = " . myEsc($this->teamEventId),
                    myEsc($this->teamEventName),
                    myEsc($this->teamEventDescription),
                    myEsc($this->teamEventLocationString),
                    myEsc($this->teamEventStartTime),
                    myEsc($this->teamEventEndTime));
            
            $sql = mysql_query($query, $db);
                
            return $sql;                  
        }
        
        public function ModifyEventName($teamEventId, $newEventName) {  
            global $db;
            
            if($teamEventId = -999) {
                return false;
            } else if ( $newEventName) {            
                $query = "update event set eventName = " . myEsc($newEventName)  .
                        "where eventId = " . myEsc($teamEventId);
                $sql = mysql_query($query, $db);
                return $sql;
            } else {
                return false;
            }
        }
        
        public function DeleteEvent($eventId) {
            global $db;
            if($eventId) {
                $query = "delete from team_event where teamEventId = " . myEsc($eventId);
                $sql = mysql_query($query, $db);
                return $sql;
            }
        }
    }
?>