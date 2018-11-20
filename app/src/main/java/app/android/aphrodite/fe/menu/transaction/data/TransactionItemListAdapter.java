package app.android.aphrodite.fe.menu.transaction.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.be.model.TransactionItem;
import app.android.aphrodite.fe.menu.transaction.event.TransactionItemClickEvent;
import app.android.aphrodite.fe.menu.transaction.event.TransactionItemDeleteEvent;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionItemListAdapter extends ArrayAdapter<TransactionItem> {

    public TransactionItemListAdapter(Context context) {
        super(context, R.layout.row_inventory);
    }

    public class ViewHolder {
        @BindView(R.id.lblName)         TextView lblName;
        @BindView(R.id.lblQuantity)     TextView lblQuantity;
        @BindView(R.id.lblSubtotal)     TextView lblSubtotal;
        @BindView(R.id.lblHargaJual)    TextView lblHargaJual;
        @BindView(R.id.btnRemove)       ImageButton btnRemove;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_transaction_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.lblName.setText(getItem(position).getName());
        viewHolder.lblQuantity.setText(HelperUtil.formatNumber(getItem(position).getQuantity()));
        viewHolder.lblSubtotal.setText(HelperUtil.formatNumber(getItem(position).getHargaJual() * getItem(position).getQuantity()));
        viewHolder.lblHargaJual.setText("Each @" + HelperUtil.formatNumber(getItem(position).getHargaJual()));
        viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new TransactionItemDeleteEvent(position));
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new TransactionItemClickEvent(position));
            }
        });

        return convertView;
    }
}
