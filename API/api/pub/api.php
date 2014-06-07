<?php
    //OWNER: S. NAIMOLI

    header("Access-Control-Allow-Origin: *");
    
    require_once("Rest.inc.php");
    require_once("controllers/UserController.php");
    
    class API extends REST 
    {
    
        public $data = "";
        
        
        /*
         * Public method for access api.
         * This method dynmically call the method based on the query string
         *
         */
        public function processApi()
        {
            $func = strtolower(trim(str_replace("/","_",$_REQUEST['rquest'])));
            list ($group, $myFunc) = explode("_", $func);
            $myFunc = $group . "_" . $myFunc;
            if((int)method_exists($this,$myFunc) > 0)
                $this->$myFunc();
                #$this->$func();
            else
                $this->response('',404);                
            // If the method not exist with in this class, response would be "Page not found".
        }

        private function User_AuthenticatePassword()
        {
            $xUser = new UserController;
            $failure = true;
    
            if ($this->get_request_method() != "POST")
            {
                $this->response('', 406);
            }        
            
            $authArray = $this->_request;

            if (isset($authArray['loginId']))
            {
                $xUser = $xUser->GetUserFromLoginID($authArray['loginId']);
                if ($xUser->userId <> "-999")
                {
                    if (isset($authArray['password']))
                    {   
                        //validate hash
                        $good = $xUser->ValidatePasswordHash($authArray['password']);
                        if ($good)
                        {
                            $retArray = array ('response' => 'success', 'userIdentifier' => $xUser->userIdentifier, 'userSecret' => $xUser->userSecret);
                            $failure = false;
                        }
                        else
                        {
                            //bad password
                            $retArray = array ('response' => 'failure', 'message' => 'bad password');
                            $failure = true;
                        }
                    }   
                    else
                    {   
                        //did not submit password
                        $failure = true;
                        $retArray = array ('response' => 'failure', 'message' => 'password not submitted');
                    }   
                }
                else
                {
                    //user not found
                    $failure = true;
                    $retArray = array ('response' => 'failure', 'message' => 'user not found');
                }
                
            }   
            else
            {
                //did not submit user
                $failure = true;
                $retArray = array ('response' => 'failure', 'message' => 'user not submitted');
            }

            if ($failure)
            {
                $this->response($this->json($retArray),401);
            }
            else
            {
                $this->response($this->json($retArray),200);
            }            
             
            // expect loginId and password
            // return userIdentifier and userSecret
        }       

        private function User_ChangePassword()
        {
            //MUST BE AUTHENTICATED
            $xUser = new UserController;
            $failed = true;
            if ($this->get_request_method() != "POST")
            {
                $this->response('', 406);
            }

            $info = $this->_request;
            
            //check authentication; assign user; change password.
            if (isset($info['loginId']))
            {
                //if challenge failed
                //{
                //['AWT-AUTH'] aka $userIdentifier
                //['AWT-AUTH-CHALLENGE'] aka $challenge
                //}
                $xUser = $xUser->GetUserFromLoginID($info['loginId']);
                if ($xUser->userId <> "-999")
                {
                    if (isset($info['newPassword']))
                    {
                        $ret = $xUser->ChangeUserPassword($info['newPassword']);
                        if ($ret)
                        {
                            //change successful
                            $retArray = array ('response' => 'success', 'message' => 'password changed');
                            $failed = false;
                        }
                        else
                        {
                            //change failed
                            $retArray = array ('response' => 'failed', 'message' => 'password change failed');
                            $failed = true;
                        }   
                    }
                    else
                    {
                        //new password not set
                        $retArray = array ('response' => 'failed', 'message' => 'newPassword not set');
                        $failed = true;
                    }
                }
                else
                {
                    //user not found
                    $retArray = array ('response' => 'failed', 'message' => 'user not found');
                    $failed = true;
                }
            }
            else
            {
                //user not submitted
                $retArray = array ('response' => 'failed', 'message' => 'user not submitted');
                $failed = true;
            }

            if ($failed)
            {
                $this->response($this->json($retArray),401);
            }
            else
            {
                $this->response($this->json($retArray),200);
            }

        }
 
        private function User_CreateUser()
        {
            $xUser = new UserController;
            
            if ($this->get_request_method() != "POST")
            {
                $this->response('', 406);
            }
            //pass user info as array
            $usrArray = $this->_request;
            $newUid = $xUser->CreateUser($usrArray);
           
            $jsonMsg = array(); 
            if ($newUid > -999)
            {
                $jsonMsg = array('status' => 'success', 'response'=> $newUid);
            }
            else
            {
                $jsonMsg = array('status' => 'failure', 'response'=> -999);
            }

            $this->response($this->json($jsonMsg),200);
        }

        private function User_GetUser()
        {
            //GetUserFromLoginID
            
            $xUser = new UserController;
            $failure = true;

            if ($this->get_request_method() != "GET")
            {
                $this->response('',406);
            }
    
            if (isset($_GET['loginId']))
            {
                $loginId = $_GET['loginId'];
                $xUser = $xUser->GetUserFromLoginID($loginId);
            }
            elseif (isset($_GET['userId']))
            {
                $userId = $_GET['userId'];
                $xUser = $xUser->GetUserFromID($userId);
            }
        
            if ($xUser->userId == -999)
            {
                $failure = true;
            }
            else
            {
                $failure = false;
            }

            if ($failure == true)
            {
                $resp = array('status' => "failure", 'response' => "user not found");
            }
            else
            {
                //convert object to array for json
                $xUser = get_object_vars($xUser);
                unset($xUser['userSalt']);
                unset($xUser['userIdentifier']);
                unset($xUser['userSecret']);
                unset($xUser['password']);
                $resp = array('status' => "success", 'response' => $xUser);            
            }
            
            $this->response($this->json($resp),200);
        }

        private function User_ModifyUser()
        {
            $xUser = new UserController;

            if($this->get_request_method() != "POST")
            {
                $this->response('',406);
            }

            $array1         = $this->_response;
            $response       = $xUser->ModifyUser($array1);

            $jsonstr        = array('status' => $response);

            $this->response($this->json($jsonstr),200);

        }

        /*
         *  Encode array into JSON
        */
        private function json($data)
        {
            if(is_array($data))
            {
                return json_encode($data);
            }
        }
    }
    
    // Initiiate Library if rest call

    if ($_SERVER['PHP_SELF'] == '/api.php')
    {
        $api = new API;
        $api->processApi();
    }

    function callAPIFunction($name, $args)
    {
        $tAPI = new API();
        $ref = new ReflectionClass($tAPI);
        $refMethod = $ref->getMethod($name);
        $refMethod->setAccessible(true);

        return $refMethod->invoke($tAPI, $args);
    }
    

    function xhandler($number,$string,$file,$line,$context)
    {
        //log to text file?

        //log to xml file?

        //store in database?

        //whatever you want to do!


        $stuff = "number: " . $number . " string: " . $string . " file: " . $file . " line: " . $line . " context: " . $context . "\n";

        file_put_contents ('/tmp/phplogtest.txt', $stuff, FILE_APPEND | LOCK_EX);
    }

    set_error_handler('xhandler',E_ALL);

?>
