import java.util.ArrayList;
import java.util.Collections;

public class MatchPredictor
{
    ArrayList<Match> matches;
    ArrayList<Player> players;
    Region[] regions = {new Region("eu"), new Region("na"), new Region("oce"), new Region("sam")};
    
    public MatchPredictor()
    {
        calibrate();
    }
    
    public void calibrate(){
        TourneyJSONReader.sortMatchesByDate();
        
        matches = TourneyJSONReader.matches;
        players = TourneyJSONReader.players;
        
        calculateSkillRating();
        calculateConfidenceRating();
        sortPlayersBySkillRating();
        sortRegionsBySkillRating();
    }
    
    public void calibrate(Match match){
        if(match.date >= matches.get(matches.size()-1).date){
            matches = TourneyJSONReader.matches;
            players = TourneyJSONReader.players;
            
            changeSkillRating(match);
            calculateConfidenceRating(match.premier, match.LAN, match.date);
            sortPlayersBySkillRating();
            sortRegionsBySkillRating();
        } else {
            TourneyJSONReader.sortMatchesByDate();
            
            matches = TourneyJSONReader.matches;
            players = TourneyJSONReader.players;
            
            for(int i=0; i<players.size(); i++){
                players.get(i).skill = 1000;
            }
            
            calculateSkillRating();
            calculateConfidenceRating(match.premier, match.LAN, match.date);
            sortPlayersBySkillRating();
            sortRegionsBySkillRating();
        }
    }
    
    public void main(){
        TourneyJSONReader.main();
        
        calibrate();
        
        printPlayers();
        printRegions();
        
        /*
        Team gfe = new Team("Gale Force eSports", "GFE", new Player[] {findPlayer("Kaydop"), findPlayer("Turbopolsa"), findPlayer("ViolentPanda"), findPlayer("Dogu")}, "eu");
        Team met = new Team("Method", "MET", new Player[] {findPlayer("Al0t"), findPlayer("Mognus"), findPlayer("Metsanauris"), findPlayer("Sniper")}, "eu");
        Team mi = new Team("Mock-It eSports EU", "Mock-It", new Player[] {findPlayer("Paschy90"), findPlayer("FreaKii"), findPlayer("Fairy_Peak"), findPlayer("Petrick")}, "eu");
        Team psg = new Team("PSG eSports", "PSG", new Player[] {findPlayer("Ferra"), findPlayer("Chausette45"), findPlayer("Bluey"), findPlayer("Yukeo")}, "eu");
        Team nrg = new Team("NRG eSports", "NRG", new Player[] {findPlayer("Jacob"), findPlayer("Fireburner"), findPlayer("GarrettG"), findPlayer("DudeWithTheNose")}, "na");
        Team c9 = new Team("Cloud9", "C9", new Player[] {findPlayer("SquishyMuffinz"), findPlayer("Torment"), findPlayer("Gimmick"), findPlayer("Napp")}, "na");
        Team gho = new Team("Ghost", "GHO", new Player[] {findPlayer("Lethamyr"), findPlayer("Klassux"), findPlayer("Zanejackey"), findPlayer("Blueze")}, "na");
        Team g2 = new Team("G2 Esports", "G2", new Player[] {findPlayer("Kronovi"), findPlayer("Rizzo"), findPlayer("JKnaps"), findPlayer("Turtle")}, "na");
        Team chfs = new Team("Chiefs eSports Club", "CHFS", new Player[] {findPlayer("Jake"), findPlayer("Drippay"), findPlayer("Torsos")}, "oce");
        Team ph = new Team("Pale Horse eSports", "PH", new Player[] {findPlayer("Kamii"), findPlayer("Kia"), findPlayer("CJCJ")}, "oce");
        
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
        
        FutureMatch test = new FutureMatch(true, true, 1510334100000L, met, mi, this);*/
        
        Team c9 = new Team("Cloud9", "C9", new Player[] {findPlayer("SquishyMuffinz"), findPlayer("Torment"), findPlayer("Gimmick"), findPlayer("Sadjunior")}, "na");
        Team g2 = new Team("G2 Esports", "G2", new Player[] {findPlayer("Rizzo"), findPlayer("JKnaps"), findPlayer("Chicago"), findPlayer("Mijo")}, "na");
        Team nrg = new Team("NRG eSports", "NRG", new Player[] {findPlayer("Fireburner"), findPlayer("GarrettG"), findPlayer("Jstn"), findPlayer("Chrome")}, "na");
        Team rge = new Team("Rogue", "RGE", new Player[] {findPlayer("Wonder"), findPlayer("AyyJayy"), findPlayer("Kronovi"), findPlayer("Sizz")}, "na");
        
        Team psg = new Team("PSG Esports", "PSG", new Player[] {findPlayer("Ferra"), findPlayer("Chausette45"), findPlayer("Fruity"), findPlayer("Mout")}, "eu");
        Team rv = new Team("Renault Vitality", "RV", new Player[] {findPlayer("Fairy_Peak"), findPlayer("Scrub_Killa"), findPlayer("Kaydop"), findPlayer("Neqzo")}, "eu");
        Team tt = new Team("Triple Trouble", "TT", new Player[] {findPlayer("Ronaky"), findPlayer("Tadpole"), findPlayer("Kassio"), findPlayer("Seeb")}, "eu");
        Team fcb = new Team("FC Barcelona", "FCB", new Player[] {findPlayer("Deevo"), findPlayer("Bluey"), findPlayer("Alpha54"), findPlayer("ByMateos")}, "eu");
        
        Team rng = new Team("Renegades", "RNG", new Player[] {findPlayer("Torsos"), findPlayer("Kamii"), findPlayer("ZeN"), findPlayer("Requiem")}, "oce");
        Team gzg = new Team("Ground Zero Gaming", "GZG", new Player[] {findPlayer("Decka"), findPlayer("Julz"), findPlayer("Siki"), findPlayer("Kia")}, "oce");
        
        Team lke = new Team("Lowkey Esports", "LKE", new Player[] {findPlayer("Tander"), findPlayer("Caard"), findPlayer("RenaN"), findPlayer("FAsi")}, "sam");
        Team intz = new Team("INTZ eSports", "INTZ", new Player[] {findPlayer("Repi"), findPlayer("PJ"), findPlayer("Matix"), findPlayer("J%C3%84T")}, "sam");
        
        System.out.println("~~~~~~~~HARD CODED PREDICTIONS~~~~~~~~~~~");
        
        FutureMatch a1 = new FutureMatch(true, true, 1561140000000L, nrg, intz, this);
        FutureMatch a2 = new FutureMatch(true, true, 1561219200000L, a1, true, psg, this);
        FutureMatch a3 = new FutureMatch(true, true, 1561233600000L, a1, false, psg, this);
        
        FutureMatch b1 = new FutureMatch(true, true, 1561143600000L, rv, gzg, this);
        FutureMatch b2 = new FutureMatch(true, true, 1561222800000L, b1, true, g2, this);
        FutureMatch b3 = new FutureMatch(true, true, 1561237200000L, b1, false, g2, this);
        
        FutureMatch c1 = new FutureMatch(true, true, 1561132800000L, rng, fcb, this);
        FutureMatch c2 = new FutureMatch(true, true, 1561147200000L, c1, true, rge, this);
        FutureMatch c3 = new FutureMatch(true, true, 1561226400000L, c1, false, rge, this);
        
        FutureMatch d1 = new FutureMatch(true, true, 1561136400000L, lke, c9, this);
        FutureMatch d2 = new FutureMatch(true, true, 1561150800000L, d1, true, tt, this);
        FutureMatch d3 = new FutureMatch(true, true, 1561230000000L, d1, false, tt, this);
        
        /*FutureMatch q1 = new FutureMatch(true, true, 1561305600000L, nrg, g2, this);
        FutureMatch q2 = new FutureMatch(true, true, 1561309200000L, c9, rng, this);
        FutureMatch q3 = new FutureMatch(true, true, 1561312800000L, rv, intz, this);
        FutureMatch q4 = new FutureMatch(true, true, 1561316400000L, fcb, lke, this);
        FutureMatch s1 = new FutureMatch(true, true, 1561320000000L, q1, true, q2, true, this);
        FutureMatch s2 = new FutureMatch(true, true, 1561323600000L, q3, true, q4, true, this);
        FutureMatch gf = new FutureMatch(true, true, 1561327200000L, s1, true, s2, true, this);*/
    }
    
    public boolean predict(Match future){
        if(future.games != null){
            throw new Error("Argument 'future' is not a future match.");
        }
        System.out.println("\nPredicting a match: " + future);
        
        double dominance = 10*scoreDominance(future.team1, future.team2, findSimilarMatches(future.team1, future.team2), future.premier, future.LAN, future.date);
        double team1Skill = avgSkill(future.team1);
        double team2Skill = avgSkill(future.team2);
        double team1Conf = avgConf(future.team1);
        double team2Conf = avgConf(future.team2);
        double skillWeight = (team1Conf + team2Conf)/2;
        
        TourneyJSONReader.log("Dominance score: "+6*dominance);
        double skill = team1Skill - team2Skill;
        TourneyJSONReader.log("Skill Rating Difference: "+skill);
        double regSkill = findRegion(future.region1).skill - findRegion(future.region2).skill;
        TourneyJSONReader.log("Region Rating Difference: "+regSkill);
        TourneyJSONReader.log("Skill Weight: "+skillWeight);
        
        double prediction = skillWeight*TourneyJSONReader.PLAYERWEIGHT*skill + skillWeight*TourneyJSONReader.REGIONWEIGHT*regSkill + TourneyJSONReader.DOMINANCEWEIGHT*dominance;
        
        System.out.println("Prediction: " + prediction);
        future.predictionConfidence = prediction;
        
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
    
    public Region findRegion(String name){
        for(int i=0; i<regions.length; i++){
            if(regions[i].name.equals(name)){
                return regions[i];
            }
        }
        throw new Error("Couldn't find region: " + name);
    }
    
    private void calculateSkillRating(){
        for(int i=0; i<matches.size(); i++){
            Match match = matches.get(i);
            changeSkillRating(match);
        }
    }
    
    private void calculateConfidenceRating(boolean premier, boolean LAN, long date){
        double totalCorrect = 0;
        double total = 0;
        for(int i=0; i<players.size(); i++){
            Player player = players.get(i);
            for(int j=0; j<player.matches.size(); j++){
                Match match = player.matches.get(j);
                double weight = timeWeight(match.date, date);
                if(match.LAN == LAN){
                    weight *= 2;
                }
                if(match.premier == premier){
                    weight *= 2;
                }
                if(match.predictedAttempt){
                    total += weight;
                    if(match.predictedCorrect){
                        totalCorrect += weight;
                    }
                }
            }
            if(total == 0 || totalCorrect == 0){ 
                player.skillConfidence = 0; 
            } else {
                player.skillConfidence = totalCorrect/total;
            }
        }
    }
    
    private void calculateConfidenceRating(){
        double totalCorrect = 0;
        double total = 0;
        for(int i=0; i<players.size(); i++){
            Player player = players.get(i);
            for(int j=0; j<player.matches.size(); j++){
                Match match = player.matches.get(j);
                double weight = 1;
                if(match.predictedAttempt){
                    total += weight;
                    if(match.predictedCorrect){
                        totalCorrect += weight;
                    }
                }
            }
            player.skillConfidence = totalCorrect/total;
        }
    }
    
    private void changeSkillRating(Match match){
        if(match.team1.length == 0 || match.team2.length == 0){
            return;
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
                double change = calculateSkillChange(player.skill, team2AVG, match);
                if(change < 0){  throw new Error("Winning team should not be losing mmr..."); }
                if(change > TourneyJSONReader.SKILLCHANGEMAX){  throw new Error("Skill change should not be greater than" + TourneyJSONReader.SKILLCHANGEMAX); }
                player.skill += change;
            }
            for(int j=0; j<match.team2.length; j++){
                Player player = findPlayer(match.team2[j].name);
                double change = calculateSkillChange(team1AVG, player.skill, match);
                if(change < 0){  throw new Error("Losing team should not be gaining mmr..."); }
                if(change > TourneyJSONReader.SKILLCHANGEMAX){  throw new Error("Skill change should not be greater than " + TourneyJSONReader.SKILLCHANGEMAX); }
                player.skill -= change;
            }
        } else {
            for(int j=0; j<match.team2.length; j++){
                Player player = findPlayer(match.team2[j].name);
                double change = calculateSkillChange(player.skill, team1AVG, match);
                if(change < 0){  throw new Error("Winning team should not be losing mmr..."); }
                if(change > TourneyJSONReader.SKILLCHANGEMAX){  throw new Error("Skill change should not be greater than " + TourneyJSONReader.SKILLCHANGEMAX); }
                player.skill += change;
            }
            for(int j=0; j<match.team1.length; j++){
                Player player = findPlayer(match.team1[j].name);
                double change = calculateSkillChange(team2AVG, player.skill, match);
                if(change < 0){  throw new Error("Losing team should not be gaining mmr..."); }
                if(change > TourneyJSONReader.SKILLCHANGEMAX){  throw new Error("Skill change should not be greater than " + TourneyJSONReader.SKILLCHANGEMAX); }
                player.skill -= change;
            }
        }
        
        if(!match.region1.equals(match.region2)){
            if(match.win){
                Region r1 = findRegion(match.region1);
                Region r2 = findRegion(match.region2);
                double change = calculateSkillChange(r1.skill, r2.skill, match);
                r1.skill += change;
                r2.skill -= change;
            } else {
                Region r1 = findRegion(match.region1);
                Region r2 = findRegion(match.region2);
                double change = calculateSkillChange(r2.skill, r1.skill, match);
                r2.skill += change;
                r1.skill -= change;
            }
        }
    }
    
    public double calculateSkillChange(double winner, double loser, Match match){
        return match.dominance2*TourneyJSONReader.SKILLCHANGEMAX/(1+Math.pow(2*Math.PI,0.0199253858115*(winner-loser)));
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
    
    private void sortRegionsBySkillRating(){
        for(int i=0; i<regions.length; i++){
            double max = Double.NEGATIVE_INFINITY;
            int maxIndex = -1;
            for(int j=i; j<regions.length; j++){
                if(regions[j].skill > max){
                    max = regions[j].skill;
                    maxIndex = j;
                }
            }
            Region maxR = regions[maxIndex];
            regions[maxIndex] = regions[i];
            regions[i] = maxR;
        }
    }
    
    private void printPlayers(){
        TourneyJSONReader.log("\nPlayer Skill Levels:");
        for(int i=0; i<players.size(); i++){
            TourneyJSONReader.log(players.get(i).name + ": " + players.get(i).skill);
        }
    }
    
    private void printRegions(){
        TourneyJSONReader.log("\nRegion Skill Levels:");
        for(int i=0; i<regions.length; i++){
            TourneyJSONReader.log(regions[i].name + ": " + regions[i].skill);
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
            //TourneyJSONReader.log(weight);
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
            TourneyJSONReader.log("totalWeight = 0, relevant.size() = " + relevant.size());
            return 0;
        }
        return totalWeightedScore/totalWeight;
    }
    
    public double avgSkill(Player[] team){
        double total = 0;
        for(int i=0; i < team.length; i++){
            total += team[i].skill;
        }
        if(total == 0){
            return 0;
        }
        return total/team.length;
    }
    
    private double avgConf(Player[] team){
        double total = 0;
        for(int i=0; i < team.length; i++){
            total += team[i].skillConfidence;
        }
        if(total == 0 || team.length == 0){
            return 0;
        }
        return total/team.length;
    }
    
    private double timeWeight(long past, long future){
        if(future-past<0){return 0;}
        TourneyJSONReader.log(Long.toString(future/1000-past/1000+151033));
        if(future-past == 0){future++;}
        TourneyJSONReader.log("timeWeight = "+(double)15779075/(future/1000-past/1000+151033));
        //15779074882
        //2629743000
        //10368000000

        return (double)15779075L/(future/1000-past/1000+151033);
    }
}
