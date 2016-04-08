package com.chat.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -618111787691985360L;
	private JPanel contentPane;
	private JTextField nameField;
	private JPasswordField passField;

	private String name = null;
	private String pass = null;
	private DataOutputStreamU16 out = null;
	
//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					LoginFrame frame = new LoginFrame();
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
	public LoginFrame(DataOutputStreamU16 dout) {
		
		out = dout;
		
		setTitle("BarChat");
		setIconImage(Toolkit.getDefaultToolkit().getImage("F:\\justcoding\\BarChating\\img\\icon.png"));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 250);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		int w = (Toolkit.getDefaultToolkit().getScreenSize().width - 400) / 2;
		int h = (Toolkit.getDefaultToolkit().getScreenSize().height - 250) / 2;
		
		setLocation(w,h);
		
		JLabel nameLabel = new JLabel("\u8D26\u6237\uFF1A");
		nameLabel.setFont(new Font("方正舒体", Font.PLAIN, 20));
		nameLabel.setBounds(40, 45, 60, 30);
		contentPane.add(nameLabel);
		
		JLabel passLabel = new JLabel("\u5BC6\u7801\uFF1A");
		passLabel.setFont(new Font("方正舒体", Font.PLAIN, 20));
		passLabel.setBounds(40, 85, 60, 30);
		contentPane.add(passLabel);
		
		nameField = new JTextField();
		nameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				if(arg0.getKeyChar()==(char)KeyEvent.VK_ENTER)
					login();
			}
		});
		nameField.setFont(new Font("方正舒体", Font.PLAIN, 15));
		nameField.setBounds(110, 45, 235, 30);
		contentPane.add(nameField);
		nameField.setColumns(10);
		
		passField = new JPasswordField();
		passField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				if(arg0.getKeyChar()==(char)KeyEvent.VK_ENTER)
					login();
			}
		});
		passField.setFont(new Font("方正舒体", Font.PLAIN, 15));
		passField.setBounds(110, 85, 235, 30);
		contentPane.add(passField);
		
		JButton loginBt = new JButton("\u767B\u5F55");
		loginBt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				if(arg0.getKeyChar()==(char)KeyEvent.VK_ENTER)
					login();
			}
		});
		loginBt.setFont(new Font("方正舒体", Font.PLAIN, 25));
		loginBt.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				login();
			}
		});
		loginBt.setBounds(124, 140, 144, 30);
		contentPane.add(loginBt);
		
	}
	
	
	public String getName(){
		if(name == null){
			name = nameField.getText().trim();
		}
		return name;
	}
	
	private void login(){
		name = nameField.getText().trim();
		pass = passField.getText();
		
		if(name.equals("") || name.equals(null)){
			JOptionPane.showMessageDialog(LoginFrame.this, "账户不能为空，请检查并重新输入！");
			return;
		} else if (pass.equals("")||pass.equals(null)){
			JOptionPane.showMessageDialog(LoginFrame.this, "密码不能为空，请检查并重新输入！");
			return;
		} else {
			try {
				out.writeUTF16("login -u " + name + " -p " + pass + "#");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
