package notification;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Notification
{
    private String addressed;
    private String message;
    private String subject;
    private String date;
    private Boolean isRead;


    public Notification(String addressed, String message, String subject)
    {
        this.addressed=addressed;
        this.message = message;
        this.subject = subject;
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-hh:mm:ss:sss");
        Date date1 = new Date();
        date = dateFormat.format(date1);
        isRead=false;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getRead()
    {
        return isRead;
    }
}