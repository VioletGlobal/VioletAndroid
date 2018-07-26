package com.violet.net.http.server.download;

/**
 * Created by liuqun1 on 2018/2/24.
 */

public class DownloadConfig {
    private String mDownloadFolder;//默认下载目录

    public DownloadConfig(Builder builder){
        mDownloadFolder = builder.downloadFolder;
    }

    public String getDownloadFolder(){
        return mDownloadFolder;
    }


    public static class Builder {
        public String downloadFolder;

        public Builder downloadFolder(String folder){
            downloadFolder = folder;
            return this;
        }

        public DownloadConfig build(){
            return new DownloadConfig(this);
        }
    }
}
