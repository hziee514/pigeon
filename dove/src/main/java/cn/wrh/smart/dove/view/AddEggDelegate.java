package cn.wrh.smart.dove.view;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.mvp.AbstractViewDelegate;
import cn.wrh.smart.dove.util.DateUtils;

/**
 * @author bruce.wu
 * @date 2018/7/17
 */
public class AddEggDelegate extends AbstractViewDelegate {

    private static final String[] COUNTS = {
            "1","2"
    };

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_add_egg;
    }

    @Override
    public void onInit() {
        getSpinner(R.id.count).setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.spinner_dropdown_item, COUNTS));

        ((EditText)findViewById(R.id.laying)).setText(DateUtils.getDateForEditor());
    }

    public void setCageSns(List<String> sns) {
        getSpinner(R.id.cage_sn).setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.spinner_dropdown_item, sns));
    }

    public void setCageSns(List<String> sns, String sn) {
        setCageSns(sns);
        int position = sns.indexOf(sn);
        if (position >= 0) {
            getSpinner(R.id.cage_sn).setSelection(position);
            getSpinner(R.id.cage_sn).setEnabled(false);
        }
    }

    public void setLayingDt(String dt) {
        getTextView(R.id.laying).setText(dt);
    }

    public void setCount(int count) {
        getSpinner(R.id.count).setSelection(count - 1);
    }

    public void setReviewDt(String dt) {
        getTextView(R.id.review).setText(dt);
    }

    public void setHatchDt(String dt) {
        getTextView(R.id.hatch).setText(dt);
    }

    public String getSn() {
        return (String)getSpinner(R.id.cage_sn).getSelectedItem();
    }

    public int getCount() {
        return getSpinner(R.id.count).getSelectedItemPosition() + 1;
    }

    public Date getLayingDt() {
        String dt = getTextView(R.id.laying).getText().toString();
        return DateUtils.getDateForEntity(dt);
    }

    public Date getReviewDt() {
        String dt = getTextView(R.id.review).getText().toString();
        return DateUtils.getDateForEntity(dt);
    }

    public Date getHatchDt() {
        String dt = getTextView(R.id.hatch).getText().toString();
        return DateUtils.getDateForEntity(dt);
    }

    public void showDatePicker(Calendar calendar, Consumer<DatePicker> consumer) {
        final DatePickerDialog dlg = new DatePickerDialog(getActivity(), null,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dlg.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), (dialog, which) -> dialog.dismiss());
        dlg.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), (dialog, which) -> {
            dialog.dismiss();
            consumer.accept(dlg.getDatePicker());
        });
        dlg.show();
    }

    public void showDeleteConfirm(Runnable consumer) {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.del_egg_confirm)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    consumer.run();
                })
                .show();
    }

}
