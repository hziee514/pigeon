package cn.wrh.smart.dove.storage;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author bruce.wu
 * @date 2018/7/23
 */
public class ConverterTest {

    @Test
    public void parseHeader() throws Exception {
        //System.out.println(Converter.parseHeader(null));
        //System.out.println(Converter.parseHeader(""));
        System.out.println(Converter.parseHeader("DOVE:2018-07-23 17:06:32|2.0.3"));
    }

}