package client.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class ChgPwdActivity extends Activity {
	private Button makesure,close;
	private EditText oldpsd,newpsd,newpsd1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//���ø��෽�����þ�����Զ�����
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_password);//ͨ�������ļ���id���ø�Activity��ʹ�õĲ����ļ�
        makesure=(Button)findViewById(R.id.modifypsd);
        close=(Button)findViewById(R.id.backpsd);
        
        oldpsd=(EditText)findViewById(R.id.oldpsd);
        newpsd=(EditText)findViewById(R.id.newpsd1);
        newpsd1=(EditText)findViewById(R.id.newpsd2);
        
        close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        
        makesure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
    }

}
