<?php
    require_once('/home/awayteam/api/pub/apiconfig.php');
    require_once('/home/awayteam/api/pub/models/TeamTasks.php');
    require_once('/home/awayteam/api/pub/models/Team.php');
    
    class TeamTasksXUnitTest extends PHPUnit_Framework_TestCase
    {
        var $teamTask0;
        var $teamId;
        var $team0;
        var $teamTaskId;
        
        public function setUp() {
            dbConnect();
            global $db;
            
            $this->team0->teamName = "team task test team";
            $this->team0->teamLocationName = "san diego";
            $this->team0->teamDescription = "team task test team";
            $this->team0->teamManaged = 0;
            
            $this->teamId = $this->team0->InsertTeam('vuda1');
            
            $this->teamTask0->taskTitle = "unit test create task";
            $this->teamTask0->taskDescription = "unit testing team task class";
            $this->teamTask0->taskCompleted = false;
            $this->teamTask0->taskTeamId = $this->teamId;
        }
        
        public function testTeamInit(){
            $team = new Team;
            
            $this->assertEquals(-999, $team->teamId);
            $this->assertEquals("", $team->teamName);
            $this->assertEquals("", $team->teamLocationName);
            $this->assertEquals("", $team->teamDescription);
            $this->assertEquals(false,$team->teamManaged);
        }
        
        public function testInsertTeamTasks() {            
            $this->teamTaskId = $this->teamTask0->InsertTeamTasks();
            $this->assertTrue($this->teamTaskId> 0);
        }
        
        public function testModifyTeamTask() {            
            $this->teamTask0->taskTitle = "unit testing modify team task";
            $this->teamTask0->taskDescription = "unit testing modify team task";
            $result = $this->teamTask0->ModifyTeamTask();
            $this->assertTrue($result != NULL);
        }
        
        public function testMarkTeamTaskComplete() {
            $result  $this->teamTask0->MarkTeamTaskComplete($this->teamTaskId,"true");
            $this->assertTrue($result != NULL);
        }
        
        public function testDeleteTeamTask() {          
            $result = $this->teamTask0->DeleteTeamTask($this->teamTaskId);
            $this->assertNotNull($result);
        }
        
        public function testSelectTeamTasks() {
            $result = $this->teamTask0->SelectTeamTasks($this->teamId);
            $this->assertNotNull($result);
        }       
    }
?>