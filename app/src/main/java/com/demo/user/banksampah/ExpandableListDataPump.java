package com.demo.user.banksampah;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> cricket = new ArrayList<String>();
        cricket.add("BOTOL");
        cricket.add("BOTOL");
        cricket.add("BOTOL");
        cricket.add("BOTOL");
        cricket.add("BOTOL");

        List<String> football = new ArrayList<String>();
        football.add("BOTOL");
        football.add("BOTOL");
        football.add("BOTOL");
        football.add("BOTOL");
        football.add("BOTOL");

        List<String> basketball = new ArrayList<String>();
        basketball.add("BOTOL");
        basketball.add("BOTOL");
        basketball.add("BOTOL");
        basketball.add("BOTOL");
        basketball.add("BOTOL");

        expandableListDetail.put("PLASTIK", cricket);
        expandableListDetail.put("ALUMUNIUM", football);
        expandableListDetail.put("KERTAS", basketball);

        return expandableListDetail;
    }
}
