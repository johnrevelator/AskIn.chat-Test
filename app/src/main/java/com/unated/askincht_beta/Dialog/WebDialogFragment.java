package com.unated.askincht_beta.Dialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.Activity.MainActivity;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.Constants;
import com.unated.askincht_beta.Utils.SharedStore;


import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;


public class WebDialogFragment extends SuperDialogFragment  {

    private String mGeolocationOrigin;
    private GeolocationPermissions.Callback mGeolocationCallback;

    @Override
    public int onInflateViewFragment() {

        return R.layout.view_web_dialog;
    }

    @Override
    public void onCreateFragment(Bundle instance) {



    }


    public void gtb(){
        Activity activity = getActivity();
        if(activity != null) {
            startActivity(new Intent(activity, MainActivity.class).putExtra("type", 3).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            Log.i("MyLog", "starting");
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            dismiss();
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
              gtb();
            }
        };
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            if (url.equals(Constants.API.SIMPLE_URL+"home")) {
                Log.i("MyLog", "finished");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        gtb();
                    }
                }, 1000);            } else if (url.equals(Constants.API.SIMPLE_URL+"my/edit/type")||url.equals(Constants.API.SIMPLE_URL+"my/edit")) {
                showProgressDialog();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        closeProgressDialog();
                    }
                }, 4000);
            }
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);


           /* if(url.equals("http://app.askin.chat/my/edit/type")){
                Log.i("MyLog","prfinished");*/
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view.evaluateJavascript("javascript: document.getElementsByClassName('submit')[0].style.display = 'none'", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d("LogName", s); // Prints 'this'
                    }
                });
            }*/
           /* if(url.equals("http://app.askin.chat/my/edit/type")){
                Log.i("MyLog","prfinished");*/
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);


            return true;

        }


    }
    /*@OnClick({R.id.nextButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextButton:
                if(mWebView.getUrl().equals("https://app.askin.chat/my/edit/type")){
                    mWebView.evaluateJavascript("javascript: document.getElementsByName('form_edit_type')[0].submit()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d("LogName", s+" type"); // Prints 'this'
                        }
                    });



                } else if(mWebView.getUrl().equals("https://app.askin.chat/my/edit/cats")){
                    mWebView.evaluateJavascript("javascript: document.getElementsByName('form_edit_cats')[0].submit()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d("LogName", s+" cats"); // Prints 'this'


                        }
                    });

                }else if(mWebView.getUrl().equals("https://app.askin.chat/my/edit")){
                    mWebView.evaluateJavascript("javascript: document.getElementsByName('form_edit').submit()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d("LogName", s+" edit"); // Prints 'this'

                        }
                    });

                }

                break;


        }
    }
*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE && resultCode == Activity.RESULT_OK) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                Log.d("MyLog", String.valueOf(WebChromeClient.FileChooserParams.parseResult(resultCode, intent)));

                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            Log.d("MyLog", String.valueOf(result));

            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(getActivity().getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }


    public class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            callback.invoke(origin, true, false);
        }
    }


    static WebView mWebView;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    Activity  manager;
    public void setUpWebView(){
        Call<SimpleResponse> call = AppMain.getClientSimple().login(SharedStore.getInstance().getSID());
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful()&&response.body().getStatus()==0) {
                    //sosi_pisos(It's for Russian developers)00))))

                    Headers headers = response.headers();
                    Log.i("MyLog",headers.size()+" headers");

                    for(int i=0;i<headers.size();i++){
                        Log.i("MyLog",headers.name(i)+" "+ i);
                        if(headers.name(i).equals("Set-Cookie")){
                            CookieManager.getInstance().setCookie(Constants.API.SIMPLE_URL+"my/edit/type", headers.value(i));

                        }
                    }

                    mWebView.getSettings().setJavaScriptEnabled(true);
                    mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                    mWebView.getSettings().setBuiltInZoomControls(true);
                    mWebView.getSettings().setAllowFileAccess(true);
                    mWebView.getSettings().setAppCacheEnabled(true);
                    mWebView.getSettings().setDatabaseEnabled(true);
                    mWebView.getSettings().setAllowContentAccess(true);
                    mWebView.getSettings().setGeolocationEnabled(true);
                    mWebView.setWebViewClient(new MyWebViewClient());
                    mWebView.setWebChromeClient( new WebChromeClient() {
                        @Override
                        public void onGeolocationPermissionsShowPrompt(final String origin,
                                                                       final GeolocationPermissions.Callback callback) {

                            if(!mWebView.getUrl().equals(Constants.API.SIMPLE_URL+"home")) {
                                // Geolocation permissions coming from this app's Manifest will only be valid for devices with API_VERSION < 23.
                                // On API 23 and above, we must check for permission, and possibly ask for it.
                                Log.i("MyLog", "23");
                                final String permission = Manifest.permission.ACCESS_FINE_LOCATION;
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                                        ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
                                    // we're on SDK < 23 OR user has already granted permission
                                    Log.i("MyLog", "<23");
                                    final boolean remember = false;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                                    builder.setTitle("Геолокация");

                                    builder.setMessage("Хотите разрешить определение геолокации?")
                                            .setCancelable(true).setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // origin, allow, remember
                                            callback.invoke(origin, true, true);
                                        }
                                    }).setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // origin, allow, remember
                                            callback.invoke(origin, false, remember);
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();

                                } else {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                                        Log.i("MyLog", ">23d");
                                        // user has denied this permission before and selected [/] DON'T ASK ME AGAIN
                                        // TODO Best Practice: show an AlertDialog explaining why the user could allow this permission, then ask again
                                    } else {
                                        // ask the user for permissions
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, 1);
                                        mGeolocationOrigin = origin;
                                        mGeolocationCallback = callback;
                                        Log.i("MyLog", ">23r");

                                    }

                                }
                            }
                        }


                        // For 3.0+ Devices (Start)
                        // onActivityResult attached before constructor
                        protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                            mUploadMessage = uploadMsg;
                            Intent getIntent = new Intent(
                                    Intent.ACTION_PICK);
                            getIntent.setType("image/*");


                            Intent chooserIntent = Intent.createChooser(getIntent, "Выберите картинку");
                            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                        }


                        // For Lollipop 5.0+ Devices
                        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                            if (uploadMessage != null) {
                                uploadMessage.onReceiveValue(null);
                                uploadMessage = null;
                            }

                            uploadMessage = filePathCallback;

                            Intent intent = fileChooserParams.createIntent();
                            intent.setType("image/*");

                            try {
                                startActivityForResult(intent, REQUEST_SELECT_FILE);
                            } catch (ActivityNotFoundException e) {
                                uploadMessage = null;
                                Toast.makeText(getActivity().getApplicationContext(), "Невозможно открыть файл", Toast.LENGTH_LONG).show();
                                return false;
                            }
                            return true;
                        }

                        //For Android 4.1 only
                        protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                            mUploadMessage = uploadMsg;
                            Intent getIntent = new Intent(
                                    Intent.ACTION_PICK);
                            getIntent.setType("image/*");


                            Intent chooserIntent = Intent.createChooser(getIntent, "Выберите картинку");
                            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                        }

                        protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                            mUploadMessage = uploadMsg;
                            Intent getIntent = new Intent(
                                    Intent.ACTION_PICK);
                            getIntent.setType("image/*");


                            Intent chooserIntent = Intent.createChooser(getIntent, "Выберите картинку");
                            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                        }
                    });
                    if(getArguments().getInt("type")==1)
                        mWebView.loadUrl(Constants.API.SIMPLE_URL+"my/edit");

                    else
                        mWebView.loadUrl(Constants.API.SIMPLE_URL+"my/edit/type");

                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.setIsRefresh(true);
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                setUpWebView();
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

            }

        });

    }
    @Override
    public View onCreateViewFragment(final View view) {

      manager = getActivity();

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mWebView = (WebView) view.findViewById(R.id.webView);
        setUpWebView();



        return view;
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
    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                boolean allow = false;
                Log.i("MyLog","23d");

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // user has allowed these permissions
                    Log.i("MyLog","+23d");

                    allow = true;
                }
                if (mGeolocationCallback != null) {
                    final boolean remember = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Геолокация");
                    final boolean finalAllow = allow;
                    final boolean finalAllow1 = allow;
                    builder.setMessage("Хотите разрешить определение геолокации?")
                            .setCancelable(true).setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // origin, allow, remember
                            mGeolocationCallback.invoke(mGeolocationOrigin, finalAllow1, true);
                        }
                    }).setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // origin, allow, remember
                            mGeolocationCallback.invoke(mGeolocationOrigin, false, false);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    Log.i("MyLog","-23d");

                }
                break;
        }
    }


    @Override
    public void onAttachFragment(Activity activity) {

    }


}









