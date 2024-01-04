package com.example.dacmini_projet;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.locks.ReentrantLock;

// downloader class
public class FileDownloader extends AsyncTask<String, Integer, String> {

    private ReentrantLock lock = new ReentrantLock();
    private volatile boolean isPaused = false;
    private DownloadListener downloadListener;
    private int fileLength;

    public FileDownloader(DownloadListener downloadListener, int fileLength) {

        this.downloadListener = downloadListener;
        this.fileLength = fileLength;
    }


    @Override
    protected String doInBackground(String... params) {
        String fileUrl = params[0];
        String fileName = params[1];
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(fileName);
            byte[] data = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                while (isPaused) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }
                }
                total += count;
                publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void startDownload(String fileUrl, String fileName) {
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fileUrl, fileName);
    }

    public void pause() {
            isPaused = true;
    }

    public void resume() {
            isPaused = false;
    }


    // send the progress
    @Override
    protected void onProgressUpdate(Integer... values) {
        if (downloadListener != null) {
            downloadListener.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (downloadListener != null) {
            if (result != null) {
                downloadListener.onDownloadComplete(result);
            } else {
                downloadListener.onDownloadFailed();
            }
        }
    }

    public interface DownloadListener {
        void onProgressUpdate(int progress);

        void onDownloadComplete(String filePath);

        void onDownloadFailed();

    }
}
