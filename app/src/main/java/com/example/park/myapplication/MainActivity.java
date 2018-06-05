package com.example.park.myapplication;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.*;
import java.net.*;
import java.util.regex.Pattern;

public class MainActivity extends Activity {
    EditText userId, userPwd;
    Button loginBtn, joinBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userId = (EditText) findViewById(R.id.userId);
        userPwd = (EditText) findViewById(R.id.userPwd);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        joinBtn = (Button) findViewById(R.id.joinBtn);
        loginBtn.setOnClickListener(btnListener);
        joinBtn.setOnClickListener(btnListener);
    }
    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
//                URL url = new URL("http://192.168.100.66:8080/webProject/data.jsp");
                URL url = new URL("http://192.168.100.71:8080/Android/login.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded charset=UTF-8");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = "ID="+strings[0]+"&PWD="+strings[1];
                osw.write(sendMsg);
                osw.flush();
                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }

        @Override
        protected void onPostExecute(String receiveMsg){
            super.onPostExecute(receiveMsg);

            if(receiveMsg !=null){
                Log.d("ASYNC", "msg="+receiveMsg);
            }

            if(receiveMsg.trim().equals("true")) {
                Toast.makeText(MainActivity.this,"로그인",Toast.LENGTH_SHORT).show();
                /*로그인 성공시 다음으로 실행될 class지정*/
                //Intent intent = new Intent(MainActivity.this, 보내고 싶은 액티비티.class);
                //startActivity(intent);
                finish();
            }else if(receiveMsg.trim().equals("false")) {
                 Toast.makeText(MainActivity.this,"아이디or 비밀번호가 다름",Toast.LENGTH_SHORT).show();
                 userId.setText("");
                 userPwd.setText("");
            }
            /*else if(receiveMsg.trim().equals("noId")) {
                Toast.makeText(MainActivity.this,"존재하지 않는 아이디",Toast.LENGTH_SHORT).show();
                userId.setText("");
                userPwd.setText("");
            }*/
        }

    }

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loginBtn : // 로그인 버튼 눌렀을 경우
                    String ID = userId.getText().toString();
                    String PWD = userPwd.getText().toString();
                    boolean regId = Pattern.matches("^[a-zA-Z0-9]*$", ID);

                    try {
                            if (ID.length() == 0 || regId == false) {
                                Toast.makeText(MainActivity.this, "ID는 공백일 수 없으며 숫자와 영어만 가능합니다.", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (PWD.length() == 0) {
                                Toast.makeText(MainActivity.this, "패스워드를 입력해주세요", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                new CustomTask().execute(ID,PWD);
                            }


                    }catch (Exception e) {

                    }break;

                case R.id.joinBtn : // 회원가입
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);

             }
        }
    };
}

