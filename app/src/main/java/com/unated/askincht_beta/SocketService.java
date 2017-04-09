package com.unated.askincht_beta;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.unated.askincht_beta.Pojo.BusMessages.BadMessage;
import com.unated.askincht_beta.Pojo.BusMessages.CloseMessage;
import com.unated.askincht_beta.Pojo.BusMessages.ConnectMessage;
import com.unated.askincht_beta.Pojo.BusMessages.CounterMessage;
import com.unated.askincht_beta.Pojo.BusMessages.DisconnectMessage;
import com.unated.askincht_beta.Pojo.BusMessages.IncreaseRadius;
import com.unated.askincht_beta.Pojo.BusMessages.InvalidTokenMsg;
import com.unated.askincht_beta.Pojo.BusMessages.InviteMessage;
import com.unated.askincht_beta.Pojo.BusMessages.ListenWritingMessage;
import com.unated.askincht_beta.Pojo.BusMessages.MessageItem;
import com.unated.askincht_beta.Pojo.BusMessages.MsgPnd;
import com.unated.askincht_beta.Pojo.BusMessages.NewInviteMessage;
import com.unated.askincht_beta.Pojo.BusMessages.NotServedMessage;
import com.unated.askincht_beta.Pojo.BusMessages.NothingMessage;
import com.unated.askincht_beta.Pojo.BusMessages.NotificationMessage;
import com.unated.askincht_beta.Pojo.BusMessages.PayToCall;
import com.unated.askincht_beta.Pojo.BusMessages.PostListenWritingMessage;
import com.unated.askincht_beta.Pojo.BusMessages.ReadedMessage;
import com.unated.askincht_beta.Pojo.BusMessages.RequestCounter;
import com.unated.askincht_beta.Pojo.BusMessages.SearchMessage;
import com.unated.askincht_beta.Pojo.BusMessages.SendMessage;
import com.unated.askincht_beta.Pojo.BusMessages.SingleMessage;
import com.unated.askincht_beta.Pojo.BusMessages.WritingMessage;
import com.unated.askincht_beta.Pojo.BusMessages.rId;
import com.unated.askincht_beta.Pojo.MyRequestItem;
import com.unated.askincht_beta.Pojo.MyShopResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.RequestItem;
import com.unated.askincht_beta.Utils.Constants;
import com.unated.askincht_beta.Utils.SharedStore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SocketService extends Service {

    private String TAG = "SocketService";

    private static Socket mSocket;
    boolean notConnected = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            IO.Options options = new IO.Options();
            options.reconnection = true;
            options.reconnectionAttempts = 10;
            options.reconnectionDelay = 5000;
            options.timeout = -1;
            mSocket = IO.socket(Constants.API.SOCKET_URL, options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().register(this);
    }


    private void tryReconnect() throws URISyntaxException, InterruptedException {
        for (int i = 0; i < 5 && notConnected; i++) {
            Log.d(TAG, "TRY TO RECONNECT... ATTEMPT: " + i);
            if (mSocket != null) {
                mSocket.disconnect();
                mSocket = null;
                mSocket = IO.socket(Constants.API.SOCKET_URL);
                Thread.sleep(10000);
                mSocket.connect();
            }
        }
    }

    @Subscribe
    public void disconnect(DisconnectMessage disconnect) {
        if (mSocket != null) {
            mSocket.disconnect();
            try {
                IO.Options options = new IO.Options();
                options.reconnection = true;
                options.reconnectionAttempts = 10;
                options.reconnectionDelay = 5000;
                options.timeout = -1;
                mSocket = IO.socket(Constants.API.SOCKET_URL, options);
            } catch (URISyntaxException e) {
e.printStackTrace();

            }
        }
    }

    @Subscribe
    public void connectToServer(ConnectMessage connect) {
        if (mSocket != null && !mSocket.connected()) {
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnectListener);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on("newmessage", onNewMessageListener);//
            mSocket.on("close_request", onCloseRequest);//
            mSocket.on("single_word", onSingle);//
            mSocket.on("invalid_token", onInvalidToken);//
            mSocket.on("new_invite_to_chat", onInviteListner);//
            mSocket.on("new_request", onNewRequestListener);
            mSocket.on("message_pending", onMessagePending);

            mSocket.on("search_results", onSearchResultsListener);
            mSocket.on("cat_not_found", onCat);
            mSocket.on("badword_detected", onBad);
            mSocket.on("city_not_served", onNotServed);
            mSocket.on("shop_not_found", onShop);

            mSocket.on("writing", onListenWriting);
            mSocket.on("readed", onReadListener);
        }
    }

    private Emitter.Listener onConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "CONNECTED");
            try {
                subscribeToChannel();
            } catch (JSONException e) {
                if (e.getMessage().equals("jwt expired")) {
                    if (!AppMain.isRefresh) {
                        AppMain.isRefresh = true;
                        Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(), SharedStore.getInstance().getToken());
                        callr.enqueue(new Callback<RefreshResponse>() {
                            @Override
                            public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                                if (response.isSuccessful() && response.body().getStatus() == 0) {
                                    SharedStore.getInstance().setToken(response.body().getData().getToken());
                                    try {
                                        tryReconnect();
                                    } catch (URISyntaxException e1) {
                                        e1.printStackTrace();
                                    } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                    }
                                    AppMain.isRefresh = false;


                                }
                            }

                            @Override
                            public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                            }
                        });
                    }
                }
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "ERROR");
            disconnect(new DisconnectMessage());
            connectToServer(new ConnectMessage());
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "DISCONNECTED");
        }
    };

    //newmessage
    private Emitter.Listener onNewMessageListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "onNewMessageListener" + args[0].toString());
            MessageItem messageItem = new Gson().fromJson(((JSONObject) args[0]).toString(), MessageItem.class);
            EventBus.getDefault().post(messageItem);
            updateCounters(messageItem.getShop_id());
            EventBus.getDefault().post(new NotificationMessage(messageItem.getText(), false));
            EventBus.getDefault().post(new CounterMessage(true));
            Log.i(TAG, SharedStore.getInstance().getUserId() + " " + messageItem.getUserId());
           if (!SharedStore.getInstance().getUserId().equals(messageItem.getUserId())&&!isApplicationBroughtToBackground()) {

               try {
                   Log.i(TAG,"work");
                   AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                   Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                   final MediaPlayer mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.newmsg);

                   Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

                   switch( am.getRingerMode() ){
                       case AudioManager.RINGER_MODE_NORMAL:
                           if(AppMain.inChat) {
                               vibrator.vibrate(500);
                               mediaPlayer.start();
                           }
                           else{
                               Ringtone r = RingtoneManager.getRingtone(getBaseContext(), notification);
                               vibrator.vibrate(500);
                               r.play();
                           }
                           break;
                       case AudioManager.RINGER_MODE_SILENT:
                           break;
                       case AudioManager.RINGER_MODE_VIBRATE:

                           vibrator.vibrate(500);

                           break;

                   }

               } catch (Exception e) {
                   Log.d(TAG, String.valueOf(e));

               }
            }
        }
    };

    //new_request
    private Emitter.Listener onNewRequestListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "onNewRequestListener" + args[0].toString());
            RequestItem requestItem = new Gson().fromJson(((JSONObject) args[0]).toString(), RequestItem.class);
            updateCounters(requestItem.getShop_id());
            EventBus.getDefault().post(new NotificationMessage(requestItem.getText(), true));
            EventBus.getDefault().post(new CounterMessage(true));
           /* try {
                AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

                switch( am.getRingerMode() ){
                    case AudioManager.RINGER_MODE_NORMAL:
                        Ringtone r = RingtoneManager.getRingtone(getBaseContext(), notification);
                        vibrator.vibrate(500);
                        r.play();
                        break;
                    case AudioManager.RINGER_MODE_SILENT:
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:

                        vibrator.vibrate(500);

                        break;

                }

            } catch (Exception e) {
                Log.d(TAG, String.valueOf(e));

            }*/

        }
    };
    private Emitter.Listener onInvalidToken= new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "invalid_token" + args[0].toString());
            Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
            callr.enqueue(new Callback<RefreshResponse>() {
                @Override
                public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                    if (response.isSuccessful() && response.body().getStatus() == 0) {
                        SharedStore.getInstance().setToken(response.body().getData().getToken());
                        EventBus.getDefault().post(new InvalidTokenMsg());
                        try {
                            tryReconnect();
                        } catch (URISyntaxException e) {
                            Log.i(TAG, String.valueOf(e));
                        } catch (InterruptedException e) {
                            Log.i(TAG, String.valueOf(e));
                        }

                    }
                }

                @Override
                public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                }
            });
           /* try {
                AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

                switch( am.getRingerMode() ){
                    case AudioManager.RINGER_MODE_NORMAL:
                        Ringtone r = RingtoneManager.getRingtone(getBaseContext(), notification);
                        vibrator.vibrate(500);
                        r.play();
                        break;
                    case AudioManager.RINGER_MODE_SILENT:
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:

                        vibrator.vibrate(500);

                        break;

                }

            } catch (Exception e) {
                Log.d(TAG, String.valueOf(e));

            }*/

        }
    };

    //search_results
    private Emitter.Listener onSearchResultsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "onSearchResultsListener ");

            Log.d(TAG, "onSearchResultsListener " + args[0].toString());
            MyRequestItem myRequestItem = new Gson().fromJson(((JSONObject) args[0]).toString(), MyRequestItem.class);
            EventBus.getDefault().post(myRequestItem);
        }
    };
    private Emitter.Listener onCloseRequest = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "onCloseRequest ");
            try {
                JSONObject jsonObject = new JSONObject(args[0].toString());
                Log.d(TAG, "onCloseRequest" + args[0].toString());
                EventBus.getDefault().post(new CloseMessage( jsonObject.optString("shop_id"), jsonObject.optString("rating")));
            } catch (JSONException e) {
                e.printStackTrace();
            }




        }
    };
    private Emitter.Listener onInviteListner = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Log.d(TAG, "new_invite_to_chat " + args[0].toString());
            NewInviteMessage nim=null;
            try {
                JSONObject jsonObject = new JSONObject(args[0].toString());
                nim= new NewInviteMessage(jsonObject.optString("request_id"), jsonObject.optString("shop_id"), jsonObject.optString("guest_user_id")
                        , jsonObject.optString("user_name") , jsonObject.optString("chat_name") , jsonObject.optString("chat_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            EventBus.getDefault().post(nim);
        }
    };

    //listen_writing
    private Emitter.Listener onListenWriting = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "onListenWriting" + args[0].toString());
            ListenWritingMessage writingMessage = null;
            try {
                JSONObject jsonObject = new JSONObject(args[0].toString());
                writingMessage = new ListenWritingMessage(jsonObject.optString("request_id"), jsonObject.optString("shop_id"));
            } catch (JSONException e) {
                Log.i(TAG, String.valueOf(e))   ;
            }
            EventBus.getDefault().post(writingMessage);
        }
    };

    //readed
    private Emitter.Listener onReadListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "onReadListener");
            EventBus.getDefault().post("readed");
        }
    };
    private Emitter.Listener onSingle = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "single_word");
            SingleMessage singleMessage=null;
            try {
                JSONObject jsonObject = new JSONObject(args[0].toString());
                singleMessage = new SingleMessage(jsonObject.optString("request_id"), jsonObject.optString("message"));
            } catch (JSONException e) {
                Log.i(TAG, String.valueOf(e))   ;
            }
            EventBus.getDefault().post(singleMessage);
        }
    };

    private Emitter.Listener onCat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "cat_not_found");
            EventBus.getDefault().post(new NothingMessage());

        }
    };
    private Emitter.Listener onNotServed = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            Log.d(TAG, "city_not_served" + args[0].toString());

            NotServedMessage notServedMessage=null;
            try {
                JSONObject jsonObject = new JSONObject(args[0].toString());
                notServedMessage = new NotServedMessage(jsonObject.optString("message"),jsonObject.optString("city"));
            } catch (JSONException e) {
                Log.i(TAG, String.valueOf(e))   ;
            }
            EventBus.getDefault().post(notServedMessage);

        }
    };
    private Emitter.Listener onBad = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "badword_detected");
            EventBus.getDefault().post(new BadMessage());

        }
    };

    private Emitter.Listener onShop = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "shop_not_found");
            EventBus.getDefault().post(new NothingMessage());

        }
    };
    private Emitter.Listener onMessagePending = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "message_pending");
            EventBus.getDefault().post(new MsgPnd());

        }
    };

    private void subscribeToChannel() throws JSONException {
        JSONObject subsctibeObject = new JSONObject();
        subsctibeObject.accumulate("userid", SharedStore.getInstance().getUserId());
        subsctibeObject.accumulate("token", SharedStore.getInstance().getToken());
        subsctibeObject.accumulate("sid", SharedStore.getInstance().getSID());
      subsctibeObject.accumulate("last_message", SharedStore.getInstance().getLastMessageId());
        subsctibeObject.accumulate("last_request", SharedStore.getInstance().getLastRequestId());
        Log.d(TAG, "Subscribe: userid = " + SharedStore.getInstance().getUserId() + "\ntoken = " + SharedStore.getInstance().getToken()+ "\nsid = " + SharedStore.getInstance().getSID()
                + "\nlast_message = " + SharedStore.getInstance().getLastMessageId() + "\nlast_request = " + SharedStore.getInstance().getLastRequestId() + "\nDEVICE UUID = " + SharedStore.getInstance().getUUID());
        mSocket.emit("subscribe", subsctibeObject);


       /* mSocket.on("new_requests", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "new_requests ");

                Log.d(TAG, "new_requests " + args[0].toString());
              *//*  MyRequestItem myRequestItem = new Gson().fromJson(((JSONObject) args[0]).toString(), MyRequestItem.class);
                EventBus.getDefault().post(myRequestItem);   *//*             }
        });*/
    }

    @Subscribe
    public void onReaded(ReadedMessage readedMessage) {
        Log.d(TAG, "onReaded");

        if (!mSocket.connected())
            connectToServer(new ConnectMessage());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("request_id", readedMessage.getReqId());
            jsonObject.accumulate("shop_id", readedMessage.getShopId());
            mSocket.emit("readed", jsonObject);
            EventBus.getDefault().post(new CounterMessage(true));

            Log.d(TAG, "readed = " + readedMessage.getReqId() + " / " + readedMessage.getShopId());
        } catch (Throwable e) {
            Log.d(TAG, String.valueOf(e));

        }
    }

    @Subscribe
    public void onSendMessage(SendMessage sendMessage) {
        Log.d(TAG, "onSendMessage");

        if (!mSocket.connected())
            connectToServer(new ConnectMessage());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("text", sendMessage.getText());
            jsonObject.accumulate("request_id", sendMessage.getRequest_id());
            jsonObject.accumulate("shop_id", sendMessage.getShop_id());
            jsonObject.accumulate("uniq", sendMessage.getUniq());
            mSocket.emit("send_message", jsonObject);
            Log.d(TAG, "send message = " + sendMessage.getText()+ sendMessage.getRequest_id()+sendMessage.getShop_id());
        } catch (Throwable e) {
            Log.d(TAG, String.valueOf(e));

        }
    }
    @Subscribe
    public void onSearchMore(rId rid) {
        Log.d(TAG, "search_more");

        if (!mSocket.connected())
            connectToServer(new ConnectMessage());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("request_id", rid.getrId());

            mSocket.emit("search_more", jsonObject);
            Log.d(TAG, "search_more = "+rid.getrId());
        } catch (Throwable e) {
            Log.d(TAG, String.valueOf(e));

        }
    }
    @Subscribe
    public void onPayToCall(PayToCall payToCall) {
        Log.d(TAG, "payToCall");

        if (!mSocket.connected())
            connectToServer(new ConnectMessage());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("sid", payToCall.getSid());
            jsonObject.accumulate("request_id", payToCall.getRequestId());
            mSocket.emit("payToCall", jsonObject);
            Log.d(TAG, "payToCall: sid" + payToCall.getSid() + "/ req " + payToCall.getRequestId());
        } catch (Throwable e) {
            Log.d(TAG, String.valueOf(e));

        }
    }


    @Subscribe
    public void onWriting(WritingMessage writingMessage) {
        Log.d(TAG, "onWriting");

        if (!mSocket.connected())
            connectToServer(new ConnectMessage());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("request", writingMessage.getRequestId());
            jsonObject.accumulate("shop", writingMessage.getShopId());
            mSocket.emit("writing", jsonObject);
            Log.d(TAG, "writing: req " + writingMessage.getRequestId() + "/ shop " + writingMessage.getShopId());
        } catch (Throwable e) {
            Log.d(TAG, String.valueOf(e));

        }
    }
    @Subscribe
    public void onInvite(InviteMessage inviteMessage) {
        Log.d(TAG, "onInvite");

        if (!mSocket.connected())
            connectToServer(new ConnectMessage());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("chat_id", inviteMessage.getChatId());
            jsonObject.accumulate("friend_phone", inviteMessage.getFriendPhone());
            mSocket.emit("invite_to_chat", jsonObject);
            Log.d(TAG, "onInvite: chat_id " + inviteMessage.getChatId() + "/ friend_phone " + inviteMessage.getFriendPhone());
        } catch (Throwable e) {
            Log.d(TAG, String.valueOf(e));

        }
    }

    @Subscribe
    public void onListenWriting(PostListenWritingMessage writingMessage) {
        Log.d(TAG, "onListenWriting");
        if (!mSocket.connected())
            connectToServer(new ConnectMessage());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("request_id", writingMessage.getRequestId());
            jsonObject.accumulate("shop_id", writingMessage.getShopId());
            mSocket.emit("listen_writing", jsonObject);
            Log.d(TAG, "listen_writing: req " + writingMessage.getRequestId() + "/ shop " + writingMessage.getShopId());
        } catch (Throwable e) {
            Log.d(TAG, String.valueOf(e));

        }
    }

    @Subscribe
    public void onSearchEvent(SearchMessage message) {
        Log.d(TAG, "onSearchWriting");

        if (!mSocket.connected())

        connectToServer(new ConnectMessage());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("text", message.getText());
                jsonObject.accumulate("lat", message.getLat());
                jsonObject.accumulate("lon", message.getLon());
                jsonObject.accumulate("radius", message.getRadius());
                jsonObject.accumulate("uniq", message.getUniq());
                mSocket.emit("search", jsonObject);
                Log.d(TAG, "search (да, да, тот самый) " + jsonObject.toString());

            } catch (JSONException e) {
                Log.d(TAG, String.valueOf(e));
            }
    }
    @Subscribe
    public void onIncreaseEvent(IncreaseRadius message) {
        Log.d(TAG, "onIncreaseEvent");

        if (!mSocket.connected())

            connectToServer(new ConnectMessage());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("request_id", message.getReqId());

            jsonObject.accumulate("radius", message.getRadius());
            mSocket.emit("increase_radius", jsonObject);
            Log.d(TAG, "increase_radius " + jsonObject.toString());

        } catch (JSONException e) {
            Log.d(TAG, String.valueOf(e));
        }
    }
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mSocket.close();
        super.onDestroy();
    }

    private void updateCounters(int shopId) {
        MyShopResponse.Data.MyShop myShop = SharedStore.getInstance().getMyShop();
        if (myShop != null) {
            if (shopId != 0 && shopId != myShop.getId()) {
                SharedStore.getInstance().setNewMyRequests(1);
                EventBus.getDefault().post(new RequestCounter(SharedStore.getInstance().getMyRequestsCount(), true));
            } else {
                SharedStore.getInstance().setNewUserRequests(1);
                EventBus.getDefault().post(new RequestCounter(SharedStore.getInstance().getUserRequestsCount(), false));
            }
        } else {
            SharedStore.getInstance().setNewMyRequests(1);
            EventBus.getDefault().post(new RequestCounter(SharedStore.getInstance().getMyRequestsCount(), true));
        }
    }

    private boolean isApplicationBroughtToBackground() {
        ActivityManager am = (ActivityManager) getApplicationContext()
                . getSystemService(Context.ACTIVITY_SERVICE);
        List <ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(getPackageName())) {
                return true;
            }
        }

        return false;
    }


}
