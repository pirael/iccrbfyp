package com.iccrb_fyp.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FypBaseUtils {

    public static String getEllapedTime(){
        try{
            return String.valueOf(new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ")
                    .parse(new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ")
                            .format(new Date())).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf("0");
    }

    public static String getMessageId(){
        String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm-";
        final Random random=new Random();
        final StringBuilder builder=new StringBuilder(32);
        for(int i=0;i<32;++i){
            builder.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        return builder.toString();
    }
}
