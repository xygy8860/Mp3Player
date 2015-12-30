package com.example.mp3player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcProcessor {

	public ArrayList<Queue> process(InputStream inputStream) {
		
		//将歌词文件和时间清空
		if(PublicVariable.lrcStrList.size() > 0){
			PublicVariable.lrcStrList.removeAll(PublicVariable.lrcStrList);
		}
		if(PublicVariable.lrcStrTime.size() > 0){
			PublicVariable.lrcStrTime.removeAll(PublicVariable.lrcStrTime);
		}
		
		//存放时间点数据
		Queue<Long> timeMills = new LinkedList<Long>();
		//存放时间点所对应的歌词
		Queue<String> messages = new LinkedList<String>();
		ArrayList<Queue> queues = new ArrayList<Queue>();
		InputStreamReader inputReader = null;
		BufferedReader bufferedReader = null;
		try {
			//创建BufferedReader对象
			inputReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputReader);
			String temp = null;
			int i = 0;
			//创建一个正则表达式对象
			Pattern p = Pattern.compile("\\[\\d{2}:\\d{2}.\\d{2}\\]");//歌词时间正则表达式[00:00.00]
			String result = "";
			boolean b = true;
			while ((temp = bufferedReader.readLine()) != null) {
				i++;
				Matcher m = p.matcher(temp);
				if (m.find()) {
					if (result != null && result != "") {
						messages.add(result);//将歌词加入队列
						PublicVariable.lrcStrList.add(result);//将歌词同步加入list
					}
					String timeStr = m.group();
					Long timeMill = time2Long(timeStr.substring(1, timeStr
							.length() - 1));
					if (b) {
						timeMills.offer(timeMill);//将时间加入队列
						PublicVariable.lrcStrTime.add(timeMill + "");//将歌词时间加入list
					}
					String msg = temp.substring(10);
					result = "" + msg + "\n";
				} else {
					result = result + temp + "\n";
				}
			}
			messages.add(result);
			//将歌词队列和时间队列加入到list,返回参数
			queues.add(timeMills);
			queues.add(messages);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(inputReader != null){
				try {
					inputReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(bufferedReader != null){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return queues;
	}
	/**
	 * 将分钟，秒全部转换成毫秒
	 * @param timeStr
	 * @return
	 */
	public Long time2Long(String timeStr) {
		String s[] = timeStr.split(":");
		int min = Integer.parseInt(s[0]);
		String ss[] = s[1].split("\\.");
		int sec = Integer.parseInt(ss[0]);
		int mill = Integer.parseInt(ss[1]);
		return min * 60 * 1000 + sec * 1000 + mill * 10L;
	}

}
