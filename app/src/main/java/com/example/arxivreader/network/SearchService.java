package com.example.arxivreader.network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.arxivreader.model.entity.Paper;
import com.example.arxivreader.ui.vm.PaperViewModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchService extends Service {
    private final IBinder binder = new LocalBinder();
    private final static String baseUrl = "https://export.arxiv.org/api/query?start=0&max_results=100&search_query=";

    public class LocalBinder extends Binder {
        public SearchService getService() {
            return SearchService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public SearchService() {
    }

    private static String getUrl(Map<String, String> params, boolean ifAnd) {
        StringBuilder result = new StringBuilder(baseUrl);
        String sep = "+" + (ifAnd ? "AND" : "OR") + "+";
        for (String key : params.keySet()) {
            result.append(key).append(":").append(params.get(key));
            result.append(sep);
        }
        return result.substring(0, result.length() - sep.length());
    }

    public void search(Map<String, String> params, boolean ifAnd, PaperViewModel viewModel) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getUrl(params, ifAnd);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, resp -> {
            List<Paper> papers = parseXML(resp);
            if (papers.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Nothing matches your search.", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.getPapersLiveData().postValue(papers);
        }, error -> {
            Toast.makeText(getApplicationContext(), error.getCause().getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("searchService", error.getMessage());
        });
        queue.add(stringRequest);

    }

    private List<Paper> parseXML(String resp) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(resp));
            parser.nextTag();
            return readFeed(parser);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Paper> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Paper> entries = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private Paper readEntry(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "entry");
        Map<String, String> simpleContent = new HashMap<>();
        String link = null;
        String category = null;
        List<String> authors = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if ("id".equals(name) || "title".equals(name) || "summary".equals(name) || "published".equals(name)) {
                simpleContent.put(name, readSimpleContent(parser, name));
            } else if ("link".equals(name)) {
                String temp = readLink(parser);
                link = temp == null ? link : temp;
            } else if ("category".equals(name)) {
                category = readCategory(parser);
            } else if ("author".equals(name)) {
                authors.add(readAuthor(parser));
            } else {
                skip(parser);
            }
        }
        return new Paper(simpleContent.get("id"),
                simpleContent.get("title"),
                category,
                simpleContent.get("summary"),
                simpleContent.get("published"),
                link,
                authors);
    }

    private String readSimpleContent(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, tag);
        String content = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, tag);
        return content;
    }

    private String readCategory(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "category");
        String category = parser.getAttributeValue(null, "term");
        parser.nextTag();
        return category;
    }

    private String readAuthor(XmlPullParser parser) throws IOException, XmlPullParserException {
        String authorName = "";
        parser.require(XmlPullParser.START_TAG, null, "author");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if ("name".equals(name)) {
                authorName = readText(parser);
            }  else {
                skip(parser);
            }
        }
        return authorName;
    }

    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = null;
        parser.require(XmlPullParser.START_TAG, null, "link");
        String type = parser.getAttributeValue(null, "type");
        if (type != null && type.contains("pdf")) {
            link = parser.getAttributeValue(null, "href");
        }
        parser.nextTag();
        return link;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
