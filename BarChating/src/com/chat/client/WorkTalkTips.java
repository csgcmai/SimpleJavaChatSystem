package com.chat.client;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.sun.awt.AWTUtilities;
/**
 * ��Ļ���½ǳ��ֽ������Ե���ʾ�� ʹ�õ���JDK1.6�������Ե�͸�����壬���Ա���Ҫʹ��JDK1.6�����ϰ汾��JDK �������£� 1.�������ʱ������
 * 2.ͣ��һ���ʱ��֮����Զ���ģ��ֱ����ʧ 3.����رհ�ť����ģ��ֱ����ʧ 4.��ʾ����֧��html��ǩ
 * 
 */
@SuppressWarnings("restriction")
public class WorkTalkTips implements Runnable {

	private JFrame showInfo;
	private JEditorPane detail;
	private int stayTime;// ����ʱ��
	private String title, message;// ��Ϣ����,����
	private int style;// ������ʽ
	private JLabel Logo;


	/**
	 * �������Ե���ʾ��
	 * 
	 * @param style
	 *            ��ʾ�����ʽ ����Ϊ��ʽ��ѡֵ�� 0 NONE ��װ�Σ���ȥ���������� 1 FRAME ��ͨ���ڷ�� 2
	 *            PLAIN_DIALOG �򵥶Ի����� 3 INFORMATION_DIALOG ��Ϣ�Ի����� 4
	 *            ERROR_DIALOG ����Ի����� 5 COLOR_CHOOSER_DIALOG ʰɫ���Ի����� 6
	 *            FILE_CHOOSER_DIALOG �ļ�ѡ��Ի����� 7 QUESTION_DIALOG ����Ի����� 8
	 *            WARNING_DIALOG ����Ի�����
	 * @param title
	 *            ��ʾ�����
	 * @param message
	 *            ��ʾ������
	 * @param logo ��ʾlogo
	 */
	public WorkTalkTips(int style, String title, String message, JLabel Logo) {
		this.stayTime = 5;
		this.style = style;
		this.title = title;
		this.message = message;
		this.Logo = Logo;
	}

	// public static void main(String[] args) {
	// String title = "������ʾ��";
	// String message = "<strong>���ļ��ĸ�ʽ</strong><br>��ֻ�ܴ�jpgͼƬ��txt�ı���ʽ���ļ�";
	// // Runnable translucent=new
	// // TranslucentFrame(250,180,10,4,title,message);
	// Runnable translucent = new TranslucentFrame(2, title, message);
	// Thread thread = new Thread(translucent);
	// thread.start();
	// }

	public void print() {
		showInfo = new JFrame();
		showInfo.setLayout(new BorderLayout());
		detail = new JEditorPane();
		detail.setEditable(false);// ���ɱ༭
		detail.setContentType("text/html");// ���༭������Ϊ֧��html�ı༭��ʽ
		detail.setText(message);
		showInfo.add(Logo, BorderLayout.NORTH);
		showInfo.add(detail, BorderLayout.SOUTH);
		showInfo.setSize(200, 160);
		showInfo.setTitle(title);
		// ���ô����λ�ü���С
		int x = Toolkit.getDefaultToolkit().getScreenSize().width
				- Toolkit.getDefaultToolkit().getScreenInsets(
						showInfo.getGraphicsConfiguration()).right - showInfo.getWidth() - 5;
		int y = Toolkit.getDefaultToolkit().getScreenSize().height
				- Toolkit.getDefaultToolkit().getScreenInsets(
						showInfo.getGraphicsConfiguration()).bottom - showInfo.getHeight()
				- 5;
		showInfo.setBounds(x, y, showInfo.getWidth(), showInfo.getHeight());
		showInfo.setUndecorated(true); // ȥ�����ڵ�װ��
		showInfo.getRootPane().setWindowDecorationStyle(style); // ������ʽ
		AWTUtilities.setWindowOpacity(showInfo, 0.01f);// ��ʼ��͸����
		showInfo.setVisible(true);
		showInfo.setAlwaysOnTop(true);// �����ö�
		// ��ӹرմ��ڵļ���
		showInfo.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				hide();
			}
		});
	}

	/**
	 * �����𽥱�����
	 * 
	 */
	public void show() {
		for (int i = 0; i < 50; i++) {
			try {
				Thread.sleep(50);
			} catch (Exception sleepex) {
				sleepex.printStackTrace();
			}
			AWTUtilities.setWindowOpacity(showInfo, i * 0.02f);
		}
	}

	/**
	 * �����𽥱䵭ֱ����ʧ
	 * 
	 */
	public void hide() {
		float opacity = 100;
		while (true) {
			if (opacity < 2) {
				break;
			}
			opacity = opacity - 2;
			AWTUtilities.setWindowOpacity(showInfo, opacity / 100);
			try {
				Thread.sleep(20);
			} catch (Exception sleepex) {
				sleepex.printStackTrace();
			}
		}
		showInfo.dispose();
	}

	public void run() {
		print();
		show();
		try {
			Thread.sleep(stayTime * 1000);
		} catch (Exception e) {
		}
		hide();

	}
}