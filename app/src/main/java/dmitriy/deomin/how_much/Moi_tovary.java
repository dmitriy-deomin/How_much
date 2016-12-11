package dmitriy.deomin.how_much;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dmitriy.deomin.how_much.adaptery.Adapter_tovary;

/**
 * Created by Admin on 23.04.2016.
 */
public class Moi_tovary extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {
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
    int Posicia;
    Adapter_tovary adapter_news;

    public static ArrayList<Map<String,Object>> d;

    ListView listView;
    Context context;
    SwipeRefreshLayout swipeLayout;


    public Moi_tovary(){
        Posicia = 0;
        Database database = new Database();
        database.execute("вперед");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.moi_tovary, null);

        context = v.getContext();

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


        listView =(ListView)v.findViewById(R.id.listView_moi_tovary);
        zapolnit_liswie();

        return v;
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
        }else {
            adapter_news.notifyDataSetChanged();
        }


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

        @Override
        protected String doInBackground(String... params) {

            Connection.Response response = null;
            try {
                response = Jsoup
                        .connect("http://i9027296.bget.ru/read_db_tovary.php")
                        .data("Zapros","SELECT * FROM how_much WHERE id_user = '"+Main.ID_DEVISE+"' AND id > "+String.valueOf(Posicia)+" ORDER BY id DESC LIMIT "+Main.POST_COUNT)
                        .method(Connection.Method.POST)
                        .timeout(5*1000)
                        .execute();

            } catch (IOException e) {
                e.printStackTrace();
            }
if(response!=null) {
    Elements elements = null;
    try {
        elements = response.parse().select("otvet_db");
    } catch (IOException e) {
        e.printStackTrace();
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
