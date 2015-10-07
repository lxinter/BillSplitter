/**
 * 
 */
package com.example.billsplitter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author LxInter
 *
 */
public class AddTransaction extends ActionBarActivity {

	/**
	 * 
	 */
	private ArrayList<String> userlist;
	private ArrayList<String> userlistForAddDebtor;
	private ArrayList<String> Debtorlist;
	Button AddCreditor;
	Button AddDebtor;
	TextView Creditor;
	TextView Debtor;
	EditText money;
	Spinner sCreditor;
	Spinner sDebtor;
	Button removeDebtor;
	Button confirm;
	Button cancel;
	String transactions;
	ArrayAdapter<String> adapter;
	Bundle extras;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addtransaction);
		Context context = getApplicationContext();
		extras = getIntent().getExtras();
		//the program is by default called by add transaction
		
		ArrayList<User> Userlist = User.getmemberlist(context);
		userlist = new ArrayList<String>();
		userlistForAddDebtor = new ArrayList<String>();

		for (User i : Userlist) {
			userlist.add(i.username);
		}
		userlistForAddDebtor.addAll(userlist);
		// set source for Drop Down List
		sCreditor = (Spinner) findViewById(R.id.spinner1);
		sDebtor = (Spinner) findViewById(R.id.spinner2);
		adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, userlist);
		sCreditor.setAdapter(adapter);
		sDebtor.setAdapter(adapter);

		// set button onclick event for add creditor
		AddCreditor = (Button) findViewById(R.id.button1);
		Creditor = (TextView) findViewById(R.id.textView3);
		AddCreditor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String selected = (String) sCreditor.getSelectedItem();
				Creditor.setText(selected);
			}

		});
		// set button onclick event for add debtor

		AddDebtor = (Button) findViewById(R.id.button2);
		Debtor = (TextView) findViewById(R.id.textView5);
		Debtor.setMovementMethod(new ScrollingMovementMethod());
		Debtorlist = new ArrayList<String>();
		AddDebtor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String selected = (String) sDebtor.getSelectedItem();

				// check if drop down list is empty
				if (selected != null) {
					// check if it is the first item in textview
					String s = Debtor.getText().toString();
					if (s.length() == 0) {
						Debtor.setText(selected);
					} else {
						Debtor.append(", " + selected);
					}
					// update Debtor list
					Debtorlist.add(selected);
					// update drop down list
					userlistForAddDebtor.remove(selected);
					ArrayAdapter<String> adapterDebtor = new ArrayAdapter<String>(getApplicationContext(),
							R.layout.spinner_item, userlistForAddDebtor);
					sDebtor.setAdapter(adapterDebtor);
				}
			}

		});

		// set button onclick event for remove debtor
		removeDebtor = (Button) findViewById(R.id.button5);
		removeDebtor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// check if any debtor is seleted
				if (Debtorlist.size() != 0) {
					String tobeRemoved = Debtorlist.get(Debtorlist.size() - 1);
					// update Debtor list
					Debtorlist.remove(Debtorlist.size() - 1);
					// update textview
					String s = Debtor.getText().toString();
					if (Debtorlist.size() != 0) {
						Debtor.setText(s.substring(0, s.length() - tobeRemoved.length() - 2));
					} else {
						Debtor.setText(s.substring(0, s.length() - tobeRemoved.length()));
					}

					// update drop down list
					userlistForAddDebtor.add(tobeRemoved);
					ArrayAdapter<String> adapterDebtor = new ArrayAdapter<String>(getApplicationContext(),
							R.layout.spinner_item, userlistForAddDebtor);
					sDebtor.setAdapter(adapterDebtor);

				}
			}

		});

		// set button onclick event for confirm button
		confirm = (Button)findViewById(R.id.button4);
		money = (EditText) findViewById(R.id.editText1);
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// check if input is valid
				int flag = 0;
				if (Creditor.getText().toString().trim().length() == 0) {
					Creditor.setError("Creditor cannot be empty.");
					flag = 1;
				}else{Creditor.setError(null);}
				if (Debtor.getText().toString().trim().length() == 0) {
					Debtor.setError("Debtor cannot be empty.");
					flag = 1;
				}else{Debtor.setError(null);}
				if (money.getText().toString().trim().length() == 0) {
					money.setError("Amount spent cannot be empty.");
					flag = 1;
				}else{Debtor.setError(null);}
				
				if (flag == 1){return;}

				SharedPreferences sharedPref = getSharedPreferences("transactions", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				//info to be transferred
				transactions = Creditor.getText() + ", " + money.getText() + ", " + Debtor.getText();
				if(extras == null){
					int NumberofTrans = sharedPref.getInt("NumberofTrans", 0);
					NumberofTrans++;
					editor.putString("trans" + NumberofTrans, transactions);
					editor.putInt("NumberofTrans", NumberofTrans);
				}else{
					int key = extras.getInt("key");
					editor.putString("trans" + key, transactions);
				}
				
				
				
				editor.commit();
				
				Intent intent = new Intent(v.getContext(), MainActivity.class);
				intent.putExtra("fragment", (int) 1);
				finish();
				startActivity(intent);
			}

		});

		// set button onclick event for cancel button
		cancel = (Button) findViewById(R.id.button3);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(v.getContext(), MainActivity.class);
				intent.putExtra("fragment", (int) 1);
				finish();
				startActivity(intent);

			}

		});

		//check if it is call by edit
		
		if(extras != null){
			 int key = extras.getInt("key");
			 //change title to edit transaction
			 TextView title = (TextView) findViewById(R.id.textView1);
			 title.setText("Edit Transaction");
			 //get the origin creditor, debtor, and apply the appropriate spinner for debtor
			 SharedPreferences sharedPref = getSharedPreferences("transactions", Context.MODE_PRIVATE);
			 Transaction transaction = new Transaction();
			 transaction.transDetail = sharedPref.getString("trans" + key, null);
			 if (transaction.transDetail != null) {
					String[] transactionArray = transaction.transDetail.split(", ");
					//set creditor
					Creditor.setText(transactionArray[0]);
					//set amount
					money.setText(transactionArray[1]);
					//set debtor and spinner
					
					for (int j = 2; j < transactionArray.length; j++) {
						if (j != 2) {
							Debtor.append(", ");
						}
						Debtor.append(transactionArray[j]);
						Debtorlist.add(transactionArray[j]);
						userlistForAddDebtor.remove(transactionArray[j]);
					}
					
					ArrayAdapter<String> adapterDebtor = new ArrayAdapter<String>(getApplicationContext(),
							R.layout.spinner_item, userlistForAddDebtor);
					sDebtor.setAdapter(adapterDebtor);
			 }
		}
		
	}


}
