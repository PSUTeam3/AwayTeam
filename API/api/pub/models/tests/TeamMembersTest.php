<?php

/*
Test Results from July 13, 2014:

THIS TEST WILL FAIL AFTER FIRST RUN BECAUSE OF DELETION AND CHANGING OF TEAM IDs

root@awayteamDev:/home/awayteam/Desktop/php-5.4.30# php phpunit.phar /home/awayteam/api/pub/models/tests/TeamMembersTest.php
PHPUnit 4.1.3 by Sebastian Bergmann.

............

Time: 53 ms, Memory: 2.75Mb

OK (12 tests, 16 assertions)
root@awayteamDev:/home/awayteam/Desktop/php-5.4.30# 

*/
    require_once('/home/awayteam/api/pub/apiconfig.php');
    require_once('/home/awayteam/api/pub/models/TeamMembers.php');
    require_once('/home/awayteam/api/pub/models/Team.php');
    require_once('/home/awayteam/api/pub/models/User.php');
    
    class TeamMembersXUnitTest extends PHPUnit_Framework_TestCase
    {
        var $team0;
        var $teamMembers0;
        var $team0Id;
        var $userId;   
        var $teamMemberId;
        
        public function setUp() {
            dbConnect();
            global $db;    
            
            $this->team0 = new Team;
            $this->team0->teamName = "chargers";
            $this->team0->teamDescription = "who dat going to beat those saints";
            $this->team0->teamLocationName = "la jolla";
            $this->team0->teamManaged = "false";
            
            $team0Id = $this->team0->InsertTeam('vuda1');
            $user = $this->user->SelectUserFromLoginID('vuda1');
            
            $this->teamMembers0 = new TeamMembers;
            $this->teamMembers0->teamId = $team0Id;
            $this->teamMembers0->userId = $user->userId;
            $this->teamMembers0->manager = "false";
            $this->teamMembers0->pendingApproval = "false";          
        }
        
        public function testTeamMembersInit() {
            $teamMembers = new TeamMembers;            

            $this->assertEquals(-999,$teamMembers->teamMemberId);
            $this->assertEquals(-999,$teamMembers->teamId);
            $this->assertEquals(-999,$teamMembers->userId);
            $this->assertEquals(false,$teamMembers->manager);
            $this->assertEquals(false,$teamMembers->pendingApproval);
        }        

        public function testInsertTeamMember() {            
            $this->teamMemberId = $this->teamMembers0->InsertTeamMember();            
            $this->assertTrue($this->teamMemberId >0);
        }
        
        public function testAddTeamMemberNonManager() {
            $result = NULL;            
            $result = $this->teamMembers0->AddteamMember($this->team0Id,'vuda1');
            $this->assertTrue($result > 0);
        }
        
        public function testAddFirstTeamMember() {
            $result = $this->teamMembers0->AddFirstTeamMember($this->team0Id,'vuda1');
            $this->assertTrue($result > 0);
        }
        
        public function testSelectTeamMemberFromId() {
            $result = $this->teamMembers0->SelectTeamMemberFromId($this->teamMemberId);
            $this->assertTrue($result != NULL);
        }
        
        public function testSelectTeamMemberFromTeamId() {
            $result = $this->teamMembers0->SelectTeamMemberFromTeamId($this->team0Id);
            $this->assertNotEmpty($result);
        }
        
        public function testModifyTeamMember() {
            $this->teamMembers0->manager = "false";
            $this->teamMembers0->pendingApproval = "true"; 
            
            $result = $this->teamMembers0->ModifyTeamMember();
            $this->assertTrue($result != NULL);
        }
        
        public function testModifyManagerAttribute() {
            $result = $this->teamMembers0->ModifyManagerAttribute($this->teamMemberId,0);
            $this->assertTrue($result != NULL);
        }
        
        public function testModifyPendingApproval() {
            $result = $this->teamMembers0->ModifyPendingApproval($this->teamMemberId,0);
            $this->assertTrue($result != NULL);
        }
        
        public function testModifyTeamMemberTeamId() {
            $result = $this->teamMembers0->ModifyTeamMemberTeamId($this->teamMemberId,1);
            $this->assertTrue($result != NULL);
        }
        
        public function testTeamMemberIdExists() {
            $result = $this->teamMembers0->TeamMemberIdExists($this->teamMemberId);
            $this->assertTrue($result == true);
        }       

        public function testVerifyManagerForUser() {
            $result = $this->teamMembers0->VerifyManagerForUser($this->team0Id,$this->userId);
            $this->assertTrue($result == 0);
        }        

        public function testVerifyTeamMemberExist() {
            $result = $this->teamMembers0->VerifyTeamMemberExist($this->team0Id,$this->userId);
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
            $result = $this->teamMembers0->DeleteTeamMember($this->team0Id,$this->userId);
            $this->assertTrue($result != NULL);
        }
        
        public function testDeleteTeamMemberRemove() {
            $result = $this->teamMembers0->DeleteTeamMemberTeamRemove($this->team0Id,$this->userId);
            $this->assertTrue($result == 2);
        }
    }
?>