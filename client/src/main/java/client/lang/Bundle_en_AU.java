package client.lang;

import java.util.ListResourceBundle;

public class Bundle_en_AU extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                // Login View
                {"auth", "Authorization"},
                {"reg", "Registration"},
                {"register", "register"},
                {"enter", "Enter"},
                {"question_enter", "Don't have account?"},
                {"lang", "Language"},
                {"login", "Login"},
                {"password", "Password"},
                {"question_reg", "Already have account?"},
                {"inv_format", "Invalid format!"},
                {"gen_err", "An error occured!"},
                // Main View
                {"unable_update", "Can't show the change!"},
                {"req_err", "Request failed!"},
                {"serv_conn_err", "Server connection error!"},
                {"filter", "Filter"},
                {"map", "Map"},
                {"table", "Table"},
                {"add_upd", "Add/Update"},
                {"user", "User"},
                {"delete", "Delete"},
                {"update", "Edit"},

                {"creat_date", "Creation date"},
                {"naming", "Naming"},
                {"type", "Type"},
                {"fuel", "Fuel"},
                {"power", "Power"},
                {"x_cord", "Х-cord"},
                {"y_cord", "У-cord"},
                {"owner", "Owner"},
                {"create", "Create"},
                {"min_cond", "Is minimal"},
                {"creat_title", "Creating an object"},
                {"update_title", "Editing an object"},

                // Server messages
                {"command_fail", "Command procession error!"},
                {"already_auth", "User is already authorized!"},
                {"already_auth_name", "User with this name is already authorized!"},
                {"need_auth", "Please authorize yourself!"},
                {"success", "Success!"},
                {"not_min", "Element is not minimal."},
                {"auth_failed", "Authorization failed!"},
                {"reg_failed", "Registration failed!"},
                {"already_exists", "User already exists."},
                {"not_your", "You can delete/edit only your objects."}
        };
    }
}
