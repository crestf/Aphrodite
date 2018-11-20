package app.android.aphrodite.fe.menu.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import app.android.aphrodite.be.model.Backup;
import app.android.aphrodite.be.model.Inventory;
import app.android.aphrodite.be.model.Transaction;
import app.android.aphrodite.be.model.TransactionItem;
import app.android.aphrodite.fe.base.BaseController;
import app.android.aphrodite.fe.base.BaseEvent;
import app.android.aphrodite.fe.common.HelperUtil;
import app.android.aphrodite.fe.common.SharedPrefManager;
import app.android.aphrodite.fe.menu.inventory.event.InventoryDataFetchComplete;
import app.android.aphrodite.fe.menu.inventory.event.InventoryDetailListFetchComplete;
import app.android.aphrodite.fe.menu.inventory.event.InventoryListFetchComplete;
import app.android.aphrodite.fe.menu.inventory.event.InventorySaveComplete;
import app.android.aphrodite.fe.menu.settings.event.ImportExportEvent;

@SuppressLint("StaticFieldLeak")
public class SettingController extends BaseController {

    public SettingController(Context context) {
        super(context);
    }


    public void exportData() {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                try {
                    String fileName = "Export_" + HelperUtil.formatDateToFormat(new Date(), "ddMMyyyy") + ".txt";

                    Backup data = new Backup(getDB().inventoryDao().selectAll(),
                            getDB().transactionDao().selectAll(),
                            getDB().transactionItemDao().selectAll());

                    Gson gson = new Gson();
                    String json = gson.toJson(data);

                    writeToFile(fileName, json, context);
                    return new ImportExportEvent(true, "Data Exported to file " + fileName);
                } catch (Exception e) {
                    return new ImportExportEvent(false, e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }

    public void importData(final Uri uri) {
        execute(new AsyncProcess() {
            @Override
            public BaseEvent doInBackground() {
                try {
                    String json = readFromFile(uri, context);

                    Backup data = new Gson().fromJson(json, Backup.class);

                    getDB().beginTransaction();
                    try {
                        getDB().inventoryDao().deleteAll();
                        getDB().transactionDao().deleteAll();
                        getDB().transactionItemDao().deleteAll();

                        for (Inventory item : data.getListInventory()) {
                            getDB().inventoryDao().save(item);
                        }
                        for (Transaction item : data.getListTransaction()) {
                            getDB().transactionDao().save(item);
                        }
                        for (TransactionItem item : data.getListTransactionItem()) {
                            getDB().transactionItemDao().save(item);
                        }
                        getDB().setTransactionSuccessful();
                        getDB().endTransaction();
                    } catch (Exception e) {
                        getDB().endTransaction();
                        throw e;
                    }

                    return new ImportExportEvent(true, "Data from " + uri.getPath() + " successfully imported");
                } catch (Exception e) {
                    return new ImportExportEvent(false, e.getMessage());
                }
            }

            @Override
            public void onComplete(BaseEvent event) {
                EventBus.getDefault().post(event);
            }
        });
    }

    private void writeToFile(String fileName, String data, Context context) throws Exception {
        File root = android.os.Environment.getExternalStorageDirectory();
        File folder = new File(root.getAbsolutePath() + "/AppExportData");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file =  new File(folder, fileName);

        FileOutputStream f = new FileOutputStream(file);
        PrintWriter pw = new PrintWriter(f);
        pw.print(data);
        pw.flush();
        pw.close();
        f.close();
    }

    private String readFromFile(Uri uri, Context context) throws Exception {
        File file = new File(uri.getPath());

        FileInputStream fin = new FileInputStream(file);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
    public String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }


}
