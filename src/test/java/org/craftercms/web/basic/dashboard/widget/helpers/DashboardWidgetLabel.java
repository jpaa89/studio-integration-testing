package org.craftercms.web.basic.dashboard.widget.helpers;

/**
 * @author Juan Avila
 * Meant to hold the label info of a content/item
 */
public class DashboardWidgetLabel {

    public static final String KIND_PAGE = "Page";
    public static final String KIND_COMPONENT = "Component";
    public static final String KIND_DOCUMENT = "Document";

    public static final String STATUS_IN_PROGRESS = "In Progress";
    public static final String STATUS_SUBMITTED = "Submitted";
    public static final String STATUS_DELETED = "Deleted";
    public static final String STATUS_PROCESSING = "Processing";

    private String kind;
    private String title;
    private String status;
    private String lastEdited;
    private String editedBy;
    private String lockedBy;
    private String scheduled;

    public DashboardWidgetLabel(String kind, String title, String status, String lastEdited, String editedBy, String lockedBy, String scheduled) {
        this.kind = kind;
        this.title = title;
        this.status = status;
        this.lastEdited = lastEdited;
        this.editedBy = editedBy;
        this.lockedBy = lockedBy;
        this.scheduled = scheduled;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(String lastEdited) {
        this.lastEdited = lastEdited;
    }

    public String getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(String editedBy) {
        this.editedBy = editedBy;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public String getScheduled() {
        return scheduled;
    }

    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }
}
