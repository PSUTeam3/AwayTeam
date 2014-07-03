<?php

    require_once('/home/awayteam/api/pub/apiconfig.php');
    
    class EventUtilities
    {
        public function EventNameUsed($eventName) {
            global $db;
            if($eventName) {
                $eventName = strtolower($eventName);
                $query = "select count($eventName) as num from event where eventName = " . myEsc($eventName);
                $sql = mysql_query($query, $db);
                $data = mysql_fetch_assoc($sql);
                
                if($data['num'] == 0) {
                    return false;
                } else {
                    return true;
                }
            
            }
        }
    }
?>