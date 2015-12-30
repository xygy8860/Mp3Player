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
		// �õ���ǰ�ⲿ�洢�豸��Ŀ¼
		SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator;
	}

	/**
	 * ��SD���ϴ����ļ�
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
	 * ��SD���ϴ���Ŀ¼
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dir) {
		File dirFile = new File(SDCardRoot + dir + File.separator);
		System.out.println(dirFile.mkdirs());
		return dirFile;
	}

	/**
	 * �ж�SD���ϵ��ļ����Ƿ����
	 * @param urlStr 
	 * @param inputStream 
	 */
	@SuppressWarnings("resource")
	public boolean isFileExist(String fileName, String path, String urlStr) {
		boolean flag = false;
		File file = new File(SDCardRoot + path + File.separator + fileName);
		if(file.exists()){
			 //����ļ��������ж��ļ���С
			 try {
				 FileInputStream fis = null;
				 fis = new FileInputStream(file);
				 int size = fis.available();
				 fis.close();//�����ȹرղ���ɾ��
				 Log.v("123", "size-->" + size);
				 //����ļ���СΪ0����������
				 if(size == 0){
					 
					 file.delete();//ɾ���ļ���������
					 flag = false;
				 }
				 //�����Ϊ0����Ƚ��Ѵ����ļ���С�������ļ���С������һ�£�ҲҪ��������
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
	 * ��һ��InputStream���������д�뵽SD����
	 * @param strFile_size 
	 * @throws IOException 
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input, String urlStr,String strSize)  {
		
		//��������ļ�Ϊlrc���Ų�ִ��֪ͨ���£���flag����Ϊtrue������ִ�и��£�����Ϊfalse
		boolean flag = (strSize == "lrc" || strSize.equals("lrc"))?false:true;
		//����Ǹ�ʣ�ȥ���ո�
		if(!flag){
			fileName = fileName.replace(" ", "");
		}

		File file = null;
		OutputStream output = null;
		URLConnection connection = null;
		
		//���This message is already in use.��������
//		Message message = MainActivity.instance.handler.obtainMessage();

		URL url;
		try {
			url = new URL(urlStr);
			connection = url.openConnection();
			if (connection.getReadTimeout()==5) {
                Log.v("123", "��ǰ����������");
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
			����debug�����������ڣ�conn.getContentLength() ʱ��ȡ����ֵΪ -1�����¼������ʱ�����������ԶΪ������
			�����ϲ����϶�˵�Ƿ����û����content length���������Э�̣�������������ˡ�����Ϊë2.2��
			��ʱ��Ϳ��Է����Ҳû�谡����API ��Returns the content length in bytes specified by the response 
			header field content-length or -1 if this field is not set.��APIҲ�ƺ��������˼��
			��������Ͷ���ˣ�������������£����ܲ����������ϡ�ͻȻ�ּ�������²��ң�������ôһ�λ���

		      By default this implementation of HttpURLConnection requests that servers use gzip compression. 
		      Since getContentLength() returns the number of bytes transmitted, you cannot use that method 
		      to predict how many bytes can be read from getInputStream(). Instead, read that stream until 
		      it is exhausted: when read() returns -1. Gzip compression can be disabled by setting the 
		      acceptable encodings in the request header��

			�ƺ���˵����Ĭ������£�HttpURLConnection ʹ�� gzip��ʽ��ȡ���ļ� getContentLength() ���������
			ÿ��read��ɺ���Ի�ã���ǰ�Ѿ������˶������ݣ������������������ȡ ��Ҫ���Ͷ����ֽڵ����ݣ���read() 
			���� -1ʱ����ȡ��ɣ��������ѹ������ܳ������޷���ȡ����ô��������û������ֵ�ˡ�
			���������䣺
			//���֮������û�н��~~~
			*/
			connection.setRequestProperty("Accept-Encoding", "identity"); 
			connection.connect();

			//connection.getContentLength()��ʱ����-1
			int FileLength = connection.getContentLength();
			int DownloadFileLength = 0;
			byte buffer[] = new byte[4 * 1024];
			//֪ͨ��������id
			int id = PublicVariable.notiId ;
			if(flag && FileLength != -1){
				PublicVariable.notiId ++;
				//���This message is already in use.��������
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
						output.write(buffer, 0, temp);//����д�����ַ�
						
						if(flag && FileLength != -1){
							DownloadFileLength += temp;
							int x = DownloadFileLength*100/FileLength;
//							Log.v("123", "DownloadFileLength-->" + DownloadFileLength);
//							Log.v("123","FileLength-->" + FileLength);
							//���Ƹ����ٶȣ�5%����һ�Σ���Ȼ��Ƶ������
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
//	 * ��ȡĿ¼�е�Mp3�ļ������ֺʹ�С
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