package com.example.andriypuhach.android_teamvoy_test;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Джон on 17.01.2015.
 */
public class MovieDatabaseHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME="movieinfo.db";
    private final static int DATABASE_VERSION=1;

    private final String NOTES_TABLE_NAME ="Notes";
    private final String FAVORITES_TABLE_NAME="Favorites";
    private final String MOVIES_TABLE_NAME="Movies";
    //region Notes Columns
    private final String NOTE_MOVIE_ID_COLUMN ="movieID";
    private final String NOTE_TITLE_COLUMN="noteTitle";
    private final String NOTE_TEXT_COLUMN="noteText";
    private final String NOTE_IMAGEPATH_COLUMN="imagePath";
    private final String [] NOTE_COLUMNS ={NOTE_MOVIE_ID_COLUMN,NOTE_TITLE_COLUMN,NOTE_TEXT_COLUMN,NOTE_IMAGEPATH_COLUMN};
    //endregion
    //region Movies Columns
    private final String MOVIE_ID_COLUMN="movieID";
    private final String ORIGINAL_TITLE_COLUMN="originalTitle";
    private final String TITLE_COLUMN="title";
    private final String POSTER_PATH_COLUMN="posterPath";
    private final String RELEASE_DATE_COLUMN="releaseDate";
    private final String BUDGET_COLUMN="budget";
    private final String REVENUE_COLUMN="revenue";
    private final String HOMEPAGE_COLUMN="homePage";
    private final String POPULARITY_COLUMN="popularity";
    private final String STATUS_COLUMN="status";
    private final String VOTE_AVERAGE_COLUMN="voteAverage";
    private final String OVERVIEW_COLUMN="overview";
    private final String VIDEOS_COLUMN="videos_json";
    private final String REVIEWS_COLUMN="reviews_json";
    private final String CREDITS_COLUMN="credits_json";
    private final String GENRES_COLUMN="genres_json";
    private final String COMPANIES_COLUMN="companies_json";
    private final String IMAGES_COLUMN="images_json";
    private final String COUNTRIES_COLUMN="countries_json";
    private final String LANGUAGES_COLUMN="languages_json";

    private final String [] MOVIE_COLUMNS={MOVIE_ID_COLUMN,
            ORIGINAL_TITLE_COLUMN,TITLE_COLUMN,POSTER_PATH_COLUMN,
            RELEASE_DATE_COLUMN,BUDGET_COLUMN,REVENUE_COLUMN,
            HOMEPAGE_COLUMN,POPULARITY_COLUMN,STATUS_COLUMN,VOTE_AVERAGE_COLUMN,OVERVIEW_COLUMN,
            GENRES_COLUMN,COMPANIES_COLUMN,IMAGES_COLUMN,VIDEOS_COLUMN,REVIEWS_COLUMN,COUNTRIES_COLUMN,LANGUAGES_COLUMN,CREDITS_COLUMN};
    //endregion

    //region Favorites Columns
    private final String FAVORITES_MOVIE_ID_COLUMN="movieID";
    //endregion
    private final static String CREATE_TABLE_QUERY="CREATE TABLE <TABLE_NAME>(<CREATE_STAFF>)";
    private final static String INSERT_QUERY="INSERT INTO <TABLE_NAME> (<COLUMNS>) VALUES(<VALUES>)";
    private final static String UPDATE_QUERY="UPDATE <TABLE_NAME> SET <SETS>";
    private final static String DELETE_QUERY="DELETE FROM <TABLE_NAME> <CONDITION>";
    private final static String SELECT_QUERY="SELECT <COLUMNS> FROM <TABLE_NAME>";

    private String quotate(String str){
        if(str==null) return "' '";
        if(str.length()==0) return "' '";
        return "\'"+str.replace("'","''")+"\'";
    }
    public MovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
        String movieTableCreateQuery=CREATE_TABLE_QUERY
                .replace("<TABLE_NAME>",MOVIES_TABLE_NAME)
                .replace("<CREATE_STAFF>","_id integer primary key autoincrement,"
                +MOVIE_ID_COLUMN+" integer unique,"
                +ORIGINAL_TITLE_COLUMN+" text,"
                +TITLE_COLUMN+" text,"
                +POSTER_PATH_COLUMN+" text,"
                +RELEASE_DATE_COLUMN+" text,"
                +BUDGET_COLUMN +" float,"
                +REVENUE_COLUMN+" float,"
                +HOMEPAGE_COLUMN+" text,"
                +POPULARITY_COLUMN+" double,"
                +STATUS_COLUMN+" text,"
                +VOTE_AVERAGE_COLUMN+" double,"
                +OVERVIEW_COLUMN+" text,"
                +GENRES_COLUMN+" text,"
                +COMPANIES_COLUMN+" text,"
                +IMAGES_COLUMN+" text,"
                +VIDEOS_COLUMN+" text,"
                +REVIEWS_COLUMN+" text,"
                +COUNTRIES_COLUMN+" text,"
                +LANGUAGES_COLUMN+" text,"
                +CREDITS_COLUMN+" text"
                );

        String noteTableCreateQuery=CREATE_TABLE_QUERY
                .replace("<TABLE_NAME>", NOTES_TABLE_NAME)
                .replace("<CREATE_STAFF>", "_id integer primary key autoincrement,"
                        + NOTE_MOVIE_ID_COLUMN + " integer,"
                        + NOTE_TITLE_COLUMN + " text,"
                        + NOTE_TEXT_COLUMN + " text,"
                        + NOTE_IMAGEPATH_COLUMN
                        + " text,foreign key("
                        + NOTE_MOVIE_ID_COLUMN
                        + ") references "
                        + MOVIES_TABLE_NAME
                        + "(" + MOVIE_ID_COLUMN + ")");

        String favoriteTableCreateQuery=CREATE_TABLE_QUERY
                .replace("<TABLE_NAME>",FAVORITES_TABLE_NAME)
                .replace("<CREATE_STAFF>","_id integer primary key autoincrement,"
                         +FAVORITES_MOVIE_ID_COLUMN+" integer, foreign key("+FAVORITES_MOVIE_ID_COLUMN+") references "+MOVIES_TABLE_NAME+"("+MOVIE_ID_COLUMN+")");
        db.execSQL(movieTableCreateQuery);
        db.execSQL(noteTableCreateQuery);
        db.execSQL(favoriteTableCreateQuery);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertNote(Context context,Movie movie,Movie.Details.Note note){
        String [] movieValues={
                String.valueOf(movie.getId()),
                quotate(movie.getOriginal_title()),
                quotate(movie.getTitle()),
                quotate(movie.getPoster_path()),
                movie.getRelease_date()!=null?quotate(movie.getRelease_date().toLocalDate().toString()):null,
                String.valueOf(movie.getDetails().getBudget()),
                String.valueOf(movie.getDetails().getRevenue()),
                quotate(movie.getDetails().getHomepage()),
                String.valueOf(movie.getPopularity()),
                quotate(movie.getDetails().getStatus()),
                String.valueOf(movie.getVote_average()),
                quotate(movie.getDetails().getOverview()),
                quotate(movie.getDetails().getGenresJson()),
                quotate(movie.getDetails().getCompaniesJson()),
                quotate(movie.getDetails().getImages().getImagesJson()),
                quotate(movie.getDetails().getVideosJson()),
                quotate(movie.getDetails().getReviewsJson()),
                quotate(movie.getDetails().getCountriesJson()),
                quotate(movie.getDetails().getLanguagesJson()),
                quotate(movie.getDetails().getCreditsJson())
};
        String tryGetMovieQuery=SELECT_QUERY
                .replace("<TABLE_NAME>",MOVIES_TABLE_NAME)
                .replace("<COLUMNS>","*")
                .concat(" where movieID="+movie.getId());


        String [] noteValues ={String.valueOf(movie.getId()),
                quotate(note.getNoteTitle()),
                quotate(note.getNoteText()),
                note.getImagePath()!=null?quotate(note.getImagePath()):null
};

        String movieInsertQuery=INSERT_QUERY
                .replace("<TABLE_NAME>",MOVIES_TABLE_NAME)
                .replace("<COLUMNS>",Joiner.join(Arrays.asList(MOVIE_COLUMNS),','))
                .replace("<VALUES>",Joiner.join(Arrays.asList(movieValues),','));

        String query=INSERT_QUERY
                .replace("<TABLE_NAME>", NOTES_TABLE_NAME)
                .replace("<COLUMNS>",Joiner.join(Arrays.asList(NOTE_COLUMNS),','))
                .replace("<VALUES>",Joiner.join(Arrays.asList(noteValues),','));
        Cursor cursor=getReadableDatabase().rawQuery(tryGetMovieQuery, null);
        try {

            if(!cursor.moveToFirst()){
                getWritableDatabase().execSQL(movieInsertQuery);
            }

        }
        catch(Exception e){
            Log.w("TAG",e.getMessage());
            Toast.makeText(context,"При спробі додати нотатку сталась проблема,спробуйте ще раз",Toast.LENGTH_LONG).show();
        }
        finally {
            cursor.close();
        }
        getWritableDatabase().execSQL(query);
        Toast.makeText(context,"Нотатку додано",Toast.LENGTH_SHORT).show();

    }
    public void updateNote(Context context,Movie.Details.Note note){
        String query=UPDATE_QUERY
                .replace("<TABLE_NAME>", NOTES_TABLE_NAME)
                .replace("<SETS>",NOTE_TITLE_COLUMN+"="+quotate(note.getNoteTitle())
                        +","+NOTE_TEXT_COLUMN+"="+ quotate(note.getNoteText())
                        +","+NOTE_IMAGEPATH_COLUMN+"="+quotate(note.getImagePath())
                        +" where _id="+note.getId());
        this.getWritableDatabase().execSQL(query);
        Toast.makeText(context, "Нотатку відредаговано", Toast.LENGTH_SHORT).show();
    }
    public List<Movie.Details.Note> selectAllNotes() {
        String query = SELECT_QUERY
                .replace("<COLUMNS>", "*")
                .replace("<TABLE_NAME>", NOTES_TABLE_NAME);
        List<Movie.Details.Note> notes = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery(query, null);
        while (cursor.moveToNext()) {
            Movie.Details.Note note = new Movie.Details.Note();
            note.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            note.setNoteTitle(cursor.getString(2));
            note.setNoteText(cursor.getString(3));
            note.setImagePath(cursor.getString(4));
            notes.add(note);

        }
        cursor.close();
        this.close();
        return notes;
    }
    public Movie.Details.Note getNoteById(int id){
        String query = SELECT_QUERY
                .replace("<NOTE_COLUMNS>", "*")
                .replace("<NOTES_TABLE_NAME>", NOTES_TABLE_NAME)
        .concat("where _id="+id);
        Movie.Details.Note note = new Movie.Details.Note();
        Cursor cursor = this.getReadableDatabase().rawQuery(query, null);
        while (cursor.moveToNext()) {
            note.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            note.setNoteTitle(cursor.getString(2));
            note.setNoteText(cursor.getString(3));
            note.setImagePath(cursor.getString(4));
        }
        cursor.close();
        return note;
    }
    public List<Movie.Details.Note> selectNoteByMovieID(int movieID){
        String query = SELECT_QUERY
                .replace("<COLUMNS>", "*")
                .replace("<TABLE_NAME>", NOTES_TABLE_NAME)
                .concat(" where "+ NOTE_MOVIE_ID_COLUMN +"="+movieID);
        List<Movie.Details.Note> notes = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery(query, null);
        while (cursor.moveToNext()) {
            Movie.Details.Note note = new Movie.Details.Note();
            note.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            note.setNoteTitle(cursor.getString(2));
            note.setNoteText(cursor.getString(3));
            note.setImagePath(cursor.getString(4));
            notes.add(note);
        }
        cursor.close();
        this.close();
        return notes;

    }

    public void deleteNote(int id){
        String query = DELETE_QUERY
                .replace("<TABLE_NAME>", NOTES_TABLE_NAME)
                .replace("<CONDITION>","WHERE _id="+id);
        this.getWritableDatabase().execSQL(query);
        this.close();
    }

    public List<Movie> searchByNote(String title){
        String query= "select * from Movies mv join Notes nt on mv."+MOVIE_ID_COLUMN+"="+"nt."+NOTE_MOVIE_ID_COLUMN+" where nt."+NOTE_TITLE_COLUMN
                +" like '%"+ title.replace("%20"," ")+"%'";
        List<Movie> movies = new ArrayList<>();
        Cursor cursor=getReadableDatabase().rawQuery(query,null);
        try {
            while (cursor.moveToNext()) {

                Movie film = new Movie();
                film.setId(cursor.getInt(cursor.getColumnIndex(MOVIE_ID_COLUMN)));
                film.setOriginal_title(cursor.getString(cursor.getColumnIndex(ORIGINAL_TITLE_COLUMN)));
                film.setTitle(cursor.getString(cursor.getColumnIndex(TITLE_COLUMN)));
                film.setPoster_path(cursor.getString(cursor.getColumnIndex(POSTER_PATH_COLUMN)));
                film.setRelease_date(DateTime.parse(cursor.getString(cursor.getColumnIndex(RELEASE_DATE_COLUMN))));
                film.setPopularity(cursor.getDouble(cursor.getColumnIndex(POPULARITY_COLUMN)));
                film.setVote_average(cursor.getDouble(cursor.getColumnIndex(VOTE_AVERAGE_COLUMN)));
                Movie.Details details = new Movie.Details();
                details.setBudget(cursor.getFloat(cursor.getColumnIndex(BUDGET_COLUMN)));
                details.setRevenue(cursor.getFloat(cursor.getColumnIndex(REVENUE_COLUMN)));
                details.setStatus(cursor.getString(cursor.getColumnIndex(STATUS_COLUMN)));
                String imageJson= cursor.getString(cursor.getColumnIndex(IMAGES_COLUMN));
                String genresJson = cursor.getString(cursor.getColumnIndex(GENRES_COLUMN));
                String companyJson = cursor.getString(cursor.getColumnIndex(COMPANIES_COLUMN));
                String countryJson = cursor.getString(cursor.getColumnIndex(COUNTRIES_COLUMN));
                String languageJson = cursor.getString(cursor.getColumnIndex(LANGUAGES_COLUMN));
                String videoJson = cursor.getString(cursor.getColumnIndex(VIDEOS_COLUMN));
                String reviewJson = cursor.getString(cursor.getColumnIndex(REVIEWS_COLUMN));
                String creditJson=cursor.getString(cursor.getColumnIndex(CREDITS_COLUMN));
                Gson converter= new Gson();
                List<Movie.Details.Images.Backdrop> images=converter.fromJson(imageJson, new TypeToken<List<Movie.Details.Images.Backdrop>>(){}.getType());
                Movie.Details.Credits credits = converter.fromJson(creditJson, Movie.Details.Credits.class);
                List<Movie.Details.Country> countries = converter.fromJson(countryJson,new TypeToken<List<Movie.Details.Country>>(){}.getType());
                List<Movie.Details.Company> companies = converter.fromJson(companyJson,new TypeToken<List<Movie.Details.Company>>(){}.getType());
                List<Movie.Details.Genre> genres = converter.fromJson(genresJson,new TypeToken<List<Movie.Details.Genre>>(){}.getType());
                List<Movie.Details.Language> languages = converter.fromJson(languageJson,new TypeToken<List<Movie.Details.Language>>(){}.getType());
                Movie.Details.Reviews reviews = converter.fromJson(reviewJson,Movie.Details.Reviews.class);
                Movie.Details.Videos videos = converter.fromJson(videoJson,Movie.Details.Videos.class);
                Movie.Details.Images im = new Movie.Details.Images();
                im.setBackdrops(images);
                details.setImages(im);
                details.setReviews(reviews);
                details.setCredits(credits);
                details.setProduction_companies(companies);
                details.setGenres(genres);
                details.setSpoken_languages(languages);
                details.setProduction_countries(countries);
                details.setOverview(cursor.getString(cursor.getColumnIndex(OVERVIEW_COLUMN)));
                details.setVideosWrapper(videos);
                film.setDetails(details);
                movies.add(film);
            }
        }
        catch(Exception e){
            Exception exception=e;
            exception.getMessage();
        }
        finally {
            cursor.close();
            this.close();

        }
        return movies;
    }
}