package cn.wrh.smart.dove.view.snippet;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.model.TaskModel;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {

    protected final ImageView image1;

    protected final TextView text1;

    protected final TextView text2;

    protected ItemViewHolder(View itemView) {
        super(itemView);
        image1 = itemView.findViewById(R.id.image1);
        text1 = itemView.findViewById(R.id.text1);
        text2 = itemView.findViewById(R.id.text2);
    }

    public void bind(Object o, OnItemClickListener listener) {
        bind(o);
        itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(o);
            }
        });
    }

    protected void bind(Object o) {
        Preconditions.checkNotNull(o);
    }

    protected void setName(String name) {
        text1.setText(name);
    }

}