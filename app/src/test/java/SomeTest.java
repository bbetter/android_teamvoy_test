import com.example.andriypuhach.android_teamvoy_test.models.MovieRequestResult;
import com.example.andriypuhach.android_teamvoy_test.rest.RestClient;

import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by andriypuhach on 06.02.15.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class SomeTest {
    @Before
    public void setup(){

    }
    @Test
    public void someTest(){
       MovieRequestResult result = RestClient.getApi().getMovies("popular",1);
       Assert.assertTrue("1212424",result.getTotal_pages()<0);
    }
}
