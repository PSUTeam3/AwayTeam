<?php
    include_once('/home/awayteam/api/pub/apiconfig.php');
    
    class Manager 
    {

        public function IsManager($teamId, $userId)
        {
            global $db;

            $query = "select count(userId) as isManager from team_member where teamId=" . myEsc($teamId) . " and userId=" . myEsc($userId) . " and manager=1";
            logIt($query);
            
            $sql = mysql_query($query, $db);
            
            $data = mysql_fetch_assoc($sql);

            if ($data['isManager'] == 0)
            {   
                return false;
            }   
            else
            {   
                return true;
            }   
        }          

        public function GetPendingUsers($teamId) 
        {
            global $db;
            $pendingUsers = array();

            $query = "select user.loginId as loginId, user.firstName as firstName, user.lastName as lastName, user.email as email from user,team_member " . 
                        "where user.userId = team_member.userId and team_member.teamId=" . myEsc($teamId);

            $sql = mysql_query($query, $db);
    
            if (mysql_num_rows($sql) > 0)
            {
                while ($rlt = mysql_fetch_array($sql, MYSQL_ASSOC))
                {
                    $pendingUsers[] = $rlt;
                }
            }
            else
            {
                $pendingUsers = null;
            }

            return $pendingUsers;
        }
        
        public function ApproveUser($teamId, $userId) 
        {
            global $db;
            
            $query = "update team_member set pendingApproval = 0 where teamId = " . myEsc($teamId) . 
                        " and userId = " .myEsc($userId);
            
            $sql = mysql_query($query,$db);

            return $sql;
        }
        
        public function RemoveUser($teamId, $userId) 
        {
            global $db;
            
            $query = "delete from team_member where teamId = " . myEsc($teamId) . " and userId = " .myEsc($userId);
            
            $sql = mysql_query($query,$db);

            return $sql;
        }

        public function PromoteUser($teamId, $userId)
        {
            global $db;
            
            $query = "update team_member set manager=1 where teamId=" . myEsc($teamId) .
                      " and userId=" . myEsc($userId);
            
            $sql = mysql_query($query, $db);

            return $sql;
        }

        public function DemoteUser($teamId, $userId)
        {   
            global $db;
            
            $query = "update team_member set manager=0 where teamId=" . myEsc($teamId) .
                      " and userId=" . myEsc($userId);
            
            $sql = mysql_query($query, $db);

            return $sql;
        }   
    }
?>
