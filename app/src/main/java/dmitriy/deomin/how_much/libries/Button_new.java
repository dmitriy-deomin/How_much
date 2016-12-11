package dmitriy.deomin.how_much.libries;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

import dmitriy.deomin.how_much.Main;

/**
 * Created by Admin on 30.07.2016.
 */
public class Button_new extends Button {
    public Button_new(Context context) {
        super(context);
    }

    public Button_new(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Button_new(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.setTextSize(20);
        this.setTypeface(Main.face);
        this.setTextColor(Main.COLOR_TEXT);
        super.onDraw(canvas);
    }
}
