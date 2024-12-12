import java.math.BigInteger;

public class RequestMessage {
    int choice;
    int index;
    String Data;
    int difficulty;
    String ID;
    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

}
