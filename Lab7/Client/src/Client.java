import ClientWork.Authorization;
import ClientWork.ReadCommands;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        new ReadCommands();
    }
}
