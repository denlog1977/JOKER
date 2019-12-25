package ru.it4u24.joker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Preferences extends Activity {

    private SharedPreferences sPref;

    public void setPrefService1c(String service1cLog, String service1cPas, Context ctx) {
        //sPref = getPreferences(MODE_PRIVATE);
        Map map = getPrefService1c();
        String doservice1cLog = map.get("service1cLog").toString();
        String doservice1cPas = map.get("service1cPas").toString();
        if (service1cLog == doservice1cLog && service1cPas == doservice1cPas) return;

        sPref = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("service1cLog", service1cLog);
        ed.putString("service1cPas", service1cPas);
        ed.apply();
        Log.d("myLogs", "Сохранено service1cLog и service1cPas");
    }

    public Map getPrefService1c() {
        //sPref = getPreferences(MODE_PRIVATE);
        sPref = getSharedPreferences("mysettings", Context.MODE_PRIVATE, Context ctx);
        String service1cLog = sPref.getString("service1cLog", "");
        String service1cPas = sPref.getString("service1cPas", "");
        Log.d("myLogs", "Получено service1cLog=" + service1cLog);

        Map map = new HashMap();
        map.put("service1cLog", service1cLog);
        map.put("service1cPas", service1cPas);

        return map;
    }
}
