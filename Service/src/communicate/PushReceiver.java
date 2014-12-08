package communicate;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import client.ui.ControlActivity;
import client.ui.DetailMessageActivity;
import client.ui.ValidationActivity;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import fragment.MessageFragment;

public class PushReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d("GetuiSdk", "onReceive() action=" + bundle.getInt("action"));
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
		
		case PushConsts.GET_MSG_DATA:
			// ��ȡ͸������
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");
			
			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");
			
			// smartPush��������ִ���ýӿڣ�action��ΧΪ90000-90999���ɸ���ҵ�񳡾�ִ��
			boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
			System.out.println("��������ִ�ӿڵ���" + (result ? "�ɹ�" : "ʧ��"));
			
			if (payload != null && PushConfig.username != "") {
				String data = new String(payload);
				
				Log.d("GetuiSdk", "Got Payload:" + data);
				//����͸����Ϣ
				try {
					JSONObject json = new JSONObject(data);
					String type = json.getString("type");
					JSONObject h_message = json.getJSONObject("data");
					Log.i("PushReceiver", data);
					if (PushConfig.username == "")
						return;
					Intent i = new Intent();
					if (type.equals("help")) {
						// ������Ϣ
						i.setAction("helpmessage");
						i.putExtra("locate", PushConfig.notifyevent);
						i.putExtra("username", h_message.getString("username"));
						i.putExtra("content", h_message.getString("content"));
						i.putExtra("time", h_message.getString("time"));
						i.putExtra("kind", h_message.getString("kind"));
						i.putExtra("audio", h_message.getString("audio"));
						i.putExtra("video", h_message.getString("video"));
						i.putExtra("eventid", h_message.getString("eventid"));
						i.putExtra("avatar", h_message.getString("avatar"));
						
						
						Intent in = new Intent();
						in.setAction("helpmessage");
						if (PushConfig.helpmessage == 0 && PushConfig.aidmessage == 0) {
							//����DetailMessageActivit
							in.putExtra("needhelp", h_message.getString("username"));
							in.putExtra("time", h_message.getString("time"));
							in.putExtra("content", h_message.getString("content"));
							in.putExtra("eid", h_message.getString("eventid"));
							in.putExtra("audio", h_message.getString("audio"));
							in.putExtra("video", h_message.getString("video"));
							in.putExtra("image", h_message.getString("avatar"));
							in.setClass(context, DetailMessageActivity.class);
							
							PushConfig.toevent = h_message.getInt("eventid");
						} else {
							//����ControlActivity
							in.setClass(context, ControlActivity.class);
							
							PushConfig.toevent = -1;
						}

						in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						
						PushConfig.helpmessage++;
						String tickerText = "�յ�һ��������Ϣ\n" + h_message.getString("content");
						String title;
						String content;
						if (PushConfig.helpmessage == 1 && PushConfig.aidmessage == 0) {
							title = h_message.getString("username") + "����������Ϣ";
							content = h_message.getString("content");
						} else if (PushConfig.aidmessage == 0) {
							title = "�յ�" + PushConfig.helpmessage + "��������Ϣ";
							content = h_message.getString("content");
						} else {
							title = "�յ�" + (PushConfig.helpmessage + PushConfig.aidmessage) + "������Ϣ";
							content = "�յ�" + PushConfig.helpmessage + "��������Ϣ��" + PushConfig.aidmessage + "��Ԯ����Ϣ";
						}
						PushConfig.sendNotification(context, tickerText, title, content, in, PushConfig.NOTIFICATION_EVENT);
						
						if (!PushConfig.notifyevent) {
							PushConfig.clearNotification(context, PushConfig.NOTIFICATION_EVENT);
							PushConfig.helpmessage = 0;
						}
						
					} else if (type.equals("aid")) {
						// Ԯ����Ϣ
						i.setAction("aidmessage");
						i.putExtra("username", h_message.getString("username"));
						i.putExtra("content", h_message.getString("content"));
						i.putExtra("time", h_message.getString("time"));
						i.putExtra("eventid", h_message.getString("eventid"));
						i.putExtra("avatar", h_message.getString("avatar"));
						Log.i("test11", "rec");
						
						Intent in = new Intent();
						in.setAction("aidmessage");
						if ((PushConfig.helpmessage == 0 && PushConfig.aidmessage == 0) || PushConfig.toevent == h_message.getInt("eventid")) {
							//����DetailMessageActivit
							Map<String, Object> map = MessageFragment.getEventById(h_message.getString("eventid"));
							if (map != null) {
								in.putExtra("needhelp", (String)map.get("name"));
								in.putExtra("time", (String)map.get("time"));
								in.putExtra("content", (String)map.get("content"));
								in.putExtra("eid", (String)map.get("eid"));
								in.putExtra("video", (String)map.get("video"));
								in.putExtra("audio", (String)map.get("audio"));
								in.putExtra("image", (String)map.get("image"));
								in.setClass(context, DetailMessageActivity.class);
							} else {
								in.setClass(context, ControlActivity.class);
							}
							
							PushConfig.toevent = h_message.getInt("eventid");
						} else {
							//����ControlActivity
							in.setClass(context, ControlActivity.class);
							
							PushConfig.toevent = -1;
						}

						in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
						
						PushConfig.aidmessage++;
						String tickerText = "�յ�һ��Ԯ����Ϣ\n" + h_message.getString("content");
						String title;
						String content;
						if (PushConfig.helpmessage == 0) {
							title = "�յ�" + PushConfig.aidmessage + "��Ԯ����Ϣ";
							content = h_message.getString("content");
						} else {
							title = "�յ�" + (PushConfig.helpmessage + PushConfig.aidmessage) + "������Ϣ";
							content = "�յ�" + PushConfig.helpmessage + "��������Ϣ��" + PushConfig.aidmessage + "��Ԯ����Ϣ";
						}
						PushConfig.sendNotification(context, tickerText, title, content, in, PushConfig.NOTIFICATION_EVENT);
						if (!PushConfig.notifyevent || PushConfig.eventid == h_message.getInt("eventid")) {
							PushConfig.clearNotification(context, PushConfig.NOTIFICATION_EVENT);
							PushConfig.aidmessage = 0;
						}
					} else if (type.equals("endhelp")) {
						// ���������¼�
						i.setAction("finishevent");
						i.putExtra("eventid", h_message.getString("eventid"));
						i.putExtra("username", h_message.getString("username"));
						i.putExtra("time", h_message.getString("time"));
						
						Intent in = new Intent();
						in.setAction("endhelp");
						PushConfig.endevents.add(h_message.getString("username"));
						String tickerText = h_message.getString("username") + "���͵������¼��ѽ���\n�¼�������" + h_message.getString("time");
						String title = "�������" + PushConfig.endevents.size() + "���¼��ѽ���";
						String content = PushConfig.endevents.get(0);
						for (int n = 1; n < PushConfig.endevents.size(); n++) {
							content += "��" + PushConfig.endevents.get(n);
						}
						content += "���͵������¼��ѽ���";
						PushConfig.sendNotification(context, tickerText, title, content, in, PushConfig.NOTIFICATION_END_EVENT);
						if (!PushConfig.notifyevent) {
							PushConfig.clearNotification(context, PushConfig.NOTIFICATION_END_EVENT);
							PushConfig.endevents.clear();
						}
					} else if (type.equals("invite")) {
						// ���Ӻ�������
						Intent in = new Intent(context, ValidationActivity.class);
						PushConfig.addfriend++;
						PushConfig.sendNotification(context, "�յ�һ����������", 
								"�յ�" + PushConfig.addfriend + "����������",
								h_message.getString("info"), in, PushConfig.NOTIFICATION_FRIEND);
						
						i.setAction("addfriend");
						i.putExtra("username", h_message.getString("username"));
						i.putExtra("info", h_message.getString("info"));
						i.putExtra("type", h_message.getString("type"));
						i.putExtra("kind", h_message.getString("kind"));

					} else if (type.equals("agree")) {
						//ͨ��������֤
						i.setAction("becomefriends");
						i.putExtra("username", h_message.getString("username"));
						i.putExtra("type", h_message.getString("type"));
						i.putExtra("agree", h_message.getString("agree"));
					} else if (type.equals("remove")) {
						// �Ƴ�����
						i.setAction("removefriend");
						i.putExtra("username", h_message.getString("username"));
					}
					context.sendBroadcast(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			break;
		case PushConsts.GET_CLIENTID:
			//��ȡClientID(CID)
			//������Ӧ����Ҫ��CID�ϴ��������������������ҽ���ǰ�û��˺ź�CID���й������Ա��պ�ͨ���û��˺Ų���CID������Ϣ����
			PushConfig.clientId = bundle.getString("clientid");
			Log.i("PushReceiver", "cid=" + PushConfig.clientId);
			PushSender.sendClientId();
			break;
		default:
			break;
		}
	}
	
}