package cn.edu.pku.penglinhan.select_dormitory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import cn.edu.pku.penglinhan.select_dormitory.room.room;
import cn.edu.pku.penglinhan.select_dormitory.util.NetUtil;
import cn.edu.pku.penglinhan.select_dormitory.student.student;
/**
 * Created by Administrator on 2017/12/19 0019.
 */

public class function extends Activity implements View.OnClickListener{
    private static final int UPDATE_TODAY_WEATHER = 1;/*与线程有关*/
    private static final int UPDATE_ROOM = 2;/*与线程有关*/
    private Button btn3,btn2;
    private Logger logger;
    private ListView List;
    private ImageView btn1;
    private ArrayAdapter<String> adapter;
    private int gender;
    private String number;
    private TextView textview;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    student ssss = (student) msg.obj;
                    update(ssss);
                    break;
                case UPDATE_ROOM:
                    room sss = (room) msg.obj;
                    new AlertDialog.Builder(function.this).setTitle("查询结果").setMessage("5号楼 剩余空床数"+sss.getRoom5()+"\n13号楼 剩余空床数"+sss.getRoom13()+"\n14号楼 剩余空床数"+sss.getRoom14()+"\n8号楼 剩余空床数"+sss.getRoom8()+"\n9号楼 剩余空床数"+sss.getRoom9()).setPositiveButton("返回", null).show();
                    break;
                default:
                    break;
            }
        };
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function);
        btn1 = (ImageView) findViewById(R.id.title_back);
        btn1.setOnClickListener(this);
        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(this);
        btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(this);
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(function.this,"网络OK！", Toast.LENGTH_LONG).show();
        }else
        {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(function.this,"网络挂了！", Toast.LENGTH_LONG).show();
        }
        initView();
    }
    private void initView() {
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        List = (ListView) findViewById(R.id.list);
        textview = (TextView) findViewById(R.id.title_name);
        number=null;
        Intent intent = getIntent();
        if (intent != null) {
            number = intent.getStringExtra("number");
        }
        Log.d("ab", number);
        textview.setText("当前学号："+number);
        connet1(number);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                /*返回的参数*/
                finish();
                break;
            case R.id.btn2:
                connet2();
                break;
            case R.id.btn3:
                Intent i=new Intent(function.this, select_number.class);
                i.putExtra("number",number);
                startActivity(i);
                break;
            default:
                break;
        }
    }
    public void connet2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection con = null;
                room room=null;
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

                    URL url = new URL("https://api.mysspku.com/index.php/V1/MobileCourse/getRoom?gender="+gender);
                    Log.d("b", url.toString());
                    con = (HttpsURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("b", str);
                    }
                    room=jiexi2(response.toString());//用与解析网上数据
                    //Log.d("abc1", room.toString());
                    if(room !=null){
                        Log.d("abc2",room.toString());
                        //通过消息机制将解析的天气对象，通过消息发送给主线程，主线程接受消息后调用更新函数来更新界面数据
                        Message msg = new Message();
                        msg.what = UPDATE_ROOM;
                        msg.obj = room;
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
    public void connet1(final String number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection con = null;
                student student=null;
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

                    URL url = new URL("https://api.mysspku.com/index.php/V1/MobileCourse/getDetail?stuid="+number);
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
                    student=jiexi1(response.toString());//用与解析网上数据
                    Log.d("abc1", response.toString());
                    if(student !=null){
                        Log.d("abc2",student.toString());
                        //通过消息机制将解析的天气对象，通过消息发送给主线程，主线程接受消息后调用更新函数来更新界面数据
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = student;
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
    private student jiexi1(String xmldata){
        student student = null;
        String[] sring=xmldata.split("\"");
            for (int i=0;i<sring.length;i++) {
                //Log.d("abc", sring[i]);
                if(sring[i].equals(":0,"))
                {student = new student();}
                if (sring[i].equals("studentid")) {i=i+2;
                    student.setstudentid(sring[i]);
                } else if (sring[i].equals("name")) {i=i+2;
                    student.setname(sring[i]);
                } else if (sring[i].equals("gender")) {i=i+2;
                    student.setgender(sring[i]);if(sring[i].equals("男")){gender=1;}else{gender=2;}
                } else if (sring[i].equals("vcode")) {i=i+2;
                    student.setvcode(sring[i]);
                } else if (sring[i].equals("room")) {i=i+2;
                    student.setroom(sring[i]);
                } else if (sring[i].equals("building")) {i=i+2;
                    student.setbuilding(sring[i]);
                } else if (sring[i].equals("location")) {i=i+2;
                    student.setlocation(sring[i]);
                } else if (sring[i].equals("grade")) {i=i+2;
                    student.setgrade(sring[i]);
                }
            }
        return student;
    }
    private room jiexi2(String xmldata){
        room room= null;
        String[] sring=xmldata.split("\"");
        for (int i=0;i<sring.length;i++) {
            //Log.d("abc", sring[i]);
            if(sring[i].equals(":0,"))
            {room = new room();}
            if (sring[i].equals("5")) {i=i+1;
                room.setroom5(sring[i].substring(1,sring[i].length()));
            } else if (sring[i].equals("13")) {i=i+1;
                room.setroom13(sring[i].substring(1,sring[i].length()));
            } else if (sring[i].equals("14")) {i=i+1;
                room.setroom14(sring[i].substring(1,sring[i].length()));
            } else if (sring[i].equals("8")) {i=i+1;
                room.setroom8(sring[i].substring(1,sring[i].length()));
            } else if (sring[i].equals("9")) {i=i+1;
                room.setroom9(sring[i].substring(1,sring[i].length()-2));
            }
        }
        return room;
    }
    void update(student sss){
        List<String> data = new ArrayList<String>();
        data.add("学号："+sss.getstudentid());
        data.add("名字："+sss.getname());
        data.add("性别："+sss.getgender());
        data.add("效验码："+sss.getvcode());
        if(sss.getroom()==null){data.add("宿舍号：");}else {data.add("宿舍号："+sss.getroom());}
        if(sss.getbuilding()==null){data.add("楼号：");}else {data.add("楼号："+sss.getbuilding());}
        data.add("校区："+sss.getlocation());
        data.add("年级："+sss.getgrade());
        Log.d("abc3",data.toString());
        int size = data.size();
        String[] student_List = (String[]) data.toArray(new String[size]);
        adapter = new ArrayAdapter<String>(function.this, android.R.layout.simple_list_item_1, student_List);
        List.setAdapter(adapter);
    }
}
