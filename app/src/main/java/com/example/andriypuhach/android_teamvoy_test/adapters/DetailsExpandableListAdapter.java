package com.example.andriypuhach.android_teamvoy_test.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by andriypuhach on 26.01.15.
 */
public class DetailsExpandableListAdapter extends BaseExpandableListAdapter {

    private final static int VIEW_TYPE_INFO = 0;
    private final static int VIEW_TYPE_REVIEW=4;
    public final static int VIEW_TYPE_NOTES = 5;
    private final static int VIEW_TYPE_CAST = 1;
    private final static int VIEW_TYPE_CREW = 2;
    private final static int VIEW_TYPE_VIDEO = 3;
    public static YouTubeThumbnailLoader [] loaders;

    private final String [] headers={"Інформація","Знімались","Знімали","Відео","Рецензії","Нотатки"};
    private LayoutInflater inflater;
    private Context context;
    private Movie movie;

    public void setMovie(Movie mv){
        movie=mv;
        loaders= new YouTubeThumbnailLoader[movie.getDetails().getVideosWrapper().getVideos().size()];
        notifyDataSetChanged();
    }
    public DetailsExpandableListAdapter(Activity activity,Movie mv){
        context=activity.getApplicationContext();
        inflater=LayoutInflater.from(context);
        setMovie(mv);
    }
    @Override
    public int getGroupCount() {
        return headers.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        switch(groupPosition){
            case VIEW_TYPE_CAST:return movie.getDetails().getCredits().getCast().size();
            case VIEW_TYPE_CREW:return movie.getDetails().getCredits().getCrew().size();
            case VIEW_TYPE_NOTES:return movie.getDetails().getNotes().size();
            case VIEW_TYPE_VIDEO:return movie.getDetails().getVideosWrapper().getVideos().size();
            case VIEW_TYPE_REVIEW:return movie.getDetails().getReviewWrapper().getReviews().size();
            default:return 1;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headers[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String header=(String)getGroup(groupPosition);
        if(convertView==null){
            convertView=inflater.inflate(R.layout.group_item,parent,false);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(header);
        return convertView;
    }

    public void releaseLoader(){
        for(YouTubeThumbnailLoader loader :loaders){
            if(loader!=null)
            loader.release();
        }
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        switch (groupPosition) {

                case VIEW_TYPE_INFO: {
                    if(convertView==null || ((ViewHolder)convertView.getTag()).tvBudget==null) {
                        convertView = inflater.inflate(R.layout.details_row_info, parent, false);
                        holder.tvBudget = (TextView) convertView.findViewById(R.id.tvBudget);
                        holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                        holder.tvCompanies = (TextView) convertView.findViewById(R.id.tvCompany);
                        holder.tvGenres = (TextView) convertView.findViewById(R.id.tvGenre);
                        holder.tvHomePage = (TextView) convertView.findViewById(R.id.tvHomePage);
                        holder.tvOverview = (TextView) convertView.findViewById(R.id.tvOverview);
                        holder.tvRevenue = (TextView) convertView.findViewById(R.id.tvRevenue);
                        holder.tvTagLine = (TextView) convertView.findViewById(R.id.tvTagline);
                        holder.tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
                        holder.tvReleaseDate = (TextView) convertView.findViewById(R.id.tvYear);
                    }
                    else{
                        holder=(ViewHolder)convertView.getTag();
                    }
                }
                break;
                case VIEW_TYPE_NOTES: {

                    if(convertView==null || ((ViewHolder)convertView.getTag()).tvNoteTitle==null){
                        convertView=inflater.inflate(R.layout.note_row,parent,false);
                        holder.tvNoteText = (TextView)convertView.findViewById(R.id.tvNoteText);
                        holder.tvNoteTitle=(TextView)convertView.findViewById(R.id.tvNoteTitle);
                        holder.ivNoteImage=(ImageView)convertView.findViewById(R.id.noteImage);
                    }
                    else{
                       holder=(ViewHolder)convertView.getTag();
                    }
                }
                break;
                case VIEW_TYPE_CAST:{
                    if(convertView==null || ((ViewHolder)convertView.getTag()).tvCastCharacter==null) {
                        convertView = inflater.inflate(R.layout.cast_row, parent, false);
                        holder.tvCastCharacter=(TextView)convertView.findViewById(R.id.tvCastCharacter);
                        holder.tvCastName=(TextView)convertView.findViewById(R.id.tvCastName);
                        holder.ivCastImage=(ImageView)convertView.findViewById(R.id.castImage);
                    }
                    else{
                        holder=(ViewHolder)convertView.getTag();
                    }
                }
                break;
                case VIEW_TYPE_CREW:{
                    if(convertView==null || ((ViewHolder)convertView.getTag()).tvCrewDepartmentNJob==null) {
                        convertView = inflater.inflate(R.layout.crew_row, parent, false);
                        holder.tvCrewDepartmentNJob=(TextView)convertView.findViewById(R.id.tvCrewDepartmentJob);
                        holder.tvCrewName=(TextView)convertView.findViewById(R.id.tvCrewName);
                        holder.ivCrewImage=(ImageView)convertView.findViewById(R.id.crewImage);
                    }
                    else{
                        holder=(ViewHolder)convertView.getTag();
                    }
                }
                break;
                case VIEW_TYPE_VIDEO:{
                    if(convertView==null || ((ViewHolder)convertView.getTag()).youTubeThumbnailView==null){
                        convertView=inflater.inflate(R.layout.video_row,parent,false);
                        holder.youTubeThumbnailView=(YouTubeThumbnailView)convertView.findViewById(R.id.ytThumbnailView);
                        holder.tvVideoTitle=(TextView) convertView.findViewById(R.id.tvVideoTitle);
                    }
                    else{
                        holder=(ViewHolder)convertView.getTag();
                    }

                }
                break;
                case VIEW_TYPE_REVIEW:{
                    if(convertView==null || ((ViewHolder)convertView.getTag()).tvReviewAuthor==null){
                        convertView=inflater.inflate(R.layout.review_row,parent,false);
                        holder.tvReviewAuthor=(TextView)convertView.findViewById(R.id.tvReviewAuthor);
                        holder.tvReviewText=(TextView) convertView.findViewById(R.id.tvReviewText);
                        holder.ivReviewImage=(ImageView)convertView.findViewById(R.id.reviewImage);
                    }
                    else{
                        holder=(ViewHolder)convertView.getTag();
                    }
                }
                break;
            }
            convertView.setTag(holder);

        switch(groupPosition){

            case VIEW_TYPE_INFO:
                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                holder.tvTitle.setText(movie.getOriginal_title());
                holder.tvOverview.setText(movie.getDetails().getOverview());
                holder.tvTagLine.setText(movie.getDetails().getTagline());
                holder.tvHomePage.setText(movie.getDetails().getHomepage());
                holder.tvReleaseDate.setText(movie.getRelease_date().toLocalDate().toString());
                holder.tvBudget.setText(formatter.format(movie.getDetails().getBudget()));
                holder.tvRevenue.setText(formatter.format(movie.getDetails().getRevenue()));
                holder.tvStatus.setText(movie.getDetails().getStatus());
                holder.tvGenres.setText(movie.getDetails().getGenresCommaSeparatedList());
                holder.tvCompanies.setText(movie.getDetails().getCompaniesCommaSeparatedList());
                break;
            case VIEW_TYPE_NOTES:
                List<Movie.Details.Note> notes=movie.getDetails().getNotes();
                holder.tvNoteTitle.setText(notes.get(childPosition).getNoteTitle());
                holder.tvNoteText.setText(notes.get(childPosition).getNoteText());
                Picasso.with(context).load("file:///"+notes.get(childPosition).getImagePath()).error(R.drawable.failed_to_load).into(holder.ivNoteImage);
                break;
            case VIEW_TYPE_CAST:{
                List<Movie.Details.Credits.CastPerson> cast=movie.getDetails().getCredits().getCast();
                if(cast.size()>0) {
                    holder.tvCastCharacter.setText(cast.get(childPosition).getCharacter());
                    holder.tvCastName.setText(cast.get(childPosition).getName());
                    Picasso.with(context).load(Movie.transformPathToURL(cast.get(childPosition).getProfile_path(), Movie.ImageSize.W75)).skipMemoryCache().error(R.drawable.failed_to_load).into(holder.ivCastImage);
                }
            }
            break;
            case VIEW_TYPE_CREW:{
                List<Movie.Details.Credits.CrewPerson> crew=movie.getDetails().getCredits().getCrew();
                if(crew.size()>0) {
                    String dep = crew.get(childPosition).getDepartment();
                    String job = crew.get(childPosition).getJob();
                    holder.tvCrewDepartmentNJob.setText(dep + "\n'" + job + "'");
                    holder.tvCrewName.setText(crew.get(childPosition).getName());
                    Picasso.with(context).load(Movie.transformPathToURL(crew.get(childPosition).getProfile_path(), Movie.ImageSize.W75)).skipMemoryCache().error(R.drawable.failed_to_load).into(holder.ivCrewImage);
                }

            }
            break;
            case VIEW_TYPE_VIDEO:{
                if(movie.getDetails().getVideosWrapper().getVideos().size()>0) {
                    final Movie.Details.Videos.Video video = movie.getDetails().getVideosWrapper().getVideos().get(childPosition);
                    holder.tvVideoTitle.setText(video.getName());
                    holder.youTubeThumbnailView.initialize(Movie.Details.Videos.Video.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                            loaders[childPosition]=youTubeThumbnailLoader;

                          //  youTubeThumbnailLoader.release();
                            youTubeThumbnailLoader.setVideo(video.getKey());

                        }

                        @Override
                        public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                            youTubeThumbnailView.setImageResource(R.drawable.failed_to_load);

                        }
                    });
                }
            }
            break;
            case VIEW_TYPE_REVIEW:{
                List<Movie.Details.Reviews.Review> reviews=movie.getDetails().getReviewWrapper().getReviews();
                if(reviews.size()>0) {
                    String author = reviews.get(childPosition).getAuthor();
                    String text = reviews.get(childPosition).getContent();
                    holder.tvReviewAuthor.setText(author);
                    holder.tvReviewText.setText(text);
                    Picasso.with(context).load(Movie.transformPathToURL(movie.getPoster_path(), Movie.ImageSize.W75)).error(R.drawable.failed_to_load).into(holder.ivReviewImage);
                }
                break;

            }
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return groupPosition == VIEW_TYPE_NOTES || groupPosition == VIEW_TYPE_VIDEO || groupPosition == VIEW_TYPE_REVIEW;
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvStatus;
        TextView tvReleaseDate;
        TextView tvTagLine;
        TextView tvHomePage;
        TextView tvBudget;
        TextView tvRevenue;
        TextView tvGenres;
        TextView tvCompanies;
        TextView tvOverview;
        TextView tvNoteTitle;
        TextView tvNoteText;
        ImageView ivNoteImage;

        TextView tvCastCharacter;
        TextView tvCastName;
        ImageView ivCastImage;

        TextView tvCrewDepartmentNJob;
        TextView tvCrewName;
        ImageView ivCrewImage;

        TextView tvReviewAuthor;
        TextView tvReviewText;
        ImageView ivReviewImage;

        TextView tvVideoTitle;
        YouTubeThumbnailView youTubeThumbnailView;

    }
}
