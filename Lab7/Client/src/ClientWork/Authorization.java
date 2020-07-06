package ClientWork;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import static ClientWork.ReadCommands.locker;

public class Authorization implements Serializable {
    public String log;
    public String pass;
    Scanner scanner;
    private boolean isConnected = false;
    private SocketAddress socketAddress;
    private DatagramSocket socket;
    public static int k = 0;

    public Authorization() {
    }

    public Authorization(String login, String password) {
        this.log = login;
        this.pass = password;
    }

    public void authorize() throws IOException, InterruptedException, ClassNotFoundException {
        scanLogin();
        System.out.println("Введите пароль, если вы уже авторизованы. Если нет - придумайте новый пароль: ");
        pass = scanner.nextLine();
        Authorization user = new Authorization(log, pass);
        while (!isConnected) {
            isConnected = true;
            socket = connectToServer();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(user);
            byte[] commandInBytes = outputStream.toByteArray();
            DatagramPacket packet = new DatagramPacket(commandInBytes, commandInBytes.length, socketAddress);
            socket.send(packet);
            try {
                locker.lock();
                byte[] answerInBytes2 = new byte[16666];
                packet = new DatagramPacket(answerInBytes2, answerInBytes2.length);
                socket.receive(packet);
                ByteArrayInputStream byteStream2 = new ByteArrayInputStream(answerInBytes2);
                ObjectInputStream obs2 = new ObjectInputStream(byteStream2);
                String result2 = obs2.readObject().toString();

                if (!result2.contains("ekrngojekrnfowikerjf")) {
                    k = 0;
                    System.out.println(result2);
                } else if (result2.contains("ekrngojekrnfowikerjf")) {
                    System.out.println("Пароль введен неверно, либо пользователь с таким логином уже существует");
                    System.out.println("");
                    System.out.println("Введите команду. Для просмотра возможных команд введите help");
                    k++;
                }
            } catch (IOException e) {
                //e.printStackTrace();
            } finally {
                locker.unlock();
            }
            socket.close();
            outputStream.close();
        }
    }

    public void ifNull() throws InterruptedException {
        if ((log == null) || (log.trim().length() == 0)) {
            System.out.println("Логин не может быть пустым. Повторите ввод: ");
            scanLogin();
        }
    }

    public void scanLogin() throws InterruptedException {
        scanner = new Scanner(System.in);
        log = scanner.nextLine();
        ifNull();
    }

    private DatagramSocket connectToServer() throws IOException {
        try {
            InetAddress address = InetAddress.getByName("localhost");
            socketAddress = new InetSocketAddress(address, 1111);
            socket = new DatagramSocket();
            socket.connect(socketAddress);
        } catch (ConnectException e) {
            connectToServer();
        } catch (Exception e) {
            System.out.println("Проблемы с сервером");
        }
        return socket;
    }

    public String getLogin() {
        return log;
    }

    public String getPassword() {
        return pass;
    }

}