package com.example.billsplitter;



import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class AddMember extends Fragment {
	
	EditText LastName;
	EditText FirstName;
	Button Submit;
	Button Cancel;
	String uname;
	int flag = 0;
	public AddMember(){
	
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.addmember, container, false);
        
        //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
		//SharedPreferences.Editor editor = sharedPref.edit();
		//editor.clear();
	  //  editor.commit();
        
        Submit = (Button) rootView.findViewById(R.id.button1);
        LastName = (EditText) rootView.findViewById(R.id.editText1);
        FirstName = (EditText) rootView.findViewById(R.id.editText2);
        Submit.setOnClickListener(new OnClickListener(){
        //Fragment fragment = new AddMember();
        	

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// save new member 
				
		        uname = FirstName.getText().toString() + " " + LastName.getText().toString();
		        if(uname.trim().length() == 0) {
		        	LastName.setError("Name cannot be empty.");
		        	FirstName.setError("Name cannot be empty.");
		            return;
		         }
		        
				SharedPreferences sharedPref = getActivity().getSharedPreferences("user",Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				
				
				int NumberofMember = sharedPref.getInt("NumberofMember", 0);
				NumberofMember++;
				editor.putString("user" + NumberofMember, uname);
				editor.putInt("NumberofMember", NumberofMember);
				editor.commit();
				LastName.setText("");
				FirstName.setText("");
				
				((MainActivity)getActivity()).setCurrentItem (0, true);
			}
    		
        });
        Cancel = (Button) rootView.findViewById(R.id.button2);
        Cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LastName.setText("");
				FirstName.setText("");
				LastName.setError(null);
	        	FirstName.setError(null);
				((MainActivity)getActivity()).setCurrentItem (0, true);
			}
        	
        });
        
        return rootView;
    }


}
