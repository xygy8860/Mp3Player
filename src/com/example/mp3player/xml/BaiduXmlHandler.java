package com.example.mp3player.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.example.mp3player.DownloadMp3Model;
import com.example.mp3player.Mp3;

public class BaiduXmlHandler extends DefaultHandler{


	//全局变量
	public static DownloadMp3Model mp3 = null;
//	static ArrayList<String> dowanloadListFromXmlHandler = null;
	String tagName;
	boolean isUrlAdd = false;//控制是否存入下载地址
	boolean isFileExtensionAdd = false;
	int num = 0;//判断下载地址是否存满

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
//		dowanloadListFromXmlHandler = new ArrayList<String>();
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
		if(tagName.equals("song_downWeb_response")){
			mp3 = new DownloadMp3Model();//为每一首歌实例化对象
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		if(localName.equals("song_downWeb_response")){
			
		}
		tagName = "";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		//将读到的ch数组装换成字符串
		String temp = new String(ch, start, length);
		
		switch (tagName) {
		case "file_bitrate": if(Integer.parseInt(temp) >= 64){
				isUrlAdd = true;
			}break;
		case "file_link":if(isUrlAdd && (!temp.isEmpty() || temp != "" || temp != null) && num < 3){
				mp3.setDownload_file_link(temp);
				num ++;
			}break;
		case "show_link": if(isUrlAdd && (!temp.isEmpty() || temp != "" || temp != null) && num < 3){
			mp3.setDownload_show_link(temp);
			num ++;
			}
			break;
		case "file_extension": if(isUrlAdd && (!temp.isEmpty() || temp != "" || temp != null) && !isFileExtensionAdd){
			mp3.setFile_extension(temp);
			isFileExtensionAdd = true;
			}
			break;
		case "lrclink": mp3.setLrclink(temp);break;
		case "song_id": mp3.setSong_id(temp);break;
		case "title": mp3.setTitle(temp);break;
		case "pic_small": mp3.setPic_small(temp);break;
		case "ting_uid": mp3.setArtist_id(temp);break;
		case "author": mp3.setAuthor(temp);break;
		case "file_size": if(isUrlAdd && num < 2){
			mp3.setFile_size(temp);
			}break;
		default: break;
		}
	}

}
