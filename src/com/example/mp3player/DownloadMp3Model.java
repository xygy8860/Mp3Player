package com.example.mp3player;

public class DownloadMp3Model {
	String id ;
	String type;
	String title;
	String artist_id;
	String pic_small;
	String lrclink;
	String song_id;
	String author;
	String album_title;
	String download_show_link;
	String download_file_link;
	String file_extension;
	String paly_show_link;
	String play_file_link;
	String file_size;
	
	public String getFile_size() {
		return file_size;
	}
	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArtist_id() {
		return artist_id;
	}
	public void setArtist_id(String artist_id) {
		this.artist_id = artist_id;
	}
	public String getPic_small() {
		return pic_small;
	}
	public void setPic_small(String pic_small) {
		this.pic_small = pic_small;
	}
	public String getLrclink() {
		return lrclink;
	}
	public void setLrclink(String lrclink) {
		this.lrclink = lrclink;
	}
	public String getSong_id() {
		return song_id;
	}
	public void setSong_id(String song_id) {
		this.song_id = song_id;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAlbum_title() {
		return album_title;
	}
	public void setAlbum_title(String album_title) {
		this.album_title = album_title;
	}
	public String getDownload_show_link() {
		return download_show_link;
	}
	public void setDownload_show_link(String download_show_link) {
		this.download_show_link = download_show_link;
	}
	public String getDownload_file_link() {
		return download_file_link;
	}
	public void setDownload_file_link(String download_file_link) {
		this.download_file_link = download_file_link;
	}
	public String getFile_extension() {
		return file_extension;
	}
	public void setFile_extension(String file_extension) {
		this.file_extension = file_extension;
	}
	public String getPaly_show_link() {
		return paly_show_link;
	}
	public void setPaly_show_link(String paly_show_link) {
		this.paly_show_link = paly_show_link;
	}
	public String getPlay_file_link() {
		return play_file_link;
	}
	public void setPlay_file_link(String play_file_link) {
		this.play_file_link = play_file_link;
	}
	public DownloadMp3Model(String id, String type, String title,
			String artist_id, String pic_small, String lrclink, String song_id,
			String author, String album_title, String download_show_link,
			String download_file_link, String file_extension,
			String paly_show_link, String play_file_link) {
		super();
		this.id = id;
		this.type = type;
		this.title = title;
		this.artist_id = artist_id;
		this.pic_small = pic_small;
		this.lrclink = lrclink;
		this.song_id = song_id;
		this.author = author;
		this.album_title = album_title;
		this.download_show_link = download_show_link;
		this.download_file_link = download_file_link;
		this.file_extension = file_extension;
		this.paly_show_link = paly_show_link;
		this.play_file_link = play_file_link;
	}
	
	
	
	public DownloadMp3Model(String id, String type, String title,
			String artist_id, String pic_small, String lrclink, String song_id,
			String author, String album_title, String download_show_link,
			String download_file_link, String file_extension,
			String paly_show_link, String play_file_link, String file_size) {
		super();
		this.id = id;
		this.type = type;
		this.title = title;
		this.artist_id = artist_id;
		this.pic_small = pic_small;
		this.lrclink = lrclink;
		this.song_id = song_id;
		this.author = author;
		this.album_title = album_title;
		this.download_show_link = download_show_link;
		this.download_file_link = download_file_link;
		this.file_extension = file_extension;
		this.paly_show_link = paly_show_link;
		this.play_file_link = play_file_link;
		this.file_size = file_size;
	}
	public DownloadMp3Model() {
		super();
	}
	public DownloadMp3Model(String type, String title, String song_id,
			String author) {
		super();
		this.type = type;
		this.title = title;
		this.song_id = song_id;
		this.author = author;
	}
	
	
	

}
