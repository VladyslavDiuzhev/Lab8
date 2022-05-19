package client.argcreation;

import core.essentials.UserInfo;
import core.interact.ConsoleInteractor;
import core.interact.UserInteractor;

public abstract class Authorizer {

    public static UserInfo getUserInfo(UserInteractor interactor) {
        String login = "";
        String password = "";
        while (login.isEmpty()) {
            interactor.broadcastMessage("Введите логин: ", false);
            login = interactor.getData();

        }
        while (password.isEmpty()){
            interactor.broadcastMessage("Введите пароль: ", false);
            password = interactor.getSecureData();
        }
        return new UserInfo(login, password);
    }
}
