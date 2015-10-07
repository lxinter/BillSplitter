package com.example.billsplitter;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

public class User {

	public User() {
		// TODO Auto-generated constructor stub
	}
	String username;
	boolean selected;
	int key;

	public boolean isSelected() {
		return selected;
	}

	public String getName() {
		return username;
	}
	
	public int getKey() {
		return key;
	}
	
	public static ArrayList<User> getmemberlist(Context ctxt) {
		// TODO Auto-generated method stub
		ArrayList<User> Userlist = new ArrayList<User>();
		
		SharedPreferences sharedPref = ctxt.getSharedPreferences("user", Context.MODE_PRIVATE);
		int numberofmember = sharedPref.getInt("NumberofMember", 0);
		User user = null;
		if (numberofmember != 0) {
			for (int i = 1; i <= numberofmember; i++) {
				
				user = new User();
				user.username = sharedPref.getString("user" + i, null);
				if(user.username != null){
				user.selected = false;
				user.key = i;
				Userlist.add(user);
				}
			}

		}
		return Userlist;
	}

}
