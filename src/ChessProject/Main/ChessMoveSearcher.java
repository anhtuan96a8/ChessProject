package ChessProject.Main;

public class ChessMoveSearcher {
	ChessProject chessproject;
	
	public ChessMoveSearcher(ChessProject chessproject){
		this.chessproject = chessproject;
	}
	//score=điểm người chơi - điểm máy.Lượt người chơi chọn sẽ tìm nước nào có score bé nhất.
	// trong khí đó lượt máy đi sẽ chọn nước đi nào mà trong đó score lớn nhất
	public ChessPosition alphaBeta(int player, ChessPosition position, int max, int min, int depth) {
		if(depth == 0) return position;
		ChessPosition best_position = null;
		ChessGameEngine engine = new ChessGameEngine(position, player);
		engine.generateMoves();
		ChessPosition[] positions = engine.getPositions();
		if(positions.length == 0) return position;
		
		for(ChessPosition _position : positions){
			if(best_position == null) best_position = _position;
			if(player == ChessGameData.HUMAN){
				ChessPosition opponent_position = alphaBeta(ChessGameData.COMPUTER, _position, max, min, depth-1);
				int score = engine.evaluate(opponent_position);
				if(score>=min ) return _position;
				if(score > max){
					best_position = _position;
					max = score;
				}
			} else {
				ChessPosition opponet_position = alphaBeta(ChessGameData.HUMAN, _position, max, min, depth-1);
				if(new ChessGame(opponet_position).isChecked(ChessGameData.HUMAN)) return _position;
				int score = engine.evaluate(opponet_position);
				if(score<=max) return _position;
                if(score<min){
                    best_position = _position;
                    min = score;
                } 
			}
		}
		return best_position;
	}
}
