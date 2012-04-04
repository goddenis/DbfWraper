package com.goddenis;

import org.xBaseJ.DBF;
import org.xBaseJ.xBaseJException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * User: goddenis
 * Date: 04.04.12
 */
public class Wrapper {
    public static void main(String args[]) {
        ArrayList<String> strings = new ArrayList<String>();

        DBF dbf;
        try {
            dbf = new DBF("D:\\Projects\\DbfWrapper\\DbfWraper\\TestData\\test.dbf");
        } catch (xBaseJException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        int i = 1;
    }
}
