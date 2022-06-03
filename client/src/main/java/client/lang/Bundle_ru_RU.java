package client.lang;

import java.util.ListResourceBundle;

public class Bundle_ru_RU extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                // Login View
                {"auth", "Авторизация"},
                {"reg", "Регистрация"},
                {"register", "Зарегистрироваться"},
                {"enter", "Войти"},
                {"question_enter", "Нет аккаунта?"},
                {"lang", "Язык"},
                {"login", "Логин"},
                {"password", "Пароль"},
                {"question_reg", "Уже есть аккаунт?"},
                {"inv_format", "Неверный формат ввода!"},
                {"gen_err", "Возникла ошибка!"},
                // Main View
                {"unable_update", "Не удалось отобразить изменение!"},
                {"req_err", "Ошибка при обработке запроса!"},
                {"serv_conn_err", "Ошибка соединения с сервером!"},
                {"filter", "Фильтр"},
                {"map", "Карта"},
                {"table", "Таблица"},
                {"add_upd", "Добавить/Обновить"},
                {"user", "Пользователь"},
                {"delete", "Удалить"},
                {"update", "Изменить"},

                {"creat_date", "Дата создания"},
                {"naming", "Название"},
                {"type", "Вид"},
                {"fuel", "Топливо"},
                {"power", "Мощность"},
                {"x_cord", "Х-координата"},
                {"y_cord", "У-координата"},
                {"owner", "Владелец"},
                {"create", "Создать"},
                {"min_cond", "Условие минимальности"},
                {"creat_title", "Создание объекта"},
                {"update_title", "Обновление объекта"},

                // Server messages
                {"command_fail", "Ошибка при обработке команды."},
                {"already_auth", "Пользователь уже авторизован!"},
                {"already_auth_name", "Пользователь с таким именем уже авторизован!"},
                {"need_auth", "Только авторизованные пользователи могут выполнять команды!"},
                {"success", "Команда выполнена успешно!"},
                {"not_min", "Элемент не минимальный."},
                {"auth_failed", "Авторизация не пройдена!"},
                {"reg_failed", "Регистрация не пройдена!"},
                {"already_exists", "Пользователь с таким логином уже существует."},
                {"not_your", "Вы можете удалять/изменять только созданные вами объекты"}
        };
    }
}
