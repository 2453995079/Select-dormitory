package cn.edu.pku.penglinhan.select_dormitory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import cn.edu.pku.penglinhan.select_dormitory.util.NetUtil;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public class Lodin extends Activity implements View.OnClickListener{
    private static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView loginbtn;
    private TextView number,password;
    private Logger logger;
    private String result;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String str = (String) msg.obj;
            new AlertDialog.Builder(Lodin.this).setTitle("提示").setMessage("学号或密码错误").setPositiveButton("返回", null).show();
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_lodin);

        loginbtn = (ImageView) findViewById(R.id.btn1);
        loginbtn.setOnClickListener(this);
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(Lodin.this,"网络OK！", Toast.LENGTH_LONG).show();
        }else
        {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(Lodin.this,"网络挂了！", Toast.LENGTH_LONG).show();
        }
        initView();
    }

    private void initView() {
        number = (TextView) findViewById(R.id.number);
        password = (TextView) findViewById(R.id.password);
        result=null;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn1){
            infoGet();
             //Log.d("abc",result);
        }

    }
    public void infoGet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection con = null;
                logger = Logger.getLogger("net");
                try {
                    trustAllHttpsCertificates();
                    HostnameVerifier hv = new HostnameVerifier() {
                        public boolean verify(String urlHostName, SSLSession session) {
                            logger.info("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                            return true;
                        }
                    };
                    HttpsURLConnection.setDefaultHostnameVerifier(hv);
                    URL url = new URL("https://api.mysspku.com/index.php/V1/MobileCourse/Login?username="+number.getText().toString()+"&password="+password.getText().toString());
                    Log.d("abc", url.toString());
                    con = (HttpsURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("abc", str);
                    }
                    result=response.toString();
                    result=result.substring(11,12);
                    Log.d("abc", result);
                    if(result.equals("0")){
                    Intent i=new Intent(Lodin.this, function.class);
                    Log.d("abc",number.getText().toString());
                    i.putExtra("number",number.getText().toString());

                    startActivity(i);
                    }else {
                        Log.d("abc","为什么");
                        Message msg = new Message();
                        msg.obj = "Hello";
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

}

