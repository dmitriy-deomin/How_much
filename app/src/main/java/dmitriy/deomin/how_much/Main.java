package dmitriy.deomin.how_much;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.iangclifton.android.floatlabel.FloatLabel;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import dmitriy.deomin.how_much.libries.FloatingActionButton;
import dmitriy.deomin.how_much.libries.TiltEffectAttacher;


public class Main extends FragmentActivity implements View.OnClickListener {

    public static Context context;
    public static ViewPager viewPager;
    public static Myadapter myadapter;

    static LinearLayout liner_content;
    static RelativeLayout liner_add;

    public static LinearLayout liner_boss;
    static Animation poivlenie_contenta;
    static Animation ischeznovenie_contenta;
    static Animation poivlenie_add;
    static Animation ischeznovenie_add;

    //при обновлении будем в них передавать нужные данные для изменения
    public static String id_item;
    public static FloatLabel sity_adres;
    public static FloatLabel editText_name;
    public static FloatLabel editText_mani;
    public static FloatLabel editText_coment;
    public static Button button_add_update;

    Button filtr_tovar_main;

    public static int page_open;
    public static int page_scroll_new;

    static FloatingActionButton fabButton;

    static ImageView foto1;
    static boolean load_imag; // если выбирали картинку то true
    public static int width_d;
    public static int heigh_d;

    private CropImageView mCropImageView;
    private LinearLayout edit_liner_button;

    static public ProgressBar progressBar;

    String[] mags;

    static public int MAX_DIZLAYK; // пороговое значение дизлайкой после которого будет показана кнопка удалить пост и он окрасится
    static public String ID_DEVISE;// id пользователя
    static public int TIME_GOVORIT_DINOZAVR; // сколько по времени будет говорить динозавр
    static public int FON; //свет фона
    static public int COLOR_MY_POST;
    static public int COLOR_POST;
    static public int COLOR_POST2;
    static public int COLOR_POST_DELETE;
    static public int COLOR_TEXT;
    static public String POST_COUNT; // количество загружаемых постов
    static public String ID_TELEFONA; //
    static public String PASVORD_TELEFONA; //
    static public int RENAMES; // сколько сегодня меняли аккаунт
    static public int SIZE_RENAMES; // сколько можно раз менять
    static public int TIME_SHOW_REKLAMA; // сколько показывать рекламу

    private static final int PERMISSION_REQUEST_CAMERA = 11; // для разрешения камеры
    public boolean CAMERA_ACCESS;

    private static final int PERMISSION_REQUEST_FILE = 55; // для разрешения записи(главное)
    private static final int PERMISSION_REQUEST_FILE1 = 66; // повторное спрашивание разрешение записи
    public boolean FILE_ACCESS;

    //чек сумма таблицы при любом изменении будет менятся
    public static String CHEKSUMMA;
    GetCheksumma getCheksumma;
    boolean visi;//true при активном приложении
    boolean time_show_reklamma; //
    static Button refresh;

    static public Typeface face;

    //сохранялка
    public static SharedPreferences mSettings; // сохранялка
    public static final String APP_PREFERENCES = "mysettings"; // файл сохранялки

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        face = Typeface.createFromAsset(getAssets(), ((save_read("fonts").equals("")) ? "fonts/Tweed.ttf" : save_read("fonts")));

        //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width_d = display.getWidth();
        heigh_d = display.getHeight();


        edit_liner_button = (LinearLayout) findViewById(R.id.edit_liner_button);
        edit_liner_button.setVisibility(View.GONE);


        id_item = "";


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width_d - 20, width_d - 20);
        params.setMargins(10, 10, 10, 10);

        //нужные значения которые потом будет отрегулировать
        //****************************************************************
        TIME_SHOW_REKLAMA = 10; //секнды показа рекламы
        MAX_DIZLAYK = 7; // критическая разница между дизлайками и лайками
        TIME_GOVORIT_DINOZAVR = 7;
        POST_COUNT = "200"; // будет грузить в листвью посты порциями
        ID_TELEFONA = getUniqueID();
        PASVORD_TELEFONA = getUniqueID_pasvord();

        ////если не меняли ставим стандартный
        if (save_read("ID_DEVISE").equals("")) {
            ID_DEVISE = ID_TELEFONA + "&pazduplitel&" + PASVORD_TELEFONA;
        } else {
            ID_DEVISE = save_read("ID_DEVISE");
        }

        RENAMES = save_read_int("RENAMES");//Сколько уже раз поменяли аккаунт
        SIZE_RENAMES = 5; //Сколько можно раз менять
        CHEKSUMMA = "1";  //начальная чексумма бызы
        visi = true;  // приложение активно

        if (save_read_int("fon_color") == 0) {
            FON = getResources().getColor(R.color.fon);
        } else {
            FON = save_read_int("fon_color");
        }
        if (save_read_int("color_my_post") == 0) {
            COLOR_MY_POST = getResources().getColor(R.color.color_my_post);
        } else {
            COLOR_MY_POST = save_read_int("color_my_post");
        }
        if (save_read_int("color_post") == 0) {
            COLOR_POST = getResources().getColor(R.color.color_post);
        } else {
            COLOR_POST = save_read_int("color_post");
        }
        if (save_read_int("color_post2") == 0) {
            COLOR_POST2 = getResources().getColor(R.color.color_post2);
        } else {
            COLOR_POST2 = save_read_int("color_post2");
        }

        COLOR_POST_DELETE = ((save_read_int("color_post_delete") == 0) ? getResources().getColor(R.color.color_post_delete) : save_read_int("color_post_delete"));
        COLOR_TEXT = ((save_read_int("color_text") == 0) ? getResources().getColor(R.color.color_text) : save_read_int("color_text"));
        //***********************************************************************

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.med_no_photo);

        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        mCropImageView.setLayoutParams(params);
        mCropImageView.setAspectRatio(10, 10);
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setGuidelines(1);
        mCropImageView.setVisibility(View.GONE);
        mCropImageView.setImageBitmap(bitmap);


        progressBar = ((ProgressBar) findViewById(R.id.progressBar_add));

        myadapter = new Myadapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(myadapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // номер страницы
                fon_button(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        //анимация на кнопках*****************************************.
        final Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        ((Button) findViewById(R.id.Button_logo)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.startAnimation(anim);
                return false;
            }
        });
        ((Button) findViewById(R.id.button_new)).setTypeface(face);
        ((Button) findViewById(R.id.button_new)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.startAnimation(anim);
                return false;
            }
        });
        ((Button) findViewById(R.id.button_kategorii)).setTypeface(face);
        ((Button) findViewById(R.id.button_kategorii)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.startAnimation(anim);
                return false;
            }
        });
        ((Button) findViewById(R.id.button_moi_tovary)).setTypeface(face);
        ((Button) findViewById(R.id.button_moi_tovary)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.startAnimation(anim);
                return false;
            }
        });


        refresh = ((Button) findViewById(R.id.button_refresh_content));

        //**************************************************************

        TiltEffectAttacher.attach(findViewById(R.id.button_add));

        poivlenie_contenta = AnimationUtils.loadAnimation(context, R.anim.myrotate_poivlenie_contenta);
        ischeznovenie_contenta = AnimationUtils.loadAnimation(context, R.anim.myrotate_ischeznovenie_contenta);
        poivlenie_add = AnimationUtils.loadAnimation(context, R.anim.myrotate_poivlenie_add);
        ischeznovenie_add = AnimationUtils.loadAnimation(context, R.anim.myrotate_ischeznovenie_add);

        liner_content = (LinearLayout) findViewById(R.id.liner_content);
        liner_add = (RelativeLayout) findViewById(R.id.liner_add);
        liner_boss = (LinearLayout) findViewById(R.id.liner_bos);

        liner_boss.setBackgroundColor(FON);

        //add

        editText_name = (FloatLabel) findViewById(R.id.editText_name);
        editText_name.getEditText().setTypeface(face);
        editText_mani = (FloatLabel) findViewById(R.id.editText_mani);
        editText_mani.getEditText().setTypeface(face);
        editText_coment = (FloatLabel) findViewById(R.id.editText_coment);
        editText_coment.getEditText().setTypeface(face);
        sity_adres = (FloatLabel) findViewById(R.id.editText_sity);
        sity_adres.getEditText().setTypeface(face);

        button_add_update = (Button) findViewById(R.id.button_add);

        filtr_tovar_main = ((Button) findViewById(R.id.button_vibrat_tovar_main));
        filtr_tovar_main.setTypeface(face);

        //списки

        // товары
        //если есть сохранялка установим ранее выбраную позицию
        if (Main.save_read("spisok_tovary_f").length() > 3) {
            filtr_tovar_main.setText(Main.save_read("spisok_tovary_f"));
        } else {
            filtr_tovar_main.setText("Категория");
        }
        filtr_tovar_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrat_kategoriu();
            }
        });

        // города
        if (save_read("spisok_gorodov").length() > 2) { //если раньше выбирали город то пишем его
            ((Button) findViewById(R.id.button_gorod)).setText(save_read("spisok_gorodov"));
            ((Button) findViewById(R.id.button_gorod)).setTypeface(face);
        } else {
            ((Button) findViewById(R.id.button_gorod)).setText("Выбрать город");
        }

//адрес - улица или еще че

        sity_adres.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                save_value("sity_adres", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (save_read("sity_adres").length() > 2) {
            sity_adres.getEditText().setText(save_read("sity_adres"));
        }


        foto1 = (ImageView) findViewById(R.id.foto1);
        foto1.setLayoutParams(params);
        load_imag = false; // картинку пока не выбирали

        fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.b))
                .withButtonColor(Color.alpha(0))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 1, 1)
                .create();
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu(v);
            }
        });

        page_scroll_new = 0;
        page_open = 0;
        //по умолчанию откроем новые товары
        viewPager.setCurrentItem(page_open);
        //покрасим кнопку
        fon_button(page_open);


        //подсказки

        final ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);

        if (save_read_int("start_menu") == 0) {

            ToolTipView myToolTipView;

            ToolTip toolTip = new ToolTip()
                    .withText("Клик по логотипу откроет меню")
                    .withColor(COLOR_MY_POST)
                    .withTextColor(COLOR_TEXT)
                    .withShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_TOP);
            myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.Button_logo));
            myToolTipView.setOnToolTipViewClickedListener(new ToolTipView.OnToolTipViewClickedListener() {
                @Override
                public void onToolTipViewClicked(ToolTipView toolTipView) {
                    toolTipRelativeLayout.setVisibility(View.GONE);
                    save_value_int("start_menu", 1);
                }
            });
        } else {
            toolTipRelativeLayout.setVisibility(View.GONE);
        }


        //спросим сразу все разрешения

        CAMERA_ACCESS = false;
        //проверим разрешение еть ли для камеры, если есть откроем пункт камеры при добавлении фото
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);//выводит диалог, где пользователю предоставляется выбор
            } else {
                //камера разрешена
                CAMERA_ACCESS = true;
            }
        } else {
            //устройство не новое то все заебись
            //камера разрешена
            CAMERA_ACCESS = true;
        }


        FILE_ACCESS = false;
        //проверим разрешение еть ли для записи, если есть откроем пункт  добавлении фото
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_FILE);//выводит диалог, где пользователю предоставляется выбор
            } else {
                //запись разрешена
                FILE_ACCESS = true;
            }
        } else {
            //устройство не новое то все заебись
            //запись разрешена
            FILE_ACCESS = true;
        }







        //реклама
        final AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        time_show_reklamma = false;  //если бы черти isVisible mAdView сделали это херня бы не пригодилась

        //если нет интеренета скроем еЁ
        if (!isNetworkConnected()) {
            mAdView.setVisibility(View.GONE);
        } else {
            //через 10 секунд скроем её(пока так потом можно регулировать от количества постов)
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.v("TTT","ebasit");
                    if (visi) {
                        if (time_show_reklamma) {
                            mAdView.setVisibility(View.GONE); // скроем рекламу и поток больше не запустится
                        } else {
                            //иначе покажем
                            mAdView.setVisibility(View.VISIBLE);
                            time_show_reklamma = true; // это нужно чтоб знать что реклама показна
                            handler.postDelayed(this, 1000 * TIME_SHOW_REKLAMA); // через 10 секунд вырубим рекламу
                        }
                    }else {
                        handler.postDelayed(this, 1000 * 2); // если приложение свернуто пока в пустую погоняем поток
                    }
                }
            });
        }


        refresh.setVisibility(View.GONE);
        run_potok_proverki_update();

    }


    void run_potok_proverki_update() {

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (visi) {
                    if (refresh.getVisibility() == View.GONE) {
                        getCheksumma = new GetCheksumma();
                        getCheksumma.execute();
                        handler.postDelayed(this, 1000 * 20); // 20 секуннд
                    }
                }
            }
        });
    }

    public void Refresh_content(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.myscale));

        //сохраняем положение прокруток в листвьюшках

        //обновляем
        Refresh_new_tovary();


    }


    private class GetCheksumma extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            Connection.Response response = null;
            try {
                response = Jsoup.connect("http://i9027296.bget.ru/Get_Cheksumma.php")
                        .method(Connection.Method.POST)
                        .timeout(10 * 1000)  //10 секунд
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response != null) {
                try {
                    return response.parse().select("otvet_sum").text();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "0";
                }
            } else {
                return "0";
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("TTT", s);
            if (!CHEKSUMMA.equals(s)) {
                CHEKSUMMA = s;
                refresh.setVisibility(View.VISIBLE);
            } else {
                refresh.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void Vibrat_magazin(View view) {
        String[] result = {};

        if (!isOnline(context)) {
            Main.Toast_error("Нет итернет соединения");
        } else {
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
            result = set.toArray(new String[set.size()]);

        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(context).inflate(R.layout.custom_dialog_goroda_vibor, null);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ((LinearLayout) content.findViewById(R.id.fon_vibora_goroda)).setBackgroundColor(Main.FON);

        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(context, R.layout.delegat_list, result);
        ((ListView) content.findViewById(R.id.listView_dialog_goroda)).setAdapter(stringArrayAdapter);
        ((ListView) content.findViewById(R.id.listView_dialog_goroda)).setTextFilterEnabled(true);
        ((ListView) content.findViewById(R.id.listView_dialog_goroda)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String n = ((TextView) v).getText().toString();
                save_value("sity_adres", n);
                sity_adres.getEditText().setText(save_read("sity_adres"));
                alertDialog.cancel();
            }
        });

        ((EditText) content.findViewById(R.id.editText_dialog_goroda_poisk)).addTextChangedListener(new TextWatcher() {
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

        String[] mas = getResources().getStringArray(R.array.kategorii_tovarov_filtr);

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(context).inflate(R.layout.delegat_list_kategoria, null);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ((LinearLayout) content.findViewById(R.id.fon_list_kategorii)).setBackgroundColor(Main.FON);


        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(context, R.layout.delegat_list, mas);
        ((ListView) content.findViewById(R.id.listView_kategoria_vibor)).setAdapter(stringArrayAdapter);
        ((ListView) content.findViewById(R.id.listView_kategoria_vibor)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String n = ((TextView) v).getText().toString();
                Main.save_value("spisok_tovary_f", n);
                filtr_tovar_main.setText(Main.save_read("spisok_tovary_f"));
                alertDialog.cancel();
            }
        });
    }

    public void Menu_progi(View v) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        v.startAnimation(anim);


        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(context).inflate(R.layout.menu_progi, null);
        builder.setView(content);
        // builder.show();

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ((LinearLayout) content.findViewById(R.id.menu_progi)).setBackgroundColor(FON);

        ((Button) content.findViewById(R.id.button_setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                Intent i = new Intent(context, dmitriy.deomin.how_much.Settings.class);
                startActivity(i);
            }
        });
        ((Button) content.findViewById(R.id.button_user_kabinet)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                Intent i = new Intent(context, User_kabinet.class);
                startActivity(i);
            }
        });
        ((Button) content.findViewById(R.id.button_statistika)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                if (!isNetworkConnected()) {
                    Toast_error("Нету интернету");
                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
                    final View content = LayoutInflater.from(context).inflate(R.layout.statistika, null);
                    builder.setView(content);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    Get_statistika get_statistika = new Get_statistika();
                    String otv = null;
                    try {
                        otv = get_statistika.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    ((LinearLayout) content.findViewById(R.id.fon_statistika)).setBackgroundColor(FON);

                    if (otv != null) {
                        final String[] mas = otv.split("perenos");
                        //mas[0].toString() всего записей
                        //mas[1].toString() количество пользователей
                        //mas[2].toString() количество моих постов
                        //mas[3].toString() ид и пароль
                        ((TextView) content.findViewById(R.id.textView_statistika_count_post)).setText(mas[0].toString());
                        ((TextView) content.findViewById(R.id.textView_statistika_count_user)).setText(mas[1].toString());
                        ((TextView) content.findViewById(R.id.textView_statistika_count_my_post)).setText(mas[2].toString());
                        ((TextView) content.findViewById(R.id.textView_statistika_id_pasford_user)).setText(mas[3].toString());
                        ((TextView) content.findViewById(R.id.textView_statistika_id_pasford_user)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                putText(mas[3].toString());
                            }
                        });
                    } else {
                        ((TextView) content.findViewById(R.id.textView_statistika_count_post)).setText("error");
                        ((TextView) content.findViewById(R.id.textView_statistika_count_user)).setText("error");
                        ((TextView) content.findViewById(R.id.textView_statistika_count_my_post)).setText("error");
                        ((TextView) content.findViewById(R.id.textView_statistika_id_pasford_user)).setText("error");
                    }
                }

            }
        });
        ((Button) content.findViewById(R.id.button_abaut)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                Intent i = new Intent(context, Abaut.class);
                startActivity(i);
            }
        });

    }


    public void Open_dialog_add_foto(View view) {
        //если главное разрешение записи разрешено откроем окно выбора
        if (FILE_ACCESS) {
            startActivityForResult(getPickImageChooserIntent(), 200);
            foto1.setVisibility(View.GONE);
            mCropImageView.setVisibility(View.VISIBLE);
            edit_liner_button.setVisibility(View.VISIBLE);
        } else {
            //спросим еще раз разрешение
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_FILE1);//выводит диалог, где пользователю предоставляется выбор
                } else {
                    //запись разрешена
                    FILE_ACCESS = true;
                }
            }
        }
    }

    public Intent getPickImageChooserIntent() {

// Determine Uri of camera image to  save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();


        //если пользователь разрешил добавим камеру в список
        if (CAMERA_ACCESS) {
// collect all camera intents
            Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(res.activityInfo.packageName);
                if (outputFileUri != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                }
                allIntents.add(intent);
            }
        }

// collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

// the main intent is the last in the  list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

// Create a chooser from the main  intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

// Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);
            mCropImageView.setImageUriAsync(imageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CAMERA_ACCESS = true;
                } else {
                    CAMERA_ACCESS = false;
                }
                break;

            case PERMISSION_REQUEST_FILE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FILE_ACCESS = true;
                } else {
                    FILE_ACCESS = false;
                }
                break;

            case PERMISSION_REQUEST_FILE1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FILE_ACCESS = true;
                    //откроем окно добавление фото
                    Open_dialog_add_foto((View) foto1);
                } else {
                    FILE_ACCESS = false;
                }
                break;
        }
    }


    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    public void Gotovo_foto(View view) {
        Bitmap cropped = mCropImageView.getCroppedImage(500, 500);
        if (cropped != null) {
            mCropImageView.setVisibility(View.GONE);
            foto1.setVisibility(View.VISIBLE);
            foto1.setImageBitmap(cropped);
            edit_liner_button.setVisibility(View.GONE);
            load_imag = true;
        } else {
            load_imag = false;
        }
    }

    public void Vibrat_gorod(View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(context).inflate(R.layout.custom_dialog_goroda_vibor, null);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ((LinearLayout) content.findViewById(R.id.fon_vibora_goroda)).setBackgroundColor(Main.FON);

        String[] mas = getResources().getStringArray(R.array.goroda_rossii);
        ArrayList arrayList = new ArrayList();

        for (int i = 1; i < mas.length; i++) {
            arrayList.add(mas[i].toString());
        }


        final ArrayAdapter<String> stringArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.delegat_list, arrayList);

        ((ListView) content.findViewById(R.id.listView_dialog_goroda)).setAdapter(stringArrayAdapter);
        ((ListView) content.findViewById(R.id.listView_dialog_goroda)).setTextFilterEnabled(true);
        ((ListView) content.findViewById(R.id.listView_dialog_goroda)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String n = ((TextView) v).getText().toString();
                save_value("spisok_gorodov", n);
                ((Button) findViewById(R.id.button_gorod)).setText(save_read("spisok_gorodov"));
                alertDialog.cancel();
            }
        });

        ((EditText) content.findViewById(R.id.editText_dialog_goroda_poisk)).addTextChangedListener(new TextWatcher() {
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

    public void Rotate_foto(View view) {
        mCropImageView.rotateImage(90);
    }

    public class Myadapter extends FragmentStatePagerAdapter {

        public Myadapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ((!isNetworkConnected()) ? new Fragment_ofline() : new New_tofary());
                case 1:
                    return ((!isNetworkConnected()) ? new Fragment_ofline() : new Kategorii());
                case 2:
                    return ((!isNetworkConnected()) ? new Fragment_ofline() : new Moi_tovary());
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public void New_tovary(View v) {
        viewPager.setCurrentItem(0);
    }

    public void Kategorii(View v) {
        viewPager.setCurrentItem(1);
    }

    public void Moi_tovary(View v) {
        viewPager.setCurrentItem(2);
    }

    public static void Refresh_new_tovary() {
        myadapter.notifyDataSetChanged();
        viewPager.setAdapter(myadapter);
        viewPager.setCurrentItem(page_open);

        //скроем кнопу обновить
        refresh.setVisibility(View.GONE);

    }

    public static void Menu(View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        view.startAnimation(anim);

        button_add_update.setText("Добавить");
        editText_name.getEditText().setText("");
        editText_mani.getEditText().setText("");
        editText_coment.getEditText().setText("");
        id_item = "";

        if (liner_content.getVisibility() == View.GONE) {
            liner_add.startAnimation(ischeznovenie_add);
            liner_add.setVisibility(View.GONE);

            liner_content.startAnimation(poivlenie_contenta);
            liner_content.setVisibility(View.VISIBLE);

        } else {
            liner_content.startAnimation(ischeznovenie_contenta);
            liner_content.setVisibility(View.GONE);

            liner_add.startAnimation(poivlenie_add);
            liner_add.setVisibility(View.VISIBLE);
        }

    }

    public static void edit_update(String n, String m, String k, String s, String i) {

        button_add_update.setText("Обновить");

        editText_name.getEditText().setText(n);
        editText_mani.getEditText().setText(m);
        editText_coment.getEditText().setText(k);
        sity_adres.getEditText().setText(s);
        id_item = i;

        liner_content.startAnimation(ischeznovenie_contenta);
        liner_content.setVisibility(View.GONE);

        liner_add.startAnimation(poivlenie_add);
        liner_add.setVisibility(View.VISIBLE);

    }

    public static ImageView getFoto1() {
        return foto1;
    }

    public void add(View view) {

        //если кнопка готово не нажата у картинки нажмем её
        if (foto1.getVisibility() == View.GONE) {
            Gotovo_foto(findViewById(R.id.button_gotovo_foto));
        }

        ArrayList arrayList = new ArrayList();
        arrayList.add(0, "0");
        arrayList.add(1, "0");
        arrayList.add(2, editText_name.getEditText().getText().toString());
        arrayList.add(3, editText_mani.getEditText().getText().toString());
        arrayList.add(4, sity_adres.getEditText().getText().toString());
        arrayList.add(5, ID_DEVISE);
        arrayList.add(6, ((Main.save_read("spisok_tovary_f").length() > 3) ? Main.save_read("spisok_tovary_f") : "Все товары"));
        arrayList.add(7, editText_coment.getEditText().getText().toString());
        arrayList.add(8, save_read("spisok_gorodov"));

        if (!isNetworkConnected()) {
            Toast_error("Нет итернет соединения");
        } else {
            //основное действие
            Add_data add_data = new Add_data();
            add_data.execute(arrayList);
        }

    }

    public void fon_button(int button) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);

        page_open = button;

        switch (button) {
            case 0:
                ((Button) findViewById(R.id.button_new)).setTextColor(Color.RED);
                (this.findViewById(R.id.button_new)).startAnimation(anim);

                ((Button) findViewById(R.id.button_kategorii)).setTextColor(Main.COLOR_TEXT);
                ((Button) findViewById(R.id.button_moi_tovary)).setTextColor(Main.COLOR_TEXT);
                break;
            case 1:
                ((Button) findViewById(R.id.button_kategorii)).setTextColor(Color.RED);
                (this.findViewById(R.id.button_kategorii)).startAnimation(anim);

                ((Button) findViewById(R.id.button_new)).setTextColor(Main.COLOR_TEXT);
                ((Button) findViewById(R.id.button_moi_tovary)).setTextColor(Main.COLOR_TEXT);
                break;
            case 2:
                ((Button) findViewById(R.id.button_moi_tovary)).setTextColor(Color.RED);
                (this.findViewById(R.id.button_moi_tovary)).startAnimation(anim);

                ((Button) findViewById(R.id.button_kategorii)).setTextColor(Main.COLOR_TEXT);
                ((Button) findViewById(R.id.button_new)).setTextColor(Main.COLOR_TEXT);
                break;

        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            event.startTracking();
            Menu_progi(this.findViewById(R.id.Button_logo));
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        if (liner_content.getVisibility() == View.GONE) {

            if (liner_add.getVisibility() == View.VISIBLE) {
                liner_add.startAnimation(ischeznovenie_add);
                liner_add.setVisibility(View.GONE);
            }

            liner_content.startAnimation(poivlenie_contenta);
            liner_content.setVisibility(View.VISIBLE);
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
            final View content = LayoutInflater.from(context).inflate(R.layout.custon_dialog_exit, null);
            builder.setView(content);
            // builder.show();

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();

            ((Button) content.findViewById(R.id.button_dialog_exit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                    v.startAnimation(anim);
                    alertDialog.dismiss();
                    pizdec();

                }
            });
            ((Button) content.findViewById(R.id.button_dialog_cansel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                    v.startAnimation(anim);
                    alertDialog.dismiss();
                }
            });

        }

    }

    public void pizdec() {
        this.finish();
    }


    //id
    public String getUniqueID() {
        String myAndroidDeviceId = "123456789";
        myAndroidDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        //возьмём часть
        myAndroidDeviceId = myAndroidDeviceId.substring(0, myAndroidDeviceId.length() / 3);
        return myAndroidDeviceId;
    }

    //пароль
    public String getUniqueID_pasvord() {
        String myAndroidDeviceId = "123456789";
        myAndroidDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        //возьмём часть
        myAndroidDeviceId = myAndroidDeviceId.substring(myAndroidDeviceId.length() / 3);
        return myAndroidDeviceId;
    }

    //ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
    public static void save_value(String Key, String Value) { //сохранение строки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    public static String save_read(String key_save) {  // чтение настройки
        if (mSettings.contains(key_save)) {
            return (mSettings.getString(key_save, ""));
        } else {
            return "";
        }

    }

    public static void save_value_int(String Key, int Value) { //сохранение строки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(Key, Value);
        editor.apply();
    }

    public static int save_read_int(String key_save) {  // чтение настройки
        if (mSettings.contains(key_save)) {
            return (mSettings.getInt(key_save, 0));
        } else {
            return 0;
        }

    }

    //sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static void Toast(String mesag) {
        SuperToast.create(context, mesag, SuperToast.Duration.LONG,
                Style.getStyle(Style.GREEN, SuperToast.Animations.FLYIN)).show();
    }

    public static void Toast_error(String mesag) {
        SuperToast.create(context, mesag, SuperToast.Duration.LONG,
                Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
    }

    //запись
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    public void putText(String text) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
            Toast("Скопировали:" + text + " в буфер");
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = ClipData.newPlainText(text, text);
            clipboard.setPrimaryClip(clip);
            Toast("Скопировали:" + text + " в буфер");
        }
    }

    //чтение
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    public String getText() {
        String text = null;
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.getText() != null) {
                text = clipboard.getText().toString();
            }
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.getText() != null) {
                text = clipboard.getText().toString();
            }
        }
        return text;
    }


    @Override
    protected void onPause() {
        super.onPause();
        visi = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        visi = true;
        run_potok_proverki_update();
    }
}
