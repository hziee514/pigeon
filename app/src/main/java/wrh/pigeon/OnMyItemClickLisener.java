package wrh.pigeon;

import android.view.View;

/**
 * Created by Administrator on 2016/1/9.
 */
public abstract interface OnMyItemClickLisener {

    public abstract void onMyItemClick(View view, int groupPosition, int childPosition);
    public abstract void onMyItemLongClick(View view, int groupPosition, int childPosition);
    public abstract void onMyItemDisclosure(View view, int groupPosition, int childPosition);

}
