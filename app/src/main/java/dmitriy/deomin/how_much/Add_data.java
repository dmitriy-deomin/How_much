package dmitriy.deomin.how_much;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Add_data extends AsyncTask<ArrayList, Integer, Boolean> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //покажем анимацию
       Main.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(ArrayList... params) {
        String loginURL = "http://i9027296.bget.ru/db_add_data.php";

        File file = null;
        try {
            file = new File(savePicture(Main.foto1, 80));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        Connection.Response response = null;
        try {
            //делаем пост запрос
            response = Jsoup.connect(loginURL)
                    .data("prioritet", params[0].get(0).toString())
                    .data("name", params[0].get(2).toString())
                    .data("mani", params[0].get(3).toString())
                    .data("gorod", params[0].get(4).toString())
                    .data("id_user", params[0].get(5).toString())
                    .data("kotegoria", params[0].get(6).toString())
                    .data("koment", params[0].get(7).toString() + "&")
                    .data("gorod_spisok", params[0].get(8).toString())
                    .data("id_item", Main.id_item)
                    .data("file", file.getName(), fs) // тег,имя файла, и сам файл
                    .timeout(60*1000) //30 секунд ждём
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            String otv = null;
            try {
                otv = response.parse().select("otvet_connect").text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (otv.equals("vso norm")) {
                //если всё пучком обновим чексумму нашу
                try {
                    Main.CHEKSUMMA = response.parse().select("summa").text();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                return false;
            }
        }else{
            Main.Toast_error("Ошибка, возможно база не отвечает");
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);
        Main.progressBar.setVisibility(View.GONE);
//tost---------------------------------------------------------------
        if (s) {
            Main.Refresh_new_tovary();
            Main.Toast("Удачно");
            Main.Menu(Main.fabButton);
        } else {
            Main.Toast_error("Ошибка,попробуйте еще раз");
        }
//---------------------------------------------------------------------
    }
    private String savePicture(ImageView iv, int id) throws IOException {

        //если картинка выбрата
        if(Main.load_imag) {
            String folderToSave = "/Pictures/how_much/";

            //создадим папки если нет
            File sddir = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/how_much/");
            if (!sddir.exists()) {
                sddir.mkdirs();
            }

            OutputStream fOut = null;

            File file = new File(Environment.getExternalStorageDirectory().toString(), folderToSave + "crop_" + System.currentTimeMillis() + ".jpg");
            fOut = new FileOutputStream(file);
            Bitmap bitmap = (Bitmap) ((BitmapDrawable) iv.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, id, fOut);
            fOut.flush();
            fOut.close();


            return file.getAbsolutePath();

            //и если нет то отправим образец хуйни
        }else{

            String folderToSave = "/Pictures/how_much/";

            //создадим папки если нет
            File sddir = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/how_much/");
            if (!sddir.exists()) {
                sddir.mkdirs();
            }



            //если файла нет создадим
            File net_foto = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/how_much/net_foto.jpg");
            if (net_foto.exists()) {
               return  net_foto.getAbsolutePath(); // если есть отправим адрес
            }else {

                OutputStream fOut = null;

                File file = new File(Environment.getExternalStorageDirectory().toString(), folderToSave + "net_foto.jpg");
                fOut = new FileOutputStream(file);
                Bitmap bitmap = BitmapFactory.decodeResource(Main.context.getResources(), R.drawable.med_no_photo);
                bitmap.compress(Bitmap.CompressFormat.JPEG, id, fOut);
                fOut.flush();
                fOut.close();

                return file.getAbsolutePath();
            }
        }
    }

}