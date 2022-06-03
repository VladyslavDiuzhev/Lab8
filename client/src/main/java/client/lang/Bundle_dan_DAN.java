package client.lang;

import java.util.ListResourceBundle;

public class Bundle_dan_DAN extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                // Login View
                {"auth", "Bemyndigelse"},
                {"reg", "Registrering"},
                {"register", "Tilmeld"},
                {"enter", "At komme ind"},
                {"question_enter", "Har du ikke en konto?"},
                {"lang", "Sprog"},
                {"login", "Log på"},
                {"password", "Adgangskode"},
                {"question_reg", "har du allerede en bruger?"},
                {"inv_format", "Ugyldigt inputformat!"},
                {"gen_err", "Der opstod en fejl!"},
                // Main View
                {"unable_update", "Ændringen kunne ikke vises!"},
                {"req_err", "Fejl ved behandling af anmodning!"},
                {"serv_conn_err", "Serverforbindelsesfejl!"},
                {"filter", "Filter"},
                {"map", "Kort"},
                {"table", "Bord"},
                {"add_upd", "Tilføj/Opdater"},
                {"user", "Bruger"},
                {"delete", "Slet"},
                {"update", "Lave om"},

                {"creat_date", "dato for oprettelse"},
                {"naming", "Navn"},
                {"type", "Udsigt"},
                {"fuel", "Brændstof"},
                {"power", "Strøm"},
                {"x_cord", "X-koordinat"},
                {"y_cord", "У-koordinat"},
                {"owner", "Ejer"},
                {"create", "skab"},
                {"min_cond", "Minimumstilstand"},
                {"creat_title", "Opret et objekt"},
                {"update_title", "Objektopdatering"},

                // Server messages
                {"command_fail", "Der opstod en fejl under behandling af kommandoen."},
                {"already_auth", "Brugeren er allerede logget ind!"},
                {"already_auth_name", "En bruger med dette navn er allerede logget ind!"},
                {"need_auth", "Kun autoriserede brugere kan udføre kommandoer!"},
                {"success", "Kommando fuldført med succes!"},
                {"not_min", "Elementet er ikke minimalt."},
                {"auth_failed", "Godkendelse mislykkedes!"},
                {"reg_failed", "Registreringen fejlede!"},
                {"already_exists", "Der findes allerede en bruger med dette login."},
                {"not_your", "Du kan kun slette/ændre objekter, du har oprettet."}
        };
    }
}
