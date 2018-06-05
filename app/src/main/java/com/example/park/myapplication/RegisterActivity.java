package com.example.park.myapplication;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.regex.Pattern;


public class RegisterActivity extends Activity {

    EditText textId, textPwd, textName;
    Button joinBtn, regBtn;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textId   = (EditText) findViewById(R.id.userId);    /* 연결을 시작하기전 준비하는 단계*/
        textPwd  = (EditText) findViewById(R.id.userPwd);
        textName = (EditText) findViewById(R.id.textName);
        joinBtn  = (Button) findViewById(R.id.joinBtn);
        regBtn   = (Button)findViewById(R.id.regBtn);

        joinBtn.setOnClickListener(btnListener);
        regBtn.setOnClickListener(btnListener);

    }
    public class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected  void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            /*
            * 실제 연결이 진행되는 단계 어플리케이션에서 입력한 ID,PW,NAME 값을 strings에 받음
            * strings 에 받은 것을 꺼내려면 strings[0]를 사용, 먼저보낸 것은 0, 그 다음 보낸 것은 1 순으로 설정
            * URL 설정
            * 목적 Jsp를 strings[0], 보낼 param을 strings[1]로 받음
            * 받은 strings를 다시 semdMsg를 통해 jsp와 통신
            * JSP에 연결되어 해당 기능을 수행하고나면 jsp에 out.print("ok");를 통해 안드로이드로 ok 라는 문자열이
            * 안드로이드의 receiveMsg에 넘어 오게 됨
            * ok라는 문자열을 공백을 재거 해주고 나서 onPostExecute로 받아서 조건에 맞게 구현
            * */
            try {

                String str;

                URL url = new URL("http://192.168.100.71:8080/Android/"+strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "param=" + strings[1];
                System.out.println(sendMsg);
                System.out.println(strings[0]);
                System.out.println(strings[1]);
                osw.write(sendMsg);
                osw.flush();
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
        /*
        통신 후 recviveMsg를 통해 수행하는 onPostExecute
        * */
        @Override
        protected void onPostExecute(String receiveMsg){
            super.onPostExecute(receiveMsg);

            if(receiveMsg != null){
                Log.d("ASYNC","result="+receiveMsg);
            }
            if(receiveMsg.trim().equals("ok")){
                /*
                * Alert창 만들기
                * */
                AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                alert.setMessage("회원가입을 축하합니다.");
                alert.show();

                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else if(receiveMsg.trim().equals("true")){
                AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                alert.setMessage("중복 된 ID 입니다.");
                alert.show();


            }else{
                AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                alert.setMessage("사용 가능한 ID 입니다.");
                alert.show();

            }
        }
    }


    final View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            EditText textId   = (EditText) findViewById(R.id.textId);
            EditText textPwd  = (EditText) findViewById(R.id.textPwd);
            EditText textName = (EditText) findViewById(R.id.textName);
            String   ID       = textId.getText().toString();
            String   PWD      = textPwd.getText().toString();
            String   NAME     = textName.getText().toString();
            boolean  regId    = Pattern.matches("^[a-zA-Z0-9]*$", ID);
            boolean  regName  = Pattern.matches("^[a-zA-Z가-힣]*$", NAME);


            switch (view.getId()) {
                case R.id.joinBtn:
                   try {
                        if (ID.length() == 0 || regId == false) {
                            Toast.makeText(RegisterActivity.this, "ID는 공백일 수 없으며 숫자와 영어만 가능합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (PWD.length() == 0) {
                            Toast.makeText(RegisterActivity.this, "패스워드는 공백일 수 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (NAME.length() == 0 || regName == false) {
                            Toast.makeText(RegisterActivity.this, "이름을 입력해 주세요 (한글,영어)", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            String jsp = "insert.jsp";
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("ID",ID);
                            jsonObject.put("PWD",PWD);
                            jsonObject.put("NAME",NAME);

                            new CustomTask().execute(jsp,jsonObject.toString());
                            /*
                             ID, PWD, NAME 값을 가지고 DB연결 MEMBER TABLE INSERT
                            */

                        }
                    } catch (Exception e) {
                    }
                    break;

                case R.id.regBtn:

                    try {
                        if (ID.length() == 0 || regId == false) {
                            Toast.makeText(RegisterActivity.this, "ID는 공백일 수 없으며 숫자와 영어만 가능합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            String jsp = "regId.jsp";
                            new CustomTask().execute(jsp,ID);
                        }
                    } catch (Exception e) {
                    }
                    break;
            }
        }
    };
}


