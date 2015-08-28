package com.scottlanoue.photochopbattles.RedditJson;

import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.stream.JsonReader;

public class LinkFetcher {

    private ArrayList<Link> returnList = new ArrayList<Link>();

    public List<Link> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            readLinksArray(reader);
        } finally {
            reader.close();
        }
        return returnTheList();
    }

    private List<Link> readLinksArray(JsonReader reader) throws IOException {
        List<Link> links = new ArrayList<Link>();

        reader.beginObject();
        while (reader.hasNext()) {
//			links.add(addLink(reader));
            String name = reader.nextName();
            if (name.equals("data")) {
                links.add(addLink(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return links;
    }

    private Link addLink(JsonReader reader) throws IOException {
        Link link = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("children")) {
                link = addLinkData(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
//		System.out.println(link + " addLink");
        return link;
    }

    private Link addLinkData(JsonReader reader) throws IOException {
        Link link = null;

        reader.beginArray();
        while (reader.hasNext()) {
//			// need to go down from data
//			System.out.println(reader.toString());
//			String name = reader.nextName();
//			if (name.equals("data")) {
            link = addActualLink(reader);
//			} else {
//				reader.skipValue();
//			}
        }
        reader.endArray();
        return link;
    }

    private Link addActualLink(JsonReader reader) throws IOException {
        Link link = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("data")) {
                link = actuallyThisDeepIsRidiculuous(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        returnList.add(link);
//		System.out.println(link + " addActual");
        return link;
    }

    private Link actuallyThisDeepIsRidiculuous(JsonReader reader) throws IOException {
        String title = null;
        String url = null;
        String permaLink = "http://www.reddit.com";
        int score = 0;
        String domain = null;
        String id = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("title")) {
                title = reader.nextString();
            } else if (name.equals("url")) {
                url = reader.nextString();
            } else if (name.equals("score")) {
                score = reader.nextInt();
            } else if (name.equals("permalink")) {
                permaLink = permaLink + reader.nextString();
            } else if (name.equals("domain")) {
                domain = reader.nextString();
            } else if (name.equals("id")) {
                id = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Link(title, url, permaLink, score, domain, id);
    }

    private ArrayList<Link> returnTheList() {
        return returnList;
    }
}
