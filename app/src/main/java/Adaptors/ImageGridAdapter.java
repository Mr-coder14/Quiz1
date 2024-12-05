package Adaptors;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.RapCodeTechnologies.Quiz.R;
import com.google.android.material.imageview.ShapeableImageView;

public class ImageGridAdapter extends BaseAdapter {
    private Context context;
    private int[] imageResources;

    public ImageGridAdapter(Context context, int[] imageResources) {
        this.context = context;
        this.imageResources = imageResources;
    }

    @Override
    public int getCount() {
        return imageResources.length;
    }

    @Override
    public Object getItem(int position) {
        return imageResources[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.grid_item_image, parent, false);
        }

        ShapeableImageView imageView = convertView.findViewById(R.id.imageViewItem);
        imageView.setImageResource(imageResources[position]);

        return convertView;
    }
}
