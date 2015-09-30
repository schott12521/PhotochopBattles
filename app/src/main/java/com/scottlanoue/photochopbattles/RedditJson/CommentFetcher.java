package com.scottlanoue.photochopbattles.RedditJson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.stream.JsonReader;
import com.scottlanoue.photochopbattles.RedditJson.Comment;

public class CommentFetcher {
	
	private ArrayList<Comment> commentsList = new ArrayList<Comment>();
	
	public List<Comment> readJsonStream(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			readForKindAndData(reader);
		} finally {
			reader.close();
		}
		return returnCommentsList();
	}
	
	private List<Comment> readForKindAndData(JsonReader reader) throws IOException {
		List<Comment> kindOrData = new ArrayList<Comment>();
				
		reader.beginArray();
		while (reader.hasNext()) {
			/**
			 * We skip the first value because that object is the title/thread information
			 */
			reader.skipValue();
			kindOrData = addChildrenComments(reader);
		}
		reader.endArray();
		return kindOrData;
	}
	
	private List<Comment> addChildrenComments(JsonReader reader) throws IOException {
		List<Comment> children = new ArrayList<Comment>();
		
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("data")) {
				children.add(addChildrenFilter(reader));
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return children;
	}
	
	private Comment addChildrenFilter(JsonReader reader) throws IOException {
		Comment commentToReturn = null;
		
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("children")) {
				commentToReturn = addChildrenArray(reader);
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return commentToReturn;
	}
	
	private Comment addChildrenArray(JsonReader reader) throws IOException {
		Comment commentToReturn = null;
				
		reader.beginArray();
		while (reader.hasNext()) {
			commentToReturn = addChildrenData(reader);
		}
		reader.endArray();
		return commentToReturn;
	}
	
	private Comment addChildrenData(JsonReader reader) throws IOException {
		Comment commentToReturn = null;
		
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("data")) {
				commentToReturn = addCommentData(reader);
			}
			else {
				reader.skipValue();
			}
		}
		reader.endObject();
		
		if (commentToReturn != null) {
			commentsList.add(commentToReturn);
		}
		
		return commentToReturn;
	}
	
	private Comment addCommentData(JsonReader reader) throws IOException {
		String body = null;
		String author = null;
		String bodyHtml = null;
		int score = 0;
		String url = null;
		
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("body")) {
				body = reader.nextString();
			} else if (name.equals("author")) {
				author = reader.nextString();
			} else if (name.equals("score")) {
				score = reader.nextInt();
			} else if (name.equals("body_html")) {
				bodyHtml = reader.nextString();
			} else if (name.equals("permalink")) {
				url = "http://reddit.com/" + reader.nextString();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return new Comment(author, body, bodyHtml, score, url);
	}
	
	private ArrayList<Comment> returnCommentsList() {
		return commentsList;
	}

}
