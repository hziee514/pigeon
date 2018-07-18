package cn.wrh.smart.dove;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.junit.Test;

import java.util.Date;

import cn.wrh.smart.dove.util.DateUtils;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("a", "a1");
        multimap.put("a", "a2");
        multimap.put("a", "a3");
        multimap.put("b", "b1");
        multimap.put("b", "b2");
        multimap.put("b", "b3");
        System.out.println(multimap.toString());
    }

    @Test
    public void date() {
        System.out.println(DateUtils.now());
        //System.out.println(DateTime.now(DateTimeZone.UTC).toDate());
        System.out.println(new Date());
    }

}