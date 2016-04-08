<?php

class AdminAction extends Action {
	public function index(){
		$database = M('user');
		$userinformation = $_SESSION['onlineuser'];
		
		$data['name'] = $userinformation['name'];
		$data['usertype'] = 2;
		$this->assign('username',$data['name']);
		
		$result = $database->field('name,real_name,company_name,phone,email,comment')->where($data)->select();
		
		if(!$result) $this->redirect('/Index','',1,'illegal request, redirect to login page');//防止直接使用链接攻击
		
		foreach($result[0] as $key => $value){
			$this->assign($key,$value);
		}
		
		$this->display();
	}
	
	public function edit(){
		$database = M('user');
		$userinformation = $_SESSION['onlineuser'];
	
		$data['name'] = $userinformation['name'];
		$data['usertype'] = 2;
	
		$this->assign('username',$data['name']);
	
		$result = $database->field('name,real_name,company_name,phone,email,comment')->where($data)->select();
	
		if(!$result) $this->redirect('/Index','',1,'illegal request, redirect to login page');//防止直接使用链接攻击
		
		foreach($result[0] as $key => $value){
			$this->assign($key,$value);
		}
		$this->display();
	}
	
	public function update(){
	
		$database = M('user');
	
		$userinformation = $_SESSION['onlineuser'];
		$data['name'] = $userinformation['name'];
		$data['usertype'] = 2;
	
		$result = $database->where($data)->select();
		if(!$result) $this->redirect('/Index','',1,'illegal request, redirect to login page');//防止直接使用链接攻击
		
		unset($data);
		$data['name'] = $_POST['name'];
		$update = $_POST;
		
		$result = $database->where($data)->save($update);
		if($data['name']==$userinformation['name'])
			$this->redirect('index');
		else $this->redirect('search');
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
		$data['usertype'] = 2;
		
		$this->assign('username',$data['name']);
		
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
	
	public function newuser(){
		$database = M('user');
		
		$userinformation = $_SESSION['onlineuser'];
		$data['name'] = $userinformation['name'];
		$data['usertype'] = 2;
		
		$this->assign('username',$data['name']);
		
		$result = $database->where($data)->select();
		if(!$result) $this->redirect('/Index','',1,'illegal request, redirect to login page');//防止直接使用链接攻击
		
		$this->display();
	}
	
	public function adduser(){
		$database = M('user');
		
		$userinformation = $_SESSION['onlineuser'];
		$data['name'] = $userinformation['name'];
		$data['usertype'] = 2;
		
		$this->assign('username',$data['name']);
		
		$result = $database->where($data)->select();
		if(!$result) $this->redirect('/Index','',1,'illegal request, redirect to login page');//防止直接使用链接攻击
		
		$insert = $_POST;
		$insert['password'] = $insert['pw1'];
		unset($insert['pw1']);
		unset($insert['pw2']);
		
		$result = $database->add($insert);
		if($result) {
			$this->assign('run', 'addsuccess()');
		}else $this->assign('run','addfailed()');
		
		$this->display();
	}
	
	public function search(){
		
		$database = M('user');
		
		$userinformation = $_SESSION['onlineuser'];
		$data['name'] = $userinformation['name'];
		$data['usertype'] = 2;
		
		$this->assign('username',$data['name']);
		
		$result = $database->where($data)->select();
		if(!$result) $this->redirect('/Index','',1,'illegal request, redirect to login page');//防止直接使用链接攻击
		
		
		unset($data);
		if($_POST['field']!='alluser')
			$data[$_POST['field']] = $_POST['name'];
		
		//$result = $database->field('name,usertype,real_name,company_name,phone,email')->where($data)->select();
		
		
		import('ORG.Util.Page');// 导入分页类
		$count      = $database->where($data)->count();// 查询满足要求的总记录数
		$Page       = new Page($count,10);// 实例化分页类 传入总记录数和每页显示的记录数
		$show       = $Page->show();// 分页显示输出
		// 进行分页数据查询 注意limit方法的参数要使用Page类的属性
		$list = $database->field('name,usertype,real_name,company_name,phone,email')->where($data)->order('name')->limit($Page->firstRow.','.$Page->listRows)->select();
		
		
		$gentable = '';
		foreach($list as $row){
			$gentable.='<tr>';
			foreach ($row as $key => $column){
				$gentable.='<td>';
				if($key == 'usertype'){
					if($column==2) $gentable.='管理员';
					else $gentable.='普通用户';
				} else {
					$gentable.=$column;
				}
				$gentable.='</td>';
			}
			
			$gentable.='<td>
					<form action="__ROOT__/index.php/Admin/edit01" method="post" class="user" >
						<input type="submit" name='.$row['name'].' class="edit01" value=""/>
					</form>
					<form action="__ROOT__/index.php/Admin/delete" method="post" class="user" onsubmit="return deleteConfirm()">
						<input type="submit" name='.$row['name'].' class="delete" value=""/>
					</form>
			</td>';
			$gentable.='</tr>';
		}
		
		$this->assign('table',$gentable);
		
		$this->assign('page',$show);// 赋值分页输出
		
		
		
		
		
		$this->display();
	}
	
	public function delete(){
		$database = M('user');
		
		$userinformation = $_SESSION['onlineuser'];
		$data['name'] = $userinformation['name'];
		$data['usertype'] = 2;
		
		$this->assign('username',$data['name']);
		
		$result = $database->where($data)->select();
		if(!$result) $this->redirect('/Index','',1,'illegal request, redirect to login page');//防止直接使用链接攻击
		
		unset($data);
		foreach($_POST as $key => $d){
			$data['name'] = $key;
		}
		
		$result = $database->where($data)->select();
		
		if($result[0]['usertype']==0){
			$deleRes = $database->where($data)->delete();
			if($deleRes) $this->assign('run','deletesuccess()');
		}else{
			$this->assign('run','deleteAdministrator()');
			$this->assign('adminname',$data['name']);
		}
		
		$this->display();
	}
	
	public function deleteadmin(){
		$database = M('user');
		
		$userinformation = $_SESSION['onlineuser'];
		$data['name'] = $userinformation['name'];
		$data['usertype'] = 2;
		
		$this->assign('username',$data['name']);
		
		$result = $database->where($data)->select();
		if(!$result) $this->redirect('/Index','',1,'illegal request, redirect to login page');//防止直接使用链接攻击
		
		$data['password'] = $_POST['password'];
		$result = $database->where($data)->select();
		if(!$result) {
			$this->assign('run', 'incorrecteditorpassword()');
			$this->display();
		}
		
		unset($data);
		
		$data['name'] = $_POST['adminname'];
		$data['password'] = $_POST['adminpassword'];
		$data['usertype'] = 2;
		
		$result = $database->where($data)->delete();
		if(!$result) $this->assign('run', 'incorrectdeletedpassword()');
		
		else $this->assign('run', 'deleteadminsuccess()');
		
		$this->display();
	}
	
	public function edit01(){
		$database = M('user');
		
		$userinformation = $_SESSION['onlineuser'];
		$data['name'] = $userinformation['name'];
		$data['usertype'] = 2;
		
		$this->assign('username',$data['name']);
		
		$result = $database->where($data)->select();
		if(!$result) $this->redirect('/Index','',1,'illegal request, redirect to login page');//防止直接使用链接攻击
		
		unset($data);
		
		foreach($_POST as $key => $d){
			$data['name'] = $key;
		}
		
		$result = $database->field('name,real_name,company_name,phone,email,comment')->where($data)->select();
		
		foreach($result[0] as $key => $value){
			$this->assign($key,$value);
		}
		$this->display('edit');
		
		
	}
}