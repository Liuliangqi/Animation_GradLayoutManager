package liuliangqi.ttan;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuliangqi on 2017/5/1.
 */

public class AlbumDetailActivity extends Activity{

    public static final String EXTRA_ALBUM_ART_RESID = "EXTRA_ALBUM_ART_RESID";
    private TransitionManager mTransitionManager;
    private Scene mCurrentScene;
    private Scene mExpandedScene;
    private Scene mCollapsedScene;

    @Bind(R.id.album_art) ImageView albumArtView;
    @Bind(R.id.fab)
    ImageButton fab;
    @Bind(R.id.title_panel)
    ViewGroup titlePanel;
    @Bind(R.id.track_panel) ViewGroup trackPanel;
    @Bind(R.id.detail_container) ViewGroup detailContainer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        ButterKnife.bind(this);
        populate();
        setupTransitions();
    }


    @OnClick(R.id.track_panel)
    public void onTrackPanelClicked(View view) {
        if(mCurrentScene == mExpandedScene){
            mCurrentScene = mCollapsedScene;
        }else{
            mCurrentScene = mExpandedScene;
        }

        mTransitionManager.transitionTo(mCurrentScene);
    }

    private void setupTransitions(){
        Slide slide = new Slide(Gravity.BOTTOM);
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        getWindow().setEnterTransition(slide);
        getWindow().setSharedElementsUseOverlay(false);
        mTransitionManager = new TransitionManager();
        // 需要指定root
        ViewGroup transitionRoot = detailContainer;

        mExpandedScene = Scene.getSceneForLayout(transitionRoot,
                R.layout.activity_album_detail_expand, this);

        mExpandedScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                // 绑定目标activity
                ButterKnife.bind(AlbumDetailActivity.this);
                populate();
                mCurrentScene = mExpandedScene;
            }
        });

        TransitionSet expandTransitionSet = new TransitionSet();
        expandTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        ChangeBounds changeBounds = new ChangeBounds();
        expandTransitionSet.setDuration(200);
        expandTransitionSet.addTransition(changeBounds);

        Fade fadeLyrics = new Fade();
        fadeLyrics.addTarget(R.id.lyrics);
        fadeLyrics.setDuration(150);
        expandTransitionSet.addTransition(fadeLyrics);


        // collapsed scene
        mCollapsedScene = Scene.getSceneForLayout(transitionRoot, R.layout.activity_album_detail, this);
        mCollapsedScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                ButterKnife.bind(AlbumDetailActivity.this);
                populate();
                mCurrentScene = mCollapsedScene;
            }
        });

        TransitionSet collapseTransitionSet = new TransitionSet();
        collapseTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        Fade fadeOutLyrics = new Fade();
        fadeOutLyrics.addTarget(R.id.lyrics);
        fadeOutLyrics.setDuration(150);
        collapseTransitionSet.addTransition(fadeOutLyrics);

        ChangeBounds resetBounds = new ChangeBounds();
        resetBounds.setDuration(200);
        collapseTransitionSet.addTransition(resetBounds);



        // 设置动画
        mTransitionManager.setTransition(mExpandedScene, mCollapsedScene, collapseTransitionSet);
        mTransitionManager.setTransition(mCollapsedScene, mExpandedScene, expandTransitionSet);
        mCollapsedScene.enter();
    }
    private void populate() {
        // 获取从 album list activity 里传过来的参数，默认是 mean_something_kinder_than_wolves
        int albumArtResId = getIntent().getIntExtra(EXTRA_ALBUM_ART_RESID, R.drawable.mean_something_kinder_than_wolves);

        albumArtView.setImageResource(albumArtResId);

        Bitmap albumBitmap = getReducedBitmap(albumArtResId);
        colorizeFromImage(albumBitmap);
    }

    private void colorizeFromImage(Bitmap albumBitmap) {
        Palette palette = Palette.from(albumBitmap).generate();

        int defaulstPanelColor = 0xFF808080;
        int defaultFabColor = 0xFFEEEEEE;

        titlePanel.setBackgroundColor(palette.getDarkVibrantColor(defaulstPanelColor));
        trackPanel.setBackgroundColor(palette.getLightMutedColor(defaultFabColor));

        int[][] states = new int[][]{
            new int[]{android.R.attr.state_enabled},
            new int[]{android.R.attr.state_pressed}
        };

        int[] color = new int[]{
                palette.getVibrantColor(defaultFabColor),
                palette.getLightVibrantColor(defaulstPanelColor)
        };

    }

    private Bitmap getReducedBitmap(int albumArtResId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // 读取图片时不分配内存但是可以获取图片信息
        options.inJustDecodeBounds = false;
        // 抽样， 宽高都是原来的 1／8
        options.inSampleSize = 8;
        return BitmapFactory.decodeResource(getResources(), albumArtResId, options);
    }


}
