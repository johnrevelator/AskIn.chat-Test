package com.unated.askincht_beta.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.unated.askincht_beta.Fragments.ShopListFragment;
import com.unated.askincht_beta.R;


public class ShopListActivity extends SuperActivity {

    public static final String EXTRA = "extra_item";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_base;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setFragment(R.id.container, ShopListFragment.newInstance(getIntent().getIntExtra(EXTRA,0)));
    }

    public static Intent newIntent(Context context, int myRequestItem) {
        Intent intent = new Intent(context, ShopListActivity.class);
        intent.putExtra(EXTRA, myRequestItem);
        return intent;
    }
    @Override
    public void onBackPressed() {

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.back);
        switch( am.getRingerMode() ) {
            case AudioManager.RINGER_MODE_NORMAL:
                mediaPlayer.start();
                break;
        }
        startActivity(new Intent(this, MainActivity.class).putExtra("type",1));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        this.finish();
    }
}
