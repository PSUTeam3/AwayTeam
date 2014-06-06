<?php

	include_once('/home/awayteam/api/pub/apiconfig.php');
	//include db config

	class User 
	{
		//class attributes
		public $userId;
		public $email;
		public $password;
		public $firstName;
		public $lastName;
		public $loginId;
		public $cellPhone;
		public $emergencyPhone;		
		
		public function __construct()
		{
			$this->initialize();
		}
		
		public function initialize()
		{
			$this->userId 		= -999;
			$this->email 		= "";
			$this->password 	= "";
			$this->firstName 	= "";
			$this->lastName 	= "";
			$this->loginId 		= "";
			$this->cellPhone 	= "";
			$this->emergencyPhone 	= "";			
		}

		//data functions
		public function UpdateUser($id)
		{
			global $db; //finish...
			$query = sprintf("update user set email='%s', password='%s', firstName='%s', lastName='%s', cellPhone='%s', emergencyPhone='%s', loginId='%s' where userId=%d", 
			myEsc($this->email), 
			myEsc($this->password),
			myEsc($this->firstName),
			myEsc($this->lastName),
			myEsc($this->cellPhone),
			myEsc($this->emergencyPhone),
			myEsc($this->loginId),
			myEsc($this->userId));
			
			//send back code if successful or not
			$sql = mysql_query($query, $db);	
			
			return $sql;
		}

		public function SelectUserFromID($id)
		{
			global $db;
			if ($id)
			{
				$query = "select * from user where userId=" . myEsc($id);
			}

			$sql = mysql_query($query, $db);
			if (mysql_num_rows($sql) > 0)
			{
				$result = array();
				while ($rlt = mysql_fetch_array($sql, MYSQL_ASSOC))
				{
					$result[] = $rlt;
				}

				//convert array to object
				foreach($result[0] as $item=>$value)
				{
					$tUser->$item = $value;
				}
				return $tUser;
			}
			
		}

                public function SelectUserFromLoginID($loginId)
                {
					global $db;
					if ($loginId)
					{
							$query = "select * from user where loginId='" . myEsc($loginId) . "'";
					}

					$sql = mysql_query($query, $db);
					//file_put_contents ('/tmp/phplogtest.txt', $query . "\n", FILE_APPEND | LOCK_EX);

					if (mysql_num_rows($sql) > 0)
					{
						$result = array();
						while ($rlt = mysql_fetch_array($sql, MYSQL_ASSOC))
						{
								$result[] = $rlt;
						}

						//convert array to object
						foreach($result[0] as $item=>$value)
						{
								$tUser->$item = $value;
						}

							return $tUser;
					}
                }


		public function DeleteUser($id)
		{
			global $db;
			if ($id)
			{
					$query = "delete from user where userId=" . myEsc($id);
			}

			$sql = mysql_query($query, $db);
			return $result;
		}

		public function InsertUser()
		{
			global $db;

			$query = sprintf("insert into user (email,loginId,password,firstname,lastName,cellPhone,emergencyPhone) values ('%s','%s','%s','%s','%s','%s','%s')",
						myEsc($this->email),
						myEsc($this->loginId),
						myEsc($this->password),
						myEsc($this->firstName),
						myEsc($this->lastName),
						myEsc($this->cellPhone),
						myEsc($this->emergencyPhone));


			$id = -999;
			mysql_query($query, $db);
			$id = mysql_insert_id();

			return $id;
		}
	}
?>
