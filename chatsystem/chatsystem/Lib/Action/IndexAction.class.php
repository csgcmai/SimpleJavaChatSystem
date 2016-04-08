<?php
// 本类由系统自动生成，仅供测试用途
class IndexAction extends Action {
    public function index(){
    	unset($_SESSION);
    	$this->display();
	}
    
    
    public function verify(){
    	$usertype = $_POST['usertype'];
    	$name = $_POST['name'];
    	$password = $_POST['password'];
    	
    	if(($name != null)&&($password!=null)){
    		$database = M('user');
    		
    		$data['name'] = $name;
    		$data['password'] = $password;
    		
    		if($usertype == null) $usertype=0;
    		
    		$data['usertype'] = $usertype;
    		dump($data);
    		
    		$result = $database->where($data)->select();
    		
    		dump($result);
    		if($result!=null){
    			$usermessage = $result['0'];
    			$data['name'] = $usermessage['name'];
    			$_SESSION['onlineuser'] = $data;
    			
    			if($usertype==0)
    				$this->redirect('index.php/User/index');
    			if($usertype==2)
    				$this->redirect('index.php/Admin/index');
    		}
    		else $this->redirect('index.php/Index/alert');
    	}else $this->redirect('index.php/Index/alert');
    }
    
    public function alert(){
    	$this->display();
    }
    
    public function register(){
    	 $code="134";
    	 if($_SESSION['code']==null||$_SESSION['code'] == ""){
    	 	$code = rand();
    	 	$code = substr(base_convert($code,10,16),0,6);
    	 	while(strlen($code)<6) $code.=rand(0,9);
    	 	$_SESSION['code'] = $code;
    	 }else $code = $_SESSION['code'];
    	
		 $this->assign('code',$code);    

		 import('ORG.Crypt.Hmac');
		 
		 $key = "aksnvvntri";
		 $correctCode = Hmac::sha1($key, $code);
		  
		 //echo $correctCode = bin2hex($correctCode);
		 
		 
		 
    	 $this->display();
    }
    
    public function reguser(){
    	
    	
    	$key = "aksnvvntri";
    	$code = $_SESSION['code'];
    	
    	import('ORG.Crypt.Hmac');

    	$correctCode = Hmac::sha1($key, $code);
    	
    	$correctCode = bin2hex($correctCode);
    	
    	$inputCode = $_POST['invicode'];
    	
    	if(strncasecmp($correctCode, $inputCode, 6)) {
    		$this->assign('run','addfailed()');
    		$this->display();
    		$_SESSION['code'] = $code;
    	}
    	
    	$database = M('user');
    	
    	$insert = $_POST;
    	
    	$insert['password'] = $insert['pw1'];
    	unset($insert['pw1']);
    	unset($insert['pw2']);
    	unset($insert['invicode']);
    	
    	$result = $database->add($insert);
    	if($result) {
    		unset($_SESSION['code']);
    		$this->assign('run', 'addsuccess()');
    	}else{
    		$this->assign('run', 'addfailedname()');
    	}
    	
    	$this->display();
    }
}