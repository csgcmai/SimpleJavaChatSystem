package com.chat.client;

import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JEditorPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.ScrollPaneConstants;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 290123787455078757L;
	private JPanel contentPane;
	private JScrollPane scrollPane = null;
	private JEditorPane sendPane = new JEditorPane();
	private JEditorPane recvPane = new JEditorPane();
	public JCheckBox workBox = new JCheckBox("\u5DE5\u4F5C\u6A21\u5F0F");
	private MainFrame mainFrame;
	private String name=null,targetName = null;
	private DataOutputStreamU16 out = null;
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ChatFrame frame = new ChatFrame();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public ChatFrame(MainFrame frame, String myName, String tName, DataOutputStreamU16 dout) {
		name = myName;
		targetName = tName;
		out = dout;
		mainFrame = frame;
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\img\\icon.png"));
		setTitle("BarChat:   " + "与" + targetName + "聊天中");
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.menu);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				setVisible(false);
				recvPane.setText("");
				dispose();
			}
		});
		int w = (Toolkit.getDefaultToolkit().getScreenSize().width - 500) / 2;
		int h = (Toolkit.getDefaultToolkit().getScreenSize().height - 400) / 2;
		
		setLocation(w,h);
		
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0,100,80,80};
		gbl_contentPane.rowHeights = new int[]{0,30};
		gbl_contentPane.columnWeights = new double[]{1.0};
		gbl_contentPane.rowWeights = new double[]{1.0};
		contentPane.setLayout(gbl_contentPane);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBackground(SystemColor.inactiveCaption);
		GridBagConstraints gbc_mainPanel = new GridBagConstraints();
		gbc_mainPanel.gridwidth = 4;
		gbc_mainPanel.insets = new Insets(0, 0, 5, 0);
		gbc_mainPanel.fill = GridBagConstraints.BOTH;
		gbc_mainPanel.gridx = 0;
		gbc_mainPanel.gridy = 0;
		contentPane.add(mainPanel, gbc_mainPanel);
		sendPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				if(arg0.isControlDown()&&(arg0.getKeyChar()==(char)KeyEvent.VK_ENTER))
					sendMsg();
			}
		});
		
		sendPane.setFont(new Font("新宋体", Font.PLAIN, 12));
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GroupLayout gl_mainPanel = new GroupLayout(mainPanel);
		gl_mainPanel.setHorizontalGroup(
			gl_mainPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(sendPane)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
		);
		gl_mainPanel.setVerticalGroup(
			gl_mainPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_mainPanel.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(sendPane, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE))
		);
		recvPane.setEditable(false);
		
		recvPane.setFont(new Font("新宋体", Font.PLAIN, 12));
		recvPane.setBackground(Color.WHITE);
		scrollPane.setViewportView(recvPane);
		mainPanel.setLayout(gl_mainPanel);
		
		GridBagConstraints gbc_workBox = new GridBagConstraints();
		gbc_workBox.insets = new Insets(0, 0, 0, 5);
		gbc_workBox.gridx = 1;
		gbc_workBox.gridy = 1;
		workBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean isChecked = workBox.isSelected();
				
				if(isChecked)
					mainFrame.friendToWork(targetName);
				else mainFrame.workToFriend(targetName);	
			}
		});
		workBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				if(arg0.isControlDown()&&(arg0.getKeyChar()==(char)KeyEvent.VK_ENTER))
					sendMsg();
			}
		});
		contentPane.add(workBox, gbc_workBox);
		GridBagConstraints gbc_sendBt = new GridBagConstraints();
		gbc_sendBt.insets = new Insets(0, 0, 0, 5);
		gbc_sendBt.gridx = 2;
		gbc_sendBt.gridy = 1;
		
		JButton quitBt = new JButton("\u9000\u51FA");
		quitBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				recvPane.setText("");
				dispose();
			}
		});
		
		JButton sendBt = new JButton("\u53D1\u9001");
		sendBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sendMsg();
			}
		});
		contentPane.add(sendBt, gbc_sendBt);
		GridBagConstraints gbc_quitBt = new GridBagConstraints();
		gbc_quitBt.gridx = 3;
		gbc_quitBt.gridy = 1;
		contentPane.add(quitBt, gbc_quitBt);
		
		setVisible(true);
	}
	
	public void setRecvMessage(String msg){
		
		String oldMsg = recvPane.getText();
		oldMsg+= '\n' + msg;
		recvPane.setText(oldMsg);
		int max = scrollPane.getVerticalScrollBar().getMaximum();
		scrollPane.getVerticalScrollBar().setValue(max);
		
		if(workBox.isSelected()){
			FileWriter fileOut;
			File tempfile = new File("./data/" + name + "/");
			if(!tempfile.isDirectory()){
				tempfile.mkdir();
				System.out.println(tempfile.toString());
			}
			try {
				fileOut = new FileWriter("./data/" + name + "/与" + targetName + "的工作数据记录.txt",true);
				fileOut.write("\r\n"+msg);
				fileOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
	private void sendMsg(){
		String sendMsg = sendPane.getText().trim();
		if(sendMsg.equals("") || sendMsg.equals(null)){
			JOptionPane.showMessageDialog(ChatFrame.this, "发送内容不能为空！");
			return;
		}
		
		String msg = "send -s " + name + " -t " + targetName + " -w ";
		boolean isWork = workBox.isSelected();
		if(isWork) msg += "work -m " + DateUtils.getDate() + ":\n" + sendMsg;
		else msg += "chat -m " + DateUtils.getDate() + ":\n" +sendMsg;
		
		try {
			out.writeUTF16(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setRecvMessage( "我：" + DateUtils.getDate() + ":\n" +sendMsg);
		sendPane.setText("");
	}
}
