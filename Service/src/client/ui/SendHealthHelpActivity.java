package client.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import routeplan.Location;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import communicate.PushConfig;
import communicate.PushSender;


public class SendHealthHelpActivity extends Activity implements OnLongClickListener{

	private static final String[] sen={"�������", "ͻ�����ಡ","����Ѫ","�Թ���"};
	private EditText edt;
	private Spinner spinner;
	private Button sendmessage,btn_audio, btn_video,close;
	private ArrayAdapter<String> adapter;
    private Send send;
    private ProgressBar pro;
    private String audio_file="", video_file="";
    private Location lo;
    private MediaPlayer audio_player = new MediaPlayer();
    
    boolean isLongClick=false;
	/* ¼�Ƶ���Ƶ�ļ� */
	private File			mRecAudioFile;
	private File			mRecAudioPath;
	private File			dir;
	private String SD_CARD_TEMP_DIR = getSDPath() + File.separator + "myaudio" + File.separator;
	private MediaRecorder	mMediaRecorder;
	private String			strTempFile	= "audio_";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sendhealthhelp_activity);
		close=(Button)findViewById(R.id.close);
		pro=(ProgressBar)findViewById(R.id.progressBar1);
		edt = (EditText) findViewById(R.id.send_msg);
		spinner = (Spinner) findViewById(R.id.common_sentence);
		sendmessage=(Button)findViewById(R.id.sendmessage);
		btn_audio = (Button) findViewById(R.id.voice);
		btn_video = (Button) findViewById(R.id.video);
		btn_audio.setOnLongClickListener(this);//ע�����
		btn_audio.setOnTouchListener(new MyClickListener());//�����ɿ�
		
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, sen);
		lo=new Location(SendHealthHelpActivity.this);
		
		//���������б�ķ��
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	    //��adapter ��ӵ�spinner��
	    spinner.setAdapter(adapter);
	    
	    //����¼�Spinner�¼����� 
	    spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
	    
	    sendmessage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pro.setVisibility(View.VISIBLE);
			    send=new Send();
			    send.execute();
			    
				
			}
		});
	    
	    
	    
	    btn_video.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(SendHealthHelpActivity.this,Video.class);  
		        startActivityForResult(intent, 100);
			}
		});
	    close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	    
	}
	
	
	
	//ʹ��������ʽ����

	class SpinnerSelectedListener implements OnItemSelectedListener{

	    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	    	
	    	if (arg2 != 0)
	    		edt.setText(edt.getText().toString() + sen[arg2] + "...");
	    	
	    }

	    public void onNothingSelected(AdapterView<?> arg0) {

	    }
	}
	
	private class Send extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) { 
        	Map<String,Object> a=new HashMap<String, Object>();
			a.put("kind",3);
			a.put("content", edt.getText().toString()); 
			if(!video_file.equals("")){
				a.put("videosign", "1");
			}
			else 
				a.put("videosign", "0");
			if(!audio_file.equals("")){
				a.put("audiosign", "1");
			}
			else 
				a.put("audiosign", "0");
			a.put("latitude",lo.GetLatitude());
			a.put("longitude",lo.Getlongtitude());
			Map<String,Object> b= new HashMap<String, Object>();
			File file=new File(video_file);
			b.put("video",file);
			file=new File(audio_file);
			b.put("audio",file);
			b.put("username",PushConfig.username);
			b.put("message", a);
			
            return PushSender.sendMessage("helpmessage", b,5000,60000);
        }
        @Override
        protected void onPreExecute() {   
        	
        }
        @Override
        protected void onPostExecute(String result) {   	
        	
        	if(result.equals("network error")){
        		Toast.makeText(SendHealthHelpActivity.this,"����û������", Toast.LENGTH_SHORT).show();
            	//pro.setVisibility(View.INVISIBLE);
        	}
        	if(result.equals("error")){
        		Toast.makeText(SendHealthHelpActivity.this,"���ӷ�����ʧ��", Toast.LENGTH_SHORT).show();
            	//pro.setVisibility(View.INVISIBLE);
        	}
            try {
            	switch (new JSONObject(result).getInt("state")) {
            	case 1:
            		Toast.makeText(SendHealthHelpActivity.this, "���ͳɹ�", Toast.LENGTH_SHORT).show();
            		ControlActivity.SetPager(1);
            		finish();
            		break;
            	default:
            		Toast.makeText(SendHealthHelpActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
            	}
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            pro.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
        }
    }
	
	
	//�¼���Ĵ��� 8/5
		//���ɵ���Ƶ����Ƶ�ļ����ɴ���audio_file��video_file�С�
		
		public boolean onLongClick(View v) {//ʵ�ֽӿ��еķ���  
			
			  if(v == btn_audio){//�����µ��ǰ�ťʱ 
				  isLongClick=true;
				  //�������¼������
				  dir = new File(SD_CARD_TEMP_DIR);
					if (!dir.exists()) {
						dir.mkdir();
					}
				  recorder();	
			  }  
			  return false;  
		}
		
		private class MyClickListener implements OnTouchListener{
			  public boolean onTouch(View v, MotionEvent event) {
				  if(isLongClick)
				     switch (event.getAction()) {
				     //����̧���Ķ���
				     case MotionEvent.ACTION_UP:
				    	 System.out.println("̧�𡣡���");
				    	 try{
				    		 System.out.print("******����try");
								if(mRecAudioFile != null){
									/* ֹͣ¼�� */
									mMediaRecorder.stop();
									mMediaRecorder.release();
									mMediaRecorder = null;
								}
								audio_file = mRecAudioFile.getAbsolutePath();
								System.out.println("¼���ɹ�" + audio_file);
								Toast.makeText(SendHealthHelpActivity.this, "¼���ɹ�" + audio_file, Toast.LENGTH_SHORT).show();
							}catch(Exception e) {
									System.out.print("release");
									e.printStackTrace();
							}
				    	 isLongClick=false;
				    	 break;
				     default:
				    	 break;
				     }
				     return false;
			  }
		}
		
		protected void recorder(){
			//try{
				/* ����Ƿ����SD�� */
				if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
				{
					/* �õ�SD����·�� */
					mRecAudioPath = Environment.getExternalStorageDirectory();
					//Toast.makeText(getApplicationContext(), "SD������", Toast.LENGTH_LONG).show();
					
				}
				else
				{
					Toast.makeText(getApplicationContext(), "û��SD��", Toast.LENGTH_LONG).show();
				}
				
				Toast.makeText(getApplicationContext(), "��ס¼����̧��ֹͣ", Toast.LENGTH_SHORT).show();
				/*createTempFile(String prefix, String suffix, File directory)��
				��ָ��Ŀ¼�д���һ���µĿ��ļ���ʹ�ø�����ǰ׺�ͺ�׺�ַ������������ơ�*/
				try {
					mRecAudioFile = File.createTempFile(strTempFile, ".amr", dir);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("1");
					e.printStackTrace();
				}
				//Toast.makeText(getApplicationContext(), "��ʼ¼��......", Toast.LENGTH_SHORT).show();
				/*ʵ����*/
				mMediaRecorder = new MediaRecorder();
				mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
				mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				mMediaRecorder.setOutputFile(mRecAudioFile.getAbsolutePath());
				try {
					mMediaRecorder.prepare();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					System.out.println("2");
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("3");
					e.printStackTrace();
				}
				System.out.println("prepare()");
				mMediaRecorder.start();
				System.out.println("start()");
				//btn_audio_start.setText("����¼��");
				/*��ʱ���ȸ�λ��������*/
				//timer.setBase(SystemClock.elapsedRealtime());
				//timer.start();
			//}catch(IOException e) {
				//e.printStackTrace();
			//}
		}

		public static String getSDPath() {
			File sdDir = null;
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
			if (sdCardExist) {
				sdDir = Environment.getExternalStorageDirectory();// ��ȡ��Ŀ¼
			} else {
				return null;
			}
			//System.out.println("sd·���ǣ�" + sdDir.toString());
			return sdDir.toString();

		}
		
		private class btn_video_Listener implements OnClickListener{
			public void onClick(View v){
				 Intent intent=new Intent(SendHealthHelpActivity.this,Video.class);  
		         startActivityForResult(intent, 100);
			}
		}
		
		@Override  
	    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
	    {  
	        //���Ը��ݶ���������������Ӧ�Ĳ���  
	        if(20==resultCode)  
	        {  
	            audio_file = data.getExtras().getString("audio_file");
	            Toast.makeText(getApplicationContext(), audio_file, Toast.LENGTH_LONG).show();
	        } 
	        if(21==resultCode)  
	        {  
	        	video_file = data.getExtras().getString("video_file");
	        	Toast.makeText(getApplicationContext(), video_file, Toast.LENGTH_LONG).show();
	        } 
	        super.onActivityResult(requestCode, resultCode, data);  
	    }

}
