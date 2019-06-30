package com.sky.filemanger.domain;

import android.widget.*;
import android.text.*;
import com.sky.filemanger.activity.*;
import java.util.*;
import android.view.*;
import android.util.*;
import android.view.WindowManager.*;
import android.graphics.drawable.*;
import android.graphics.*;
import android.widget.AdapterView.*;
import android.content.*;
import android.app.*;

public class PopListMenu extends PopupWindow{
	private View layout;
	private int x;
	private int y;
	private MyOnItemClickListener mocl;
	public PopListMenu(Context context, List<String> listContent, int scourse, int id, int x, int y){
		super(context);
		this.x = x;
		this.y = y;
		layout =((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(scourse,null);
		layout.setFocusable(true);
		layout.setFocusableInTouchMode(true);
		
		ListView menuList = (ListView) layout.findViewById(id);		
		ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
														   android.R.layout.simple_list_item_1, listContent);
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		setContentView(layout);
		setWidth(metrics.widthPixels/2);
		setHeight(LayoutParams.WRAP_CONTENT);
		setTouchable(true);
		setFocusable(true);
		setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setOutsideTouchable(true);
		update();
		menuList.setAdapter(aa);
		menuList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
										int arg2, long arg3){
					
					dismiss();
					mocl.onClick(arg2);
					
				}
			});

	}
	public void show(){
		showAtLocation(layout, Gravity.BOTTOM | Gravity.RIGHT,
							   x, y);
	}
	public void close(){
		dismiss();
	}
	public boolean isShowing(){
		return isShowing();
	}
	public void setOnItemClickListener(MyOnItemClickListener mocl){
		this.mocl = mocl;
	}
}
