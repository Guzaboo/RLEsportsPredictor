public class Match
{
    Game[] games;
    Player[] team1, team2;
    String region1, region2;
    boolean premier;
    boolean LAN;
    long date;
    
    boolean predictedAttempt;
    boolean predictedCorrect;
    double predictionConfidence;
    
    //stats
    int matchesWon = 0;
    int matchesLost = 0;
    boolean win;
    double dominance, dominance2;
    boolean bad = false;
    
    public Match(Game[] games, boolean premier, boolean LAN, long date, String region1, String region2, boolean pCorrect)
    {
        int bad = 0;
        for(int i=0; i<games.length; i++){
            if(games[i].score1 == 0 && games[i].score2 == 0){
                bad++;
            }
        }
        if(bad == 0){
            this.games = games;
        } else {
            this.games = new Game[games.length - bad];
            bad = 0;
            for(int i=0; i<games.length; i++){
                if(games[i].score1 != 0 || games[i].score2 != 0){
                    this.games[i-bad] = games[i];
                } else {
                    bad++;
                }
            }
        }
        this.premier = premier;
        this.LAN = LAN;
        this.date = date;
        this.region1 = region1;
        this.region2 = region2;
        predictedAttempt = true;
        predictedCorrect = pCorrect;
        team1 = this.games[0].team1;
        team2 = this.games[0].team2;
        for(int i = 0; i < this.games.length; i++){
            if(this.games[i].win){
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
    
    public Match(Game[] games, boolean premier, boolean LAN, long date, String region1, String region2)
    {
        int bad = 0;
        for(int i=0; i<games.length; i++){
            if(games[i].score1 == 0 && games[i].score2 == 0){
                bad++;
            }
        }
        if(bad == 0){
            this.games = games;
        } else if(bad == games.length){
            this.bad = true;
        } else {
            this.games = new Game[games.length - bad];
            bad = 0;
            for(int i=0; i<games.length; i++){
                if(games[i].score1 != 0 || games[i].score2 != 0){
                    this.games[i-bad] = games[i];
                } else {
                    bad++;
                }
            }
        }
        if(!this.bad){
            this.premier = premier;
            this.LAN = LAN;
            this.date = date;
            this.region1 = region1;
            this.region2 = region2;
            team1 = this.games[0].team1;
            team2 = this.games[0].team2;
            for(int i = 0; i < this.games.length; i++){
                if(this.games[i].win){
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
    }
    
    public Match(boolean premier, boolean LAN, long date, Player[] team1, Player[] team2, String region1, String region2)
    {
        this.premier = premier;
        this.LAN = LAN;
        this.date = date;
        this.team1 = team1;
        this.team2 = team2;
        this.region1 = region1;
        this.region2 = region2;
    }
    
    private void dominance(){
        TourneyJSONReader.log("Match:");
        double goalDif = 0;
        for(int i=0; i < games.length; i++){
            TourneyJSONReader.log("Game "+(i+1)+": "+games[i].score1+"-"+games[i].score2);
            if(win){
                goalDif += games[i].score1 - games[i].score2;
            } else {
                goalDif += games[i].score2 - games[i].score1;
            }
        }
        
        double gamesLost;
        double gamesWon;
        double boN;
        if(win){
            gamesLost = matchesLost;
            gamesWon = matchesWon;
            boN = matchesWon*2 - 1;
        } else {
            gamesLost = matchesWon;
            gamesWon = matchesLost;
            boN = matchesLost*2 - 1;
        }
        
        TourneyJSONReader.log("Game 'Differential': "+(boN-gamesLost));
        dominance = TourneyJSONReader.GOALDIFDOM*goalDif/games.length + TourneyJSONReader.BONDOM*(boN-gamesLost)/boN; //average goal differential in a game + percentage of matches won (if a bo5 is 3-1, the winning team is considered to have won 4 of 5 matches) //////////I may need to change this, because some tournaments make the teams play out the bo5 anyway
        for(int i=0; i < games.length; i++){
            if(win){
                dominance2 += (double)games[i].score1 / (double)(games[i].score1 + games[i].score2);
            } else {
                dominance2 += (double)games[i].score2 / (double)(games[i].score1 + games[i].score2);
            }
        }
        dominance2 /= games.length;
        TourneyJSONReader.log("Dominance: "+dominance);
        TourneyJSONReader.log("Dominance: "+dominance2);
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
