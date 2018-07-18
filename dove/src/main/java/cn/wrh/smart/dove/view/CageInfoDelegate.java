package cn.wrh.smart.dove.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.function.Consumer;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.dal.entity.EggEntity;
import cn.wrh.smart.dove.domain.model.CageModel;
import cn.wrh.smart.dove.domain.model.EggModel;
import cn.wrh.smart.dove.util.Tuple;
import cn.wrh.smart.dove.widget.MyExpandableListAdapter;

/**
 * @author bruce.wu
 * @date 2018/7/18
 */
public class CageInfoDelegate extends AbstractListDelegate {

    private Consumer<Tuple<Integer, Integer>> onItemClick;

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_cage_info;
    }

    @Override
    public void onInit() {
        super.onInit();
        getSpinner(R.id.status).setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.spinner_dropdown_item,
                getResources().getStringArray(R.array.cage_status)));
    }

    public void setOnItemClick(Consumer<Tuple<Integer, Integer>> listener) {
        this.onItemClick = listener;
    }

    public void setCageSn(String sn) {
        getTextView(R.id.cage_sn).setText(sn);
    }

    public void setStatus(CageModel.Status status) {
        getSpinner(R.id.status).setSelection(status.ordinal());
    }

    public void watchStatus(Consumer<CageModel.Status> listener) {
        getSpinner(R.id.status).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listener.accept(CageModel.Status.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected MyExpandableListAdapter createAdapter(List<String> groups, List<List<Object>> data) {
        return new FeedExpandableListAdapter(groups, data);
    }

    class FeedExpandableListAdapter extends MyExpandableListAdapter {

        private final String[] STAGES;

        FeedExpandableListAdapter(List<String> groups, List<List<Object>> children) {
            super(getActivity(), groups, children);
            STAGES = getResources().getStringArray(R.array.egg_stages);
        }

        @Override
        protected void bindChild(View view, int groupPosition, int childPosition) {
            EggEntity entity = (EggEntity)getChild(groupPosition, childPosition);
            setDt(view, entity.getStageDt());
            setStage(view, entity.getStage());
        }

        @Override
        protected void onItemClick(View view, int groupPosition, int childPosition) {
            if (onItemClick != null) {
                onItemClick.accept(new Tuple<>(groupPosition, childPosition));
            }
        }

        private void setDt(View view, String dt) {
            ((TextView)view.findViewById(R.id.text1)).setText(dt);
        }

        private void setStage(View view, EggModel.Stage stage) {
            ((TextView)view.findViewById(R.id.text2)).setText(STAGES[stage.ordinal()]);
        }

    }

}
