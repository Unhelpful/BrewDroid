package us.looking_glass.brewdroid;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Calculator extends ActionBarActivity implements ActionBar.OnNavigationListener {
    private final static String TAG = Calculator.class.getSimpleName();
    final static boolean debug = true;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        Logd("getSupportActionBar");
        actionBar.setDisplayShowTitleEnabled(false);
        Logd("setDisplayShowTitleEnabled");
        CategorySpinnerAdapter actionsAdapter = new CategorySpinnerAdapter(
                actionBar.getThemedContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1);

        float metric = getResources().getDisplayMetrics().density;
        actionsAdapter.add(new CategorySpinnerModel(getString(R.string.convert), 0, true));
        actionsAdapter.add(new CategorySpinnerModel(getString(R.string.temperature), R.string.temperature, false));
        actionsAdapter.add(new CategorySpinnerModel(getString(R.string.density), R.string.density, false));
        actionsAdapter.add(new CategorySpinnerModel(getString(R.string.alcohol), R.string.alcohol, false));
        actionsAdapter.add(new CategorySpinnerModel(getString(R.string.acidity), R.string.acidity, false));
        actionsAdapter.add(new CategorySpinnerModel(getString(R.string.volume), R.string.volume, false));
        actionsAdapter.add(new CategorySpinnerModel(getString(R.string.weight), R.string.weight, false));
        actionsAdapter.add(new CategorySpinnerModel(getString(R.string.concentration), R.string.concentration, false));
        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                actionsAdapter,
                this);
        Logd("setListNavigationCallbacks");
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        Logd("setNavigationMode");

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calculator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        switch((int) id) {
            case R.string.temperature:
            case R.string.density:
            case R.string.alcohol:
            case R.string.acidity:
            case R.string.volume:
            case R.string.weight:
            case R.string.concentration:
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment current = fragmentManager.findFragmentById(R.id.container);
                if (current instanceof  ConversionFragment) {
                    ConversionFragment conversionFragment = (ConversionFragment) current;
                    conversionFragment.setUnits((int) id);
                } else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new ConversionFragment((int) id))
                            .commit();
                }
        }
        return true;
    }

//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_calculator, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
//            return rootView;
//        }
//    }

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
