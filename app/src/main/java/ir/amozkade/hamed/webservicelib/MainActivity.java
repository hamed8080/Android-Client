package ir.amozkade.hamed.webservicelib;

import android.media.audiofx.EnvironmentalReverb;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;

import ir.amozkade.hamed.webservice.Response;
import ir.amozkade.hamed.webservice.StatusCode;
import ir.amozkade.hamed.webservice.WebService;

public class MainActivity extends AppCompatActivity  implements WebService.OnResponse,
        WebService.OnDownload {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new WebService()
                .setUrl("http://192.168.1.5/ApplicationMadiran/API/Madiran/DateTime")
                .setRequestID(12)
                .setOnResponseListener(this)
                .connect();
        new WebService()
                .setUrl("http://192.168.1.5/ApplicationMadiran/api/Madiran/CheckUpdateApp")
                .setRequestID(13)
                .setOnResponseListener(this)
                .connect();

        HashMap<String,Object> parameters =new HashMap<>();
        parameters.put("username","shams");
        parameters.put("password","1");
        new WebService()
                .setUrl("http://192.168.1.5/ApplicationMadiran/api/Madiran/Authentication")
                .setRequestID(14)
                .setMethod(WebService.POST)
                .setParameters(parameters)
                .setOnResponseListener(this)
                .connect();


        //file downloader
        new WebService()
                .setUrl(" http://192.168.1.5/ApplicationMadiran/apk/app.apk")
                .setRequestID(15)
                .setFilepath(Environment.getExternalStorageDirectory()+"/test.apk")
                .setHandler(new Handler())
                .setOnDownloadListener(this)
                .downloader();

        //string downloader
        new WebService()
                .setUrl(" http://192.168.1.5/ApplicationMadiran/apk/app.apk")
                .setRequestID(15)
                .setFilepath(Environment.getExternalStorageDirectory()+"/test.apk")
                .setHandler(new Handler())
                .setOnDownloadListener(this)
                .downloader();
    }


    @Override
    public void response(Response response, Integer id) {
        if (response.responseCode == StatusCode.OK) {
            switch (id) {
            }
        }
    }

    @Override
    public void download(int percent, float downloadedSize, float fileSize, boolean downloadComplete, boolean fail) {
        if(percent!=100){

        }
    }
}
