package WorkWithCommands;

import java.io.Serializable;

public class ReceiveCommand implements Serializable {

    String answer;

    public ReceiveCommand(String command, String answer) {
        this.answer = answer;
    }

    public ReceiveCommand(String answer){
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }
}
