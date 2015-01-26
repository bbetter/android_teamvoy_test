package com.example.andriypuhach.android_teamvoy_test.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by andriypuhach on 26.01.15.
 */
public class CastListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Movie.Details.CastPerson> cast;
    public CastListAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }
    public void setCast(List<Movie.Details.CastPerson> cast){
        this.cast=cast;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return cast.size();
    }

    @Override
    public Object getItem(int position) {
        return cast.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tvCharacter;
        TextView tvName;
        ImageView ivImage;

        if(convertView==null){
            convertView=inflater.inflate(R.layout.cast_row,parent,false);
            tvName = (TextView)convertView.findViewById(R.id.tvCastName);
            tvCharacter=(TextView)convertView.findViewById(R.id.tvCastCharacter);
            ivImage=(ImageView)convertView.findViewById(R.id.castImage);
            convertView.setTag(new CastViewHolder(ivImage,tvCharacter,tvName));
        }
        else{
            CastViewHolder noteViewHolder=(CastViewHolder)convertView.getTag();
            ivImage=noteViewHolder.ivImage;
            tvName=noteViewHolder.tvCastName;
            tvCharacter=noteViewHolder.tvCastCharacter;
        }
        Movie.Details.CastPerson person=cast.get(position);
        tvName.setText(person.getName());
        tvCharacter.setText(person.getCharacter());
        ImageLoader.getInstance().displayImage(Movie.transformPathToURL(person.getProfile_path(), Movie.ImageSize.W300),ivImage);
        return convertView;
    }
    static class CastViewHolder {
        public final TextView tvCastCharacter;
        public final TextView tvCastName;
        public final ImageView ivImage;

        public CastViewHolder(ImageView image, TextView title, TextView text) {
            this.tvCastName =text;
            this.tvCastCharacter =title;
            this.ivImage=image;
        }
    }
}
