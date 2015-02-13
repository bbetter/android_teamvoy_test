

import com.example.andriypuhach.android_teamvoy_test.models.Movie;
import com.example.andriypuhach.android_teamvoy_test.models.MovieRequestResult;
import com.example.andriypuhach.android_teamvoy_test.rest.RestClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
     * Created by andriypuhach on 06.02.15.
     */
    @Config(emulateSdk = 18,manifest = Config.NONE)
    @RunWith(RobolectricTestRunner.class)
    public class SomeTest {
        private final Gson gsonConverter=new GsonBuilder()
        .setDateFormat("yyyy-mm-dd")
        .registerTypeAdapter(DateTime.class, new RestClient.DateTimeTypeConverter())
                .create();
        private synchronized String fileToJson(String fileName) throws IOException {
            InputStream stream = getClass().getResourceAsStream("/"+fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder builder=new StringBuilder();
            while((line=reader.readLine())!=null){
                builder.append(line);
            }
            reader.close();
            return builder.toString();
        }
        @Test
        public void testJsonGenreValidity() throws IOException {
            String json = fileToJson("genre.json");
            TestGenre genre=gsonConverter.fromJson(json, TestGenre.class);
            TestGenre tGe=new TestGenre(28,"text");
            Assert.assertEquals(genre,tGe);
        }
        @Test
        public void testMovieValidity() throws  IOException{
            String json=fileToJson("movie.json");
            Movie etalon = gsonConverter.fromJson(json, Movie.class);
            Movie mock = new Movie();
            mock.setAdult(false);
            mock.setPoster_path("/ft8IqAGFs3V7i87z0t0EVRUjK1p.jpg");
            mock.setOriginal_title("Fast And Furious");
            mock.setRelease_date(new DateTime("2009-03-11"));
            mock.setPopularity(20.1446806145099);
            mock.setVote_average(7.7);
            mock.setVote_count(41);
            mock.setId(13804);
            Assert.assertEquals(etalon,mock);
        }
        @Test
        public void addToFavoriteTest() throws IOException {
            JsonObject object = (JsonObject) new JsonParser().parse("{\"media_type\":\"movie\",\"media_id\":603,\"favorite\":true}");
            JsonElement elm= RestClient.getApi().setFavorite(object, RestClient.sessionId);
            Assert.assertEquals("some error",elm.getAsJsonObject().get("status_code").getAsInt(),1);            Assert.assertEquals("some error",elm.getAsJsonObject().get("status_code").getAsInt(),1);
        }

        @Test
        public void wrongPageTest(){
            MovieRequestResult result = RestClient.getApi().getMovies("popular",1);
            Assert.assertTrue("Wrong page",result.getTotal_pages()>0);
        }

        @Test
        public void getTokenTest(){
            JsonElement element = RestClient.getApi().getToken();
            Assert.assertTrue("can't get token",element.getAsJsonObject().get("success").getAsBoolean());
        }
        @Test
        public void validateTokenTest(){
            JsonElement element = RestClient.getApi().validateToken();
            Assert.assertTrue("wrong pass or smth",element.getAsJsonObject().get("success").getAsBoolean());
        }
       @Test
        public void testMovieRequestResult() throws IOException{
            String json =fileToJson("movies.json");
            MovieRequestResult etalon = gsonConverter.fromJson(json,MovieRequestResult.class);
            MovieRequestResult mock = new MovieRequestResult();
            mock.setPage(1);
            List<Movie> movieList = new ArrayList<>();
            Random rm = new Random();
            for(int i=0;i<20;++i){
                Movie mv = new Movie();
                mv.setId(rm.nextInt(10000));
                movieList.add(mv);
            }
            mock.setResults(movieList);
            mock.setTotal_pages(2400);
            mock.setTotal_results(24000);
            Assert.assertEquals(etalon,mock);

        }


    }
