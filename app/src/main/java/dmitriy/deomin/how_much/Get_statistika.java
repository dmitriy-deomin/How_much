package dmitriy.deomin.how_much;

import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by Admin on 09.05.2016.
 */
public class Get_statistika extends AsyncTask<Void,Void,String> {
    @Override
    protected String doInBackground(Void... params) {
        Connection.Response response=null;
        try {
           response = Jsoup.connect("http://i9027296.bget.ru/db_statistika.php")
                   .data("id_user",Main.ID_DEVISE)
                   .method(Connection.Method.POST)
                   .timeout(5*1000)  //5 секунд
                   .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(response!=null) {
            try {
                return response.parse().select("stat").text().replace("&pazduplitel&",":");
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }else{
            return "error baza zalupaetsa";
        }
    }
}
