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
        
        public function testSelectTeamMemberFromTeamId() {
            $teamMembers = new TeamMembers;
            
            $id = 34;
            $result = NULL;
            $result =  $teamMembers ->SelectTeamMemberFromTeamId($id);
            $this->assertNotEmpty($result);
        }
        
        public function testModifyTeamMember() {
            $teamMembers = new TeamMembers;
            $teamMembers->teamMemberId = 1;
            $teamMembers->userId = 7;
            $teamMembers->teamId = 34;
            $teamMembers->manager = 0;
            $teamMembers->pendingApproval = 1;
            
            $result = NULL;
            $result = $teamMembers->ModifyTeamMember();
            $this->assertTrue($result != NULL);
        }
        
        public function testModifyManagerAttribute() {
            $teamMembers = new TeamMembers;
            $result = NULL;
            $result = $teamMembers->ModifyManagerAttribute(12,1);
            $this->assertTrue($result != NULL);
        }
        
        public function testModifyPendingApproval() {
            $teamMembers = new TeamMembers;
            $result = NULL;
            $result = $teamMembers->ModifyPendingApproval(19,0);
            $this->assertTrue($result != NULL);
        }
        
        public function testModifyTeamMemberTeamId() {
            $teamMembers = new TeamMembers;
            $result = NULL;
            $result = $teamMembers->ModifyTeamMemberTeamId(3,12);
            $this->assertTrue($result != NULL);
        }
        
        public function testVerifyTeamMemberExist() {
            $teamMembers = new TeamMembers;
            $result = NULL;
            $result = $teamMembers->VerifyTeamMemberExist(13,8);
            $this->assertTrue($result != NULL);
        }
        
        public function testDeleteTeamMember() {
            $teamMembers = new TeamMembers;
            $result = NULL;
            $result = $teamMembers->DeleteTeamMember(2);
            $this->assertTrue($result != NULL);
        }
    }
?>