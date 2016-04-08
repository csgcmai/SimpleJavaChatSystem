package com.chat.server;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.chat.client.PropUtils;

public class OnlineFrame extends JFrame implements Runnable{

	private JPanel contentPane;
	private JTable onlineTable;

	private static Vector table = new Vector();
	private static Vector title = new Vector();
	
	public static PropUtils configPropUtils = new PropUtils("config");
	private int SERVER_PORT;

	private ServerSocket server = null;
	
	Vector<String> client_message = new Vector<String>();
	Vector<CreateServerThread> client_info = new Vector<CreateServerThread>();

	private static OnlineFrame frame;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new OnlineFrame();
					frame.setVisible(true);
					Thread t = new Thread(frame);
					t.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings("unchecked")
	public OnlineFrame() {
		SERVER_PORT = Integer.parseInt(configPropUtils.getString("port"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		title.add("用户名");
		title.add("真实姓名");
		title.add("公司名称");
		title.add("联系电话");
		title.add("电子邮件");
		
		//table.add(title);
		onlineTable = new JTable(table,title);
		scrollPane.setViewportView(onlineTable);
		
		int w = (Toolkit.getDefaultToolkit().getScreenSize().width - 600) / 2;
		int h = (Toolkit.getDefaultToolkit().getScreenSize().height - 400) / 2;
		setLocation(w,h);
		
		try {
			server = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startServer(){
		while(true){
			try {
				while (true) {
					Socket socket = server.accept();
					new CreateServerThread(socket,this,client_message, client_info);

				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean deleUser(String name){
		Iterator<Vector> it = table.iterator();
		Vector row = null;
		while(it.hasNext()){
			row = it.next();
			if(row.get(0).equals(name)){
				boolean flag = table.remove(row);
				onlineTable.updateUI();
				return flag;
			}
		}
		return false;
		
	}
	
	@SuppressWarnings("unchecked")
	public void addVector(Vector row){
		table.add(row);
		onlineTable.updateUI();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.startServer();
	}
}
