package com.violet.core.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    public static final String FILE_TYPE_MP4 = ".mp4";
    public static final String FILE_TYPE_MP3 = ".mp3";
    public static final String FILE_TYPE_JPG = ".jpg";

    public static final long MIN_DOWNLOAD_IMAGE_SOTROAGRE_SIZE = 1000 * 1000; // 1M

    /**
     * Default I/O buffer size
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    /**
     * Name of download directory
     */
    public static final String DOWNLOAD_DIRECTORY = "download";
    /**
     * Name of uncleanable directory
     */
    private static final String UNCLEANABLE_DIRECTORY = "uncleanable";
    /**
     * Download directory
     */
    private static File sDownloadDirectory = null;
    /**
     * Uncleanable directory
     */
    private static File sUncleanableDirectory = null;
    /**
     * Cache directory
     */
    private static File sCacheDirectory = null;

    /**
     * 获取下载目录
     *
     * @return
     */
    public static File getDownloadDirectory(Context context) {
        if (null == sDownloadDirectory) {
            synchronized (FileUtils.class) {
                if (null == sDownloadDirectory) {
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//                        sDownloadDirectory = SinaBaseCore.getInstance().getContext()
//                                .getExternalFilesDir(DOWNLOAD_DIRECTORY);
                        sDownloadDirectory = Environment.getExternalStorageDirectory();
//                                .getExternalFilesDir(DOWNLOAD_DIRECTORY);

                    } else {
                        sDownloadDirectory = context.getDir(
                                DOWNLOAD_DIRECTORY, Context.MODE_PRIVATE);
                    }
                }
            }
        }

        return sDownloadDirectory;
    }

    /**
     * 获取不可清除缓存的目录
     *
     * @return
     */
    public static File getUncleanableDirectory(Context context) {
        if (null == sUncleanableDirectory) {
            synchronized (FileUtils.class) {
                if (null == sUncleanableDirectory) {
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        sUncleanableDirectory = context
                                .getExternalFilesDir(UNCLEANABLE_DIRECTORY);
                    } else {
                        sUncleanableDirectory = context.getDir(
                                UNCLEANABLE_DIRECTORY, Context.MODE_PRIVATE);
                    }
                }
            }
        }

        return sUncleanableDirectory;
    }

    /**
     * 获取缓存目录
     *
     * @return
     */
    public static File getCacheDirectory(Context context) {
        if (null == sCacheDirectory) {
            synchronized (FileUtils.class) {
                if (null == sCacheDirectory) {
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        try {
                            sCacheDirectory = context.getExternalCacheDir();
                        } catch (Exception exception) {
                        }
                    }
                    // default to use internal cache directory
                    if (null == sCacheDirectory) {
                        sCacheDirectory = context.getCacheDir();
                    }
                }
            }
        }

        return sCacheDirectory;
    }

    public static File getDirFile(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }

        return f;
    }

    public static String getUncleanableDirectoryPath(Context context) {
        File directory = getUncleanableDirectory(context);
        String path = null;
        if (null != directory) {
            path = directory.getAbsolutePath();
        }

        return path;
    }

    public static String getCacheDirectoryPath(Context context) {
        File cacheDir = getCacheDirectory(context);
        String cachePath = null;
        if (null != cacheDir) {
            cachePath = cacheDir.getAbsolutePath();
        }
        return cachePath;
    }

    public static boolean isCacheSpaceLess(Context context) {
        try {
            long leftSize = getAvailableCacheSpace(context);
            if (leftSize < MIN_DOWNLOAD_IMAGE_SOTROAGRE_SIZE) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private static long getAvailableCacheSpace(Context context) {
        StatFs stat = new StatFs(getCacheDirectoryPath(context));
        long blockSize = 0L;
        long availBlocks = 0L;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availBlocks = stat.getAvailableBlocks();
        }
        return availBlocks * blockSize;
    }

    /**
     * Read data from input stream and return as a String
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static String readStream(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int cnt = 0;
        while ((cnt = in.read(buffer)) > -1) {
            baos.write(buffer, 0, cnt);
        }
        baos.flush();

        in.close();
        baos.close();

        return baos.toString();
    }

    /**
     * @param file: file object
     * @return file contents as string
     */
    public static String readFileToString(File file) {
        try {
            InputStream fileInputStream = new FileInputStream(file);
            return readStream(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] readFileToBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            throw new FileNotFoundException(filePath);
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fileChannel = fileInputStream.getChannel();
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
            while (fileChannel.read(byteBuffer) > 0) {
                //do nothing
            }
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                fileChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Copy data from input stream to output stream
     *
     * @param input
     * @param out
     * @throws IOException
     */
    public static void copyStream(InputStream input, OutputStream out) throws IOException {
        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int temp = -1;
        while ((temp = input.read(buffer)) != -1) {
            out.write(buffer, 0, temp);
        }
        out.flush();
    }

    /**
     * Copy stream data without closing stream
     *
     * @param input
     * @param out
     * @throws IOException
     * @author xuegang
     * @version Created: 2014年9月23日 下午5:45:06
     */
    public static void copyWithoutOutputClosing(InputStream input, OutputStream out)
            throws IOException {
        try {
            final byte[] buffer = new byte[512];
            int temp = -1;
            while ((temp = input.read(buffer)) != -1) {
                out.write(buffer, 0, temp);
                out.flush();
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            input.close();
        }
    }

    public static final int sSaveBmpSuccess = 0;
    public static final int sSaveBmpAlreadyExist = 1;
    public static final int sSaveBmpFailed = 2;
    public static final int sSaveBmpNull = 3;
    private static final String PATH = String.format("%s%s",
            Environment.getExternalStorageDirectory(), "/sina/news/save/");

    /**
     * savePicAlbum: save input bitmap to album.
     *
     * @param - ctx
     *          - bmp, bitmap which will be saved into album.
     *          - urlOrName, picture url or file name, which will be used to generate saved name.
     *          - outPathBuild, returned saved file path in string builder, can be null.
     *          - reCreate: recreate file when same file exists if true.
     * @return - sSaveBmpSuccess, sSaveBmpAlreadyExist, sSaveBmpFailed or sSaveBmpNull
     */
    public static int savePicAlbum(Context ctx, Bitmap bmp, String urlOrName,
                                   StringBuilder outPathStrBuilder, boolean reCreate) {
        if (bmp == null) {
            return sSaveBmpNull;
        }

        File file = new File(PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        if (urlOrName.toLowerCase(Locale.getDefault()).contains(".png")) {
            urlOrName = MD5.hexdigest(urlOrName).substring(0, 10) + ".png";
        } else {
            urlOrName = MD5.hexdigest(urlOrName).substring(0, 10) + ".jpg";
        }

        String path = PATH + urlOrName;
        if (outPathStrBuilder != null) {
            outPathStrBuilder.append(path);
        }
        File tempFile = new File(path);
        if (tempFile.exists()) {
            if (!reCreate) {
                return sSaveBmpAlreadyExist;
            }

            tempFile.delete();
        }

        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(path);
            if (urlOrName.toLowerCase(Locale.getDefault()).contains(".png")) {
                bmp.compress(CompressFormat.PNG, 100, outputStream);
            } else {
                bmp.compress(CompressFormat.JPEG, 100, outputStream);
            }
            outputStream.flush();
            outputStream.close();
            addFile2MediaDb(ctx, path);
        } catch (IOException e) {
            e.printStackTrace();
            return sSaveBmpFailed;
        }
        return sSaveBmpSuccess;
    }

    /**
     * copy: copy file from src to dst.
     *
     * @param - src: source path.
     *          - dst: destination path.
     */
    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        try {
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    /**
     * addFile2MediaDb: send {@value Intent#ACTION_MEDIA_SCANNER_SCAN_FILE} broadcast to notify
     * system update media database.
     * <p/>
     * Call this method to show items in Gallary immediately after save file to
     * sdcard.
     */
    public static void addFile2MediaDb(Context ctx, String path) {
        Uri newFileUri = Uri.fromFile(new File(path));
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(newFileUri);
        ctx.sendBroadcast(intent);
    }

    /**
     * Close a Closeable object safely
     *
     * @param object
     * @author xuegang
     * @version Created: 2014年11月25日 上午11:48:29
     */
    public static void safeClose(Closeable object) {
        if (null != object) {
            try {
                object.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取SDCard总大小
     *
     * @return
     */
    public static long getSDCardTotalSize() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            return sf.getBlockSize() * (sf.getBlockCount() / 1024);
        }
        return 0;
    }

    /**
     * 获取SDCard剩余大小
     *
     * @return(单位：k)
     */
    public static long getSDCardRemainSize() {
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File sdcardDir = Environment.getExternalStorageDirectory();
                StatFs sf = new StatFs(sdcardDir.getPath());
                return sf.getAvailableBlocks() * (sf.getBlockSize() / 1024);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        return 0;
    }

    /**
     * SD卡是否可用
     *
     * @return
     */
    public static boolean isSDCardUsable() {
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    //
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static String formatString(final String msg, Object... args) {

        return String.format(Locale.ENGLISH, msg, args);
    }


    /**
     * 压缩整个文件夹中的所有文件，生成指定名称的zip压缩包
     *
     * @param filePath 文件所在目录
     * @param zipPath  压缩后zip文件名称
     * @param dirFlag  zip文件中第一层是否包含一级目录，true包含；false没有
     */
    public static void ZipFolder(String filePath, String zipPath, boolean dirFlag) {
        try {
            File file = new File(filePath);// 要被压缩的文件夹
            File zipFile = new File(zipPath);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File fileSec : files) {
                    if (dirFlag) {
                        zipFile(zipOut, fileSec, file.getName() + File.separator);
                    } else {
                        zipFile(zipOut, fileSec, "");
                    }
                }
            }
            zipOut.flush();
            zipOut.finish();
            zipOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void zipFile(ZipOutputStream zipOut, File file, String baseDir) throws Exception {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileSec : files) {
                zipFile(zipOut, fileSec, baseDir + file.getName() + File.separator);
            }
        } else {
            byte[] buf = new byte[1024];
            InputStream input = new FileInputStream(file);
            zipOut.putNextEntry(new ZipEntry(baseDir + file.getName()));
            int len;
            while ((len = input.read(buf)) != -1) {
                zipOut.write(buf, 0, len);
            }
            input.close();
        }
    }

    private static final String LINE_SEP = System.getProperty("line.separator");

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(final String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    /**
     * 重命名文件
     *
     * @param filePath 文件路径
     * @param newName  新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(final String filePath, final String newName) {
        return rename(getFileByPath(filePath), newName);
    }

    /**
     * 重命名文件
     *
     * @param file    文件
     * @param newName 新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(final File file, final String newName) {
        // 文件为空返回 false
        if (file == null) return false;
        // 文件不存在返回 false
        if (!file.exists()) return false;
        // 新的文件名为空返回 false
        if (isSpace(newName)) return false;
        // 如果文件名没有改变返回 true
        if (newName.equals(file.getName())) return true;
        File newFile = new File(file.getParent() + File.separator + newName);
        // 如果重命名的文件已存在返回 false
        return !newFile.exists()
                && file.renameTo(newFile);
    }

    /**
     * 判断是否是目录
     *
     * @param dirPath 目录路径
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDir(final String dirPath) {
        return isDir(getFileByPath(dirPath));
    }

    /**
     * 判断是否是目录
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDir(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    /**
     * 判断是否是文件
     *
     * @param filePath 文件路径
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(final String filePath) {
        return isFile(getFileByPath(filePath));
    }

    /**
     * 判断是否是文件
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param dirPath 目录路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(final String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(final File file) {
        // 如果存在，是目录则返回 true，是文件则返回 false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(final String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        // 如果存在，是文件则返回 true，是目录则返回 false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param filePath 文件路径
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    public static boolean createFileByDeleteOldFile(final String filePath) {
        return createFileByDeleteOldFile(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param file 文件
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    public static boolean createFileByDeleteOldFile(final File file) {
        if (file == null) return false;
        // 文件存在并且删除失败返回 false
        if (file.exists() && !file.delete()) return false;
        // 创建目录失败返回 false
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制或移动目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param listener    是否覆盖监听器
     * @param isMove      是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveDir(final String srcDirPath, final String destDirPath, final OnReplaceListener listener, final boolean isMove) {
        return copyOrMoveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener, isMove);
    }

    /**
     * 复制或移动目录
     *
     * @param srcDir   源目录
     * @param destDir  目标目录
     * @param listener 是否覆盖监听器
     * @param isMove   是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveDir(final File srcDir, final File destDir, final OnReplaceListener listener, final boolean isMove) {
        if (srcDir == null || destDir == null) return false;
        // 如果目标目录在源目录中则返回 false，看不懂的话好好想想递归怎么结束
        // srcPath : F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res
        // destPath: F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res1
        // 为防止以上这种情况出现出现误判，须分别在后面加个路径分隔符
        String srcPath = srcDir.getPath() + File.separator;
        String destPath = destDir.getPath() + File.separator;
        if (destPath.contains(srcPath)) return false;
        // 源文件不存在或者不是目录则返回 false
        if (!srcDir.exists() || !srcDir.isDirectory()) return false;
        if (destDir.exists()) {
            if (listener.onReplace()) {// 需要覆盖则删除旧目录
                if (!deleteAllInDir(destDir)) {// 删除文件失败的话返回 false
                    return false;
                }
            } else {// 不需要覆盖直接返回即可 true
                return true;
            }
        }
        // 目标目录不存在返回 false
        if (!createOrExistsDir(destDir)) return false;
        File[] files = srcDir.listFiles();
        for (File file : files) {
            File oneDestFile = new File(destPath + file.getName());
            if (file.isFile()) {
                // 如果操作失败返回 false
                if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) return false;
            } else if (file.isDirectory()) {
                // 如果操作失败返回 false
                if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) return false;
            }
        }
        return !isMove || deleteDir(srcDir);
    }

    /**
     * 复制或移动文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param listener     是否覆盖监听器
     * @param isMove       是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveFile(final String srcFilePath, final String destFilePath, final OnReplaceListener listener, final boolean isMove) {
        return copyOrMoveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener, isMove);
    }

    /**
     * 复制或移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 是否覆盖监听器
     * @param isMove   是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveFile(final File srcFile, final File destFile, final OnReplaceListener listener, final boolean isMove) {
        if (srcFile == null || destFile == null) return false;
        // 如果源文件和目标文件相同则返回 false
        if (srcFile.equals(destFile)) return false;
        // 源文件不存在或者不是文件则返回 false
        if (!srcFile.exists() || !srcFile.isFile()) return false;
        if (destFile.exists()) {// 目标文件存在
            if (listener.onReplace()) {// 需要覆盖则删除旧文件
                if (!destFile.delete()) {// 删除文件失败的话返回 false
                    return false;
                }
            } else {// 不需要覆盖直接返回即可 true
                return true;
            }
        }
        // 目标目录不存在返回 false
        if (!createOrExistsDir(destFile.getParentFile())) return false;
        try {
            return FileIOUtils.writeFileFromIS(destFile, new FileInputStream(srcFile), false)
                    && !(isMove && !deleteFile(srcFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param listener    是否覆盖监听器
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyDir(final String srcDirPath, final String destDirPath, final OnReplaceListener listener) {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener);
    }

    /**
     * 复制目录
     *
     * @param srcDir   源目录
     * @param destDir  目标目录
     * @param listener 是否覆盖监听器
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyDir(final File srcDir, final File destDir, final OnReplaceListener listener) {
        return copyOrMoveDir(srcDir, destDir, listener, false);
    }

    /**
     * 复制文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param listener     是否覆盖监听器
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyFile(final String srcFilePath, final String destFilePath, final OnReplaceListener listener) {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener);
    }

    /**
     * 复制文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 是否覆盖监听器
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyFile(final File srcFile, final File destFile, final OnReplaceListener listener) {
        return copyOrMoveFile(srcFile, destFile, listener, false);
    }

    /**
     * 移动目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param listener    是否覆盖监听器
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static boolean moveDir(final String srcDirPath, final String destDirPath, final OnReplaceListener listener) {
        return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener);
    }

    /**
     * 移动目录
     *
     * @param srcDir   源目录
     * @param destDir  目标目录
     * @param listener 是否覆盖监听器
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static boolean moveDir(final File srcDir, final File destDir, final OnReplaceListener listener) {
        return copyOrMoveDir(srcDir, destDir, listener, true);
    }

    /**
     * 移动文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param listener     是否覆盖监听器
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static boolean moveFile(final String srcFilePath, final String destFilePath, final OnReplaceListener listener) {
        return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener);
    }

    /**
     * 移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 是否覆盖监听器
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static boolean moveFile(final File srcFile, final File destFile, final OnReplaceListener listener) {
        return copyOrMoveFile(srcFile, destFile, listener, true);
    }

    /**
     * 删除目录
     *
     * @param dirPath 目录路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteDir(final String dirPath) {
        return deleteDir(getFileByPath(dirPath));
    }

    /**
     * 删除目录
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteDir(final File dir) {
        if (dir == null) return false;
        // 目录不存在返回 true
        if (!dir.exists()) return true;
        // 不是目录返回 false
        if (!dir.isDirectory()) return false;
        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * 删除文件
     *
     * @param srcFilePath 文件路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(final String srcFilePath) {
        return deleteFile(getFileByPath(srcFilePath));
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(final File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    /**
     * 删除目录下所有东西
     *
     * @param dirPath 目录路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteAllInDir(final String dirPath) {
        return deleteAllInDir(getFileByPath(dirPath));
    }

    /**
     * 删除目录下所有东西
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteAllInDir(final File dir) {
        return deleteFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        });
    }

    /**
     * 删除目录下所有文件
     *
     * @param dirPath 目录路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFilesInDir(final String dirPath) {
        return deleteFilesInDir(getFileByPath(dirPath));
    }

    /**
     * 删除目录下所有文件
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFilesInDir(final File dir) {
        return deleteFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
    }

    /**
     * 删除目录下所有过滤的文件
     *
     * @param dirPath 目录路径
     * @param filter  过滤器
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFilesInDirWithFilter(final String dirPath, final FileFilter filter) {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter);
    }

    /**
     * 删除目录下所有过滤的文件
     *
     * @param dir    目录
     * @param filter 过滤器
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFilesInDirWithFilter(final File dir, final FileFilter filter) {
        if (dir == null) return false;
        // 目录不存在返回 true
        if (!dir.exists()) return true;
        // 不是目录返回 false
        if (!dir.isDirectory()) return false;
        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    if (file.isFile()) {
                        if (!file.delete()) return false;
                    } else if (file.isDirectory()) {
                        if (!deleteDir(file)) return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 获取目录下所有文件
     * <p>不递归进子目录</p>
     *
     * @param dirPath 目录路径
     * @return 文件链表
     */
    public static List<File> listFilesInDir(final String dirPath) {
        return listFilesInDir(dirPath, false);
    }

    /**
     * 获取目录下所有文件
     * <p>不递归进子目录</p>
     *
     * @param dir 目录
     * @return 文件链表
     */
    public static List<File> listFilesInDir(final File dir) {
        return listFilesInDir(dir, false);
    }

    /**
     * 获取目录下所有文件
     *
     * @param dirPath     目录路径
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    public static List<File> listFilesInDir(final String dirPath, final boolean isRecursive) {
        return listFilesInDir(getFileByPath(dirPath), isRecursive);
    }

    /**
     * 获取目录下所有文件
     *
     * @param dir         目录
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    public static List<File> listFilesInDir(final File dir, final boolean isRecursive) {
        return listFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        }, isRecursive);
    }

    /**
     * 获取目录下所有过滤的文件
     * <p>不递归进子目录</p>
     *
     * @param dirPath 目录路径
     * @param filter  过滤器
     * @return 文件链表
     */
    public static List<File> listFilesInDirWithFilter(final String dirPath,
                                                      final FileFilter filter) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, false);
    }

    /**
     * 获取目录下所有过滤的文件
     * <p>不递归进子目录</p>
     *
     * @param dir    目录
     * @param filter 过滤器
     * @return 文件链表
     */
    public static List<File> listFilesInDirWithFilter(final File dir,
                                                      final FileFilter filter) {
        return listFilesInDirWithFilter(dir, filter, false);
    }

    /**
     * 获取目录下所有过滤的文件
     *
     * @param dirPath     目录路径
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    public static List<File> listFilesInDirWithFilter(final String dirPath,
                                                      final FileFilter filter,
                                                      final boolean isRecursive) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive);
    }

    /**
     * 获取目录下所有过滤的文件
     *
     * @param dir         目录
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    public static List<File> listFilesInDirWithFilter(final File dir,
                                                      final FileFilter filter,
                                                      final boolean isRecursive) {
        if (!isDir(dir)) return null;
        List<File> list = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    list.add(file);
                }
                if (isRecursive && file.isDirectory()) {
                    //noinspection ConstantConditions
                    list.addAll(listFilesInDirWithFilter(file, filter, true));
                }
            }
        }
        return list;
    }

    /**
     * 获取文件最后修改的毫秒时间戳
     *
     * @param filePath 文件路径
     * @return 文件最后修改的毫秒时间戳
     */

    public static long getFileLastModified(final String filePath) {
        return getFileLastModified(getFileByPath(filePath));
    }

    /**
     * 获取文件最后修改的毫秒时间戳
     *
     * @param file 文件
     * @return 文件最后修改的毫秒时间戳
     */
    public static long getFileLastModified(final File file) {
        if (file == null) return -1;
        return file.lastModified();
    }

    /**
     * 简单获取文件编码格式
     *
     * @param filePath 文件路径
     * @return 文件编码
     */
    public static String getFileCharsetSimple(final String filePath) {
        return getFileCharsetSimple(getFileByPath(filePath));
    }

    /**
     * 简单获取文件编码格式
     *
     * @param file 文件
     * @return 文件编码
     */
    public static String getFileCharsetSimple(final File file) {
        int p = 0;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            p = (is.read() << 8) + is.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(is);
        }
        switch (p) {
            case 0xefbb:
                return "UTF-8";
            case 0xfffe:
                return "Unicode";
            case 0xfeff:
                return "UTF-16BE";
            default:
                return "GBK";
        }
    }

    /**
     * 获取文件行数
     *
     * @param filePath 文件路径
     * @return 文件行数
     */
    public static int getFileLines(final String filePath) {
        return getFileLines(getFileByPath(filePath));
    }

    /**
     * 获取文件行数
     * <p>比 readLine 要快很多</p>
     *
     * @param file 文件
     * @return 文件行数
     */
    public static int getFileLines(final File file) {
        int count = 1;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            int readChars;
            if (LINE_SEP.endsWith("\n")) {
                while ((readChars = is.read(buffer, 0, 1024)) != -1) {
                    for (int i = 0; i < readChars; ++i) {
                        if (buffer[i] == '\n') ++count;
                    }
                }
            } else {
                while ((readChars = is.read(buffer, 0, 1024)) != -1) {
                    for (int i = 0; i < readChars; ++i) {
                        if (buffer[i] == '\r') ++count;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(is);
        }
        return count;
    }

    /**
     * 获取目录大小
     *
     * @param dirPath 目录路径
     * @return 文件大小
     */
    public static String getDirSize(final String dirPath) {
        return getDirSize(getFileByPath(dirPath));
    }

    /**
     * 获取目录大小
     *
     * @param dir 目录
     * @return 文件大小
     */
    public static String getDirSize(final File dir) {
        long len = getDirLength(dir);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小
     */
    public static String getFileSize(final String filePath) {
        return getFileSize(getFileByPath(filePath));
    }

    /**
     * 获取文件大小
     *
     * @param file 文件
     * @return 文件大小
     */
    public static String getFileSize(final File file) {
        long len = getFileLength(file);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    /**
     * 获取目录长度
     *
     * @param dirPath 目录路径
     * @return 目录长度
     */
    public static long getDirLength(final String dirPath) {
        return getDirLength(getFileByPath(dirPath));
    }

    /**
     * 获取目录长度
     *
     * @param dir 目录
     * @return 目录长度
     */
    public static long getDirLength(final File dir) {
        if (!isDir(dir)) return -1;
        long len = 0;
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    len += getDirLength(file);
                } else {
                    len += file.length();
                }
            }
        }
        return len;
    }

    /**
     * 获取文件长度
     *
     * @param filePath 文件路径
     * @return 文件长度
     */
    public static long getFileLength(final String filePath) {
        return getFileLength(getFileByPath(filePath));
    }

    /**
     * 获取文件长度
     *
     * @param file 文件
     * @return 文件长度
     */
    public static long getFileLength(final File file) {
        if (!isFile(file)) return -1;
        return file.length();
    }

    /**
     * 获取文件的 MD5 校验码
     *
     * @param filePath 文件路径
     * @return 文件的 MD5 校验码
     */
    public static String getFileMD5ToString(final String filePath) {
        File file = isSpace(filePath) ? null : new File(filePath);
        return getFileMD5ToString(file);
    }

    /**
     * 获取文件的 MD5 校验码
     *
     * @param file 文件
     * @return 文件的 MD5 校验码
     */
    public static String getFileMD5ToString(final File file) {
        return bytes2HexString(getFileMD5(file));
    }

    /**
     * 获取文件的 MD5 校验码
     *
     * @param filePath 文件路径
     * @return 文件的 MD5 校验码
     */
    public static byte[] getFileMD5(final String filePath) {
        return getFileMD5(getFileByPath(filePath));
    }

    /**
     * 获取文件的 MD5 校验码
     *
     * @param file 文件
     * @return 文件的 MD5 校验码
     */
    public static byte[] getFileMD5(final File file) {
        if (file == null) return null;
        DigestInputStream dis = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            dis = new DigestInputStream(fis, md);
            byte[] buffer = new byte[1024 * 256];
            while (true) {
                if (!(dis.read(buffer) > 0)) break;
            }
            md = dis.getMessageDigest();
            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(dis);
        }
        return null;
    }

    /**
     * 获取全路径中的最长目录
     *
     * @param file 文件
     * @return filePath 最长目录
     */
    public static String getDirName(final File file) {
        if (file == null) return null;
        return getDirName(file.getPath());
    }

    /**
     * 获取全路径中的最长目录
     *
     * @param filePath 文件路径
     * @return filePath 最长目录
     */
    public static String getDirName(final String filePath) {
        if (isSpace(filePath)) return filePath;
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? "" : filePath.substring(0, lastSep + 1);
    }

    /**
     * 获取全路径中的文件名
     *
     * @param file 文件
     * @return 文件名
     */
    public static String getFileName(final File file) {
        if (file == null) return null;
        return getFileName(file.getPath());
    }

    /**
     * 获取全路径中的文件名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    public static String getFileName(final String filePath) {
        if (isSpace(filePath)) return filePath;
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }

    /**
     * 获取全路径中的不带拓展名的文件名
     *
     * @param file 文件
     * @return 不带拓展名的文件名
     */
    public static String getFileNameNoExtension(final File file) {
        if (file == null) return null;
        return getFileNameNoExtension(file.getPath());
    }

    /**
     * 获取全路径中的不带拓展名的文件名
     *
     * @param filePath 文件路径
     * @return 不带拓展名的文件名
     */
    public static String getFileNameNoExtension(final String filePath) {
        if (isSpace(filePath)) return filePath;
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }

    /**
     * 获取全路径中的文件拓展名
     *
     * @param file 文件
     * @return 文件拓展名
     */
    public static String getFileExtension(final File file) {
        if (file == null) return null;
        return getFileExtension(file.getPath());
    }

    /**
     * 获取全路径中的文件拓展名
     *
     * @param filePath 文件路径
     * @return 文件拓展名
     */
    public static String getFileExtension(final String filePath) {
        if (isSpace(filePath)) return filePath;
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return "";
        return filePath.substring(lastPoi + 1);
    }

    ///////////////////////////////////////////////////////////////////////////
    // copy from ConvertUtils
    ///////////////////////////////////////////////////////////////////////////

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * byteArr 转 hexString
     * <p>例如：</p>
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes 字节数组
     * @return 16 进制大写字符串
     */
    private static String bytes2HexString(final byte[] bytes) {
        if (bytes == null) return null;
        int len = bytes.length;
        if (len <= 0) return null;
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    /**
     * 根据Uri返回文件绝对路径
     * 兼容了file:///开头的 和 content://开头的情况
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 字节数转合适内存大小
     * <p>保留 2 位小数</p>
     *
     * @param byteNum 字节数
     * @return 合适内存大小
     */
    @SuppressLint("DefaultLocale")
    private static String byte2FitMemorySize(final long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < 1024) {
            return String.format("%.2fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format("%.2fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format("%.2fMB", (double) byteNum / 1048576);
        } else {
            return String.format("%.2fGB", (double) byteNum / 1073741824);
        }
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public interface OnReplaceListener {
        boolean onReplace();
    }

    /**
     * 检查文件是否存在
     */
    public static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }
}
