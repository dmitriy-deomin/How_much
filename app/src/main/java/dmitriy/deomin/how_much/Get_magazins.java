package dmitriy.deomin.how_much;

import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Get_magazins extends AsyncTask<Void,Void,String[]> {
    @Override
    protected String[] doInBackground(Void... params) {
        Connection.Response response=null;
        try {
            response = Jsoup.connect("http://i9027296.bget.ru/db_magazins.php")
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(response!=null) {
            Elements mag_elements =null;
            try {
                mag_elements = response.parse().select("mag");
            } catch (IOException e) {
                e.printStackTrace();
            }

            String [] magazins = new String[mag_elements.size()];

            for(int i=  0; i<mag_elements.size();i++){
                    magazins[i]= mag_elements.get(i).text();
            }
            return magazins;

        }else{
            return new String[]{"error baza zalupaetsa"};
        }
    }
}