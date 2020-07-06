package MainPack;

import WorkWithCommands.Commands;
import WorkWithCommands.Receiver;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static File fileeek;

    //private static ExecutorService executeIt = Executors.newFixedThreadPool(8);

    public static void main(String[] args) throws Exception {
        try {
            String fnamee = args[0];
            fileeek = new File(fnamee);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            System.out.println("Неверный ввод параметров (нужно было ввести название файла со скриптом)");
        }

        /*try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver не найден");
            e.printStackTrace();
            return;
        }
        System.out.println("PostgreSQL JDBC Driver успешно подключен");
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
        } catch (SQLException e) {
            System.out.println("Связь прервалась");
            e.printStackTrace();
            return;
        }
        if (connection != null) {
            System.out.println("Вы успешно подключились к базе данных");
        } else {
            System.out.println("Не удалось подключиться к базе данных");
        }
        Statement statement = connection.createStatement();
        String usersTable = "CREATE TABLE Users " +
                "(login VARCHAR(100), " +
                "password VARCHAR(100));";
        String routesTable = "CREATE TABLE Routes " +
                "(id  SERIAL PRIMARY KEY NOT NULL, " +
                "creationDate DATE, " +
                "name VARCHAR(50), " +
                "coorX FLOAT, " +
                "coorY INT, " +
                "fromX INT, " +
                "fromY FLOAT, " +
                "fromZ INT, " +
                "toX FLOAT, " +
                "toY FLOAT, " +
                "toZ INTEGER, " +
                "distance INTEGER, " +
                "login VARCHAR(50));";
        statement.executeUpdate(usersTable);
        statement.executeUpdate(routesTable);*/

        Commands.connectToBD();

        try {
            new Receiver();
        } catch (NullPointerException e) {
            System.out.println("Возникла ошибка. Работа сервера прекращена. Подключитесь заново");
            System.exit(0);
        }
    }

    public Server() {
    }

    public File getScriptName() {
        return fileeek;
    }
}
