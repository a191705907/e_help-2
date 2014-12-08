package client.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import communicate.PushConfig;
import communicate.PushSender;

public class PersonalInfoActivity extends Activity {
	
	private ImageView portrait;

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// ����
	private static final int PHOTO_REQUEST_GALLERY = 2;// �������ѡ��
	private static final int PHOTO_REQUEST_CUT = 3;// ���
	private Map<String,Object> data=new HashMap<String,Object>();
	private TextView nameTV,sexTV,ageTV,phoneTV,addressTV,diseaseTV,pointTV;
	private GetPersonalInfo getpersonalinfo;
	private Button makesure;
	private RatingBar creditRB;
	private String photo;
	private Bitmap bitmap=null;
	private MyProgressBar myProgressBar;
	// ����һ���Ե�ǰʱ��Ϊ���Ƶ��ļ�
	File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_info);
		
		nameTV = (TextView)findViewById(R.id.personal_info_name);
		sexTV = (TextView)findViewById(R.id.personal_info_sex);
		ageTV = (TextView)findViewById(R.id.personal_info_age);
		phoneTV = (TextView)findViewById(R.id.personal_info_phone);
		addressTV = (TextView)findViewById(R.id.personal_info_address);
		diseaseTV = (TextView)findViewById(R.id.personal_info_disease);
		creditRB = (RatingBar)findViewById(R.id.personal_info_ratingBar);
		pointTV = (TextView)findViewById(R.id.personal_info_point);
		portrait = (ImageView)findViewById(R.id.personal_info_portrait);
		makesure=(Button)findViewById(R.id.makesure);
		myProgressBar = (MyProgressBar)findViewById(R.id.progressBar1);
		myProgressBar.setProgress(0, 10);
		creditRB.setRating((float) 0.0);
		pointTV.setText("0");
		//portrait.setImageResource(R.drawable.bb_boy);
		
		TextView label_portrait = (TextView)findViewById(R.id.personal_info_label_portrait);
		label_portrait.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(PersonalInfoActivity.this)
				       .setTitle("ͷ������")
				       .setPositiveButton("����", new DialogInterface.OnClickListener() {		
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								// ����ϵͳ�����չ���
								Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								// ָ������������պ���Ƭ�Ĵ���·��
								intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(tempFile));
								startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
							}
				       	})
				       	.setNegativeButton("���", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								Intent intent = new Intent(Intent.ACTION_PICK, null);
								intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
								startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
						}}).show();
			}	
		});
		
		TextView label_name = (TextView)findViewById(R.id.personal_info_lable_name);
		label_name.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final EditText et = new EditText(PersonalInfoActivity.this);
				et.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(20)});
				new AlertDialog.Builder(PersonalInfoActivity.this)
					  .setTitle("�ף�ԭ�����ǽ����ϴ�˵�е�")
					  .setView(et)
					  .setIcon(R.drawable.user)
					  .setPositiveButton("ȷ��", new AlertDialog.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String input = et.getText().toString();
							try {
								Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog, false);
								if(input.equals(""))
									Toast.makeText(getApplicationContext(), "�ף��������ݲ���Ϊ��", Toast.LENGTH_SHORT).show();
								else{
									nameTV.setText(input);
									field.set(dialog,true);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}				  
					  })
					  .setNegativeButton("ȡ��",new AlertDialog.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							try {
								Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog,true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}					  
					  })
					  .show();
			}	
		});
		
		TextView label_sex = (TextView)findViewById(R.id.personal_info_label_sex);
		label_sex.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final String[] items = new String[]{"��","Ů"};
				new AlertDialog.Builder(PersonalInfoActivity.this)
				      .setIcon(R.drawable.sex)
				      .setTitle("�ף������Ա���")
				      .setSingleChoiceItems(items,0,new AlertDialog.OnClickListener(){
						  @Override
						  public void onClick(DialogInterface dialog, int which) {
							  // TODO Auto-generated method stub
							  String mysex = items[which];
							  sexTV.setText(mysex);
						  }			
					  })
				     .setPositiveButton("ȷ��", null)
				     .show();
			}		
		});
		
		TextView label_age = (TextView)findViewById(R.id.personal_info_label_age);
		label_age.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final EditText et = new EditText(PersonalInfoActivity.this);
				et.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(3)});
				new AlertDialog.Builder(PersonalInfoActivity.this)
					  .setTitle("�ף����ķ�����")
					  .setIcon(R.drawable.age_tree)
					  .setView(et)
					  .setPositiveButton("ȷ��", new AlertDialog.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String input = et.getText().toString();
							try {
								Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog, false);
								if(input.equals(""))
									Toast.makeText(getApplicationContext(), "�ף��������ݲ���Ϊ��", Toast.LENGTH_SHORT).show();
								else{
									ageTV.setText(input);
									field.set(dialog,true);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}				  
					  })
					  .setNegativeButton("ȡ��",new AlertDialog.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							try {
								Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog,true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}					  
					  })
					  .show();
			}		
		});
		
		TextView label_phone = (TextView)findViewById(R.id.personal_info_label_phone);
		label_phone.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final EditText et = new EditText(PersonalInfoActivity.this);
				et.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(11)});
				new AlertDialog.Builder(PersonalInfoActivity.this)
					  .setTitle("�ף�����������绰����")
					  .setIcon(R.drawable.phone_call)
					  .setView(et)
					  .setPositiveButton("ȷ��", new AlertDialog.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String input = et.getText().toString();
							try {
								Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog, false);
								if(input.equals(""))
									Toast.makeText(getApplicationContext(), "�ף��������ݲ���Ϊ��", Toast.LENGTH_SHORT).show();
								else{
									phoneTV.setText(input);
									field.set(dialog,true);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}	
						}				  
					  })
					  .setNegativeButton("ȡ��",new AlertDialog.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							try {
								Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog,true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}					  
					  })
					  .show();
			}		
		});
		
		TextView label_address = (TextView)findViewById(R.id.personal_info_label_address);
		label_address.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final EditText et = new EditText(PersonalInfoActivity.this);
				et.setSingleLine();
				new AlertDialog.Builder(PersonalInfoActivity.this)
					  .setTitle("�ף���ס���ﰡ")
					  .setView(et)
					  .setIcon(R.drawable.house)
					  .setPositiveButton("ȷ��", new AlertDialog.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String input = et.getText().toString();
							try {
								Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog, false);
								if(input.equals(""))
									Toast.makeText(getApplicationContext(), "�ף��������ݲ���Ϊ��", Toast.LENGTH_SHORT).show();
								else{
									addressTV.setText(input);
									field.set(dialog,true);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}	
						}				  
					  })
					  .setNegativeButton("ȡ��",new AlertDialog.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							try {
								Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog,true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}					  
					  })
					  .show();
			}	
		});
		
		TextView label_disease = (TextView)findViewById(R.id.personal_info_label_disease);
		label_disease.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final EditText et = new EditText(PersonalInfoActivity.this);
				et.setSingleLine();
				new AlertDialog.Builder(PersonalInfoActivity.this)
					  .setTitle("�ף�ע������Ŷ")
					  .setIcon(R.drawable.cross)
					  .setView(et)
					  .setPositiveButton("ȷ��", new AlertDialog.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String input = et.getText().toString();
							try {
								Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog, false);
								if(input.equals(""))	
									Toast.makeText(getApplicationContext(), "�ף��������ݲ���Ϊ��", Toast.LENGTH_SHORT).show();
								else{
									diseaseTV.setText(input);
									field.set(dialog,true);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}				
						}				  
					  })
					  .setNegativeButton("ȡ��",new AlertDialog.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							try {
								Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog,true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}					  
					  })
					  .show();
			}	
		});
		makesure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Makesure ms=new Makesure();
				ms.execute();
			}
		});
		getpersonalinfo=new GetPersonalInfo();
		getpersonalinfo.execute();
	}


	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub

		switch (requestCode) {
			case 	PHOTO_REQUEST_TAKEPHOTO://��ѡ������ʱ����
					startPhotoZoom(Uri.fromFile(tempFile), 150);
					break;

			case 	PHOTO_REQUEST_GALLERY://��ѡ��ӱ��ػ�ȡͼƬʱ
					//���ǿ��жϣ������Ǿ��ò����������¼��õ�ʱ��㲻�ᱨ�쳣����ͬ
					if (data != null)
						startPhotoZoom(data.getData(), 150);
						break;

			case 	PHOTO_REQUEST_CUT://���صĽ��
					if (data != null) 
						setPicToView(data);
					break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	//�����м��ú��ͼƬ��ʾ��UI������
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			bitmap = bundle.getParcelable("data");
		Drawable drawable = new BitmapDrawable(bitmap);
		portrait.setBackgroundDrawable(drawable);
		}
	}
	
	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// cropΪtrue�������ڿ�����intent��������ʾ��view���Լ���
		intent.putExtra("crop", "true");

		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY �Ǽ���ͼƬ�Ŀ��
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}
	
	// ʹ��ϵͳ��ǰ���ڼ��Ե�����Ϊ��Ƭ������
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}	
	
	
	private class GetPersonalInfo extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) { 
        	
        	data.put("username",PushConfig.username); 
        	photo=PushSender.sendMessage("getavatar",data);
            return PushSender.sendMessage("getuserinfo",data);
        }
        @Override
        protected void onPreExecute() {   
        	
        }
        @Override
        protected void onPostExecute(String result) {   	
        	if(result.equals("network error")){
        		Toast.makeText(PersonalInfoActivity.this,"����û������", Toast.LENGTH_SHORT).show();
            	//pro.setVisibility(View.INVISIBLE);
        	}
        	if(result.equals("error")){
        		Toast.makeText(PersonalInfoActivity.this,"���ӷ�����ʧ��", Toast.LENGTH_SHORT).show();
            	//pro.setVisibility(View.INVISIBLE);
        	}
            try {
				JSONObject jo=new JSONObject(result);
				JSONObject info=jo.getJSONObject("result");
				nameTV.setText(info.getString("realname"));
				if(info.getString("sex").equals("1"))
					sexTV.setText("��");
				else
					sexTV.setText("Ů");
				ageTV.setText(info.getString("age"));
				phoneTV.setText(info.getString("phone"));
				addressTV.setText(info.getString("address"));
				diseaseTV.setText(info.getString("illness"));
				creditRB.setRating((float)(info.getDouble("credit")));
				jo=new JSONObject(photo);
				portrait.setBackgroundDrawable(ControlActivity.base64ToDrawable(jo.getString("avatar")));
				myProgressBar.setProgress(info.getInt("score")-info.getInt("scoreMin"),info.getInt("scoreMax")-info.getInt("scoreMin"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            	
            super.onPostExecute(result);
        }
    }
	
	private class Makesure extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) { 
        	Map<String,Object> a=new HashMap<String, Object>();
        	a.put("realname",nameTV.getText().toString());
			if(sexTV.getText().toString().equals("��"))
				a.put("sex", "1");
			else
				a.put("sex", "2");
			
			a.put("age", ageTV.getText().toString());
			a.put("phone", phoneTV.getText().toString());
			a.put("address", addressTV.getText().toString());
			a.put("illness", diseaseTV.getText().toString());
			
			Map<String,Object> b= new HashMap<String, Object>();
			b.put("username",PushConfig.username);
			b.put("changemessage", a);
			if(bitmap!=null)
				b.put("avatar",Bitmap.createScaledBitmap(bitmap,70,70, true));
            return PushSender.sendMessage("updateuserinfo", b);
        }
        @Override
        protected void onPreExecute() {   
        	
        }
        @Override
        protected void onPostExecute(String result) {   	
        	
        	if(result.equals("network error")){
        		Toast.makeText(PersonalInfoActivity.this,"����û������", Toast.LENGTH_SHORT).show();
        	}
        	if(result.equals("error")){
        		Toast.makeText(PersonalInfoActivity.this,"���ӷ�����ʧ��", Toast.LENGTH_SHORT).show();
        	}
            try {
            	switch (new JSONObject(result).getInt("state")) {
            	case 1:
            		Toast.makeText(PersonalInfoActivity.this, "���ͳɹ�", Toast.LENGTH_SHORT).show();
            		finish();
            		break;
            	default:
            		Toast.makeText(PersonalInfoActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
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
