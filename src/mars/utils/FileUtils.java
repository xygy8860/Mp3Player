package mars.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.example.mp3player.MainActivity;
import com.example.mp3player.PublicVariable;

//import mars.model.Mp3Info;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

public class FileUtils {
	private String SDCardRoot;

	public FileUtils() {
		// 得到当前外部存储设备的目录
		SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator;
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public File createFileInSDCard(String fileName, String dir)
			throws IOException {
		File file = new File(SDCardRoot + dir + File.separator + fileName);
		System.out.println("file---->" + file);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dir) {
		File dirFile = new File(SDCardRoot + dir + File.separator);
		System.out.println(dirFile.mkdirs());
		return dirFile;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 * @param urlStr 
	 * @param inputStream 
	 */
	@SuppressWarnings("resource")
	public boolean isFileExist(String fileName, String path, String urlStr) {
		boolean flag = false;
		File file = new File(SDCardRoot + path + File.separator + fileName);
		if(file.exists()){
			 //如果文件存在则判断文件大小
			 try {
				 FileInputStream fis = null;
				 fis = new FileInputStream(file);
				 int size = fis.available();
				 fis.close();//必须先关闭才能删除
				 Log.v("123", "size-->" + size);
				 //如果文件大小为0，重新下载
				 if(size == 0){
					 
					 file.delete();//删除文件重新下载
					 flag = false;
				 }
				 //如果不为0，则比较已存在文件大小与下载文件大小，若不一致，也要重新下载
				 else{
					 URLConnection connection = null;
					 URL url = new URL(urlStr);
					 connection = url.openConnection();
					 
					 int fileLength = connection.getContentLength();
					 
					 Log.v("123", "fileLength-->" + fileLength);
					 if(size != fileLength){
						 file.delete();
						 flag = false;
					 }else{
						 flag = true;
					 }
				 }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return flag;
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * @param strFile_size 
	 * @throws IOException 
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input, String urlStr,String strSize)  {
		
		//如果下载文件为lrc，着不执行通知更新，故flag设置为true；否则执行更新，设置为false
		boolean flag = (strSize == "lrc" || strSize.equals("lrc"))?false:true;
		//如果是歌词，去掉空格
		if(!flag){
			fileName = fileName.replace(" ", "");
		}

		File file = null;
		OutputStream output = null;
		URLConnection connection = null;
		
		//解决This message is already in use.报错问题
//		Message message = MainActivity.instance.handler.obtainMessage();

		URL url;
		try {
			url = new URL(urlStr);
			connection = url.openConnection();
			if (connection.getReadTimeout()==5) {
                Log.v("123", "当前网络有问题");
                // return;
               }
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			creatSDDir(path);
			file = createFileInSDCard(fileName, path);
			output = new FileOutputStream(file);
			/*
			经过debug，发现是由于，conn.getContentLength() 时获取到的值为 -1，导致计算进度时，结果有误，永远为负数。
			在网上查资料都说是服务端没有设content length，跟服务端协商，加上这个就行了。但是为毛2.2，
			的时候就可以服务端也没设啊，查API ：Returns the content length in bytes specified by the response 
			header field content-length or -1 if this field is not set.看API也似乎是这个意思。
			本来打算投降了，跟服务端商量下，看能不能主动加上。突然手贱多点了下查找，发现这么一段话：

		      By default this implementation of HttpURLConnection requests that servers use gzip compression. 
		      Since getContentLength() returns the number of bytes transmitted, you cannot use that method 
		      to predict how many bytes can be read from getInputStream(). Instead, read that stream until 
		      it is exhausted: when read() returns -1. Gzip compression can be disabled by setting the 
		      acceptable encodings in the request header。

			似乎是说，在默认情况下，HttpURLConnection 使用 gzip方式获取，文件 getContentLength() 这个方法，
			每次read完成后可以获得，当前已经传送了多少数据，而不能用这个方法获取 需要传送多少字节的内容，当read() 
			返回 -1时，读取完成，由于这个压缩后的总长度我无法获取，那么进度条就没法计算值了。
			添加如下语句：
			//添加之后问题没有解决~~~
			*/
			connection.setRequestProperty("Accept-Encoding", "identity"); 
			connection.connect();

			//connection.getContentLength()有时返回-1
			int FileLength = connection.getContentLength();
			int DownloadFileLength = 0;
			byte buffer[] = new byte[4 * 1024];
			//通知栏进度条id
			int id = PublicVariable.notiId ;
			if(flag && FileLength != -1){
				PublicVariable.notiId ++;
				//解决This message is already in use.报错问题
				Message message1 = new Message();
				message1.what = PublicVariable.WHAT_START_NOTIFICATION_PROGRESSBAR;
				message1.arg2 = id;
				MainActivity.instance.handlerUpdateProgressBAR.sendMessage(message1);
			}

			Log.v("126","FileLength-->" + fileName + ":" +FileLength);

				int temp = 0;
				int n = 0;
				while (DownloadFileLength < FileLength || temp != -1) {
					temp = 0;
					n++;
					synchronized (this) {
						temp = input.read(buffer);
						Log.v("126", "int  temp --> " + temp);
						output.write(buffer, 0, temp);//避免写入乱字符
						
						if(flag && FileLength != -1){
							DownloadFileLength += temp;
							int x = DownloadFileLength*100/FileLength;
//							Log.v("123", "DownloadFileLength-->" + DownloadFileLength);
//							Log.v("123","FileLength-->" + FileLength);
							//控制更新速度，5%更新一次，不然会频繁更新
							if(n % 30 == 0 || x == 100){
								Message message = new Message();
								message.what = PublicVariable.WHAT_UPDATE_NOTIFICATION_PROGRESSBAR;
								message.arg1 = x;
								message.arg2 = id;
								message.obj = fileName;
								MainActivity.instance.handlerUpdateProgressBAR.sendMessage(message);
							}
						}
					}
						
				}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/*
	
//	/**
//	 * 读取目录中的Mp3文件的名字和大小
//	 
	public List<Mp3Info> getMp3Files(String path) {
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		File file = new File(SDCardRoot + File.separator + path);
		File[] files = file.listFiles();
		FileUtils fileUtils = new FileUtils();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith("mp3")) {
				Mp3Info mp3Info = new Mp3Info();
				mp3Info.setMp3Name(files[i].getName());
				mp3Info.setMp3Size(files[i].length() + "");
				String temp [] = mp3Info.getMp3Name().split("\\.");
				String eLrcName = temp[0] + ".lrc";
				if(fileUtils.isFileExist(eLrcName, "/mp3")){
					mp3Info.setLrcName(eLrcName);
				}
				mp3Infos.add(mp3Info);
			}
		}
		return mp3Infos;
	}
*/
}