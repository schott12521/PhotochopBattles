package com.scottlanoue.photochopbattles.RedditJson;

public class Comment {
	
	private String author;
	private String body;
	private String bodyHtml;
	private String url;
	private int score;
	
	public Comment(String authorIn, String bodyIn, String body_htmlIn, int scoreIn, String urlIn) {
		author = authorIn;
		body = bodyIn;
		bodyHtml = body_htmlIn;
		score = scoreIn;
		url = urlIn;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getBody() {
		return body;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getImageLink() {
		if (!bodyHtml.contains("a href=")) {
			/**
			 * If the program could not find a link in the comment HTML,
			 * return an error for handling later.
			 */
			return "Error: Picture not found (Error 001)";
		}
		String returnString = bodyHtml.substring(bodyHtml.indexOf("&lt;a href=\"") + 12);
		returnString = returnString.substring(0, returnString.indexOf("\"&gt"));
		return returnString;
	}

	public String getUrl() {
		return url;
	}
	
	public String toString() {
		return author + " " + body + " " + score;
	}

}
