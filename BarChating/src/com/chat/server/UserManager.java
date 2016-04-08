package com.chat.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import com.chat.client.PropUtils;

public class UserManager{

	//offline time
	public static final long OFF_TIME = 10L;
	
	//Database related
	public static final String TABLE = " user ";
	private static Connection conn = null;
	
	private static void init(){
		String db_host=null,db_name=null,db_user=null,db_pwd=null,db_port=null;
		if(db_host==null){
			db_host = OnlineFrame.configPropUtils.getString("DB_HOST");
			db_name = OnlineFrame.configPropUtils.getString("DB_NAME");
			db_user = OnlineFrame.configPropUtils.getString("DB_USER");
			db_pwd = OnlineFrame.configPropUtils.getString("DB_PASSWORD");
			db_port = OnlineFrame.configPropUtils.getString("DB_PORT");
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://"+ db_host + ":" + db_port +"/" + db_name,db_user,db_pwd);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean login(String username, String password) throws SQLException{
		if(conn == null) init();
		
		String statement = "select * from " + TABLE + "where (name = ? and password = ?)";
		PreparedStatement pstmt = conn.prepareStatement(statement);
		pstmt.setString(1, username);
		pstmt.setString(2, password);
		System.out.println(pstmt.toString());
		ResultSet rs = pstmt.executeQuery();
		if(!rs.first()) return false;
		
		statement = "update " + TABLE + "set lastupdatetime = ? where name = ?";
		pstmt = conn.prepareStatement(statement);
		pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		pstmt.setString(2, username);
		if(pstmt.executeUpdate()==0) return false;
		
		return true;
		
	}
	
	public static boolean isOnline(String username) throws SQLException{
		if(conn == null) init();
		String statement = "select * from " + TABLE + "where name = ? and lastupdatetime > ?";
		PreparedStatement pstmt = conn.prepareStatement(statement);
		pstmt.setString(1, username);
		pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()-OFF_TIME));
		if(pstmt.executeQuery().wasNull()) return false;
		return true;
	}
	
	public static boolean updateUserStatus(String username) throws SQLException{
		if(conn == null) init();
		String statement = "update " + TABLE + "set lastupdatetime = ? where name = ?";
		PreparedStatement pstmt = conn.prepareStatement(statement);
		pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		pstmt.setString(2, username);
		if(pstmt.executeUpdate()==0) return false;
		return true;
	}
	
	public static int getUserIP(String username) throws SQLException{
		if(conn == null) init();
		String statement = "select onlineip from " + TABLE + "where name = ? and lastupdatetime > ?";
		PreparedStatement pstmt = conn.prepareStatement(statement);
		pstmt.setString(1, username);
		pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()-OFF_TIME));
		ResultSet rs = pstmt.executeQuery();
		if(rs.wasNull()) return 0;
		else{
			return rs.getInt(1);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Vector getOnlineUser() throws SQLException{
		if(conn == null) init();
		String statement = "select name,real_name,company_name,phone,email where lastupdatetime > ?";
		PreparedStatement pstmt = conn.prepareStatement(statement);
		pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()-OFF_TIME));
		ResultSet rs = pstmt.executeQuery();
		
		Vector onlineList = new Vector();
		rs.beforeFirst();
		while(rs.next()){
			Vector newRow = new Vector();
			newRow.add(rs.getObject(1));
			newRow.add(rs.getObject(2));
			newRow.add(rs.getObject(3));
			newRow.add(rs.getObject(4));
			newRow.add(rs.getObject(5));
			
			onlineList.add(newRow);
			newRow = null;
		}
		return onlineList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Vector getUserInfo(String username) throws SQLException{
		if(conn == null) init();
		String statement = "select name,real_name,company_name,phone,email from" + TABLE +"where name = ?";
		PreparedStatement pstmt = conn.prepareStatement(statement);
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();
		
		Vector onlineList = new Vector();
		rs.beforeFirst();
		while(rs.next()){
			Vector newRow = new Vector();
			newRow.add(rs.getObject(1));
			newRow.add(rs.getObject(2));
			newRow.add(rs.getObject(3));
			newRow.add(rs.getObject(4));
			newRow.add(rs.getObject(5));
			
			onlineList.add(newRow);
			newRow = null;
		}
		return (Vector) onlineList.get(0);
	}
}
