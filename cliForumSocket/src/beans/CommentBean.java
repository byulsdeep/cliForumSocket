package beans;

public class CommentBean {
	private int postIdx;
	private int commentIdx;
	private String user;
	private String content;
	private String date;
	private String time;
	public int getPostIdx() {
		return postIdx;
	}
	public void setPostIdx(int postIdx) {
		this.postIdx = postIdx;
	}
	public int getCommentIdx() {
		return commentIdx;
	}
	public void setCommentIdx(int commentIdx) {
		this.commentIdx = commentIdx;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
