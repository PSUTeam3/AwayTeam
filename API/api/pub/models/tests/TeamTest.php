<?php
    require_once('/home/awayteam/api/pub/apiconfig.php');
    require_once('/home/awayteam/api/pub/models/Team.php');
    
    class TeamXUnitTest extends PHPUnit_Framework_TestCase
    {
        public function setUp() {
            dbConnect();
        }
        
        public function testTeamInit(){
            $team = new Team;
            
            $this->assertEquals(-999, $team->teamId);
            $this->assertEquals("", $team->teamName);
            $this->assertEquals("", $team->teamLocationName);
            $this->assertEquals("", $team->teamDescription);
            $this->assertEquals(false,$team->teamManaged);
        }
        
        public function testInsertTeam() {
            $team = new Team;
            $teamId = $team->InsertTeam('vuda1');
            $this->assertTrue($teamId> 0);
        }
        
        public function testSelectAllTeams() {
            $team = new Team;
            $result = $team->SelectAllTeams();
            $this->assertNotEmpty($result);
        }
        
        public function testSelectTeamFromId() {
            $team = new Team;
            $result = NULL;
            $result = $team ->SelectTeamFromId(34,'vuda1');
            $this->assertNotNull($result);
        }
        
        public function testSelectTeamFromTeamName() {
            $team = new Team;
            $result = NULL;
            $result = $team->SelectTeamFromTeamName('testCreateTrigger','vuda1');
            $this->assertNotNull($result);
        }
        
        public function testGetTeamList() {
            $team = new Team;
            $result = NULL;
            $result = $team->GetTeamList(8);
            $this->assertNotNull($result);
        }
        
        public function testModifyTeamModel() {
            $team = new Team;
            $team->teamName = "unitModifyTeamModelTest";
            $team->teamLocationName = "Orlando";
            $team->teamDescription = "unitTestingChanges";
            $team->teamManaged = 0;
            $result = NULL;
            $result = $team->ModifyTeamModel();
            $this->assertNotNull($result);
        }
        
        public function testModifyTeamNameModel() {
            $team = new Team;
            $result = NULL;
            $result = $team->ModifyTeamNameModel(34,"unitTestModifyTeamNameModel");
            $this->assertNotNull($result);
        }
        
        public function testDeleteTeam() {
            $team = new Team;
            $result = NULL;
            $result = $team->DeleteTeam(34);
            $this->assertFalse($result);
        }
    }
?>