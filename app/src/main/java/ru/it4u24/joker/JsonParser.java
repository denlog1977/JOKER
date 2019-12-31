package ru.it4u24.joker;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class JsonParser {

    private JSONObject resultJson;
    private JSONArray jsonArray;
    private final String LOG_TAG = "myLogs";

    public String[][] Parser(String response) throws JSONException {

        String[][] resultString = new String[0][0];
        JSONObject jsonObject = new JSONObject(response);
        if (!jsonObject.has("МассивДанных")) {
            return resultString;
        }
        JSONArray jsonArray = jsonObject.getJSONArray("МассивДанных");
        if (jsonArray.length() == 0) {
            return resultString;
        }
        JSONObject arrayJSONObject = jsonArray.getJSONObject(0);

        resultString = new String[jsonArray.length()][arrayJSONObject.length()];

        //resultString[0][1] = jsonObject.getString("Текст");
        for (int i = 0; i < jsonArray.length(); i++) {
            //JSONObject joOrg = jsonArray.getJSONObject(i);
            //String id = joOrg.getString("ID");
            //String name = joOrg.getString("Наименование");
            //int idfb = joOrg.getInt("IDFb");

            JSONObject jsonObjectColumn = jsonArray.getJSONObject(i);
            JSONArray keys = jsonObjectColumn.names();

            for (int c = 0; c < keys.length(); c++) {
                String key = keys.getString(c);
                Object value = jsonObjectColumn.get(key);

                resultString[i][c] = value.toString();
                Log.d(LOG_TAG, "key=value - " + key + "=" + value);
            }

            //resultString[i][0] = id;
            //resultString[i][1] = name;

            //Log.d(LOG_TAG, "ID = " + id + ", Наименование = " + name +
            //        ", IDFb = " + idfb);
        }

        return resultString;
    }

    private static Map<String, Object> convertJSONObjectToHashMap(JSONObject jsonObject) {
        HashMap<String, Object> map = new HashMap<>();
        JSONArray keys = jsonObject.names();
        for (int i = 0; i < keys.length(); ++i) {
            String key;
            try {
                key = keys.getString(i);
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject) {
                    value = convertJSONObjectToHashMap((JSONObject) value);
                }
                map.put(key, value);
            } catch (JSONException e) {
            }
        }
        return map;
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
