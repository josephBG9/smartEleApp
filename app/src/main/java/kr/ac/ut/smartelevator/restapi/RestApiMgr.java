package kr.ac.ut.smartelevator.restapi;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.ac.ut.smartelevator.common.HandlerCallback;

public class RestApiMgr {
    public static final int N_THREADS = 4;

    private ExecutorService executorService;
    private Handler handler;
    private String urlBase;
    private HandlerCallback target;

    public RestApiMgr(Handler handler, String urlBase, HandlerCallback target) {
        this.handler = handler;
        this.urlBase = urlBase;
        executorService = Executors.newFixedThreadPool(RestApiMgr.N_THREADS);

        this.target = target;
    }

    public RestApiMgr(Handler handler, String urlBase) {
        this.handler = handler;
        this.urlBase = urlBase;
        executorService = Executors.newFixedThreadPool(RestApiMgr.N_THREADS);

       // this.target = target;
    }


    public void getFromApiServer(String method, String urlFile) {
        executorService.execute(new ApiServerMgr(method, urlBase+urlFile, handler, null ));
    }

    public void getFromApiServer(String urlFile) {
        Log.i("API", urlBase + urlFile);
        getFromApiServer("GET", urlFile);
    }

    public void putToApiServer(String method, String urlFile, JSONObject data) {
        executorService.execute(new ApiServerMgr(method, urlBase+urlFile, handler, data ));
    }

    public void putToApiServer(String urlFile, JSONObject data) {
        putToApiServer("PUT", urlFile, data);
    }

    private class ApiServerMgr implements Runnable {
        private String requestMethod;
        private String urlStr;
        private Handler handler;
        private JSONObject data;

        public ApiServerMgr(String method, String urlStr, Handler handler,  JSONObject data ) {
            requestMethod = method;
            this.urlStr = urlStr;
            this.handler = handler;
            this.data = data;
        }

        @Override
        public void run() {
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;
            StringBuffer buffer = new StringBuffer();
            String str;
            JSONArray array;

            try {
                Log.i("API","URL : " + urlStr + " : METHOD : " + requestMethod);
                URL url = new URL(urlStr);
                HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
                Message msg;

                Log.i("API", "URL String : " + urlStr);

                httpConnection.setDefaultUseCaches(false);
                httpConnection.setRequestMethod(requestMethod);
                httpConnection.setDoInput(true);
                httpConnection.setConnectTimeout(1000);
                if(!requestMethod.equals("GET")) {
                    httpConnection.setDoOutput(true);
                    httpConnection.setRequestProperty("content-type", "application/json");

                    // httpConnection.connect();
                    bufferedWriter = new BufferedWriter(
                            new OutputStreamWriter(httpConnection.getOutputStream()));
                    bufferedWriter.write(data.toString());
                    bufferedWriter.flush();
                }

                msg = new Message();

                if(httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    if(requestMethod.equals("GET")) {
                        bufferedReader = new BufferedReader(
                                new InputStreamReader(httpConnection.getInputStream()));
                        while ((str = bufferedReader.readLine()) != null) {
                            buffer.append(str);
                        }

                        httpConnection.disconnect();

                        if (buffer.toString().startsWith("{")) {
                            buffer.insert(0, "[");
                            buffer.append("]");
                        }

                        array = new JSONArray(buffer.toString());
                        msg.what = HandlerCallback.GET_OK;
                        msg.obj = array;
                    }
                    else {
                        msg.what = HandlerCallback.PUT_OK;
                    }
                }
                else {
                    msg.what = HandlerCallback.HTTP_ERROR;
                }
                handler.sendMessage(msg);
                //handler.post(new Runnable() {
                //    @Override
                //    public void run() {
                //        target.handleMessage(msg);
                //    }
                //});

            } catch (MalformedURLException e) {
                Log.i("API : ", "URL(" + urlStr + ") is not valied.");
            } catch (IOException e) {
                Log.i("API : ", "IO error to/from Api Server.");
                Log.i("API : ", e.toString());
            } catch (JSONException e) {
                Log.i("API : ", "JSONArray Conversion Error.!!");
            }

        }
    }

}
