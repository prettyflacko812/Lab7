package WorkWithCommands;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

public class Sender implements Runnable {
    private ReceiveCommand receiveCommand;

    public Sender(ReceiveCommand receiveCommand) throws InterruptedException {
        this.receiveCommand = receiveCommand;
        Thread sendThread = new Thread(this);
        Receiver.readThread.join(500);
        sendThread.start();
        sendThread.join();
    }

    @Override
    public void run() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(receiveCommand.answer);
            //System.out.println("receiveCommand.answer: " + receiveCommand.answer);
            byte[] answer = outputStream.toByteArray();
            ByteBuffer byteBuffer = ByteBuffer.wrap(answer);
            //System.out.println("byteBuffer: " + byteBuffer);
            Receiver.channel.send(byteBuffer, Receiver.address);
        } catch (ClassCastException e) {
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
}
