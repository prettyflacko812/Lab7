package WorkWithCommands;

import ClientWork.Authorization;
import ClientWork.HelpObject;
import RouteInformation.*;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayDeque;

public class Receiver implements Runnable {
    public static String com;
    public static Route route;
    public static long id;
    public static String name;
    public static Integer dist;
    public static String hist;
    public static String[] arr;
    public static DatagramChannel channel;
    public static SocketAddress address;
    private byte[] buffer = new byte[16666];
    public static ByteBuffer byteBuffer;
    public static String login;
    public String password;
    public String encrPass;
    public static ArrayDeque<String> logins;
    public static final String URL = "jdbc:postgresql://localhost:5432/data";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "321";

    static Thread readThread;

    public Receiver() {
        readThread = new Thread(this);
        readThread.start();
    }

    @Override
    public void run() {
        try {
            Commands command = new Commands();
            address = new InetSocketAddress(1111);
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.bind(address);
            while (true) {
                byteBuffer = ByteBuffer.wrap(buffer);
                do {
                    address = channel.receive(byteBuffer);
                } while (address == null);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer);
                ObjectInputStream obs = new ObjectInputStream(byteStream);
                com = "";
                Object obj = obs.readObject();
                if (obj instanceof Authorization) {
                    login = ((Authorization) obj).getLogin();
                    //System.out.println("Логин: " + login);
                    password = ((Authorization) obj).getPassword();
                    encrPass = encryptPass(password);
                    userVerification();
                } else {
                    com = ((HelpObject) obj).getCom();
                    route = ((HelpObject) obj).getRoute();
                    id = ((HelpObject) obj).getId();
                    name = ((HelpObject) obj).getName();
                    dist = ((HelpObject) obj).getDist();
                    hist = ((HelpObject) obj).getHist();
                }

                arr = com.trim().split(" ", 2);
                switch (arr[0]) {
                    case "help":
                        ReceiveCommand help = new ReceiveCommand("help", Commands.help());
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                        objectOutputStream.writeObject(help.answer);
                        byte[] answer = outputStream.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer);
                        channel.send(byteBuffer, address);
                        break;
                    case "info":
                        ReceiveCommand info = new ReceiveCommand("info", Commands.info());
                        /*ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(outputStream1);
                        objectOutputStream1.writeObject(info.answer);
                        byte[] answer1 = outputStream1.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer1);
                        channel.send(byteBuffer, address);*/
                        new Sender(info);
                        break;
                    case "show":
                        ReceiveCommand show = new ReceiveCommand("show", Commands.show());
                        /*ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(outputStream2);
                        objectOutputStream2.writeObject(show.answer);
                        byte[] answer2 = outputStream2.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer2);
                        channel.send(byteBuffer, address);*/
                        new Sender(show);
                        break;
                    case "add":
                        ReceiveCommand add = new ReceiveCommand("add", Commands.add(route));
                        /*ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream3 = new ObjectOutputStream(outputStream3);
                        objectOutputStream3.writeObject(add.answer);
                        byte[] answer3 = outputStream3.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer3);
                        channel.send(byteBuffer, address);*/
                        new Sender(add);
                        break;
                    case "updateId":
                        ReceiveCommand updateId = new ReceiveCommand("updateId", Commands.updateId(id, route));
                        /*ByteArrayOutputStream outputStream4 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream4 = new ObjectOutputStream(outputStream4);
                        objectOutputStream4.writeObject(updateId.answer);
                        byte[] answer4 = outputStream4.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer4);
                        channel.send(byteBuffer, address);*/
                        new Sender(updateId);
                        break;
                    case "removeById":
                        ReceiveCommand removeById = new ReceiveCommand("removeById", Commands.removeById(id));
                        /*ByteArrayOutputStream outputStream5 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream5 = new ObjectOutputStream(outputStream5);
                        objectOutputStream5.writeObject(removeById.answer);
                        byte[] answer5 = outputStream5.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer5);
                        channel.send(byteBuffer, address);*/
                        new Sender(removeById);
                        break;
                    case "clear":
                        ReceiveCommand clear = new ReceiveCommand("clear", Commands.clear());
                        /*ByteArrayOutputStream outputStream6 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream6 = new ObjectOutputStream(outputStream6);
                        objectOutputStream6.writeObject(clear.answer);
                        byte[] answer6 = outputStream6.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer6);
                        channel.send(byteBuffer, address);*/
                        new Sender(clear);
                        break;
                    case "executeScript":
                        ReceiveCommand executeScript = new ReceiveCommand("executeScript", command.executeScript().toString());
                        /*ByteArrayOutputStream outputStream7 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream7 = new ObjectOutputStream(outputStream7);
                        objectOutputStream7.writeObject(executeScript.answer);
                        byte[] answer7 = outputStream7.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer7);
                        channel.send(byteBuffer, address);*/
                        new Sender(executeScript);
                        break;
                    case "exit":
                        System.out.println("Клиент завершил работу программы, коллекция сохранена");
                        break;
                    case "addIfMax":
                        ReceiveCommand addIfMax = new ReceiveCommand("addIfMax", command.addIfMax(route));
                        /*ByteArrayOutputStream outputStream8 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream8 = new ObjectOutputStream(outputStream8);
                        objectOutputStream8.writeObject(addIfMax.answer);
                        byte[] answer8 = outputStream8.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer8);
                        channel.send(byteBuffer, address);*/
                        new Sender(addIfMax);
                        break;
                    case "removeLower":
                        ReceiveCommand removeLower = new ReceiveCommand("removeLower", Commands.removeLower(id));
                        /*ByteArrayOutputStream outputStream9 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream9 = new ObjectOutputStream(outputStream9);
                        objectOutputStream9.writeObject(removeLower.answer);
                        byte[] answer9 = outputStream9.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer9);
                        channel.send(byteBuffer, address);*/
                        new Sender(removeLower);
                        break;
                    case "history":
                        ReceiveCommand history = new ReceiveCommand("history", hist);
                        /*ByteArrayOutputStream outputStream10 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream10 = new ObjectOutputStream(outputStream10);
                        objectOutputStream10.writeObject(history.answer);
                        byte[] answer10 = outputStream10.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer10);
                        channel.send(byteBuffer, address);*/
                        new Sender(history);
                        break;
                    case "maxByTo":
                        ReceiveCommand maxByTo = new ReceiveCommand("maxByTo", Commands.maxByTo());
                        /*ByteArrayOutputStream outputStream11 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream11 = new ObjectOutputStream(outputStream11);
                        objectOutputStream11.writeObject(maxByTo.answer);
                        byte[] answer11 = outputStream11.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer11);
                        channel.send(byteBuffer, address);*/
                        new Sender(maxByTo);
                        break;
                    case "filterStartsWithName":
                        ReceiveCommand filterStartsWithName = new ReceiveCommand("filterStartsWithName", Commands.filterStartsWithName(name));
                        /*ByteArrayOutputStream outputStream12 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream12 = new ObjectOutputStream(outputStream12);
                        objectOutputStream12.writeObject(filterStartsWithName.answer);
                        byte[] answer12 = outputStream12.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer12);
                        channel.send(byteBuffer, address);*/
                        new Sender(filterStartsWithName);
                        break;
                    case "filterLessThanDistance":
                        ReceiveCommand filterLessThanDistance = new ReceiveCommand("filterLessThanDistance", Commands.filterLessThanDistance(dist));
                        /*ByteArrayOutputStream outputStream13 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream13 = new ObjectOutputStream(outputStream13);
                        objectOutputStream13.writeObject(filterLessThanDistance.answer);
                        byte[] answer13 = outputStream13.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer13);
                        channel.send(byteBuffer, address);*/
                        new Sender(filterLessThanDistance);
                        break;
                }
            }
        } catch (ClassNotFoundException | ClassCastException e) {
            System.out.println("Сервер ожидал команду, а получил что-то не то");
        } catch (ClosedChannelException ignored) {
        } catch (IOException e) {
            System.out.println("Проблемы с подключением...");
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException ignored) {
            System.out.println("Произошла ошибка");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encryptPass(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-384");
        byte[] messageDigest = md.digest(password.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        StringBuilder hashtext = new StringBuilder(no.toString(16));
        while (hashtext.length() < 32) {
            hashtext.insert(0, "0");
        }
        return hashtext.toString();
    }

    public void userVerification() throws IOException {
        String answer = "";
        String selus = "SELECT * from users;";
        String insus = "INSERT INTO users (login, password) VALUES (?,?)";
        boolean checker = false;
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(selus);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("login").equals(login)) {
                    checker = true;
                    if (resultSet.getString("password").equals(encrPass)) {
                        answer = "Вы успешно вошли в учётную запись";
                        ByteArrayOutputStream outputStream14 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream14 = new ObjectOutputStream(outputStream14);
                        objectOutputStream14.writeObject(answer);
                        byte[] answer14 = outputStream14.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer14);
                        channel.send(byteBuffer, address);
                    } else {
                        answer = "ekrngojekrnfowikerjf";
                        ByteArrayOutputStream outputStream15 = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream15 = new ObjectOutputStream(outputStream15);
                        objectOutputStream15.writeObject(answer);
                        byte[] answer15 = outputStream15.toByteArray();
                        byteBuffer = ByteBuffer.wrap(answer15);
                        channel.send(byteBuffer, address);
                        //channel.close();
                        //new Receiver();
                    }
                }
            }
            if (!checker) {
                PreparedStatement preparedStatement1 = connection.prepareStatement(insus);
                //encrPass = "1";
                preparedStatement1.setString(1, login);
                preparedStatement1.setString(2, encrPass);
                preparedStatement1.executeUpdate();
                answer = "Пользователь успешно авторизовался \n";
                ByteArrayOutputStream outputStream16 = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream16 = new ObjectOutputStream(outputStream16);
                objectOutputStream16.writeObject(answer);
                byte[] answer16 = outputStream16.toByteArray();
                byteBuffer = ByteBuffer.wrap(answer16);
                channel.send(byteBuffer, address);
            }
            logins = new ArrayDeque<>();
            logins.addFirst(login);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}