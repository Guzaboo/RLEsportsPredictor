import java.util.ArrayList;

public class Player
{
    String name;
    ArrayList<Match> matches = new ArrayList<Match>();
    double skill = 1000;
    double skillConfidence = 0;

    public Player(String name)
    {
        this.name = name;
    }
}
