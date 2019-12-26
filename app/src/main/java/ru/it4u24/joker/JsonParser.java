package ru.it4u24.joker;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {

    private JSONObject resultJson;
    private JSONArray jsonArray;
    private final String LOG_TAG = "myLogs";

    public String[][] Parser(String response) throws JSONException {

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

    public void addObject(String name, Object value) throws JSONException {

        if (resultJson == null) {
            resultJson = new JSONObject();
        }
        resultJson.put(name, value);
    }

    public void addObjectArray(String name) throws JSONException {

        if (resultJson == null || jsonArray == null) return;
        resultJson.put(name, jsonArray);
    }

    public void newArray() {
        jsonArray = new JSONArray();
    }

    public void addArray(Object value) {

        if (jsonArray == null) {
            newArray();
        }
        jsonArray.put(value);
    }

    public void addArrayObject(String name, Object value) throws JSONException {

        if (jsonArray == null) {
            newArray();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(name, value);
        jsonArray.put(jsonObject);
    }

    public String getResult() {
        Log.d(LOG_TAG, "resultJson = " + resultJson.toString());
        return resultJson.toString();
    }
}
