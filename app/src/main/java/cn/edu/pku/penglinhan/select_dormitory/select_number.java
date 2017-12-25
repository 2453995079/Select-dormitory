package cn.edu.pku.penglinhan.select_dormitory;

/**
 * Created by Administrator on 2017/12/22 0022.
 */
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
import android.widget.RadioButton;
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
public class select_number  extends Activity {
    public RadioButton radio1,radio2,radio3;
    public String number,xuehao;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_select);
        radio1 = (RadioButton) findViewById(R.id.radio1);
        radio2 = (RadioButton) findViewById(R.id.radio2);
        radio3 = (RadioButton) findViewById(R.id.radio3);
        initView();
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {@Override
        public void onClick(View v) {
            if (radio1.isChecked() == true) {
                number = "1";
            }
            if (radio2.isChecked() == true) {
                number = "2";
            }
            if (radio3.isChecked() == true) {
                number = "3";
            }
            Log.d("771", number+"");
            if(number==null) {
                new AlertDialog.Builder(select_number.this).setTitle("提示").setMessage("请选择办理人数").setPositiveButton("返回", null).show();
            }else{
            Intent i=new Intent(select_number.this, select.class);
            i.putExtra("xuehao",xuehao);
            i.putExtra("number",number);
            startActivity(i);}
        }
        });

    }
    private void initView(){
        xuehao=null;
        number=null;
        Intent intent = getIntent();
        if (intent != null) {
            xuehao = intent.getStringExtra("number");
        }
        Log.d("77", xuehao);

    }
}
