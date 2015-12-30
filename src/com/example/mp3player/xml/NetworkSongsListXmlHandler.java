package com.example.mp3player.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.example.mp3player.DownloadMp3Model;
import com.example.mp3player.LrcActivity;
import com.example.mp3player.PublicVariable;

public class NetworkSongsListXmlHandler extends DefaultHandler{

	//ȫ�ֱ���
	DownloadMp3Model mp3;
	String tagName;

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		//����б�
		if(PublicVariable.networkMp3ModelList != null){
			PublicVariable.networkMp3ModelList.removeAll(PublicVariable.networkMp3ModelList);
		}
		super.startDocument();
	}

	//������ȡ�ĵ�
	//������id�͸���pic_small ��ű���
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

    @Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		tagName = localName;
		if(tagName.equals("song")){
			mp3 = new DownloadMp3Model();//Ϊÿһ�׸�ʵ��������
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		if(localName.equals("song")){
			PublicVariable.networkMp3ModelList.add(mp3);
			
			//�����ֺ͸���uid��Ŷ�Ӧ���룬���ݸ������Ʋ�ѯuid
			LrcActivity.inLrcActivity.editor.putString(mp3.getAuthor(), mp3.getArtist_id());
			LrcActivity.inLrcActivity.editor.commit();//ִ������
			//���ݸ������Ƹ������ƣ��ɲ�ѯ�õ�pic��ַ
			String t1 = mp3.getTitle() + mp3.getAuthor();
			String t2 = mp3.getPic_small();
			//Log.v("126", "mp3.getPic_small()-->" + mp3.getPic_small());
			LrcActivity.inLrcActivity.editor.putString(t1,t2);
			LrcActivity.inLrcActivity.editor.commit();//ִ������
		}
		tagName = "";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		//��������ch����װ�����ַ���
		String temp = new String(ch, start, length);

		switch (tagName) {
		case "type": mp3.setType(temp);break;
		case "title": mp3.setTitle(temp);break;
		case "ting_uid": mp3.setArtist_id(temp);break;
		case "pic_small": mp3.setPic_small(temp);break;
		case "lrclink": mp3.setLrclink(temp);break;
		case "song_id": mp3.setSong_id(temp);break;
		case "author": mp3.setAuthor(temp);break;
		case "album_title": mp3.setAlbum_title(temp);break;
		default: break;
		}
	}

}
