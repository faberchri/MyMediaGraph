package org.mymedialite.correlation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TimeAdjustment extends  HashMap<Integer, Double> {
  
  public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
  
//--------------------------------------------------------------------------------------------------
  public TimeAdjustment(String filename) throws IOException {
    super();
    Calendar start = Calendar.getInstance();
    start.set(2013, 1, 1, 0, 0, 0);
    BufferedReader reader = new BufferedReader(new FileReader(filename));

    String line;
    while((line = reader.readLine()) != null) {
      String[] elements = line.split(",");
      int item;
      Calendar date;
      try {
        item = Integer.parseInt(elements[1]);
        date = parse(elements[2]);
        int hours = (int)((date.getTimeInMillis() - start.getTimeInMillis()) / 3600000);
        hours = Math.max(hours, 0) + 1;
        if(hours > 168) hours = 168;   
        double adjustment = Math.pow(hours, 0.4) / Math.pow(168.0, 0.4);
        System.out.println("hours: " + hours + " adjustment: " + adjustment);
        this.put(item, adjustment);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    reader.close();    
  }
//--------------------------------------------------------------------------------------------------
  public static Calendar parse(String string) throws ParseException {
    if(string.endsWith("Z")) string = string.substring(0, 19) + "+0000";
    if(string.length() == 19) string = string + "+0000";
    string = string.replaceAll("([\\+\\-]\\d\\d):(\\d\\d)","$1$2");
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(dateFormat.parse(string));
    return calendar;
  }
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

}
