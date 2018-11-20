package app.android.aphrodite.fe.menu.recapitulation.data;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import app.android.aphrodite.be.enums.PaymentTypeEnum;
import app.android.aphrodite.be.model.Recap;
import app.android.aphrodite.be.model.Transaction;
import app.android.aphrodite.fe.base.BaseController;
import app.android.aphrodite.fe.base.BaseEvent;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.fe.menu.transaction.event.RecapitulationListFetchComplete;

public class RecapitulationController extends BaseController {

    public RecapitulationController(Context context) {
        super(context);
    }

    public void fetchRecapitulationList() {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                try {
                    String startDate = HelperUtil.formatDisplayToDB(SharedPrefManager.getInstance(context).getStartDate());
                    String endDate = HelperUtil.formatDisplayToDB(SharedPrefManager.getInstance(context).getEndDate());

                    List<Recap> result = new ArrayList<>();
                    result.addAll(getDB().transactionDao().getCustomerList());

                    for (Recap item : result) {
                        item.setQty(getDB().transactionDao().getTotalQty(item.name, startDate, endDate));
                        Recap cash = getDB().transactionDao().getRecap(item.name, startDate, endDate, PaymentTypeEnum.CASH);
                        if (cash != null) {
                            item.setModalCash(cash.modal);
                            item.setTotalCash(cash.total - getDB().transactionDao().getTotalDiscount(item.name,
                                    startDate, endDate, PaymentTypeEnum.CASH));
                        }
                        Recap bca = getDB().transactionDao().getRecap(item.name, startDate, endDate, PaymentTypeEnum.BCA);
                        if (bca != null) {
                            item.setModalBCA(bca.modal);
                            item.setTotalBCA(bca.total - getDB().transactionDao().getTotalDiscount(item.name,
                                    startDate, endDate, PaymentTypeEnum.BCA));
                        }
                        Recap bri = getDB().transactionDao().getRecap(item.name, startDate, endDate, PaymentTypeEnum.BRI);
                        if (bri != null) {
                            item.setModalBRI(bri.modal);
                            item.setTotalBRI(bri.total - getDB().transactionDao().getTotalDiscount(item.name,
                                    startDate, endDate, PaymentTypeEnum.BRI));
                        }
                    }

                    return new RecapitulationListFetchComplete(result);
                } catch (Exception e) {
                    return new RecapitulationListFetchComplete(e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }
}
