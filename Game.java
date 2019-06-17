public class Game
{
    Player[] team1, team2;
    int score1, score2;
    boolean win;
    
    public Game(Player[] team1, Player[] team2, int score1, int score2)
    {
        this.team1 = team1;
        this.team2 = team2;
        this.score1 = score1;
        this.score2 = score2;
        if(score1 > score2){
            win = true;
        } else {
            win = false;
        }
    }
}
