/*
 * Copyright (C) 2013  stevendreamer (in github)
 * Project Location: https://github.com/stevendreamer/openerp_mobile

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * Addition: any copy of this program should keep the author name info.
 * any copy without the author info will be a pirate

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.oe.mobile.activity.stock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.Row;
import com.debortoliwines.openerp.api.RowCollection;
import com.oe.mobile.MyApp;
import com.oe.mobile.R;
import com.oe.mobile.R.id;
import com.oe.mobile.R.layout;
import com.oe.mobile.R.menu;
import com.oe.mobile.retired.Model;
import com.oe.mobile.service.Stock;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class StockInListActivity extends Activity {

	MyApp app;
	List<Map<String, Object>> listItems;
	LinearLayout headerLayout;
	Handler handler;
	ListView list;
	MyTask mTask;

	ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_stockin_list);

		list = (ListView) findViewById(R.id.stockinlist);

		listItems = new ArrayList<Map<String, Object>>();

		dialog = ProgressDialog.show(this, "", "�������ݣ����Ե�Ƭ�� ��", true, true);

		// set the dialog
		/*
		 * dialog = new ProgressDialog(getApplicationContext());
		 * dialog.setMessage("Loading..."); dialog.setCancelable(false);
		 */

		list.setOnItemClickListener(new ItemClickListener());

		// call the asynchronized task
		mTask = new MyTask();
		mTask.execute();

	}

	public void setPageView(RowCollection rc) {

		// construct the arraylist used to show on the page
		for (Row r : rc) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			// "name", "partner_id", "origin", "status"
			listItem.put("name", r.get("name"));
			if (r.get("partner_id") != null)
				listItem.put("partner_id",
						((Object[]) r.get("partner_id"))[1].toString());
			else
				listItem.put("partner_id", "");
			listItem.put("origin", r.get("origin"));
			listItem.put("state", r.get("state"));
			listItem.put("stockinId", r.get("id"));
			listItems.add(listItem);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
				R.layout.stockin_list, new String[] { "name", "partner_id",
						"origin", "state", "stockinId" }, new int[] {
						R.id.stockin_name, R.id.stockin_supplier,
						R.id.stockin_origin, R.id.stockin_state });
		list.setAdapter(simpleAdapter);
	}

	class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			System.out.println("this is in the clicker");

			// get the item id of the list, and goto the item detail page
			// to show the item detail information.
			System.out
					.println("zzyan inside list click trigger:"
							+ "arg2:"
							+ arg2
							+ " name:"
							+ ((HashMap) list.getItemAtPosition(arg2))
									.get("stockinId"));
			// parse the id of the item
			HashMap h = (HashMap) list.getItemAtPosition(arg2);
			int id = (Integer) h.get("stockinId");
			System.out.println("end of clicker");
			Intent intent = new Intent(StockInListActivity.this,
					StockInDetailActivity.class);
			intent.putExtra("stockinId", id);
			startActivity(intent);

		}
	}

	private class MyTask extends AsyncTask<String, Integer, RowCollection> {

		@Override
		protected void onPreExecute() {
			Log.i("ItemListPage", "onPreExecute() called");
			// dialog.show();
		}

		@Override
		protected RowCollection doInBackground(String... params) {
			RowCollection result = null;
			try {
				result = Stock.getStockIn();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... progresses) {

		}

		@Override
		protected void onPostExecute(RowCollection rc) {

			setPageView(rc);
			dialog.dismiss();

		}
	}
}
