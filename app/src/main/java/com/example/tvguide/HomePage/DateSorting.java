package com.example.tvguide.HomePage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Map;

public class DateSorting implements Comparator<Map<String,String>> {
    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");


    @Override
    public int compare(Map<String, String> t1, Map<String, String> t2) {
        try{
            String[] path1 = t1.get("path").split("/");
            String[] path2 = t2.get("path").split("/");
            return format.parse(path1[path1.length - 1]).compareTo(format.parse(path2[path2.length - 1]));
        }catch (ParseException e){
            System.out.println(e.getMessage());
        }
        return -3;
    }
}
