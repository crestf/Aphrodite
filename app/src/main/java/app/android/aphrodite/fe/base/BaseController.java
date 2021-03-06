package app.android.aphrodite.fe.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import app.android.aphrodite.be.data.AppDatabase;
import app.android.aphrodite.be.enums.TransactionTypeEnum;

public class BaseController {

    protected Context context;
    private AppDatabase db;

    public BaseController(Context context) {
        this.context = context;
    }

    public AppDatabase getDB() {
        if (db == null)
            db = AppDatabase.getDatabase(context);
        return db;
    }

    public interface AsyncProcess {
        BaseEvent doInBackground();
        void onComplete(BaseEvent event);
    }

    private AsyncTask task;

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("unchecked")
    public void execute(final AsyncProcess newTask) {
        if (task != null) {
            task.cancel(true);
            task = null;
        }

        task = new AsyncTask<Object, Void, BaseEvent>() {
            @Override
            protected BaseEvent doInBackground(Object[] objects) {
                return newTask.doInBackground();
            }

            @Override
            protected void onPostExecute(BaseEvent event) {
                super.onPostExecute(event);
                newTask.onComplete(event);
            }
        };
        task.execute();
    }

    public Double findAvailableStock(String name, Double hargaJual, Double hargaBeli) {
        return getDB().inventoryDao().findByProps(name, hargaJual, hargaBeli).getQuantity();
    }
}
