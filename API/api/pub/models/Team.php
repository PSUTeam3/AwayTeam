<?php

    include_once('/home/awayteam/api/pub/apiconfig.php');
	
    class Team
    {
        //class attributes
        public $teamId;
        public $teamName;
        public $teamLocationId;
        public $teamDescription;
        public $teamManaged;
        
        public function_construct()
        {
            $this->initialize();
        }
        
        public function initialize()
        {
            $this->teamId = -999;
            $this->teamName = "";
            $this->teamLocationId = "";
            $this->teamDescription = "";
            $this->teamManaged = False;            
        }
        
        //data functions
        public function UpdateTeam($id)
        {
            global $db;
            $query = sprintf("update team set 
        }
        
	