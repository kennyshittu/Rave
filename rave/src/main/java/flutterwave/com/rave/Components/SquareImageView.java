package flutterwave.com.rave.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Shittu on 20/08/16.
 */
public class SquareImageView extends ImageView {
    private boolean mSquareByHeight;
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void init(final AttributeSet attrs) {
        if (attrs != null) {
            String packageName = "http://schemas.android.com/apk/res-auto";

            mSquareByHeight = attrs.getAttributeBooleanValue(packageName, "squareViewByHeight", true);
        }
    }

    int squareDim = 1000000000;

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int h = this.getMeasuredHeight();
        int w = this.getMeasuredWidth();
        int curSquareDim = mSquareByHeight ? h : w;

        if(curSquareDim < squareDim)
        {
            squareDim = curSquareDim;
        }

        Log.d("MyApp", "h "+h+"w "+w+"squareDim "+squareDim);


        setMeasuredDimension(squareDim, squareDim);

    }
}
