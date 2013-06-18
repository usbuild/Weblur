package com.lecoding.data;

import android.content.UriMatcher;
import android.net.Uri;

/**
 * Created by usbuild on 13-6-17.
 */
public class KeywordProvider extends AbstractContentProvider {

    private static final UriMatcher URI_MATCHER;
    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String AUTHORITY = "com.lecoding.data.keywordprovider";
    public static final String BASE_PATH = "keywords";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH, ALL_KEYS);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/#", ONE_KEY);
    }

    public static ContentProviderTraits traits = new ContentProviderTraits();

    static {
        traits.set_ID(_ID);
        traits.setNAME(NAME);
        traits.setAuthority(AUTHORITY);
        traits.setBasePath(BASE_PATH);
        traits.setUriMatcher(URI_MATCHER);
        traits.setContentUri(CONTENT_URI);
        traits.setTable("keywords");
    }

    public KeywordProvider() {
        super(traits);
    }
}
