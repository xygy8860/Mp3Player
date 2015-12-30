package com.example.mp3player.xml;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.example.mp3player.DownloadMp3Model;
import com.example.mp3player.PublicVariable;

public class SelectSongsXmlHandler extends DefaultHandler{

	//全局变量
	public static DownloadMp3Model mp3 = null;
	String tagName;
	int i;
	
	
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		if(PublicVariable.networkMp3ModelList != null){
			PublicVariable.networkMp3ModelList.removeAll(PublicVariable.networkMp3ModelList);
		}
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		tagName = localName;
		
		if(tagName.equals("song_elt")){
			mp3 = new DownloadMp3Model();//为每一首歌实例化对象
			i = 1;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		if(localName.equals("song_elt")){
			mp3.setType("");
			mp3.setArtist_id("");
			mp3.setPic_small("");
			mp3.setLrclink("");
			mp3.setAlbum_title("");
			PublicVariable.networkMp3ModelList.add(mp3);
		}
		tagName = "";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		if(tagName.equals("song_elt_elt")){
			
			//将读到的ch数组装换成字符串
			String temp = new String(ch, start, length);
			switch (i) {
			case 1: mp3.setSong_id(temp);break;
			case 2: mp3.setTitle(temp);break;
			case 3:	setAuther(temp);break;
			case 4:	setAuther(temp);break;
			case 5:	setAuther(temp);break;
			case 6: setAuther(temp);break;
			default: break;
			}
			i++;
		}
	}
	/**
	 * 首先判断字符串长度大于1，不大于1肯定不是作者，直接返回
	 * 大于1之后再判断字符串是否还有数字，含有数字的也不是作者
	 * @param temp 歌手名字符串
	 */
	private void setAuther(String temp){
		if(temp.length() > 1){
			boolean isNumeric = false;
			int j = 0;
			for(int i = 0; i < temp.length(); i++){
				String s = temp.charAt(i) + "";
				Pattern pattern = Pattern.compile("[0-9]*");
	            Matcher isNum = pattern.matcher(s);
	            if(isNum.matches()){
	            	j++;//含有多少数字，2个以上的视为非歌手名
	            	if(j == 2){
	            		isNumeric = true;
	            	}
	            }
			}
			if(!isNumeric){
				mp3.setAuthor(temp);
			}
		}
	}

}
