<?php

    //OWNER: S. NAIMOLI

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
        public $userSecret;
        public $userIdentifier;
        public $userSalt;
        
        public function __construct()
        {
            $this->initialize();
        }
        
        public function initialize()
        {
            $this->userId           = -999;
            $this->email            = "";
            $this->password         = "";
            $this->firstName        = "";
            $this->lastName         = "";
            $this->loginId          = "";
            $this->cellPhone        = "";
            $this->emergencyPhone   = "";           
            $this->userSecret       = "";
            $this->userIdentifier   = "";
            $this->userSalt         = "";
        }

        //data functions
        public function UpdateUser($id)
        {
            global $db; //finish...
            $query = sprintf("update user set email='%s', firstName='%s', lastName='%s', cellPhone='%s', emergencyPhone='%s', loginId='%s' where userId=%d", 
            myEsc($this->email), 
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
            $tUser = new User;

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
            }
            
            return $tUser;
        }

        public function SelectUserFromLoginID($loginId)
        {
            global $db;
            $tUser = new User; 

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

            } 
            //if a user is found it will send the information
            //if a user isnt found it will send a blank user where the id is -999...
            //-999 will indicate a not found state.
            return $tUser;
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

        public function ValidatePasswordHash($password)
        {
            $realHash       = $this->password;
            $salt           = $this->userSalt;
            $submittedHash  = hash('sha256', $salt . $password);

            if ($realHash === $submittedHash)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        public function ValidateAuthenticationChallange($loginId, $userIdentifier, $challengeHash)
        {
            global $db;
            $serverTime     = getTime();
            $startTime      = $serverTime - 30;
            $stopTime       = $serverTime + 30;

            //select userSecret,userIdentifier from user where loginId=$loginId and userIdentifier='$userIdentifier'
            $query = sprintf("select userSecret, userIdentifier from user where loginId='%s' and userIdentifier='%s'",
                myEsc($loginId),
                myEsc($userIdentifier));

            $userIdentifer      = "";
            $userSecret         = "";

            $sql = mysql_query($query, $db);
            if (mysql_num_rows($sql) > 0)
            {
                $result = array();
                while ($rlt = mysql_fetch_array($sql, MYSQL_ASSOC))
                {
                    $result[] = $rlt;
                }

                $userIdentifer      = $result[0]['userIdentifier'];
                $userSecret         = $result[0]['userSecret'];
                //challengeHash should eq hmac('sha256', timestamp(within 30sec) . $loginId . $userIdentifier, $userSecret) 
                $i = 0;
                for ($i=$startTime; $i<=$stopTime; $i++)
                {
                    if ($challengeHash === hash_hmac('sha256', $i . $loginId . $userIdentifier, $userSecret))
                    {
                        return true;
                    }
                }
                return false; //no matches
            }
            {
                //failed
                return false;
            }
            return false; //something went wrong
        }

        
        public function ChangeUserPassword($newpassword)
        {
            global $db;
            if ($this->userId == -999)
            {
                return false;
            }

            $secArr = $this->GenerateSecrets($newpassword);
    
            $this->userSalt         = $secArr['salt'];
            $this->password         = $secArr['password'];
            $this->userIdentifier   = $secArr['identifier'];
            $this->userSecret       = $secArr['secret'];

            $query = sprintf("update user set password='%s', userIdentifier='%s', userSalt='%s', userSecret='%s' where userId='%s'",
                myEsc($this->password),
                myEsc($this->userIdentifier),
                myEsc($this->userSalt),
                myEsc($this->userSecret),
                myEsc($this->userId));
        
            mysql_query($query, $db);
            
            return true;
        }

        public function GenerateSecrets($password)
        {
            $timestamp      = getTime();            
            $randbits       = openssl_random_pseudo_bytes(64);
            $salt           = hash('sha256', $timestamp . $randbits);

            $encPassword    = hash('sha256', $salt . $password);

            $timestamp      = getTime();            
            $randbits       = openssl_random_pseudo_bytes(64);
            $identifier     = hash('sha256', $timestamp . $randbits);

            $timestamp      = getTime();            
            $randbits       = openssl_random_pseudo_bytes(64);
            $randbits       = openssl_random_pseudo_bytes(64);
            $randbits       = openssl_random_pseudo_bytes(64);
            $secret         = hash('sha256', $timestamp . $salt . $randbits);

            $retArr['salt']         = $salt;
            $retArr['password']     = $encPassword;
            $retArr['identifier']   = $identifier;
            $retArr['secret']       = $secret;

            return $retArr;
        }

        public function InsertUser()
        {
            if ($this->loginId == "")
            {
                return -999;
            }
            global $db;

            $secArr = $this->GenerateSecrets($this->password);
        
            $this->userSalt         = $secArr['salt'];
            $this->password         = $secArr['password'];
            $this->userIdentifier   = $secArr['identifier'];
            $this->userSecret       = $secArr['secret'];

            $query = sprintf("insert into user (email,loginId,password,firstname,lastName,cellPhone,emergencyPhone,userSalt,userIdentifier,userSecret) values ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')",
                myEsc($this->email),
                myEsc($this->loginId),
                myEsc($this->password),
                myEsc($this->firstName),
                myEsc($this->lastName),
                myEsc($this->cellPhone),
                myEsc($this->emergencyPhone),
                myEsc($this->userSalt),
                myEsc($this->userIdentifier),
                myEsc($this->userSecret));

            $id = -999;
            mysql_query($query, $db);
            $id = mysql_insert_id();

            return $id;
        }
    }
?>
