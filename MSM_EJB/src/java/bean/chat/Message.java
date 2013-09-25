/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.chat;

/**
 *
 * @author nemo
 */
public class Message
{
    private static Integer currentId=0;
    private Integer id;
    private String message;
    private Boolean sent;

    public Message(String message, Boolean sent)
    {
        this.message = message;
        this.sent = sent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }
}
