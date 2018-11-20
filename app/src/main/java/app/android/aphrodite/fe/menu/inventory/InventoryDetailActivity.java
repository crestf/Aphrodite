package app.android.aphrodite.fe.menu.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import app.android.aphrodite.R;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.fe.menu.inventory.event.InventoryDetailListFetchComplete;
import app.android.aphrodite.fe.base.BaseActivity;
import app.android.aphrodite.fe.menu.inventory.data.InventoryController;
import app.android.aphrodite.fe.menu.inventory.data.InventoryDetailListAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

@SuppressWarnings("ALL")
public class InventoryDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.nav_view)        NavigationView navigationView;
    @BindView(R.id.drawer_layout)   DrawerLayout drawer;
    @BindView(R.id.fab)             FloatingActionButton fab;
    @BindView(R.id.lblItemName)     TextView lblItemName;
    @BindView(R.id.lblHargaJual)    TextView lblHargaJual;
    @BindView(R.id.lblNoData)       TextView lblNoData;
    @BindView(R.id.lvList)          ListView lvList;
    @BindView(R.id.loading)         RelativeLayout loading;

    InventoryController controller = new InventoryController(this);
    InventoryDetailListAdapter adapter;

    private Integer _itemId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_detail_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        _itemId = getIntent().getIntExtra("itemId", -1);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InventoryDetailActivity.this, AddEditInventoryActivity.class);
                Bundle b = new Bundle();
                b.putInt("itemId", _itemId);
                i.putExtras(b);
                startActivity(i);
            }
        });

        getSupportActionBar().setTitle("Inventory Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new InventoryDetailListAdapter(this);
        lvList.setAdapter(adapter);

        refreshContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContent();
    }

    @OnItemClick(R.id.lvList)
    public void lvListOnItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!adapter.getItem(position).getTransaction()) {
            Intent i = new Intent(this, AddEditInventoryActivity.class);
            Bundle b = new Bundle();
            b.putInt("id", adapter.getItem(position).getId());
            b.putInt("itemId", _itemId);
            i.putExtras(b);
            startActivity(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.realOnBackPressed();
                SharedPrefManager.getInstance(this).setIsUnlocked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.realOnBackPressed();
        SharedPrefManager.getInstance(this).setIsUnlocked(true);
    }


    public void refreshContent() {
        setLoading(true);
        controller.fetchInventoryDetailList(getIntent().getIntExtra("itemId", -1));
    }

    @Subscribe
    public void refreshContentComplete(InventoryDetailListFetchComplete event) {
        setLoading(false);
        if (!event.getSuccess()) {
            showError(event.getMessage());
        } else {
            lblHargaJual.setText(HelperUtil.formatNumber(event.getItem().getSellPrice()));
            lblItemName.setText(event.getItem().getName());

            adapter.clear();
            adapter.addAll(event.getData());

            if (adapter.getCount() > 0) {
                lblNoData.setVisibility(View.GONE);
                lvList.setVisibility(View.VISIBLE);
            } else {
                lblNoData.setVisibility(View.VISIBLE);
                lvList.setVisibility(View.GONE);
            }
        }
    }

    public void setLoading(Boolean isLoading) {
        if (isLoading) {
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.GONE);
        }
    }
}
