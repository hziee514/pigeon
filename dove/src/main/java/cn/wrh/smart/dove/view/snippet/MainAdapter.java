package cn.wrh.smart.dove.view.snippet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.bo.GroupBO;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class MainAdapter extends RecyclerView.Adapter {

    public interface ItemViewHolderFactory {

        ItemViewHolder create(View view);

    }

    private static final int TYPE_GROUP = 0;
    private static final int TYPE_ITEM = 1;

    private final Context context;

    /**
     * strong reference to data source
     */
    private final List<Object> data;

    private ItemViewHolderFactory factory;

    private OnItemClickListener clickListener;

    private OnItemLongClickListener longClickListener;

    public MainAdapter(Context context, List<Object> data) {
        this.context = context;
        this.data = data;
    }

    public MainAdapter setFactory(ItemViewHolderFactory factory) {
        this.factory = factory;
        return this;
    }

    public MainAdapter setItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
        return this;
    }

    public MainAdapter setItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
        return this;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_GROUP) {
            View view = LayoutInflater.from(context).inflate(R.layout.li_main_group, parent, false);
            return new GroupViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.li_main_item, parent, false);
            return factory.create(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_GROUP) {
            ((GroupViewHolder)holder).bind(data.get(position));
        } else {
            ((ItemViewHolder)holder).bind(position, data.get(position), clickListener, longClickListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object o = data.get(position);
        return (o instanceof GroupBO) ? TYPE_GROUP : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
