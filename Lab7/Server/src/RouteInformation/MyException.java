package RouteInformation;

import java.io.Serializable;

public class MyException extends Exception implements Serializable {
    public MyException(String message) {
        super(message);
    }
}