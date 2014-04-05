package us.looking_glass.brewtool;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;
import android.widget.FrameLayout;

import java.util.Arrays;

/**
 * Created by chshrcat on 4/4/14.
 */
public class CheckableFrame extends FrameLayout implements Checkable {
    private final int[] STATE_CHECKED = { android.R.attr.state_checked };
    private static final String TAG = CheckableFrame.class.getSimpleName();

    private boolean checked = false;

    public CheckableFrame(Context context) {
        super(context);
    }

    public CheckableFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableFrame(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 3);
        if (checked)
            mergeDrawableStates(drawableState, STATE_CHECKED);
        Logd("onCreateDrawableState: new state %s", Arrays.toString(drawableState));
        return drawableState;
    }


    @Override
    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            refreshDrawableState();
            Drawable background = getBackground();
            Logd("background %s state %s", background, background != null && getBackground().isStateful() ? Arrays.toString(getBackground().getState()) : "none");
        }
        Logd("setChecked %s", checked);
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(this.checked);
    }

    private static void Logd(String text, Object... args) {
        if (args != null && args.length > 0)
            text = String.format(text, args);
        Log.d(TAG, text);
    }

    private static void Logv(String text, Object... args) {
        if (args != null && args.length > 0)
            text = String.format(text, args);
        Log.v(TAG, text);
    }}
