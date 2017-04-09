package com.unated.askincht_beta.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.Language;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.google.android.gms.maps.model.LatLng;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.Activity.PhoneDialogFragment;
import com.unated.askincht_beta.Adapter.ChatAdapter;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Dialog.RecallDialogFragment;
import com.unated.askincht_beta.Fragments.MapChat;
import com.unated.askincht_beta.Fragments.SuperFragment;
import com.unated.askincht_beta.Pojo.BalanceResponse;
import com.unated.askincht_beta.Pojo.BusMessages.CloseMessage;
import com.unated.askincht_beta.Pojo.BusMessages.CounterMessage;
import com.unated.askincht_beta.Pojo.BusMessages.InviteMessage;
import com.unated.askincht_beta.Pojo.BusMessages.ListenWritingMessage;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.BusMessages.MessageItem;
import com.unated.askincht_beta.Pojo.BusMessages.MsgPnd;
import com.unated.askincht_beta.Pojo.BusMessages.PostListenWritingMessage;
import com.unated.askincht_beta.Pojo.BusMessages.ReadedMessage;
import com.unated.askincht_beta.Pojo.BusMessages.RequestCounter;
import com.unated.askincht_beta.Pojo.BusMessages.SendMessage;
import com.unated.askincht_beta.Pojo.BusMessages.WritingMessage;
import com.unated.askincht_beta.Pojo.ChatInterface;
import com.unated.askincht_beta.Pojo.MessagesResponse;
import com.unated.askincht_beta.Pojo.MyShopResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.Pojo.Unread;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.DateFormater;
import com.unated.askincht_beta.Utils.RealPathUtil;
import com.unated.askincht_beta.Utils.SharedStore;


import org.apache.commons.io.FileUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vk.sdk.VKUIHelper.getApplicationContext;


public class ChatFragment extends SuperFragment {

    private static final String EXTRA_REQUEST = "extra_req";
    private static final String EXTRA_SHOP = "extra_shop";
    public static final String TAG = "MyLog";
    private static final String EXTRA_TITLE = "extra_title";

    @Bind(R.id.rvChat)
    RecyclerView mRvChat;

    @Bind(R.id.etText)
    EditText mEtText;
    @Bind(R.id.ivSend)
    ImageView mIvSend;
    @Bind(R.id.ivAdd)
    ImageView mAdd;
    @Bind(R.id.call)
    ImageView calling;
    @Bind(R.id.more_chat)
LinearLayout moreChat;
    @Bind(R.id.textView7)
    TextView mTitleView;
    @Bind(R.id.tvMsgCount1)
    TextView tvMsgCount;
    @Bind(R.id.sale)
    RelativeLayout sale;
    @Bind(R.id.status)
    TextView status;
    @Bind(R.id.writing_stat)
    TextView writingStatus;

    @Bind(R.id.nav)
    LinearLayout nav;


    @Bind(R.id.title_chat)
    LinearLayout titChat;
    @Bind(R.id.llMsgStatus)
    LinearLayout llMsgStatus;
    @Bind(R.id.close_sale)
    ImageView closeSale;

    int check;
    double lat;
    double lng;

    private String mRequestId;
    private ImageView phone;
    private String mShopId;
    private String mPhone;
    private String balance;
    private String mTitle;
    private double value;
    private ChatAdapter mAdapter;
    private List<ChatInterface> mMessageItemList;
    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;
    private boolean isWriting = false;
    private boolean isIAmWriting = false;


    public static ChatFragment newInstance(String requestId, String shopId, String title) {
        Bundle args = new Bundle();
        args.putString(EXTRA_REQUEST, requestId);
        args.putString(EXTRA_SHOP, shopId);
        args.putString(EXTRA_TITLE, title);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int onInflateViewFragment() {
        return R.layout.fragment_chat;
    }

    @Override
    public void onCreateFragment(Bundle instance) {


        mMessageItemList = new ArrayList<>();
        mAdapter = new ChatAdapter(getActivity(), mMessageItemList);
    }

    public void showDialPay() {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setMessage("Для того, чтобы продолжить общение, пожалуйста, пополните баланс")
                .

                        setCancelable(false)
                .

                        setNegativeButton("Ок",
                                new DialogInterface.OnClickListener() {
                                    public void onClick (DialogInterface dialog,int id){
                                        dialog.cancel();
                                    }
                                }

                        );
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void getBalance(){

        Call<BalanceResponse> call = AppMain.getClient().getShopBalance(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<BalanceResponse>() {
            @Override
            public void onResponse(Call<BalanceResponse> call, Response<BalanceResponse> response) {
                if (response.isSuccessful() && response.body().getData() != null&&response.body().getStatus()==0) {
                    Log.i(TAG, response.body().getData().getBalance() + " баланс магазина");
                    SharedStore.getInstance().setBalance(response.body().getData().getBalance());
                    getMessages();


                }
                else  if(response.body().getStatus() == 1026) {

                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                getBalance();
                                }
                        }

                        @Override
                        public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                            closeProgressDialog();
                        }
                    });
                }else  if(response.body().getStatus() == 1027||response.body().getStatus() == 1028) {


                    logout();
                }
                else {
                    getMessages();
                }
            }

            @Override
            public void onFailure(Call<BalanceResponse> call, Throwable t) {
                Log.i("MyLog", String.valueOf(t));
                getMessages();
            }
        });

    }
    private Uri outputFileUri;

   /* public String getPath(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }}

*/
   private String getRealPathFromURI(Uri contentURI) {
       String result;
       Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
       if (cursor == null) { // Source is Dropbox or other similar local file path
           result = contentURI.getPath();
       } else {
           cursor.moveToFirst();
           int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
           result = cursor.getString(idx);
           cursor.close();
       }
       return result;
   }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // запишем в лог значения requestCode и resultCode
        // если пришло ОК
        String filePath = "";
        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK) {

            ArrayList<Uri> image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            for (int i = 0; i < image_uris.size(); i++) {

                //do something

/*
            selectedImageUri = data.getData();
*/
                Log.d("MyLog", String.valueOf(image_uris.get(i)));
                Log.d("MyLog", String.valueOf(image_uris.get(i).getPath()));

                File file = FileUtils.getFile(image_uris.get(i).getPath());
                Log.d("MyLog", String.valueOf(getImageContentUri(getActivity(), file)));


                final String[] separated = mShopId.split(";");
                Log.d("OkHttp", String.valueOf(file));
                RequestBody bFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                RequestBody bSid = RequestBody.create(okhttp3.MultipartBody.FORM, SharedStore.getInstance().getToken());
                RequestBody bRequest = RequestBody.create(okhttp3.MultipartBody.FORM, mRequestId);
                RequestBody bId = RequestBody.create(okhttp3.MultipartBody.FORM, separated[0]);
                RequestBody bUniq = RequestBody.create(okhttp3.MultipartBody.FORM, random());

                RequestBody requestFile =
                        RequestBody.create(
                                MediaType.parse(getContext().getContentResolver().getType(getImageContentUri(getActivity(), file))),
                                file
                        );

                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file", file.getName(), requestFile);


                Call<ResponseBody> call = AppMain.getClient().sendFiles(bSid, bRequest, bId, bUniq, body);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        Log.d("OkHttp", "success");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("OkHttp", t.getMessage());
                    }
                });
            }
        } else if (requestCode == 234 && resultCode == Activity.RESULT_OK) {
            contactPicked(data);

        }

        }
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getActivity().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }
    public static String random() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 13; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        Log.i(TAG, output);
        return output;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Log.v(TAG, "onResume");

        AppMain.inChat = true;
        NotificationManager nMgr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(3);
        setTimer();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }        AppMain.inChat = false;
        super.onDestroy();

    }
    @Override
    public View onCreateViewFragment(final View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRvChat.setLayoutManager(linearLayoutManager);
        mRvChat.setAdapter(mAdapter);
        final String[] separated = mShopId.split(";");


        EventBus.getDefault().post(new PostListenWritingMessage(mRequestId,separated[0]));
        clearCounters(Integer.valueOf(separated[0]));
        mTitleView.setText(mTitle);
        mEtText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mRvChat.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRvChat.scrollToPosition(mRvChat.getAdapter().getItemCount() - 1);

                        }
                    }, 500);
                } else {

                }
            }
        });
        mEtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isIAmWriting) {
                    isIAmWriting = true;
                    Log.i(TAG,"Im Writing");
                    EventBus.getDefault().post(new WritingMessage(mRequestId, separated[0]));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isIAmWriting = false;
                            Log.i(TAG,"Im Writing close");

                        }
                    }, 5000);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;
    }
    int idChat;
    private void getMessages() {
        showProgressDialog();
        final String[] separated = mShopId.split(";");
        Log.i(TAG, separated[0]+" "+mRequestId);

        Call<MessagesResponse> msg = AppMain.getClient().getRequestMessages(SharedStore.getInstance().getSID(), separated[0], mRequestId,SharedStore.getInstance().getToken());
        msg.enqueue(new Callback<MessagesResponse>() {
            @Override
            public void onResponse(Call<MessagesResponse> call, Response<MessagesResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    mMessageItemList.clear();
                    mMessageItemList.addAll(response.body().getData().getMessages());
                    if (response.body().getData().getOnline())
                        status.setText("В сети");
                    else
                        status.setText("Был(-а) в сети в " + DateFormater.getFormatedDate(response.body().getData().getLast() * 1000, "HH:mm"));
/*
                mPhone=response.body().getData().g
*/
                    mAdapter.notifyDataSetChanged();
                    mRvChat.scrollToPosition(mAdapter.getItemCount() - 1);
                    titChat.setVisibility(View.VISIBLE);
                    if(isClose)
                    sale.setVisibility(View.VISIBLE);

                    if (check == 0) {
/*
                        timen.setVisibility(View.VISIBLE);
*/
                        if(!separated[0].equals(String.valueOf(20))) {


                            nav.setVisibility(View.VISIBLE);
                            moreChat.setVisibility(View.VISIBLE);
                        }
                    } else {
                        /*calling.setBackground(getResources().getDrawable(R.drawable.coupon));
                        if(!separated[0].equals(String.valueOf(20))) {


                            nav.setVisibility(View.VISIBLE);
                        } else{
                            sale.setVisibility(View.GONE);
                        }
*/
                        nav.setVisibility(View.GONE);
                        moreChat.setVisibility(View.GONE);
                        if (Integer.valueOf(SharedStore.getInstance().getBalance()) <= 25) {
                            final FrameLayout frameView = new FrameLayout(getActivity());

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Чтобы отвечать на сообщения клиентов или на новые запросы клиентов, пожалуйста, пополните баланс")
                                    .setCancelable(false)
                                    .setView(frameView);

                            AlertDialog alert = builder.create();
                            LayoutInflater inflater = alert.getLayoutInflater();
                            View dialoglayout = inflater.inflate(R.layout.alert_dialog_layout, frameView);

                            alert.show();
                            TextView messageView = (TextView) alert.findViewById(android.R.id.message);
                            messageView.setGravity(Gravity.CENTER);

                        }


                    }

                    setRead();

                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.setIsRefresh(true);
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                getMessages();
                                AppMain.setIsRefresh(false);


                            }else if(response.body().getStatus() == 1029){
                                AppMain.setIsRefresh(false);

                                logout();
                            }
                        }

                        @Override
                        public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                            closeProgressDialog();
                        }
                    });
                }else  if(response.body().getStatus() == 1027||response.body().getStatus() == 1028||response.body().getStatus() == 1029) {


                    logout();
                }
            }

            @Override
            public void onFailure(Call<MessagesResponse> call, Throwable t) {
                closeProgressDialog();
            }
        });
    }


    private void setRead() {
        final String[] separated = mShopId.split(";");

        Call<SimpleResponse> call = AppMain.getClient().setRead(SharedStore.getInstance().getSID(),separated[0], mRequestId,SharedStore.getInstance().getToken());
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.isSuccessful()&&response.body().getStatus()==0){
                notif();
                closeProgressDialog();
                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.setIsRefresh(true);
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                setRead();
                                AppMain.setIsRefresh(false);


                            }else if(response.body().getStatus() == 1029){
                                AppMain.setIsRefresh(false);

                                logout();
                            }
                        }

                        @Override
                        public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                            closeProgressDialog();
                        }
                    });
                }else  if(response.body().getStatus() == 1027||response.body().getStatus() == 1028||response.body().getStatus() == 1029) {


                    logout();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                closeProgressDialog();
            }
        });
    }
    boolean isClose;

    @Override
    public void onAttachFragment(Activity activity) {
        if (getArguments() != null) {
            mRequestId = getArguments().getString(EXTRA_REQUEST);
            Log.i(TAG,mRequestId+" reqId in Frag");

            mShopId = getArguments().getString(EXTRA_SHOP);
            String[] separated = mShopId.split(";");
            if (separated[0].length() == 0 || separated[0].equals("0")) {
                mShopId = String.valueOf(SharedStore.getInstance().getMyShop().getId());
            }
            mTitle=separated[1];
            if(separated[2].equals(mRequestId)){
                Log.i(TAG,separated[2]+" "+mRequestId);
                check=1;
                isClose=Boolean.parseBoolean(separated[3]);
                Log.i("MyLog","status "+isClose);

            }
            else{
                value= Double.parseDouble(separated[2].replace(",","."));
                isClose=Boolean.parseBoolean(separated[6]);
                Log.i("MyLog","status "+isClose);



            }

        }
    }
    /*public void getTimeDir(){
        final String[] separate = mShopId.split(";");

        GoogleDirection.withServerKey("AIzaSyDqEgRCf3qXDdfBuahMGQgwToUpXBX2Ozw")
                .from(new LatLng(Double.valueOf(SharedStore.getInstance().getMyLat()), Double.valueOf(SharedStore.getInstance().getMyLng())))
                .to(new LatLng(Double.valueOf(separate[3]),Double.valueOf(separate[4])))
                .language(Language.RUSSIAN)
                .alternativeRoute(true)
                .transportMode(TransportMode.DRIVING)

                .avoid(AvoidType.FERRIES)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if(direction.isOK()) {

                            for (int i = 0; i < direction.getRouteList().size(); i++) {
                                Route route = direction.getRouteList().get(i);
                                progDrive.setVisibility(View.GONE);
                                Log.i(TAG, String.valueOf("5 "+direction.getRouteList().get(0).getLegList().get(0).getDuration().getText()));
                                tvDriveTime.setText(direction.getRouteList().get(0).getLegList().get(0).getDuration().getText());

                                Log.i(TAG, String.valueOf(rawBody)+"5");


                            }                            }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.i(TAG, String.valueOf(t));

                    }
                });
        GoogleDirection.withServerKey("AIzaSyDqEgRCf3qXDdfBuahMGQgwToUpXBX2Ozw")
                .from(new LatLng(Double.valueOf(SharedStore.getInstance().getMyLat()), Double.valueOf(SharedStore.getInstance().getMyLng())))
                .to(new LatLng(Double.valueOf(separate[3]),Double.valueOf(separate[4])))
                .language(Language.RUSSIAN)
                .alternativeRoute(true)
                .transportMode(TransportMode.WALKING)

                .avoid(AvoidType.FERRIES)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if(direction.isOK()) {

                            for (int i = 0; i < direction.getRouteList().size(); i++) {
                                Route route = direction.getRouteList().get(i);
                                progWalk.setVisibility(View.GONE);

                                Log.i(TAG, String.valueOf("6 "+direction.getRouteList().get(0).getLegList().get(0).getDuration().getText()));

                                tvWalkTime.setText(direction.getRouteList().get(0).getLegList().get(0).getDuration().getText());


                            }                            }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.i(TAG, String.valueOf(t));

                    }
                });
    }
*/

    Uri imageUri;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    @OnClick({R.id.ivSend,R.id.call,R.id.ivAdd,R.id.llMsgStatus,R.id.more_chat,R.id.sale,R.id.toolbar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivSend:
                final String[] separated = mShopId.split(";");

                if (mEtText.getText().toString().length() > 0) {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText(mEtText.getText().toString());
                    sendMessage.setRequest_id(Integer.valueOf(mRequestId));
                    sendMessage.setShop_id(Integer.valueOf(separated[0]));
                    sendMessage.setUniq(UUID.randomUUID().toString());
                    Log.i(TAG, separated[0] + " dgsdgd " + mRequestId);
                    AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

                    final MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.inchat);
                    switch (am.getRingerMode()) {
                        case AudioManager.RINGER_MODE_NORMAL:

                            mediaPlayer.start();
                            break;
                    }
                    EventBus.getDefault().post(sendMessage);
                    mEtText.setText(null);
                }



                break;
            case R.id.more_chat:
                final String[] separat = mShopId.split(";");

                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                if(isClose)
                    popupMenu.inflate(R.menu.popupmenuclose);
                else
                    popupMenu.inflate(R.menu.popupmenu); // Для Android 4.0
                // для версии Android 3.0 нужно использовать длинный вариант
                // popupMenu.getMenuInflater().inflate(R.menu.popupmenu,
                // popupMenu.getMenu());

                popupMenu
                        .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                // Toast.makeText(PopupMenuDemoActivity.this,
                                // item.toString(), Toast.LENGTH_LONG).show();
                                // return true;
                                switch (item.getItemId()) {

                                    case R.id.menu1:

                                        return true;
                                    case R.id.menu3:
                                        RecallDialogFragment searchProcessDialogFragment = new  RecallDialogFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("type",0);
                                        bundle.putInt("id",Integer.valueOf(separat[0]));
                                        bundle.putInt("rId",Integer.valueOf(mRequestId));
                                        searchProcessDialogFragment.setArguments(bundle);
                                        searchProcessDialogFragment.show(getFragmentManager(), "TAG");
                                        return true;
                                    case R.id.menu2:
                                        final String[] separate = mShopId.split(";");

                                        Intent inten=new Intent(getActivity(),MapChat.class);
                                        inten.putExtra("value",value);
                                        inten.putExtra("lat",separate[3]);
                                        inten.putExtra("lng",separate[4]);

                                        startActivity(inten);
                                        return true;
                                    case R.id.menu4:
                                        RecallDialogFragment searchProcessDialogFragmen = new  RecallDialogFragment();
                                        Bundle b = new Bundle();
                                        b.putInt("type",1);
                                        b.putInt("id",Integer.valueOf(separat[0]));
                                        b.putInt("rId",Integer.valueOf(mRequestId));
                                        searchProcessDialogFragmen.setArguments(b);
                                        searchProcessDialogFragmen.show(getFragmentManager(), "TAG");
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });

                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

                    @Override
                    public void onDismiss(PopupMenu menu) {

                    }
                });
                popupMenu.show();
                break;

            case R.id.ivAdd:
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int permissionCheckCam = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission_group.CAMERA);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED||permissionCheckCam != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 1);
                } else {

                    getImages();
                }
                break;
            case R.id.llMsgStatus:
                getActivity().onBackPressed();
                break;
            case R.id.sale:
                sale.setVisibility(View.INVISIBLE);
                break;


            case R.id.call:
                final String[] seprated = mShopId.split(";");

                Intent intend =new Intent(getActivity(), PhoneDialogFragment.class);
                intend.putExtra("tel",seprated[5]);
                intend.putExtra("req",mRequestId);
                startActivity(intend);
                break;
          /*  case R.id.invite:
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, 234);

*//*
              EventBus.getDefault().post(new InviteMessage());
*//*
                break;*/
           /* case R.id.gtmap:
                final String[] separate = mShopId.split(";");

                Intent inten=new Intent(getActivity(),MapChat.class);
                inten.putExtra("value",value);
                inten.putExtra("lat",separate[3]);
                inten.putExtra("lng",separate[4]);

                startActivity(inten);
                break;*/
        }
    }
    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            // Set the value to the textviews
            Log.i("MyLog",phoneNo);
/*
            EventBus.getDefault().post(new InviteMessage());
*/
        } catch (Exception e) {
            Log.i("MyLog", String.valueOf(e));
        }
    }

    private void getImages() {
        Config config = new Config();
        config.setToolbarTitleRes(R.string.photo);
        config.setSelectionMin(1);
        config.setSelectionLimit(10);
        config.setCameraHeight(R.dimen.camera_hieght);

        ImagePickerActivity.setConfig(config);

        Intent intent  = new Intent(getActivity(), ImagePickerActivity.class);
        getActivity().startActivityForResult(intent,INTENT_REQUEST_GET_IMAGES);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    Log.i(TAG, "next");
                    getImages();
                }
                break;

            default:
                break;
        }
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private static Uri getCaptureImageOutputUri(Activity activity) {
        Uri outputFileUri = null;
        File getImage = activity.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "ImagePicked.jpeg"));
        }
        return outputFileUri;
    }

    public static Uri getPickImageResultUri(Activity activity, Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri(activity) : data.getData();
    }



    public static String getRealPathFromURI(Activity activity,Uri contentUri) {
        String result;
        Cursor cursor = activity.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    private static String getUniqueImageFilename() {
        // TODO Auto-generated method stub
        String fileName = "img_" + System.currentTimeMillis() + ".jpg";
        return fileName;
    }
    private void openImageIntent() {

// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdir();
        final String fname = getUniqueImageFilename();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, 123);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Log.v(TAG, "onStart");
notif();
        AppMain.inChat = true;
    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop");

        EventBus.getDefault().unregister(this);
        AppMain.inChat = false;
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void newMessage(MessageItem messageItem) {
        final String[] separated = mShopId.split(";");

        if (messageItem.getShop_id() == Integer.valueOf(separated[0]).intValue() && messageItem.getRequest_id() == Integer.valueOf(mRequestId).intValue()) {
            mMessageItemList.add(mMessageItemList.size(), messageItem);
            mAdapter.notifyItemInserted(mMessageItemList.size() - 1);
            mRvChat.scrollToPosition(mAdapter.getItemCount() - 1);

            clearCounters(messageItem.getShop_id());
            EventBus.getDefault().post(new ReadedMessage(mRequestId, separated[0]));

        }
        setRead();

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void msgPnd(MsgPnd msgPnd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Сожалеем, но вы немного опоздали. Пятеро Ваших конкурентов отреагировали раньше и уже общаются с этим клиентом.\n" +
                "Если клиент пожелает увеличить количество предложений от компаний, ваше сообщение сразу уйдет клиенту. Скорость города берёт...")
                .setCancelable(false)
                .setNegativeButton("Ок",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
        /*final String[] separated = mShopId.split(";");

        if (msgPnd.getShop_id() == Integer.valueOf(separated[0]).intValue() && messageItem.getRequest_id() == Integer.valueOf(mRequestId).intValue()) {
            mMessageItemList.add(mMessageItemList.size(), messageItem);
            mAdapter.notifyItemInserted(mMessageItemList.size() - 1);
            mRvChat.scrollToPosition(mAdapter.getItemCount() - 1);

            clearCounters(messageItem.getShop_id());
            EventBus.getDefault().post(new ReadedMessage(mRequestId, separated[0]));
        }*/

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWriting(ListenWritingMessage writingMessage) {
        final String[] separate = mShopId.split(";");

        Log.i(TAG,"Writing emt");
        Log.i(TAG,writingMessage.getShopId()+"Writing emt"+separate[0]);
        Log.i(TAG,writingMessage.getRequestId()+"Writing emt"+mRequestId);

        if (writingMessage != null) {
            if (writingMessage.getShopId().equalsIgnoreCase(separate[0])
                    && writingMessage.getRequestId().equalsIgnoreCase(mRequestId)
                    && !isWriting) {
                isWriting = true;
                writingStatus.setVisibility(View.VISIBLE);
                Log.i(TAG,"Writing");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        writingStatus.setVisibility(View.GONE);

                        isWriting = false;
                        Log.i(TAG,"Writing close");

                    }
                }, 2000);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void isReaded(String readed) {
        updateReaded();
    }

    private void updateReaded() {
        final String[] separated = mShopId.split(";");

        Call<MessagesResponse> msg = AppMain.getClient().getRequestMessages(SharedStore.getInstance().getSID(), separated[0], mRequestId,SharedStore.getInstance().getToken());
        msg.enqueue(new Callback<MessagesResponse>() {
            @Override
            public void onResponse(Call<MessagesResponse> call, Response<MessagesResponse> response) {
                if(response.isSuccessful()&&response.body().getStatus()==0) {
                    notif();
                    mMessageItemList.clear();
                    mMessageItemList.addAll(response.body().getData().getMessages());
                    mAdapter.notifyDataSetChanged();
                    mRvChat.scrollToPosition(mAdapter.getItemCount() - 1);
                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.setIsRefresh(true);
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                updateReaded();
                                AppMain.setIsRefresh(false);


                            }else if(response.body().getStatus() == 1029){
                                AppMain.setIsRefresh(false);

                                logout();
                            }
                        }

                        @Override
                        public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                            closeProgressDialog();
                        }
                    });
                }else  if(response.body().getStatus() == 1027||response.body().getStatus() == 1028||response.body().getStatus() == 1029) {


                    logout();
                }
            }

            @Override
            public void onFailure(Call<MessagesResponse> call, Throwable t) {
                closeProgressDialog();
            }
        });
    }

    private void clearCounters(int shopId) {
        MyShopResponse.Data.MyShop myShop = SharedStore.getInstance().getMyShop();
        if (myShop != null) {
            if (shopId != 0 && shopId != myShop.getId()) {
                SharedStore.getInstance().clearMyRequests();
                EventBus.getDefault().post(new RequestCounter(0, false));
            } else {
                SharedStore.getInstance().clearUserRequests();
                EventBus.getDefault().post(new RequestCounter(0, true));
            }
        } else {
            SharedStore.getInstance().clearMyRequests();
            EventBus.getDefault().post(new RequestCounter(0, false));
        }
    }
    public void setTimer(){

        getBalance();





    }
    boolean not=false;
    public void notif() {
        Log.i("msgT", "new");
        if (!not) {
            not=true;
            Call<Unread> call = AppMain.getClient().getUnreadCounts(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
            call.enqueue(new Callback<Unread>() {
                @Override
                public void onResponse(Call<Unread> call, Response<Unread> response) {
                    closeProgressDialog();
                    if (response.isSuccessful() && response.body().getStatus() == 0) {
                        int myReq = 0;
                        int usReq = 0;
                        Log.i("msgT", String.valueOf(myReq));
                        Log.i("msgT", String.valueOf(usReq));

                        myReq = response.body().getData().getMessageShops();
                        usReq = response.body().getData().getMessageClients();

                        if (myReq + usReq > 0) {
                            llMsgStatus.setVisibility(View.VISIBLE);
                            tvMsgCount.setText(String.valueOf(myReq + usReq));

                        } else {
                            llMsgStatus.setVisibility(View.GONE);


                        }
                        not=false;


                    } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                        AppMain.setIsRefresh(true);
                        Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                        callr.enqueue(new Callback<RefreshResponse>() {
                            @Override
                            public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                                closeProgressDialog();
                                if (response.isSuccessful() && response.body().getStatus() == 0) {
                                    SharedStore.getInstance().setToken(response.body().getData().getToken());
                                    notif();
                                    AppMain.setIsRefresh(false);


                                }else if(response.body().getStatus() == 1029){
                                    AppMain.setIsRefresh(false);

                                    logout();
                                }
                            }

                            @Override
                            public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                                closeProgressDialog();
                            }
                        });
                    }else  if(response.body().getStatus() == 1027||response.body().getStatus() == 1028||response.body().getStatus() == 1029) {


                        logout();
                    }
                }

                @Override
                public void onFailure(Call<Unread> call, Throwable t) {
                    closeProgressDialog();
                }
            });
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateToolbarCounter(CounterMessage message) {
        notif();
    }
 @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateClosed(CloseMessage message) {
     sale.setVisibility(View.VISIBLE);
     isClose=true;
onResume();
 }

    public void logout(){
        Call<LogoutResponse> call = AppMain.getClient().logout(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<LogoutResponse>() {

            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    SharedStore.getInstance().setSID(null);
                    SharedStore.getInstance().setUserId(null);
                    SharedStore.getInstance().setMyShop(null);
                    SharedStore.getInstance().setDeviceAuth(true);
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                    getActivity().finish();


                } else {
                    showToast(response.body().getError_msg());
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
            }

        });
    }


}
