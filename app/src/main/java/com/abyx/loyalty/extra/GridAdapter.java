package com.abyx.loyalty.extra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abyx.loyalty.R;
import com.abyx.loyalty.tasks.ThumbnailImageTask;
import com.abyx.loyalty.contents.Card;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<Card> data;

    public GridAdapter(Context context, List<Card> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Card getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.single_grid, null);
        }
        TextView textView = (TextView) view.findViewById(R.id.textView);
        TextView barcodeView = (TextView) view.findViewById(R.id.barcodeView);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        textView.setText(data.get(i).getName());
        barcodeView.setText(data.get(i).getBarcode());
        new ThumbnailImageTask(imageView, context, data.get(i).getImageLocation(), data.get(i)).execute(data.get(i).getImageURL());
        return view;
    }

    public void refresh(List<Card> items) {
        this.data = items;
        notifyDataSetChanged();
    }
}
