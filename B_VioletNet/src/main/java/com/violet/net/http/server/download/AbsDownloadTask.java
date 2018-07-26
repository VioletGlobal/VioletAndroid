package com.violet.net.http.server.download;

import com.violet.net.http.model.Progress;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by liuqun1 on 2018/2/5.
 */

public class AbsDownloadTask<T extends AbsDownloadTask> implements IDownloadTask<T> {
    public Progress progress;
    public Map<Object, DownloadListener> listeners;
    @Override
    public T folder(String folder) {
        return null;
    }

    @Override
    public T fileName(String fileName) {
        return null;
    }

    @Override
    public T priority(int priority) {
        return null;
    }

    @Override
    public T extra1(Serializable extra1) {
        return null;
    }

    @Override
    public T extra2(Serializable extra2) {
        return null;
    }

    @Override
    public T extra3(Serializable extra3) {
        return null;
    }

    @Override
    public T save() {
        return null;
    }

    @Override
    public T register(DownloadListener listener) {
        return null;
    }

    @Override
    public void unRegister(DownloadListener listener) {

    }

    @Override
    public void unRegister(String tag) {

    }

    @Override
    public void start() {

    }

    @Override
    public void restart() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void remove() {

    }

    @Override
    public T remove(boolean isDeleteFile) {
        return null;
    }

    @Override
    public void run() {

    }
}
