package dmitriy.deomin.how_much;

import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Admin on 24.07.2016.
 */
public class Get_name_tovary extends AsyncTask<Void,Void,String[]> {
    @Override
    protected String[] doInBackground(Void... params) {
        Connection.Response response=null;
        try {
            response = Jsoup.connect("http://i9027296.bget.ru/db_name_tovar.php")
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(response!=null) {
            Elements id_elements =null;
            try {
                id_elements = response.parse().select("name");
            } catch (IOException e) {
                e.printStackTrace();
            }

            String [] magazins = new String[id_elements.size()];

            for(int i=  0; i<id_elements.size();i++){
                magazins[i]= id_elements.get(i).text();
            }
            return magazins;

        }else{
            return new String[]{"error baza zalupaetsa"};
        }
    }
}