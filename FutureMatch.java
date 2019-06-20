public class FutureMatch
{
    boolean premier, LAN;
    long date;
    Team winner, loser;
    double confidence;
    
    public FutureMatch(boolean premier, boolean LAN, long date, Team team1, Team team2, MatchPredictor p)
    {
        this.premier = premier;
        this.LAN = LAN;
        this.date = date;
        
        Match match = new Match(premier, LAN, date, team1.players, team2.players, team1.region, team2.region);
        
        if(p.predict(match)){
            winner = team1;
            loser = team2;
        } else {
            winner = team2;
            loser = team1;
        }
        
        confidence = Math.abs(match.predictionConfidence);
    }
    
    public FutureMatch(boolean premier, boolean LAN, long date, FutureMatch match1, boolean winner1, FutureMatch match2, boolean winner2, MatchPredictor p)
    {
        this.premier = premier;
        this.LAN = LAN;
        this.date = date;
        Team team1, team2;
        if(winner1){
            team1 = match1.winner;
        } else {
            team1 = match1.loser;
        }
        if(winner2){
            team2 = match2.winner;
        } else {
            team2 = match2.loser;
        }
        
        Match match = new Match(premier, LAN, date, team1.players, team2.players, team1.region, team2.region);
        
        if(p.predict(match)){
            winner = team1;
            loser = team2;
        } else {
            winner = team2;
            loser = team1;
        }
        
        confidence = Math.abs(match.predictionConfidence);
    }
    
    public FutureMatch(boolean premier, boolean LAN, long date, Team team1, FutureMatch match2, boolean winner2, MatchPredictor p)
    {
        this.premier = premier;
        this.LAN = LAN;
        this.date = date;
        Team team2;
        if(winner2){
            team2 = match2.winner;
        } else {
            team2 = match2.loser;
        }
        
        Match match = new Match(premier, LAN, date, team1.players, team2.players, team1.region, team2.region);
        
        if(p.predict(match)){
            winner = team1;
            loser = team2;
        } else {
            winner = team2;
            loser = team1;
        }
        
        confidence = Math.abs(match.predictionConfidence);
    }
    
    public FutureMatch(boolean premier, boolean LAN, long date, FutureMatch match1, boolean winner1, Team team2, MatchPredictor p)
    {
        this.premier = premier;
        this.LAN = LAN;
        this.date = date;
        Team team1;
        if(winner1){
            team1 = match1.winner;
        } else {
            team1 = match1.loser;
        }
        
        Match match = new Match(premier, LAN, date, team1.players, team2.players, team1.region, team2.region);
        
        if(p.predict(match)){
            winner = team1;
            loser = team2;
        } else {
            winner = team2;
            loser = team1;
        }
        
        confidence = Math.abs(match.predictionConfidence);
    }
}
