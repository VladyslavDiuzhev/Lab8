package core.essentials;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private final String login;
    private final String pass;
    private int id;

    public UserInfo(String login, String pass, int id) {
        this.login = login;
        this.pass = pass;
        this.id = id;
    }

    public UserInfo(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return pass;
    }

    public int getId() {
        return id;
    }
}
