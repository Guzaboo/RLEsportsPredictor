public class Team
{
    String name, id;
    Player[] players;
    
    public Team(String name, String id, String[] players)
    {
        this.name = name;
        this.id = id;
        this.players = new Player[players.length];
        for(int i=0; i < players.length; i++){
            this.players[i] = new Player(players[i]);
        }
    }
    
    public Team(String name, String id, Player[] players)
    {
        this.name = name;
        this.id = id;
        this.players = players;
    }
}
