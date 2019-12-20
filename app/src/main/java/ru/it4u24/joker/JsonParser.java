package ru.it4u24.joker;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {

    public String[][] Parser(String response) throws JSONException {
        final String LOG_TAG = "myLogs";
        String[][] resultString;
        JSONObject jsonObject = new JSONObject(response);
        JSONArray ja = jsonObject.getJSONArray("МассивОрганизаций");

        resultString = new String[ja.length()][3];

        //resultString[0][1] = jsonObject.getString("Текст");
        for (int i = 0; i < ja.length(); i++) {
            JSONObject joOrg = ja.getJSONObject(i);
            String id = joOrg.getString("ID");
            String name = joOrg.getString("Наименование");
            int idfb = joOrg.getInt("IDFb");

            resultString[i][0] = id;
            resultString[i][1] = name;

            Log.d(LOG_TAG, "ID = " + id + ", Наименование = " + name +
                    ", IDFb = " + idfb);
        }

        return resultString;
    }
}
