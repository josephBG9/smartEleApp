package kr.ac.ut.smartelevator.sock;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;

public class SockClient {
    private final static int ERROR_CODE_LENGTH = 8;
    private final static int ERROR_CODE_START = 14;
    private final static int ELEVATOR_ID_START = 5;
    private final static int ERROR_COUNT = 12;

    private Socket client;
    private ExecutorService executorService;
    private JSONObject jsonObject;

    public SockClient(String ipaddr, int port, ExecutorService executorService) {
        try {
            client = new Socket(ipaddr, port);
            jsonObject = null;

        } catch (IOException e) {
            client = null;
            Log.i("SOCKET", "Socket creation error!");
        }
    }

    public JSONObject getElevatorErrorCode(){
        return jsonObject;
    }
}
