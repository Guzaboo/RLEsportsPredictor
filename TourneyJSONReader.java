import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.Collections;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class TourneyJSONReader
{
    public static final boolean DEBUG = false;
    
    public static double SKILLCHANGEMAX = 14.579999999999995;
    public static double PLAYERWEIGHT = 6.399999999999996;
    public static double REGIONWEIGHT = 1.0;
    public static double DOMINANCEWEIGHT = -1.3322676295501853E-16;
    
    public static double GOALDIFDOM = 1.4399999999999995;
    public static double BONDOM = -1.4399999999999997;
    
    public static final long FILTERDATE = 1561132800000L;
    
    static String json = readLineByLineJava8("RLtournaments.json");
    static ArrayList<Match> matches = new ArrayList<Match>();
    static ArrayList<Player> players = new ArrayList<Player>();
    
    public static double main() {
        MatchPredictor pred = new MatchPredictor();
        double rightPredictions = 0;
        double wrongPredictions = 0;
        
        //log(json);
        Integer tournaments = JsonPath.read(json, "$.tournaments.length()");
        for(int e=0; e < tournaments; e++){
            String tourneyName = JsonPath.read(json, "$.tournaments[" + e + "].name");
            boolean premier = JsonPath.read(json, "$.tournaments[" + e + "].premier");
            
            log(tourneyName);
            boolean LAN = JsonPath.read(json, "$.tournaments[" + e + "].lan");
                
            Integer numTeams = JsonPath.read(json, "$.tournaments[" + e + "].teams.length()");
            Team[] teams = new Team[numTeams];
            for(int j=0; j < numTeams; j++) {
                String teamName = JsonPath.read(json, "$.tournaments[" + e + "].teams[" + j + "].name");
                String teamID = JsonPath.read(json, "$.tournaments[" + e + "].teams[" + j + "].id");
                String region = JsonPath.read(json, "$.tournaments[" + e + "].teams[" + j + "].region");
                
                Integer numPlayers = JsonPath.read(json, "$.tournaments[" + e + "].teams[" + j + "].players.length()");
                String[] players = new String[numPlayers];
                for(int k=0; k < numPlayers; k++) {
                    players[k] = JsonPath.read(json, "$.tournaments[" + e + "].teams[" + j + "].players[" + k + "]");
                }
                teams[j] = new Team(teamName, teamID, players, region);
                for(int k=0; k < teams[j].players.length; k++){
                    boolean add = true;
                    for(int a=0; a < TourneyJSONReader.players.size(); a++) {
                        //log(TourneyJSONReader.players.get(a).name + " is equal to " + teams[j].players[k].name + "? " + TourneyJSONReader.players.get(a).name.equals(teams[j].players[k].name));
                        if(TourneyJSONReader.players.get(a).name.equals(teams[j].players[k].name)){
                            add = false;
                            break;
                        }
                    }
                    if(add){
                        if(teams[j].players[k].name.equals("Tigreee")){
                            int asdfl = 0;
                        }
                        TourneyJSONReader.players.add(teams[j].players[k]);
                    }
                }
            }
            
            for(int j=0; j<numTeams; j++){
                log(teams[j].id);
            }
            
            Integer numMatches = JsonPath.read(json, "$.tournaments[" + e + "].matches.length()");
            matches:
            for(int j=0; j < numMatches; j++) {
                //log("$.tournaments[" + e + "].matches[" + j + "].date");
                long date = JsonPath.read(json, "$.tournaments[" + e + "].matches[" + j + "].date");
                
                //String[] teamIDs = new String[2];
                String id1 = JsonPath.read(json, "$.tournaments[" + e + "].matches[" + j + "].teams[0]");
                String id2 = JsonPath.read(json, "$.tournaments[" + e + "].matches[" + j + "].teams[1]");
                log("\n"+id1+" vs "+id2);
                //String[] teamIDs = JsonPath.read(json, "$.tournaments[" + e + "].matches[" + j + "].teams");
                Team team1 = null;
                Team team2 = null;
                for(int k=0; k < numTeams; k++) {
                    if(teams[k].id.toLowerCase().equals(/*teamIDs[0]*/id1.toLowerCase())) {
                        team1 = teams[k];
                    } else if(teams[k].id.toLowerCase().equals(/*teamIDs[1]*/id2.toLowerCase())) {
                        team2 = teams[k];
                    }
                }
                
                boolean p = true;
                /*if(e < 10){
                    p = false;
                }*/
                FutureMatch prediction = null;
                if(team1 != null && team2 != null){
                    for(int k=0; k < team1.players.length && p == true; k++){
                        //if(pred.findPlayer(team1.players[k].name).skill == 1000){
                        if(pred.findPlayer(team1.players[k].name).matches.size() < 5){
                            p = false;
                        }
                    }
                    for(int k=0; k < team2.players.length && p == true; k++){
                        //if(pred.findPlayer(team2.players[k].name).skill == 1000){
                        if(pred.findPlayer(team2.players[k].name).matches.size() < 5){
                            p = false;
                        }
                    }
                    if(p){
                        prediction = new FutureMatch(premier, LAN, date, team1, team2, pred);
                        log("Prediction: ");
                        log("Winner: "+prediction.winner.id);
                    }
                }
                
                Integer numGames = JsonPath.read(json, "$.tournaments[" + e + "].matches[" + j + "].games.length()");
                Game[] games = new Game[numGames];
                
                for(int k=0; k < numGames; k++) {
                    //Player[] team1, Player[] team2, int score1, int score2
                    if(team1 == null || team2 == null){
                        log("A team was not found. Cause: ID discrepancy.");
                        continue matches;
                        //throw new Error("A team was not found.");
                    } else {
                        int score1 = JsonPath.read(json, "$.tournaments[" + e + "].matches[" + j + "].games[" + k + "].['"+team1.id+"']");
                        //log("$.tournaments[" + e + "].matches[" + j + "].games[" + k + "]."+team2.id);
                        int score2 = JsonPath.read(json, "$.tournaments[" + e + "].matches[" + j + "].games[" + k + "].['"+team2.id+"']");
                        games[k] = new Game(team1.players, team2.players, score1, score2);
                    }
                }
                Match match = new Match(games, premier, LAN, date, team1.region, team2.region);
                double multiplier = 1;
                if(p){multiplier *= prediction.confidence;}
                if(LAN){multiplier *= 2;}
                if(premier){multiplier *= 2;}
                if(p && prediction != null && !match.bad){
                    if(match.win && prediction.winner == team1 || !match.win && p && prediction.winner == team2){
                        rightPredictions += multiplier;
                        match = new Match(games, premier, LAN, date, team1.region, team2.region, pred.avgSkill(team1.players) + pred.findRegion(team1.region).skill > pred.avgSkill(team2.players) + pred.findRegion(team2.region).skill);
                    } else {
                        wrongPredictions += multiplier;
                        match = new Match(games, premier, LAN, date, team1.region, team2.region, pred.avgSkill(team2.players) + pred.findRegion(team2.region).skill > pred.avgSkill(team1.players) + pred.findRegion(team1.region).skill);
                    }
                }
                if(match.date < FILTERDATE && !match.bad){
                    matches.add(match);
                    for(int k=0; k < team1.players.length; k++) {
                        for(int a=0; a < TourneyJSONReader.players.size(); a++) {
                            if(TourneyJSONReader.players.get(a).name.equals(team1.players[k].name)){
                                TourneyJSONReader.players.get(a).matches.add(match);
                            }
                        }
                    }
                    for(int k=0; k < team2.players.length; k++) {
                        for(int a=0; a < TourneyJSONReader.players.size(); a++) {
                            if(TourneyJSONReader.players.get(a).name.equals(team2.players[k].name)){
                                TourneyJSONReader.players.get(a).matches.add(match);
                            }
                        }
                    }
                    pred.calibrate(match);
                }
            }
        }
        log("Correct: "+rightPredictions+"\nTotal: "+(rightPredictions+wrongPredictions)+"\n%Correct: "+(rightPredictions*100/(rightPredictions+wrongPredictions)));
        return rightPredictions*100/(rightPredictions+wrongPredictions);
    }
    
    public static void findGoodWeights() throws FileNotFoundException{
        double bestskillchangemax = SKILLCHANGEMAX;
        double bestplayerweight = PLAYERWEIGHT;
        double bestregionweight = REGIONWEIGHT;
        double bestdominanceweight = DOMINANCEWEIGHT;
        
        double bestgoaldifdom = GOALDIFDOM;
        double bestbondom = BONDOM;
        double maxAccuracy = Double.NEGATIVE_INFINITY;
        
        while(true){
            double skillchangemax = SKILLCHANGEMAX;
            double playerweight = PLAYERWEIGHT;
            double regionweight = REGIONWEIGHT;
            double dominanceweight = DOMINANCEWEIGHT;
            
            double goaldifdom = GOALDIFDOM;
            double bondom = BONDOM;
            for(int w = 0; w < 6; w++){
                for(double i = -2; i <= 2; i += .2){
                    double m;
                    if(w==0){
                        m=(i+2)/2;
                    } else {
                        m=i;
                    }
                    if(m==0){
                        continue;
                    }
                    switch(w){
                        case 0:
                            SKILLCHANGEMAX = skillchangemax*m;
                            break;
                        case 1:
                            PLAYERWEIGHT = playerweight*m;
                            break;
                        case 2:
                            REGIONWEIGHT = regionweight*m;
                            break;
                        case 3:
                            DOMINANCEWEIGHT = dominanceweight*m;
                            break;
                        case 4:
                            GOALDIFDOM = goaldifdom*m;
                            break;
                        case 5:
                            BONDOM = bondom*m;
                            break;
                        default:
                            throw new Error("w is outside of the range 0 to 5: "+w);
                    }
                    reset();
                    double accuracy;
                    try{accuracy = main();}catch(Error e){continue;}
                    if(accuracy > maxAccuracy){
                        maxAccuracy = accuracy;
                        bestskillchangemax = SKILLCHANGEMAX;
                        bestplayerweight = PLAYERWEIGHT;
                        bestregionweight = REGIONWEIGHT;
                        bestdominanceweight = DOMINANCEWEIGHT;
            
                        bestgoaldifdom = GOALDIFDOM;
                        bestbondom = BONDOM;
                        String bestWeights = "{\n	\"SKILLCHANGEMAX\": "+bestskillchangemax
                            + ",\n	\"PLAYERWEIGHT\": "+bestplayerweight
                            + ",\n	\"REGIONWEIGHT\": "+bestregionweight
                            + ",\n	\"DOMINANCEWEIGHT\": "+bestdominanceweight
                            + ",\n	\"GOALDIFDOM\": "+bestgoaldifdom
                            + "\n	\"BONDOM\": "+bestbondom+"\n}";
                        PrintWriter out = new PrintWriter("settings.json");
                        out.println(bestWeights);
                        out.close();
                    } 
                }
                SKILLCHANGEMAX = bestskillchangemax;
                PLAYERWEIGHT = bestplayerweight;
                REGIONWEIGHT = bestregionweight;
                DOMINANCEWEIGHT = bestdominanceweight;
                
                GOALDIFDOM = bestgoaldifdom;
                BONDOM = bestbondom;
            }
        }
    }
    
    public static void reset(){
        matches = new ArrayList<Match>();
        players = new ArrayList<Player>();
    }
    
    private static String readLineByLineJava8(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
 
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
 
        return contentBuilder.toString();
    }
    
    public static void sortMatchesByDate(){
        for(int i=0; i<matches.size(); i++){
            long min = Long.MAX_VALUE;
            int mindex = -1;
            for(int j=i; j<matches.size(); j++){
                if(matches.get(j).date < min){
                    min = matches.get(j).date;
                    mindex = j;
                }
            }
            Collections.swap(matches, i, mindex);
        }
    }
    
    public static void listMatchesByPlayer(){
        for(int i=0; i < players.size(); i++){
            Player player = players.get(i);
            log("\n" + player.name + "'s Matches:");
            for(int j=0; j < player.matches.size(); j++){
                Match match = player.matches.get(j);
                String out = "";
                for(int k=0; k < match.team1.length; k++){
                    out += match.team1[k].name;
                    if(k != match.team1.length - 1){
                        out += " + ";
                    }
                }
                out += " VS ";
                for(int k=0; k < match.team2.length; k++){
                    out += match.team2[k].name;
                    if(k != match.team2.length - 1){
                        out += " + ";
                    }
                }
                log(out);
            }
        }
    }
    
    public static void log(String str){
        if(DEBUG){
            System.out.println(str);
        }
    }
}
