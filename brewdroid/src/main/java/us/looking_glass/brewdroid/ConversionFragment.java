package us.looking_glass.brewdroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chshrcat on 12/17/13.
 */
public class ConversionFragment extends Fragment implements AdapterView.OnItemSelectedListener, TextWatcher {
    private final static String TAG = ConversionFragment.class.getSimpleName();
    private final static NumberFormat formatter = new DecimalFormat("##0.####E0");
    final static boolean debug = true;

    private int id = -1;
    private ArrayAdapter<Measurements.Unit> adapter = null;
    private Measurements.Unit from = null;
    private Measurements.Unit to = null;
    private double value = 0;
    List<Measurements.Unit> units = new ArrayList<Measurements.Unit>();
    private Spinner toSpinner;
    private Spinner fromSpinner;
    private TextView outTextView;
    private EditText inEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.simple_conversion, container, false);
        adapter = new UnitSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner = (Spinner) view.findViewById(R.id.fromSpinner);
        fromSpinner.setAdapter(adapter);
        fromSpinner.setOnItemSelectedListener(this);
        toSpinner = (Spinner) view.findViewById(R.id.toSpinner);
        toSpinner.setAdapter(adapter);
        toSpinner.setOnItemSelectedListener(this);
        inEditText = (EditText) view.findViewById(R.id.valueIn);
        inEditText.addTextChangedListener(this);
        outTextView = (TextView) view.findViewById(R.id.valueOut);
        setUnits(this.id, true);
        return view;
    }

    void setUnits(int id) {
        if (this.id == -1)
            this.id = id;
        else
            setUnits(id, false);
    }

    void setUnits(int id, boolean force) {
        if (id == -1 || (!force && id == this.id))
            return;
        Measurements.Unit[] newUnits = null;
        switch(id) {
            case R.string.temperature:
                newUnits = Measurements.Temperature.values();
                break;
            case R.string.density:
                newUnits = Measurements.Density.values();
                break;
            case R.string.alcohol:
                newUnits = Measurements.AlcoholicStrength.values();
                break;
            case R.string.acidity:
                newUnits = Measurements.Acidity.values();
                break;
            case R.string.volume:
                newUnits = Measurements.Volume.values();
                break;
            case R.string.weight:
                newUnits = Measurements.Weight.values();
                break;
            case R.string.concentration:
                newUnits = Measurements.Concentration.values();
                break;
            case R.string.refractivity:
                newUnits = Measurements.Refractivity.values();
                break;
        }
        units.clear();
        units.addAll(Arrays.asList(newUnits));
        adapter.notifyDataSetChanged();
        this.id = id;
        from = newUnits[0];
        to = newUnits[0];
        toSpinner.setSelection(0);
        fromSpinner.setSelection(0);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.toSpinner:
                to = adapter.getItem(position);
                updateOutText();
                break;
            case R.id.fromSpinner:
                Measurements.Unit newFrom = adapter.getItem(position);
                value = newFrom.from(value, from);
                from = newFrom;
                inEditText.setText(String.format("%.3f", value));
                updateOutText();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (to == null || from == null)
            return;
        try {
            value = Float.parseFloat(s.toString());
            updateOutText();
        } catch (NumberFormatException e) {
        }
    }

    private void updateOutText() {
        outTextView.setText(formatter.format(to.from(value, from)));
    }

    @Override
    public void afterTextChanged(Editable s) {

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
