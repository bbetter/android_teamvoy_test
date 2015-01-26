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
public class CrewListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Movie.Details.CrewPerson> crew;
    public CrewListAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }
    public void setCrew(List<Movie.Details.CrewPerson> crew){
        this.crew=crew;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return crew.size();
    }

    @Override
    public Object getItem(int position) {
        return crew.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tvDepartmentNJob;
        TextView tvName;
        ImageView ivImage;

        if(convertView==null){
            convertView=inflater.inflate(R.layout.crew_row,parent,false);
            tvName = (TextView)convertView.findViewById(R.id.tvCrewName);
            tvDepartmentNJob=(TextView)convertView.findViewById(R.id.tvCrewDepartmentJob);
            ivImage=(ImageView)convertView.findViewById(R.id.crewImage);
            convertView.setTag(new CrewViewHolder(ivImage,tvDepartmentNJob,tvName));
        }
        else{
            CrewViewHolder crewViewHolder =(CrewViewHolder)convertView.getTag();
            ivImage= crewViewHolder.ivImage;
            tvName= crewViewHolder.tvCrewName;
            tvDepartmentNJob= crewViewHolder.tvDepartmentNJob;
        }
        Movie.Details.CrewPerson person=crew.get(position);
        tvName.setText(person.getName());
        tvDepartmentNJob.setText(person.getDepartment()+"\n'"+person.getJob()+"'");
        ImageLoader.getInstance().displayImage(Movie.transformPathToURL(person.getProfile_path(), Movie.ImageSize.W300),ivImage);
        return convertView;
    }
    static class CrewViewHolder {
        public final TextView tvDepartmentNJob;
        public final TextView tvCrewName;
        public final ImageView ivImage;

        public CrewViewHolder(ImageView image, TextView title, TextView text) {
            this.tvCrewName =text;
            this.tvDepartmentNJob =title;
            this.ivImage=image;
        }
    }
}
