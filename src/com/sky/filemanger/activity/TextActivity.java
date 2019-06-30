package com.sky.filemanger.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;

import com.sky.filemanger.R;
import java.io.*;
import android.view.*;
import android.widget.*;
import android.view.View.*;
import android.text.*;

public class TextActivity extends Activity
{

	

    private Intent intent;
    private TextView textView;
    private TextView textFileName;
    private File file;
    private String path;
    private Handler handler;
	private Button btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.textview);
		intent = getIntent();
		path = intent.getStringExtra("textpath");
		file = new File(path);
		initUI();
		new Thread(new ReadThread("UTF-8")).start();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg){
				textView.append(msg.obj.toString());
			}
		};
    }

    private void initUI(){
		textView = (TextView) findViewById(R.id.text);
		textFileName = (TextView) findViewById(R.id.textFileName);
		btnMenu = (Button)findViewById(R.id.textviewbtnMenu);
		textFileName.setText(file.getName());
		btnMenu.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					openMenu();
    }
	});
	}
	private void openMenu(){
		PopupMenu popMenu;
		popMenu = new PopupMenu(TextActivity.this, btnMenu);
		popMenu.getMenuInflater().inflate(R.menu.menu, popMenu.getMenu());
		popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

				@Override
				public boolean onMenuItemClick(MenuItem p1){
					// TODO: Implement this method
					switch(p1.getItemId()){
						case R.id.utf_ecode:
							new Thread(new ReadThread("UTF-8")).start();
							break;
						case R.id.gbk_ecode:
							new Thread(new ReadThread("GBK")).start();
							break;
					}
					return true;
				}
			});
		popMenu.show();

	}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
		switch (keyCode){
			case KeyEvent.KEYCODE_BACK:
				finish();
				break;
				case KeyEvent.KEYCODE_MENU:
					openMenu();
				break;
		}
		return true;
    }

    @Override
    protected void onDestroy(){
		super.onDestroy();
    }

    class ReadThread implements Runnable{
		private BufferedReader br;
		public ReadThread(String charSet){
			try{
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charSet));
			}
			catch (UnsupportedEncodingException e){
				e.printStackTrace();
			}
			catch (FileNotFoundException e){
				e.printStackTrace();
			}
			textView.setText(null);
		}
		@Override
		public void run(){
			StringBuilder sb=new StringBuilder();
			String text="";
			int lineNumber=0;
			try{
				while ((text = br.readLine()) != null){
					sb.append(text + "\n");
					++lineNumber;
					if (lineNumber % 100 == 0){
						sendMessage(sb);
						sb = new StringBuilder();
					}
				}
				sendMessage(sb);
			}
			catch (Exception e){
				// TODO 鑷姩鐢熸垚鐨� catch 鍧�
				e.printStackTrace();
			}
			finally{
				try{
					br.close();
				}
				catch (IOException e){
					// TODO 鑷姩鐢熸垚鐨� catch 鍧�
					e.printStackTrace();
				}
			}


		}
	}

	private void sendMessage(StringBuilder sb){
		Message msg = new Message();
		msg.obj = sb;
		handler.sendMessage(msg);
	}
}
