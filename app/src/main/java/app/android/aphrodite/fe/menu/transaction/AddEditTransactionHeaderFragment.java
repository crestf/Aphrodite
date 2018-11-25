package app.android.aphrodite.fe.menu.transaction;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.android.aphrodite.R;
import app.android.aphrodite.be.model.TransactionItem;
import app.android.aphrodite.fe.base.BaseFragment;
import app.android.aphrodite.fe.common.GlobalState;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.be.enums.PaymentTypeEnum;
import app.android.aphrodite.be.enums.TransactionStatusEnum;
import app.android.aphrodite.be.enums.TransactionTypeEnum;
import app.android.aphrodite.fe.event.RecalculateTransactionHeaderEvent;
import app.android.aphrodite.be.model.Transaction;
import app.android.aphrodite.fe.menu.inventory.AddEditInventoryActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddEditTransactionHeaderFragment extends BaseFragment {

    @BindView(R.id.txtTransactionType)  EditText txtTransactionType;
    @BindView(R.id.txtCustomer)  EditText txtCustomer;
    @BindView(R.id.txtTransactionDate)  EditText txtTransactionDate;
    @BindView(R.id.txtPaymentType)  EditText txtPaymentType;
    @BindView(R.id.txtStatus)  EditText txtStatus;
    @BindView(R.id.txtDiscount)  EditText txtDiscount;
    @BindView(R.id.txtNote)  EditText txtNote;
    @BindView(R.id.txtSubtotal)  EditText txtSubtotal;
    @BindView(R.id.txtGrandTotal)  EditText txtGrandTotal;
    @BindView(R.id.cbActive) Switch cbActive;

    @BindView(R.id.tilTransactionType)     TextInputLayout tilTransactionType;
    @BindView(R.id.tilCustomer)     TextInputLayout tilCustomer;
    @BindView(R.id.tilTransactionDate)     TextInputLayout tilTransactionDate;
    @BindView(R.id.tilPaymentType)     TextInputLayout tilPaymentType;
    @BindView(R.id.tilStatus)     TextInputLayout tilStatus;
    @BindView(R.id.tilDiscount)     TextInputLayout tilDiscount;
    @BindView(R.id.tilNotes)     TextInputLayout tilNotes;
    @BindView(R.id.tilSubtotal)     TextInputLayout tilSubtotal;
    @BindView(R.id.tilGrandTotal)     TextInputLayout tilGrandTotal;


    Double subtotal = 0d;

    ArrayList<String> transactionTypeList = new ArrayList<>();
    ArrayList<String> statusList = new ArrayList<>();
    ArrayList<String> paymentTypeList = new ArrayList<>();

    Boolean _isedit = false;
    private String transactionType;

    public AddEditTransactionHeaderFragment() { }

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

    public AddEditTransactionActivity getParent() {
        return (AddEditTransactionActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.addedit_transaction_content_header, container, false);
        ButterKnife.bind(this, view);

        prepareList();

        Date d = new Date();
        String date = HelperUtil.formatDateToDisplay(d);
        txtTransactionDate.setText(date);
        txtTransactionDate.setTag(d);

        HelperUtil.setupEditText(txtDiscount);

        txtDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateGrandTotal();
            }
        });

        prepareView();

        return view;
    }

    public void prepareList() {
        transactionTypeList.addAll(TransactionTypeEnum.list);
        txtTransactionType.setText(transactionTypeList.get(0));
        transactionType = transactionTypeList.get(0);

        statusList.addAll(TransactionStatusEnum.list);
        txtStatus.setText(statusList.get(0));

        paymentTypeList.addAll(PaymentTypeEnum.list);
        txtPaymentType.setText(paymentTypeList.get(0));
    }

    private void prepareView() {
        Transaction header = GlobalState.getInstance().getTransactionData();
        if (header != null) {
            _isedit = true;
            txtTransactionType.setText(header.getType());
            txtCustomer.setText(header.getCustomerName());
            txtTransactionDate.setText(HelperUtil.formatDBToDisplay(header.getTransactionDate()));
            txtTransactionDate.setTag(HelperUtil.formatDBToDate(header.getTransactionDate()));
            txtPaymentType.setText(header.getPaymentType());
            txtStatus.setText(header.getStatus());
            txtNote.setText(header.getNote());
            txtDiscount.setText(HelperUtil.formatCurrency(header.getDiscount()));
            cbActive.setChecked(header.getActive());
        }

        cbActive.setVisibility(_isedit ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.txtTransactionDate)
    public void txtTransactionDateOnClick() {
        Calendar c = Calendar.getInstance();
        c.setTime((Date)txtTransactionDate.getTag());
        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                Date d = c.getTime();
                String date = HelperUtil.formatDateToDisplay(d);
                txtTransactionDate.setText(date);
                txtTransactionDate.setTag(d);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecalculateTransactionHeaderEvent(RecalculateTransactionHeaderEvent event) {
        subtotal = event.getTotal();
        txtSubtotal.setText(HelperUtil.formatNumber(subtotal));
        calculateGrandTotal();
    }

    private void calculateGrandTotal() {
        Double discount = 0d;
        try {
            discount = HelperUtil.extractCurrency(txtDiscount);
        } catch (Exception e) {}
        txtGrandTotal.setText(HelperUtil.formatNumber(subtotal - discount));
    }

    @OnClick(R.id.txtTransactionType)
    public void txtTransactionTypeOnClick(View v) {
        showPopup(transactionTypeList, (EditText) v, new Callback() {
            @Override
            public void callback(final String newTransactionType) {
                final String prevType = AddEditTransactionHeaderFragment.this.transactionType;
                if (prevType == null || !prevType.equalsIgnoreCase(newTransactionType)) {
                    if (getParent().isCurrentlyHasDetail()) {
                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        AddEditTransactionHeaderFragment.this.transactionType = newTransactionType;
                                        getParent().clearDetail();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        AddEditTransactionHeaderFragment.this.transactionType = prevType;
                                        txtTransactionType.setText(prevType);
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Changing Transaction Type will clear all inputted items")
                                .setTitle("Change Transaction Type?")
                                .setPositiveButton("Change", listener)
                                .setNegativeButton("Cancel", listener)
                                .show();
                    } else {
                        AddEditTransactionHeaderFragment.this.transactionType = newTransactionType;
                    }
                }
            }
        });
    }
    @OnClick(R.id.txtStatus)
    public void txtStatusOnClick(View v) {
        showPopup(statusList, (EditText)v, null);
    }
    @OnClick(R.id.txtPaymentType)
    public void txtPaymentTypeOnClick(View v) {
        showPopup(paymentTypeList, (EditText)v, null);
    }

    public String getTransactionType() {
        return transactionType;
    }


    private interface Callback {
        void callback(String value);
    }

    private void showPopup(final ArrayList<String> list, final EditText bindView, final Callback callback) {
        // initialize a pop up window type
        final PopupWindow popupWindow = new PopupWindow(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,
                list);
        // the drop down list is a list view
        ListView listViewSort = new ListView(getContext());

        // set our adapter and pass our pop up window contents
        listViewSort.setAdapter(adapter);

        // set on item selected
        listViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bindView.setText(list.get(position));
                if (callback != null) {
                    callback.callback(list.get(position));
                }
                popupWindow.dismiss();
            }
        });

        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(400);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.dark_gray));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the listview as popup content
        popupWindow.setContentView(listViewSort);
        popupWindow.showAsDropDown(bindView, 0, 0);
    }

    public Boolean validate() {
        Boolean result = true;
        if (txtCustomer.getText().toString().isEmpty()) {
            showError("Item Name must be filled");
            tilCustomer.setError("Item Name must be filled");
            result = false;
        }
        if (Double.parseDouble(txtGrandTotal.getText().toString().replace(",", "")) < 0) {
            showError("Grand total cannot be less than 0");
            tilGrandTotal.setError("Grand total cannot be less than 0");
            result = false;
        }
        return result;
    }

    public Transaction getHeaderData() {
        Transaction header = GlobalState.getInstance().getTransactionData();
        if (header == null) {
            header = new Transaction();
        }

        header.setType(txtTransactionType.getText().toString());
        header.setCustomerName(txtCustomer.getText().toString());
        header.setTransactionDate(HelperUtil.formatDateToDB((Date)txtTransactionDate.getTag()));
        header.setPaymentType(txtPaymentType.getText().toString());
        header.setStatus(txtStatus.getText().toString());
        header.setDiscount(HelperUtil.extractCurrency(txtDiscount));
        header.setNote(txtNote.getText().toString());
        header.setSubTotal(Double.parseDouble(txtSubtotal.getText().toString().replace(",", "")));
        header.setGrandTotal(Double.parseDouble(txtGrandTotal.getText().toString().replace(",", "")));
        header.setActive(_isedit ? cbActive.isChecked() : true);

        return header;
    }
}
