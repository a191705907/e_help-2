package client.ui;

import adapter.ValidationAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;
import communicate.PushConfig;
import communicate.PushSender;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationActivity extends Activity{
	
	BroadcastReceiver receiver;
	
	private Integer[] imageIds = {R.drawable.img00};
	private String[] name = {"Hychile"};
	
	private ListView listView;
	private ValidationAdapter adapter;
	private List<Map<String,Object>> listItem;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.validation_layout);
		
		PushConfig.addfriend = 0;
		
		listView = (ListView)findViewById(R.id.val_list);
		listItem = new ArrayList<Map<String, Object>>(); //getListItem();
		adapter = new ValidationAdapter(this, listItem);
		listView.setAdapter(adapter);
		
		
		Validation val = new Validation();
		val.execute();
		
	    /*Intent intent = this.getIntent();
	    Bundle bundle = intent.getExtras();
		if (bundle != null){
			listItem.clear();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", imageIds[0]);			// ͼƬ��Դ���޴�
	        map.put("name", bundle.getString("username"));		// �û���
	        map.put("info", bundle.getString("info"));
	        //map.put("agree", agree);				// ͬ��
	        //map.put("reject", reject);				// �ܾ�
	        listItem.add(0, map);
	        adapter.notifyDataSetChanged();
		}
		
	    */
	    receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle bundle = intent.getExtras();
						
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("image", imageIds[0]);			// ͼƬ��Դ���޴�
	            map.put("name", bundle.getString("username"));		// �û���
	            map.put("info", bundle.getString("info"));
	            map.put("kind", bundle.getString("kind"));
	            //map.put("agree", agree);				// ͬ��
	            //map.put("reject", reject);				// �ܾ�
	            listItem.add(0, map);
	            adapter.notifyDataSetChanged();
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction("invite");
		ValidationActivity.this.registerReceiver(receiver, filter);
		
	}
	
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	/*	
	private List<Map<String, Object>> getListItem(){
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        //���������֤��Ϣ
        for(int i = 0; i < name.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
    		
            map.put("image", imageIds[i]);			// ͼƬ��Դ
            map.put("name", name[i]);				// �û���
            map.put("info", "����Hychile.");
            map.put("kind", "1");
            //map.put("agree", agree);				// ͬ��
            //map.put("reject", reject);				// �ܾ�
            listItems.add(map);
        }
		return listItems;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	*/
	
	
	private class Validation extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) { 
        	Map<String,Object> data = new HashMap<String, Object>();
			data.put("username", PushConfig.username);
			
			String result = PushSender.sendMessage("getvalidation", data);
			
            return result;
        }
        
        @Override
        protected void onPostExecute(String result) {   	
        	//Toast.makeText(ValidationActivity.this, result, Toast.LENGTH_SHORT).show();
			
        	if(result.equals("network error")){
        		Toast.makeText(ValidationActivity.this,"����û������", Toast.LENGTH_SHORT).show();
            	//pro.setVisibility(View.INVISIBLE);
        	}
        	if(result.equals("error")){
        		Toast.makeText(ValidationActivity.this,"���ӷ�����ʧ��", Toast.LENGTH_SHORT).show();
            	//pro.setVisibility(View.INVISIBLE);
        	}
            try {
            	switch (new JSONObject(result).getInt("state")) {
            	case 0:
            		Toast.makeText(ValidationActivity.this, "û��δ����ĺ�������", Toast.LENGTH_SHORT).show();
            		finish();
            		break;
            	case 1:
            		Toast.makeText(ValidationActivity.this, "���ճɹ�", Toast.LENGTH_SHORT).show();
            		try{
        				JSONArray validation = new JSONObject(result).getJSONArray("validations");
        				for (int i=0; i<validation.length(); i++){
        					Map<String, Object> map = new HashMap<String, Object> ();
        					map.put("image", imageIds[0]);			// ͼƬ��Դ���޴�
        			        map.put("name", validation.getJSONObject(i).getString("u_name"));  // �û���
        			        map.put("info", validation.getJSONObject(i).getString("info"));   //��֤��Ϣ
        			        map.put("kind", validation.getJSONObject(i).getString("kind"));   //�������
        			        listItem.add(0, map);
        		            adapter.notifyDataSetChanged();
        				}
        			}catch (JSONException e) {
        				// TODO Auto-generated catch block
        				Log.i("test",e.toString());
        				e.printStackTrace();
        			}
            		break;
            	default:
            		Toast.makeText(ValidationActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
            	}
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            super.onPostExecute(result);
        }
    }
	
}
