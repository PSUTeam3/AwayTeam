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
        
        public function testAddTeamMember() {
            $teamMembers = new TeamMembers;
            
            $teamId = 33;
            $loginId = 'karski';
            $result = NULL;
            $result = $teamMembers->AddteamMember($teamId,$loginId);
            $this->assertTrue($result > 0);
        }
        
        public function testAddFirstTeamMember() {
            $teamMembers = new TeamMembers;
            
            $teamId = 14;
            $loginId = 'karski';
            $result = $teamMembers->AddFirstTeamMember($teamId,$loginId);
            $this->assertTrue($result > 0);
        }
        
        public function testSelectTeamMemberFromId() {
            $teamMembers = new TeamMembers;
            
            $id = 19;
            $result=NULL;
            $result = $teamMembers->SelectTeamMemberFromId($id);
            $this->assertTrue($result != NULL);
            
        }
        
        
        
        
    }
?>