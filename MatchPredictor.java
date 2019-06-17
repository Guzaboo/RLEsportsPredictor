import java.util.ArrayList;
import java.util.Collections;

public class MatchPredictor
{
    ArrayList<Match> matches;
    ArrayList<Player> players;
    
    public MatchPredictor()
    {
        calibrate();
    }
    
    public void calibrate(){
        TourneyJSONReader.sortMatchesByDate();
        
        matches = TourneyJSONReader.matches;
        players = TourneyJSONReader.players;
        
        calculateSkillRating();
        sortPlayersBySkillRating();
    }
    
    public void main(){
        TourneyJSONReader.main(null);
        TourneyJSONReader.sortMatchesByDate();
        TourneyJSONReader.listMatchesByPlayer();
        
        matches = TourneyJSONReader.matches;
        players = TourneyJSONReader.players;
        
        calculateSkillRating();
        sortPlayersBySkillRating();
        
        printPlayers();
        
        Team gfe = new Team("Gale Force eSports", "GFE", new Player[] {findPlayer("Kaydop"), findPlayer("Turbopolsa"), findPlayer("ViolentPanda"), findPlayer("Dogu")});
        Team met = new Team("Method", "MET", new Player[] {findPlayer("Al0t"), findPlayer("Mognus"), findPlayer("Metsanauris"), findPlayer("Sniper")});
        Team mi = new Team("Mock-It eSports EU", "Mock-It", new Player[] {findPlayer("Paschy90"), findPlayer("FreaKii"), findPlayer("Fairy_Peak"), findPlayer("Petrick")});
        Team psg = new Team("PSG eSports", "PSG", new Player[] {findPlayer("Ferra"), findPlayer("Chausette45"), findPlayer("Bluey"), findPlayer("Yukeo")});
        Team nrg = new Team("NRG eSports", "NRG", new Player[] {findPlayer("Jacob"), findPlayer("Fireburner"), findPlayer("GarrettG"), findPlayer("DudeWithTheNose")});
        Team c9 = new Team("Cloud9", "C9", new Player[] {findPlayer("SquishyMuffinz"), findPlayer("Torment"), findPlayer("Gimmick"), findPlayer("Napp")});
        Team gho = new Team("Ghost", "GHO", new Player[] {findPlayer("Lethamyr"), findPlayer("Klassux"), findPlayer("Zanejackey"), findPlayer("Blueze")});
        Team g2 = new Team("G2 Esports", "G2", new Player[] {findPlayer("Kronovi"), findPlayer("Rizzo"), findPlayer("JKnaps"), findPlayer("Turtle")});
        Team chfs = new Team("Chiefs eSports Club", "CHFS", new Player[] {findPlayer("Jake"), findPlayer("Drippay"), findPlayer("Torsos")});
        Team ph = new Team("Pale Horse eSports", "PH", new Player[] {findPlayer("Kamii"), findPlayer("Kia"), findPlayer("CJCJ")});
        
        FutureMatch r1 = new FutureMatch(true, true, 1510334100000L, g2, chfs, this);
        FutureMatch r2 = new FutureMatch(true, true, 1510337700000L, psg, ph, this);
        FutureMatch q2 = new FutureMatch(true, true, 1510341300000L, gho, mi, this);
        FutureMatch q4 = new FutureMatch(true, true, 1510344900000L, met, nrg, this);
        FutureMatch q1 = new FutureMatch(true, true, 1510349400000L, gfe, r1, true, this);
        FutureMatch q3 = new FutureMatch(true, true, 1510353000000L, c9, r2, true, this);
        FutureMatch s1 = new FutureMatch(true, true, 1510420500000L, q1, true, q2, true, this);
        FutureMatch s2 = new FutureMatch(true, true, 1510424100000L, q3, true, q4, true, this);
        FutureMatch l11 = new FutureMatch(true, true, 1510428600000L, q4, false, r1, false, this);
        FutureMatch l12 = new FutureMatch(true, true, 1510432800000L, q2, false, r2, false, this);
        FutureMatch l21 = new FutureMatch(true, true, 1510436100000L, q3, false, l11, true, this);
        FutureMatch l22 = new FutureMatch(true, true, 1510440000000L, q1, false, l12, true, this);
        FutureMatch l31 = new FutureMatch(true, true, 1510507500000L, s1, false, l21, true, this);
        FutureMatch l32 = new FutureMatch(true, true, 1510511700000L, s2, false, l22, true, this);
        FutureMatch l4 = new FutureMatch(true, true, 1510514100000L, l31, true, l32, true, this);
        FutureMatch wf = new FutureMatch(true, true, 1510517700000L, s1, true, s2, true, this);
        FutureMatch lf = new FutureMatch(true, true, 1510523100000L, wf, false, l4, true, this);
        FutureMatch gf = new FutureMatch(true, true, 1510528500000L, wf, true, lf, true, this);
    }
    
    public boolean predict(Match future){
        if(future.games != null){
            throw new Error("Argument 'future' is not a future match.");
        }
        System.out.println("\nPredicting a match: " + future);
        
        double dominance = 10*scoreDominance(future.team1, future.team2, findSimilarMatches(future.team1, future.team2), future.premier, future.LAN, future.date);
        double team1Skill = avgSkill(future.team1);
        double team2Skill = avgSkill(future.team2);
        
        System.out.println("Dominance score: "+dominance);
        System.out.println("Skill Rating Difference: "+(team1Skill - team2Skill));
        
        double prediction = team1Skill - team2Skill + dominance;
        
        System.out.println("Prediction: " + prediction);
        
        if(prediction >= 0){
            return true;
        } else {
            return false;
        }
    }
    
    public Player findPlayer(String name){
        for(int i=0; i<players.size(); i++){
            if(players.get(i).name.equals(name)){
                return players.get(i);
            }
        }
        throw new Error("Couldn't find player by name: " + name);
    }
    
    private void calculateSkillRating(){
        for(int i=0; i<matches.size(); i++){
            Match match = matches.get(i);
            if(match.team1.length == 0 || match.team2.length == 0){
                continue;
            }
            int team1AVG = 0;
            for(int j=0; j<match.team1.length; j++){
                team1AVG += findPlayer(match.team1[j].name).skill;
            }
            team1AVG /= match.team1.length;
            int team2AVG = 0;
            for(int j=0; j<match.team2.length; j++){
                team2AVG += findPlayer(match.team2[j].name).skill;
            }
            team2AVG /= match.team2.length;
            if(match.win){
                for(int j=0; j<match.team1.length; j++){
                    Player player = findPlayer(match.team1[j].name);
                    double change = calculateSkillChange(player.skill, team2AVG);
                    player.skill += change;
                }
                for(int j=0; j<match.team2.length; j++){
                    Player player = findPlayer(match.team2[j].name);
                    double change = calculateSkillChange(team1AVG, player.skill);
                    player.skill -= change;
                }
            } else {
                for(int j=0; j<match.team2.length; j++){
                    Player player = findPlayer(match.team2[j].name);
                    double change = calculateSkillChange(player.skill, team1AVG);
                    player.skill += change;
                }
                for(int j=0; j<match.team1.length; j++){
                    Player player = findPlayer(match.team1[j].name);
                    double change = calculateSkillChange(team2AVG, player.skill);
                    player.skill -= change;
                }
            }
        }
    }
    
    private double calculateSkillChange(double winner, double loser){
        return 30.0/(1+Math.pow(2*Math.PI,0.0075*(winner-loser)));
    }
    
    private void sortPlayersBySkillRating(){
        for(int i=0; i<players.size(); i++){
            double max = Double.NEGATIVE_INFINITY;
            int maxIndex = -1;
            for(int j=i; j<players.size(); j++){
                if(players.get(j).skill > max){
                    max = players.get(j).skill;
                    maxIndex = j;
                }
            }
            Collections.swap(players, i, maxIndex);
        }
    }
    
    private void printPlayers(){
        System.out.println("\nPlayer Skill Levels:");
        for(int i=0; i<players.size(); i++){
            System.out.println(players.get(i).name + ": " + players.get(i).skill);
        }
    }
    
    private ArrayList<Match> findSimilarMatches(Player[] team1, Player[] team2){
        ArrayList<Match> relevant = new ArrayList<Match>();
        for(int i=0; i<matches.size(); i++){
            boolean hasTeam1PlayerOnTeam1 = false;
            boolean hasTeam2PlayerOnTeam1 = false;
            boolean hasTeam1PlayerOnTeam2 = false;
            boolean hasTeam2PlayerOnTeam2 = false;
            Player[] team = matches.get(i).team1;
            for(int j=0; j<team.length; j++){
                for(int k=0; k<team1.length; k++){
                    if(team[j].name.equals(team1[k].name)){
                        hasTeam1PlayerOnTeam1 = true;
                    }
                }
                for(int k=0; k<team2.length; k++){
                    if(team[j].name.equals(team2[k].name)){
                        hasTeam2PlayerOnTeam1 = true;
                    }
                }
            }
            team = matches.get(i).team2;
            for(int j=0; j<team.length; j++){
                for(int k=0; k<team1.length; k++){
                    if(team[j].name.equals(team1[k].name)){
                        hasTeam1PlayerOnTeam2 = true;
                    }
                }
                for(int k=0; k<team2.length; k++){
                    if(team[j].name.equals(team2[k].name)){
                        hasTeam2PlayerOnTeam2 = true;
                    }
                }
            }
            if(hasTeam1PlayerOnTeam1 && hasTeam2PlayerOnTeam2 || hasTeam2PlayerOnTeam1 && hasTeam1PlayerOnTeam2){
                relevant.add(matches.get(i));
            }
        }
        return relevant;
    }
    
    private double scoreDominance(Player[] team1, Player[] team2, ArrayList<Match> relevant, boolean premier, boolean LAN, long date){
        double totalWeight = 0;
        double totalWeightedScore = 0;
        for(int i=0; i<relevant.size(); i++){
            int count11 = 0;
            int count12 = 0;
            int count21 = 0;
            int count22 = 0;
            Player[] team = relevant.get(i).team1;
            for(int j=0; j<team.length; j++){
                for(int k=0; k<team1.length; k++){
                    if(team[j].name.equals(team1[k].name)){
                        count11++;
                    }
                }
                for(int k=0; k<team2.length; k++){
                    if(team[j].name.equals(team2[k].name)){
                        count21++;
                    }
                }
            }
            team = relevant.get(i).team2;
            for(int j=0; j<team.length; j++){
                for(int k=0; k<team1.length; k++){
                    if(team[j].name.equals(team1[k].name)){
                        count12++;
                    }
                }
                for(int k=0; k<team2.length; k++){
                    if(team[j].name.equals(team2[k].name)){
                        count22++;
                    }
                }
            }
            double weight = timeWeight(relevant.get(i).date, date);
            if(relevant.get(i).LAN == LAN){
                weight *= 2;
            }
            if(relevant.get(i).premier == premier){
                weight *= 2;
            }
            //System.out.println(weight);
            double weightedScore;
            if(count11 + count22 > count12 + count21) {
                weight *= count11 + count22 - count12 - count21;
                if(relevant.get(i).win){
                    weightedScore = relevant.get(i).dominance*weight;
                } else {
                    weightedScore = -1*relevant.get(i).dominance*weight;
                }
            } else if(count11 + count22 < count12 + count21) {
                weight *= count12 + count21 - count11 - count22;
                if(relevant.get(i).win){
                    weightedScore = -1*relevant.get(i).dominance*weight;
                } else {
                    weightedScore = relevant.get(i).dominance*weight;
                }
            } else {
                weight = 0;
                weightedScore = 0;
            }
            
            totalWeight += weight;
            totalWeightedScore += weightedScore;
        }
        if(totalWeight == 0){
            System.out.println("totalWeight = 0, relevant.size() = " + relevant.size());
            return 0;
        }
        return totalWeightedScore/totalWeight;
    }
    
    private double avgSkill(Player[] team){
        double total = 0;
        for(int i=0; i < team.length; i++){
            total += team[i].skill;
        }
        return total/team.length;
    }
    
    private double timeWeight(long past, long future){
        System.out.println(future/1000-past/1000);
        if(future-past == 0){future++;}
        System.out.println("timeWeight = "+(double)15779075/(future/1000-past/1000));
        //15779074882
        //2629743000
        //10368000000

        return (double)15779075L/(future/1000-past/1000);
    }
}
