package wrh.pigeon;

/**
 * Created by Administrator on 2016/1/9.
 */
public abstract interface OnMyItemClickLisener {

    public abstract void onMyItemClick(int groupPosition, int childPosition);
    public abstract void onMyItemLongClick(int groupPosition, int childPosition);
    public abstract void onMyItemDisclosure(int groupPosition, int childPosition);

}
