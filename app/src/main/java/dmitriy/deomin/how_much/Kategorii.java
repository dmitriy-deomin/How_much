package dmitriy.deomin.how_much;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import dmitriy.deomin.how_much.adaptery.Adapter_tovary;

public class Kategorii extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener{

    final String PRIORITET = "prioritet";
    final String FOTO = "foto";
    final String NAME = "name";
    final String MANI = "mani";
    final String GOROD = "gorod";
    final String DATA = "data";
    final String ID_USER = "id_user";
    final String KOTEGORIA = "kotegoria";
    final String KOMENT = "koment";
    final String GOROD_SPISOK="gorod_spisok";

    final String LIKE = "like_item";
    final String DISLIKE = "dislike_item";
    final String LIKE_ID_USER = "like_user_id"; // если 0 то лайков небыло, если + или -  соответсвенно были

    final String ID_ITEM =  "id_item";

    final String ON_OF_DELETE = "on_of_delete";

    String [] prioritet = {};
    String [] foto = {};
    String [] name =  {};
    String [] mani = {};
    String [] gorod = {};
    String [] data = {};
    String [] id_user = {};
    String [] kotegoria = {};
    String [] koment = {};
    String [] gorod_spisok= {};
    String [] id_item = {};

    String [] like_item  = {};
    String [] dislike_item = {};
    String [] like_user_id = {};



    private int preLast;
    public static  ArrayList<Map<String,Object>> d;
    int Posicia;
    Adapter_tovary adapter_news;


    ListView listView;
    static Context context;
    SwipeRefreshLayout swipeLayout;

    Button filtr_gorod;
    Button filtr_magazin;
    String filtr_kategoria;

    Button filtr_tovar;

    String[] mags;
    Map<String,String> key_value;

    Button vibrat_user;
    Button vibrat_name_tovar;

    public Kategorii() {
        // Required empty public constructor
        Posicia = 0;
        Database database = new Database();
        database.execute("вперед");
    }

    LinearLayout liner_filtr;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.kategorii, null);

       context = container.getContext();

        filtr_gorod = ((Button)v.findViewById(R.id.button_vibrat_gorod));
        filtr_magazin  =((Button)v.findViewById(R.id.button_vibrat_magazin));
        vibrat_name_tovar = ((Button)v.findViewById(R.id.button_vibrat_tovat_name));
        vibrat_user = ((Button)v.findViewById(R.id.button_vibrat_user));
        filtr_tovar = ((Button)v.findViewById(R.id.button_vibrat_tovar));

        //устанавливаем шрифт
        filtr_gorod.setTypeface(Main.face);
        filtr_magazin.setTypeface(Main.face);
        vibrat_user.setTypeface(Main.face);
        vibrat_name_tovar.setTypeface(Main.face);
        filtr_tovar.setTypeface(Main.face);
        //


        key_value = new HashMap<String, String>(1000);


        liner_filtr = (LinearLayout)v.findViewById(R.id.filtr_loaut);
        liner_filtr.setBackgroundColor(Main.COLOR_POST_DELETE);

        if(Main.save_read("poisk").length()>2){
            liner_filtr.setVisibility(View.VISIBLE);
        }else{
            liner_filtr.setVisibility(View.GONE);
        }

        ((Button)v.findViewById(R.id.button_show_filtr)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(liner_filtr.getVisibility()==View.GONE){
                    liner_filtr.setVisibility(View.VISIBLE);
                    Main.save_value("poisk","viden");
                }else {
                    liner_filtr.setVisibility(View.GONE);
                    Main.save_value("poisk","");
                }
            }
        });

        //обновление тянуть вниз-------------------------------------------------------------
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.conteiner_swipe);
        swipeLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        swipeLayout.setColorSchemeColors(
                Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);

        swipeLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //-------------------------------------------------------------------------------------------




        // города
        if(Main.save_read("spisok_gorodov_filtr").length()>2){ //если раньше выбирали город то пишем его
            filtr_gorod.setText(Main.save_read("spisok_gorodov_filtr"));
        }else{
            filtr_gorod.setText("Город");
        }
        filtr_gorod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrat_gorod();
            }
        });

        //магазин
        if(Main.save_read("spisok_mag_filtr").length()>2){ //если раньше выбирали магазин то пишем его
            filtr_magazin.setText(Main.save_read("spisok_mag_filtr"));
        }else{
            filtr_magazin.setText("Магазин");
        }
        filtr_magazin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrat_magazin();
            }
        });

        // товар  - фильтр
        //если есть сохранялка установим ранее выбраную позицию
        if(Main.save_read("spisok_tovary_filtr").length()>3){
            filtr_kategoria =Main.save_read("spisok_tovary_filtr");
            filtr_tovar.setText(filtr_kategoria);
        }else{
            filtr_tovar.setText("Категория");
        }
        filtr_tovar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrat_kategoriu();
            }
        });

        //пользователь
        //если есть сохранялка установим ранее выбраную позицию
        if(Main.save_read("vibrat_user").length()>3){
            vibrat_user.setText(Main.save_read("vibrat_user"));
        }else{
            vibrat_user.setText("Пользователь");
        }
        vibrat_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrat_user();
            }
        });

        //название товара
        //если есть сохранялка установим ранее выбраную позицию
        if(Main.save_read("vibrat_name_tovar").length()>1){
            vibrat_name_tovar.setText(Main.save_read("vibrat_name_tovar"));
        }else{
            vibrat_name_tovar.setText("Товар");
        }
        vibrat_name_tovar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrat_name_tovar();
            }
        });


        listView =(ListView)v.findViewById(R.id.listView_filtr);


        //подсказки

        final ToolTipRelativeLayout toolTipRelativeLayout_cat = (ToolTipRelativeLayout) v.findViewById(R.id.activity_main_tooltipRelativeLayout_kat);

        if(Main.save_read_int("start_kat")==0){

            ToolTipView myToolTipView;

            ToolTip toolTip = new ToolTip()
                    .withText("Фильтр")
                    .withColor(Main.COLOR_MY_POST)
                    .withTextColor(Main.COLOR_TEXT)
                    .withShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_TOP);
            myToolTipView = toolTipRelativeLayout_cat.showToolTipForView(toolTip, v.findViewById(R.id.button_show_filtr));
            myToolTipView.setOnToolTipViewClickedListener(new ToolTipView.OnToolTipViewClickedListener() {
                @Override
                public void onToolTipViewClicked(ToolTipView toolTipView) {
                    Main.save_value_int("start_kat",1);
                    toolTipRelativeLayout_cat.setVisibility(View.GONE);
                }
            });
        }else {
            toolTipRelativeLayout_cat.setVisibility(View.GONE);
        }




        zapolnit_liswie();
        return v;
    }

    private void Vibrat_name_tovar() {
        String[] names_mass = new String[0];
        String[] result ={};

        if(!isOnline(context)){
            Main.Toast_error("Нету интернету");
        }else {
            Get_name_tovary mas_names = new Get_name_tovary();

            try {
                names_mass = mas_names.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            //удаляем повторы
            Set<String> set = new HashSet<String>(Arrays.asList(names_mass));
            result = set.toArray(new String[set.size()+1]);
            result[result.length-1]=result[0];
            result[0]="Все товары";

        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View content = LayoutInflater.from(context).inflate(R.layout.custom_dialog_goroda_vibor, null);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ((LinearLayout)content.findViewById(R.id.fon_vibora_goroda)).setBackgroundColor(Main.FON);

        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(context,R.layout.delegat_list,result);
        ((ListView)content.findViewById(R.id.listView_dialog_goroda)).setAdapter(stringArrayAdapter);
        ((ListView)content.findViewById(R.id.listView_dialog_goroda)).setTextFilterEnabled(true);
        ((ListView)content.findViewById(R.id.listView_dialog_goroda)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String n =((TextView) v).getText().toString();
                Main.save_value("vibrat_name_tovar",n);
                vibrat_name_tovar.setText(Main.save_read("vibrat_name_tovar"));
                alertDialog.cancel();
            }
        });

        ((EditText)content.findViewById(R.id.editText_dialog_goroda_poisk)).addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // текст только что изменили
                stringArrayAdapter.getFilter().filter(s);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // текст будет изменен
            }
            @Override
            public void afterTextChanged(Editable s) {
                // текст уже изменили
            }
        });
    }

    private void Vibrat_user() {
        String[] id_users_mass = new String[0];
        String[] result = {};

        if(!isOnline(context)){
            Main.Toast_error("Нету интернету");
        }else {
            Get_id_users mas_id_users = new Get_id_users();

            try {
                id_users_mass = mas_id_users.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            //удаляем повторы
            Set<String> set = new HashSet<String>(Arrays.asList(id_users_mass));
            result = set.toArray(new String[set.size()+1]);
            result[result.length-1]=result[0];
            result[0]="Все пользователи";

        }

        ArrayList mas = new ArrayList();


        for(String s :result){
            if(s.contains("&pazduplitel&")){
                String[] m = s.split("&pazduplitel&");
                key_value.put(m[0].toString(),m[1].toString());
                mas.add(m[0].toString());
            }else {
                key_value.put(s,s);
                mas.add(s);
            }
        }






        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View content = LayoutInflater.from(context).inflate(R.layout.custom_dialog_goroda_vibor, null);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ((LinearLayout)content.findViewById(R.id.fon_vibora_goroda)).setBackgroundColor(Main.FON);

        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(context,R.layout.delegat_list,mas);
        ((ListView)content.findViewById(R.id.listView_dialog_goroda)).setAdapter(stringArrayAdapter);
        ((ListView)content.findViewById(R.id.listView_dialog_goroda)).setTextFilterEnabled(true);
        ((ListView)content.findViewById(R.id.listView_dialog_goroda)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String n =((TextView) v).getText().toString();
                Main.save_value("vibrat_user",n);
                vibrat_user.setText(Main.save_read("vibrat_user"));
                alertDialog.cancel();
            }
        });

        ((EditText)content.findViewById(R.id.editText_dialog_goroda_poisk)).addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // текст только что изменили
                stringArrayAdapter.getFilter().filter(s);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // текст будет изменен
            }
            @Override
            public void afterTextChanged(Editable s) {
                // текст уже изменили
            }
        });
    }

    private void Vibrat_kategoriu() {

        String [] mas = getResources().getStringArray(R.array.kategorii_tovarov_filtr_1);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View content = LayoutInflater.from(context).inflate(R.layout.delegat_list_kategoria, null);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ((LinearLayout)content.findViewById(R.id.fon_list_kategorii)).setBackgroundColor(Main.FON);


        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(context,R.layout.delegat_list,mas);
        ((ListView)content.findViewById(R.id.listView_kategoria_vibor)).setAdapter(stringArrayAdapter);
        ((ListView)content.findViewById(R.id.listView_kategoria_vibor)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String n =((TextView) v).getText().toString();
                Main.save_value("spisok_tovary_filtr",n);
                filtr_tovar.setText(Main.save_read("spisok_tovary_filtr"));
                alertDialog.cancel();
            }
        });
    }


    public  void Vibrat_magazin(){
        String[] result ={};

        if(!isOnline(context)){
            Main.Toast_error("Нет итернет соединения");
        }else {
            Get_magazins get_magazins = new Get_magazins();

            try {
                mags = get_magazins.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            //удаляем повторы
            Set<String> set = new HashSet<String>(Arrays.asList(mags));
            result = set.toArray(new String[set.size()+1]);
            result[result.length-1]=result[0];
            result[0]="Все точки";

        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View content = LayoutInflater.from(context).inflate(R.layout.custom_dialog_goroda_vibor, null);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ((LinearLayout)content.findViewById(R.id.fon_vibora_goroda)).setBackgroundColor(Main.FON);

        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(context,R.layout.delegat_list,result);
        ((ListView)content.findViewById(R.id.listView_dialog_goroda)).setAdapter(stringArrayAdapter);
        ((ListView)content.findViewById(R.id.listView_dialog_goroda)).setTextFilterEnabled(true);
        ((ListView)content.findViewById(R.id.listView_dialog_goroda)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String n =((TextView) v).getText().toString();
                Main.save_value("spisok_mag_filtr",n);
                filtr_magazin.setText(Main.save_read("spisok_mag_filtr"));
                alertDialog.cancel();
            }
        });

        ((EditText)content.findViewById(R.id.editText_dialog_goroda_poisk)).addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // текст только что изменили
                stringArrayAdapter.getFilter().filter(s);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // текст будет изменен
            }
            @Override
            public void afterTextChanged(Editable s) {
                // текст уже изменили
            }
        });

    }


    public void Vibrat_gorod() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View content = LayoutInflater.from(context).inflate(R.layout.custom_dialog_goroda_vibor, null);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ((LinearLayout)content.findViewById(R.id.fon_vibora_goroda)).setBackgroundColor(Main.FON);

        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(context,R.layout.delegat_list,content.getResources().getStringArray(R.array.goroda_rossii));
        ((ListView)content.findViewById(R.id.listView_dialog_goroda)).setAdapter(stringArrayAdapter);
        ((ListView)content.findViewById(R.id.listView_dialog_goroda)).setTextFilterEnabled(true);
        ((ListView)content.findViewById(R.id.listView_dialog_goroda)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String n =((TextView) v).getText().toString();
                Main.save_value("spisok_gorodov_filtr",n);
                filtr_gorod.setText(Main.save_read("spisok_gorodov_filtr"));
                alertDialog.cancel();
            }
        });

        ((EditText)content.findViewById(R.id.editText_dialog_goroda_poisk)).addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // текст только что изменили
                stringArrayAdapter.getFilter().filter(s);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // текст будет изменен
            }
            @Override
            public void afterTextChanged(Editable s) {
                // текст уже изменили
            }
        });

    }



    @Override
    public void onRefresh() {

        if(!isOnline(context)){
            Main.myadapter.notifyDataSetChanged();
            Main.viewPager.setAdapter(Main.myadapter);
        }else{
            Main.page_scroll_new=0;
            Posicia=0;
            Database database = new Database();
            database.execute("вперед");
        }

    }

    public void zapolnit_liswie(){

        if(Posicia==0){
            d = new ArrayList<Map<String,Object>>(name.length);
        }


        Map<String,Object> m ;


        for(int i = 0;i< name.length;i++){
            m= new HashMap<String,Object>();
            m.put(PRIORITET,(prioritet.length>i)?prioritet[i]:"0");
            m.put(FOTO,(foto.length>i)?foto[i]:"-");
            m.put(NAME,(name.length>i)?name[i]:"-");
            m.put(MANI,(mani.length>i)?mani[i]:"-");
            m.put(GOROD,(gorod.length>i)?gorod[i]:"-"); //
            m.put(DATA,(data.length>i)?data[i]:"-"); //
            m.put(ID_USER,(id_user.length>i)?id_user[i]:"-"); //
            m.put(KOTEGORIA,(kotegoria.length>i)?kotegoria[i]:"-"); //
            m.put(KOMENT,(koment.length>i)?koment[i]:"-"); //
            m.put(GOROD_SPISOK,(gorod_spisok.length>i)?gorod_spisok[i]:"-"); //
            m.put(ID_ITEM,(id_item.length>i)?id_item[i]:"-");
            m.put(LIKE,(like_item.length>i)?like_item[i]:"0");
            m.put(DISLIKE,(dislike_item.length>i)?dislike_item[i]:"0");
            m.put(LIKE_ID_USER,(like_user_id.length>i)?like_user_id[i]:"0");

            //если дизлайков дохера сразу подсчитаем и запишем все
            m.put(ON_OF_DELETE,((Integer.valueOf(dislike_item[i])>Integer.valueOf(like_item[i]))?
                    (String.valueOf(Integer.valueOf(dislike_item[i])-Integer.valueOf(like_item[i]))): "0"));

            d.add(m);
        }

        if(Posicia==0){
            adapter_news = new Adapter_tovary(context,d, R.layout.delegat_tovary,null,null);
            ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter_news);
            animationAdapter.setAbsListView(listView);
            listView.setAdapter(animationAdapter);

            listView.setOnScrollListener(this);


            listView.post(new Runnable() {
                @Override
                public void run() {
                    listView.requestFocusFromTouch();
                    listView.setSelection(Main.page_scroll_new);
                }
            });
        }else{
            adapter_news.notifyDataSetChanged();
        }


    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView lw, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        final int lastItem = firstVisibleItem + visibleItemCount;
        if(lastItem == totalItemCount) {
            if(preLast!=lastItem){ //to avoid multiple calls for last item
                if(Integer.valueOf(Main.POST_COUNT)<=totalItemCount){
                    preLast = lastItem;
                    Posicia = preLast;
                    Database database = new Database();
                    database.execute("вперед");
                }
            }
        }
    }

    private class Database extends AsyncTask<String,Void,String> {

        String filtr= "";
        String zapros;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //перечеслятся параметры должны через and
            // и должно быть так
            //kotegoria=''   в скобочках выбранная категория
            //gorod = ''
            //gorod_spisok = ''

           //сохранёные в памяти предыдущего запуска параметры
//            Main.save_read("spisok_gorodov_filtr")
//            Main.save_read("spisok_mag_filtr")
//            Main.save_read("spisok_tovary_f")

            // id_user
            //Main.save_read("vibrat_user")  "Все пользователи"  "Пользователь"


            //name
            //Main.save_read("vibrat_name_tovar") "Все товары"  "Товар"




            //выбор города
            String g = "gopa";
            if(Main.save_read("spisok_gorodov_filtr").equals("Город")){
                g = "";
            }
            if(Main.save_read("spisok_gorodov_filtr").equals("Вся страна")){
                g = "";
            }
            if(Main.save_read("spisok_gorodov_filtr").equals("")){
                g = "";
            }
            if(g.length()>2){
                g = "gorod_spisok = '"+Main.save_read("spisok_gorodov_filtr")+"'";
            }
            //

            //выбор адреса
            String a = "gopa";
            if(Main.save_read("spisok_mag_filtr").equals("Магазин")){
                a="";
            }
            if(Main.save_read("spisok_mag_filtr").equals("Все точки")){
                a="";
            }
            if(Main.save_read("spisok_mag_filtr").equals("")){
                a="";
            }
            if(a.length()>2){
                a="gorod = '"+Main.save_read("spisok_mag_filtr")+"'";
            }
            //

            //выбор категории товаров
            String k="gopa";
            if(Main.save_read("spisok_tovary_filtr").equals("Все товары")){
                k="";
            }
            if(Main.save_read("spisok_tovary_filtr").equals("Категория")){
                k="";
            }
            if(Main.save_read("spisok_tovary_filtr").equals("")){
                k="";
            }
            if(k.length()>2){
                k="kotegoria='"+Main.save_read("spisok_tovary_filtr")+"'";
            }
            //

            //отдельный пользователь
            String u = "gopa_v_lesu";
            if(Main.save_read("vibrat_user").equals("Все пользователи")){
                u = "";
            }
            if(Main.save_read("vibrat_user").equals("Пользователь")){
                u = "";
            }
            if(Main.save_read("vibrat_user").equals("")){
                u = "";
            }
            if(u.length()>2){
                if(key_value!=null){
                    u="id_user = '"+Main.save_read("vibrat_user")+"&pazduplitel&"+key_value.get(Main.save_read("vibrat_user"))+"'";
                }else{
                    u="id_user = '"+Main.save_read("vibrat_user")+"'";
                }
            }


            //отдельный товар
            String t = "gopa_v_lesu";
            if(Main.save_read("vibrat_name_tovar").equals("Все товары")){
                t = "";
            }
            if(Main.save_read("vibrat_name_tovar").equals("Товар")){
                t = "";
            }
            if(Main.save_read("vibrat_name_tovar").equals("")){
                t = "";
            }
            if(t.length()>2){
                t="name = '"+Main.save_read("vibrat_name_tovar")+"'";
            }

//hhhhhhhhhhhh
            if(g.length()>2){
                if(a.length()>2|k.length()>2|u.length()>2|t.length()>2){
                    g = g+" and ";
                }
            }

            if(a.length()>2){
                if(k.length()>2|u.length()>2|t.length()>2){
                    a = a+" and ";
                }
            }

            if(k.length()>2){
                if(u.length()>2|t.length()>2){
                   k = k+" and ";
                }
            }

            if(u.length()>2){
                if(t.length()>2){
                    u = u+" and ";
                }
            }


            filtr = g+a+k+u+t;

            if(filtr.length()>2){
                zapros= "SELECT * FROM how_much WHERE id > "+String.valueOf(Posicia)+" AND "+filtr+" ORDER BY id DESC LIMIT "+Main.POST_COUNT;
            }else{
                zapros= "SELECT * FROM how_much WHERE id > "+String.valueOf(Posicia)+" ORDER BY id DESC LIMIT "+Main.POST_COUNT;
            }

            Log.d("TTT",filtr);


        }

        @Override
        protected String doInBackground(String... params) {


            Connection.Response response = null;
            try {
                response = Jsoup
                        .connect("http://i9027296.bget.ru/read_db_tovary.php")
                        .data("Zapros",zapros)
                        .method(Connection.Method.POST)
                        .execute();

            } catch (IOException e) {
                e.printStackTrace();
            }

            if(response!=null) {
                Elements elements= null;
                try {
                    elements = response.parse().select("otvet_db");
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                // и парсим тело
                pars(elements);
                return "ok";
            }else{
                return null;
            }
        }

        private void pars(Elements elements){
            prioritet = new String[elements.size()];
            foto = new String[elements.size()];
            name = new String[elements.size()];
            mani = new String[elements.size()];
            gorod = new String[elements.size()];
            data = new String[elements.size()];
            id_user = new String[elements.size()];
            kotegoria = new String[elements.size()];
            koment = new String[elements.size()];
            gorod_spisok = new String[elements.size()];
            id_item = new String[elements.size()];
            like_item = new String[elements.size()];
            dislike_item = new String[elements.size()];
            like_user_id = new String[elements.size()];


            for(int i = 0;i!=elements.size();i++){
                prioritet[i]= (elements.get(i).select("prioritet").text()==null)?"0":elements.get(i).select("prioritet").text();
                foto[i] = (elements.get(i).select("foto").text()==null)?"":elements.get(i).select("foto").text();
                name[i] = (elements.get(i).select("name").text()==null)?"-":elements.get(i).select("name").text();
                mani[i] = (elements.get(i).select("mani").text()==null)?"-":elements.get(i).select("mani").text();
                gorod[i] = (elements.get(i).select("gorod").text()==null)?"-":elements.get(i).select("gorod").text();
                data[i] = (elements.get(i).select("data").text()==null)?"-":elements.get(i).select("data").text();
                id_user[i] = (elements.get(i).select("id_user").text()==null)?"-":elements.get(i).select("id_user").text();
                kotegoria[i] = (elements.get(i).select("kotegoria").text()==null)?"-":elements.get(i).select("kotegoria").text();
                koment[i] = (elements.get(i).select("koment").text()==null)?"-":elements.get(i).select("koment").text();
                gorod_spisok[i]= (elements.get(i).select("gorod_spisok").text()==null)?"-":elements.get(i).select("gorod_spisok").text();
                id_item[i]=(elements.get(i).select("id").text()==null)?"-":elements.get(i).select("id").text();
                like_item[i]=(elements.get(i).select("like_item").text()==null)?"-":elements.get(i).select("like_item").text();
                dislike_item[i]=(elements.get(i).select("dislike_item").text()==null)?"-":elements.get(i).select("dislike_item").text();
                like_user_id[i]=(elements.get(i).select("like_user_id").text()==null)?"0":elements.get(i).select("like_user_id").text();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null){
                Main.Toast_error("Ошибка, возможно проблеммы со связью");
                swipeLayout.setRefreshing(false);
            }else {
                swipeLayout.setRefreshing(false);
                zapolnit_liswie();
            }
        }

    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
}