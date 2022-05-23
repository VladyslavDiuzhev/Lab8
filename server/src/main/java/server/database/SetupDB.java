package server.database;

import core.interact.UserInteractor;
import server.database.models.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public abstract class SetupDB {
    private static Connection connection;

    public static Connection getConnection() {
        return connection;
    }

    public static boolean createConnection(UserInteractor userInteractor, File cfgFile) {
        Connection c;
        try {
            Class.forName("org.postgresql.Driver");
            Properties info = new Properties();
            info.load(Files.newInputStream(cfgFile.toPath()));
            c = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%s/%s",info.getProperty("DB_HOST"),
                    info.getProperty("DB_PORT"),info.getProperty("DB_NAME")), info.getProperty("DB_LOGIN"),
                    info.getProperty("DB_PASSWORD"));

            userInteractor.broadcastMessage("Успешное соединение с БД.", true);

            connection = c;
            return true;
        } catch (Exception e) {
//            e.printStackTrace();
            userInteractor.broadcastMessage("Ошибка при соединении с БД. Проверьте валидность конфигурационного файла.", true);
            return false;
        }
    }

    public static void createTables(UserInteractor userInteractor) {
        Model[] models = {
                new UserModel(),
                new VehicleModel()
        };
        for (Model model : models) {
            if (model.createTable(connection)) {
                userInteractor.broadcastMessage(String.format("Таблица %s создана!", model.getName()), true);
            } else {
                userInteractor.broadcastMessage(String.format("Таблица %s уже существует!", model.getName()), true);
            }
        }
        userInteractor.broadcastMessage("Преднастройка БД завершена!", true);
    }
}
