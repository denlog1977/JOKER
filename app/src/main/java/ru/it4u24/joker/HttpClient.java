package ru.it4u24.joker;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class HttpClient extends AsyncTask<String, Integer, String[][]> {

    private long TimeStart = 0, TimeEnd = 0;
    private String ERROR = "";
    private String URL = "https://kamaz.ddns.net:10100/testut/hs/ExchangeTFK/query";
    private final String LOG_TAG = "myLogs";

    private OkHttpClient client = new OkHttpClient();

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getERROR() {
        return ERROR;
    }

    public long getTimeEnd() {
        return TimeEnd;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Date date = new Date();
        TimeStart = date.getTime();
    }

    @Override
    protected String[][] doInBackground(String... strings) {

        String[][] resultString = new String[0][0];
        String LOGIN = "";
        String PASSWORD = "";
        String query = strings[0];

        try {
            Authenticate(LOGIN, PASSWORD);
            String result = run(query);
            if (!ERROR.isEmpty()) return resultString;
            try {
                JsonParser jsonParser = new JsonParser();
                resultString = jsonParser.Parser(result);
            } catch (Exception e) {
                e.printStackTrace();
                ERROR = e.getMessage();
                Log.d(LOG_TAG, "JSONObject error: " + ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ERROR = e.getMessage();
            Log.d(LOG_TAG,"Error HttpClient: " + ERROR);
        }

        redefintionError();

        Date date = new Date();
        TimeEnd = date.getTime() - TimeStart;

        Log.d(LOG_TAG, "Результат времени в милисекундах: " + TimeEnd);

        return resultString;
    }

    private SSLSocketFactory sslSocket() {
        X509TrustManager trustManager;
        TrustManager[] trustManagers;
        SSLSocketFactory sslSocketFactory;
        try {
            trustManagers = new TrustManager[]{new SSLConnection._FakeX509TrustManager()};
            trustManager = (X509TrustManager) trustManagers[0];

            //if (trustManagers == null) {
            //    trustManagers = new TrustManager[]{new SSLConnection._FakeX509TrustManager()};
            //}
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        return sslSocketFactory;
    }

    private void Authenticate(final String login, final String password) {
        client = new OkHttpClient.Builder()
                .readTimeout(300, TimeUnit.SECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslSocket())
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        if (response.request().header("Authorization") != null) {
                            return null; // Give up, we've already attempted to authenticate.
                        }

                        //System.out.println("Authenticating for response: " + response);
                        //System.out.println("Challenges: " + response.challenges());
                        String credential = Credentials.basic(login, password);
                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .build();
                    }
                })
                .build();
    }

    private String run(String query) {

        RequestBody formBody = new FormBody.Builder()
                .add("GetList", query)
                .add("g", "test")
                .build();

        Request request = new Request.Builder()
                .url(URL)
                //.addHeader("gg", "test2")
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                Log.d("myLogs",responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }
            String result = response.body().string();
            Log.d("myLogs","Result: " + result);
            return result;
        } catch (IOException e) {
            ERROR = "Error HttpClient run: " + e.getMessage();
            Log.d("myLogs", ERROR);
            e.printStackTrace();
        }

        redefintionError();

        return "";
    }

    private void redefintionError() {

        int index401 = ERROR.indexOf("code=401");
        int index404 = ERROR.indexOf("code=404");
        int index405 = ERROR.indexOf("code=405");
        int indexHost = ERROR.indexOf("No address associated with hostname");
        int indexPort = ERROR.indexOf("unexpected url");
        if (index401 > -1) {
            ERROR = "Не пройдена авторизация";
        } else if (indexHost > -1) {
            ERROR = "Не определен хост";
        } else if (indexPort > -1) {
            ERROR = "Не определен порт хоста";
        } else if (index404 > -1 || index405 > -1) {
            ERROR = "Не верный запрос";
        }
    }
}