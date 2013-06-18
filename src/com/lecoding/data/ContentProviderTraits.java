package com.lecoding.data;

import android.content.UriMatcher;
import android.net.Uri;

/**
 * Created by usbuild on 13-6-18.
 */
public class ContentProviderTraits {
    private String authority;
    private String basePath;
    private String _ID;
    private String NAME;
    private UriMatcher uriMatcher;
    private Uri contentUri;
    private String table;
    private String dbname;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public Uri getContentUri() {
        return contentUri;
    }

    public void setContentUri(Uri contentUri) {
        this.contentUri = contentUri;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public UriMatcher getUriMatcher() {
        return uriMatcher;
    }

    public void setUriMatcher(UriMatcher uriMatcher) {
        this.uriMatcher = uriMatcher;
    }
}
