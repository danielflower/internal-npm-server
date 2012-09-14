package com.danielflower.webstarter;

import java.util.HashMap;

public class ContentTypeGuesser {
    public static final String text_plain = "text/plain";
    public static final String text_html = "text/html";
    public static final String application_json = "application/json";

    public static final HashMap<String,String> MIME_TYPES = new HashMap<String, String>() {{
        put("html", text_html);
        put("txt", text_plain);
        put("css", "text/css");
        put("js", "application/javascript");
        put("png", "image/png");
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("gif", "image/gif");
        put("ico", "image/png");
        put("json", application_json);
    }};

    private final HashMap<String,String> mimeTypes;

    public ContentTypeGuesser() {
        this(MIME_TYPES);
    }

    public ContentTypeGuesser(HashMap<String, String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public String fromName(String path) {
        int i = path.lastIndexOf(".") + 1;

        String ending = path.substring(i).trim().toLowerCase();
        if (mimeTypes.containsKey(ending))
            return mimeTypes.get(ending);

        return "";
    }
}
