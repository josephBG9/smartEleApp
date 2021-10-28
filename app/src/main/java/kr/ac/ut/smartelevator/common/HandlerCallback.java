package kr.ac.ut.smartelevator.common;

import android.os.Message;

public interface HandlerCallback {

    public static final int HTTP_ERROR = -1;
    public static final int GET_OK = 0;
    public static final int PUT_OK = 1;

    void handleMessage(Message msg);
}
