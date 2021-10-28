package kr.ac.ut.smartelevator.sock;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    public SockClient(ExecutorService executorService, Handler handler, HandlerCallback proc) {
        this.executorService = executorService;
        this.handler = handler;
        msgProc = proc;
    }

    public String getErrorDate(byte[] packet, int idx) {
        int pos = idx * SockClient.ERROR_CODE_LENGTH + SockClient.ERROR_CODE_START;

        return String.format("%d-%d-%d %d:%d:%d", packet[pos], packet[pos+1],
                packet[pos+2], packet[pos+3],packet[pos+4], packet[pos+5]);
    }

    public short getErrorCode(byte[] packet, int idx) {
        int pos = idx * SockClient.ERROR_CODE_LENGTH + SockClient.ERROR_CODE_START + 6;
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
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
                byte[] reqData = { (byte) 0xA5, (byte) 0x5A, (byte) 0x06, (byte) 0x00,
                        (byte) 0x21, (byte) 0x46, (byte) 0x81 };
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

                try {
                    client = new Socket(ipaddr, port);

                    while(true) {
                        // Send the request packet.
                        client.getOutputStream().write(reqData);
                        client.getOutputStream().flush();

                        // Receive the error codes from elevator module.
                        readBytes = 0;
                        while(readBytes < 1024) {
                            // 여기는 추가적으로 오류 검사를 할 수 있어야 함.
                            readBytes += client.getInputStream().read(resData);
                        }
                        if(resData[SockClient.ERROR_COUNT] == 0)
                            break;

                        jsonObject.put("lift_id", getElevatorID(resData));

                        for(int j=0; j<resData[SockClient.ERROR_COUNT]; j++) {
                            errData = new JSONObject();
                            errData.put("date", getErrorDate(resData, j));
                            errData.put("event_code", getErrorCode(resData, j));
                            jsonArray.put(errData);
                        }
                    }

                    Message msg = new Message();
                    msg.what = HandlerCallback.ELEVATOR_ERR_CODE;
                    msg.obj = jsonObject;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            msgProc.handleMessage(msg);
                        }
                    });

                } catch (IOException e) {
                    client = null;
                    Log.i("SOCKET", "Socket Proc. error! : " + e);
                } catch (JSONException e) {
                    Log.i("SOCKET", "JSONObject put() error : " + e);
                }

            }
        });


    }

}
