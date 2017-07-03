package com.bolo1.mobilesafe.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 菠萝 on 2017/7/3.
 */

public class StreamUtil {

    public static String StreamToString(InputStream is) {
        ByteArrayOutputStream  bos = new ByteArrayOutputStream();
        byte [] buffer = new byte[1024];
        int temp = -1;
        try {
            while ((temp=is.read(buffer))!=-1){
                bos.write(buffer,0,temp);
            }
            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                is.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
