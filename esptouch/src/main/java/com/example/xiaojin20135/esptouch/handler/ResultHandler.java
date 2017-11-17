package com.example.xiaojin20135.esptouch.handler;

import android.os.Handler;
import android.os.Message;

/**
 * Created by xiaojin20135 on 2017-11-17.
 */

public enum ResultHandler {
    RESULT_HANDLER;
    private Handler handler;

    public boolean sendMessage(int what,String msg){
        if(handler != null){
            Message message = new Message();
            message.what = what;
            message.obj = msg;
            return handler.sendMessage(message);
        }else{
            return false;
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
