public class Match
{
    Game[] games;
    Player[] team1, team2;
    boolean premier;
    boolean LAN;
    long date;
    
    //stats
    int matchesWon = 0;
    int matchesLost = 0;
    boolean win;
    double dominance;
    
    public Match(Game[] games, boolean premier, boolean LAN, long date)
    {
        this.games = games;
        this.premier = premier;
        this.LAN = LAN;
        this.date = date;
        team1 = games[0].team1;
        team2 = games[0].team2;
        for(int i = 0; i < games.length; i++){
            if(games[i].win){
                matchesWon++;
            } else {
                matchesLost++;
            }
        }
        if(matchesWon > matchesLost){
            win = true;
        } else {
            win = false;
        }
        
        dominance();
    }
    
    public Match(boolean premier, boolean LAN, long date, Player[] team1, Player[] team2)
    {
        this.premier = premier;
        this.LAN = LAN;
        this.date = date;
        this.team1 = team1;
        this.team2 = team2;
    }
    
    private void dominance(){
        System.out.println("Match:");
        double goalDif = 0;
        for(int i=0; i < games.length; i++){
            System.out.println("Game "+(i+1)+": "+games[i].score1+"-"+games[i].score2);
            if(win){
                goalDif += games[i].score1 - games[i].score2;
            } else {
                goalDif += games[i].score2 - games[i].score1;
            }
        }
        
        double gamesLost;
        double boN;
        if(win){
            gamesLost = matchesLost;
            boN = matchesWon*2 - 1;
        } else {
            gamesLost = matchesWon;
            boN = matchesLost*2 - 1;
        }
        
        System.out.println("Game 'Differential': "+(boN-gamesLost));
        dominance = goalDif/games.length + (boN-gamesLost)/boN; //average goal differential in a game + percentage of matches won (if a bo5 is 3-1, the winning team is considered to have won 4 of 5 matches) //////////I may need to change this, because some tournaments make the teams play out the bo5 anyway
        System.out.println("Dominance: "+dominance);
    }
    
    public String toString(){
        String out = "";
        for(int k=0; k < team1.length; k++){
            out += team1[k].name;
            if(k != team1.length - 1){
                out += " + ";
            }
        }
        out += " VS ";
        for(int k=0; k < team2.length; k++){
            out += team2[k].name;
            if(k != team2.length - 1){
                out += " + ";
            }
        }
        return out;
    }
}
