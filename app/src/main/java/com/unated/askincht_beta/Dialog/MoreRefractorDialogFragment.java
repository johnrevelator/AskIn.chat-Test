package com.unated.askincht_beta.Dialog;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.unated.askincht_beta.Activity.ChatActivity;
import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.BalanceResponse;
import com.unated.askincht_beta.Pojo.BusMessages.CounterMessage;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.MyShopResponse;
import com.unated.askincht_beta.Pojo.ProfileResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.RequestSupResponse;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.Pojo.Unread;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import org.apache.commons.io.FileUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.EXTRA_TEXT;


public class MoreRefractorDialogFragment extends SuperDialogFragment {



    @Bind(R.id.img_rel)
    RelativeLayout relativeLayout;
    @Bind(R.id.main_ph)
    TextView mainPh;



    @Bind(R.id.cancel)

    LinearLayout cancel;
 @Bind(R.id.change_ph)

    LinearLayout change;

  @Bind(R.id.main_img2)

   ImageView mainImg;


int first=0;
    @Override
    public int onInflateViewFragment() {
        return R.layout.view_refractor_dialog;
    }

    @Override
    public void onCreateFragment(Bundle instance) {




    }


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
    }


     public void getProfile(){
         Call<ProfileResponse> call = AppMain.getClient().getProfile(SharedStore.getInstance().getToken());
         call.enqueue(new Callback<ProfileResponse>() {
             @Override
             public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                 if (response.isSuccessful() && response.body().getStatus() == 0) {


                     Log.i("MyLog", "есть");
                     prevName=response.body().getData().getUsername();
                     mainPh.setText(response.body().getData().getUsername());
                     Glide.with(getActivity()).load(response.body().getData().getUserAvatar()).asBitmap().centerCrop().placeholder(R.drawable.mask)

                             .into(new BitmapImageViewTarget(mainImg) {
                                 @Override
                                 protected void setResource(Bitmap resource) {
                                     RoundedBitmapDrawable circularBitmapDrawable =
                                             RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                     circularBitmapDrawable.setCircular(true);
                                     mainImg.setImageDrawable(circularBitmapDrawable);
                                 }
                             });



                 } else if (response.isSuccessful() && response.body().getStatus() == 1026 && !AppMain.isRefresh) {
                     AppMain.setIsRefresh(true);
                     Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(), SharedStore.getInstance().getToken());
                     callr.enqueue(new Callback<RefreshResponse>() {
                         @Override
                         public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                             closeProgressDialog();
                             if (response.isSuccessful() && response.body().getStatus() == 0) {
                                 SharedStore.getInstance().setToken(response.body().getData().getToken());
getProfile();                                 AppMain.setIsRefresh(false);


                             } else if (response.body().getStatus() == 1029) {
                                 AppMain.setIsRefresh(false);

                                 logout();
                             }
                         }

                         @Override
                         public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                             closeProgressDialog();
                         }
                     });
                 } else if (response.body().getStatus() == 1027 || response.body().getStatus() == 1028 || response.body().getStatus() == 1029) {


                     logout();
                 }

             }

             @Override
             public void onFailure(Call<ProfileResponse> call, Throwable t) {
                 Log.i("MyLog", String.valueOf(t));
                 first = 1;


             }
         });
     }
    String prevName;

    @Override
    public View onCreateViewFragment(View view) {


       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                closeProgressDialog();
            }
        }, 2000);*/
        Log.i("MyLog","start");

        getProfile();
        mainImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "next!");

                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int permissionCheckCam = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission_group.CAMERA);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED||permissionCheckCam != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 1);
                } else {

                    getImages();
                }
            }
        });


        return view;
    }

    @Override
    public void onAttachFragment(Activity activity) {

    }
    File file=null;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // запишем в лог значения requestCode и resultCode
        // если пришло ОК
        String filePath = "";
        if (requestCode == 45 && resultCode == Activity.RESULT_OK) {


            ArrayList<Uri> image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            for (int i = 0; i < image_uris.size(); i++) {
                file = FileUtils.getFile(image_uris.get(i).getPath());
                Log.i("MyLog", String.valueOf(file));

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), getImageContentUri(getActivity(),file));
                    mainImg.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Log.i("MyLog", String.valueOf(e));
                }

                //do something

/*
            selectedImageUri = data.getData();
*/
                Log.d("MyLog", String.valueOf(image_uris.get(i)));
                Log.d("MyLog", String.valueOf(image_uris.get(i).getPath()));

               /* File file = FileUtils.getFile(image_uris.get(i).getPath());
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
                });*/
            }
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
    public void saveProfile(){

if(file!=null) {
    RequestBody requestFile =
            RequestBody.create(
                    MediaType.parse(getContext().getContentResolver().getType(getImageContentUri(getActivity(), file))),
                    file
            );

    MultipartBody.Part body =
            MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);


                Call<ResponseBody> call = AppMain.getClient().editAvatar(body,SharedStore.getInstance().getToken());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        Log.d("OkHttp", "success_file");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("OkHttp", t.getMessage());
                    }
                });
            }
        if(!prevName.equals(mainPh.getText().toString())){
            Call<SimpleResponse> call = AppMain.getClient().editName(SharedStore.getInstance().getToken(), mainPh.getText().toString());
            call.enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call,
                                       Response<SimpleResponse> response) {
                    Log.d("OkHttp", "success_file");
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    Log.d("OkHttp", t.getMessage());
                }
            });

        }


    }
    @OnClick({R.id.cancel, R.id.save, R.id.change_ph})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
dismiss();
                break;

            case R.id.change_ph:
                Log.i(TAG, "next");

                Config config = new Config();
                config.setToolbarTitleRes(R.string.photo);
                config.setSelectionMin(1);
                config.setSelectionLimit(1);
                config.setCameraHeight(R.dimen.camera_hieght);

                ImagePickerActivity.setConfig(config);

                Intent intent  = new Intent(getActivity(), ImagePickerActivity.class);
                startActivityForResult(intent,45);
                break;
            case R.id.save:
                Log.i(TAG, "save");

                saveProfile();
                dismiss();
                break;


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    Log.i(TAG, "next2");
                    getImages();
                }
                break;

            default:
                break;
        }
    }
    private void getImages() {
        Config config = new Config();
        config.setToolbarTitleRes(R.string.photo);
        config.setSelectionMin(1);
        config.setSelectionLimit(1);
        config.setCameraHeight(R.dimen.camera_hieght);

        ImagePickerActivity.setConfig(config);

        Intent intent  = new Intent(getActivity(), ImagePickerActivity.class);
        getActivity().startActivityForResult(intent,45);

    }

    String EXTRA_REQUEST = "extra_request";
    String EXTRA_SHOP = "extra_shop";
    String EXTRA_TITLE = "extra_title";
    String EXTRA_ACT = "extra_act";

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
