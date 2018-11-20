package app.android.aphrodite.fe.menu.recapitulation.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import app.android.aphrodite.R;
import app.android.aphrodite.be.model.Recap;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.be.enums.PaymentTypeEnum;
import app.android.aphrodite.be.model.Transaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecapitulationListAdapter extends ArrayAdapter<Recap> {

    public RecapitulationListAdapter(Context context) {
        super(context, R.layout.row_recapitulation);
    }

    public class ViewHolder {
        @BindView(R.id.lblItemName) TextView lblItemName;
//        @BindView(R.id.lblHargaBeliEach) TextView lblHargaBeliEach;
//        @BindView(R.id.lblHargaJualEach) TextView lblHargaJualEach;
        @BindView(R.id.lblQuantity) TextView lblQuantity;
        @BindView(R.id.lblBCA) TextView lblBCA;
        @BindView(R.id.lblBRI) TextView lblBRI;
        @BindView(R.id.lblCash) TextView lblCash;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_recapitulation, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.lblItemName.setText(getItem(position).getName());
        viewHolder.lblQuantity.setText(HelperUtil.formatNumber(getItem(position).getQty()));
        viewHolder.lblBCA.setText(HelperUtil.formatNumber(getItem(position).getTotalBCA()));
        viewHolder.lblBRI.setText(HelperUtil.formatNumber(getItem(position).getTotalBRI()));
        viewHolder.lblCash.setText(HelperUtil.formatNumber(getItem(position).getTotalCash()));

        return convertView;
    }
}
