package com.chat.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataInputStreamU16 extends DataInputStream {

	public DataInputStreamU16(InputStream arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	public final String readUTF16()
            throws IOException{
		int length = this.readInt();
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<length;i++){
			sb.append(this.readChar());
		}
		
		System.out.print("���ճ��ȣ�");
		System.out.print(length);
		System.out.println(" ���ݣ�"+sb.toString());
		return sb.toString();
	}

}
