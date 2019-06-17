import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.Collections;

public class TourneyJSONReader
{
    
    static String json = readLineByLineJava8("RLtournaments.json");
    static ArrayList<Match> matches = new ArrayList<Match>();
    static ArrayList<Player> players = new ArrayList<Player>();
    
    public static void main(String[] args) {
        MatchPredictor pred = new MatchPredictor();
        double rightPredictions = 0;
        double wrongPredictions = 0;
        
        //System.out.println(json);
        Integer tournaments = JsonPath.read(json, "$.tournaments.length()");
        for(int e=0; e < tournaments; e++){
            String tourneyName = JsonPath.read(json, "$.tournaments[" + e + "].name");
            boolean premier = JsonPath.read(json, "$.tournaments[" + e + "].premier");
            
            System.out.println(tourneyName);
            boolean LAN = JsonPath.read(json, "$.tournaments[" + e + "].lan");
                
            Integer numTeams = JsonPath.read(json, "$.tournaments[" + e + "].teams.length()");
            Team[] teams = new Team[numTeams];
            for(int j=0; j < numTeams; j++) {
                String teamName = JsonPath.read(json, "$.tournaments[" + e + "].teams[" + j + "].name");
                String teamID = JsonPath.read(json, "$.tournaments[" + e + "].teams[" + j + "].id");
                
                Integer numPlayers = JsonPath.read(json, "$.tournaments[" + e + "].teams[" + j + "].players.length()");
                String[] players = new String[numPlayers];
                for(int k=0; k < numPlayers; k++) {
                    players[k] = JsonPath.read(json, "$.tournaments[" + e + "].teams[" + j + "].players[" + k + "]");
                }
                teams[j] = new Team(teamName, teamID, players);
                for(int k=0; k < teams[j].players.length; k++){
                    boolean add = true;
                    for(int a=0; a < TourneyJSONReader.players.size(); a++) {
                        //System.out.println(TourneyJSONReader.players.get(a).name + " is equal to " + teams[j].players[k].name + "? " + TourneyJSONReader.players.get(a).name.equals(teams[j].players[k].name));
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
                System.out.println(teams[j].id);
            }
            
            Integer numMatches = JsonPath.read(json, "$.tournaments[" + e + "].matches.length()");
            matches:
            for(int j=0; j < numMatches; j++) {
                //System.out.println("$.tournaments[" + e + "].matches[" + j + "].date");
                long date = JsonPath.read(json, "$.tournaments[" + e + "].matches[" + j + "].date");
                
                //String[] teamIDs = new String[2];
                String id1 = JsonPath.read(json, "$.tournaments[" + e + "].matches[" + j + "].teams[0]");
                String id2 = JsonPath.read(json, "$.tournaments[" + e + "].matches[" + j + "].teams[1]");
                System.out.println("\n"+id1+" vs "+id2);
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
                FutureMatch prediction = null;
                if(team1 != null && team2 != null){
                    pred.calibrate();
                    for(int k=0; k < team1.players.length; k++){
                        if(pred.findPlayer(team1.players[k].name).skill == 1000){
                            p = false;
                        }
                    }
                    for(int k=0; k < team2.players.length; k++){
                        if(pred.findPlayer(team2.players[k].name).skill == 1000){
                            p = false;
                        }
                    }
                    if(p){
                        prediction = new FutureMatch(premier, LAN, date, team1, team2, pred);
                        System.out.println("Prediction: ");
                        System.out.println("Winner: "+prediction.winner.id);
                    }
                }
                
                Integer numGames = JsonPath.read(json, "$.tournaments[" + e + "].matches[" + j + "].games.length()");
                Game[] games = new Game[numGames];
                for(int k=0; k < numGames; k++) {
                    //Player[] team1, Player[] team2, int score1, int score2
                    if(team1 == null || team2 == null){
                        System.out.println("A team was not found. Cause: ID discrepancy.");
                        continue matches;
                        //throw new Error("A team was not found.");
                    } else {
                        int score1 = JsonPath.read(json, "$.tournaments[" + e + "].matches[" + j + "].games[" + k + "].['"+team1.id+"']");
                        //System.out.println("$.tournaments[" + e + "].matches[" + j + "].games[" + k + "]."+team2.id);
                        int score2 = JsonPath.read(json, "$.tournaments[" + e + "].matches[" + j + "].games[" + k + "].['"+team2.id+"']");
                        games[k] = new Game(team1.players, team2.players, score1, score2);
                    }
                }
                Match match = new Match(games, premier, LAN, date);
                matches.add(match);
                if(p && prediction != null){
                    if(match.win && prediction.winner == team1 || !match.win && p && prediction.winner == team2){
                        rightPredictions++;
                    } else {
                        wrongPredictions++;
                    }
                }
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
            }
        }
        System.out.println("Correct: "+rightPredictions+"\nTotal: "+(rightPredictions+wrongPredictions)+"\n%Correct: "+(rightPredictions*100/(rightPredictions+wrongPredictions)));
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
            System.out.println("\n" + player.name + "'s Matches:");
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
                System.out.println(out);
            }
        }
    }
}
