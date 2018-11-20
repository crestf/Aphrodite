package app.android.aphrodite.fe.menu.transaction.data;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import app.android.aphrodite.fe.menu.transaction.AddEditTransactionDetailFragment;
import app.android.aphrodite.fe.menu.transaction.AddEditTransactionHeaderFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private AddEditTransactionHeaderFragment header = new AddEditTransactionHeaderFragment();
    private AddEditTransactionDetailFragment detail = new AddEditTransactionDetailFragment();

    public AddEditTransactionHeaderFragment getHeader() {
        return header;
    }

    public AddEditTransactionDetailFragment getDetail() {
        return detail;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return header;
        } else {
            return detail;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "Header";
        } else if (position == 1) {
            title = "Detail";
        }
        return title;
    }
}
