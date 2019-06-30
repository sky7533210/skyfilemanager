package com.sky.filemanger.utils;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.sky.filemanger.R;
import com.sky.filemanger.activity.MainActivity;

public class OpenFiles {
	public static void open(MainActivity context, File file) {
		if (file != null && file.isFile()) {
			String fileName = file.toString();
			Intent intent;
			if (checkEndsWithInStringArray(fileName, context.getResources()
					.getStringArray(R.array.fileEndingImage))) {
				intent = OpenFiles.getImageFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingWebText))) {
				intent = OpenFiles.getHtmlFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingPackage))) {
				intent = OpenFiles.getApkFileIntent(file);
				context.startActivity(intent);

			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingAudio))) {
				intent = OpenFiles.getAudioFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingVideo))) {
				intent = OpenFiles.getVideoFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingText))) {    
				context.startTextActivity(fileName);
			/*    intent = OpenFiles.getTextFileIntent(file);
				context.startActivity(intent);*/
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingPdf))) {
				intent = OpenFiles.getPdfFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingWord))) {
				intent = OpenFiles.getWordFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingExcel))) {
				intent = OpenFiles.getExcelFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingPPT))) {
				intent = OpenFiles.getPPTFileIntent(file);
				context.startActivity(intent);
			} else {
				Toast.makeText(context, context.getString(R.string.can_not_open_file), 1).show();
			}
		} else {
			Toast.makeText(context, context.getString(R.string.not_file), 1000).show();
		}

		if (file != null && file.isFile()) {
		    Intent intent = getFileOpenIntent(context, file);
		    if (intent != null) {
		       // context.startActivity(intent);
		    } else {
		        Toast.makeText(context, context.getString(R.string.can_not_open_file), 1).show();
		    }
		} else {
		    Toast.makeText(context, context.getString(R.string.not_file), 1).show();
		}
	}

    public static Intent getFileOpenIntent(Context context, File file) {
        Intent intent = null;
        String fileName = file.toString();
        if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingImage))) {
            intent = OpenFiles.getImageFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingWebText))) {
            intent = OpenFiles.getHtmlFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingPackage))) {
            intent = OpenFiles.getApkFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingAudio))) {
            intent = OpenFiles.getAudioFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingVideo))) {
            intent = OpenFiles.getVideoFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingText))) {
            intent = OpenFiles.getTextFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingPdf))) {
            intent = OpenFiles.getPdfFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingWord))) {
            intent = OpenFiles.getWordFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingExcel))) {
            intent = OpenFiles.getExcelFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingPPT))) {
            intent = OpenFiles.getPPTFileIntent(file);
        } else {
            // do nothing
        	Toast.makeText(context, context.getString(R.string.can_not_open_file), 1).show();
        }

        return intent;
    }

	private static boolean checkEndsWithInStringArray(String checkItsEnd,String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	public static Intent getHtmlFileIntent(File file) {
		Uri uri = Uri.parse(file.toString()).buildUpon()
				.encodedAuthority("com.android.htmlfileprovider")
				.scheme("content").encodedPath(file.toString()).build();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	public static Intent getImageFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	public static Intent getPdfFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}

	public static Intent getTextFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "text/plain");
		return intent;
	}

	public static Intent getAudioFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	public static Intent getVideoFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	public static Intent getChmFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	public static Intent getWordFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	public static Intent getExcelFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	public static Intent getPPTFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	public static Intent getApkFileIntent(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		return intent;
	}

}
