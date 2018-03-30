package neobis.alier.parking.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Calendar;

import io.realm.RealmList;
import neobis.alier.parking.models.Data;

public class FileUtils {

    public static Uri getNormalizedUri(Context context, Uri uri) {
        if (uri != null && uri.toString().contains("content:"))
            return Uri.fromFile(FileUtils.getPath(context, uri, MediaStore.Images.Media.DATA));
        else return uri;
    }

    private static File getPath(Context context, Uri uri, String column) {
        String[] columns = {column};
        Cursor cursor = context.getContentResolver().query(uri, columns, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(column);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return new File(path);
    }

    public static String getImagePathFromInputStreamUri(Context context, Uri uri) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri); // context needed
                File photoFile = createTemporalFileFrom(context, inputStream);

                filePath = photoFile.getPath();

            } catch (FileNotFoundException e) {
                // log
            } catch (IOException e) {
                // log
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return filePath;
    }

    public static RealmList<Data> readFile(String fileName, Context activity) {
        // Reading saved data from assets/ file(data is constant)
        String json_file = null;
        try {
            json_file = FileUtils.readFile(activity.getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Type listType = new TypeToken<RealmList<Data>>() {
        }.getType();
        return new Gson().fromJson(json_file, listType);
    }

    public synchronized static <T> boolean writeCache(Context context, String filename, T data) {
        File cacheDir = new File(context.getCacheDir(), "path");
        if (!cacheDir.exists()) cacheDir.mkdir();
        try {
            FileOutputStream fos = new FileOutputStream(new File(cacheDir, filename));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized static <T> T readCache(Context context, String filename) {
        File cacheDir = new File(context.getCacheDir(), "path");
        if (!cacheDir.exists()) return null;
        try {
            FileInputStream fis = new FileInputStream(new File(cacheDir, filename));
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static File createTemporalFileFrom(Context context, InputStream inputStream) throws IOException {
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];

            targetFile = createTemporalFile(context);
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return targetFile;
    }

    private static File createTemporalFile(Context context) {
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), Calendar.getInstance().getTimeInMillis() + ".jpg"); // context needed
    }

    public static Uri getCaptureImageOutputUri(Context context, String fileName) {
        Uri outputFileUri = null;
        File getImage = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), fileName + ".jpeg"));
        }
        return outputFileUri;
    }

    public static Uri getPickImageResultUri(Context context, Intent data, String fileName) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera || data.getData() == null
                ? getCaptureImageOutputUri(context, fileName) : data.getData();
    }

    @SuppressLint("NewApi")
    private static String getImageFile(Context context, Uri uri) {
        try {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    switch (type) {
                        case "image":
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "video":
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "audio":
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                            break;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri.getPath();
    }

    public static File getNormalizedImagePath(Context context, Uri uri) {
        String path = null;
        try {
            path = getImageFile(context, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (path == null) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                File tempFile = File.createTempFile(System.currentTimeMillis() + "", "jpg");
                tempFile.deleteOnExit();
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(tempFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (inputStream != null) {
                    byte[] buffer = new byte[1024 * 24];
                    int n;
                    while ((n = inputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, n);
                    }
                    inputStream.close();
                }

                if (out != null) {
                    out.close();
                }
                return tempFile;
            } catch (IOException e) {
                e.printStackTrace();
                return new File(uri.getPath());
            }
        } else {
            return new File(path);
        }
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String readFile(InputStream stream) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

        } catch (Exception ignored) {
        }
        return content.toString();

    }
}