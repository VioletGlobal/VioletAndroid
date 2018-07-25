package com.violet.lib.android.storage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.view.View;

import com.violet.base.ui.fragment.BaseFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import static android.content.Context.MODE_WORLD_WRITEABLE;

/**
 * Created by kan212 on 2018/5/14.
 * Android的五中存储方式
 */

public class Storage5Fragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView(View parent) {

    }

    @Override
    protected void initData(Intent intent) {

    }

    /**
     * 保存基于XML文件存储的key-value键值对数据，通常用来存储一些简单的配置信息。通过DDMS的File Explorer面板，
     * 展开文件浏览树,很明显SharedPreferences数据总是存储在/data/data/<package name>/shared_prefs目录下。
     * SharedPreferences对象本身只能获取数据而不支持存储和修改,存储修改是通过SharedPreferences.edit()
     * 获取的内部接口Editor对象实现。 SharedPreferences本身是一个接口，程序无法直接创建SharedPreferences实例，
     * 只能通过Context提供的getSharedPreferences(String name, int mode)方法来获取SharedPreferences实例，
     * 该方法中name表示要操作的xml文件名，
     * 第二个参数具体如下：
     * Context.MODE_PRIVATE: 指定该SharedPreferences数据只能被本应用程序读、写。
     * Context.MODE_WORLD_READABLE:  指定该SharedPreferences数据能被其他应用程序读，但不能写。
     * Context.MODE_WORLD_WRITEABLE:  指定该SharedPreferences数据能被其他应用程序读，写
     * Editor有如下主要重要方法：
     * SharedPreferences.Editor clear():清空SharedPreferences里所有数据
     * SharedPreferences.Editor putXxx(String key , xxx value): 向SharedPreferences存入指定key对应的数据，其中xxx 可以是boolean,float,int等各种基本类型据
     * SharedPreferences.Editor remove(): 删除SharedPreferences中指定key对应的数据项
     * boolean commit(): 当Editor编辑完成后，使用该方法提交修改
     *
     * @param str
     */
    private void saveSharedPreferences(String str) {
        //创建一个SharedPreferences.Editor接口对象，lock表示要写入的XML文件名，MODE_WORLD_WRITEABLE写操作
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("lock", MODE_WORLD_WRITEABLE).edit();
        editor.putString("test", str);
        editor.commit();
    }

    /**
     * 核心原理: Context提供了两个方法来打开数据文件里的文件IO流 FileInputStream openFileInput(String name);
     * FileOutputStream(String name , int mode),这两个方法第一个参数 用于指定文件名，第二个参数指定打开文件
     * 的模式。具体有以下值可选：
     * MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，
     * 如果想把新写入的内容追加到原文件中。可以使用Context.MODE_APPEND
     * MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
     * MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；
     * MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
     * 除此之外，Context还提供了如下几个重要的方法：
     * getDir(String name , int mode):在应用程序的数据文件夹下获取或者创建name对应的子目录
     * File getFilesDir():获取该应用程序的数据文件夹得绝对路径
     * String[] fileList():返回该应用数据文件夹的全部文件
     */
    private void saveFile() {
        readFile("test.txt");
        writeFile("test");
    }

    private String readFile(String path) {
        try {
            FileInputStream inputStream = getActivity().openFileInput(path);
            byte[] buffer = new byte[1024];
            int hasRead = 0;
            StringBuffer sb = new StringBuffer();
            while ((hasRead = inputStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, hasRead));
            }
            inputStream.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeFile(String msg) {
        try {
            FileOutputStream outputStream = getActivity().openFileOutput("fileName", Context.MODE_APPEND);
            outputStream.write(msg.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1、调用Environment的getExternalStorageState()方法判断手机上是否插了sd卡,且应用程序具有读写SD卡的权限，
     * 如下代码将返回true
     * Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
     * 2、调用Environment.getExternalStorageDirectory()方法来获取外部存储器，也就是SD卡的目录,
     * 或者使用"/mnt/sdcard/"目录
     * 3、使用IO流操作SD卡上的文件
     */
    private void saveSdcard() {
        readSdcard("","");
        writeSdcard("","");
    }

    private String readSdcard(String DIR,String FILENAME) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 如果sdcard存在
            File file = new File(Environment.getExternalStorageDirectory()
                    .toString()
                    + File.separator
                    + DIR
                    + File.separator
                    + FILENAME); // 定义File类对象
            if (!file.getParentFile().exists()) { // 父文件夹不存在
                file.getParentFile().mkdirs(); // 创建文件夹
            }
            Scanner scan = null; // 扫描输入
            StringBuilder sb = new StringBuilder();
            try {
                scan = new Scanner(new FileInputStream(file)); // 实例化Scanner
                while (scan.hasNext()) { // 循环读取
                    sb.append(scan.next() + "\n"); // 设置文本
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (scan != null) {
                    scan.close(); // 关闭打印流
                }
            }
        } else { // SDCard不存在，使用Toast提示用户
        }
        return null;
    }

    private void writeSdcard(String DIR,String FILENAME) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 如果sdcard存在
            File file = new File(Environment.getExternalStorageDirectory()
                    .toString()
                    + File.separator
                    + DIR
                    + File.separator
                    + FILENAME); // 定义File类对象
            if (!file.getParentFile().exists()) { // 父文件夹不存在
                file.getParentFile().mkdirs(); // 创建文件夹
            }
            PrintStream out = null; // 打印流对象用于输出
            try {
                out = new PrintStream(new FileOutputStream(file, true)); // 追加文件
                out.println(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close(); // 关闭打印流
                }
            }
        } else { // SDCard不存在，使用Toast提示用户
        }
    }


}
