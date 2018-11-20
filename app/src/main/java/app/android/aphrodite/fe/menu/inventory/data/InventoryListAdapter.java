package app.android.aphrodite.fe.menu.inventory.data;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.be.model.Inventory;
import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryListAdapter extends ArrayAdapter<Inventory> {

    public Boolean selectable = false;

    public Integer getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(Integer selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    public Integer selectedIndex = -1;

    public InventoryListAdapter(Context context, Boolean selectable) {
        super(context, R.layout.row_inventory);
        this.selectable = selectable;
    }

    public class ViewHolder {
        @BindView(R.id.lblItemName) TextView lblItemName;
        @BindView(R.id.lblHargaJual) TextView lblHargaJual;
        @BindView(R.id.lblQuantity) TextView lblQuantity;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_inventory, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.lblItemName.setText(getItem(position).getName());
        viewHolder.lblHargaJual.setText(HelperUtil.formatNumber(getItem(position).getSellPrice()));
        viewHolder.lblQuantity.setText(HelperUtil.formatNumber(getItem(position).getQuantity()));

        if (selectable && position == selectedIndex){
            convertView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.primary));
        } else if (getItem(position).getActive()) {
            convertView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.white));
        } else {
            convertView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.dark_gray));
        }

        return convertView;
    }
}
