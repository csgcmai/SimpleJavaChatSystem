<?php
class UserAction extends Action{
	
	
	public function index(){
		
		$database = M('user');
		$userinformation = $_SESSION['onlineuser'];
		
		$data['name'] = $userinformation['name'];
		$data['usertype'] = $userinformation['usertype'];
		
		$this->assign('username',$data['name']);
		
		$result = $database->field('name,real_name,company_name,phone,email,comment')->where($data)->select();
		
		foreach($result[0] as $key => $value){
			$this->assign($key,$value);
		}
		
		$this->display();
	}
	
	public function edit(){
		$database = M('user');
		$userinformation = $_SESSION['onlineuser'];
		
		$data['name'] = $userinformation['name'];
		$data['usertype'] = $userinformation['usertype'];
		
		$this->assign('username',$data['name']);
		
		$result = $database->field('name,real_name,company_name,phone,email,comment')->where($data)->select();
		
		foreach($result[0] as $key => $value){
			$this->assign($key,$value);
		}
		$this->display();
	}
	
	public function update(){
		
		$database = M('user');
		
		$userinformation = $_SESSION['onlineuser'];
		$data['name'] = $userinformation['name'];
		$data['usertype'] = $userinformation['usertype'];
		
		$update = $_POST;

		$result = $database->where($data)->save($update);
		
		$this->redirect('index');
	}
	
	public function  modifypw(){
		$userinformation = $_SESSION['onlineuser'];
		$data['name'] = $userinformation['name'];
		$this->assign('username',$data['name']);
		$this->display();
	}
	
	public function setpw(){
		$database = M('user');
		
		$userinformation = $_SESSION['onlineuser'];
		$data['name'] = $userinformation['name'];
		$data['usertype'] = $userinformation['usertype'];
		$data['password'] = $_POST['oldpassword'];
		
		$update['password'] = $_POST['newpassword2'];
		
		$result = $database->where($data)->select();
		if(!$result){
			$this->assign('run','setfailed()');
			$this->display();
		}else{
			$result = $database->where($data)->save($update);
			$this->assign('run','setsuccess()');
			$this->display();
		}
			
	}
}