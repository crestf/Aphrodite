package app.android.aphrodite.fe.menu.settings.event;

import app.android.aphrodite.fe.base.BaseEvent;

public class ImportExportEvent extends BaseEvent {

    private final Boolean success;
    private final String message;

    public ImportExportEvent(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

}
