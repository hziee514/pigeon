package cn.wrh.smart.dove.view.snippet;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import cn.wrh.smart.dove.domain.bo.GroupBO;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class GroupViewHolder extends RecyclerView.ViewHolder {

    GroupViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(Object o) {
        Preconditions.checkNotNull(o);
        Preconditions.checkArgument(o instanceof  GroupBO, "Invalid type: " + o.getClass().getSimpleName());
        ((TextView)itemView).setText(o.toString());
    }

}
