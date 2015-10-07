package com.example.billsplitter;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

public class Transaction {

	public Transaction() {
		// TODO Auto-generated constructor stub
	}

	String transDetail;
	boolean selected;
	int key;
	String[] transactionArray;
	
	public boolean isSelected() {
		return selected;
	}

	public String getTransaction() {
		return transDetail;
	}

	public int getKey() {
		return key;
	}

	public static ArrayList<Transaction> getTransactionList(Context ctxt) {
		// TODO Auto-generated method stub
		ArrayList<Transaction> transactionlist = new ArrayList<Transaction>();

		SharedPreferences sharedPref = ctxt.getSharedPreferences("transactions", Context.MODE_PRIVATE);
		int numberofTrans = sharedPref.getInt("NumberofTrans", 0);
		Transaction transaction = null;
		if (numberofTrans != 0) {
			for (int i = 1; i <= numberofTrans; i++) {

				transaction = new Transaction();
				transaction.transDetail = sharedPref.getString("trans" + i, null);
				if (transaction.transDetail != null) {
					String[] transactionArray = transaction.transDetail.split(", ");
					StringBuffer transacDetail = new StringBuffer();

					if (transactionArray.length >= 3) {

						transacDetail.append("<font color=#00bfff>" + transactionArray[0] + "</font>" + " spend "
								+ "<font color=#ffcc00>$" + transactionArray[1] + "</font>" + " for ");

						for (int j = 2; j < transactionArray.length; j++) {
							transacDetail.append("<font color=#cc0029>" + transactionArray[j] + "</font>");
							if (j < transactionArray.length - 2) {
								transacDetail.append(", ");
							}
							if (j == transactionArray.length - 2) {
								transacDetail.append(" and ");
							}
						}
					} else {
						transacDetail.append(transaction.transDetail);
					}
					transaction.transDetail = transacDetail.toString();
					transaction.transactionArray = transactionArray;
					transaction.selected = false;
					transaction.key = i;
					transactionlist.add(transaction);
				}
			}

		}
		return transactionlist;
	}

}
