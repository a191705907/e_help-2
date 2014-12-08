package communicate;

import android.util.Log;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

class GetuiSdkHttpPost {

	public static String httpPost(String action, Map<String, Object> map, int CONNECTION_TIMEOUT, int READ_TIMEOUT) {

		String param = JSONObject.toJSONString(map);

		if (param != null) {

			URL url = null;

			try {
				url = new URL(PushConfig.SERVICEURL + action);
				HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
				urlConn.setDoInput(true); // ���������������ֽ���
				urlConn.setDoOutput(true); // ��������������ֽ���
				urlConn.setRequestMethod("POST");
				urlConn.setUseCaches(false); // ���û���
				urlConn.setRequestProperty("Charset", "utf-8");
				urlConn.setConnectTimeout(CONNECTION_TIMEOUT);
				urlConn.setReadTimeout(READ_TIMEOUT);

				urlConn.connect(); // ���Ӽ�������˷�����Ϣ
				Log.i("HttpPost", "connected to server");

				DataOutputStream dop = new DataOutputStream(urlConn.getOutputStream());
				dop.write(param.getBytes("utf-8"));
				Log.i("HttpPost", "sending data");
				dop.flush(); // ���ͣ���ջ���
				dop.close(); // �ر�

				// ���濪ʼ�����չ���
				BufferedReader bufferReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
				Log.i("HttpPost", "sending finished");
				String result = ""; // ��ȡ��������������
				String readLine = null;
				while ((readLine = bufferReader.readLine()) != null) {
					result += readLine;
				}
				bufferReader.close();
				urlConn.disconnect();

				return result;

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// ���ӷ�����ʧ��
			return "error";
		}
		return "param is null";
	}
	

}
