package com.nloops.myrecipe.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.nloops.myrecipe.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class will be the definition of Recipe Fragment that display the video
 * when we are using tablet.
 */

public class RecipeFragment extends Fragment implements
        ExoPlayer.EventListener {

    @BindView(R.id.fragment_exo_simple_player)
    SimpleExoPlayerView mExoSimplePlayer;
    @BindView(R.id.fragment_tv_display_activity)
    TextView mDescriptionTextView;
    @BindView(R.id.fragment_tv_no_video)
    TextView mNoVideoTextView;
    @BindView(R.id.fragment_exo_progress_bar)
    ProgressBar mProgressBar;
    @Nullable
    @BindView(R.id.iv_fragment_thumbnail)
    ImageView mThumbinalFragmentView;
    private SimpleExoPlayer mExoPlayer;
    private String mVideoURL;
    private String mDescription;
    private String mThumbinalURL;
    private long mPlayerPosition;
    private Uri videoUri;
    private Context mContext;
    private String STATE_POSITON = "EXO.PLAYER.STATE";
    private String VIDEO_URL = "com.nloops.myrecipe.EXTRAS.video.url";
    private String EXTRAS_DESCRIPTION = "com.nloops.myrecipe.EXTRAS.description";
    private String THUMBINAL_URL = "com.nloops.myrecipe.EXTRAS.thumbinal.url";

    // Empty Constructor
    public RecipeFragment() {
    }

    // inflate our xml layout
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tablet_detail_layout_fragment, container, false);
        if (savedInstanceState != null) {
            mVideoURL = savedInstanceState.getString(VIDEO_URL);
            mDescription = savedInstanceState.getString(EXTRAS_DESCRIPTION);
            mPlayerPosition = savedInstanceState.getLong(STATE_POSITON);
            mThumbinalURL = savedInstanceState.getString(THUMBINAL_URL);
        }
        mContext = container.getContext();
        ButterKnife.bind(this, rootView);
        if (mVideoURL.length() > 0) {
            mNoVideoTextView.setVisibility(View.INVISIBLE);
            videoUri = Uri.parse(mVideoURL);
        } else {
            mNoVideoTextView.setVisibility(View.VISIBLE);
        }
        Typeface txtFont = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Doomsday_Light.ttf");
        mDescriptionTextView.setText(mDescription);
        mDescriptionTextView.setTypeface(txtFont);
        displayThumbinalImage(mContext);
        initializePlayer(videoUri, mContext);
        return rootView;

    }


    /**
     * Initialize ExoPlayer
     *
     * @param videoUri
     */
    private void initializePlayer(Uri videoUri, Context context) {
        if (videoUri != null) {
            if (mExoPlayer == null) {
                // Create an instance of the ExoPlayer.
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
                mExoSimplePlayer.setPlayer(mExoPlayer);

                // add listener
                mExoPlayer.addListener(this);

                // Prepare the MediaSource.
                String userAgent = Util.getUserAgent(context, "MyRecipe");
                MediaSource mediaSource = new ExtractorMediaSource(videoUri, new DefaultDataSourceFactory(
                        context, userAgent), new DefaultExtractorsFactory(), null, null);
                if (mPlayerPosition != -1) {
                    mExoPlayer.seekTo(mPlayerPosition);
                }
                mExoPlayer.prepare(mediaSource);
                mExoPlayer.setPlayWhenReady(true);
            }
        }
    }

    public void setVideoURL(String videoURL) {
        this.mVideoURL = videoURL;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public void setThumbinalURL(String thumbinalURL) {
        this.mThumbinalURL = thumbinalURL;
    }

    private void displayThumbinalImage(Context context) {
        if (mThumbinalURL.length() > 0) {
            mThumbinalFragmentView.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(mThumbinalURL)
                    .into(mThumbinalFragmentView);
        } else {
            mThumbinalFragmentView.setVisibility(View.GONE);
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
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mPlayerPosition = mExoPlayer.getCurrentPosition();
            releasePlayer();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoUri != null) {
            initializePlayer(videoUri, mContext);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(STATE_POSITON, mPlayerPosition);
        outState.putString(EXTRAS_DESCRIPTION, mDescription);
        outState.putString(VIDEO_URL, mVideoURL);
        outState.putString(THUMBINAL_URL, mThumbinalURL);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mPlayerPosition = savedInstanceState.getLong(STATE_POSITON);
        }
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
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }
}
