package fm.icelink.chat.websync4;

public class Contacts {
    private int id;
    private String contact;
    private int ringtone;

    public Contacts(int id, String contact, int ringtone) {
        this.id = id;
        this.contact = contact;
        this.ringtone = ringtone;
    }

    public Contacts() {}


    public int getId() {
        return id;
    }

    public String getContact() {
        return contact;
    }

    public int getRingtone() {
        return ringtone;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return contact;
    }
}
