<?php
    //OWNER: S. NAIMOLI

    require_once('/home/awayteam/api/pub/models/Expense.php');
    require_once('/home/awayteam/api/pub/apiconfig.php');

    //logic functions
    class ExpenseController extends Expense
    {
        public function ModifyExpense($expArray)
        {
            $tExpense = new Expense;
            $tExpense = $this->arrayToObject($expArray);
            $code     = $tExpense->UpdateExpense(); 
            
            //send return code
    
            return $code;
        }

        public function CreateExpense($expArray)
        {
            $tExpense = new Expense;
            $tExpense = $this->arrayToObject($expArray);
            $newExp   = $tExpense->InsertExpense();            

            return $newExp;
        }

        public function RemoveExpense()
        {
            return $this->DeleteExpense($this->expenseId);
        }

        public function GetExpense($criteria)
        {
            if (isset($criteria['expenseId']))
            {
                logIt("expenseId");
                $arr = $this->SelectExpense($criteria['expenseId'], $criteria['userId'], $criteria['teamId']);
            }            
            elseif (isset($criteria['reqDate']))
            {
                logIt("reqDate");
                $arr = $this->SelectExpensesByDate($criteria['reqDate'], $criteria['userId'], $criteria['teamId']);
            } 
            elseif (isset($criteria['reqType']))
            {
                logIt("reqType");
                $arr = $this->SelectExpensesByType($criteria['reqType'], $criteria['userId'], $criteria['teamId']);
            }
            else
            {
                logIt("default");
                $arr = $this->SelectExpenses($criteria['userId'], $criteria['teamId']);
            }
            return $arr;
        }

        public function GetReceipt($expenseId)
        {
            return $this->SelectReceipt($expenseId);
        }

        public function PutReceipt($expenseId)
        {
            return $this->ApplyReceipt($expenseId);
        }

        private function arrayToObject($array)
        {
            $tExpense = new Expense;
            //convertArray to User Object
            foreach($array as $item=>$value)
            {
                $tExpense->$item = $value;
            }
            
            return $tExpense;
        }
    }
?>
