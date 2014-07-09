<?php
    //OWNER: S. NAIMOLI

    header("Access-Control-Allow-Origin: *");
    
    require_once("Rest.inc.php");
    require_once("controllers/UserController.php");
    require_once("controllers/ExpenseController.php");
    require_once("controllers/TeamController.php");
    require_once("controllers/TeamMemberController.php");
    
    require_once("externalControllers/FoursquareController.php");
    
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


//==========================================DO NOT EDIT ABOVE=======================================

        private function Team_CreateTeam() {
            $newTeam = new TeamController;
            $jsonMsg = array();
            $failure = false;
            
            if($this->get_request_method() != "POST") {
                $this->response('',406);
            }   
    
            $teamArray = $this->_request;
            
            $authUser = $this->AuthRequired($teamArray);
            
            if($authUser) {
                $jsonMsg = array('status' => 'failure', 'response' => "authentication is missing");
                $failure = true;
            } else if(!isset($info['teamName'])) {
                $jsonMsg = array('status' => 'failure', 'response'=> "teamName is not filled in");
                $failure = true;
            } else if(!isset($info['teamLocationName'])) {
                $jsonMsg = array('status' => 'failure', 'response' => "teamLocationName is not filled in");                
                $failure = true;
            } else if(!isset($info['teamManaged'])) {
                $jsonMsg = array('status' => 'failure', 'response' => "teamManaged is not filled in");
                $failure = true;
            }
            
            if($failure == false) {
                if(TeamNameUsed($info['teamName'])) {
                    $jsonMsg = array('status' => 'failure', 'response'=> "team name is already used");
                    $failure = true;
                }
            }
            
            if($failure == false) {
                $newTeamId = $newTeam->CreateTeam($teamArray);
            }    
            
            if ($newTeamId > 0) {
                $jsonMsg = array('status' => 'success', 'response'=> $newTeamId);
            } 
    
            $this->response($this->json($jsonMsg), 200);
        }   
    
        private function Team_GetAllTeams() {
            $selectTeam = new TeamController;
                
            if($this->get_request_method() != "POST") {
                $this->response('',406);
            }   
    
            $selectTeam = $selectTeam->GetAllTeams();    
            
            if(!empty($selecTeam)) { 
                $jsonMsg = array('status' => 'success', 'response' => $selectTeam);
            } else {
                $jsonMsg = array('status' => 'failure', 'response' => "no teams registered");
            }
            
            $this->response($this->json($jsonMsg), 200);
        }
        private function Team_GetTeam() 
        {
            //multiple errors in here. you have the function requiring POST, but you are looking for GET responses.
            //mismatched function calls and parameter values
            $selectTeam = new TeamController;
            $aTeam = new Team;
            $failure = true;

            if($this->get_request_method() != "POST") 
            {
                $this->response('',406);
            }

            $info = $this->_request;            
            $authUser = $this->AuthRequired($info);

            if(isset($info['loginId']))
            {
                $username = $info['loginId'];
                if(isset($info['teamId'])) 
                {
                    $teamId = $info['teamId'];                   
                    $aTeam = $selectTeam->GetTeamFromID($info['teamId'], $info['loginId']);
                }                
            }

            if($aTeam->teamId == -999 or $aTeam->teamName=="") 
            {
                $failure=true;
            } else 
            {
                $failure=false;
            }

            if($failure == true) 
            {
                $respArray = array('status' => 'failure', 'response' => "team not found");
            } else 
            {
                $newTeam = get_object_vars($aTeam);
                $respArray = array('status' => 'success', 'response' => $newTeam);
            }

            $this->response($this->json($respArray), 200);
        }

        private function Team_GetTeamList() {
            $getTeamList = new TeamController;

            if($this->get_request_method() != "POST") {
                $this->response('',406);
            }
            
            $info = $this->_request;
            $authUser = $this->AuthRequired($info);
            
            if(isset($info['loginId'])) {              
                $teamList = $getTeamList->GetTeamListForUser($info['loginId']);
            }

            $teamListArray = get_object_vars($teamList);
            $respArray = array('status' => "success", 'response' => $teamListArray);
            $this->response($this->json($respArray), 200);

        }

        private function Team_ModifyTeam() {
            $modifyTeam = new TeamController;

            if($this->get_request_method() != "POST")
            {
                $this->response('',406);
            }
            
            $info = $this->_request;
            
            if(isset($info['loginId']) && isset($info['teamId'])) {
                $userId = $info['userId'];
                $teamId = $info['teamId'];
                if(VerifyTeamMemberExist($teamId, $userId)) {
                    $respArray = $this->_response;
                    $response = $modifyTeam->ModifyTeam($respArray);
                    $jsonstr = array('status'=>$response);

                    $this->response($this->json(jsonstr), 200);
                } else {
                    $this->response('',406);
                }
            } else {
                $this->response('',406);
            }
        }

        private function Team_ChangeTeamName() {
            $changeTeamName = new TeamController;

            if($this->get_request_method() != "POST") {
                $this->response('',406);
            }
            
            $info = $this->_request;
            
            if(isset($info['loginId']) && isset($info['teamId'])) {
                $userId = $info['userId'];
                $teamId = $info['teamId'];
                if(VerifyTeamMemberExist($teamId, $userId)) {
                    $respArray = $this->_response;
                    $response = $changeTeamName->ModifyTeamName($respArray);
                    $this->response($this->json(jsonstr), 200);
                } else {
                    $this->response($this->json(jsonstr),200);
                }
            } else {
                $this->response('', 406);
            }
        }

        private function User_EmailExist()
        {   
            $xUser = new UserController;
            $failure = true;
            $resp = false;

            if ($this->get_request_Method() != "GET")
            {   
                $this->response('', 406);
            }   
    
            if (isset($_GET['email']) && strlen($_GET['email']) > 0)
            {   
               $resp = $xUser->EmailExist($_GET['email']); 
               $failure = false;
            }   
            else
            {   
                $failure = true;
                $resp = false;
            }   

            if ($failure)
            {   
                $retArray = array ('response' => 'failure', 'message' => 'email not submitted');
                $this->response($this->json($retArray),401);
            }   
            else
            {   
                if ($resp)
                {   
                    $retArray = array ('response' => 'success', 'message' => 'not available');
                    // if true - email is used
                }   
                else
                {   
                    $retArray = array ('response' => 'success', 'message' => 'available');
                    // if false - email is not used and is available for registration
                }   
                $this->response($this->json($retArray),200);
            }   

        }   


        private function User_LoginIDExist()
        {
            $xUser = new UserController;
            $failure = true;
            $resp = false;

            if ($this->get_request_Method() != "GET")
            {
                $this->response('', 406);
            }
    
            if (isset($_GET['loginId']) && strlen($_GET['loginId']) > 0)
            {
               $resp = $xUser->LoginIDExist($_GET['loginId']); 
               $failure = false;
            }
            else
            {
                $failure = true;
                $resp = false;
            }

            if ($failure)
            {
                $retArray = array ('response' => 'failure', 'message' => 'loginId not submitted');
                $this->response($this->json($retArray),401);
            }
            else
            {
                if ($resp)
                {
                    $retArray = array ('response' => 'success', 'message' => 'not available');
                    // if true - user is taken
                }
                else
                {
                    $retArray = array ('response' => 'success', 'message' => 'available');
                    // if false - user is available
                }
                $this->response($this->json($retArray),200);
            }

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
            $authUser = $this->AuthRequired($info);

            if (isset($info['loginId']))
            {
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
                            $retArray = array ('response' => 'failure', 'message' => 'password change failed');
                            $failed = true;
                        }   
                    }
                    else
                    {
                        //new password not set
                        $retArray = array ('response' => 'failure', 'message' => 'newPassword not set');
                        $failed = true;
                    }
                }
                else
                {
                    //user not found
                    $retArray = array ('response' => 'failure', 'message' => 'user not found');
                    $failed = true;
                }
            }
            else
            {
                //user not submitted
                $retArray = array ('response' => 'failure', 'message' => 'user not submitted');
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
            logIt("creating user");
            logIt(var_export($usrArray, true)); 
            $newUid = $xUser->CreateUser($usrArray);
            $jsonMsg = array(); 
            logIt("new uid = " . var_export($newUid, true));

            if ($newUid >= 0)
            {
                $jsonMsg = array('response' => 'success', 'message'=> $newUid);
            }
            else
            {
                $jsonMsg = array('response' => 'failure', 'message'=> $newUid);
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
                $resp = array('response' => "failure", 'message' => "user not found");
            }
            else
            {
                //convert object to array for json
                $xUser = get_object_vars($xUser);
                unset($xUser['userSalt']);
                unset($xUser['userIdentifier']);
                unset($xUser['userSecret']);
                unset($xUser['password']);
                $resp = array('response' => "success", 'message' => $xUser);            
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

            $array1         = $this->_request;
            $response       = $xUser->ModifyUser($array1);

            $jsonstr        = array('response' => $response);

            $this->response($this->json($jsonstr),200);

        }

        private function Expense_CreateExpense()
        {   

            $xExpense = new ExpenseController;
    
            if ($this->get_request_method() != "POST")
            {   
                $this->response('', 406);
            }   
            $expArray = $this->_request;
            $authUser = $this->AuthRequired($expArray);
            $newExp = $xExpense->CreateExpense($expArray);

            $jsonMsg = array(); 

            if ($newExp >= 0)
            {   
                $jsonMsg = array('response' => 'success', 'message'=> $newExp);
            }   
            else
            {   
                $jsonMsg = array('response' => 'failure', 'message'=> $newExp);
            }   

            $this->response($this->json($jsonMsg),200);
        }   

        private function Expense_ModifyExpense()
        {   
            $xExpense = new ExpenseController;

            if($this->get_request_method() != "POST")
            {   
                $this->response('',406);
            }   

            $array1         = $this->_request;
            $authUser       = $this->AuthRequired($array1);
            $response       = $xExpense->ModifyExpense($array1);
            
            if ($response)
            {
                $jsonstr = array('response' => 'success', 'message' => 'expense modified');
            }
            else
            {
                $jsonstr = array('response' => 'failure', 'message' => 'expense not modified');
            }
            $this->response($this->json($jsonstr),200);
        }   

        private function Expense_DeleteExpense()
        {
            $xExpense = new ExpenseController;
            
            if ($this->get_request_method() != "POST")
            {
                $this->response('',406);
            }
            
            $array1                 = $this->_request;
            $authUser               = $this->AuthRequired($array1);
            $xExpense->expenseId    = $array1['expenseId'];
            $response               = $xExpense->RemoveExpense();

            if ($response)
            {
                $jsonstr = array('response' => 'success', 'message' => 'expense deleted');
            }
            else
            {
                $jsonstr = array('response' => 'failure', 'message' => 'expense not deleted');
            }           
            $this->response($this->json($jsonstr),200);
        }

        private function Expense_GetExpense()
        {
            $xExpense = new ExpenseController;
            if ($this->get_request_method() != "POST")
            {
                $this->response('',406);
            }

            $array1         = $this->_request;
            $authUser = $this->AuthRequired($array1);
            $results        = $xExpense->GetExpense($array1);

            if (is_array($results))
            {
               // do nothing... good stuff 
            }
            else
            {
                if ($results->expenseId == -999)
                {
                    //nothing found
                    $jsonstr = array('response' => 'no results');
                    $this->response($this->json($jsonstr),200);
                    exit;
                }
            }
            //results found
            $jsonstr = array('response' => $results);
            $this->response($this->json($jsonstr),200);

        }

        //GetReceipt(expenseId)
        //PutReceipt(expenseId)

        private function FQ_GetSpots()
        {
            $xFQ = new FoursquareController;
            
            if ($this->get_request_method() != "POST")
            {
                $this->response('',406);
            }
            
            $array1 = $this->_request;
            $results = $xFQ->FindSpot($array1);

            $jsonstr = array('response' => $results);
            $this->response($this->json($jsonstr),200);
        }

//==========================================DO NOT EDIT BELOW=======================================

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

        private function AuthRequired($info)
        {
            $xUser = new UserController;

            //check authentication; assign user; change password.
            if (isset($info['loginId']))
            {   
                if (isset($info['AWT_AUTH']) && isset($info['AWT_AUTH_CHALLENGE']))
                {   
                    $userIdentifier = $info['AWT_AUTH'];
                    $challenge      = $info['AWT_AUTH_CHALLENGE'];

                    $good = $xUser->ValidateAuthenticationChallange($info['loginId'], $userIdentifier, $challenge);

                    if ($good == false)
                    {   
                        $x = array('response' => 'failure', 'message' => 'auth required');
                        $this->response($this->json($x), 200);
                        exit;
                    }   
                    else
                    {   
                        //succesful hash
                        $authenticated['loginId'] = $info['loginId'];
                        $authenticated['userId']  = $xUser->GetUserFromID($info['loginId']);
                    
                        return $authenticated;
                    }   
                }   
                else
                {   
                    //fail
                    $x = array('response' => 'failure', 'message' => 'auth required');
                    $this->response($this->json($x), 401);
                    exit;
                }   
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
