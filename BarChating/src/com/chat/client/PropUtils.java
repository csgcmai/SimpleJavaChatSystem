package com.chat.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class PropUtils {
	private  Properties properties = null;

	private String name;
	
	public PropUtils(String n){
		name = n;
		properties = new Properties();
	}
	
	public Properties loadPropertiesFile() {
		File file = new File(System.getProperty("user.dir")+"/data/"+ this.name + ".properties");
		FileInputStream filein = null;
		try {
			if(!file.exists())file.createNewFile();
			filein = new FileInputStream(file);
			properties.load(filein);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				filein.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}

	public  String getString(String string) {
		loadPropertiesFile();
		return properties.getProperty(string);
	}

	public  HashMap<String, String> getAllUserInfo() {
		HashMap<String, String> user_map = new HashMap<String, String>();
		loadPropertiesFile();
		Set<?> keyset = properties.keySet();
		@SuppressWarnings("unchecked")
		Iterator<String> it = (Iterator<String>) keyset.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			user_map.put(key, properties.getProperty(key));
		}
		return user_map;
	}

	public  Set<Object> getAllUser() {
		loadPropertiesFile();
		Set<Object> keyset = properties.keySet();
		return keyset;
	}

	public  boolean addUser(String name, String information) {
		boolean flag = false;
		loadPropertiesFile();
		if (properties.containsKey(name)) {
			JOptionPane.showMessageDialog(null, "已存在"+name+"，不能重复注册");
			return flag;
		} else {
			flag = appendAddInfo("./data/"+this.name + ".properties", stringToUnicode(name)
					+ "=" + information + "\n");
		}
		return flag;
	}
	
	public  boolean updateUser(String name, String newinfo, String oldinfo){
		boolean flag = false;
		String toreplace ="\\Q"+ name + "=" + oldinfo + "\n";
		try {
			StringBuffer sb = new StringBuffer();
			String templine;
			File file = new File("./data/"+this.name + ".properties");
			BufferedReader bin = new BufferedReader(new FileReader(file));
			while ((templine = bin.readLine()) != null) {
				templine = unicodeToString(templine);
				sb.append(templine + "\n");
			}
			String save = new String(sb.toString());
			Pattern pattern = Pattern.compile(toreplace, Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(save);
			while (matcher.find()) {
				save = matcher.replaceAll(name + "=" + newinfo + "\n");
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			String[] saves = save.split("\n");
			for (int i = 0; i < saves.length; i++) {
				String[] key_values = saves[i].split("=");
				writer.write(stringToUnicode(key_values[0]) + "="
						+ key_values[1] + "\n");
			}
			
			writer.flush();
			bin.close();
			writer.close();
			flag = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}

	public  boolean deleteUser(String delete_key, String delete_value) {
		boolean flag = false;
		String toreplace ="\\Q"+delete_key + "=" + delete_value + "\n";
		try {
			StringBuffer sb = new StringBuffer();
			String templine;
			File file = new File("./data/"+name + ".properties");
			BufferedReader bin = new BufferedReader(new FileReader(file));
			while ((templine = bin.readLine()) != null) {
				templine = unicodeToString(templine);
				sb.append(templine + "\n");
			}
			String save = new String(sb.toString());
			Pattern pattern = Pattern.compile(toreplace, Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(save);
			while (matcher.find()) {
				save = matcher.replaceAll("");
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			String[] saves = save.split("\n");
			for (int i = 0; i < saves.length; i++) {
				String[] key_values = saves[i].split("=");
				writer.write(stringToUnicode(key_values[0]) + "="
						+ key_values[1] + "\n");
			}
			
			writer.flush();
			bin.close();
			writer.close();
			flag = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}

	private  boolean appendAddInfo(String fileName, String content) {
		boolean flag = false;
		RandomAccessFile raf = null;
		try {
			// 按读写方式创建一个随机访问文件流
			raf = new RandomAccessFile(fileName, "rw");
			long fileLength = raf.length();// 获取文件的长度即字节数
			// 将写文件指针移到文件尾。
			raf.seek(fileLength);
			// 按字节的形式将内容写到随机访问文件流中
			raf.writeBytes(content);
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (raf != null) {
				// 关闭流
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	public  String stringToUnicode(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			if (ch > 255)
				str += "\\u" + Integer.toHexString(ch);
			else
				str += (char) ch;
		}
		return str;
	}

	/**
	 * 把十六进制Unicode编码字符串转换为中文字符串
	 */
	public  String unicodeToString(String str) {
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch + "");
		}
		return str;
	}
}
