package com.danielflower.internalnpmserver.webserver;

import java.util.HashMap;

public class ContentTypeGuesser {
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String APPLICATION_JSON = "application/json";

    private static final HashMap<String, String> mimeTypes = new HashMap<String, String>() {{
        put("html", TEXT_HTML);
        put("txt", TEXT_PLAIN);
        put("css", "text/css");
        put("js", "application/javascript");
        put("png", "image/png");
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("gif", "image/gif");
        put("ico", "image/png");
        put("json", APPLICATION_JSON);
    }};

    public String fromName(String path) {

        int i = path.lastIndexOf(".") + 1;

        String ending = path.substring(i).trim().toLowerCase();
        if (mimeTypes.containsKey(ending))
            return mimeTypes.get(ending);

        return "";
    }
}
