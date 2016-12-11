package dmitriy.deomin.how_much;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Admin on 17.05.2016.
 */
public class Fragment_ofline extends Fragment implements ViewSwitcher.ViewFactory {
    ShimmerTextView hTextView;
    Shimmer shimmer;
    ImageSwitcher imageSwitcher;
    Timer mTimer;
    MyTimerTask mMyTimerTask;
    int live;
    int position;
    int[] mImageIds;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_ofline, null);

        context = v.getContext();


        position = 0;
        mImageIds = new int[]{R.drawable.no_internet, R.drawable.a1, R.drawable.a2, R.drawable.a3};

        //устанавливаем шрифт
        Typeface face = Typeface.createFromAsset(context.getAssets(),"fonts/Tweed.ttf");

        hTextView = (ShimmerTextView) v.findViewById(R.id.textView_no_internet);
        hTextView.setTypeface(face);
        hTextView.setText("Нет интернет соединения");
        shimmer = new Shimmer();
        shimmer.start(hTextView);

        imageSwitcher  = (ImageSwitcher)v.findViewById(R.id.imageSwitcher);

        imageSwitcher.setFactory(this);

        imageSwitcher.setImageResource(mImageIds[0]);

        imageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                live= 0; //
                position = 0;

                if(isOnline(context)){
                    Main.myadapter.notifyDataSetChanged();
                    Main.viewPager.setAdapter(Main.myadapter);
                }else{
                    imageSwitcher.setEnabled(false);

                    mTimer = new Timer();
                    mMyTimerTask = new MyTimerTask();
                    mTimer.schedule(mMyTimerTask, 100, 70);

                    hTextView.setText(rand_fraza());
                    shimmer.start(hTextView);
                }
            }
        });

        return v;
    }

    public String rand_fraza(){
        String[] mas_fraz = {
                "Нету интернету",
                "Пробую все равно нету",
                "Опять нету",
                "Может и связи нету?",
                "А Wifi включен?",
                "Опять засада (, гдеж он есть",
                "Елки палки, нету",
                "Дайте интернету",
                "Это Чюбайс виноват",
                "Ды блин чё такое ёмоё"
        };

        int rnd  = random_nomer(0,mas_fraz.length-1);

        if(rnd<mas_fraz.length){
            return mas_fraz[rnd].toString();
        }else {
            return mas_fraz[0].toString();
        }
    }

    public int random_nomer(int o,int d){
        Random rand = new Random();
        int n = rand.nextInt(d)+o;
        return n;
    }

    @Override
    public View makeView() {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new
                ImageSwitcher.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        imageView.setBackgroundColor(0xFF000000);
        return imageView;
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
           getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(position<=3){
                        imageSwitcher.setImageResource(mImageIds[position]);
                        position ++;
                    }else {
                        position = 0 ;
                        live++;
                    }

                    if(live>=Main.TIME_GOVORIT_DINOZAVR){
                        if (mTimer != null) {
                            mTimer.cancel();
                        }
                        shimmer.start(hTextView);
                        hTextView.setText("Нет интернет соединения");
                        imageSwitcher.setEnabled(true);
                        imageSwitcher.setImageResource(mImageIds[0]);
                    }
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTimer != null) {
            mTimer.cancel();
        }
        imageSwitcher.setEnabled(true);
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