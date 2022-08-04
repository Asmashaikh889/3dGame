package com.notbestlord.network.server;

import com.notbestlord.network.data.ClientLoginReplyType;

import java.util.ArrayList;
import java.util.List;

public class UserdataList {
	public static class Userdata{
		private String username;
		private String password;
		private String uuid;

		public Userdata(String username, String password, String uuid) {
			this.username = username;
			this.password = password;
			this.uuid = uuid;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		public String getUuid() {
			return uuid;
		}
	}
	public static class UserLoginReply{
		public ClientLoginReplyType replyType;
		public String uuid = "";
	}
	public final List<Userdata> usersDataList = new ArrayList<>();

	public UserLoginReply userLogin(String username, String password){
		UserLoginReply reply = new UserLoginReply();
		for (Userdata data : usersDataList) {
			if(data.getUsername().equals(username) && data.getPassword().equals(password)){
				reply.replyType = ClientLoginReplyType.logged_in;
				reply.uuid = data.getUuid();
				return reply;
			}
			else if(data.getUsername().equals(username) && !data.getPassword().equals(password)){
				reply.replyType = ClientLoginReplyType.incorrect_login;
				return reply;
			}
		}
		reply.replyType = ClientLoginReplyType.user_no_exist;
		return reply;
	}
	public ClientLoginReplyType registerUsernameReply(String username, String password){
		if(username.length() < 4){
			return ClientLoginReplyType.username_short;
		}
		username = username.toLowerCase();
		for (Userdata data : usersDataList) {
			if(data.getUsername().equals(username)) {
				return ClientLoginReplyType.user_exists;
			}
		}
		if(password.length() < 6){
			return ClientLoginReplyType.password_short;
		}
		if(password.length() > 16){
			return ClientLoginReplyType.password_long;
		}
		return ClientLoginReplyType.logged_in;
	}

	public String getUuid(String username){
		for(Userdata userdata : usersDataList){
			if(userdata.getUsername().equalsIgnoreCase(username)){
				return userdata.getUuid();
			}
		}
		return "";
	}
}
