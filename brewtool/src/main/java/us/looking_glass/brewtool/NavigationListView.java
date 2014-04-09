package us.looking_glass.brewtool;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

/**
 * Created by chshrcat on 4/2/14.
 */
public class NavigationListView extends ExpandableListView {
    private int checkedGroupPos = -1;
    private int checkedChildPos = -1;
    private long checkedId = -1;

    private final static String TAG = NavigationListView.class.getSimpleName();

    public NavigationListView(Context context) {
        this(context, null);
    }

    public NavigationListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnChildClickListener(null);
        setOnGroupExpandListener(null);
        setOnGroupClickListener(null);
        setGroupIndicator(null);
    }

    public void setAdapter(ExpandableListAdapter adapter, boolean widthFromContents) {
        super.setAdapter(adapter);
        if (widthFromContents)
            setWidthFromContents();
    }

    public void setWidthFromContents() {
        ExpandableListAdapter adapter = getExpandableListAdapter();
        int maxWidth = 0;
        int width;
        int groupCount = adapter.getGroupCount();
        View v;
        for (int i = 0; i < groupCount; i++) {
            v = adapter.getGroupView(i, false, null, this);
            v.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            width = v.getMeasuredWidth();
            if (width > maxWidth)
                maxWidth = width;
            int childrenCount = adapter.getChildrenCount(i);
            for (int j = 0; j < childrenCount; j++) {
                v = adapter.getChildView(i, j, false, null, this);
                v.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                width = v.getMeasuredWidth();
                if (width > maxWidth)
                    maxWidth = width;
            }
        }
        ViewGroup.LayoutParams lp = getLayoutParams();
        super.onMeasure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        maxWidth += getListPaddingLeft() + getListPaddingRight() + getVerticalScrollbarWidth();
        lp.width = maxWidth;    }

    @Override
    public void setOnGroupExpandListener(final OnGroupExpandListener listener) {
        OnGroupExpandListener wrapper = new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                if (checkedGroupPos == i) {
                    if (checkedChildPos != -1 && getExpandableListAdapter().getChildId(checkedGroupPos, checkedChildPos) == checkedId) {
                        int index = getFlatListPosition(ExpandableListView.getPackedPositionForChild(checkedGroupPos, checkedChildPos));
                        setItemChecked(index, true);
                    } else {
                        checkedGroupPos = -1;
                        checkedChildPos = -1;
                        checkedId = -1;
                    }
                }
                if (listener != null)
                    listener.onGroupExpand(i);
            }
        };
        super.setOnGroupExpandListener(wrapper);
    }

    @Override
    public void setOnGroupClickListener(final OnGroupClickListener listener) {
        OnGroupClickListener wrapper = new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View view, int pos, long id) {
                int index = parent.getFlatListPosition(getPackedPositionForGroup(pos));
                if (getExpandableListAdapter().getChildrenCount(pos) == 0) {
                    parent.setItemChecked(index, true);
                    checkedGroupPos = pos;
                    checkedChildPos = -1;
                    checkedId = id;
                    OnItemClickListener itemClickListener = getOnItemClickListener();
                    if (itemClickListener != null)
                        itemClickListener.onItemClick(parent, view, index, id);
                    if (listener != null)
                        return listener.onGroupClick(parent, view, pos ,id);
                }
                return false;
            }
        };
        super.setOnGroupClickListener(wrapper);
    }

    @Override
    public void setOnChildClickListener(final OnChildClickListener listener) {
        OnChildClickListener wrapper = new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                parent.setItemChecked(index, true);
                checkedGroupPos = groupPosition;
                checkedChildPos = childPosition;
                checkedId = id;
                OnItemClickListener itemClickListener = getOnItemClickListener();
                if (itemClickListener != null)
                    itemClickListener.onItemClick(parent, view, index, id);
                if (listener != null)
                    return listener.onChildClick(parent, view, groupPosition, childPosition, id);
                else
                    return false;
            }
        };
        super.setOnChildClickListener(wrapper);
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
