package app.android.aphrodite.fe.menu.inventory.data;

import android.content.Context;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.android.aphrodite.R;
import app.android.aphrodite.be.model.TransactionItem;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.be.model.Inventory;
import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryDetailListAdapter extends ArrayAdapter<TransactionItem> {

    public InventoryDetailListAdapter(Context context) {
        super(context, R.layout.row_inventory);
    }

    public class ViewHolder {
        @BindView(R.id.lblDate)         TextView lblDate;
        @BindView(R.id.lblQuantity)     TextView lblQuantity;
        @BindView(R.id.imgRight)        ImageView imgRight;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_inventory_detail, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.lblDate.setText(HelperUtil.formatDBToDisplay(getItem(position).getTransactionDate()));
        viewHolder.lblQuantity.setText(HelperUtil.formatNumber(getItem(position).getQuantity()));
        if (!getItem(position).getActive()) {
            convertView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.dark_gray));
        } else if (getItem(position).getHeaderId() != null) {
            convertView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.light_green));
        } else {
            convertView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.white));
        }

        if (getItem(position).getHeaderId() == null) {
            viewHolder.imgRight.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imgRight.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
