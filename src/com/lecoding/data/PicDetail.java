package com.lecoding.data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-19 下午8:22
 */
public class PicDetail implements Serializable {
    public String thumbnailPic;

    public String getThumbnailPic() {
        return thumbnailPic;
    }

    public void setThumbnailPic(String thumbnailPic) {
        this.thumbnailPic = thumbnailPic;
    }
}
