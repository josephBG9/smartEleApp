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

    public SockClient(ExecutorService executorService, Handler handler) {
        this.executorService = executorService;
        this.handler = handler;
    }

    private String getErrorDate(byte[] packet, int idx) {
        int pos = idx * SockClient.ERROR_CODE_LENGTH + SockClient.ERROR_CODE_START;

        /*
            현재 연도는 21년과 같이 전송되지만 데이터베이스에는 2021년과 같이 저장됨.
            따라서, 일단 연도 데이터 앞에 20을 붙임.
         */
        return String.format("20%d-%d-%d %d:%d:%d", packet[pos], packet[pos+1],
                packet[pos+2], packet[pos+3],packet[pos+4], packet[pos+5]);
    }

    private short getErrorCode(byte[] packet, int idx) {
        int pos = idx * SockClient.ERROR_CODE_LENGTH + SockClient.ERROR_CODE_START + 6;
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(packet[pos]);
        byteBuffer.put(packet[pos+1]);

        return byteBuffer.getShort(0);
    }

    private String getElevatorID(byte[] packet) {
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
                // Socket csocket = null;

                byte[] reqData = { (byte) 0xA5, (byte) 0x5A, (byte) 0x09, (byte) 0x00,
                        (byte) 0x21, (byte)0x18, (byte)0x0B, (byte) 0x0D, (byte) 0x0A };
                byte[] resData = new byte[1024];
                int readBytes;
                int recvdBytes;

                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                JSONObject errData;

                try {
                    jsonObject.put("lift_errors", jsonArray);
                } catch (JSONException e) {
                    Log.i("SOCKET", "JSONObject put() array error : " + e);
                }

                try {
                    Log.i("ELEVATOR","Connection request..."  );
                    client = new Socket(ipaddr, port);

                    // Send the request packet.
                    client.getOutputStream().write(reqData);
                    client.getOutputStream().flush();

                    Log.i("ELEVATOR","Request Message : sent."  );

                    while(true) {
                        // Receive the error codes from elevator module.
                        recvdBytes = 0;

                        while(recvdBytes < 1024) {
                            // 여기는 추가적으로 오류 검사를 할 수 있어야 함.
                            Log.i("ELEVATOR", "Waiting for receiving");
                            readBytes = client.getInputStream().read(resData, recvdBytes, 1024 - recvdBytes);
                            recvdBytes += readBytes;

                            Log.i("ELEVATOR","Length of Receive : " + readBytes  );
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

                        for(int j=0; j<resData[SockClient.ERROR_COUNT]; j++) {
                            errData = new JSONObject();
                            errData.put("datetime", getErrorDate(resData, j));
                            errData.put("errCode", getErrorCode(resData, j));
                            jsonArray.put(errData);
                        }

                        if(resData[SockClient.ERROR_COUNT] < 125)
                            break;
                    }
                    if(client != null)
                        client.close();

                    Message msg = new Message();
                    msg.what = HandlerCallback.ELEVATOR_ERR_CODE;
                    msg.obj = jsonObject;

                    handler.sendMessage(msg);

                    Log.i("ELEVATOR","Done - reading error code");

                } catch (IOException e) {
                    client = null;
                    Log.i("ELEVATOR", "Socket Proc. error! : " + e);
                } catch (JSONException e) {
                    Log.i("ELEVATOR", "JSONObject put() error : " + e);
                }

                Log.i("ELEVATOR", "Exit from thread.");
            }
        });
    }

}
