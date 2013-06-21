package com.loopj.android.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import java.io.*;
import java.lang.ref.SoftReference;
import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebImageCache {
    public static final String DISK_CACHE_PATH = "/mnt/sdcard/weiblur/web_image_cache/";

    private ConcurrentHashMap<String, SoftReference<Bitmap>> memoryCache;
    private String diskCachePath;
    private boolean diskCacheEnabled = false;
    private ExecutorService writeThread;

    public WebImageCache(Context context) {
        // Set up in-memory cache store
        memoryCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();

        // Set up disk cache store
        Context appContext = context.getApplicationContext();
//        diskCachePath = appContext.getCacheDir().getAbsolutePath() + DISK_CACHE_PATH;
        diskCachePath = DISK_CACHE_PATH;

        File outFile = new File(diskCachePath);
        if (!outFile.exists())
            outFile.mkdirs();

        diskCacheEnabled = outFile.exists();

        // Set up threadpool for image fetching tasks
        writeThread = Executors.newSingleThreadExecutor();
    }

    public Bitmap get(final String url) {
        Bitmap bitmap = null;

        // Check for image in memory
        bitmap = getBitmapFromMemory(url);

        // Check for image on disk cache
        if (bitmap == null) {
            bitmap = getBitmapFromDisk(url);

            // Write bitmap back into memory cache
            if (bitmap != null) {
                cacheBitmapToMemory(url, bitmap);
            }
        }

        return bitmap;
    }

    public void put(String url, Bitmap bitmap) {
        cacheBitmapToMemory(url, bitmap);
        cacheBitmapToDisk(url, bitmap);
    }

    public void remove(String url) {
        if (url == null) {
            return;
        }

        // Remove from memory cache
        memoryCache.remove(getCacheKey(url));

        // Remove from file cache
        File f = new File(diskCachePath, getCacheKey(url));
        if (f.exists() && f.isFile()) {
            f.delete();
        }
    }

    public void clear() {
        // Remove everything from memory cache
        memoryCache.clear();

        // Remove everything from file cache
        File cachedFileDir = new File(diskCachePath);
        if (cachedFileDir.exists() && cachedFileDir.isDirectory()) {
            File[] cachedFiles = cachedFileDir.listFiles();
            for (File f : cachedFiles) {
                if (f.exists() && f.isFile()) {
                    f.delete();
                }
            }
        }
    }

    public String cacheSize() {
        File dir = new File(diskCachePath);
        String fileSizeString;
        DecimalFormat df = new DecimalFormat("#.00");
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                result += fileList[i].length();
            }
            if (result == 0) return "0";


            if (result < 1024) {
                fileSizeString = df.format((double) result) + "B";
            } else if (result < 1048576) {
                fileSizeString = df.format((double) result / 1024) + "K";
            } else if (result < 1073741824) {
                fileSizeString = df.format((double) result / 1048576) + "M";
            } else {
                fileSizeString = df.format((double) result / 1073741824) + "G";
            }
            return fileSizeString;
        }
        return "";
    }

    private void cacheBitmapToMemory(final String url, final Bitmap bitmap) {
        memoryCache.put(getCacheKey(url), new SoftReference<Bitmap>(bitmap));
    }

    private void cacheBitmapToDisk(final String url, final Bitmap bitmap) {
        writeThread.execute(new Runnable() {
            @Override
            public void run() {
                if (diskCacheEnabled) {
                    BufferedOutputStream ostream = null;
                    try {
                        ostream = new BufferedOutputStream(new FileOutputStream(new File(diskCachePath, getCacheKey(url))), 2 * 1024);
                        bitmap.compress(CompressFormat.PNG, 100, ostream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (ostream != null) {
                                ostream.flush();
                                ostream.close();
                            }
                        } catch (IOException e) {
                        }
                    }
                }
            }
        });
    }

    private Bitmap getBitmapFromMemory(String url) {
        Bitmap bitmap = null;
        SoftReference<Bitmap> softRef = memoryCache.get(getCacheKey(url));
        if (softRef != null) {
            bitmap = softRef.get();
        }

        return bitmap;
    }

    private Bitmap getBitmapFromDisk(String url) {
        Bitmap bitmap = null;
        if (diskCacheEnabled) {
            String filePath = getFilePath(url);
            File file = new File(filePath);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(filePath);
            }
        }
        return bitmap;
    }

    private String getFilePath(String url) {
        return diskCachePath + getCacheKey(url);
    }

    private String getCacheKey(String url) {
        if (url == null) {
            throw new RuntimeException("Null url passed in");
        } else {
            return url.replaceAll("[:/,%?&=]", "+").replaceAll("[+]+", "+");
        }
    }
}
