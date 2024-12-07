package Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RapCodeTechnologies.Quiz.R;
import com.bumptech.glide.Glide;

import java.util.List;

import Models.BannerItem;


public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<BannerItem> banners;
    private OnBannerClickListener onBannerClickListener;

    public BannerAdapter(List<BannerItem> banners,OnBannerClickListener onBannerClickListerner) {
        this.banners = banners;
        this.onBannerClickListener=onBannerClickListerner;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.bind(banners.get(position),onBannerClickListener);

    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    public interface OnBannerClickListener {
        void onBannerClick(BannerItem bannerItem);
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout rootLayout;
        private final TextView titleTextView;
        private final TextView discountTextView;
        private final Button getButton;
        private final ImageView bannerImageView;

        public BannerViewHolder(View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            bannerImageView = itemView.findViewById(R.id.bannerImageView);
            discountTextView = itemView.findViewById(R.id.discountTextView);
            getButton = itemView.findViewById(R.id.getButton);
        }

        public void bind(BannerItem banner,final OnBannerClickListener onBannerClickListener) {
            titleTextView.setText(banner.getTitle());
            discountTextView.setText(banner.getDiscountText());
            getButton.setText(banner.getButtonText());
            rootLayout.setBackgroundColor(banner.getBackgroundColor());
            Glide.with(bannerImageView.getContext())
                    .load(banner.getIamgeview())
                    .into(bannerImageView);


            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBannerClickListener.onBannerClick(banner);
                }
            };
            itemView.setOnClickListener(clickListener);

            getButton.setOnClickListener(clickListener);
 }
}
}
