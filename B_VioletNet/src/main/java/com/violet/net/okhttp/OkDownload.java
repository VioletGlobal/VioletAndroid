/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.violet.net.okhttp;

import android.os.Environment;
import android.text.TextUtils;

import com.violet.net.http.model.Progress;
import com.violet.net.http.request.Request;
import com.violet.net.http.server.download.AbsDownloadTask;
import com.violet.net.http.server.download.DownloadConfig;
import com.violet.net.okhttp.db.DownloadDBManager;
import com.violet.net.okhttp.download.DownloadTask;
import com.violet.net.okhttp.utils.IOUtils;
import com.violet.net.okhttp.utils.OkLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：全局的下载管理类
 * 修订历史：
 * ================================================
 */
public class OkDownload {

    private String folder;                                      //下载的默认文件夹
//    private DownloadThreadPool threadPool;                      //下载的线程池
    private ConcurrentHashMap<String, AbsDownloadTask> taskMap;    //所有任务

    public static OkDownload getInstance() {
        return OkDownloadHolder.instance;
    }

    private static class OkDownloadHolder {
        private static final OkDownload instance = new OkDownload();
    }

    private OkDownload() {
//        folder = Environment.getExternalStorageDirectory() + File.separator + "download" + File.separator;
//        IOUtils.createFolder(folder);
//        threadPool = new DownloadThreadPool();
        taskMap = new ConcurrentHashMap<>();

        //校验数据的有效性，防止下载过程中退出，第二次进入的时候，由于状态没有更新导致的状态错误
        List<Progress> taskList = DownloadDBManager.getInstance().getDownloading();
        for (Progress info : taskList) {
            if (info.status == Progress.WAITING || info.status == Progress.LOADING || info.status == Progress.PAUSE) {
                info.status = Progress.NONE;
            }
        }
        DownloadDBManager.getInstance().replace(taskList);
    }

    public void init(DownloadConfig config){
        folder = config.getDownloadFolder();
        IOUtils.createFolder(folder);
    }

    public static AbsDownloadTask request(String tag, Request request) {
        return request(tag, request, null);
    }

    public static AbsDownloadTask request(String tag, Request request, String folder) {
        Map<String, AbsDownloadTask> taskMap = OkDownload.getInstance().getTaskMap();
        AbsDownloadTask task = taskMap.get(tag);
        if (task == null) {
            task = new DownloadTask(tag, request);
            taskMap.put(tag, task);
        }
        if(!TextUtils.isEmpty(folder)){
            task.folder(folder);
        }
        return task;
    }

    /** 从数据库中恢复任务 */
    public static AbsDownloadTask restore(Progress progress) {
        Map<String, AbsDownloadTask> taskMap = OkDownload.getInstance().getTaskMap();
        AbsDownloadTask task = taskMap.get(progress.tag);
        if (task == null) {
            task = new DownloadTask(progress);
            taskMap.put(progress.tag, task);
        }
        return task;
    }

    /** 从数据库中恢复任务 */
    public static List<AbsDownloadTask> restore(List<Progress> progressList) {
        Map<String, AbsDownloadTask> taskMap = OkDownload.getInstance().getTaskMap();
        List<AbsDownloadTask> tasks = new ArrayList<>();
        for (Progress progress : progressList) {
            DownloadTask task = (DownloadTask) taskMap.get(progress.tag);
            if (task == null) {
                task = new DownloadTask(progress);
                taskMap.put(progress.tag, task);
            }
            tasks.add(task);
        }
        return tasks;
    }

    /** 开始所有任务 */
    public void startAll() {
        for (Map.Entry<String, AbsDownloadTask> entry : taskMap.entrySet()) {
            AbsDownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            task.start();
        }
    }

    /** 暂停全部任务 */
    public void pauseAll() {
        //先停止未开始的任务
        for (Map.Entry<String, AbsDownloadTask> entry : taskMap.entrySet()) {
            AbsDownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            if (task.progress.status != Progress.LOADING) {
                task.pause();
            }
        }
        //再停止进行中的任务
        for (Map.Entry<String, AbsDownloadTask> entry : taskMap.entrySet()) {
            AbsDownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            if (task.progress.status == Progress.LOADING) {
                task.pause();
            }
        }
    }

    /** 删除所有任务 */
    public void removeAll() {
        removeAll(false);
    }

    /**
     * 删除所有任务
     *
     * @param isDeleteFile 删除任务是否删除文件
     */
    public void removeAll(boolean isDeleteFile) {
        Map<String, AbsDownloadTask> map = new HashMap<>(taskMap);
        //先删除未开始的任务
        for (Map.Entry<String, AbsDownloadTask> entry : map.entrySet()) {
            AbsDownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            if (task.progress.status != Progress.LOADING) {
                task.remove(isDeleteFile);
            }
        }
        //再删除进行中的任务
        for (Map.Entry<String, AbsDownloadTask> entry : map.entrySet()) {
            AbsDownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            if (task.progress.status == Progress.LOADING) {
                task.remove(isDeleteFile);
            }
        }
    }

    /** 设置下载目录 */
    public String getFolder() {
        if(folder == null){
            folder = Environment.getExternalStorageDirectory() + File.separator + "download" + File.separator;
            IOUtils.createFolder(folder);
        }
        return folder;
    }

    public OkDownload setFolder(String folder) {
        this.folder = folder;
        return this;
    }

//    public DownloadThreadPool getThreadPool() {
//        return threadPool;
//    }

    public Map<String, AbsDownloadTask> getTaskMap() {
        return taskMap;
    }

    public AbsDownloadTask getTask(String tag) {
        return taskMap.get(tag);
    }

    public boolean hasTask(String tag) {
        return taskMap.containsKey(tag);
    }

    public AbsDownloadTask removeTask(String tag) {
        return taskMap.remove(tag);
    }

//    public void addOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
//        threadPool.getExecutor().addOnAllTaskEndListener(listener);
//    }
//
//    public void removeOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
//        threadPool.getExecutor().removeOnAllTaskEndListener(listener);
//    }


    private List<OnTaskEndListener> taskEndListenerList;

    public void addOnTaskEndListener(OnTaskEndListener taskEndListener) {
        if (taskEndListenerList == null) taskEndListenerList = new ArrayList<>();
        taskEndListenerList.add(taskEndListener);
    }

    public void removeOnTaskEndListener(OnTaskEndListener taskEndListener) {
        taskEndListenerList.remove(taskEndListener);
    }

    public interface OnTaskEndListener {
        void onTaskEnd(Runnable r);
    }

    private List<OnAllTaskEndListener> allTaskEndListenerList;

    public void addOnAllTaskEndListener(OnAllTaskEndListener allTaskEndListener) {
        if (allTaskEndListenerList == null) allTaskEndListenerList = new ArrayList<>();
        allTaskEndListenerList.add(allTaskEndListener);
    }

    public void removeOnAllTaskEndListener(OnAllTaskEndListener allTaskEndListener) {
        allTaskEndListenerList.remove(allTaskEndListener);
    }

    public interface OnAllTaskEndListener {
        void onAllTaskEnd();
    }

    public void taskEnd(){
//        if (taskEndListenerList != null && taskEndListenerList.size() > 0) {
//            for (final OnTaskEndListener listener : taskEndListenerList) {
//                HttpUtils.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        listener.onTaskEnd(r);
//                    }
//                });
//            }
//        }
        //当前正在运行的数量为1 表示当前正在停止的任务，同时队列中没有任务，表示所有任务下载完毕
//        if (getActiveCount() == 1 && getQueue().size() == 0) {
//            if (allTaskEndListenerList != null && allTaskEndListenerList.size() > 0) {
//                for (final OnAllTaskEndListener listener : allTaskEndListenerList) {
//                    innerHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            listener.onAllTaskEnd();
//                        }
//                    });
//                }
//            }
//        }
    }
}
