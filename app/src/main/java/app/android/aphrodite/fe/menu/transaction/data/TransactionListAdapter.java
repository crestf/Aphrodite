package app.android.aphrodite.fe.menu.transaction.data;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.be.enums.TransactionStatusEnum;
import app.android.aphrodite.be.enums.TransactionTypeEnum;
import app.android.aphrodite.be.model.Transaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionListAdapter extends ArrayAdapter<Transaction> {

    public TransactionListAdapter(Context context) {
        super(context, R.layout.row_inventory);
    }

    public class ViewHolder {
        @BindView(R.id.lblDate) TextView lblDate;
        @BindView(R.id.lblPaymentType) TextView lblPaymentType;
        @BindView(R.id.lblCustomer) TextView lblCustomer;
        @BindView(R.id.lblGrandTotal) TextView lblGrandTotal;
        @BindView(R.id.lblStatus) TextView lblStatus;
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
            convertView = inflater.inflate(R.layout.row_transaction, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.lblDate.setText(HelperUtil.formatDBToDisplay(getItem(position).getTransactionDate()));
        viewHolder.lblPaymentType.setText(getItem(position).getPaymentType());
        viewHolder.lblCustomer.setText(getItem(position).getCustomerName());
        viewHolder.lblGrandTotal.setText(HelperUtil.formatNumber(getItem(position).getGrandTotal()));
        viewHolder.lblStatus.setText(getItem(position).getStatus());

        if (!getItem(position).getActive()) {
            convertView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.dark_gray));
        } else if (getItem(position).getType().equalsIgnoreCase(TransactionTypeEnum.PRE_ORDER)) {
            convertView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.white));
        } else {
            convertView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.yellow));
        }

        if (getItem(position).getStatus().equalsIgnoreCase(TransactionStatusEnum.NONE)) {
            viewHolder.imgRight.setImageResource(R.drawable.ic_none_black_24dp);
        } else if (getItem(position).getStatus().equalsIgnoreCase(TransactionStatusEnum.WAITING)) {
            viewHolder.imgRight.setImageResource(R.drawable.ic_waiting_black_24dp);
        } else if (getItem(position).getStatus().equalsIgnoreCase(TransactionStatusEnum.READY)) {
            viewHolder.imgRight.setImageResource(R.drawable.ic_ready_black_24dp);
        } else {
            viewHolder.imgRight.setImageResource(R.drawable.ic_finish_black_24dp);
        }

        return convertView;
    }
}
