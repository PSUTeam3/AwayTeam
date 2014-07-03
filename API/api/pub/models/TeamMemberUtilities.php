<?php

    require_once('/home/awayteam/api/pub/apiconfig.php');
    
    class TeamMemberUtilities
    {
        public function TeamMemberIdExists($id) {
            global $db;
            if($id) {
                $query = "select count(id) as num from team_member where id = " . myEsc($id);
                $sql = mysql_query($query, $db);
                $data = mysql_fetch_assoc($sql);
                
                if ($data['num'] == 0) {
                    return false;
                } else {
                    return true;
                }   
            }
        }
    }
?>