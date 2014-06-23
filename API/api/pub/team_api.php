<?php

    header("Access-Control-Allow-Origin: *");
    
    require_once("Rest.inc.php");
    require_once("controllers/UserController.php")
    
    class API extends REST 
    {
        public $data = "";
        
        public function processApi() {
            $func = strtolower(trim(str_replace("/","_",$_REQUEST['rquest'])));
            list ($group, $myFunc) = explode("_", $func);
            $myFunc = $group . "_" . $myFunc;
            if((int)method_exists($this,$myFunc) > 0) {
                $this->$myFunc();
            } else {
                $this->response('',404);
            }            
        }
        
        private function Team_CreateTeam() {
            $newTeam = new TeamController;
            
            if($this->get_request_method() != "POST") {
                $this->response('',406);
            }
            
            $teamArray = $this->request;
            $newTeamId = $newTeam->CreateTeam($teamArray);
            
            $jsonMsg = array('status' => 'success', 'response'=> $newTeamId);
            $this->response($this->json($jsonMsg), 200);
        }
        
        private function Team_GetTeam() {
            $selectTeam = new TeamController;
            $failure = true;
            
            if($this->get_request_method() != "POST") {
                $this->response('',406);
            }
            
            if(isset($_GET['teamId'])) {
                $teamId = $_GET['teamId'];
                if(!empty($teamId)) {            
                    $newTeam = $newTeam->GetTeamFromId($teamId);
                }
            } elseif(isset($_GET['teamName'])) {
                $teamName = $_GET['teamName'];
                if(!empty($teamName)) {
                    $newTeam = $newTeam->GetTeamFromName($teamName);
                }
            }
            
            if($newTeam->userId == -999 or $newTeam->teamName="") {
                $failure=true;
            } else {
                $failure=false;
            }
            
            if($failure == true) {
                $respArray = array('status' => "failure", 'response' => "team not found");
            } else {
                $newTeam = get_object_vars($newTeam);
                
                $respArray = array('status' => "success", 'response' => $newTeam);
            }   

            $this->response($this->json($respArray), 200);        
        }

        private function Team_ModifyTeam() {
            $modifyTeam = new TeamController;

            if($this->get_request_method() != "POST")
            {
                $this->response('',406);
            }
            
            $respArray = $this->_response;
            $response = $modifyTeam->ModifyTeam($respArray);
            $jsonstr = array('status'=>$response;
            
            $this->response($this->json(jsonstr), 200);
        }
        
        private function Team_ChangeTeamName {
            $changeTeamName = new TeamController;
            
            if($this->get_request_method() != "POST")
            {
                $this->response('',406);
            }
            
            $respArray = $this->_response;
            $response = $changeTeamName($responsse;
            $this->response($this->json(jsonstr), 200);                
        }
        
        private function json($data) {
            if(is_array($data)) {
                return json_encode($data);
            }
        }
    }
    
    if($_SERVER['PHP_SELF'] == '/team_api.php') {
        $api = new API;
        $api=>processApi();
    }
    
    function callAPIFunction($name, $args) {
        $tAPI = new API();
        $ref = new ReflectionClass($tAPI);
        $refMethod = $ref->getMethod($name);
        $refMethod->setAccessible(true);
        
        return $refMethod->invoke($tApi, $args);
    }

?>