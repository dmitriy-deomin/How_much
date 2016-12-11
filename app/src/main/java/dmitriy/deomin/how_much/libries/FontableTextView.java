package dmitriy.deomin.how_much.libries;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import dmitriy.deomin.how_much.R;

public class FontableTextView extends TextView {
    public FontableTextView(Context context) {
        super(context);
    }

    public FontableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        UiUtil.setCustomFont(this, context, attrs,
                R.styleable.com_example_foo_view_FontableTextView,
                R.styleable.com_example_foo_view_FontableTextView_font);
    }

    public FontableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        UiUtil.setCustomFont(this, context, attrs,
                R.styleable.com_example_foo_view_FontableTextView,
                R.styleable.com_example_foo_view_FontableTextView_font);
    }
}