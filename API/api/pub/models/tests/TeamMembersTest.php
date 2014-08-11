<?php

    require_once('/home/awayteam/api/pub/apiconfig.php');
    require_once('/home/awayteam/api/pub/models/TeamMembers.php');
    require_once('/home/awayteam/api/pub/models/Team.php');
    require_once('/home/awayteam/api/pub/models/User.php');
    
    class TeamMembersXUnitTest extends PHPUnit_Framework_TestCase
    {
        var $team0;
        var $teamMembers0;
        var $team0Id;
        var $user;
        var $teamMemberId;
        
        public function setUp() {
            dbConnect();
            global $db;    
            
            $this->team0 = new Team;
            $this->team0->teamName = "team member unit test";
            $this->team0->teamDescription = "who dat going to beat those saints";
            $this->team0->teamLocationName = "la jolla";
            $this->team0->teamManaged = "false";
            
            $user = new User;
            $this->user = $user->SelectUserFromLoginID('vuda1');
            
            $this->teamMembers0 = new TeamMembers;
            $this->teamMembers0->teamId = $this->team0Id;
            $this->teamMembers0->userId = $this->user->userId;
            $this->teamMembers0->manager = "false";
            $this->teamMembers0->pendingApproval = "false";          
        }
        
        public function testTeamMembersInit() {
            $result = $this->team0->InsertTeam('vuda1');
            $this->teamMemberId = $result['teamMemberId'];
            $this->team0Id = $result['teamId'];
            
            $teamMembers = new TeamMembers;            

            $this->assertEquals(-999,$teamMembers->teamMemberId);
            $this->assertEquals(-999,$teamMembers->teamId);
            $this->assertEquals(-999,$teamMembers->userId);
            $this->assertEquals(false,$teamMembers->manager);
            $this->assertEquals(false,$teamMembers->pendingApproval);
        }        

        
        public function testInsertTeamMember() {            
            $result = $this->teamMembers0->InsertTeamMember();
            $this->assertTrue($result >0);
        }
        
        
        public function testAddTeamMemberNonManager() {
            $result = NULL;
            $team = $this->team0->SelectTeamFromTeamName($this->team0->teamName);
            $result = $this->teamMembers0->AddteamMember($team[0]->teamId,'karski');
            $this->assertTrue($result > 0);
        }
        
        
        public function testSelectTeamMemberFromId() {
            $team = $this->team0->SelectTeamFromTeamName($this->team0->teamName);
            $teamMemberId = $this->teamMembers0->AddteamMember($team[0]->teamId,'naimols');
            $result = $this->teamMembers0->SelectTeamMemberFromId($teamMemberId);
            $this->assertTrue($result != NULL);
        }
        
        public function testSelectTeamMemberFromTeamId() {
            $team = $this->team0->SelectTeamFromTeamName($this->team0->teamName);
            $result = $this->teamMembers0->SelectTeamMemberFromTeamId($team[0]->teamId);
            $this->assertNotEmpty($result);
        }
        
        public function testModifyTeamMember() {
            $teamMember1 = new TeamMembers;
            $newUser = new User;
            
            $newUser->loginId = "testModifyTeamMemberUser";
            $newUser->email   = "test@test.com";
            $newUser->firstName = "john";
            $newUser->lastName = "doe";
            $newUser->password = "1234";
            $newUser->cellPhone = "5555555555";
            $newUser->emergencyPhone = "55555555";
            
            $id = $newUser->InsertUser();
        
            $team = $this->team0->SelectTeamFromTeamName($this->team0->teamName);
            $teamMember1->teamId = $team[0]->teamId;
            $teamMember1->userId = $id;
            $teamMember1->manager = false;
            $teamMember1->pendingApproval = false;

            $teamMember1->teamMemberId = $teamMember1->InsertTeamMember();
            $teamMember1->pendingApproval = true;
            $teamMember1->manager = true;
            
            $result = $teamMember1->ModifyTeamMember();
            $this->assertTrue($result != NULL);
        }
        
        
        public function testModifyManagerAttribute() {
            $team = $this->team0->SelectTeamFromTeamName($this->team0->teamName);
            $teamMemberId = $this->teamMembers0->AddteamMember($team[0]->teamId,'naimols');
            $result = $this->teamMembers0->ModifyManagerAttribute($teamMemberId,0);
            $this->assertTrue($result != NULL);
        }
        
        
        public function testModifyPendingApproval() {
            $team = $this->team0->SelectTeamFromTeamName($this->team0->teamName);
            $teamMemberId = $this->teamMembers0->AddteamMember($team[0]->teamId,'naimols');
            $result = $this->teamMembers0->ModifyPendingApproval($teamMemberId,0);
            $this->assertTrue($result != NULL);
        }
        
        
        public function testModifyTeamMemberTeamId() {
            $team1 = new Team;
            $newUser = new User;
            $newTeamMember = new TeamMembers;

            $team1->teamName = "team member modify team id test";
            $team1->teamDescription = "who dat going to beat those saints";
            $team1->teamLocationName = "la jolla";
            $team1->teamManaged = "false";
            
            $newUser->loginId = "testModifyTeamMemberTeamIdUser";
            $newUser->email   = "testewr@test.com";
            $newUser->firstName = "john whatever";
            $newUser->lastName = "doe";
            $newUser->password = "123412332432";
            $newUser->cellPhone = "5555555522255";
            $newUser->emergencyPhone = "55555522255";
            
            $id = $newUser->InsertUser();
            
            $result = $team1->InsertTeam('testModifyTeamMemberTeamIdUser');
            $newTeamMember->teamMemberId = $result['teamMemberId'];
            $team1->teamId = $result['teamId'];    

            $team2 = $team1->SelectTeamFromTeamName("team member unit test");
            
            $result = $this->teamMembers0->ModifyTeamMemberTeamId($newTeamMember->teamMemberId,$team2[0]->teamId);
            $this->assertTrue($result != false);
            
            $this->teamMembers0->ModifyTeamMemberTeamId($newTeamMember->teamMemberId,$team1->teamId);
            $team1->DeleteTeam($team1->teamId);
            $newUser->DeleteUser($id);
        }
        
        /*
        public function testTeamMemberIdExists() {
            $result = $this->teamMembers0->TeamMemberIdExists($this->teamMemberId);
            $this->assertTrue($result == true);
        }       

        public function testVerifyManagerForUser() {
            $result = $this->teamMembers0->VerifyManagerForUser($this->team0Id, $this->user->userId);
            $this->assertTrue($result == 0);
        }        

        public function testVerifyTeamMemberExist() {
            $result = $this->teamMembers0->VerifyTeamMemberExist($this->team0Id, $this->user->userId);
            $this->assertTrue($result != NULL);
        }
        
        public function testGetNumberOfTeamMembersRemaining() {
            $result = $this->teamMembers0->GetNumberOfTeamMembersRemaining($this->team0Id);
            $this->assertTrue($result >= 0);
        }
        
        public function testGetNumberOfTeamManager() {
            $result = $this->teamMembers0->GetNumberOfTeamManager($this->team0Id);
            $this->assertTrue($result >= 0);
        }
        
        public function testDeleteTeamMember() {
            $result = $this->teamMembers0->DeleteTeamMember($this->team0Id, $this->user->userId);
            $this->assertTrue($result != NULL);
        }
        
        public function testDeleteTeamMemberRemove() {
            $result = $this->teamMembers0->DeleteTeamMemberTeamRemove($this->team0Id, $this->user->userId);
            $this->assertTrue($result == 2);
        }*/
    }
?>
