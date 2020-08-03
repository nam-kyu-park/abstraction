package kr.co.sptek.abstraction.util;

import org.apache.kafka.common.protocol.types.Field;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Timestamp {

    private static final String TIMESTAMP_FORMAT= "yyyyMMddHHmmss";

    public static String currentTime() {
        return new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
    }

    public static String yearExpiration(String currentTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT);
            Date date = format.parse(currentTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.YEAR, 1);
            cal.add(Calendar.SECOND, -1);
            return format.format(cal.getTime());
        }
        catch (ParseException e)
        {
        }
        return currentTime;
    }

}
