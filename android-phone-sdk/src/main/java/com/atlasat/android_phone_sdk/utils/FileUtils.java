package com.atlasat.android_phone_sdk.utils;

import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.atlasat.android_phone_sdk.SDKCore;

import org.linphone.core.tools.Log;

import java.io.File;
import java.util.Locale;

public class FileUtils {
    public enum MimeType {
        PlainText,
        Pdf,
        Image,
        Video,
        Audio,
        Unknown
    }

    public static File getFileStoragePath(String fileName) {
        File path = getFileStorageDir(isExtensionImage(fileName));
        File file = new File(path, fileName);

        int prefix = 1;
        while (file.exists()) {
            file = new File(path, prefix + "_" + fileName);
            Log.w("[File Utils] File with that name already exists, renamed to " + file.getName());
            prefix += 1;
        }
        return file;
    }

    public static File getFileStorageDir(boolean isPicture) {
        File path = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.w("[File Utils] External storage is mounted");
            String directory = isPicture ? Environment.DIRECTORY_PICTURES : Environment.DIRECTORY_DOWNLOADS;
            path = SDKCore.singleHolder.get().getExternalFilesDir(directory);
            if (path == null) {
                Log.w("[File Utils] Couldn't get external storage path, using internal");
                path = SDKCore.singleHolder.get().context.getFilesDir();
            }
        }
        return path;
    }

    private static String getExtensionFromFileName(String fileName) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
        if (extension == null || extension.isEmpty()) {
            int i = fileName.lastIndexOf('.');
            if (i > 0 && i < fileName.length() - 1) {
                extension = fileName.substring(i + 1);
            }
        }

        return extension.toLowerCase(Locale.getDefault());
    }

    private static MimeType getMimeType(String type) {
        if (type == null || type.isEmpty()) return MimeType.Unknown;
        if (type.startsWith("image/")) return MimeType.Image;
        if (type.startsWith("text/plain")) return MimeType.PlainText;
        if (type.startsWith("video/")) return MimeType.Video;
        if (type.startsWith("audio/")) return MimeType.Audio;
        if (type.startsWith("application/pdf")) return MimeType.Pdf;
        return MimeType.Unknown;
    }

    private static boolean isExtensionImage(String path) {
        String extension = getExtensionFromFileName(path);
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return getMimeType(type) == MimeType.Image;
    }
}
