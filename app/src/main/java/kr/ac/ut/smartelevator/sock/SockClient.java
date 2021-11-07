package kr.ac.ut.smartelevator.sock;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;

import kr.ac.ut.smartelevator.common.HandlerCallback;

public class SockClient {

    private final static int ERROR_CODE_LENGTH = 8;
    private final static int ERROR_CODE_START = 14;
    private final static int ELEVATOR_ID_START = 5;
    private final static int ELEVATOR_ID_LENGTH = 7;
    private final static int ERROR_COUNT = 12;

    private Handler  handler;
    private Socket client;
    private ExecutorService executorService;
    private HandlerCallback msgProc;

    private boolean flag;
    private ConnectivityManager connMgr;
    private Object syncObj;
    private WifiManager wifiManager;

    public SockClient(ExecutorService executorService, Handler handler, HandlerCallback proc, Context context){ // }, ConnectivityManager conn) {
        this.executorService = executorService;
        this.handler = handler;
        msgProc = proc;

       flag = false;
       connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
       syncObj = new Object();
       wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        connMgr.requestNetwork(builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build(),
                new ConnectivityManager.NetworkCallback(){
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        Log.i("ELEVATOR","Wifi Network is available.");
                        Log.i("ELEVATOR", "internet(wifi) connection : " + connMgr.isActiveNetworkMetered());
                    }

                    @Override
                    public void onLost(@NonNull Network network) {
                        super.onLost(network);
                        Log.i("ELEVATOR","Wifi Network is unavailable.");
                        //connMgr.unregisterNetworkCallback(this);
                    }
                });

        NetworkRequest.Builder cbuilder = new NetworkRequest.Builder();
        connMgr.requestNetwork(cbuilder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build(),
                new ConnectivityManager.NetworkCallback(){
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        Log.i("ELEVATOR","Cellular Network is available.");
                        Log.i("ELEVATOR", "internet(cellular) connection : " + connMgr.isActiveNetworkMetered());
                    }

                    @Override
                    public void onLost(@NonNull Network network) {
                        super.onLost(network);
                        Log.i("ELEVATOR","Cellular Network is unavailable.");
                        //connMgr.unregisterNetworkCallback(this);
                    }
                });


    }

    public String getErrorDate(byte[] packet, int idx) {
        int pos = idx * SockClient.ERROR_CODE_LENGTH + SockClient.ERROR_CODE_START;

        return String.format("%d-%d-%d %d:%d:%d", packet[pos], packet[pos+1],
                packet[pos+2], packet[pos+3],packet[pos+4], packet[pos+5]);
    }

    public short getErrorCode(byte[] packet, int idx) {
        int pos = idx * SockClient.ERROR_CODE_LENGTH + SockClient.ERROR_CODE_START + 6;
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(packet[pos]);
        byteBuffer.put(packet[pos+1]);
        return byteBuffer.getShort(0);
    }

    public String getElevatorID(byte[] packet) {
        String id = new String();
        for(int i=SockClient.ELEVATOR_ID_START;
            i<SockClient.ELEVATOR_ID_START+SockClient.ELEVATOR_ID_LENGTH; i++) {
            id += (char)packet[i];
        }
        return id;
    }

    public void getElevatorErrorCode(String ipaddr, int port){

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                Network mobileNetwork = null;
                Socket csocket = null;

                byte[] reqData = { (byte) 0xA5, (byte) 0x5A, (byte) 0x09, (byte) 0x00,
                        (byte) 0x21, (byte)0x18, (byte)0x0B, (byte) 0x0D, (byte) 0x0A };
                byte[] resData = new byte[1024];
                int readBytes;
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                JSONObject errData;
                try {
                    jsonObject.put("lift_err", jsonArray);
                } catch (JSONException e) {
                    Log.i("SOCKET", "JSONObject put() array error : " + e);
                }

                if(!flag) {
                    flag = true;
                    Log.i("ELEVATOR", "WIFI Network is used.");



                }
                else {
                    flag = false;
                    for(Network network : connMgr.getAllNetworks()) {

                        Log.i("ELEVATOR","Network Type : " + connMgr.getNetworkCapabilities(network).toString());

                        if(connMgr.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            Log.i("ELEVATOR","Mobile Network Type : binding process to this network");

                            try {
                                //csocket = network.getSocketFactory().createSocket(ipaddr, port);
                                connMgr.bindProcessToNetwork(network); // for bindProcessToNetwork()
                                Log.i("ELEVATOR","IPADDR : " + ipaddr + "\tPort : " + port);
                                InetSocketAddress inetSocketAddress = new InetSocketAddress(ipaddr, port);
                                csocket = new Socket(ipaddr, port);// for bindProcessToNetwork()
                                // csocket = new Socket();
                                csocket.connect(inetSocketAddress, 10000);

                                Log.i("ELEVATOR","new socket : " + csocket);



                            } catch (IOException e) {
                                Log.i("ELEVATOR","getSocketFactory() Error : " + e.toString());
                                e.printStackTrace();
                                csocket = null;
                            }
                            break;
                        }

                        //NetworkInfo info = connMgr.getNetworkInfo(network);
                        //if(info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        //    Log.i("ELEVATOR","Mobile Netork Type : binding process to this network");

                        //    NetworkRequest.Builder builder = new NetworkRequest.Builder();
                        //    builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);

                            //Log.i("ELEVATOR", "WIFI disabled");
                            //wifiManager.setWifiEnabled(false);

                            //connMgr.requestNetwork(builder.build(), new ConnectivityManager.NetworkCallback(){
                            //    @Override
                            //    public void onAvailable(@NonNull Network network) {
                            //        super.onAvailable(network);
                            //        connMgr.bindProcessToNetwork(network);


                            //        synchronized (syncObj) {
                            //            syncObj.notify();
                            //        }
                            //        connMgr.unregisterNetworkCallback(this);
                            //    }
                            //} );

                            //mobileNetwork = network;
                            //try {
                            //    Log.i("ELEVATOR","Wait until network is available.");
                            //    synchronized (syncObj) {
                            //        syncObj.wait();
                            //    }
                            //    Log.i("ELEVATOR","awaked.");
                            //} catch (InterruptedException e) {
                            //    e.printStackTrace();
                            //}
                            //break;
                       // }
                    }
                }


                try {
                    Log.i("ELEVATOR","Connection request..."  );
                    if(csocket == null) {
                        Log.i("ELEVATOR","Original socket creation.");
                        client = new Socket(ipaddr, port);
                    }
                    else {
                        Log.i("ELEVATOR", "New socket");
                        client = csocket;
                    }

                    client.getOutputStream().write(reqData);
                    client.getOutputStream().flush();

                    while(true) {
                        // Send the request packet.

                        //client.getOutputStream().write(reqData);
                        //client.getOutputStream().flush();

                        // Receive the error codes from elevator module.
                        readBytes = 0;
                        while(readBytes < 1024) {
                            // 여기는 추가적으로 오류 검사를 할 수 있어야 함.
                            readBytes += client.getInputStream().read(resData);
                        }

                        if(resData[SockClient.ERROR_COUNT] == 0) {
                            // 현재는 마지막 패킷은 오류 코드의 수가 125보다 적은 경우
                            // 하지만, 마지막 패킷에 오류 코드가 125개인 경우 마지막인지를 알 수 없음.
                            // 따라서, 이 경우 오류 코드의 수가 0인 패킷을 보낸다고 가정하고 작업.
                            // 향후 협의가 필요함.
                            break;
                        }

                        jsonObject.put("lift_id", getElevatorID(resData));
                        Log.i("ELEVATOR","n of Code : " + resData[SockClient.ERROR_COUNT]);
                        // Log.i("ELEVATOR", "*************************************");
                        for(int j=0; j<resData[SockClient.ERROR_COUNT]; j++) {
                            errData = new JSONObject();
                            errData.put("date", getErrorDate(resData, j));
                            errData.put("event_code", getErrorCode(resData, j));

                            Log.i("ELEVATOR", errData.toString());

                            jsonArray.put(errData);
                        }

                        Log.i("ELELVATOR", "N of Error Data in Array : " + jsonArray.length());

                        if(resData[SockClient.ERROR_COUNT] < 125)
                            break;
                    }
                    if(client != null)
                        client.close();
                    connMgr.bindProcessToNetwork(null);

                    Message msg = new Message();
                    msg.what = HandlerCallback.ELEVATOR_ERR_CODE;
                    msg.obj = jsonObject;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            msgProc.handleMessage(msg);
                        }
                    });

                    Log.i("ELEVATOR","Done - reading error code");

                } catch (IOException e) {
                    client = null;
                    Log.i("ELEVATOR", "Socket Proc. error! : " + e);
                } catch (JSONException e) {
                    Log.i("ELEVATOR", "JSONObject put() error : " + e);
                }

            }
        });
    }

}
