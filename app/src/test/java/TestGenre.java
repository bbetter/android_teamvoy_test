import org.joda.time.DateTime;
import org.junit.Test;

import java.io.Serializable;

/**
 * Created by andriypuhach on 11.02.15.
 */
public class TestGenre implements Serializable {
    private int id;
    private String name;
    private DateTime time;

    public TestGenre (int id,String name){
        this.id=id;
        this.name=name;
    }
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

    @Override
    public boolean equals(Object o) {
        TestGenre genre = (TestGenre)o;
       if(genre==null) return false;
       return name.equals(genre.getName()) && id==genre.getId();

    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }
}
