package com.unated.askincht_beta;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.unated.askincht_beta.Network.NetworkService;
import com.unated.askincht_beta.Network.NetworkServiceArt;
import com.unated.askincht_beta.Network.NetworkServiceSimple;
import com.unated.askincht_beta.Utils.Constants;
import com.vk.sdk.VKSdk;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AppMain extends Application {

    public static boolean inChat = false;
    public static boolean isRefresh = false;
    private static Context mContext;
    private static NetworkService sNetworkService;
    private static NetworkServiceArt sNetworkServiceart;
    private static NetworkServiceSimple sNetworkServicesimple;
    private static OkHttpClient client;

    public static OkHttpClient getHttpClient() {
        return client;
    }

    public static NetworkService getClient() {
        return sNetworkService;
    }
    public static NetworkServiceSimple getClientSimple() {
        return sNetworkServicesimple;
    }
    public static NetworkServiceArt getClientArt() {
        return sNetworkServiceart;
    }

    public static Context getContext() {
        return mContext;
    }
    public static void setIsRefresh(boolean isRef){
        if(isRef) {
        isRefresh=true;
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isRefresh=false;
                }
            }, 10000);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        setupRetrofit();
        Fabric.with(this, new Answers());
        Fabric.with(this, new Crashlytics());
        VKSdk.initialize(mContext);
        initImageLoader(mContext);
        startService(new Intent(mContext, SocketService.class));
        FirebaseMessaging.getInstance().subscribeToTopic("news");

    }

    private static void initImageLoader(Context context) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config);
    }

    private void setupRetrofit() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Retrofit retrofitArt = new Retrofit.Builder()
                .baseUrl(Constants.API.ART_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Retrofit retrofitSimple = new Retrofit.Builder()
                .baseUrl(Constants.API.SIMPLE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        sNetworkService = retrofit.create(NetworkService.class);
        sNetworkServiceart = retrofitArt.create(NetworkServiceArt.class);
        sNetworkServicesimple = retrofitSimple.create(NetworkServiceSimple.class);
    }
}
