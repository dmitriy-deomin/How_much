package dmitriy.deomin.how_much;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment;

public class Settings extends Activity implements ColorPickerDialogFragment.ColorPickerDialogListener {

    LinearLayout fon;
    private int DIALOG_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fon= (LinearLayout)findViewById(R.id.fon_settings);
        fon.setBackgroundColor(Main.FON);
        ((Button)findViewById(R.id.button_edit_color_my_postov)).setBackgroundColor(Main.COLOR_MY_POST);
        ((Button)findViewById(R.id.button_edit_fon_color)).setBackgroundColor(Main.FON);
        ((Button)findViewById(R.id.button_edit_color_text)).setBackgroundColor(Main.FON);
        ((Button)findViewById(R.id.button_edit_color_postov)).setBackgroundColor(Main.COLOR_POST);
        ((Button)findViewById(R.id.button_edit_color_postov2)).setBackgroundColor(Main.COLOR_POST2);
        ((Button)findViewById(R.id.button_edit_color_postov_delete)).setBackgroundColor(Main.COLOR_POST_DELETE);
        ((Button)findViewById(R.id.button_open_edit_fonts)).setBackgroundColor(Main.FON);

    }

    public void Edit_color_fon(View view) {
        DIALOG_ID = 0;
        ColorPickerDialogFragment f = ColorPickerDialogFragment
                .newInstance(DIALOG_ID, null, null, getResources().getColor(R.color.fon), true);

        f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme);
        f.show(getFragmentManager(), "d");
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        switch(dialogId) {
            case 0:
                Main.save_value_int("fon_color",color);
                Main.FON = color;
                Main.liner_boss.setBackgroundColor(color);
                fon.setBackgroundColor(color);
                ((Button)findViewById(R.id.button_edit_fon_color)).setBackgroundColor(Main.FON);
                break;

            case 1:
                Main.save_value_int("color_my_post",color);
                Main.COLOR_MY_POST = color;
                ((Button)findViewById(R.id.button_edit_color_my_postov)).setBackgroundColor(Main.COLOR_MY_POST);
                break;

            case 2:
                Main.save_value_int("color_post",color);
                Main.COLOR_POST = color;
                ((Button)findViewById(R.id.button_edit_color_postov)).setBackgroundColor(Main.COLOR_POST);
                break;
            case 3:
                Main.save_value_int("color_post2",color);
                Main.COLOR_POST2 = color;
                ((Button)findViewById(R.id.button_edit_color_postov2)).setBackgroundColor(Main.COLOR_POST2);
                break;
            case 4:
                Main.save_value_int("color_post_delete",color);
                Main.COLOR_POST_DELETE = color;
                ((Button)findViewById(R.id.button_edit_color_postov_delete)).setBackgroundColor(Main.COLOR_POST_DELETE);
                break;
            case 5:
                Main.save_value_int("color_text",color);
                Main.COLOR_TEXT = color;
                ((Button)findViewById(R.id.button_edit_color_text)).setTextColor(Main.COLOR_TEXT);
                ((Button)findViewById(R.id.button_edit_fon_color)).setTextColor(Main.COLOR_TEXT);
                ((Button)findViewById(R.id.button_edit_color_my_postov)).setTextColor(Main.COLOR_TEXT);
                ((Button)findViewById(R.id.button_edit_color_postov)).setTextColor(Main.COLOR_TEXT);
                ((Button)findViewById(R.id.button_edit_color_postov2)).setTextColor(Main.COLOR_TEXT);
                ((Button)findViewById(R.id.button_edit_color_postov_delete)).setTextColor(Main.COLOR_TEXT);
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    public void Edit_color_my_post(View view) {

        DIALOG_ID = 1;
        ColorPickerDialogFragment f = ColorPickerDialogFragment
                .newInstance(DIALOG_ID, null, null, getResources().getColor(R.color.color_my_post), true);

        f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme);
        f.show(getFragmentManager(), "d");

    }

    public void Edit_color_postov(View view) {
        DIALOG_ID = 2;
        ColorPickerDialogFragment f = ColorPickerDialogFragment
                .newInstance(DIALOG_ID, null, null, getResources().getColor(R.color.color_post), true);

        f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme);
        f.show(getFragmentManager(), "d");
    }

    public void Edit_color_postov2(View view) {
        DIALOG_ID = 3;
        ColorPickerDialogFragment f = ColorPickerDialogFragment
                .newInstance(DIALOG_ID, null, null, getResources().getColor(R.color.color_post2), true);

        f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme);
        f.show(getFragmentManager(), "d");
    }

    public void Edit_color_post_delete(View view) {
        DIALOG_ID = 4;
        ColorPickerDialogFragment f = ColorPickerDialogFragment
                .newInstance(DIALOG_ID, null, null, getResources().getColor(R.color.color_post_delete), true);

        f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme);
        f.show(getFragmentManager(), "d");
    }

    public void Edit_color_text(View view) {
        DIALOG_ID = 5;
        ColorPickerDialogFragment f = ColorPickerDialogFragment
                .newInstance(DIALOG_ID, null, null, getResources().getColor(R.color.color_text), true);

        f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme);
        f.show(getFragmentManager(), "d");
    }

    public void edit_fonts(View view) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.myalpha);
        view.startAnimation(anim);
        Intent i = new Intent(this,Fonts_vibor.class);
        startActivity(i);
    }
}
