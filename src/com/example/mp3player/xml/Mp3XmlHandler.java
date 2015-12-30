package com.example.mp3player.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.example.mp3player.Mp3;

/**
 * 创建mp3列表xml解析的处理器
 * @author Administrator
 *
 */
public class Mp3XmlHandler extends DefaultHandler{
	


	//全局变量
	Mp3 mp3;
	String tagName;
	public static List<Mp3> listMp3 = new ArrayList<Mp3>();

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
//		listMp3 = new ArrayList<Mp3>();
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
		if(tagName.equals("song")){
			mp3 = new Mp3();//为每一首歌实例化对象
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		if(localName.equals("song")){
			listMp3.add(mp3);
		}
		tagName = "";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		//将读到的ch数组装换成字符串
		String temp = new String(ch, start, length);
		
		switch (tagName) {
		case "id":mp3.setId(temp); break;
		case "mp3.name":mp3.setMp3Name(temp); break;
		case "mp3.size":mp3.setMp3Size(temp); break;
		case "lrc.name":mp3.setLrcNmae(temp);break;
		case "lrc.size":mp3.setLrcSize(temp);break;
		case "songer":mp3.setSonger(temp);
		default: break;
		}
	}
}

