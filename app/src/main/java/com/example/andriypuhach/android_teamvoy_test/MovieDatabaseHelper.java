package com.example.andriypuhach.android_teamvoy_test;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.andriypuhach.android_teamvoy_test.models.Movie;

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
    private final String GENRES_COLUMN="genres";
    private final String COMPANIES_COLUMN="companies";
    private final String IMAGES_COLUMN="images";
    private final String VOTE_AVERAGE_COLUMN="voteAverage";

    private final String [] MOVIE_COLUMNS={MOVIE_ID_COLUMN,
            ORIGINAL_TITLE_COLUMN,TITLE_COLUMN,POSTER_PATH_COLUMN,
            RELEASE_DATE_COLUMN,BUDGET_COLUMN,REVENUE_COLUMN,
            HOMEPAGE_COLUMN,POPULARITY_COLUMN,STATUS_COLUMN,
            GENRES_COLUMN,COMPANIES_COLUMN,IMAGES_COLUMN,VOTE_AVERAGE_COLUMN};
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
        return "\'"+str+"\'";
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
                +GENRES_COLUMN+" text,"
                +COMPANIES_COLUMN+" text,"
                +IMAGES_COLUMN+" text,"
                +VOTE_AVERAGE_COLUMN+" double");

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
    public void insertNote(Movie movie,Movie.Details.Note note){
        String [] movieValues={String.valueOf(movie.getId()),
                movie.getOriginal_title()!=null?quotate(movie.getOriginal_title()):null,
                movie.getTitle()!=null?quotate(movie.getTitle()):null,
                movie.getPoster_path()!=""?quotate(movie.getPoster_path()):null,
                movie.getRelease_date()!=null?quotate(movie.getRelease_date().toLocalDate().toString()):null,
                quotate(String.valueOf(movie.getDetails().getBudget())),
                quotate(String.valueOf(movie.getDetails().getRevenue())),
                movie.getDetails().getHomepage()!=null?quotate(movie.getDetails().getHomepage()):null,
                quotate(String.valueOf(movie.getPopularity())),
                movie.getDetails().getStatus()!=null?quotate(movie.getDetails().getStatus()):null,
                quotate(movie.getDetails().getGenresCommaSeparatedList()),
                quotate(movie.getDetails().getCompaniesCommaSeparatedList()),
                movie.getDetails().getImagePathes()!=null?quotate(movie.getDetails().getImagesCommaSeparatedList()):null,
                String.valueOf(movie.getVote_average())
};
        String [] noteValues ={String.valueOf(movie.getId()),
                quotate(note.getNoteTitle()),
                quotate(note.getNoteText()),
                quotate(note.getImagePath())
};
        String movieInsertQuery=INSERT_QUERY
                .replace("<TABLE_NAME>",MOVIES_TABLE_NAME)
                .replace("<COLUMNS>",Joiner.join(Arrays.asList(MOVIE_COLUMNS),','))
                .replace("<VALUES>",Joiner.join(Arrays.asList(movieValues),','));

        String query=INSERT_QUERY
                .replace("<TABLE_NAME>", NOTES_TABLE_NAME)
                .replace("<COLUMNS>",Joiner.join(Arrays.asList(NOTE_COLUMNS),','))
                .replace("<VALUES>",Joiner.join(Arrays.asList(noteValues),','));
        try {
            getWritableDatabase().execSQL(movieInsertQuery);
        }
        catch(Exception e){

        }
        finally {
            getWritableDatabase().execSQL(query);
        }
    }
    public void updateNote(Movie.Details.Note note){
        String query=UPDATE_QUERY
                .replace("<TABLE_NAME>", NOTES_TABLE_NAME)
                .replace("<SETS>",NOTE_TITLE_COLUMN+"="+quotate(note.getNoteTitle())
                        +","+NOTE_TEXT_COLUMN+"="+ quotate(note.getNoteText())
                        +","+NOTE_IMAGEPATH_COLUMN+"="+quotate(note.getImagePath())
                        +" where _id="+note.getId());
        this.getWritableDatabase().execSQL(query);
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
        return notes;
    }

    public void deleteNote(int id){
        String query = DELETE_QUERY
                .replace("<TABLE_NAME>", NOTES_TABLE_NAME)
                .replace("<CONDITION>","WHERE _id="+id);
        this.getWritableDatabase().execSQL(query);
    }

    public List<Movie> searchByNote(String title){
        String query= "select * from Movies mv join Notes nt on mv."+MOVIE_ID_COLUMN+"="+"nt."+NOTE_MOVIE_ID_COLUMN+" where nt."+NOTE_TITLE_COLUMN
                +" like '%"+title+"%'";
        List<Movie> movies = new ArrayList<>();
        Cursor cursor=getReadableDatabase().rawQuery(query,null);
        while(cursor.moveToNext()){
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
            String imagePathes=cursor.getString(cursor.getColumnIndex(IMAGES_COLUMN));
            List<String> imagePathesList=new ArrayList<>(Arrays.asList(imagePathes.split(",")));
            String genresSeparatedByCommas=cursor.getString(cursor.getColumnIndex(GENRES_COLUMN));
            String companiesSeparatedByCommas=cursor.getString(cursor.getColumnIndex(COMPANIES_COLUMN));
            details.setGenresSimplified(genresSeparatedByCommas);
            details.setCompaniesSimplified(companiesSeparatedByCommas);
            details.setImagePathes(imagePathesList);
            film.setDetails(details);
            movies.add(film);
        }
        return movies;
    }
}