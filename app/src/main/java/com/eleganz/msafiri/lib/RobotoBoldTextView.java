package com.eleganz.msafiri.lib;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by eleganz on 12/2/18.
 */

public class RobotoBoldTextView extends android.support.v7.widget.AppCompatTextView {

    public RobotoBoldTextView(Context context) {
        super(context);
    }

    public RobotoBoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"fonts/Roboto-Bold.ttf"));
    }


}
