/**
 * 
 */
package com.example.billsplitter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
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

/**
 * @author LxInter
 *
 */
public class TransactionList extends Fragment {

	/**
	 * 
	 */

	Context context;
	MyCustomAdapter dataAdapter = null;
	Button AddNew;
	Button Delete;
	Button Edit;
	Button Generate;

	public TransactionList() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.transactionlist, container, false);

		context = getActivity().getApplicationContext();
		ArrayList<Transaction> transactionlist = Transaction.getTransactionList(context);

		dataAdapter = new MyCustomAdapter(getActivity().getApplicationContext(), R.layout.userinfo, transactionlist);
		ListView listView = (ListView) rootView.findViewById(R.id.listView1);
		listView.setAdapter(dataAdapter);

		// Button of Add a new transaction
		AddNew = (Button) rootView.findViewById(R.id.AddNewTran);
		AddNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), AddTransaction.class);

				getActivity().finish();
				startActivity(intent);

			}

		});

		// Button of Delete a transaction
		Delete = (Button) rootView.findViewById(R.id.DeleteTran);
		Delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StringBuffer responseText = new StringBuffer();
				responseText.append("The following transactions were delected...\n");
				int flag = 0;
				ArrayList<Transaction> TransactionList = dataAdapter.transactionlist;
				for (int i = 0; i < TransactionList.size(); i++) {
					Transaction transaction = TransactionList.get(i);
					if (transaction.isSelected()) {
						responseText.append("\n" + transaction.transDetail);
						SharedPreferences sharedPref = getActivity().getSharedPreferences("transactions",
								Context.MODE_PRIVATE);
						sharedPref.edit().remove("trans" + transaction.getKey()).commit();
						flag = 1;
					}
				}
				if (flag == 1) {
					Toast.makeText(getActivity().getApplicationContext(), responseText, Toast.LENGTH_LONG).show();
					Intent intent = getActivity().getIntent();
					intent.putExtra("fragment", (int) 1);
					getActivity().finish();
					startActivity(intent);
				} else {
					responseText.setLength(0);
					responseText.append("No one is selected...\n");
					Toast.makeText(getActivity().getApplicationContext(), responseText, Toast.LENGTH_LONG).show();
				}
			}
		});

		// Button of edit a transaction
		Edit = (Button) rootView.findViewById(R.id.EditTran);
		Edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// keep how many transactions are checked
				int count = 0;
				int key = 0;
				ArrayList<Transaction> TransactionList = dataAdapter.transactionlist;

				for (int i = 0; i < TransactionList.size(); i++) {
					Transaction transaction = TransactionList.get(i);
					if (transaction.isSelected()) {
						count++;
						if (count > 1) {

							Toast.makeText(getActivity().getApplicationContext(),
									"More than one transaction are selected!", Toast.LENGTH_LONG).show();
							return;
						} else {
							key = transaction.key;
						}

					}

				}
				if (count != 1) {
					Toast.makeText(getActivity().getApplicationContext(), "No transaction is selected!",
							Toast.LENGTH_LONG).show();
					return;
				}
				Intent intent = new Intent(getActivity(), AddTransaction.class);
				intent.putExtra("key", key);
				getActivity().finish();
				startActivity(intent);
			}
		});

		// Button of generate report
		Generate = (Button) rootView.findViewById(R.id.Generate);
		Generate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), Report.class);
				getActivity().finish();
				startActivity(intent);
			}

		});

		return rootView;

	}

	private class MyCustomAdapter extends ArrayAdapter<Transaction> {

		private ArrayList<Transaction> transactionlist;

		public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<Transaction> transactionlist) {
			super(context, textViewResourceId, transactionlist);
			this.transactionlist = new ArrayList<Transaction>();
			this.transactionlist.addAll(transactionlist);
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
						Transaction transaction = (Transaction) cb.getTag();
						transaction.selected = cb.isChecked();

					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Transaction transaction = transactionlist.get(position);
			holder.code.setText(Html.fromHtml(transaction.transDetail));
			holder.name.setChecked(transaction.selected);
			holder.name.setText(null);
			holder.name.setTag(transaction);

			return convertView;

		}
	}

}
