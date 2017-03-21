package com.unated.askincht_beta;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.unated.askincht_beta.Activity.ChatActivity;
import com.unated.askincht_beta.Pojo.ShopResponse;
import com.unated.askincht_beta.Pojo.RequestResponse;
import com.unated.askincht_beta.Utils.SharedStore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyLog";
    String tag;
    Integer shopId;
    Integer reqId;
    private static final String EXTRA_REQUEST = "extra_request";
    private static final String EXTRA_SHOP = "extra_shop";
    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_ACT = "extra_act";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Data: " + remoteMessage.getData().get("request_id"));
        Log.d(TAG, "Data: " + remoteMessage.getData().get("shop_id")+" "+SharedStore.getInstance().getUserId());
        if(SharedStore.getInstance().getMyShop()!=null){
            Log.d(TAG, "Data: " + remoteMessage.getData().get("shop_id")+" "+SharedStore.getInstance().getShopId());
            Log.d(TAG, "Data: " + remoteMessage.getData().get("shop_id")+" "+SharedStore.getInstance().getMyShop().getId());
        }

        reqId=Integer.valueOf(remoteMessage.getData().get("request_id"));
/*
        shopId=Integer.valueOf(remoteMessage.getData().get("shop_id"));
*/
        if (!getBaseContext().getPackageName().equalsIgnoreCase(((ActivityManager)getBaseContext().getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0).topActivity.getPackageName()))
        {
            try {
                Log.i(TAG,"MyLog");
                AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

                switch( am.getRingerMode() ){
                    case AudioManager.RINGER_MODE_NORMAL:
                        Log.i(TAG,"MyLog");

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

            }
        }
int id=0;
        if (remoteMessage.getData().size() > 0 && !AppMain.inChat) {
            if (remoteMessage.getData().get("title").equals("Новое сообщение")) {
                if(SharedStore.getInstance().getMyShop()==null){
                    id= Integer.parseInt(SharedStore.getInstance().getUserId());
                }
                else{
                    id=SharedStore.getInstance().getMyShop().getId();
                }
                if (id!=(Integer.valueOf(remoteMessage.getData().get("shop_id")))) {
                    Call<ShopResponse> call = AppMain.getClient().getShop(SharedStore.getInstance().getSID(), Integer.valueOf(remoteMessage.getData().get("shop_id")),SharedStore.getInstance().getToken());
                    call.enqueue(new Callback<ShopResponse>() {
                        @Override
                        public void onResponse(Call<ShopResponse> call, Response<ShopResponse> response) {
                            if (response.isSuccessful()) {
                                Location loc1 = new Location("");
                                loc1.setLatitude(response.body().getData().getShop().getLat());
                                loc1.setLongitude(response.body().getData().getShop().getLon());
                                Location loc2 = new Location("");

                                loc2.setLatitude(Double.parseDouble(SharedStore.getInstance().getMyLat()));
                                loc2.setLongitude(Double.parseDouble(SharedStore.getInstance().getMyLng()));
                                Log.i(TAG, SharedStore.getInstance().getUserId() + " " + response.body().getData().getShop().getUser_id());
                                tag = remoteMessage.getData().get("shop_id") + ";" + response.body().getData().getShop().getName() + ";" + String.format("%.2f", loc1.distanceTo(loc2) / 1000)
                                        + ";" + response.body().getData().getShop().getLat() + ";" + response.body().getData().getShop().getLon() + ";" + response.body().getData().getShop().getPhone();

                                sendNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("request_id"), remoteMessage.getData().get("shop_id"));

                            }

                        }

                        @Override
                        public void onFailure(Call<ShopResponse> call, Throwable t) {
                            Log.i("MyLog", String.valueOf(t));
                        }
                    });
                } else {
                    Call<RequestResponse> call = AppMain.getClient().getRequest(SharedStore.getInstance().getSID(), Integer.valueOf(remoteMessage.getData().get("request_id")),SharedStore.getInstance().getToken());
                    call.enqueue(new Callback<RequestResponse>() {
                        @Override
                        public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                            if (response.isSuccessful()) {

                                Log.i(TAG, SharedStore.getInstance().getUserId() + " " + response.body().getData().getRequests().getUser_id());
                                tag = remoteMessage.getData().get("shop_id") + ";" + response.body().getData().getRequests().getText() + ";" + response.body().getData().getRequests().getId();

                                sendNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("request_id"), remoteMessage.getData().get("shop_id"));

                            }

                        }

                        @Override
                        public void onFailure(Call<RequestResponse> call, Throwable t) {
                            Log.i("MyLog", String.valueOf(t));
                        }
                    });
                }
            }
        }




        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]
    private void getRequest(Integer req_id) {

    }
    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    private void sendNotification(String messageBody,String request_id,String shop_id) {
        Log.d(TAG, "Message Notification");

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("extra_request",request_id);
        intent.putExtra(EXTRA_SHOP,tag);
        intent.putExtra(EXTRA_ACT,"push");
        Log.i(TAG,String.valueOf(reqId)+" reqId");
        Log.i(TAG,request_id+" reqId");
        intent.setAction(Long.toString(System.currentTimeMillis()));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        String[]sep=messageBody.split(":");

        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)

        .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(new long[] {1, 1, 1})
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSmallIcon(R.drawable.ic_new_message)
                .setContentTitle("Новое сообщение")
                .setContentText(sep[1])
                .setAutoCancel(true)
                .setLights(Color.parseColor("#ffffff"), 5000, 5000)

                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(3 /* ID of notification */, notificationBuilder.build());
    }

   /* private void sendNotificationRequest(String messageBody,String request_id,String shop_id) {
        Log.d(TAG, "Message Notification");
        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.i(TAG,tag);

        String[]sep=messageBody.split(":");
        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_new_request)
                .setContentTitle("Новый запрос")
                .setContentText(sep[1])
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(new long[] {1, 1, 1})
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                //.setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(2 *//* ID of notification *//*, notificationBuilder.build());
    }*/
}