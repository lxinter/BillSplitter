package com.example.billsplitter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MemberList extends Fragment {

	Button AddNew;
	Button Delete;
	MyCustomAdapter dataAdapter = null;
	Context context;

	public MemberList() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.memberlist, container, false);

		// Print Member list
		context = getActivity().getApplicationContext();
		ArrayList<User> Userlist = User.getmemberlist(context);
		dataAdapter = new MyCustomAdapter(getActivity().getApplicationContext(), R.layout.userinfo, Userlist);
		ListView listView = (ListView) rootView.findViewById(R.id.listView1);
		listView.setAdapter(dataAdapter);

		// Button of Add a new member
		AddNew = (Button) rootView.findViewById(R.id.AddNewMember);
		AddNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainActivity) getActivity()).setCurrentItem(2, true);

			}

		});
		// Button of Delete a member
		Delete = (Button) rootView.findViewById(R.id.DeleteMember);
		Delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StringBuffer responseText = new StringBuffer();
				responseText.append("The following members were delected...\n");
				int flag = 0;
				ArrayList<User> userList = dataAdapter.UserList;
				for (int i = 0; i < userList.size(); i++) {
					User user = userList.get(i);
					if (user.isSelected()) {
						responseText.append("\n" + user.getName());
						SharedPreferences sharedPref = getActivity().getSharedPreferences("user",Context.MODE_PRIVATE);
						sharedPref.edit().remove("user"+ user.getKey()).commit();
						flag = 1;
					}
				}
				if(flag == 1){
				Toast.makeText(getActivity().getApplicationContext(), responseText, Toast.LENGTH_LONG).show();
				Intent intent = getActivity().getIntent();
			    getActivity().finish();
			    startActivity(intent);
				}else{
					responseText.setLength(0);
					responseText.append("No one is selected...\n");
					Toast.makeText(getActivity().getApplicationContext(), responseText, Toast.LENGTH_LONG).show();
				}
			}
		});
		

		return rootView;

	}

	

	private class MyCustomAdapter extends ArrayAdapter<User> {

		private ArrayList<User> UserList;

		public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<User> userList) {
			super(context, textViewResourceId, userList);
			this.UserList = new ArrayList<User>();
			this.UserList.addAll(userList);
		}

		private class ViewHolder {
			TextView code;
			CheckBox name;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.userinfo, null);

				holder = new ViewHolder();
				holder.code = (TextView) convertView.findViewById(R.id.code);
				holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
				convertView.setTag(holder);

				holder.name.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						User user = (User) cb.getTag();
						user.selected = cb.isChecked();

					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			User user = UserList.get(position);
			holder.code.setText(user.username);
			holder.name.setChecked(user.selected);
			holder.name.setText(null);
			holder.name.setTag(user);

			return convertView;

		}

	}

}
