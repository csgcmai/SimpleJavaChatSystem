package com.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import com.chat.client.*;

public class CreateServerThread extends Thread {

	Socket client;
	DataInputStreamU16 in;
	DataOutputStreamU16 out;
	
	Vector<String> loginClients;
	Vector<CreateServerThread> clientThread;
	String name = null;
	private int userid;
	Enumeration<CreateServerThread> enu;
	OnlineFrame mainFrame;
	
	public CreateServerThread(Socket s,OnlineFrame frame,Vector<String> login_clients,
			Vector<CreateServerThread> clientThread) throws IOException {
		client = s;
		mainFrame = frame;
		this.loginClients = login_clients;
		this.clientThread = clientThread;
		out = new DataOutputStreamU16(client.getOutputStream());
		in = new DataInputStreamU16(client.getInputStream());
		start();
	}
	
	public void run(){
		String recvStr = null, tempStr[] = null;
		while(true){
			try {
				recvStr = in.readUTF16();
				tempStr = recvStr.split(" ");
			} catch (IOException e) {
				userid = loginClients.indexOf(name);
				loginClients.remove(userid);
				mainFrame.deleUser(name);
				Enumeration<CreateServerThread> enun = clientThread.elements();
				while (enun.hasMoreElements()) {
					CreateServerThread th = (CreateServerThread) enun.nextElement();
					if (th != this && th.isAlive()) {
						try {
							th.out.writeUTF16("logout -u "+ name);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				clientThread.remove(this);
				return;
			}
			
			switch(tempStr[0]){
			case "login":
				if(tempStr[1].equals("-u")&&tempStr[3].equals("-p")){
					try {
						if(UserManager.login(tempStr[2], tempStr[4].substring(0, tempStr[4].length()-1)))
						{
							//避免多个用户同时登陆
							if(loginClients.contains(tempStr[2])) {
								out.writeUTF16("login -r multilogin");
								break;
							}
							name = tempStr[2];
							out.writeUTF16("login -r success");
							loginClients.add(name);
							mainFrame.addVector(UserManager.getUserInfo(tempStr[2]));
							Enumeration<CreateServerThread> enun = clientThread.elements();
							while (enun.hasMoreElements()) {
								CreateServerThread th = (CreateServerThread) enun.nextElement();
								if (th != this && th.isAlive()) {
									try {
										th.out.writeUTF16("newuser -u "+ name);
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								}
							}
							clientThread.add(this);
						}
						else out.writeUTF16("login -r failed");
					} catch (SQLException | IOException e) {
						e.printStackTrace();
					}
				}
				break;
			case "logout":
				if(tempStr[1].equals("-u")){
					userid = loginClients.indexOf(name);
					loginClients.remove(userid);
					mainFrame.deleUser(name);
					Enumeration<CreateServerThread> enun = clientThread.elements();
					while (enun.hasMoreElements()) {
						CreateServerThread th = (CreateServerThread) enun.nextElement();
						if (th != this && th.isAlive()) {
							try {
								th.out.writeUTF16("logout -u "+ name);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
					clientThread.remove(this);
				}
				return;
			
			
			case "query":
				if(tempStr.length<=2) break;
				String nameList[] = tempStr[2].split("#");
				StringBuilder sb = new StringBuilder("query -u ");
				int length=nameList.length;
				for(int i=0;i<length;i++){
					if(loginClients.contains(nameList[i]))
						sb.append(nameList[i]+"#");
				}
				try {
					out.writeUTF16(sb.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
				
			case "send":
			case "reject":
				boolean redirectFlag = false;
				if(tempStr[3].equals("-t")){
					Enumeration<CreateServerThread> enun = clientThread.elements();
					while (enun.hasMoreElements()) {
						CreateServerThread th = (CreateServerThread) enun.nextElement();
						if (th != this && th.isAlive()&&th.name.equals(tempStr[4])) {
							try {
								th.out.writeUTF16(recvStr);
								redirectFlag = true;
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
					if(redirectFlag) break;
					try {
						this.out.writeUTF16(tempStr[0]+ " -t " + tempStr[4] + " -r failed");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
				
			default:
				break;
			
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
