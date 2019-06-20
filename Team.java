public class Team
{
    String name, id, region;
    Player[] players;
    
    public Team(String name, String id, String[] players, String region)
    {
        this.name = name;
        this.id = id;
        this.players = new Player[players.length];
        this.region = region;
        for(int i=0; i < players.length; i++){
            this.players[i] = new Player(players[i]);
        }
    }
    
    public Team(String name, String id, Player[] players, String region)
    {
        this.name = name;
        this.id = id;
        this.players = players;
        this.region = region;
    }
}
