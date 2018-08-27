package com.github.hotbugfix;

import android.app.DownloadManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(TestHot.getBackgroundText());
    }

    public void clickHotfix(View view) {
        try {
            //下载路径，如果路径无效了，可换成你的下载路径
            String url = "http://c.qijingonline.com/test.mkv";
            String path = this.getFilesDir().getAbsolutePath() + File.separator + "patch";

            final long startTime = System.currentTimeMillis();
            Log.i("github","startTime="+startTime);
            //下载函数
            String filename=url.substring(url.lastIndexOf("/") + 1);
            //获取文件名
            URL myURL = new URL(url);
            URLConnection conn = myURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            int fileSize = conn.getContentLength();//根据响应获取文件大小
            if (fileSize <= 0)
                throw new RuntimeException("无法获知文件大小");
            if (is == null)
                throw new RuntimeException("stream为空");
            File file1 = new File(path);
            if(!file1.exists()){
                file1.mkdirs();
            }
            //把数据存入路径+文件名
            String downloadPath = path+File.separator+filename;
            FileOutputStream fos = new FileOutputStream(downloadPath);
            byte buf[] = new byte[1024];
            int downLoadFileSize = 0;
            do{
                //循环读取
                int numread = is.read(buf);
                if (numread == -1)
                {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;
                //更新进度条
            } while (true);

            Log.i("github","download success");
            Log.i("github","totalTime="+ (System.currentTimeMillis() - startTime));
            is.close();

            PatchManager patchManager = new PatchManager(this);
            patchManager.loadPatch(downloadPath);
        } catch (Exception ex) {
            Log.e("github", "error: " + ex.getMessage(), ex);
        }
    }

    public void clickLook(View view) {
        tv.setText(TestHot.getBackgroundText());
    }
}
