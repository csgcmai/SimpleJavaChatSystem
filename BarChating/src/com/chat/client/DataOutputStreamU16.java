package com.chat.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DataOutputStreamU16 extends DataOutputStream {

	public DataOutputStreamU16(OutputStream arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	public final void writeUTF16(String str)
            throws IOException{

		
		int length = str.length();
		this.writeInt(length);
		for(int i=0;i<length;i++){
			this.writeChar(str.codePointAt(i));
		
		}
		System.out.print("发送长度：");
		System.out.print(length);;
		System.out.println(" 内容：" + str);
		
	}
}
