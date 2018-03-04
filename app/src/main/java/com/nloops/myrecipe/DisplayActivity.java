package com.nloops.myrecipe;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.nloops.myrecipe.data.IngredientsModel;
import com.nloops.myrecipe.data.StepsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

public class DisplayActivity extends AppCompatActivity implements
        ExoPlayer.EventListener {

    @BindView(R.id.exo_simple_player)
    SimpleExoPlayerView mExoPlayerView;
    @BindView(R.id.tv_no_video)
    TextView mNoVideoTextView;
    @BindView(R.id.display_progress_bar)
    ProgressBar mProgressBar;
    @Nullable
    @BindView(R.id.tv_display_activity)
    TextView mDescriptionTextView;
    @Nullable
    @BindView(R.id.button_next)
    Button mNextButton;
    @Nullable
    @BindView(R.id.button_prev)
    Button mPrevButton;
    @Nullable
    @BindView(R.id.iv_thumbnail)
    ImageView mThumbnailImageView;
    // ref of ButterKnife Unbinder
    Unbinder unbinder;

    // Class Tag
    private static final String TAG = DisplayActivity.class.getSimpleName();
    // ref of ExoPlayer
    private SimpleExoPlayer mExoPlayer;
    // Uri that holding videoUrl
    private Uri videoUri;
    // long var to keep track video position if activity goes onPause and returned back
    private long mPlayerPosition = -1;
    private static final String POSITION_STATE = "EXO.PLAYER.STATE";
    // int pos of model
    private int mModelPosition;
    // ref arraylist of StepsModel
    private ArrayList<StepsModel> mStepsModels;
    // ref arraylist of ingredients
    private ArrayList<IngredientsModel> mIngredientsModel;
    // ref of recipe name
    private String mRecipeName;
    // Ref of Media Session
    private static MediaSessionCompat mMediaSession;
    // Ref of PlayBack Compat
    private PlaybackStateCompat.Builder mStateBuilder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        unbinder = ButterKnife.bind(this);
        if (savedInstanceState != null) {
            mPlayerPosition = savedInstanceState.getLong(POSITION_STATE);
        }
        Bundle bundle = getIntent().getExtras();
        mModelPosition = bundle.getInt(DetailActivity.EXTRAS_MODEL_POSITION);
        mStepsModels = bundle.getParcelableArrayList(RecipeCatalog.EXTRAS_STEPS_ARRAYLIST);
        mIngredientsModel = bundle.getParcelableArrayList(RecipeCatalog.EXTRAS_INGREDIENTS_ARRAYLIST);
        String description = mStepsModels.get(mModelPosition).getDescription();
        // if the videoURL is empty we show no video content
        if (mStepsModels.get(mModelPosition).getVideoURL().length() > 0) {
            videoUri = Uri.parse(mStepsModels.get(mModelPosition).getVideoURL());
        } else {
            mNoVideoTextView.setVisibility(View.VISIBLE);
        }
        mRecipeName = bundle.getString(RecipeCatalog.EXTRAS_RECIPE_NAME);
        setTitle(mRecipeName);

        initializeMediaSession();

        // if we are on landscape mode this textview is not exist
        if (!isLandScapeMode()) {
            Typeface fontType = Typeface.createFromAsset(getAssets(), "fonts/Doomsday_Light.ttf");
            mDescriptionTextView.setTypeface(fontType);
            mDescriptionTextView.setText(description);
            displayStepThumbinal();
        }
        initializePlayer(videoUri);

    }


    /**
     * Initialize ExoPlayer
     *
     * @param videoUri
     */
    private void initializePlayer(Uri videoUri) {
        if (videoUri != null) {
            if (mExoPlayer == null) {
                // Create an instance of the ExoPlayer.
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
                mExoPlayerView.setPlayer(mExoPlayer);

                // set exo listener to this activity
                mExoPlayer.addListener(this);
                // Prepare the MediaSource.
                String userAgent = Util.getUserAgent(this, "MyRecipe");
                MediaSource mediaSource = new ExtractorMediaSource(videoUri, new DefaultDataSourceFactory(
                        this, userAgent), new DefaultExtractorsFactory(), null, null);

                if (mPlayerPosition != -1) {
                    mExoPlayer.seekTo(mPlayerPosition);
                }

                mExoPlayer.prepare(mediaSource);
                mExoPlayer.setPlayWhenReady(true);
            }
        }
    }

    /**
     * Helper void that will initialize the MediaSession to keep control
     * internal or external and keep media sync
     */
    private void initializeMediaSession() {
        // Create Media Session
        mMediaSession = new MediaSessionCompat(this, TAG);
        // Enable Callbacks
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);

    }

    /**
     * Helper Method to handle Display Step Thumbinal
     */
    private void displayStepThumbinal() {
        String thumbinalUrl = mStepsModels.get(mModelPosition).getThumbnailURL();
        if (thumbinalUrl.length() > 0) {
            mThumbnailImageView.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(thumbinalUrl)
                    .into(mThumbnailImageView);
        } else {
            mThumbnailImageView.setVisibility(View.GONE);
        }
    }


    /**
     * Helper Method that control Release Resources
     * that used by ExoPlayer
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mExoPlayer != null) {
            mExoPlayer.seekTo(mPlayerPosition);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            if (!isInPictureMode()) {
                mExoPlayer.setPlayWhenReady(false);
            }
        }
        if (isInPictureMode()) {
            mExoPlayerView.setUseController(false);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mExoPlayer != null) {
            releasePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mExoPlayer != null) {
            if (!isInPictureMode()) {
                if (videoUri != null) {
                    initializePlayer(videoUri);
                }
                //mExoPlayer.setPlayWhenReady(true);
            }
        }

    }

    private boolean isInPictureMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return isInPictureInPictureMode();
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        mMediaSession.setActive(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mExoPlayer != null) {
            mPlayerPosition = mExoPlayer.getCurrentPosition();
        }
        outState.putLong(POSITION_STATE, mPlayerPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mPlayerPosition = savedInstanceState.getLong(POSITION_STATE);
        }
    }

    /**
     * to check up if we are in landscape mode
     *
     * @return
     */
    private boolean isLandScapeMode() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

    }

    @OnClick(R.id.button_next)
    @Optional
    public void nextVideo(View view) {
        if (mModelPosition + 1 < mStepsModels.size()) {
            mNextButton.setEnabled(true);
            mModelPosition++;
            Bundle bundle = new Bundle();
            bundle.putInt(DetailActivity.EXTRAS_MODEL_POSITION, mModelPosition);
            bundle.putParcelableArrayList(RecipeCatalog.EXTRAS_STEPS_ARRAYLIST, mStepsModels);
            bundle.putParcelableArrayList(RecipeCatalog.EXTRAS_INGREDIENTS_ARRAYLIST, mIngredientsModel);
            bundle.putString(RecipeCatalog.EXTRAS_RECIPE_NAME, mRecipeName);
            Intent nextVideoIntent = new Intent(DisplayActivity.this, DisplayActivity.class);
            nextVideoIntent.putExtras(bundle);
            startActivity(nextVideoIntent);
            finish();
        } else {
            mNextButton.setEnabled(false);
        }
    }

    @OnClick(R.id.button_prev)
    @Optional
    public void prevVideo(View view) {
        if (mModelPosition - 1 > -1) {
            mPrevButton.setEnabled(true);
            mModelPosition--;
            Bundle bundle = new Bundle();
            bundle.putInt(DetailActivity.EXTRAS_MODEL_POSITION, mModelPosition);
            bundle.putParcelableArrayList(RecipeCatalog.EXTRAS_STEPS_ARRAYLIST, mStepsModels);
            bundle.putParcelableArrayList(RecipeCatalog.EXTRAS_INGREDIENTS_ARRAYLIST, mIngredientsModel);
            bundle.putString(RecipeCatalog.EXTRAS_RECIPE_NAME, mRecipeName);
            Intent nextVideoIntent = new Intent(DisplayActivity.this, DisplayActivity.class);
            nextVideoIntent.putExtras(bundle);
            startActivity(nextVideoIntent);
            finish();
        } else {
            mPrevButton.setEnabled(false);
        }
    }

    private void backToParent() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RecipeCatalog.EXTRAS_STEPS_ARRAYLIST, mStepsModels);
        bundle.putParcelableArrayList(RecipeCatalog.EXTRAS_INGREDIENTS_ARRAYLIST, mIngredientsModel);
        bundle.putString(RecipeCatalog.EXTRAS_RECIPE_NAME, mRecipeName);
        Intent intent = new Intent(DisplayActivity.this, DetailActivity.class);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToParent();
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        // Control Progress Bar Behavior
        if (playbackState == ExoPlayer.STATE_BUFFERING) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    /**
     * Helper Method that take care of handle full screen when we go to landscape mode
     *
     * @param config
     */
    private void adjustFullScreen(Configuration config) {
        final View decorView = getWindow().getDecorView();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adjustFullScreen(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            adjustFullScreen(getResources().getConfiguration());
        }
    }

    @Override
    protected void onUserLeaveHint() {
        minimize();
    }

    void minimize() {
        if (mExoPlayerView == null) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            PictureInPictureParams.Builder mPipBuilder = new PictureInPictureParams.Builder();
            Rational aspectRatio = new Rational(mExoPlayerView.getWidth(),
                    mExoPlayerView.getHeight());
            mPipBuilder.setAspectRatio(aspectRatio);
            enterPictureInPictureMode(mPipBuilder.build());
        }

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

}
