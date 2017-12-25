package cn.edu.pku.penglinhan.select_dormitory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import cn.edu.pku.penglinhan.select_dormitory.room.room;
import cn.edu.pku.penglinhan.select_dormitory.student.student;
import cn.edu.pku.penglinhan.select_dormitory.util.NetUtil;
import android.graphics.Color;
/**
 * Created by Administrator on 2017/12/20 0020.
 */

public class select extends Activity implements View.OnClickListener{
    private static final int UPDATE_TODAY_WEATHER = 1;/*与线程有关*/
    private Button btn7;
    private Logger logger;
    private TextView t1_1,t1_2,t2_1,t2_2,t3_1,t3_2,louhao;
    private String number,xuehao;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    String www = (String) msg.obj;
                    if(www.equals("{\"errcode\":0}")) {
                        Intent i=new Intent(select.this, function.class);
                        i.putExtra("number",xuehao);
                        startActivity(i);
                    }
                    else {
                        new AlertDialog.Builder(select.this).setTitle("提示").setMessage("填写错误").setPositiveButton("返回", null).show();
                    }
                    break;
                default:
                    break;
            }
        };
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_dormitory);
        btn7 = (Button) findViewById(R.id.btn7);
        btn7.setOnClickListener(this);
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(select.this,"网络OK！", Toast.LENGTH_LONG).show();
        }else
        {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(select.this,"网络挂了！", Toast.LENGTH_LONG).show();
        }
        initView();
    }
    private void initView() {
        t1_1 = (TextView) findViewById(R.id.same_number1);
        t1_2 = (TextView) findViewById(R.id.same_number1_password);
        t2_1 = (TextView) findViewById(R.id.same_number2);
        t2_2 = (TextView) findViewById(R.id.same_number2_password);
        t3_1 = (TextView) findViewById(R.id.same_number3);
        t3_2 = (TextView) findViewById(R.id.same_number3_password);
        louhao = (TextView) findViewById(R.id.louhao);
        Intent intent = getIntent();
        if (intent != null) {xuehao = intent.getStringExtra("xuehao");number = intent.getStringExtra("number");}
        if(number.equals("1")){
            t2_1.setEnabled(false);t2_2.setEnabled(false);t3_1.setEnabled(false);t3_2.setEnabled(false);
            t2_1.setBackgroundColor(Color.parseColor("#5B5B5B"));t2_2.setBackgroundColor(Color.parseColor("#5B5B5B"));t3_1.setBackgroundColor(Color.parseColor("#5B5B5B"));t3_2.setBackgroundColor(Color.parseColor("#5B5B5B"));
        }
        else if(number.equals("2")){t3_1.setEnabled(false);t3_2.setEnabled(false);t3_1.setBackgroundColor(Color.parseColor("#5B5B5B"));t3_2.setBackgroundColor(Color.parseColor("#5B5B5B"));}
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn7:
                /*返回的参数*/
                connet();
                break;
            default:
                break;
        }
    }
    public void connet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection con = null;
                room room=null;
                logger = Logger.getLogger("net");
                byte[] data = getRequestData().toString().getBytes();//获得请求体
                try {
                    trustAllHttpsCertificates();
                    HostnameVerifier hv = new HostnameVerifier() {
                        public boolean verify(String urlHostName, SSLSession session) {
                            logger.info("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                            return true;
                        }
                    };
                    HttpsURLConnection.setDefaultHostnameVerifier(hv);

                    URL url = new URL("https://api.mysspku.com/index.php/V1/MobileCourse/SelectRoom");//设置连接超时时间
                    Log.d("b", url.toString());
                    con = (HttpsURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setConnectTimeout(8000);
                    con.setDoInput(true);                  //打开输入流，以便从服务器获取数据
                    con.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
                    con.setUseCaches(false);
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(data);
                    int response = con.getResponseCode();            //获得服务器的响应码
                    if(response == con.HTTP_OK) {
                        InputStream inptStream = con.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
                        StringBuilder zzz = new StringBuilder();
                        String str;
                        while((str = reader.readLine()) != null) {
                            zzz.append(str);
                            Log.d("b", str);
                        }

                        //通过消息机制将解析的天气对象，通过消息发送给主线程，主线程接受消息后调用更新函数来更新界面数据
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = zzz.toString();;
                        handler.sendMessage(msg);

                    }
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    if(con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }
    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    public StringBuffer getRequestData() {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            stringBuffer.append("num").append("=").append(number).append("&");
            stringBuffer.append("stuid").append("=").append(xuehao).append("&");
            stringBuffer.append("stu1id").append("=").append(t1_1.getText().toString()).append("&");
            stringBuffer.append("v1code").append("=").append(t1_2.getText().toString()).append("&");
            if(number.equals("1")){
                stringBuffer.append("stu2id").append("=").append("").append("&");
                stringBuffer.append("v2code").append("=").append("").append("&");
                stringBuffer.append("stu3id").append("=").append("").append("&");
                stringBuffer.append("v3code").append("=").append("").append("&");
            }
            else if(number.equals("2")){
                stringBuffer.append("stu2id").append("=").append(t2_1.getText().toString()).append("&");
                stringBuffer.append("v2code").append("=").append(t2_2.getText().toString()).append("&");
                stringBuffer.append("stu3id").append("=").append("").append("&");
                stringBuffer.append("v3code").append("=").append("").append("&");}
            else{
            stringBuffer.append("stu2id").append("=").append(t2_1.getText().toString()).append("&");
            stringBuffer.append("v2code").append("=").append(t2_2.getText().toString()).append("&");
            stringBuffer.append("stu3id").append("=").append(t3_1.getText().toString()).append("&");
            stringBuffer.append("v3code").append("=").append(t3_2.getText().toString()).append("&");}
            stringBuffer.append("buildingNo").append("=").append(louhao.getText().toString()).append("&");
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
}
