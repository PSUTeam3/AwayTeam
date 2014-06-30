<?php
    
    include_once('/home/awayteam/api/pub/apiconfig.php');
    include_once('/home/awayteam/api/pub/models/TeamUtilities.php');
    
    class Location
    {
        $this->locId;
        $this->locName;
        $this->locLatitude;
        $this->locLongitude;
        
        public function_construct() {
            $this->initialize();
        }
        
        public function initialize() {
            $this->locId = -999;
            $this->locName = "";
            $this->locLatitude = "";
            $this->locLongitude = "";            
        }
        
        public function InsertLocation() {
            $query = sprintf("insert into location (locId, locName, locLatitude, locLongitude) values ('%d','%s', '%s')",
                myEsc($this->locId),
                myEsc($this->locName),
                myEsc($this->locLatitude),
                myEsc($this->locLongitude),
                
                mysql_query($query, $db);
                
                $id = mysql_insert_id();
                
                return $id;
        }
        
        public function SelectAllLocations() {
            global $db;
            $query = "select * from location"
            $sql = mysql_query($query, $db);
            
            if(mysql_num_rows($sql) > 0) {
                $result = array();
                
                while($row = mysql_fetch_array($sql, MYSQL_ASSOC)) {
                    $result[] = $rlt;
                }
                
                foreach($result[0] as $item=>$value) {
                    $tLocation->$item=$value;
                }
                
                return $tTeam;
            }
        }
        
        public function DeleteLocation($locId) {
            global $db;
            if($locId) }
                $query = "delete from location where locId = " . myEsc($locId);
                $sql = mysql_query($query, $db);
                return $sql;
            }  
        }
        
    }
?>