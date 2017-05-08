package com.appjumper.silkscreen.util;

/**
 * -------不传, 就用默认值 -----
 */
public class ApplibraryConfig {
    // app 缓存目录
    private String appDir;
    // 图片缓存目录
    private String imageCacheDir;
    // 开关是否记录异常崩溃错误记录
    private boolean recordTrashEx;
    // 开关是否记录异常错误记录
    private boolean recordNormalEx;

    public ApplibraryConfig(){

    }
    public ApplibraryConfig(String appDir, String imageCacheDir, boolean recordTrashEx, boolean recordNormalEx) {
        this.appDir = appDir;
        this.imageCacheDir = imageCacheDir;
        this.recordTrashEx = recordTrashEx;
        this.recordNormalEx = recordNormalEx;
    }

    public String getAppDir() {
        return appDir;
    }

    public void setAppDir(String appDir) {
        this.appDir = appDir;
    }

    public String getImageCacheDir() {
        return imageCacheDir;
    }

    public void setImageCacheDir(String imageCacheDir) {
        this.imageCacheDir = imageCacheDir;
    }

    public boolean isRecordTrashEx() {
        return recordTrashEx;
    }

    public void setRecordTrashEx(boolean recordTrashEx) {
        this.recordTrashEx = recordTrashEx;
    }

    public boolean isRecordNormalEx() {
        return recordNormalEx;
    }

    public void setRecordNormalEx(boolean recordNormalEx) {
        this.recordNormalEx = recordNormalEx;
    }
}
