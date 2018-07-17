package cn.wrh.smart.dove.view;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.function.Consumer;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.mvp.AbstractViewDelegate;

/**
 * @author bruce.wu
 * @date 2018/7/17
 */
public class AddEggDelegate extends AbstractViewDelegate {
    @Override
    public int getRootLayoutId() {
        return R.layout.activity_add_egg;
    }

    @Override
    public void onInit() {
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

    private Spinner getSpinner(@IdRes int id) {
        return findViewById(id);
    }
}
