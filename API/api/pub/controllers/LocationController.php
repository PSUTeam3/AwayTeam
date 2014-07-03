<?php
    
    //Owner: David Vu
    
    require_once('/home/awayteam/api/pub/models/Team.php');
    require_once('/home/awayteam/api/pub/apiconfig.php');
    
    class LocationController extends LocationController
    {
        public function CreateLocation($locationParametersArray) {
            $aLocation = new Location;
            $aLocation = arrayToObject($teamParametersArray);
            $newLocationId = $aLocation->InsertLocation();
        }
        
        public function GetAllLocations() {
            return $this->SelectAllLocations();
        }
        
        public function GetLocationFromId($locId) {
            return $this->SelectLocationFromLocationId($locId);
        }
    }
    
?>