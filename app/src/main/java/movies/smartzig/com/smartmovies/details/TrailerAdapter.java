package movies.smartzig.com.smartmovies.details;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import movies.smartzig.com.smartmovies.R;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private final static String LOG_TAG = TrailerAdapter.class.getSimpleName();

    private final ArrayList<Trailers> mTrailers;
    private final OnItemClickListener mOnItemClickListener;

    TrailerAdapter(ArrayList<Trailers> trailers, OnItemClickListener listener) {
        mTrailers = trailers;
        mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_list_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Trailers trailer = mTrailers.get(position);
        final Context context = holder.mView.getContext();

        float paddingLeft = 0;
        if (position == 0) {
            paddingLeft = context.getResources().getDimension(R.dimen.detail_horizontal_padding);
        }

        float paddingRight = 0;
        if (position + 1 != getItemCount()) {
            paddingRight = context.getResources().getDimension(R.dimen.detail_horizontal_padding) / 2;
        }

        holder.mView.setPadding((int) paddingLeft, 0, (int) paddingRight, 0);

        holder.mTrailer = trailer;

        String thumbnailUrl = "http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg";
        Log.i(LOG_TAG, "thumbnailUrl -> " + thumbnailUrl);

        Picasso.get()
                .load(thumbnailUrl)
                .config(Bitmap.Config.RGB_565)
                .into(holder.mThumbnailView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.watch_trailer(trailer, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public void add(List<Trailers> trailers) {
        mTrailers.clear();
        mTrailers.addAll(trailers);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void watch_trailer(Trailers trailers, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        Trailers mTrailer;
        @BindView(R.id.trailer_thumbnail)
        ImageView mThumbnailView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }
}
