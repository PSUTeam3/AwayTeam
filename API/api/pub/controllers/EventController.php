<?php
    
    require_once('/home/awayteam/api/pub/models/Event.php');
    require_once('/home/awayteam/api/pub/apiconfig.php');
    
    class EventController extends Event
    {
        public function CreateEvent($eventParametersArray) {
            $anEvent = new Event;
            $anEvent = arrayToObject($eventParametersArray);
            $anEventId = $anEvent->InsertEvent();
        }
        
        public function GetAllEvents() {
            return $this->SelectAllEvents();
        }
    }
?>