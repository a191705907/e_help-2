package client.ui;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssistTipsActivity extends ExpandableListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.assist_tips);
		
		
		//��������ͼ����ʾ����  
        String[] assist_type = new String[] { "  ����֪ʶ", "  ����֪ʶ", "  ��ȫ֪ʶ",};  
        //����ͼ��ʾ����  
        String[][] type_item = new String[][] {  
                { "�Ҿ�", "�ҵ�", "����", "ֲ��", "Ӥ�׶�"},  
                { "���ಡ", "�з�", "���Լ���", "��������"},  
                { "��������", "����С͵","��������", "Σ�ս�����", "��Ʒ" } 
        }; 
		
		//����һ��List����List����Ϊһ����Ŀ�ṩ����  
        List<Map<String, String>> groups = new ArrayList<Map<String,String>>();
        List<List<Map<String, String>>> childs = new ArrayList<List<Map<String,String>>>();
        for(int i=0; i<assist_type.length; i++){
        	Map<String, String> group = new HashMap<String, String>();
        	group.put("group", assist_type[i]);
        	groups.add(group);
        	 	
        	List<Map<String, String>> child = new ArrayList<Map<String,String>>();
        	for(int j=0; j<type_item[i].length; j++){
        		Map<String, String> child_data = new HashMap<String, String>();
        		child_data.put("child", type_item[i][j]);
        		child.add(child_data);
        	}
        	childs.add(child);
        }
        
       
        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(this  
                , groups  
                , R.layout.tips_group
                , new String[]{"group"}  
                , new int[] {R.id.groupTo}  
                , childs  
                , R.layout.tips_child
                , new String[]{"child"}  
                , new int[] {R.id.childTo}  
                );  
        //��SimpleExpandableListAdapter�������ø���ǰ��ExpandableListActivity  
        setListAdapter(adapter); 
	}
}
