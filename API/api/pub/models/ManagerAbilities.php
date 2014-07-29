<?php
    include_once('/home/awayteam/api/pub/apiconfig.php');
    
    class Managers 
    {
          
        public function GetPendingUserList($teamId) {
            global $db;
            
            $teamMemberInfoResult = array();
            
            if($teamId) {
                $query = "select userId from team_member where teamId = " . myEsc($teamId);
                $getUserIdsSql = mysql_query($query,$db);
                if(mysql_num_rows($getUserIdsSql)) {
                    $getUserIdsSql = array();
                    while($getUserIdsSql = mysql_fetch_array($getUserIdsSql, MYSQL_ASSOC)) {
                        $query = "select * from user where userId = " . myEsc($getUserIdsSql['userId']);
                        $getUserObjectsSql = mysql_query($query, $db);
                        while($getUserListResult = mysql_fetch_object($getUserObjectsSql)) {
                            $aUser = $getUserListREsult;
                            $teamMemberInfoResult = $aUser;
                        }
                    }
                }
            }
        }
        
        public function ApprovePendingUser($teamId, $userId) {
            global $db;
            
            $query = "update team_member set pendingApproval = 0 where teamId = " . myEsc($teamId) . 
                        " and userId = " .myEsc($userId);
            
            $sql = mysql_query($query,$db);
            return $sql;
        }
        
        public function RejectPendingUser($teamId, $userId) {
            global $db;
            
            $query = "delete from team_member where teamId = " . myEsc($teamId) . " and userId = " .myEsc($userId);
            
            $sql = mysql_query($query,$db);
            return $sql;
        }
    }
?>