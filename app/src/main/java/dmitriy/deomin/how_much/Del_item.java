package dmitriy.deomin.how_much;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by Admin on 01.05.2016.
 */
public class Del_item extends AsyncTask<String[], Void, Boolean> {

    @Override
    protected Boolean doInBackground(String[]... params) {
        String loginURL = "http://i9027296.bget.ru/db_del_item.php";
        String loginURL_conect = "http://i9027296.bget.ru/db_connect.php"; // если база работает

        //проверяем што страничка работает
        try {
            switch (Proverochka(loginURL_conect)) {
                case 200: //все норм
                    return Del_data_zapros(params[0], loginURL); //добавляем инфу в базу
                case 0: // полный пинздец
                    return false;
                default:
                    return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);
    }
    private int Proverochka(String u) throws IOException {
        URL url = new URL(u);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.connect();
            int statusCode = connection.getResponseCode();
            return statusCode;
        } catch (UnknownHostException e) {
            return 0;
        }
    }

    private boolean Del_data_zapros(String[] params, String loginURL) throws IOException {

        //0 - item_id

        Log.d("TTT",params[1].toString().replace("http://i9027296.bget.ru/",""));
        Connection.Response response = null;
        try {
            //делаем пост запрос
            response = Jsoup.connect(loginURL)
                    .data("item_id",params[0].toString())
                    .data("foto1",params[1].toString().replace("http://i9027296.bget.ru/",""))
                    .method(Connection.Method.POST)
                    .followRedirects(true)
                    .timeout(5*1000) //5 сек обождём
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String otv = response.parse().select("otvet_connect").text();
        if (otv.equals("vso norm")) {
            // otv = response.parse().select("otvet_add_db").text();
            return true;
        }else {
            return false;
        }

    }
}
