package ir.amozkade.hamed.webservicelib;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ir.amozkade.hamed.webservice.Response;
import ir.amozkade.hamed.webservice.StatusCode;
import ir.amozkade.hamed.webservice.WebService;

public class MainActivity extends AppCompatActivity implements
        WebService.OnDownload, WebService.TokenHandler.TokenListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        new WebService()
//                .setUrl("http://192.168.1.5/ApplicationMadiran/API/Madiran/DateTime")
//                .setRequestID(12)
//                .setOnResponseListener(this)
//                .connect();
//        new WebService()
//                .setUrl("http://192.168.1.5/ApplicationMadiran/api/Madiran/CheckUpdateApp")
//                .setRequestID(13)
//                .setOnResponseListener(this)
//                .connect();
//
//        HashMap<String,Object> parameters =new HashMap<>();
//        parameters.put("username","shams");
//        parameters.put("password","1");
//        new WebService()
//                .setUrl("http://192.168.1.5/ApplicationMadiran/api/Madiran/Authentication")
//                .setRequestID(14)
//                .setMethod(WebService.POST)
//                .setParameters(parameters)
//                .setOnResponseListener(this)
//                .connect();
//
//
//        //file downloader
//        new WebService()
//                .setUrl(" http://192.168.1.5/ApplicationMadiran/apk/app.apk")
//                .setRequestID(15)
//                .setFilepath(Environment.getExternalStorageDirectory()+"/test.apk")
//                .setHandler(new Handler())
//                .setOnDownloadListener(this)
//                .downloader();
//
//        //string downloader
//        new WebService()
//                .setUrl(" http://192.168.1.5/ApplicationMadiran/apk/app.apk")
//                .setRequestID(15)
//                .setFilepath(Environment.getExternalStorageDirectory()+"/test.apk")
//                .setHandler(new Handler())
//                .setOnDownloadListener(this)
//                .downloader();

//        User user = new User();
//        user.setUserName("hamed8080");
//        user.setUserPass("hamedgmail");
//        String jwt = new TokenHandler(this).getJwt();
//        String jsonobject = "";
//        try {
//            jsonobject = new ObjectMapper().writeValueAsString(user);
//            new TokenHandler(this).saveCredential("http://192.168.1.7:9090/login", jsonobject, jwt);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        new WebService()
                .setJwt(new WebService.TokenHandler(this).getJwt())
                .setMethod(WebService.GET)
                .setUrl("http://192.168.1.7:9090/Topic/Popular")
                .setOnResponseListener(new WebService.OnResponse() {
                    @Override
                    public void response(Response response, Integer id) {
                        if (response.getResponseCode() == StatusCode.UNAUTHORIZED) {
                            //new TokenHandler(MainActivity.this).getNewToken();
                            new WebService.TokenHandler(MainActivity.this).getNewToken(MainActivity.this, id);
                        }
                    }
                }).connect();

    }


    @Override
    public void download(int percent, float downloadedSize, float fileSize, boolean downloadComplete, boolean fail) {
        if (percent != 100) {

        }
    }


    @Override
    public void onArrive(String jwt, Integer id) {

    }

    @Override
    public void onFail(String message, int statusCode, Integer id) {
        //if fail we must be intent to loginActivity
    }
}
