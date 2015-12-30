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
		
		//������ļ���ʱ�����
		if(PublicVariable.lrcStrList.size() > 0){
			PublicVariable.lrcStrList.removeAll(PublicVariable.lrcStrList);
		}
		if(PublicVariable.lrcStrTime.size() > 0){
			PublicVariable.lrcStrTime.removeAll(PublicVariable.lrcStrTime);
		}
		
		//���ʱ�������
		Queue<Long> timeMills = new LinkedList<Long>();
		//���ʱ�������Ӧ�ĸ��
		Queue<String> messages = new LinkedList<String>();
		ArrayList<Queue> queues = new ArrayList<Queue>();
		InputStreamReader inputReader = null;
		BufferedReader bufferedReader = null;
		try {
			//����BufferedReader����
			inputReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputReader);
			String temp = null;
			int i = 0;
			//����һ��������ʽ����
			Pattern p = Pattern.compile("\\[\\d{2}:\\d{2}.\\d{2}\\]");//���ʱ��������ʽ[00:00.00]
			String result = "";
			boolean b = true;
			while ((temp = bufferedReader.readLine()) != null) {
				i++;
				Matcher m = p.matcher(temp);
				if (m.find()) {
					if (result != null && result != "") {
						messages.add(result);//����ʼ������
						PublicVariable.lrcStrList.add(result);//�����ͬ������list
					}
					String timeStr = m.group();
					Long timeMill = time2Long(timeStr.substring(1, timeStr
							.length() - 1));
					if (b) {
						timeMills.offer(timeMill);//��ʱ��������
						PublicVariable.lrcStrTime.add(timeMill + "");//�����ʱ�����list
					}
					String msg = temp.substring(10);
					result = "" + msg + "\n";
				} else {
					result = result + temp + "\n";
				}
			}
			messages.add(result);
			//����ʶ��к�ʱ����м��뵽list,���ز���
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
	 * �����ӣ���ȫ��ת���ɺ���
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
