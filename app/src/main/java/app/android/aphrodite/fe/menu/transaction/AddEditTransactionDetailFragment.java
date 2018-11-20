package app.android.aphrodite.fe.menu.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import app.android.aphrodite.R;
import app.android.aphrodite.be.enums.TransactionTypeEnum;
import app.android.aphrodite.fe.common.GlobalState;
import app.android.aphrodite.fe.event.RecalculateTransactionHeaderEvent;
import app.android.aphrodite.fe.menu.transaction.event.TransactionItemChangeEvent;
import app.android.aphrodite.fe.menu.transaction.data.TransactionItemListAdapter;
import app.android.aphrodite.be.model.Inventory;
import app.android.aphrodite.be.model.TransactionItem;
import app.android.aphrodite.fe.menu.transaction.event.TransactionItemClickEvent;
import app.android.aphrodite.fe.menu.transaction.event.TransactionItemDeleteEvent;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditTransactionDetailFragment extends Fragment {

    @BindView(R.id.lblNoData)       TextView lblNoData;
    @BindView(R.id.lvList)          ListView lvList;

    TransactionItemListAdapter adapter;

    public AddEditTransactionDetailFragment() { }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.addedit_transaction_content_detail, container, false);
        ButterKnife.bind(this, view);

        adapter = new TransactionItemListAdapter(getContext());
        lvList.setAdapter(adapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                openEditItem(position);
            }
        });


        if (GlobalState.getInstance().getTransactionData() != null) {
            setDetailData(GlobalState.getInstance().getTransactionData().getItems());
        }

        return view;
    }

    public AddEditTransactionActivity getParent() {
        return (AddEditTransactionActivity) getActivity();
    }

    @Subscribe
    public void onTransactionItemChangeEvent(TransactionItemChangeEvent event) {
        if (event.getOriginalIndex() < 0) {
            addItem(event.getData());
            adjustList();
        } else {
            adapter.getItem(event.getOriginalIndex()).setQuantity(event.getData().getQuantity());
            adapter.getItem(event.getOriginalIndex()).setHargaBeli(event.getData().getHargaBeli());
            adapter.getItem(event.getOriginalIndex()).setHargaJual(event.getData().getHargaJual());
            adapter.getItem(event.getOriginalIndex()).setName(event.getData().getName());
            adapter.notifyDataSetChanged();
            adjustList();
        }
    }

    public void addItem(TransactionItem data) {
        Boolean found = false;
        for (int i=0; i<adapter.getCount(); i++) {
            TransactionItem item = adapter.getItem(i);
            if (item != null &&
                    item.getName().equalsIgnoreCase(data.getName()) &&
                    item.getHargaJual().equals(data.getHargaJual()) &&
                    item.getHargaBeli().equals(data.getHargaBeli())) {
                found = true;
                adapter.getItem(i).setQuantity(item.getQuantity() + data.getQuantity());
                adapter.notifyDataSetChanged();
            }
        }
        if (!found) {
            adapter.add(data);
        }
        adjustList();
    }

    @Subscribe
    public void onItemDeleted(TransactionItemDeleteEvent event) {
        adapter.remove(adapter.getItem(event.getIndex()));
        adapter.notifyDataSetChanged();
        adjustList();
    }

    public void openEditItem(final int position) {
        if (GlobalState.getInstance().getTransactionData().getType().equalsIgnoreCase(TransactionTypeEnum.READY_STOCK)) {
            DialogFragment newFragment = InventoryPickerDialogFragment.newInstance(new InventoryPickerDialogFragment.Callback() {
                @Override
                public void onItemSelected(Inventory item, Double qty) {
                    TransactionItem data = new TransactionItem(null, item.getName(), item.getCapitalPrice(), item.getSellPrice(), qty);
                    EventBus.getDefault().post(new TransactionItemChangeEvent(position, data));
                }
            }, adapter.getItem(position));
            newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
        } else {
            TransactionItem item = adapter.getItem(position);

            Intent i = new Intent(getContext(), TransactionItemActivity.class);
            Bundle b = new Bundle();
            b.putInt("index", position);
            b.putString("name", item.getName());
            b.putDouble("capitalPrice", item.getHargaBeli());
            b.putDouble("sellPrice", item.getHargaJual());
            b.putDouble("qty", item.getQuantity());
            i.putExtras(b);
            startActivity(i);
        }
    }

    @Subscribe
    public void onItemClick(TransactionItemClickEvent event) {
        openEditItem(event.getIndex());
    }

    public void adjustList() {
        if (adapter.getCount() > 0) {
            lblNoData.setVisibility(View.GONE);
            lvList.setVisibility(View.VISIBLE);
        } else {
            lblNoData.setVisibility(View.VISIBLE);
            lvList.setVisibility(View.GONE);
        }

        Double total = 0d;
        for (int i = 0; i < adapter.getCount(); i++) {
            total += adapter.getItem(i).getQuantity() * adapter.getItem(i).getHargaJual();
        }
        EventBus.getDefault().post(new RecalculateTransactionHeaderEvent(total));
    }

    public Boolean validate() {
        for (int i=0; i<adapter.getCount(); i++) {
            if (adapter.getItem(i).getQuantity() > 0) {
                return true;
            }
        }
        return false;
    }

    public List<TransactionItem> getDetailData() {
        if (adapter == null)
            return new ArrayList<>();

        List<TransactionItem> result = new ArrayList<>();
        for (int i=0; i<adapter.getCount(); i++) {
            result.add(adapter.getItem(i));
        }
        return result;
    }

    public void setDetailData(List<TransactionItem> detail) {
        adapter.clear();
        adapter.addAll(detail);
        adjustList();
    }
}
