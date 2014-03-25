package us.looking_glass.brewdroid;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by chshrcat on 3/24/14.
 */
public class UnitSpinnerAdapter extends ArrayAdapter <Measurements.Unit> {
    private final int textViewResourceId;

    public UnitSpinnerAdapter(Context context, int resource) {
        super(context, resource);
        textViewResourceId = -1;
    }

    public UnitSpinnerAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.textViewResourceId = textViewResourceId;
    }

    public UnitSpinnerAdapter(Context context, int resource, Measurements.Unit[] objects) {
        super(context, resource, objects);
        textViewResourceId = -1;
    }

    public UnitSpinnerAdapter(Context context, int resource, int textViewResourceId, Measurements.Unit[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
    }

    public UnitSpinnerAdapter(Context context, int resource, List<Measurements.Unit> objects) {
        super(context, resource, objects);
        textViewResourceId = -1;
    }

    public UnitSpinnerAdapter(Context context, int resource, int textViewResourceId, List<Measurements.Unit> objects) {
        super(context, resource, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return setViewText(super.getDropDownView(position, convertView, parent), position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return setViewText(super.getView(position, convertView, parent), position);
    }

    private View setViewText(View view, int position) {
        TextView textView = (TextView) (textViewResourceId != -1 ?
                view.findViewById(textViewResourceId) :
                (TextView) view);
        Measurements.Unit item = getItem(position);
        textView.setText(getContext().getString(item.getAbbreviation()));
        return view;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getAbbreviation();
    }

}
