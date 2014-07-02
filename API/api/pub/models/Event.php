<?php
    include_once('/home/awayteam/api/pub/apiconfig.php');
    
    class Event 
    {
        //Attributes
        public $eventId;
        public $eventName;
        public $eventDescription;
        public $eventLocationId;
        
        public function __construct() {
            $this->initialize();
        }
        
        public function initialize() {
            $this->eventId = -999;
            $this->eventName = "";
            $this->eventDescription = "";
            $this->eventLocationId = -999;
        }
        
        public function InsertEvent() {
            global $db;
            $query = sprintf("insert into event(eventName, eventDescription, eventLocationId) values ('%s','%s','d')",
                    myEsc($this->eventName),
                    myEsc($this->eventDescription),
                    myEsc($this->eventLocationId));
            
            mysql_query($query, $db);
            
            $id = mysql_insert_id();
            
            if($id >= 0) {
                $this->taskId = $id;
            }
            
            return $id;
        }
        
        public function ModifyEventName() {                      
        }
    }
?>