<?php
    require_once('/home/awayteam/api/pub/apiconfig.php');
    require_once('/home/awayteam/api/pub/models/TeamMembers.php');
    
    class TeamMembersXUnitTest extends PHPUnit_Framework_TestCase
    {
        public function setUp() {
            dbConnect();
        }
        
        public function testTeamMembersInit() {
            $teamMembers = new TeamMembers;            

            $this->assertEquals(-999,$teamMembers->teamMemberId);
            $this->assertEquals(-999,$teamMembers->teamId);
            $this->assertEquals(-999,$teamMembers->userId);
            $this->assertEquals(false,$teamMembers->manager);
            $this->assertEquals(false,$teamMembers->pendingApproval);
        }
        
        public function testInsertTeamMember1() {
            $teamMembers = new TeamMembers;
            
            $teamMembers->teamId = 12;
            $teamMembers->userId = 8;
            $teamMembers->manager = 1;
            $teamMembers->pendingApproval = "false";
            
            $result = $teamMembers->InsertTeamMember();
            
            $this->assertTrue($result >0);
        }
        
        
    }
?>