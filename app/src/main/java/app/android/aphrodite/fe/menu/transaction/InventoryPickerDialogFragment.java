package app.android.aphrodite.fe.menu.transaction;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.menu.inventory.data.InventoryController;
import app.android.aphrodite.fe.menu.inventory.data.InventoryListAdapter;
import app.android.aphrodite.fe.menu.inventory.event.InventoryListFetchComplete;
import app.android.aphrodite.be.model.Inventory;
import app.android.aphrodite.be.model.TransactionItem;
import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryPickerDialogFragment extends DialogFragment {

    public interface Callback {
        void onItemSelected(TransactionItem item);
    }

    @BindView(R.id.txtSearch)       EditText txtSearch;
    @BindView(R.id.txtQty)          EditText txtQty;
    @BindView(R.id.lblNoData)       TextView lblNoData;
    @BindView(R.id.txtError)       TextView txtError;
    @BindView(R.id.lvList)          ListView lvList;
    @BindView(R.id.btnOk)           Button btnOk;
    @BindView(R.id.btnCancel)       Button btnCancel;

    InventoryController controller = new InventoryController(getContext());
    TransactionItem data;
    Callback callback;
    InventoryListAdapter adapter;
    private final long DELAY = 700; // milliseconds

    private final TextWatcher searchListener = new TextWatcher() {
        final Handler handler = new Handler();
        Runnable runnable;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            handler.removeCallbacks(runnable);
        }

        @Override
        public void afterTextChanged(Editable s) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    repopulateList();
                }
            };
            handler.postDelayed(runnable, DELAY);
        }
    };

    public static InventoryPickerDialogFragment newInstance(Callback callback, TransactionItem data) {
        InventoryPickerDialogFragment frag = new InventoryPickerDialogFragment();
        frag.callback = callback;
        frag.data = data;
        return frag;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = (int)convertDpToPixel(600);
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.inventory_picker_dialog_fragment, null);
        ButterKnife.bind(this, view);
        txtSearch.addTextChangedListener(searchListener);

        adapter = new InventoryListAdapter(getContext(), true);
        lvList.setAdapter(adapter);

        repopulateList();

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedIndex(position);
                data = convertItem(adapter.getItem(position));
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double qty = 0d;
                try {
                    qty = Double.parseDouble(txtQty.getText().toString());
                } catch (Exception e) { }

                if (data == null) {
                    txtError.setText("Please select item");
                } else if (qty == 0) {
                    txtError.setText("Invalid quantity");
                } else if (data != null && qty > 0) {
                    if (qty > adapter.getSelectedItem().getQuantity()) {
                        txtError.setText("Quantity not enough");
                    } else {
                        data.setQuantity(qty);
                        data.setItemId(adapter.getSelectedItem().getId());
                        callback.onItemSelected(data);
                        dismiss();
                    }
                }
            }
        });

        if (data != null) {
            txtQty.setText(String.valueOf(data.getQuantity().intValue()));
        }

        return dialog;
    }

    private TransactionItem convertItem(Inventory item) {
        return new TransactionItem(null, null, null,
                data != null ? data.getItemId() : null, item.getName(), item.getHargaBeli(),
                item.getHargaJual(), 0d, true);
    }

    public void repopulateList() {
        controller.fetchAvailableInventory(txtSearch.getText().toString());
    }

    @Subscribe
    public void dataFetched(InventoryListFetchComplete event) {
        if (!event.getSuccess()) {
            txtError.setText(event.getMessage());
        } else {
            adapter.clear();
            adapter.addAll(event.getData());

            if (data != null) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.getItem(i).getName().equalsIgnoreCase(data.getName()) &&
                            adapter.getItem(i).getHargaJual().equals(data.getHargaJual()) &&
                            adapter.getItem(i).getHargaBeli().equals(data.getHargaBeli())) {
                        adapter.setSelectedIndex(i);
                    }
                }
            }

            if (adapter.getCount() > 0) {
                lblNoData.setVisibility(View.GONE);
                lvList.setVisibility(View.VISIBLE);
            } else {
                lblNoData.setVisibility(View.VISIBLE);
                lvList.setVisibility(View.GONE);
            }
        }
    }
}