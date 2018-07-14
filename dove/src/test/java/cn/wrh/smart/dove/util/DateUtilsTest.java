package cn.wrh.smart.dove.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class DateUtilsTest {

    @Test
    public void now() {
        System.out.println(DateUtils.now());
        System.out.println(new Date());
    }
}