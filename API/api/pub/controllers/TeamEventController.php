<?php
    
    require_once('/home/awayteam/api/pub/models/TeamEvent.php');
    require_once('/home/awayteam/api/pub/apiconfig.php');
    
    class EventController extends Event
    {
        public function CreateEvent($eventParametersArray) {
            $anEvent = new Event;
            $anEvent = arrayToObject($eventParametersArray);
            $anEventId = $anEvent->InsertEvent();
            return $anEventId;
        }
        
        public function GetAllEvents() {
            return $this->SelectAllEvents();
        }
        
        public function GetEventFromEventId($teamEventId) {
            return $this->SelectEventFromEventId($teamEventId);
        }
        
        public function GetEventFromEventName($teamEventName) {
            return $this->SelectEventFromEventName($teamEventName);
        }
        
        public function ModifyEvent($teamEventParametersArray) {
            $anEvent = new Event;
            $anEvent = arrayToObject($eventParametersArray);
            $retCode = $anEvent->ModifyEvent();
            return $retCode;
        }
        
        public function ModifyEventName($teamEventId, $newTeamEventName) {
            return $this->ModifyEventName($newTeamEventName);
        }
        
        public function RemoveEvent($eventId) {
            return $this->DeleteEvent($eventId);
        }
        
        private function arrayToObject($array) {
            $teamEvent = new TeamEvent;
            //convertArray to User Object
            foreach($array as $item=>$value)
            {
                $teamEvent->$item = $value;
            }
            
            return $teamEvent;
        }
    }
?>