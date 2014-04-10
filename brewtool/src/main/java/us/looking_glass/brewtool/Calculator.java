package us.looking_glass.brewtool;

import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class Calculator extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private final static String TAG = Calculator.class.getSimpleName();
    final static boolean debug = true;
    private ActionBarDrawerToggle drawerToggle;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private AdView adView;

    private NavigationListAdapter navigationAdapter;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();

        navigationAdapter = new NavigationListAdapter(this, R.layout.navigation_group_item, R.layout.navigation_toplevel_item, R.layout.navigation_child_item);
        navigationAdapter.addGroup(R.string.blending);
        navigationAdapter.addGroup(
                R.string.convert,
                R.string.concentration,
                R.string.refractivity,
                R.string.density,
                R.string.acidity,
                R.string.alcohol,
                R.string.volume,
                R.string.mass,
                R.string.temperature
        );

        NavigationListView navView = (NavigationListView) findViewById(R.id.navigation);
        navView.setAdapter(navigationAdapter, true);
        navView.setOnItemClickListener(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer);

        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(drawerToggle);

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        LinearLayout topContainer = (LinearLayout) findViewById(R.id.topContainer);
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(Ads.adUnitId);
        LinearLayout.LayoutParams adLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        adLayout.weight = 0;
        adView.setLayoutParams(adLayout);
        topContainer.addView(adView);
        adView.loadAd(Ads.getAdRequest());
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onPause() {
        if (adView != null)
            adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null)
            adView.resume();
    }


    @Override
    protected void onDestroy() {
        if (adView != null)
            adView.destroy();
        super.onDestroy();
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
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment current = fragmentManager.findFragmentById(R.id.container);
        switch((int) id) {
            case R.string.temperature:
            case R.string.density:
            case R.string.alcohol:
            case R.string.acidity:
            case R.string.volume:
            case R.string.mass:
            case R.string.concentration:
            case R.string.refractivity:
                if (!(current instanceof  ConversionFragment)) {
                    Fragment newFragment = new ConversionFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, newFragment)
                            .commit();
                    current = newFragment;
                }
                ConversionFragment conversionFragment = (ConversionFragment) current;
                conversionFragment.setUnits((int) id);
                break;
            case R.string.blending:
                if (current != null)
                    fragmentManager.beginTransaction()
                        .detach(current)
                        .commit();
        }
        drawer.closeDrawer(Gravity.START);
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
