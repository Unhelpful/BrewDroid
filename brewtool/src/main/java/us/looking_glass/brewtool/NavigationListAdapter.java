package us.looking_glass.brewtool;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chshrcat on 3/31/14.
 */
public class NavigationListAdapter extends BaseExpandableListAdapter {
    private final static String TAG = NavigationListAdapter.class.getSimpleName();
    private final static int[] STATE_EXPANDED = { android.R.attr.state_expanded };
    private final LayoutInflater inflater;
    private final int groupLayout;
    private final int emptyGroupLayout;
    private final int expandedGroupLayout;
    private final int childLayout;
    private final List<Group> groups = new ArrayList<Group>();

    public NavigationListAdapter(Context context, int groupLayout, int emptyGroupLayout, int childLayout) {
        this(context, groupLayout, emptyGroupLayout, groupLayout, childLayout);
    }

    public NavigationListAdapter(Context context, int groupLayout, int emptyGroupLayout, int expandedGroupLayout, int childLayout) {
        this.inflater = LayoutInflater.from(context);
        this.groupLayout = groupLayout;
        this.emptyGroupLayout = emptyGroupLayout;
        this.expandedGroupLayout = expandedGroupLayout;
        this.childLayout = childLayout;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if (i >= groups.size())
            return 0;
        Group group = groups.get(i);
        if (group == null)
            return 0;
        return group.children == null ? 0 : group.children.length;
    }

    @Override
    public Object getGroup(int i) {
        return getGroupId(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return getChildId(i, i2);
    }

    @Override
    public long getGroupId(int i) {
        if (i >= groups.size())
            return 0;
        Group group = groups.get(i);
        return group == null ? 0 : group.group;
    }

    @Override
    public long getChildId(int i, int i2) {
        if (i >= groups.size())
            return 0;
        Group group = groups.get(i);
        if (group == null || group.children == null)
            return 0;
        return i2 < group.children.length ? group.children[i2] : 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private View newGroupView(boolean isEmpty, boolean isExpanded, ViewGroup parent) {
        return inflater.inflate(isEmpty ? emptyGroupLayout : (isExpanded ? expandedGroupLayout : groupLayout), parent, false);
    }

    private View newChildView(ViewGroup parent) {
        return inflater.inflate(childLayout, parent, false);
    }

    @Override
    public int getGroupType(int groupPosition) {
        return getChildrenCount(groupPosition) == 0 ? 1 : 0;
    }

    @Override
    public int getGroupTypeCount() {
        return 2;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (i >= groups.size())
            return null;
        Group group = groups.get(i);
        view = view == null ? newGroupView(group.children == null || group.children.length == 0, b, viewGroup) : view;
        ImageView indicator = (ImageView) view.findViewById(R.id.navigationIndicator);
        if (indicator != null) {
            Drawable indicatorDrawable = indicator.getDrawable();
            if (indicatorDrawable != null) {
                if (b)
                    indicatorDrawable.setState(STATE_EXPANDED);
            }
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(group.group);
        return view;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
        view = view == null ? newChildView(viewGroup) : view;
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText((int) getChildId(i, i2));
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    void addGroup(int group, int... children) {
        Logd("addGroup %s %s", group, children);
        groups.add(new Group(group, children.length > 0 ? children : null));
    }

    private static class Group {
        private final int group;
        private final int[] children;

        private Group(int group, int[] children) {
            this.group = group;
            this.children = children;
        }
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
