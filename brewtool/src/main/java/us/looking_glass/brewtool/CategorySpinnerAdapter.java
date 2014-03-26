package us.looking_glass.brewtool;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by chshrcat on 12/17/13.
 */
public class CategorySpinnerAdapter extends ArrayAdapter<CategorySpinnerModel> {
    private final static String TAG = CategorySpinnerAdapter.class.getSimpleName();
    final static boolean debug = true;

    private final int textViewResourceId;

    public CategorySpinnerAdapter(Context context, int resource) {
        super(context, resource);
        this.textViewResourceId = -1;

    }

    public CategorySpinnerAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.textViewResourceId = textViewResourceId;
    }

    public CategorySpinnerAdapter(Context context, int resource, CategorySpinnerModel[] objects) {
        super(context, resource, objects);
        this.textViewResourceId = -1;
    }

    public CategorySpinnerAdapter(Context context, int resource, int textViewResourceId, CategorySpinnerModel[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
    }

    public CategorySpinnerAdapter(Context context, int resource, List<CategorySpinnerModel> objects) {
        super(context, resource, objects);
        this.textViewResourceId = -1;
    }

    public CategorySpinnerAdapter(Context context, int resource, int textViewResourceId, List<CategorySpinnerModel> objects) {
        super(context, resource, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        View result;
        if (convertView != null) {
            result = convertView;
        } else {
            FrameLayout frame = new FrameLayout(getContext());
            result = super.getDropDownView(position, null, parent);
            frame.setLayoutParams(result.getLayoutParams());
            result.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            frame.addView(result, 0);
            result = frame;
        }
        textView = (TextView) (textViewResourceId != -1 ?
                result.findViewById(textViewResourceId) :
                ((ViewGroup) result).getChildAt(0));
        CategorySpinnerModel item = getItem(position);
        result.setPadding(item.isHeader() ? 0 : (int)textView.getTextSize(),
                0, 0, 0);
        textView.setText(item.getLabel());
        int textColor = textView.getCurrentTextColor();
        textColor = textColor & 0xffffff | (item.isHeader() ? 0xb0000000 : 0xff000000);
        textView.setTextColor(textColor);
        return result;
    }

    @Override
    public boolean isEnabled(int position) {
        return !getItem(position).isHeader();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
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
    }
}
