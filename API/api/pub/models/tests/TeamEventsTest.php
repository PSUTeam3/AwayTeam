<?php

    require_once('/home/awayteam/api/pub/apiconfig.php');
    require_once('/home/awayteam/api/pub/models/TeamEvent.php');
    
    class TeamEventXUnitTest extends PHPUnit_Framework_TestCase 
    {
        public function setUp() {
            dbConnect();
            global $db;
            
            $this->team0->teamName = "team task test team";
            $this->team0->teamLocationName = "san diego";
            $this->team0->teamDescription = "team task test team";
            $this->team0->teamManaged = 0;
            
            $this->teamId = $this->team0->InsertTeam('vuda1');
            
            $this->teamEvent->teamEventName = "unit test for team event";
            $this->teamTask0->taskDescription = "unit testing team task class";
            $this->teamTask0->taskCompleted = false;
            $this->teamTask0->taskTeamId = $this->teamId;
        }
    }
    
    
?>