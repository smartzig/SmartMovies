package movies.smartzig.com.smartmovies.details;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import movies.smartzig.com.smartmovies.R;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {


    private final ArrayList<Reviews> mReviews;
    private final OnItemClickListener onItemClickListener;

    ReviewAdapter(ArrayList<Reviews> reviews, OnItemClickListener listener) {
        mReviews = reviews;
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reviews_list_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Reviews review = mReviews.get(position);

        holder.mReview = review;
        holder.mContentView.setText(review.getContent());
        holder.mAuthorView.setText(review.getAuthor());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.read_reviews(review, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public void add(List<Reviews> reviews) {
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public ArrayList<Reviews> getReviews() {
        return mReviews;
    }

    public interface OnItemClickListener {
        void read_reviews(Reviews review, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        @BindView(R.id.review_content)
        TextView mContentView;
        @BindView(R.id.review_author)
        TextView mAuthorView;
        Reviews mReview;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }
}
