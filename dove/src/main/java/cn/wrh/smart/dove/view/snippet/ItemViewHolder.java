package cn.wrh.smart.dove.view.snippet;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.model.CageModel;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView text1;

    private final TextView text2;

    protected ItemViewHolder(View itemView) {
        super(itemView);
        text1 = itemView.findViewById(R.id.text1);
        text2 = itemView.findViewById(R.id.text2);
    }

    public void bind(Object o, OnItemClickListener listener) {
        bind(o);
        itemView.setOnClickListener(view -> listener.onItemClick(o));
    }

    protected void bind(Object o) {
        Preconditions.checkNotNull(o);
    }

    protected void setName(String name) {
        text1.setText(name);
    }

    protected void setStatus(CageModel.Status status) {
        int color = Color.BLACK;
        if (status == CageModel.Status.Healthy) {
            color = Color.GREEN;
        } else if (status == CageModel.Status.Sickly) {
            color = Color.RED;
        }
        final String[] STATUS = itemView.getResources().getStringArray(R.array.status);
        text2.setText(STATUS[status.ordinal()]);
        text2.setTextColor(color);
    }

}
