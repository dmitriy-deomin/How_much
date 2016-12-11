package dmitriy.deomin.how_much.adaptery;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import dmitriy.deomin.how_much.Del_item;
import dmitriy.deomin.how_much.Kategorii;
import dmitriy.deomin.how_much.Main;
import dmitriy.deomin.how_much.Moi_tovary;
import dmitriy.deomin.how_much.New_tofary;
import dmitriy.deomin.how_much.R;
import dmitriy.deomin.how_much.libries.TouchImageView;
import dmitriy.deomin.how_much.libries.pop_menu.PopupMenuCompat;


public class Adapter_tovary extends SimpleAdapter {

    ArrayList<Map<String, Object>> data;
    Context context;

    public Adapter_tovary(Context context, ArrayList<Map<String, Object>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.data = data;
    }


    static class ViewHolder {
        Button b_dislike;
        Button b_likes;
        TextView text_coment;
        LinearLayout fon;
        Button root_edit;
        TextView name_tovar;
        TextView many_tovar;
        TextView gorod;
        TextView data;
        Button add_coment;
        Button delete_po_dislaykam;
        ImageView ava;

        LinearLayout liner_name_delegat;
        LinearLayout liner_mani_delegat;
        LinearLayout liner_adres_delegat;
    }

    public View getView(final int position, View view, final ViewGroup parent) {

        View v = view;
        final ViewHolder viewHolder;


        if (v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.delegat_tovary, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.b_dislike = (Button) v.findViewById(R.id.button_dislake);
            viewHolder.b_likes = (Button)v.findViewById(R.id.button_like);
            viewHolder.text_coment = (TextView) v.findViewById(R.id.textView_koment);
            viewHolder.fon =  (LinearLayout) v.findViewById(R.id.fon_delegata_tovar);
            viewHolder.root_edit = (Button) v.findViewById(R.id.button_root_edit);
            viewHolder.name_tovar = (TextView) v.findViewById(R.id.textView_tovar);
            viewHolder.many_tovar = (TextView) v.findViewById(R.id.textView_cena);
            viewHolder.gorod=(TextView) v.findViewById(R.id.textView_gorod);
            viewHolder.data =(TextView) v.findViewById(R.id.textView_data);
            viewHolder.add_coment = (Button) v.findViewById(R.id.button_add_koment_item);
            viewHolder.delete_po_dislaykam = (Button) v.findViewById(R.id.button_delet_po_dislaykam);
            viewHolder.ava = (ImageView) v.findViewById(R.id.imageView_ava_tovara);
            viewHolder.liner_name_delegat = (LinearLayout)v.findViewById(R.id.liner_name_delegat);
            viewHolder.liner_mani_delegat = (LinearLayout)v.findViewById(R.id.liner_mani_delegat);
            viewHolder.liner_adres_delegat = (LinearLayout)v.findViewById(R.id.liner_adres_delegat);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        context = v.getContext();

        //разные цвета по четности
        viewHolder.fon.setBackgroundColor((position % 2 == 0) ? Main.COLOR_POST : Main.COLOR_POST2);

        viewHolder.root_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenuCompat menu = PopupMenuCompat.newInstance(context, v);
                menu.inflate(R.menu.menu_root);
                menu.setOnMenuItemClickListener(new PopupMenuCompat.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_update) {
                            //загрузим аву в редактор
                            if (data.get(position).get("foto").toString() != null) {
                                Picasso.with(context)
                                        .load(data.get(position).get("foto").toString())
                                        .into(Main.getFoto1());
                            }
                            //загрузим остальные данны е в редактор
                            Main.edit_update(
                                    data.get(position).get("name").toString(),
                                    data.get(position).get("mani").toString(),
                                    data.get(position).get("koment").toString(),
                                    data.get(position).get("gorod").toString(),
                                    data.get(position).get("id_item").toString()
                            );
                        }
                        if (item.getItemId() == R.id.menu_delete) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
                            final View content = LayoutInflater.from(context).inflate(R.layout.custon_dialog, null);
                            builder.setView(content);
                            // builder.show();

                            final AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                            ((Button) content.findViewById(R.id.button_dialog_delete)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                                    ((Button) content.findViewById(R.id.button_dialog_delete)).startAnimation(anim);
                                    alertDialog.dismiss();
                                    Del_item del_item = new Del_item();
                                    String del_zap[] = {
                                            data.get(position).get("id_item").toString(),
                                            data.get(position).get("foto").toString()
                                    };
                                    try {
                                        if (del_item.execute(del_zap).get(1, TimeUnit.SECONDS)) {
                                            Main.Toast_error("Удалено");
                                            //сохраними положение прокрутки
                                            Main.page_scroll_new = position;
                                            //обновим страницу
                                            Main.Refresh_new_tovary();
                                        } else {
                                            Main.Toast_error("Ошибка");
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (TimeoutException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            ((Button) content.findViewById(R.id.button_dialog_edit)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                                    ((Button) content.findViewById(R.id.button_dialog_edit)).startAnimation(anim);
                                    alertDialog.dismiss();
                                }
                            });

                        }
                        return true;
                    }
                });
                menu.show();
            }
        });

        //если наш товар то окрасим в свой цвет и покажем кнопку редактирования
        if (Main.ID_DEVISE.equals(data.get(position).get("id_user").toString())) {
            viewHolder.fon.setBackgroundColor(Main.COLOR_MY_POST);
            viewHolder.root_edit.setVisibility(View.VISIBLE);
        } else {
            viewHolder.root_edit.setVisibility(View.GONE);
        }


        //data.get(position).get("prioritet").toString();

        if (data.get(position).get("foto").toString() != null) {
            Picasso.with(context)
                    .load(data.get(position).get("foto").toString())
                    .resize(Main.width_d,Main.width_d)
                    .into(viewHolder.ava);
        }

        viewHolder.ava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
                final View content = LayoutInflater.from(context).inflate(R.layout.custom_dialog_clik_ava_item, null);
                builder.setView(content);


                if (data.get(position).get("foto").toString() != null) {
                    Picasso.with(context)
                            .load(data.get(position).get("foto").toString())
                            .resize(Main.width_d, Main.width_d)
                            .into(((TouchImageView) content.findViewById(R.id.ava_full_skrin)));
                } else {
                    Picasso.with(context).load(R.drawable.med_no_photo).into(((TouchImageView) content.findViewById(R.id.ava_full_skrin)));
                }

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        if(data.get(position).get("name").toString().equals("-")){
            viewHolder.liner_name_delegat.setVisibility(View.GONE);
        }else {
            viewHolder.liner_name_delegat.setVisibility(View.VISIBLE);
            viewHolder.name_tovar.setText(data.get(position).get("name").toString());
        }

        if(data.get(position).get("mani").toString().equals("-")){
            viewHolder.liner_mani_delegat.setVisibility(View.GONE);
        }else{
            viewHolder.liner_mani_delegat.setVisibility(View.VISIBLE);
            viewHolder.many_tovar.setText(data.get(position).get("mani").toString());
        }


        if(data.get(position).get("gorod_spisok").toString().equals("-")&data.get(position).get("gorod").toString().equals("-")){
            viewHolder.liner_adres_delegat.setVisibility(View.GONE);
        }else{
            viewHolder.liner_adres_delegat.setVisibility(View.VISIBLE);
            viewHolder.gorod.setText(
                    data.get(position).get("gorod_spisok").toString() + "," +data.get(position).get("gorod").toString());
            viewHolder.data.setText(data.get(position).get("data").toString());
        }




        //koment
        if (data.get(position).get("koment").toString().isEmpty()) {
            viewHolder.text_coment.setText("нет коментов");
        } else {
            viewHolder.text_coment.setText(data.get(position).get("koment").toString().replace("&", "\n"));
        }

        viewHolder.text_coment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
                final View content = LayoutInflater.from(context).inflate(R.layout.custom_dialog_text_koment_clik, null);
                builder.setView(content);
                ((LinearLayout)content.findViewById(R.id.fon_text_coment_dialog)).setBackgroundColor(Main.FON);
                ((TextView) content.findViewById(R.id.textView_text_cokent_dialog)).setText(data.get(position).get("koment").toString().replace("&", "\n"));
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        viewHolder.add_coment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                viewHolder.add_coment.startAnimation(anim);

                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
                final View content = LayoutInflater.from(context).inflate(R.layout.custum_dialog_add_coment_item, null);
                builder.setView(content);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                ((RelativeLayout)content.findViewById(R.id.dialog_fon)).setBackgroundColor(Main.FON);

                ((Button) content.findViewById(R.id.button_add_com_dialog)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                        ((Button) content.findViewById(R.id.button_add_com_dialog)).startAnimation(anim);

                        //0 - item_id
                        //2 - coment
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(0, data.get(position).get("id_item").toString());
                        arrayList.add(1, data.get(position).get("koment").toString() +
                                ((Main.save_read("user_psevdo").length() < 1) ? "Аноним" : Main.save_read("user_psevdo")) +
                                ":" +
                                ((EditText) content.findViewById(R.id.editText_add_cometn_dialog)).getText().toString() +
                                "&");
                        arrayList.add(2,viewHolder.text_coment);
                        arrayList.add(3,position);


                        alertDialog.dismiss();

                        //сохраними положение прокрутки
                        Main.page_scroll_new = position;

                        Add_coment_item add_coment_item = new Add_coment_item();
                        add_coment_item.execute(arrayList);

                    }
                });
            }
        });


        //like*******************************************************************************************************
        viewHolder.b_likes.setText(data.get(position).get("like_item").toString());
        viewHolder.b_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                viewHolder.b_likes.startAnimation(anim);

                ArrayList arrayList = new ArrayList();
                arrayList.add(0, data.get(position).get("id_item").toString());
                arrayList.add(1, Main.ID_DEVISE);
                arrayList.add(2, "");
                arrayList.add(3, "1");
                arrayList.add(4,viewHolder.b_dislike);
                arrayList.add(5,viewHolder.b_likes);
                arrayList.add(6,position);
                arrayList.add(7,viewHolder.fon);
                arrayList.add(8,viewHolder.delete_po_dislaykam);

                Add_like add_like = new Add_like();
                add_like.execute(arrayList);
            }
        });
//**********************************************************************************************************
        viewHolder.b_dislike.setText(data.get(position).get("dislike_item").toString());
        viewHolder.b_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                viewHolder.b_dislike.startAnimation(anim);

                final ArrayList arrayList = new ArrayList();
                arrayList.add(0, data.get(position).get("id_item").toString());
                arrayList.add(1, Main.ID_DEVISE);
                arrayList.add(2, "1");
                arrayList.add(3, "");
                arrayList.add(4,viewHolder.b_dislike);
                arrayList.add(5,viewHolder.b_likes);
                arrayList.add(6,position);
                arrayList.add(7,viewHolder.fon);
                arrayList.add(8,viewHolder.delete_po_dislaykam);

                Add_like add_like = new Add_like();
                add_like.execute(arrayList);
            }
        });


        //если дизлайков дохера покажем общию кнопку удалить пост и подкрасим его в говняный цвет
if(!data.get(position).get("on_of_delete").equals("0")){
    if (Integer.valueOf(data.get(position).get("on_of_delete").toString()) > Main.MAX_DIZLAYK) {
        viewHolder.fon.setBackgroundColor(Main.COLOR_POST_DELETE);
        viewHolder.delete_po_dislaykam.setVisibility(View.VISIBLE);
        viewHolder.delete_po_dislaykam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //покажем диалоговое окно если че
                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
                final View content = LayoutInflater.from(context).inflate(R.layout.custon_dialog, null);
                builder.setView(content);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                ((Button) content.findViewById(R.id.button_dialog_delete)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                        ((Button) content.findViewById(R.id.button_dialog_delete)).startAnimation(anim);
                        alertDialog.dismiss();
                        Del_item del_item = new Del_item();
                        String del_zap[] = {
                                data.get(position).get("id_item").toString(),
                                data.get(position).get("foto").toString()
                        };
                        try {
                            if (del_item.execute(del_zap).get(1, TimeUnit.SECONDS)) {
                                Main.Toast("Удалено");
                                //сохраними положение прокрутки
                                Main.page_scroll_new = position;
                                //обновим страницу
                                Main.Refresh_new_tovary();
                            } else {
                                Main.Toast_error("Ошибка");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                });
                ((Button) content.findViewById(R.id.button_dialog_edit)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                        ((Button) content.findViewById(R.id.button_dialog_edit)).startAnimation(anim);
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }
}else {
    viewHolder.delete_po_dislaykam.setVisibility(View.GONE);
}

        return v;
    }

    public class Add_like extends AsyncTask<ArrayList, Integer, String> {

        Button bd;
        Button bl;

        LinearLayout fon;
        Button del;

        int pos;

        @Override
        protected String doInBackground(ArrayList... params) {
            bd = (Button)params[0].get(4);
            bl =(Button)params[0].get(5);

            pos = (int) params[0].get(6);

            fon = (LinearLayout) params[0].get(7);
            del = (Button)params[0].get(8);


            String loginURL = "http://i9027296.bget.ru/db_add_like_dislake.php";
                    //0 - item_id
                    //1 - like_user_id
                    //2 - dislike_item
                    //3 - like_item
                    Connection.Response response = null;
                    try {
                        //делаем пост запрос
                        response = Jsoup.connect(loginURL)
                                .data("item_id", params[0].get(0).toString())
                                .data("l_u_id", params[0].get(1).toString())
                                .data("dislike_item", params[0].get(2).toString())
                                .data("like_item", params[0].get(3).toString())
                                .method(Connection.Method.POST)
                                .timeout(5000)
                                .followRedirects(true)
                                .execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response != null) {
                        try {
                            return response.parse().select("otvet_add_db").text().toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //если всё пучком обновим чексумму нашу
                        try {
                            Main.CHEKSUMMA = response.parse().select("summa").text();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        return null;
                    }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                if (s.equals("adddis")) {
                    bd.setText(String.valueOf(Integer.valueOf(data.get(pos).get("dislike_item").toString()) + 1));

                    //делаем кусочек какашки
                    Map<String,Object> m =new HashMap<String,Object>();

                    //смотрим че щас открыто и обновляем нужное значение
                    if(Main.page_open==0){  //новые товыры
                        //теперь в цикле нужно получить все старые какашки и записать в новый кусок
                        for (Map.Entry entry : New_tofary.d.get(pos).entrySet()) {
                            if(entry.getKey().equals("dislike_item")){
                                m.put("dislike_item",bd.getText().toString());
                            }else{
                                m.put(entry.getKey().toString(),entry.getValue());
                            }
                        }
                        //замазываем ей старое
                        New_tofary.d.set(pos,m);
                    }
                    if(Main.page_open==1){  //фильтр
                        //теперь в цикле нужно получить все старые какашки и записать в новый кусок
                        for (Map.Entry entry : Kategorii.d.get(pos).entrySet()) {
                            if(entry.getKey().equals("dislike_item")){
                                m.put("dislike_item",bd.getText().toString());
                            }else{
                                m.put(entry.getKey().toString(),entry.getValue());
                            }
                        }
                        Kategorii.d.set(pos,m);
                    }
                    if(Main.page_open==2){  //мои товыры
                        //теперь в цикле нужно получить все старые какашки и записать в новый кусок
                        for (Map.Entry entry : Moi_tovary.d.get(pos).entrySet()) {
                            if(entry.getKey().equals("dislike_item")){
                                m.put("dislike_item",bd.getText().toString());
                            }else{
                                m.put(entry.getKey().toString(),entry.getValue());
                            }
                        }
                        Moi_tovary.d.set(pos,m);
                    }
                }
                if (s.equals("addlike")) {
                    bl.setText(String.valueOf(Integer.valueOf(data.get(pos).get("like_item").toString()) + 1));

                    //делаем кусочек какашки
                    Map<String,Object> m =new HashMap<String,Object>();

                    //смотрим че щас открыто
                    if(Main.page_open==0){  //новые товыры
                        //теперь в цикле нужно получить все старые какашки и записать в новый кусок
                        for (Map.Entry entry : New_tofary.d.get(pos).entrySet()) {
                            if(entry.getKey().equals("like_item")){
                                m.put("like_item",bl.getText().toString());
                            }else{
                                m.put(entry.getKey().toString(),entry.getValue());
                            }
                        }
                        //замазываем ей старое
                        New_tofary.d.set(pos,m);
                    }
                    if(Main.page_open==1){  //фильтр
                        //теперь в цикле нужно получить все старые какашки и записать в новый кусок
                        for (Map.Entry entry : Kategorii.d.get(pos).entrySet()) {
                            if(entry.getKey().equals("like_item")){
                                m.put("like_item",bl.getText().toString());
                            }else{
                                m.put(entry.getKey().toString(),entry.getValue());
                            }
                        }
                        Kategorii.d.set(pos,m);
                    }
                    if(Main.page_open==2){  //мои товыры
                        //теперь в цикле нужно получить все старые какашки и записать в новый кусок
                        for (Map.Entry entry : Moi_tovary.d.get(pos).entrySet()) {
                            if(entry.getKey().equals("like_item")){
                                m.put("like_item",bl.getText().toString());
                            }else{
                                m.put(entry.getKey().toString(),entry.getValue());
                            }
                        }
                        Moi_tovary.d.set(pos,m);
                    }
                }

                if(s.length()>7){
                    String[] mas = s.split(":");
                    bd.setText(mas[1].toString());
                    bl.setText(mas[2].toString());

                    //делаем кусочек какашки
                    Map<String,Object> m =new HashMap<String,Object>();

                    //смотрим че щас открыто
                    if(Main.page_open==0){  //новые товыры
                        //теперь в цикле нужно получить все старые какашки и записать в новый кусок
                        for (Map.Entry entry : New_tofary.d.get(pos).entrySet()) {
                            if(entry.getKey().equals("dislike_item")|entry.getKey().equals("like_item")){
                                m.put("dislike_item",bd.getText().toString());
                                m.put("like_item",bl.getText().toString());
                            }else{
                                m.put(entry.getKey().toString(),entry.getValue());
                            }
                        }
                        //замазываем ей старое
                        New_tofary.d.set(pos,m);
                    }
                    if(Main.page_open==1){  //фильтр
                        //теперь в цикле нужно получить все старые какашки и записать в новый кусок
                        for (Map.Entry entry : Kategorii.d.get(pos).entrySet()) {
                            if(entry.getKey().equals("dislike_item")|entry.getKey().equals("like_item")){
                                m.put("dislike_item",bd.getText().toString());
                                m.put("like_item",bl.getText().toString());
                            }else{
                                m.put(entry.getKey().toString(),entry.getValue());
                            }
                        }
                        Kategorii.d.set(pos,m);
                    }
                    if(Main.page_open==2){  //мои товыры
                        //теперь в цикле нужно получить все старые какашки и записать в новый кусок
                        for (Map.Entry entry : Moi_tovary.d.get(pos).entrySet()) {
                            if(entry.getKey().equals("dislike_item")|entry.getKey().equals("like_item")){
                                m.put("dislike_item",bd.getText().toString());
                                m.put("like_item",bl.getText().toString());
                            }else{
                                m.put(entry.getKey().toString(),entry.getValue());
                            }
                        }
                        Moi_tovary.d.set(pos,m);
                    }
                }


                //если дизлайков слишком дохера
                if(Integer.valueOf(bd.getText().toString())>Integer.valueOf(bl.getText().toString())){
                    if ((Integer.valueOf(bd.getText().toString())-Integer.valueOf(bl.getText().toString())) > Main.MAX_DIZLAYK) {
                        fon.setBackgroundColor(Main.COLOR_POST_DELETE);
                        del.setVisibility(View.VISIBLE);
                    } else {
                        del.setVisibility(View.GONE);
                        //если наш товар то окрасим в свой цвет или окрасим по четности
                        if (Main.ID_DEVISE.equals(data.get(pos).get("id_user").toString())) {
                            fon.setBackgroundColor(Main.COLOR_MY_POST);
                        } else {
                            fon.setBackgroundColor((pos % 2 == 0) ? Main.COLOR_POST : Main.COLOR_POST2);
                        }
                    }
                }

            } else {
                SuperToast.create(context, "Ошибка", SuperToast.Duration.SHORT,
                        Style.getStyle(Style.GREEN, SuperToast.Animations.SCALE)).show();
            }
        }
    }

    public class Add_coment_item extends AsyncTask<ArrayList, Void, String> {
        TextView tx;
        int pos;
        @Override
        protected String doInBackground(ArrayList... params) {
            tx= (TextView) params[0].get(2);
            pos = (int)params[0].get(3);

            String loginURL = "http://i9027296.bget.ru/db_add_coment.php";
            String loginURL_conect = "http://i9027296.bget.ru/db_connect.php"; // если база работает

            URL url = null;
            try {
                url = new URL(loginURL_conect);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                connection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int statusCode = 0;
            try {
                statusCode = connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (statusCode == 200) {
                //0 - item_id
                //1 - coment

                Connection.Response response = null;
                try {
                    //делаем пост запрос
                    response = Jsoup.connect(loginURL)
                            .data("item_id", params[0].get(0).toString())
                            .data("koment", params[0].get(1).toString())
                            .method(Connection.Method.POST)
                            .followRedirects(true)
                            .timeout(5*1000)
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String otv = null;
                try {
                    otv = response.parse().select("otvet_connect").text();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (otv.equals("vso norm")) {
                    try {
                        otv = response.parse().select("otvet_add_db").text();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //если всё пучком обновим чексумму нашу
                    try {
                        Main.CHEKSUMMA = response.parse().select("summa").text();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    return otv;
                } else {
                    return null;
                }
            } else {

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s==null){
                SuperToast.create(context, "Ошибка", SuperToast.Duration.SHORT,
                        Style.getStyle(Style.GREEN, SuperToast.Animations.SCALE)).show();
            }else {
                //устанавливаем чтобы было видно
                  tx.setText(s.replace("&", "\n"));
                //и обновляем адаптер


                //делаем кусочек какашки
                Map<String,Object> m =new HashMap<String,Object>();

                //смотрим че щас открыто и обновляем нужное значение
                if(Main.page_open==0){  //новые товыры
                    //теперь в цикле нужно получить все старые какашки и записать в новый кусок
                    for (Map.Entry entry : New_tofary.d.get(pos).entrySet()) {
                        if(entry.getKey().equals("koment")){
                            m.put("koment",s);
                        }else{
                            m.put(entry.getKey().toString(),entry.getValue());
                        }
                    }
                    //замазываем ей старое
                    New_tofary.d.set(pos,m);
                }
                if(Main.page_open==1){  //фильтр
                    //теперь в цикле нужно получить все старые какашки и записать в новый кусок
                    for (Map.Entry entry : Kategorii.d.get(pos).entrySet()) {
                        if(entry.getKey().equals("koment")){
                            m.put("koment",s);
                        }else{
                            m.put(entry.getKey().toString(),entry.getValue());
                        }
                    }
                    Kategorii.d.set(pos,m);
                }
                if(Main.page_open==2){  //мои товыры
                    //теперь в цикле нужно получить все старые какашки и записать в новый кусок
                    for (Map.Entry entry : Moi_tovary.d.get(pos).entrySet()) {
                        if(entry.getKey().equals("koment")){
                            m.put("koment",s);
                        }else{
                            m.put(entry.getKey().toString(),entry.getValue());
                        }
                    }
                    Moi_tovary.d.set(pos,m);
                }

            }
        }
    }


}
