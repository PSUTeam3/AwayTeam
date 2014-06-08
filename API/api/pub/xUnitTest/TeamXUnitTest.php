<?php
	class TeamXUnitTest extends PHPUnit_Framework_TestCase
	{
		public function testUpdateTeam1() {
			$teamId = -999;			
			$updateResult = updateTeam($teamId);			
			$this->assertEquals(false, $updateResult);
		}
		
		public function testUpdateTeam2() {
			$teamId = 2;			
			$updateResult = updateTeam($teamId);			
			$this-> assertEquals(true, $updateResult);			
		}
		
		public function testSelectTeamFromId()  {
			
		}
	}
?>