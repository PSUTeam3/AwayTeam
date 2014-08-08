<?php

    require_once('/home/awayteam/api/pub/apiconfig.php');
    require_once('/home/awayteam/api/pub/models/TeamEvent.php');
    
    class TeamEventXUnitTest extends PHPUnit_Framework_TestCase 
    {
        var $teamEventId;
        var $teamEvent;
        var $team0;
        
        
        public function setUp() {
            dbConnect();
            global $db;
            
            $this->team0->teamName = "team task test team";
            $this->team0->teamLocationName = "san diego";
            $this->team0->teamDescription = "team task test team";
            $this->team0->teamManaged = 0;
            
            $this->teamId = $this->team0->InsertTeam('vuda1');
            
            $this->teamEvent->teamEventName = "unit test for team event";
            $this->teamEvent->teamEventDescription = "unit testing team event class";
            $this->teamEvent->teamEventLocationString = "whateer";
            $this->teamEvent->teamEventStartTime = '2014-1-24 9:20';
            $this->teamEvent->teamEventEndTime = '2014-1-24 10:20';            
            $this->teamEvent->teamEventTeamId = $this->teamId;
        }
        
        public function testTeamEventInit() {
            $teamEvent = new TeamEvent;
            
            $this->assertEquals(-999,$teamEvent->teamEventId);
            $this->assertEquals("", $team->teamEventName);
            $this->assertEquals("", $team->teamEventDescription);
            $this->assertEquals("",$team->teamEventLocationString);
            $this->assertEquals('2013-12-31 0:0',$team->teamEventStartTime);
            $this->assertEquals('2013-12-31 0:0',$team->teamEventEndtime);
            $this->assertEquals(-999,teamEventTeamId);
        }
        
        public function testInsertEvent() {
            $this->teamEventId = $this->teamEventInsertEvent();
            $this->assertTrue($this->teamEventId > 0);            
        }
        
        public function testSelectAllEvents() {
            $result = $this->teamEvent->SelectAllEvents()
            $this->assertTrue($result != NULL);
        }
        
        public function testSelectEventFromEventId() {
            $result = $this->teamEvent->SelectEventFromEventId($this->teamEventId)
            $this->assertTrue($result != NULL);
        }
        
        
        
    }
    
    
?>