package com.sky.filemanger.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sky.filemanger.R;
import com.sky.filemanger.adapter.FileListAdapter;
import com.sky.filemanger.domain.MyOnItemClickListener;
import com.sky.filemanger.domain.MyProgressListener;
import com.sky.filemanger.domain.PopListMenu;
import com.sky.filemanger.utils.FileUtil;
import com.sky.filemanger.utils.MyFileFilter;
import com.sky.filemanger.utils.OpenFiles;
import android.os.*;

public class MainActivity extends Activity implements OnItemClickListener,
OnItemLongClickListener, OnClickListener {

    private ListView listView;

    private Context context;

    private FileListAdapter adapter;

    private TextView pathInfo;

    private final int location = 0;

    private String fileType = null;

    private Button btnMenu;

    private ListFile listFile_1;

    private ListFile listFile_2;

    private ListFile listFile;

    private Button btnList1;

    private Button btnList2;

    private Button btnlist;

    private Button btnSort;

    private PopListMenu popListMenu;

    private PopListMenu popNewMenu;

    private PopupWindow pop_copy_move;

    private PopupWindow pop_open_operat_btn;

    private View layout_open;

    private View layout_open_operat;

    private boolean isMultySelect = false;

    private File fromFile;

    private Button btnNew;

	public Handler handler;

	private char state=0;  //1涓哄崟鏂囦欢澶嶅埗   2涓哄崟涓枃浠剁Щ鍔�   3涓哄鏂囦欢澶嶅埗  4涓哄鏂囦欢绉诲姩

    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		context = this;
		initUI();

		if (Environment.getExternalStorageState().equals(
			Environment.MEDIA_MOUNTED)) {
			File file = Environment.getExternalStorageDirectory();
			listFile_1 = new ListFile(file);
			listFile = listFile_1;
			listFile_2 = new ListFile(file);
			listFile_1.parcelable = listView.onSaveInstanceState();
			listFile_2.parcelable = listView.onSaveInstanceState();
			adapter = new FileListAdapter(context, FileUtil.sort(listFile_1
																 .readListFile()), listView);
			listView.setAdapter(adapter);
			pathInfo.setText(file.getAbsolutePath());
		} else {
			Toast.makeText(context, getString(R.string.sdcard_error),
						   Toast.LENGTH_LONG).show();
		}

		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
    }

    private void initUI() {
		listView = (ListView) findViewById(R.id.listview);
		pathInfo = (TextView) findViewById(R.id.btn0);
		btnMenu = (Button) findViewById(R.id.butMenu);
		btnList1 = (Button) findViewById(R.id.list1);
		btnSort = (Button) findViewById(R.id.idbtnsort);
		btnNew = (Button) findViewById(R.id.idbtnnew);
		btnlist = btnList1;
		btnList2 = (Button) findViewById(R.id.list2);

		btnMenu.setOnClickListener(this);
		btnNew.setOnClickListener(this);
		btnList1.setOnClickListener(this);
		btnList2.setOnClickListener(this);

    }

    // item鐐瑰嚮浜嬩欢澶勭悊

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		if (isMultySelect) {
			adapter.clickItemSelect(position);
		} else {
			if (listFile.fileLists[position].isDirectory()) {
				btnlist.setText(listFile.fileLists[position].getName());
				if (listFile.fileLists[position].listFiles() == null) {
					Toast.makeText(context, "鏂囦欢I/O璇诲彇澶辫触", Toast.LENGTH_SHORT)
						.show();
				} else {
					pathInfo.append("/"
									+ listFile.fileLists[position].getName());
					if (!listFile.updata(listFile.fileLists[position])) {
						onDestroy();
					}
					adapter.update(listFile.fileLists);
					if (FileListAdapter.state != null) {
						listView.setAdapter(adapter);
						listView.setSelection(location);
					}
				}
			} else {
				OpenFiles.open(this, listFile.fileLists[position]);
			}
		}
    }

    // 闀挎寜item寮瑰嚭鎿嶄綔
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
								   int position, long id) {
		if (listFile.fileLists[position].isDirectory()) {
			fileType = getString(R.string.folder);
		} else {
			fileType = getString(R.string.file);
		}
		listView.setOnCreateContextMenuListener(fileList);
		return false;
    }

    ListView.OnCreateContextMenuListener fileList = new ListView.OnCreateContextMenuListener() {
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
										ContextMenuInfo menuInfo) {
			menu.setHeaderTitle("鎿嶄綔" + fileType);
			int resId;
			if (fileType.equals(getString(R.string.folder))) {
				resId = R.drawable.menu_folder;
			} else {
				resId = R.drawable.menu_file;
			}
			menu.setHeaderIcon(resId);
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.file_context_menu, menu);
		}
    };

    // 鍝嶅簲item鐨勯�夋嫨
    @Override
    public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo menu = (AdapterContextMenuInfo) item
			.getMenuInfo();
		fromFile = listFile.fileLists[menu.position];
		switch (item.getItemId()) {
			case R.id.openbytext:
				this.startTextActivity(fromFile.getPath());
				break;
			case R.id.delete:
				openDeleteDialog();
				listFile.parcelable = listView.onSaveInstanceState();
				refresh();
				break;
			case R.id.rename:
				rename(fromFile);
				break;
			case R.id.copy:
				System.out.println(FileUtil.getFileSize(fromFile));
				open_operat_btn();
				break;
			case R.id.move:
				state = 2;
				open_operat_btn();
				break;
			case R.id.property:
				break;
			default:
				break;
		}
		return super.onContextItemSelected(item);
    }

    // 鐗╃悊鎸夐敭
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (listFile.parentFile != null) {
				btnlist.setText(listFile.parentFile.getName());
				if (!listFile.updata(listFile.parentFile)) {
					onDestroy();
				}
				adapter.update(listFile.fileLists);
				String path = pathInfo.getText().toString();
				int indexOf = path.lastIndexOf("/");
				path = path.substring(0, indexOf);
				pathInfo.setText(path);
				if (FileListAdapter.state != null) {
					listView.setAdapter(adapter);
					listView.onRestoreInstanceState(FileListAdapter.state);
				}
			} else {
				onDestroy();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			initMenu();
			return true;
		}

		return false;
    }

    // 鎸夐挳浜嬩欢澶勭悊
    @Override
    public void onClick(View view) {
		switch (view.getId()) {
			case R.id.butMenu:
				if (popListMenu == null) {
					initMenu();
				}
				popListMenu.show();
				break;
			case R.id.list1:
				if (btnlist == btnList1) {
					return;
				} else {
					listFile_2.parcelable = listView.onSaveInstanceState();
					btnlist.setBackgroundResource(R.drawable.black);
					btnlist = btnList1;
					btnlist.setBackgroundResource(R.drawable.white);
					listFile = listFile_1;
					refresh();
				}
				break;
			case R.id.list2:
				if (btnlist == btnList2) {
					return;
				} else {
					listFile_1.parcelable = listView.onSaveInstanceState();
					btnlist.setBackgroundResource(R.drawable.black);
					btnlist = btnList2;
					btnlist.setBackgroundResource(R.drawable.white);
					listFile = listFile_2;
					refresh();
				}
				break;
			case R.id.idbtncopy:
				isMultySelect = false;
				state = 3;
				open_operat_btn();
				pop_copy_move.dismiss();
				break;
			case R.id.idbtnmove:
				isMultySelect = false;
				state = 4;
				open_operat_btn();
				pop_copy_move.dismiss();
				break;
			case R.id.idbtndelete:
				openDeleteDialog();
				break;

			case R.id.idbtncancel2:
				pop_copy_move.dismiss();
				state = 0;
				isMultySelect = false;
				adapter.cancleSelect();
				adapter.getSelectedListFiles().removeAll(adapter.getSelectedListFiles());

				break;
			case R.id.idbtnhere:
				switch (state) {
					case 1:
						initProgress();
						new Thread(new Runnable() {
								@Override
								public void run() {
									try {

										FileUtil.totalSize = FileUtil.getFileSize(fromFile);
										FileUtil.copy(fromFile, listFile.file);
									}
									catch (IOException e) {
										e.printStackTrace();
									}
								}
							}).start();
						break;
					case 2:
						FileUtil.move(fromFile, listFile.file);
						break;
					case 3:
						initProgress();
						new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
									try {

										FileUtil.totalSize = FileUtil.getFileSize(adapter.getSelectedListFiles());
										FileUtil.copy(adapter.getSelectedListFiles(),
													  listFile.file);
									}
									catch (IOException e) {
										e.printStackTrace();
									}
								}
							}).start();
						break;
					case 4:
						FileUtil.move(adapter.getSelectedListFiles(), listFile.file);
						break;
					default :
						break;
				}
				state = 0;
				pop_open_operat_btn.dismiss();
				refresh();
				break;
			case R.id.idbtnbookmark:
				break;
			case R.id.idbtnnew:
			case R.id.idbtnnew2:
				if (popNewMenu == null) {
					initNewFileMenu();
				}
				popNewMenu.show();
				break;
			case R.id.idbtncancel:
				state = 0;
				pop_open_operat_btn.dismiss();
				fromFile = null;
				adapter.cancleSelect();
				break;
		}
    }

    public void openDeleteDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("纭鍒犻櫎?");

		dialog.setPositiveButton(R.string.confirm,
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					try {
						if (isMultySelect) {
							FileUtil.delete(adapter.getSelectedListFiles());
							pop_copy_move.dismiss();
							isMultySelect = false;
						} else {
							FileUtil.delete(fromFile);
						}
					}
					catch (IOException e) {
						e.printStackTrace();
						Toast.makeText(MainActivity.this, "鍒犻櫎澶辫触", 1000)
							.show();
					}
					Toast.makeText(MainActivity.this, "鍒犻櫎鎴愬姛", 1000).show();
					listFile.parcelable = listView.onSaveInstanceState();
					refresh();
				}
			});

		dialog.setNegativeButton(R.string.cancel, null);
		dialog.show();

    }

    // 鍒濆鍖栬彍鍗�
    private void initMenu() {
		List<String> menuContent = new ArrayList<String>();
		menuContent.add("鍒锋柊");
		menuContent.add("鑿滃崟");
		menuContent.add("鍏充簬");
		menuContent.add("閫�鍑�");
		popListMenu = new PopListMenu(this, menuContent, R.layout.menu_list,
							  R.id.menu_list, btnMenu.getWidth(), btnMenu.getHeight());
		popListMenu.setOnItemClickListener(new MyOnItemClickListener() {

				@Override
				public void onClick(int position) {
					// TODO: Implement this method
					switch (position) {
						case 0:
							listFile.parcelable = listView.onSaveInstanceState();
							refresh();
							break;
						case 1:
							break;
						case 2:
							break;
						case 3:
							onDestroy();
							break;

					}
				}
			});
    }

    public void initNewFileMenu() {
		List<String> menuContent = new ArrayList<String>();
		menuContent.add("鏂板缓鏂囦欢澶�");
		menuContent.add("鏂板缓鏂囦欢");

		popNewMenu = new PopListMenu(this, menuContent, R.layout.menu_list,
								 R.id.menu_list, btnMenu.getWidth(), btnMenu.getHeight());
		popNewMenu.setOnItemClickListener(new MyOnItemClickListener() {

				@Override
				public void onClick(int position) {
					// TODO: Implement this method
					switch (position) {
						case 0:
							newCreate_folder_file(true);
							break;
						case 1:
							newCreate_folder_file(false);
							break;
					}
				}
			});

    }

    private void open_operat_btn() {
		if (pop_open_operat_btn == null) {
			layout_open_operat = getLayoutInflater().inflate(
				R.layout.button_list, null);
			pop_open_operat_btn = new PopupWindow(layout_open_operat,
												  LayoutParams.MATCH_PARENT, btnMenu.getHeight());
			pop_open_operat_btn.update();
			pop_open_operat_btn.showAtLocation(layout_open_operat,
											   Gravity.BOTTOM, 0, 0);

			Button btnHere = (Button) layout_open_operat
				.findViewById(R.id.idbtnhere);
			Button btnCancel = (Button) layout_open_operat
				.findViewById(R.id.idbtncancel);
			Button btnBookMark = (Button) layout_open_operat
				.findViewById(R.id.idbtnbookmark);
			Button btnNew = (Button) layout_open_operat
				.findViewById(R.id.idbtnnew2);

			btnHere.setOnClickListener(this);
			btnBookMark.setOnClickListener(this);
			btnNew.setOnClickListener(this);
			btnCancel.setOnClickListener(this);
		} else {
			pop_open_operat_btn.showAtLocation(layout_open_operat,
											   Gravity.BOTTOM, 0, 0);
		}

    }

    private void open_copy_move() {
		if (pop_copy_move == null) {
			layout_open = getLayoutInflater().inflate(R.layout.button_list2,
													  null);
			pop_copy_move = new PopupWindow(layout_open,
											LayoutParams.MATCH_PARENT, btnMenu.getHeight());
		
			pop_copy_move.update();
			pop_copy_move.showAtLocation(layout_open, Gravity.BOTTOM, 0, 0);
			Button btnCopy = (Button) layout_open.findViewById(R.id.idbtncopy);
			Button btnMove = (Button) layout_open.findViewById(R.id.idbtnmove);
			Button btnDelete = (Button) layout_open
				.findViewById(R.id.idbtndelete);
			Button btnCancel2 = (Button) layout_open
				.findViewById(R.id.idbtncancel2);

			btnCopy.setOnClickListener(this);
			btnMove.setOnClickListener(this);
			btnDelete.setOnClickListener(this);
			btnCancel2.setOnClickListener(this);
		} else {
			pop_copy_move.showAtLocation(layout_open, Gravity.BOTTOM, 0, 0);
		}

    }

    private void initProgress() {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setTitle("姝ｅ湪澶嶅埗");
		dialog.setMessage("璇风◢鍚�.......");
		dialog.setIcon(android.R.drawable.btn_star);
		dialog.setMax(100);
		dialog.setButton("纭畾", new ProgressDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		dialog.setCancelable(true);
		dialog.show();
		FileUtil.setProgressListener(new Handler(){
				@Override
				public void handleMessage(Message msg) {
					dialog.setProgress(msg.arg1);
				}

			});

    }

    private void newCreate_folder_file(final boolean isDir) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("鏂板缓");
		dialog.setMessage("璇疯緭鍏ュ悕瀛�");
		final EditText edit = new EditText(context);

		dialog.setView(edit);
		dialog.setPositiveButton(R.string.confirm,
			new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					File file = new File(listFile.file.toString() + "/"
										 + edit.getText().toString());
					if (!file.exists()) {
						listFile.parcelable = listView
							.onSaveInstanceState();
						if (isDir) {

							boolean b = file.mkdir();
							if (b) {
								Toast.makeText(context,
											   R.string.create_folder_success,
											   Toast.LENGTH_LONG).show();
								refresh();
							} else {
								Toast.makeText(context,
											   R.string.create_folder_fail,
											   Toast.LENGTH_LONG).show();
							}
						} else {
							try {
								file.createNewFile();
								Toast.makeText(MainActivity.this, "鍒涘缓鎴愬姛",
											   1000).show();
								refresh();
							}
							catch (IOException e) {
								Toast.makeText(MainActivity.this, "鍒涘缓澶辫触",
											   1000).show();
							}
						}
					} else {
						Toast.makeText(MainActivity.this, "鏂囦欢宸插瓨鍦�", 1000)
							.show();
					}

				}
			});
		dialog.setNegativeButton(R.string.cancel, null);
		dialog.show();

    }

    private void rename(final File file) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(R.string.rename);

		final EditText edit = new EditText(context);
		edit.setHint(R.string.plz_input_new_name);
		edit.setText(file.getName());
		edit.selectAll();

		dialog.setView(edit);
		dialog.setPositiveButton(R.string.confirm,
			new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String newName = edit.getText().toString();
					if (newName != null && !"".equals(newName)) {

						boolean b = file.renameTo(new File(file.getParent()
														   + "/" + newName));
						if (b) {
							Toast.makeText(context,
										   R.string.rename_success, 1000).show();
							listFile.parcelable = listView
								.onSaveInstanceState();
							refresh();

						} else {
							Toast.makeText(context, R.string.rename_fail,
										   1000).show();
						}
					} else {
						Toast.makeText(context, R.string.name_is_not_null,
									   2000).show();
					}
				}
			});
		dialog.setNegativeButton(R.string.cancel, null);
		dialog.show();
    }

    @Override
    protected void onDestroy() {
		// TODO: Implement this method
		int pid = android.os.Process.myPid();
		android.os.Process.killProcess(pid);
		super.onDestroy();
    }

    public void startTextActivity(String fileName) {
		Intent intent = new Intent();
		intent.putExtra("textpath", fileName);
		intent.setClass(context, TextActivity.class);
		startActivity(intent);
    }

    private void refresh() {
		adapter.update(listFile.readListFile());
		listView.setAdapter(adapter);
		listView.onRestoreInstanceState(listFile.parcelable);
    }

    public class MyOnCheekChangeListener implements
	CompoundButton.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
									 boolean isChecked) {
			if (isChecked) {
				isMultySelect = true;
				open_copy_move();
			}
		}

    }
}

class ListFile {
    public Parcelable parcelable;
    public File file;
    public File parentFile;
    public File[] fileLists;
    public boolean isShowHide = true;

    public ListFile(File file) {
		this.file = file;
		this.parentFile = file.getParentFile();
		fileLists = readListFile();
    }

    public boolean updata(File file) {
		if (!file.canRead()) {
			return false;
		}
		this.file = file;
		this.parentFile = file.getParentFile();
		fileLists = readListFile();
		return true;
    }

    public File[] readListFile() {
		if (isShowHide) {
			fileLists = file.listFiles(new MyFileFilter());
			fileLists = FileUtil.sort(fileLists);

		} else {
			fileLists = file.listFiles();
			fileLists = FileUtil.sort(fileLists);
		}
		return fileLists;
    }
}
