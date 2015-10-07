/**
 * 
 */
package com.example.billsplitter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author LxInter
 *
 */
public class Report extends ActionBarActivity {

	Context context;
	Button back;
	StringBuffer errorTransactions;
	ListView listView;
	private ArrayAdapter<String> listAdapter;
	private ListView finalReport;
	DecimalFormat df = new DecimalFormat("#,###,##0.00");

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
		setContentView(R.layout.report);
		finalReport = (ListView) findViewById(R.id.listView1);
		// create a string buffer to keep transactions which are invalid
		errorTransactions = new StringBuffer();
		// get Member and transaction list
		context = getApplicationContext();
		ArrayList<User> memberlist = User.getmemberlist(context);
		ArrayList<Transaction> transactionlist = Transaction.getTransactionList(context);

		// create a hash table to keep everyone's balance
		Hashtable<String, Float> memberCosts = new Hashtable<String, Float>();
		for (int i = 0; i < memberlist.size(); i++) {
			memberCosts.put(memberlist.get(i).username, 0.00f);
		}

		// calculate everyone's balance
		for (int i = 0; i < transactionlist.size(); i++) {
			// get the creditor and his balance
			Transaction transaction = transactionlist.get(i);
			String creditor = transaction.transactionArray[0];
			Float creditorBalance = memberCosts.get(creditor);
			if (creditorBalance != null) {
				// update creditor's balance
				float credit = Float.parseFloat(transaction.transactionArray[1]);
				memberCosts.put(creditor, creditorBalance + credit);
				// assume each debtor share the total debts equally
				// at least there needs to have one valid debtor(in the member
				// list)
				// update debtor's balance
				int countForInvalidMember = 0;
				float debtForEach = credit / (transaction.transactionArray.length - 2);
				for (int j = 2; j < transaction.transactionArray.length; j++) {
					String debtor = transaction.transactionArray[j];
					Float debtorBalance = memberCosts.get(debtor);
					if (debtorBalance != null) {
						memberCosts.put(debtor, debtorBalance - debtForEach);
					} else {
						errorTransactions.append("No debtor <font color=#cc0029>" + debtor
								+ "</font> in member list for transaction: <i>" + transaction.transDetail
								+ ".</i> His/Her debt will be transferred to other debtors.<br>");
						countForInvalidMember++;
					}
				}
				// check if there are invalid debtors
				// if so, move his/her debtors to other debtors
				if (countForInvalidMember > 0) {
					// check if has any valid debtors
					if (countForInvalidMember == transaction.transactionArray.length - 2) {
						errorTransactions.append("No valid debtors in member list for transaction: <i>"
								+ transaction.transDetail + ".</i> This transaction will be adorted.<br>");
						memberCosts.put(creditor, creditorBalance);
					} else {
						float debttoAdd = debtForEach * countForInvalidMember
								/ (transaction.transactionArray.length - 2 - countForInvalidMember);
						for (int k = 2; k < transaction.transactionArray.length; k++) {
							String debtor = transaction.transactionArray[k];
							Float debtorBalance = memberCosts.get(debtor);
							if (debtorBalance != null) {
								memberCosts.put(debtor, debtorBalance - debttoAdd);
							}
						}
					}

				}
			} else {
				errorTransactions.append("Creditor <font color=#00bfff>" + creditor
						+ "</font> was not found in member list for transaction: <i>" + transaction.transDetail
						+ ".</i> This transaction will be adorted.<br>");
			}
		}

		// setup the listview to print each balance
		ArrayList<String> membersBalanceAndReport = new ArrayList<String>();
		ArrayList<String> membersBalanceName = new ArrayList<String>();
		ArrayList<Float> membersBalanceNum = new ArrayList<Float>();
		membersBalanceAndReport.add("<font color=#000000>Balance Report:</font>");
		Iterator<Entry<String, Float>> it;
		Map.Entry entry;
		it = memberCosts.entrySet().iterator();
		while (it.hasNext()) {
			entry = it.next();
			membersBalanceAndReport
					.add(entry.getKey() + " has a balance of $" + df.format(entry.getValue()).toString());
			membersBalanceName.add(entry.getKey().toString());
			membersBalanceNum.add(Float.parseFloat(entry.getValue().toString()));
		}
		if (errorTransactions.toString().trim().length() != 0) {
			errorTransactions.insert(0, "<font color=#ff0000>Warning:</font><br>");
			membersBalanceAndReport.add(errorTransactions.toString());
		}
		ArrayList<String> membersCostReport = new ArrayList<String>();
		// sort the balance
		ArrayComparator comparator = new ArrayComparator(membersBalanceNum);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		membersBalanceAndReport.add("<font color=#000000>Payment report:</font>");
		// decide how each debtor should pay to the creditor
		ArrayList<Integer> membersKey = new ArrayList<Integer>(Arrays.asList(indexes));

		while (membersKey.size() > 1) {
			int debtorKey = membersKey.get(0);
			int creditorKey = membersKey.get(membersKey.size() - 1);
			if (Math.abs(membersBalanceNum.get(debtorKey)) > membersBalanceNum.get(creditorKey)) {
				membersCostReport.add("<font color=#cc0029>" + membersBalanceName.get(debtorKey)
						+ "</font> shall pay <font color=#00bfff>" + membersBalanceName.get(creditorKey)
						+ "</font> <font color=#ffcc00>$" + df.format(membersBalanceNum.get(creditorKey)) + "</font>");
				// update the debtor value
				float temp = membersBalanceNum.get(debtorKey);
				temp += membersBalanceNum.get(creditorKey);
				membersBalanceNum.set(debtorKey, temp);
				// remove the pay-out creditor
				membersBalanceName.remove(creditorKey);
				membersBalanceNum.remove(creditorKey);
				membersKey.remove(creditorKey);
			} else {
				membersCostReport.add("<font color=#cc0029>" + membersBalanceName.get(debtorKey)
						+ "</font> shall pay <font color=#00bfff>" + membersBalanceName.get(creditorKey)
						+ "</font> <font color=#ffcc00>$" + df.format(Math.abs(membersBalanceNum.get(debtorKey)))
						+ "</font>");
				// update the creditor value
				float temp = membersBalanceNum.get(creditorKey);
				temp -= membersBalanceNum.get(debtorKey);
				membersBalanceNum.set(creditorKey, temp);
				// remove the pay-out debtor
				membersBalanceName.remove(debtorKey);
				membersBalanceNum.remove(debtorKey);
				membersKey.remove(debtorKey);

			}
			comparator = new ArrayComparator(membersBalanceNum);
			indexes = comparator.createIndexArray();
			Arrays.sort(indexes, comparator);
			membersKey = new ArrayList<Integer>(Arrays.asList(indexes));

		}
		Collections.sort(membersCostReport);
		membersBalanceAndReport.addAll(membersCostReport);

		listAdapter = new CustomAdapter(this, R.layout.reportrow, membersBalanceAndReport);
		finalReport.setAdapter(listAdapter);

		// Button of back to main page
		back = (Button) findViewById(R.id.button1);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(v.getContext(), MainActivity.class);
				intent.putExtra("fragment", (int) 1);
				finish();
				startActivity(intent);
			}

		});

	}

	public class CustomAdapter extends ArrayAdapter<String> {

		public CustomAdapter(Context context, int resource, ArrayList<String> balanceAndReport) {
			super(context, resource, balanceAndReport);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.reportrow, null);
			}

			String p = getItem(position);

			TextView tv = (TextView) convertView.findViewById(R.id.rowTextView);
			tv.setText(Html.fromHtml(p));

			return convertView;
		}

	}

}
