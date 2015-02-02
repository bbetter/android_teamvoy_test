package com.example.andriypuhach.android_teamvoy_test.models;

import android.os.Environment;

import com.example.andriypuhach.android_teamvoy_test.Joiner;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * модель фільм
 * містить також внутрішні класи для відображення зображень ,нотаток,рецензій і т.д
 */
public class Movie implements Serializable {
    private final transient static String ImageURL = "https://image.tmdb.org/t/p/";
    private final transient static File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/android_teamvoy_test");
    public static transient List<Movie> favorites;

    /**
     * цей статичний блок відповідає за завантаження даних про улюблені фільми із бази даних
     */
    static{
        favorites=new ArrayList<>();
        refreshFavorites();
    }
    private int id;
    private int vote_count;
    private String original_title;
    private String title;
    private String poster_path;
    private DateTime release_date;
    private double vote_average;
    private double popularity;
    private boolean adult;
    private Details details;


    public static String transformPathToURL(String cutPath, ImageSize size) {
        return ImageURL + size.toString() + cutPath;
    }

    public String getPosterURL(ImageSize size) {
        return ImageURL + size.toString() + poster_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public static enum ImageSize implements Serializable {
        W75 {
            @Override
            public String toString() {
                return "w75";
            }
        },
        W150 {
            @Override
            public String toString() {
                return "w150";
            }
        },
        W300 {
            @Override
            public String toString() {
                return "w300";
            }
        },
        W600 {
            @Override
            public String toString() {
                return "w600";
            }
        }
    }

    public static String getImageURL() {
        return ImageURL;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }
    public DateTime getRelease_date() {
        return release_date;
    }

    public void setRelease_date(DateTime release_date) {
        this.release_date = release_date;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }


    public static void refreshFavorites(){
        File file = new File(appDir, "favorites.movinf");
        if (!file.exists()) {
            try {
                file.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        favorites = deserializeList(file);
    }
    public static void saveFavorites(){
        serializeList(favorites, new File(appDir.getAbsolutePath() + "/favorites.movinf"));
    }
    private static void serializeList(List<Movie> movies,File file){
        try {
            if(!file.exists()) {
                file.mkdirs();
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(movies);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static List<Movie> deserializeList(File file){
        List<Movie> movies=new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            movies = (List<Movie>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException | ClassNotFoundException ignored) {

        }
        return movies;
    }

    public static class Details implements Serializable {
        private float budget;
        private float revenue;

        private String homepage;
        private String tagline;
        private String overview;
        private String status;

        private Videos videos;
        private Images images;
        private Credits credits;
        private Reviews reviews;

        private List<Note> notes;

        private List<Genre> genres;
        private List<Company> production_companies;
        private List<Country> production_countries;
        private List<Language> spoken_languages;

        //через кому
        private String genresSimplified;
        private String companiesSimplified;

        public Details() {

        }

        public String getHomepage() {
            return homepage;
        }

        public void setHomepage(String homepage) {
            this.homepage = homepage;
        }

        public float getBudget() {
            return budget;
        }

        public void setBudget(float budget) {
            this.budget = budget;
        }

        public String getTagline() {
            return tagline;
        }

        public void setTagline(String tagline) {
            this.tagline = tagline;
        }

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public List<Genre> getGenres() {
            return genres;
        }

        public void setGenres(List<Genre> genres) {
            this.genres = genres;
            setGenresSimplified(getGenresCommaSeparatedList());
        }

        public String getGenresCommaSeparatedList(){
            List<String> genreStrings=new ArrayList<>();
            if(genres!=null) {
                for (Genre g : genres) {
                    genreStrings.add(g.getName());
                }
                if(genreStrings.size()!=0)
                return Joiner.join(genreStrings, ',');
            }
            else{
                return getGenresSimplified();
            }
            return "";
        }

        public String getCompaniesCommaSeparatedList(){
            if(genres!=null) {
                List<String> companyStrings = new ArrayList<>();
                for (Company c : production_companies) {
                    companyStrings.add(c.getName());
                }
                if(companyStrings.size()!=0)
                return Joiner.join(companyStrings, ',');
            }
            else{
                return getCompaniesSimplified();
            }
            return "";
        }

        public float getRevenue() {
            return revenue;
        }

        public void setRevenue(float revenue) {
            this.revenue = revenue;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<Company> getProduction_companies() {
            return production_companies;
        }

        public void setProduction_companies(List<Company> production_companies) {
            this.production_companies = production_companies;
            setCompaniesSimplified(getCompaniesCommaSeparatedList());
        }

        public List<Country> getProduction_countries() {
            return production_countries;
        }

        public void setProduction_countries(List<Country> production_countries) {
            this.production_countries = production_countries;
        }

        public List<Language> getSpoken_languages() {
            return spoken_languages;
        }

        public void setSpoken_languages(List<Language> spoken_languages) {
            this.spoken_languages = spoken_languages;
        }

        public List<Note> getNotes() {
            return notes;
        }

        public void setNotes(List<Note> notes) {
            this.notes = notes;
        }

        public String getGenresSimplified() {
            return genresSimplified;
        }

        public void setGenresSimplified(String genresSimplified) {
            this.genresSimplified = genresSimplified;
        }

        public String getCompaniesSimplified() {
            return companiesSimplified;
        }

        public void setCompaniesSimplified(String companiesSimplified) {
            this.companiesSimplified = companiesSimplified;
        }

        public Videos getVideosWrapper() {
            return videos;
        }

        public void setVideosWrapper(Videos videos) {
            this.videos = videos;
        }

        public Images getImages() {
            return images;
        }

        public void setImages(Images images) {
            this.images = images;
        }

        public Credits getCredits() {
            return credits;
        }

        public void setCredits(Credits credits) {
            this.credits = credits;
        }

        public Reviews getReviewWrapper() {
            return reviews;
        }

        public void setReviews(Reviews reviews) {
            this.reviews = reviews;
        }




        public static class Language implements Serializable {
            private String iso;
            private String name;

            public String getIso() {
                return iso;
            }

            public void setIso(String iso) {
                this.iso = iso;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
        public static class Company implements Serializable {
            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
        public static class Country implements Serializable {
            private String iso;
            private String name;

            public String getIso() {
                return iso;
            }

            public void setIso(String iso) {
                this.iso = iso;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
        public static class Genre implements Serializable {
            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class Images implements Serializable{
            private List<String> imagePathes;

            public List<Backdrop> getBackdrops() {
                return backdrops;
            }

            public void setImagePathes(List<String> imagePathes) {
                this.imagePathes = imagePathes;
            }

            public String getImagesCommaSeparatedList() {
                return Joiner.join(getImagePathes(),',');
            }
            public List<String> getImagePathes() {
                List<String> imagePathes = new ArrayList<>();
                for(Backdrop backdrop:backdrops){
                    imagePathes.add(backdrop.getFile_path());
                }
                return imagePathes;
            }

            public void setBackdrops(List<Backdrop> backdrops) {
                this.backdrops = backdrops;
            }

            public static class Backdrop implements Serializable{
                private String aspect_ratio;
                private String file_path;
                private int height;
                private int width;
                private String vote_average;
                private int vote_count;

                public String getAspect_ratio() {
                    return aspect_ratio;
                }

                public void setAspect_ratio(String aspect_ratio) {
                    this.aspect_ratio = aspect_ratio;
                }

                public String getFile_path() {
                    return file_path;
                }

                public void setFile_path(String file_path) {
                    this.file_path = file_path;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public String getVote_average() {
                    return vote_average;
                }

                public void setVote_average(String vote_average) {
                    this.vote_average = vote_average;
                }

                public int getVote_count() {
                    return vote_count;
                }

                public void setVote_count(int vote_count) {
                    this.vote_count = vote_count;
                }
            }
            private List<Backdrop> backdrops;
        }

        /**
         * якщо на девайсі не підключені google play services то відео не буде відображатись
         */
        public static class Videos implements Serializable{
            public static class Video implements Serializable{

                public final static String YOUTUBE_API_KEY="AIzaSyDuoKjXOkrcIABTNCwhnVdZye4tQ0yHtBE";
                String key;
                String name;
                String site;
                String type;

                public String getKey() {
                    return key;
                }

                public void setKey(String key) {
                    this.key = key;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getSite() {
                    return site;
                }

                public void setSite(String site) {
                    this.site = site;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }
            }
            public List<Video> getVideos() {
                return results;
            }

            public void setVideos(List<Video> videos) {
                this.results = videos;
            }
            private List<Video> results;
        }
        public static class Credits implements Serializable {

            public static class CastPerson implements Serializable{
                private int id;
                private String character;
                private String name;
                private String order;
                private String profile_path;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getCharacter() {
                    return character;
                }

                public void setCharacter(String character) {
                    this.character = character;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getOrder() {
                    return order;
                }

                public void setOrder(String order) {
                    this.order = order;
                }

                public String getProfile_path() {
                    return profile_path;
                }

                public void setProfile_path(String profile_path) {
                    this.profile_path = profile_path;
                }
            }
            public static class CrewPerson implements Serializable{
                private int id;
                private String department;
                private String job;
                private String name;
                private String profile_path;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getDepartment() {
                    return department;
                }

                public void setDepartment(String department) {
                    this.department = department;
                }

                public String getJob() {
                    return job;
                }

                public void setJob(String job) {
                    this.job = job;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getProfile_path() {
                    return profile_path;
                }

                public void setProfile_path(String profile_path) {
                    this.profile_path = profile_path;
                }
            }

            private int id;
            private List<CastPerson> cast;
            private List<CrewPerson> crew;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public List<CastPerson> getCast() {
                return cast;
            }

            public void setCast(List<CastPerson> cast) {
                this.cast = cast;
            }

            public List<CrewPerson> getCrew() {
                return crew;
            }

            public void setCrew(List<CrewPerson> crew) {
                this.crew = crew;
            }
        }

        public static class Reviews implements Serializable{
            public static class Review implements Serializable{
                private String author;
                private String url;
                private String content;

                public String getAuthor() {
                    return author;
                }

                public void setAuthor(String author) {
                    this.author = author;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }
            }
            private int page;
            private int total_pages;
            private List<Review> results;

            public int getPage() {
                return page;
            }

            public void setPage(int page) {
                this.page = page;
            }

            public int getTotal_pages() {
                return total_pages;
            }

            public void setTotal_pages(int total_pages) {
                this.total_pages = total_pages;
            }

            public List<Review> getReviews() {
                return results;
            }

            public void setReviews(List<Review> results) {
                this.results = results;
            }
        }

        public static class Note implements Serializable {
            private int id;
            private String noteTitle;
            private String noteText;
            private String imagePath;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getNoteTitle() {
                return noteTitle;
            }

            public void setNoteTitle(String noteTitle) {
                this.noteTitle = noteTitle;
            }

            public String getNoteText() {
                return noteText;
            }

            public void setNoteText(String noteText) {
                this.noteText = noteText;
            }

            public String getImagePath() {
                return imagePath;
            }

            public void setImagePath(String imagePath) {
                this.imagePath = imagePath;
            }
        }
    }
}
