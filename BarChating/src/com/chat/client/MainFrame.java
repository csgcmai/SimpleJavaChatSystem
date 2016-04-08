package com.chat.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.awt.Font;

public class MainFrame extends JFrame implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6642927944418496643L;
	private JPanel contentPane;
	private LoginFrame loginFrame;
	private JPopupMenu friendMenu = new JPopupMenu();
	private PropUtils myPropUtils = null;
	private PropUtils configPropUtils = new PropUtils("config");
	
	JMenuItem chatItem = null;
	JMenuItem ignoItem = null;
	JMenuItem caniItem = null;
	JMenuItem deleItem = null;
	
	private String groupName[] = new String[]{
			"在线好友",
			"在线工作好友",
			"离线好友",
			"已屏蔽"
	};
	
	private final int onlineGroupId = 0, workGroupId = 1, offlineGroupId = 2, ignoreGroupId = 3;
	private JGroupPanel groupPanel = null;
	
	private String name = null;
	private Socket socket = null;
	private final int port = Integer.parseInt(configPropUtils.getString("port"));
	private final String address = configPropUtils.getString("address");
	private DataInputStreamU16 in = null;
	private DataOutputStreamU16 out = null;
	private Thread thread = null;
	
	
	private Set<String> onlineFriend = new HashSet<String>();
	private Set<String> workFriend = new HashSet<String>();
	private Set<String> offlineFriend = new HashSet<String>();
	private Set<String> ignoreFriend = new HashSet<String>();
	private HashMap<String,ChatFrame> chatlist = new HashMap<String,ChatFrame>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					//frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\img\\icon.png"));
		setTitle("BarChat");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JButton button = new JButton("\u6DFB\u52A0\u5230\u597D\u53CB\u5217\u8868");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String tName = JOptionPane.showInputDialog(MainFrame.this, "请输入你所要添加的用户").trim();
				if(tName.equals(null)||tName.equals("")) return;
				offlineFriend.add(tName);
				
				if(myPropUtils.addUser(tName, "friend")){
					JButton tempBt = new JButton(tName);
					tempBt.addMouseListener(friendButtonListener);
					groupPanel.addMember(offlineGroupId, tempBt);
					try {
						out.writeUTF16("query -u " + tName + "#");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		button.setFont(new Font("宋体", Font.BOLD, 12));
		contentPane.add(button, BorderLayout.SOUTH);
		
		int w = (Toolkit.getDefaultToolkit().getScreenSize().width - 450) ;
		int h = (Toolkit.getDefaultToolkit().getScreenSize().height - 600) / 2;
		
		setLocation(w,h);
		setVisible(false);
		initData();
	}
	
	private void initData(){
		
		try {
			socket = new Socket(address,port);
			in = new DataInputStreamU16(socket.getInputStream());
			out = new DataOutputStreamU16(socket.getOutputStream());
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(MainFrame.this, "连接超时,请检查您的网络连接状态或防火墙设置...");
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(MainFrame.this, "连接超时,请检查您的网络连接状态或防火墙设置...");
			e.printStackTrace();
		}
		
		loginFrame = new LoginFrame(out);
		loginFrame.setVisible(true);
		
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	
	private void initComponent(){
		
		chatItem = new JMenuItem("发起会话");
		ignoItem = new JMenuItem("屏蔽好友");
		caniItem = new JMenuItem("取消屏蔽");
		deleItem = new JMenuItem("删除好友");
		
		groupPanel = new JGroupPanel(groupName);
		
		onlineFriend = new HashSet<String>();
		workFriend = new HashSet<String>();
		offlineFriend = new HashSet<String>();
		ignoreFriend = new HashSet<String>();
		chatlist = new HashMap<String,ChatFrame>();
		
		
		
		contentPane.add(groupPanel);
		groupPanel.expandGroup(onlineGroupId);
		
		friendMenu.add(chatItem);
		friendMenu.add(deleItem);
		
		chatItem.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				
				String tName = ((JButton)friendMenu.getInvoker()).getText();
				
				if(chatlist.containsKey(tName)){
					((ChatFrame)chatlist.get(tName)).setVisible(true);
				}
				else chatlist.put(tName, new ChatFrame(MainFrame.this,name,tName,out));
				
				if(workFriend.contains(tName)){
					chatlist.get(tName).workBox.setSelected(true);
				}
			}
			
		});
		
		deleItem.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				String tName = ((JButton)friendMenu.getInvoker()).getText().trim();
				
				int i = JOptionPane.showConfirmDialog(MainFrame.this, "你确认要删除好友 " + tName + " 吗？");
				if(i!=JOptionPane.OK_OPTION) return;
				
				if(chatlist.containsKey(tName)){
					((ChatFrame)chatlist.get(tName)).dispose();
					((ChatFrame)chatlist.get(tName)).setVisible(false);
					chatlist.remove(tName);
				}
				
				if(onlineFriend.contains(tName)) {
					onlineFriend.remove(tName);
					myPropUtils.deleteUser(tName, "friend");
					
					int comCount = groupPanel.getMemberCount(onlineGroupId);
					for(int index=0;index<comCount;index++){
						if(((JButton)groupPanel.getMember(onlineGroupId, index)).getText().equals(tName)){
							groupPanel.removeMember(onlineGroupId, index);
							break;
						}
					}
				}
				
				if(workFriend.contains(tName)) {
					workFriend.remove(tName);
					myPropUtils.deleteUser(tName, "friend##work");
					
					int comCount = groupPanel.getMemberCount(workGroupId);
					for(int index=0;index<comCount;index++){
						if(((JButton)groupPanel.getMember(workGroupId, index)).getText().equals(tName)){
							groupPanel.removeMember(workGroupId, index);
							break;
						}
					}
				}
				
				if(offlineFriend.contains(tName)) {
					offlineFriend.remove(tName);
					myPropUtils.deleteUser(tName, "friend");
					myPropUtils.deleteUser(tName, "friend##work");
					
					int comCount = groupPanel.getMemberCount(offlineGroupId);
					for(int index=0;index<comCount;index++){
						if(((JButton)groupPanel.getMember(offlineGroupId, index)).getText().equals(tName)){
							groupPanel.removeMember(offlineGroupId, index);
							break;
						}
					}
				}
				
				if(ignoreFriend.contains(tName)) {
					ignoreFriend.remove(tName);
					myPropUtils.deleteUser(tName, "ignore");
					
					int comCount = groupPanel.getMemberCount(ignoreGroupId);
					for(int index=0;index<comCount;index++){
						if(((JButton)groupPanel.getMember(ignoreGroupId, index)).getText().equals(tName)){
							groupPanel.removeMember(ignoreGroupId, index);
							break;
						}
					}
				}
				
			}
		});
		
		caniItem.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				String tName = ((JButton)friendMenu.getInvoker()).getText().trim();
				
				ignoreFriend.remove(tName);
				offlineFriend.add(tName);
				
				myPropUtils.updateUser(tName, "friend" , "ignore");

				int comCount = groupPanel.getMemberCount(ignoreGroupId);
				for(int index=0;index<comCount;index++){
					JButton tempBt = (JButton)groupPanel.getMember(ignoreGroupId, index);
					if(tempBt.getText().equals(tName)){
						groupPanel.removeMember(ignoreGroupId, index);
						groupPanel.addMember(offlineGroupId, tempBt);
						break;
					}
				}
				
				try {
					out.writeUTF16("query -u " + tName + "#");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
		ignoItem.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				String tName = ((JButton)friendMenu.getInvoker()).getText().trim();
				
				if(offlineFriend.contains(tName)){
					offlineFriend.remove(tName);
					int comCount = groupPanel.getMemberCount(offlineGroupId);
					for(int index=0;index<comCount;index++){
						JButton tempBt = (JButton)groupPanel.getMember(offlineGroupId, index);
						if(tempBt.getText().equals(tName)){
							groupPanel.removeMember(offlineGroupId, index);
							groupPanel.addMember(ignoreGroupId, tempBt);
							myPropUtils.updateUser(tName, "ignore" , "friend");
							myPropUtils.updateUser(tName, "ignore" , "friend##work");
							break;
						}
					}
				} else if(onlineFriend.contains(tName)){
					onlineFriend.remove(tName);
					int comCount = groupPanel.getMemberCount(onlineGroupId);
					for(int index=0;index<comCount;index++){
						JButton tempBt = (JButton)groupPanel.getMember(onlineGroupId, index);
						if(tempBt.getText().equals(tName)){
							groupPanel.removeMember(onlineGroupId, index);
							groupPanel.addMember(ignoreGroupId, tempBt);
							myPropUtils.updateUser(tName, "ignore" , "friend");
							break;
						}
					}
				} else if(workFriend.contains(tName)){
					workFriend.remove(tName);
					int comCount = groupPanel.getMemberCount(workGroupId);
					for(int index=0;index<comCount;index++){
						JButton tempBt = (JButton)groupPanel.getMember(workGroupId, index);
						if(tempBt.getText().equals(tName)){
							groupPanel.removeMember(workGroupId, index);
							groupPanel.addMember(ignoreGroupId, tempBt);
							myPropUtils.updateUser(tName, "ignore" , "friend##work");
							break;
						}
					}
				}
				ignoreFriend.add(tName);
			}
			
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					out.writeUTF16("logout -u " + name);
					out.close();
					in.close();
					socket.close();
				} catch (ConnectException ex) {
					ex.printStackTrace();
					System.err.println("连接关闭失败！");
				} catch (IOException ex) {
					ex.printStackTrace();
					System.err.println("流关闭失败！");
				} finally {
					dispose();
					System.exit(0);
				}
			}
		});
	}
	
	private void initFriend(){
		
		HashMap<String,String> temList = myPropUtils.getAllUserInfo();
		Set<?> keyset = temList.keySet();
		
		@SuppressWarnings("unchecked")
		Iterator<String> it = (Iterator<String>) keyset.iterator();
		
		while(it.hasNext()){
			String name = ((String)it.next()).trim();
			
			JButton btMember = new JButton(name);
			btMember.addMouseListener(friendButtonListener);
			btMember.setFocusPainted(false); 
			
			String type =temList.get(name).split("##")[0];
			if(type.equals("friend")){
				offlineFriend.add(name);
				groupPanel.addMember(offlineGroupId, btMember);
				
			} else if(temList.get(name).equals("ignore")){
				ignoreFriend.add(name);
				groupPanel.addMember(ignoreGroupId, btMember);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void run() {
		String recvStr = null, tempStr[] = null;
		int index = 0;
		while(true){
			try {
				recvStr = in.readUTF16().trim();
				tempStr = recvStr.split(" ");
			} catch (IOException e) {
				onlineFriend.clear();
				offlineFriend.clear();
				workFriend.clear();
				ignoreFriend.clear();
				
				groupPanel.removeGroup(onlineGroupId);
				groupPanel.removeGroup(offlineGroupId);
				groupPanel.removeGroup(workGroupId);
				groupPanel.removeGroup(ignoreGroupId);
				
				initFriend();
				JOptionPane.showMessageDialog(MainFrame.this, "网络连接超时，程序退出，请重新登陆");
				System.exit(0);
//				setVisible(false);
//				
//				Set<?> keyset = chatlist.keySet();
//				
//				@SuppressWarnings("unchecked")
//				Iterator<String> it = (Iterator<String>) keyset.iterator();
//				
//				while(it.hasNext()){
//					String name = ((String)it.next()).trim();
//					chatlist.get(name).dispose();
//					chatlist.get(name).setVisible(false);
//				}
//				
//				loginFrame.setVisible(true);
//				e.printStackTrace();
			}
			JButton tempBt = null;
			int length;
			switch(tempStr[0]){
			case "login":
				if(tempStr[1].equals("-r")&&tempStr[2].equals("success")){
					name = loginFrame.getName();
					myPropUtils = new PropUtils(name);
					initComponent();
					initFriend();
					loginFrame.setVisible(false);
					setTitle("BarChat: "+name);
					setVisible(true);
					queryOnlineUser();
				}else{
					JOptionPane.showMessageDialog(MainFrame.this, "用户名或密码错误，请检查并重新登陆");
				}
				break;
			case "send":
				if(tempStr[1].equals("-s")&&tempStr[3].equals("-t")&&tempStr[4].equals(name)){
					if(ignoreFriend.contains(tempStr[2]))
					{
						try {
							out.writeUTF16("reject -s " + name + " -t " + tempStr[2] + " -w chat");
							break;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(tempStr[5].equals("-w")&&tempStr[6].equals("work")){
						if(!workFriend.contains(tempStr[2])){
							int statu = JOptionPane.showConfirmDialog(MainFrame.this, "是否同意对方以工作模式发起聊天？");
							if(statu == JOptionPane.OK_OPTION ){
								if(onlineFriend.contains(tempStr[2])){
									friendToWork(tempStr[2]);
								}
							} else {
								try {
									out.writeUTF16("reject -s " + name + " -t " + tempStr[2] + " -w work");
									break;
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
					ChatFrame tempFrame;
					if(chatlist.containsKey(tempStr[2])){
						tempFrame = chatlist.get(tempStr[2]);
						tempFrame.setVisible(true);
					} else {
						tempFrame = new ChatFrame(MainFrame.this,name,tempStr[2],out);
						chatlist.put(tempStr[2], tempFrame);
						tempFrame.setVisible(true);
						
					}
					
					if(tempStr[6].equals("work")){
						tempFrame.workBox.setSelected(true);
						
						if(!tempFrame.isActive()){
							Runnable workTalkTips = new WorkTalkTips(
									JRootPane.INFORMATION_DIALOG,
									"友情提示",
									"有人给你发送了工作模式数据...<br><strong>欢迎您使用BarChar.</strong><br>",
									new JLabel(new ImageIcon("images/logo.png")));
							Thread thread = new Thread(workTalkTips);
							thread.start();
						}
					}
					
					int len = tempStr.length;
					for(int i = 9; i<len; i++){
						tempStr[8]+= " " + tempStr[i];
					}
					
					if(tempFrame.workBox.isSelected()&& !tempStr[6].equals("work")){
						tempFrame.setRecvMessage(tempStr[2]+ (":(闲聊数据)") +tempStr[8]);
					} else {
						tempFrame.setRecvMessage(tempStr[2]+": "+tempStr[8]);
					}
				}
				//处理接收到发送失败的事件
				if(tempStr[3].equals("-r")&&tempStr[4].equals("failed")&&tempStr[1].equals("-t")){
					
					JOptionPane.showMessageDialog(MainFrame.this, "用户" + tempStr[2] + "已经下线，发送失败");
					ChatFrame tempFrame;
					if(chatlist.containsKey(tempStr[2])){
						tempFrame = chatlist.get(tempStr[2]);
						tempFrame.setVisible(false);
						tempFrame.dispose();
					}
					
					if(onlineFriend.contains(tempStr[2])){
						onlineFriend.remove(tempStr[2]);
						offlineFriend.add(tempStr[2]);
						int comCount = groupPanel.getMemberCount(onlineGroupId);
						for(int j=0;j<comCount;j++){
							tempBt = (JButton)groupPanel.getMember(onlineGroupId, j);
							if(tempBt.getText().equals(tempStr[2])){
								groupPanel.removeMember(onlineGroupId, j);
								groupPanel.addMember(offlineGroupId, tempBt);
								break;
							}
						}
					}
					
					if(workFriend.contains(tempStr[2])){
						workFriend.remove(tempStr[2]);
						offlineFriend.add(tempStr[2]);
						int comCount = groupPanel.getMemberCount(workGroupId);
						for(int j=0;j<comCount;j++){
							tempBt = (JButton)groupPanel.getMember(workGroupId, j);
							if(tempBt.getText().equals(tempStr[2])){
								groupPanel.removeMember(workGroupId, j);
								groupPanel.addMember(offlineGroupId, tempBt);
								break;
							}
						}
					}
				}
				break;
			case "query":
				if(tempStr.length<=2) break;
				String nameList[] = tempStr[2].split("#");
				length = nameList.length;
				for(int i=0;i<length;i++){
					offlineFriend.remove(nameList[i]);
					int comCount = groupPanel.getMemberCount(offlineGroupId);
					for(int j=0;j<comCount;j++){
						tempBt = (JButton)groupPanel.getMember(offlineGroupId, j);
						if(tempBt.getText().equals(nameList[i])){
							groupPanel.removeMember(offlineGroupId, j);
							break;
						}
					}
					switch(myPropUtils.getString(nameList[i])){
					
					case "friend":
						onlineFriend.add(nameList[i]);
						groupPanel.addMember(onlineGroupId, tempBt);
						break;
					case "friend##work":
						workFriend.add(nameList[i]);
						groupPanel.addMember(workGroupId, tempBt);
						break;
					default:
						break;
					}
				}
				break;
			case "newuser":
				if(!tempStr[1].equals("-u")) break;
				if(!offlineFriend.contains(tempStr[2])) break;
				offlineFriend.remove(tempStr[2]);
				int comCount = groupPanel.getMemberCount(offlineGroupId);
				for(int j=0;j<comCount;j++){
					tempBt = (JButton)groupPanel.getMember(offlineGroupId, j);
					if(tempBt.getText().equals(tempStr[2])){
						groupPanel.removeMember(offlineGroupId, j);
						break;
					}
				}
				switch(myPropUtils.getString(tempStr[2])){
				
				case "friend":
					onlineFriend.add(tempStr[2]);
					groupPanel.addMember(onlineGroupId, tempBt);
					break;
				case "friend##work":
					workFriend.add(tempStr[2]);
					groupPanel.addMember(workGroupId, tempBt);
					break;
				default:
					break;
				}
				
				break;
			case "logout":
				if(!tempStr[1].equals("-u")) break;
				
				ChatFrame tempFrame;
				if(chatlist.containsKey(tempStr[2])){
					JOptionPane.showMessageDialog(MainFrame.this, "用户" + tempStr[2] + "已经下线，和他的对话框即将关闭");
					tempFrame = chatlist.get(tempStr[2]);
					tempFrame.setVisible(false);
					tempFrame.dispose();
				}
				
				if(onlineFriend.contains(tempStr[2])){
					onlineFriend.remove(tempStr[2]);
					offlineFriend.add(tempStr[2]);
					comCount = groupPanel.getMemberCount(onlineGroupId);
					for(int j=0;j<comCount;j++){
						tempBt = (JButton)groupPanel.getMember(onlineGroupId, j);
						if(tempBt.getText().equals(tempStr[2])){
							groupPanel.removeMember(onlineGroupId, j);
							groupPanel.addMember(offlineGroupId, tempBt);
							break;
						}
					}
				}
				
				if(workFriend.contains(tempStr[2])){
					workFriend.remove(tempStr[2]);
					offlineFriend.add(tempStr[2]);
					comCount = groupPanel.getMemberCount(workGroupId);
					for(int j=0;j<comCount;j++){
						tempBt = (JButton)groupPanel.getMember(workGroupId, j);
						if(tempBt.getText().equals(tempStr[2])){
							groupPanel.removeMember(workGroupId, j);
							groupPanel.addMember(offlineGroupId, tempBt);
							break;
						}
					}
				}
				
				break;
				
			case "reject":
				if(tempStr[1].equals("-s")&&tempStr[3].equals("-t")&&tempStr[4].equals(name)){
					if(tempStr[5].equals("-w")&&tempStr[6].equals("work")){
						int statu = JOptionPane.showConfirmDialog(MainFrame.this, "用户" + tempStr[2] + "已经屏蔽或者拒绝您的工作模式数据，确定以普通消息发送给对方");
						if(statu==JOptionPane.OK_OPTION){
							tempFrame = chatlist.get(tempStr[2]);
							tempFrame.workBox.setSelected(false);
						}
						
					}
					else JOptionPane.showMessageDialog(MainFrame.this, "用户" + tempStr[2] + "已经屏蔽或者拒绝您的消息，对方将无法查看您发送的任何消息...");
					break;
				}
				
				//处理接收到发送失败的事件
				if(tempStr[3].equals("-r")&&tempStr[4].equals("failed")&&tempStr[1].equals("-t")){
					
					JOptionPane.showMessageDialog(MainFrame.this, "用户" + tempStr[2] + "已经下线，拒绝失败");
					
					if(chatlist.containsKey(tempStr[2])){
						tempFrame = chatlist.get(tempStr[2]);
						tempFrame.setVisible(false);
						tempFrame.dispose();
					}
					
					if(onlineFriend.contains(tempStr[2])){
						onlineFriend.remove(tempStr[2]);
						offlineFriend.add(tempStr[2]);
						comCount = groupPanel.getMemberCount(onlineGroupId);
						for(int j=0;j<comCount;j++){
							tempBt = (JButton)groupPanel.getMember(onlineGroupId, j);
							if(tempBt.getText().equals(tempStr[2])){
								groupPanel.removeMember(onlineGroupId, j);
								groupPanel.addMember(offlineGroupId, tempBt);
								break;
							}
						}
					}
					
					if(workFriend.contains(tempStr[2])){
						workFriend.remove(tempStr[2]);
						offlineFriend.add(tempStr[2]);
						comCount = groupPanel.getMemberCount(workGroupId);
						for(int j=0;j<comCount;j++){
							tempBt = (JButton)groupPanel.getMember(workGroupId, j);
							if(tempBt.getText().equals(tempStr[2])){
								groupPanel.removeMember(workGroupId, j);
								groupPanel.addMember(offlineGroupId, tempBt);
								break;
							}
						}
					}
				}
				break;
			}
		}
	}
	
	private MouseListener friendButtonListener = new MouseListener(){

		@SuppressWarnings("deprecation")
		public void mouseClicked(MouseEvent arg0) {
			
			String tName = ((JButton)arg0.getComponent()).getText();
			
			
			if(arg0.getClickCount()==2 && arg0.getModifiers()==InputEvent.BUTTON1_MASK){
				
				if(!(workFriend.contains(tName)||onlineFriend.contains(tName))) return;
				if(chatlist.containsKey(tName)){
					((ChatFrame)chatlist.get(tName)).setVisible(true);
				}
				else chatlist.put(tName, new ChatFrame(MainFrame.this,name,tName,out));
				
				if(workFriend.contains(tName)){
					chatlist.get(tName).workBox.setSelected(true);
				}
				
			}
			
			if(arg0.getModifiers()==InputEvent.BUTTON3_MASK){
				
				if(ignoreFriend.contains(tName)){
					friendMenu.remove(chatItem);
					friendMenu.remove(ignoItem);
					friendMenu.add(caniItem);
				} else {
					friendMenu.remove(caniItem);
					friendMenu.add(ignoItem);
					friendMenu.add(chatItem);
					if(offlineFriend.contains(tName))
						friendMenu.remove(chatItem);
				}
				
				friendMenu.setVisible(true);
				friendMenu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			}
			
		}

		public void mouseEntered(MouseEvent arg0) {
			
		}

		public void mouseExited(MouseEvent arg0) {
			
		}

		public void mousePressed(MouseEvent arg0) {
			
		}

		public void mouseReleased(MouseEvent arg0) {
			
		}
		
	};
	
	@SuppressWarnings("rawtypes")
	private void queryOnlineUser(){
		Iterator it = offlineFriend.iterator();
		StringBuilder sb = new StringBuilder("query -u ");
		while(it.hasNext()){
			sb.append(it.next()+"#");
		}
		try {
			out.writeUTF16(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void friendToWork(String tName){
		onlineFriend.remove(tName);
		workFriend.add(tName);
		int comCount = groupPanel.getMemberCount(onlineGroupId);
		for(int index=0;index<comCount;index++){
			JButton tempBt = (JButton)groupPanel.getMember(onlineGroupId, index);
			if(tempBt.getText().equals(tName)){
				groupPanel.removeMember(onlineGroupId, index);
				groupPanel.addMember(workGroupId, tempBt);
				myPropUtils.updateUser(tName, "friend##work" , "friend");
				break;
			}
		}
	}
	
	public void workToFriend(String tName){
		workFriend.remove(tName);
		onlineFriend.add(tName);
		int comCount = groupPanel.getMemberCount(workGroupId);
		for(int index=0;index<comCount;index++){
			JButton tempBt = (JButton)groupPanel.getMember(workGroupId, index);
			if(tempBt.getText().equals(tName)){
				groupPanel.removeMember(workGroupId, index);
				groupPanel.addMember(onlineGroupId, tempBt);
				myPropUtils.updateUser(tName, "friend##work" , "friend");
				break;
			}
		}
	}
}
