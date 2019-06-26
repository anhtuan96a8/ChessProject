package ChessProject.Main;

public class Move {
	int source_location;
	int des_location;
	
	public Move(){
		this.source_location = -1;
		this.des_location = -1;
	}
	
	public Move(int source, int destination){
		this.source_location = source;
		this.des_location = destination;
	}
}
