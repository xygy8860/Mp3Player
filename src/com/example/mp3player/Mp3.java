package com.example.mp3player;

/**
 * @author Administrator
 *
 */
public class Mp3 {
	private String id = null;
	private String mp3Name = null;
	private String mp3Size = null;
	private String lrcNmae = null;
	private String lrcSize = null;
	private String songer = null;
	private String uri = null;
	
	public Mp3(String id, String mp3Name, String mp3Size, String lrcNmae,
			String lrcSize, String songer, String uri) {
		super();
		this.id = id;
		this.mp3Name = mp3Name;
		this.mp3Size = mp3Size;
		this.lrcNmae = lrcNmae;
		this.lrcSize = lrcSize;
		this.songer = songer;
		this.uri = uri;
	}


	@Override
	public String toString() {
		return "Mp3 [id=" + id + ", mp3Name=" + mp3Name + ", mp3Size="
				+ mp3Size + ", lrcNmae=" + lrcNmae + ", lrcSize=" + lrcSize
				+ ", songer=" + songer + ", uri=" + uri + "]";
	}

	public String getSonger() {
		return songer;
	}


	public void setSonger(String songer) {
		this.songer = songer;
	}

	public String getId() {
		return id;
	}


	public String getUri() {
		return uri;
	}


	public void setUri(String uri) {
		this.uri = uri;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	public Mp3() {
		super();
	}


	public String getMp3Name() {
		return mp3Name;
	}
	public void setMp3Name(String mp3Name) {
		this.mp3Name = mp3Name;
	}
	public String getMp3Size() {
		return mp3Size;
	}
	public void setMp3Size(String mp3Size) {
		this.mp3Size = mp3Size;
	}
	public String getLrcNmae() {
		return lrcNmae;
	}
	public void setLrcNmae(String lrcNmae) {
		this.lrcNmae = lrcNmae;
	}
	public String getLrcSize() {
		return lrcSize;
	}
	public void setLrcSize(String lrcSize) {
		this.lrcSize = lrcSize;
	}
	
}
