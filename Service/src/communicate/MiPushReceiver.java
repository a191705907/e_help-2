package communicate;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import client.ui.ControlActivity;
import client.ui.DetailMessageActivity;
import client.ui.ValidationActivity;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import fragment.MessageFragment;

public class MiPushReceiver extends PushMessageReceiver {
    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mStartTime;
    private String mEndTime;
    @Override
    public void onReceiveMessage(Context context, MiPushMessage message) {
		String payload = message.getContent();
		
		if (payload != null && PushConfig.username != "") {
			String data = payload;
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
					// ��Ӻ�������
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
    }
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        } 
		PushConfig.clientId = mRegId;
		Log.i("PushReceiver", "cid=" + PushConfig.clientId);
		PushSender.sendClientId();
    }
}