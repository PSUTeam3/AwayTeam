<?php
    //OWNER: S. NAIMOLI

	require_once('/home/awayteam/api/pub/models/User.php');
	require_once('/home/awayteam/api/pub/apiconfig.php');

	//logic functions
	class UserController extends User
	{
		public function ModifyUser($usrArray)
		{
			
			$tUser = new User;
			$tUser = arrayToObject($usrArray);
			$code = $tUser->UpdateUser();
			
			//send return code
	
			return $code;
		}

		public function CreateUser($usrArray)
		{
			$tUser = new User;

			$tUser = arrayToObject($usrArray);

			$newUid = $tUser->InsertUser();
			//gets new UserID #

			return $newUid;
		}

		public function DeleteUser($user)
		{
			return $this->DeleteUser($user->userId);
		}

		public function GetUserFromID($userId)
		{
			return $this->SelectUserFromID($userId);
		}

        public function GetUserFromLoginID($loginId)
        {       
            $arr = $this->SelectUserFromLoginID($loginId);
            return $arr;	
        }

		private function arrayToObject($array)
		{
			$tUser = new User;
            //convertArray to User Object
            foreach($usrArray as $item=>$value)
            {
                    $tUser->$item = $value;
            }
			
			return $tUser;
		}
	}
?>
