<?php
    include_once('/home/awayteam/api/pub/apiconfig.php');
    include_once('/home/awayteam/api/pub/models/Managers.php');
    
    class ManagersController extends Managers
    {
          
        public function PendingUserList($teamId) {
            return $this->GetPendingUserList($teamId);
        }
        
        public function ApprovePendingUser($teamId, $userId) {
            return $this->ApprovePendingUser($teramId,$userId);
        }
        
        public function RejectPendingUser($teamId, $userId) {
            return $this->RejectPendingUser($teamId,$userId);
        }
    }
?>