package client.lang;

import java.util.ListResourceBundle;

public class Bundle_de_DE extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                // Login View
                {"auth", "Genehmigung"},
                {"reg", "Anmeldung"},
                {"register", "Registrieren"},
                {"enter", "Betreten"},
                {"question_enter", "Sie haben kein Konto?"},
                {"lang", "Sprache"},
                {"login", "Login"},
                {"password", "Password"},
                {"question_reg", "Sie haben bereits ein Konto?"},
                {"inv_format", "Ungültiges Eingabeformat!"},
                {"gen_err", "Ein Fehler ist aufgetreten!"},
                // Main View
                {"unable_update", "Änderung konnte nicht angezeigt werden!"},
                {"req_err", "Fehler bei der Bearbeitung der Anfrage!"},
                {"serv_conn_err", "Serververbindungsfehler!"},
                {"filter", "F"},
                {"map", "Filter"},
                {"table", "Map"},
                {"add_upd", "Hinzufügen/Aktualisieren"},
                {"user", "Benutzer"},
                {"delete", "Löschen"},
                {"update", "Veränderung"},

                {"creat_date", "Erstelldatum"},
                {"naming", "Name"},
                {"type", "Aussicht"},
                {"fuel", "Fuel"},
                {"power", "Macht"},
                {"x_cord", "Х-cord"},
                {"y_cord", "У-cord"},
                {"owner", "Eigentümer"},
                {"create", "Schaffen"},
                {"min_cond", "Mindestbedingung"},
                {"creat_title", "Erstellen Sie ein Objekt"},
                {"update_title", "Objektaktualisierung"},

                // Server messages
                {"command_fail", "Bei der Verarbeitung des Befehls ist ein Fehler aufgetreten."},
                {"already_auth", "Der Benutzer ist bereits angemeldet!"},
                {"already_auth_name", "Ein Benutzer mit diesem Namen ist bereits angemeldet!"},
                {"need_auth", "Nur autorisierte Benutzer können Befehle ausführen!"},
                {"success", "Befehl erfolgreich abgeschlossen!"},
                {"not_min", "Das Element ist nicht minimal."},
                {"auth_failed", "Autorisation fehlgeschlagen!"},
                {"reg_failed",  "Registrierung fehlgeschlagen!"},
                {"already_exists", "Ein Benutzer mit diesem Login existiert bereits."},
                {"not_your", "Sie können nur von Ihnen erstellte Objekte löschen/ändern."}
        };
    }
}
