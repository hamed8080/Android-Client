package ir.amozkade.hamed.webservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Created By Hamed Hosseini
 */
public class WebService {




    public interface OnResponse {
        void response(Response response, Integer id);
    }

    public interface OnResponseStringDownload {
        void response(String response, int progress, boolean completed);
    }

    public interface OnDownload {
        void download(int percent, float downloadedSize, float fileSize, boolean downloadComplete, boolean fail);
    }

    public interface OnTimeOutException {
        public void timeOut(String timeOutMessage);
    }

    private Integer timeOut = 15000;
    private Integer connectTimeout = 15000;
    private Integer requestID = null;
    private String method = "GET";
    HashMap<String, Object> parameters;
    private String jsonObject;
    private String jsonArray;
    private String url;
    OnResponse onResponse = null;
    Response response = null;
    private String charset = "UTF-8";
    private String contentType = "application/json; charset=utf-8";
    private String authorization = null;
    private String message;
    private int responseCode;
    private String jwt;
    private boolean isToken=false;

    /**
     * @since for Downloader
     */
    private String filepath;
    private OnDownload onDownloadListener;
    private Handler handler;
    private OnResponseStringDownload stringDownloadListener;
    String responseString;


    public static String GET = "GET";
    public static String POST = "POST";
    public static String PUT = "PUT";
    public static String DELETE = "DELETE";


    /**
     * @param timeOut in millisecond int value
     * @since when data is very large and may long time to download data this time means that
     */
    public WebService setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
        return this;
    }


    /**
     * @param connectTimeout in millisecond int value
     * @since when android client app cant connect to server in this connect timeout
     */
    public WebService setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * @param requestID int request id
     * @since when request back response we must find witch request create this to response that
     */
    public WebService setRequestID(Integer requestID) {
        this.requestID = requestID;
        return this;
    }

    /**
     * @param jwt token to authorize request
     */
    public WebService setJwt(String jwt) {
        this.jwt = jwt;
        return this;
    }

    public WebService getToken(boolean b) {
        this.isToken= true;
        return this;
    }
    /**
     * @param method GET,POST,DELETE ,...
     * @since use WebService static fields Like 'WebService.POST'
     */
    public WebService setMethod(String method) {
        this.method = method;
        return this;
    }

    /**
     * @param url loginUrl to send request
     * @since this must be HTTP OR HTTPS request loginUrl
     */
    public WebService setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * @param response implement on class or create inner class
     * @since use When response arrive from server
     */
    public WebService setOnResponseListener(OnResponse response) {
        this.onResponse = response;
        return this;
    }

    /**
     * @param parameters HashMap of <String , Object></> String is key and Object is Value
     * @since send data to server
     */
    public WebService setParameters(HashMap<String, Object> parameters) {
        this.parameters = parameters;
        return this;
    }

    /**
     * @param jsonObject String is JsonObject
     * @since send data to server
     */
    public WebService setJsonObjectString(String jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }

    /**
     * @param jsonArray String is JsonArray
     * @since send data to server
     */
    public WebService setJsonArrayString(String jsonArray) {
        this.jsonArray = jsonArray;
        return this;
    }


    /**
     * @param charset String value
     * @since charset of data send to server
     */
    public WebService setCharset(String charset) {
        this.charset = charset;
        return this;
    }


    /**
     * @param contentType String value
     * @since content type of request JSON,XML,PLAIN,...
     */
    public WebService setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }




    public WebService setAuthorization(String userName, String password) {
        this.authorization = Base64.encodeToString((userName + ":" + password).getBytes(), Base64.DEFAULT);
        return this;
    }

    public WebService setFilepath(String filepath) {
        this.filepath = filepath;
        return this;
    }

    public WebService setOnDownloadListener(OnDownload onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
        return this;
    }

    public WebService setHandler(Handler handler) {
        this.handler = handler;
        return this;
    }

    public WebService setStringDownloadListener(OnResponseStringDownload stringDownloadListener) {
        this.stringDownloadListener = stringDownloadListener;
        return this;
    }


    public void connect() {
        new ConnectTask().execute();
    }

    public void downloadString() {
        new StringDownloaderTask().execute();
    }

    public class ConnectTask extends AsyncTask<Object, Void, Object> {
        @Override
        protected Object doInBackground(Object[] params) {
            response = readUrl();
            return response;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            onResponse.response((Response) result, requestID);
        }
    }

    public class StringDownloaderTask extends AsyncTask<Object, Void, Object> {
        @Override
        protected Object doInBackground(Object[] params) {
            stringDownloaderWithProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
        }
    }


    private Response readUrl() {

        URL url;
        StringBuilder response = new StringBuilder();
        try {
            if (method.equals(WebService.GET) && parameters != null && parameters.size() > 0) {
                this.url += convertParamsToQueryStringGetMethod(parameters);
            }
            url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setReadTimeout(timeOut);
            connection.setConnectTimeout(connectTimeout);
            connection.setDoInput(true);
            if (jwt != null) {
                connection.setRequestProperty("Authorization", "Bearer " + jwt);
            }
            if (!method.equals(WebService.GET)) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", contentType);
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, charset), 30);
                if (parameters != null && (jsonArray != null || jsonObject != null) || (jsonArray != null && jsonObject != null)) {
                    throw new Exception("cant add parameter HashMap and JsonObject or JsonArray together");
                }
                String sendParam = "";
                if (parameters != null) {
                    sendParam = (ConvertHashToJsonArray(parameters, false));
                } else if (jsonObject != null) {
                    sendParam = jsonObject;
                } else if (jsonArray != null) {
                    sendParam = jsonArray;
                }
                writer.write(sendParam);
                writer.flush();
                writer.close();
                os.close();
            }

            responseCode = connection.getResponseCode();
            if (responseCode == StatusCode.UNAUTHORIZED || responseCode == StatusCode.Forbidden) {
                Response resNotAuthorize = new Response();
                resNotAuthorize.setResponseCode(StatusCode.UNAUTHORIZED);
                return resNotAuthorize;
            }
            if(isToken){
                Response resToken = new Response();
                String jwt =  connection.getHeaderField("Authorization").replace("Bearer ","");
                resToken.setMessage(jwt);
                resToken.setResponseCode(StatusCode.OK);
                return resToken;
            }
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            return new ObjectMapper().readValue(response.toString(),Response.class);
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return new Response()
                    .setMessage("زمان اتصال به سرور طولانی شد!")
                    .setData(null)
                    .setResponseCode(StatusCode.REQUEST_ERROR);
        } catch (JSONException e) {
            e.printStackTrace();
            return new Response()
                    .setMessage("مشکل در ارسال درخواست")
                    .setData(null)
                    .setResponseCode(StatusCode.REQUEST_ERROR);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new Response()
                    .setMessage("مشکل در ارسال درخواست")
                    .setData(null)
                    .setResponseCode(StatusCode.REQUEST_ERROR);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (message == null) {
                message = "مشکل در احراز هویت";
            }
            return new Response()
                    .setMessage(WebService.this.message)
                    .setData(null)
                    .setResponseCode(WebService.this.responseCode);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response()
                    .setMessage("مشکل در ارسال درخواست")
                    .setData(null)
                    .setResponseCode(StatusCode.REQUEST_ERROR);
        }
    }

    public static String convertParamsToQueryStringGetMethod(HashMap<String, Object> params) {
        String query = "?";
        int counter = 0;
        for (Map.Entry<String, Object> pair : params.entrySet()) {
            query += pair.getKey() + "=" + pair.getValue().toString().replace(" ", "%20");
            if (params.size() - 1 != counter) {
                query += "&";
            }
            counter++;
        }
        return query;
    }

    private static String ConvertHashToJsonArray(HashMap<String, Object> map, boolean isArray) {
        String jsonString;
        if (isArray) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(new JSONObject(map));
            jsonString = jsonArray.toString();
        } else {
            JSONObject jsonObject = new JSONObject(map);
            jsonString = jsonObject.toString();
        }


        return ((jsonString.replace("\\\"", "\"")).replace("\"[", "[")).replace("]\"", "]");
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public void downloader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(WebService.this.url);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(method);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Authorization", "Basic " + authorization);
                    connection.connect();
                    int content_size;
                    if (connection.getHeaderField("content-size") != null) {
                        content_size = Integer.valueOf(connection.getHeaderField("content-size"));
                    } else if (connection.getHeaderField("Content-Length") != null) {
                        content_size = Integer.valueOf(connection.getHeaderField("Content-Length"));
                    } else {
                        onDownloadListener.download(100, 100, 0, true, true);
                        return;
                    }
                    final int fileSize = content_size;
                    final File file = new File(filepath);
                    if (file.exists() && file.length() == fileSize) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onDownloadListener.download(100, 100, fileSize, true, false);
                            }
                        });
                        return;
                    }
                    file.delete();
                    FileOutputStream outputStream = null;
                    outputStream = new FileOutputStream(filepath);
                    InputStream inputStream = null;
                    inputStream = connection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    int downloadedSize = 0;
                    while ((len = inputStream.read(buffer)) >= 0) {//this must >=0 because inputstream ended with index 0
                        assert outputStream != null;
                        outputStream.write(buffer, 0, len);
                        downloadedSize += len;
                        final int finalDownloadedSize = downloadedSize;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                int percent = (int) Math.ceil(100 * finalDownloadedSize / fileSize);
                                if (onDownloadListener != null) {
                                    onDownloadListener.download(percent, finalDownloadedSize, fileSize, false, false);
                                }
                            }
                        });
                    }
                    outputStream.flush();
                    outputStream.close();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onDownloadListener.download(100, 100, fileSize, true, false);
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

    private void stringDownloaderWithProgress() {

        try {
            if (method.equals(WebService.GET) && parameters != null) {
                url += convertParamsToQueryStringGetMethod(parameters);
            }
            URL url = new URL(WebService.this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setReadTimeout(timeOut);
            connection.setConnectTimeout(connectTimeout);
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "Basic " + authorization);
            if (!method.equals(WebService.GET)) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", contentType);
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, charset));
                if (parameters != null && (jsonArray != null || jsonObject != null) || (jsonArray != null && jsonObject != null)) {
                    throw new Exception("cant add parameter HashMap and JsonObject or JsonArray together");
                }
                String sendParam = "";
                if (parameters != null) {
                    sendParam = (ConvertHashToJsonArray(parameters, false));
                } else if (jsonObject != null) {
                    sendParam = jsonObject;
                } else if (jsonArray != null) {
                    sendParam = jsonArray;
                }
                writer.write(sendParam);
                writer.flush();
                writer.close();
                os.close();
            }
            int responseCode = connection.getResponseCode();
            int contentLength = connection.getContentLength();
            if (responseCode == StatusCode.OK) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(contentLength);
                int len;
                int downloadedSize = 0;
                InputStream in = connection.getInputStream();
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) > 0) {
                    downloadedSize += len;
                    // onResponse += line;
                    outputStream.write(buffer, 0, len);
                    final int percent = (int) Math.ceil(100 * downloadedSize / contentLength);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            stringDownloadListener.response(responseString, percent, false);
                        }
                    });
                }
                outputStream.flush();
                outputStream.close();

                responseString = outputStream.toString();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        stringDownloadListener.response(responseString, 100, true);
                    }
                });
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class TokenHandler {
        private String method = WebService.POST;
        private Context context;
        private SharedPreferences preferences;
        private TokenListener tokenListener;
        private Integer id;

        public TokenHandler(Context context) {
            this.context = context;
            preferences = context.getSharedPreferences("TokenHandler", Context.MODE_PRIVATE);
        }

        public interface TokenListener {
            void onArrive(String jwt, Integer id);

            void onFail(String message, int statusCode, Integer id);
        }

        public void saveCredential(String loginUrl, String cred, String jwt) {
            preferences.edit()
                    .putString("loginUrl", loginUrl)
                    .putString("cred", cred)
                    .putString("jwt", jwt)
                    .apply();
        }

        public void getNewToken() {
            connect();
        }

        public void getNewToken(TokenListener tokenListener,Integer id) {
            this.tokenListener = tokenListener;
            this.id = id;
        }

        public void connect() {
            String loginUrl = preferences.getString("loginUrl", null);
            String cred = preferences.getString("cred", null);
            if (loginUrl == null) {
                try {
                    throw new Exception("url must be set!");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (cred == null) {
                try {
                    throw new Exception("cred must be set!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            new WebService()
                    .setUrl(loginUrl)
                    .setMethod(WebService.POST)
                    .getToken(true)
                    .setJsonObjectString(cred)
                    .setOnResponseListener(new WebService.OnResponse() {
                        @Override
                        public void response(final Response response, Integer id) {
                            if (response.getResponseCode() == StatusCode.OK) {
                                preferences.edit().putString("jwt", response.getMessage()).apply();//only jwt token inside message filed
                                if (tokenListener != null) {
                                    tokenListener.onArrive(response.getMessage(),TokenHandler.this.id);
                                }
                            }
                            if (response.getResponseCode() == StatusCode.UNAUTHORIZED) {
                                if (tokenListener != null) {
                                    tokenListener.onFail(response.getMessage(), StatusCode.UNAUTHORIZED,TokenHandler.this.id);
                                }
                            }
                        }
                    }).connect();
        }

        public String getJwt() {
            return preferences.getString("jwt", null);
        }
    }
    public static void callTokenHandler(WebService.TokenHandler.TokenListener tokenListener,Context context, Integer id) {
        WebService.TokenHandler tokenHandler = new WebService.TokenHandler(context);
        tokenHandler.getNewToken(tokenListener, id);
        tokenHandler.connect();
    }
}
