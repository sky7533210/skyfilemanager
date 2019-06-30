package com.sky.filemanger.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.sky.filemanger.domain.MyProgressListener;
import com.sky.filemanger.activity.*;
import android.os.*;

public class FileUtil {

    private static final String line="/";
    public static long count;
    public static long totalSize;
    public static Handler handler;
    public static File[] sort(File[] listFiles) {

	List<File> list = Arrays.asList(listFiles);

	Collections.sort(list, new FileComparator());

	File[] array = list.toArray(new File[list.size()]);

	return array;
    }

    public static void copy(List<File> fromListFiles, File toFile)
	    throws IOException {
	Iterator<File> iterator = fromListFiles.iterator();
	while (iterator.hasNext()) {
	    copy(iterator.next(), toFile);
	}
    }

    public static void copy(File fromFile, File toFile) throws IOException {
	if (fromFile.isDirectory()) {
	    toFile = new File(toFile.toString() + line + fromFile.getName());
	    if (!toFile.exists()) {
		toFile.mkdir();
		File[] files = fromFile.listFiles();
		for (File _fromFile : files) {
		    copy(_fromFile, toFile);
		}
	    } else {

	    }
	} else {
	    copyFile(fromFile,
		    new File(toFile.toString() + line + fromFile.getName()));
	}
    }

    public static void copyFile(File fromFile, File toFile) throws IOException {
	BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
		fromFile));
	BufferedOutputStream bos = new BufferedOutputStream(
		new FileOutputStream(toFile));
	byte[] buffer = new byte[1024];
	int len = 0;
	while ((len = bis.read(buffer, 0, 1024)) != -1) {
	    bos.write(buffer, 0, len);
	    count+=len;
		if(count%(1024*1024)==0){
			Message msg=new Message();
			msg.arg1=(int)(count*1.0f/totalSize*100);
		    handler.sendMessage(msg);
		}
	}
		Message msg=new Message();
		msg.arg1=100;
		handler.sendMessage(msg);
	bis.close();
	bos.close();
    }

    public static void move(List<File> listFromFile, File toFile) {
	Iterator<File> itertor = listFromFile.iterator();
	while (itertor.hasNext()) {
	    move(itertor.next(), toFile);
	}
    }

    public static boolean move(File fromFile, File toFile) {
	
		return fromFile.renameTo(new File(toFile.getPath()+line+fromFile.getName()));
    }

    public static void delete(List<File> listFromFile) throws IOException {
	Iterator<File> iterator = listFromFile.iterator();
	while (iterator.hasNext()) {
	    delete(iterator.next());
	}
    }

    public static void delete(File file) throws IOException {
	if (file.isDirectory()) {
	    FileUtils.deleteDirectory(file);
	} else {
	    file.delete();
	}
    }
    public static long getFileSize(List<File> files){
	long size=0;
	Iterator<File> iterator=files.iterator();
	while(iterator.hasNext()){
	    size+=getFileSize(iterator.next());
	}
	return size;
    }
    public static long getFileSize(File file){
	long size=0;
	if(file.isFile()){
	    size=file.length();
	}else{
	    File[] files=file.listFiles();
	    for(File _file:files){
		size+=getFileSize(_file);
	    }
	}
	  return size;
    }
    public static void setProgressListener(Handler handler){
	FileUtil.handler=handler;
    }
}
