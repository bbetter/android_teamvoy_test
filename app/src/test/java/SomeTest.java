import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.EditText;
import android.widget.ListView;

import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.activities.MainActivity;
import com.example.andriypuhach.android_teamvoy_test.adapters.MovieListAdapter;
import com.example.andriypuhach.android_teamvoy_test.models.MovieRequestResult;
import com.example.andriypuhach.android_teamvoy_test.rest.RestClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.RobolectricBackgroundExecutorService;

import static org.fest.assertions.api.ANDROID.assertThat;
/**
 * Created by andriypuhach on 06.02.15.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class SomeTest {
    private MainActivity activity;
    @Before
    public void setup(){
        activity= Robolectric.buildActivity(MainActivity.class).create().get();
    }
    @Test
    public void testForNotNullActivity(){
        assertThat(activity).isNotNull();
    }
    @Test
    public void someComponentTest() throws InterruptedException {
        activity.setContentView(R.layout.main);
        EditText searchS=(EditText)activity.findViewById(R.id.searchMovieEdit);
        searchS.setText("Сталкер");
        ListView listView = (ListView)activity.findViewById(R.id.listView);
        Thread.sleep(1000);
        Assert.assertTrue("Items not found",listView.getCount()>0);

    }
    @Test
    public void addToFavoriteTest() {
    JsonElement elm=RestClient.getApi().setFavorite(new Gson().toJsonTree("{\"media_type\":\"movie,\"media_id\":603,\"favorite\":true}").getAsJsonObject(), RestClient.sessionId);
    Assert.assertEquals("some error",elm.getAsJsonObject().get("status_code").getAsInt(),1);

    }
    @Test
    public void wrongTypeTest(){
        MovieRequestResult result = RestClient.getApi().getMovies("upcming",1);
        Assert.assertTrue("Wrong type",result.getTotal_pages()>0);
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

}
