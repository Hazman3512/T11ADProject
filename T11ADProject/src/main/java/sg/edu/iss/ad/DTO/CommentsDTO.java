package sg.edu.iss.ad.DTO;

public class CommentsDTO {
	private String Comment;
	private String username;
	private String commentDateTime;
	private String stockticker;
	public String getComment() {
		return Comment;
	}
	public void setComment(String comment) {
		Comment = comment;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getCommentDateTime() {
		return commentDateTime;
	}
	public void setCommentDateTime(String commentDateTime) {
		this.commentDateTime = commentDateTime;
	}
	public String getStockticker() {
		return stockticker;
	}
	public void setStockticker(String stockticker) {
		this.stockticker = stockticker;
	}
	
	
	
}