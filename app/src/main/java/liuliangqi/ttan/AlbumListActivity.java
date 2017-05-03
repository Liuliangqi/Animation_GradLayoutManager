package liuliangqi.ttan;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AlbumListActivity extends Activity {
    @Bind(R.id.album_list)
    RecyclerView mAlbumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);

        ButterKnife.bind(this);
        populate();
    }

    interface OnViewHolderClickListener {
        void onViewHolderClicked(AlbumViewHolder viewHolder);
    }
    static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final OnViewHolderClickListener mListener;
        @Bind(R.id.album_art)
        ImageView albumArt;

        public AlbumViewHolder(View itemView, OnViewHolderClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mListener = listener;
        }

        @Override
        public void onClick(View view) {
            // 为了将点击事件的处理逻辑转移
            mListener.onViewHolderClicked(this);
        }
    }


    private void populate() {
        // 让list显示在两列，每列是vertical的方向
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mAlbumList.setLayoutManager(lm);

        final int[] albumArts = {
                R.drawable.mean_something_kinder_than_wolves,
                R.drawable.cylinders_chris_zabriskie,
                R.drawable.broken_distance_sutro,
                R.drawable.playing_with_scratches_ruckus_roboticus,
                R.drawable.keep_it_together_guster,
                R.drawable.the_carpenter_avett_brothers,
                R.drawable.please_sondre_lerche,
                R.drawable.direct_to_video_chris_zabriskie
        };


        // 这里将view holder作为原型传入
        RecyclerView.Adapter adapter = new RecyclerView.Adapter<AlbumViewHolder>() {
            @Override
            public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View albumView = getLayoutInflater().inflate(R.layout.album_grid_item, parent, false);
                return new AlbumViewHolder(albumView, new OnViewHolderClickListener() {
                    @Override
                    public void onViewHolderClicked(AlbumViewHolder viewHolder) {
                        // viewholder 可以返回被点击的是哪个
                        int albumArtResId = albumArts[viewHolder.getPosition() % albumArts.length];
                        Intent intent = new Intent(AlbumListActivity.this, AlbumDetailActivity.class);
                        intent.putExtra(AlbumDetailActivity.EXTRA_ALBUM_ART_RESID, albumArtResId);

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                          AlbumListActivity.this, viewHolder.albumArt, "albumArt");
                        startActivity(intent, options.toBundle());
                    }
                });
            }

            @Override
            public void onBindViewHolder(AlbumViewHolder holder, int position) {
                holder.albumArt.setImageResource(albumArts[position % albumArts.length]);
            }

            @Override
            public int getItemCount() {
                return albumArts.length * 4;
            }
        };

        mAlbumList.setAdapter(adapter);

    }
}
