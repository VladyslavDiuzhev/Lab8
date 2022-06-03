package core.interact;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class Message implements Serializable {
    private final String text;
    private final ZonedDateTime creationDate;
    private final boolean result;
    private String type;
    private Object object;

    public Message(String text, boolean result) {
        this.text = text;
        this.creationDate = ZonedDateTime.now();
        this.result = result;
    }

    public Message(String text,String type, boolean result) {
        this.type = type;
        this.text = text;
        this.creationDate = ZonedDateTime.now();
        this.result = result;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public boolean isSuccessful() {
        return this.result;
    }

    public String getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
