<? php
    
    require_once('/home/awayteam/api/pub/models/TeamMembers.php');
    require_once('/home/awayteam/api/pub/apiconfig.php');
    
    class TeamMemberController extends TeamMembers
    {
        public function CreateTeamMember($teamMemberParametersArray) {
            $tTeamMember = new TeamMembers;
            $tTeamMember = $this->arrayToObject($teamMemberParametersArray);
            $newTeamMemberId = $tTeamMember->InsertTeamMember();
        }
    }
?>