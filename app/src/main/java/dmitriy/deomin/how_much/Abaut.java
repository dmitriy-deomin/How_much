package dmitriy.deomin.how_much;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.Timer;
import java.util.TimerTask;

public class Abaut extends Activity {

    ShimmerTextView hTextView;
    Shimmer shimmer;
    TextView textView;
    int live;
    Timer mTimer;
    MyTimerTask mMyTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abaut);

        //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ((RelativeLayout) findViewById(R.id.fon_abaut)).setBackgroundColor(Main.FON);

        hTextView = (ShimmerTextView) findViewById(R.id.ShimmerTextView);
        hTextView.setTypeface(Main.face);
        hTextView.setText(getVersion());

        shimmer = new Shimmer();
        shimmer.start(hTextView);

        textView = (TextView) findViewById(R.id.textView_abaut);
        textView.setText(getString(R.string.abaut_text).replace("+++++", "Чё почём " + getVersion()));
        textView.setVisibility(View.GONE);

        live = 0; // 2
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 1000, 1000);

    }

    public void pizdez() {
        if (mTimer != null) {
            mTimer.cancel();
        }

        shimmer.cancel();

        hTextView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }

    public void finish(View view) {
        this.finish();
    }


    public void opengruppa(View view) {
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://vk.com/chto_pochom"));
        startActivity(browseIntent);
    }

    private String getVersion() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "?";
        }
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    live++;
                    if (live >= 2) {
                        pizdez();
                    }
                }
            });
        }
    }
}
