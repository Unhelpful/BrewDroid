package us.looking_glass.brewdroid;

/**
 * Created by chshrcat on 12/16/13.
 */
public class CategorySpinnerModel {
    private final String label;
    private final int id;
    private final boolean isHeader;

    public CategorySpinnerModel(String label, int id, boolean isHeader) {
        this.label = label;
        this.id = id;
        this.isHeader = isHeader;
    }

    public String getLabel() {
        return label;
    }

    public boolean isHeader() {
        return isHeader;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public int getId() {
        return id;
    }
}
