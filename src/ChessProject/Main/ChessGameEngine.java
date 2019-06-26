package ChessProject.Main;

import java.util.ArrayList;
import java.util.List;

public class ChessGameEngine {
	ChessPosition position;
	int player;
	List<ChessPosition> positions = new ArrayList<ChessPosition>();
	ChessGame game;
	//Khởi tạo
	public ChessGameEngine(ChessPosition position, int player) {
		this.position = position;
		this.player = player;
		game = new ChessGame(position);
	}
	
	public ChessPosition[] getPositions(){
		return positions.toArray(new ChessPosition[positions.size()]);
	}
	//tìm tất cả các bàn cờ có thể có của người chơi player
	public void generateMoves() {
		if(player == ChessGameData.HUMAN){
			for(int i = 1; i<position.human_pieces.length; i++){
				Piece piece = position.human_pieces[i];
				if(piece == null) continue;
				switch(piece.value){
				case Piece.PAWN:
					humanPawnMoves(piece);
					break;
				case Piece.KNIGHT:
					humanKnightMoves(piece);
					break;
				case Piece.BISHOP:
					humanBishopMoves(piece);
					break;
				case Piece.ROOK:
					humanRookMoves(piece);
					break;
				case Piece.QUEEN:
					humanQueenMoves(piece);
					break;
				case Piece.KING:
					humanKingMoves(piece);
					break;
				}
			}
		} else {
			for(int i = 1; i<position.computer_pieces.length; i++){
				Piece piece = position.computer_pieces[i];
				if(piece == null) continue;
				switch(piece.value){
				case Piece.PAWN:
					computerPawnMoves(piece);
					break;
				case Piece.KNIGHT:
					computerKnightMoves(piece);
					break;
				case Piece.BISHOP:
					computerBishopMoves(piece);
					break;
				case Piece.ROOK:
					computerRookMoves(piece);
					break;
				case Piece.QUEEN:
					computerQueenMoves(piece);
					break;
				case Piece.KING:
					computerKingMoves(piece);
					break;
				}
			}
		}
	}


	public void humanPawnMoves(Piece pawn) {
		int location = pawn.location;
		int forward_piece_index = position.board[location-10];
		if(forward_piece_index != ChessGameData.ILLEGAL){
			if(forward_piece_index == ChessGameData.EMPTY && game.safeMove(ChessGameData.HUMAN, location, location-10)){
				positions.add(new ChessPosition(position, new Move(location, location-10)));
			}
		}
		if(location > 80 && forward_piece_index == ChessGameData.EMPTY 
				&& position.board[location-20] == ChessGameData.EMPTY 
				&& game.safeMove(ChessGameData.HUMAN, location, location-20)){
			positions.add(new ChessPosition(position, new Move(location, location-20)));
		}
		
		int right_piece_index = position.board[location-9];
		if(right_piece_index != ChessGameData.ILLEGAL){
			if(right_piece_index < 0 && game.safeMove(ChessGameData.HUMAN, location, location-9)){
				positions.add(new ChessPosition(position, new Move(location, location-9)));
			}
		}
		
		int left_piece_index = position.board[location-11];
		if(left_piece_index != ChessGameData.ILLEGAL){
			if(left_piece_index < 0 && game.safeMove(ChessGameData.HUMAN, location, location-11)){
				positions.add(new ChessPosition(position, new Move(location, location-11)));
			}
		}
	}
	
	public void humanKnightMoves(Piece knight) {
		int location = knight.location;
		int[] destinations = { location-21, location+21, location-19, location+19, location-12, location+12, location-8, location+8 };
		for(int i = 0; i < destinations.length; i++){
			int des_piece_index = position.board[destinations[i]];
			if(des_piece_index != ChessGameData.ILLEGAL 
					&& (des_piece_index == ChessGameData.EMPTY || des_piece_index < 0) 
					&& game.safeMove(ChessGameData.HUMAN, location, destinations[i])){
				positions.add(new ChessPosition(position,new Move(location,destinations[i])));
			}
		}
	}
	
	public void humanBishopMoves(Piece bishop) {
		int location = bishop.location;
        int[] deltas = {11,-11,9,-9};
        for (int i = 0; i < deltas.length; i++) {
            int des = location + deltas[i];            
            while (true) {
                int des_piece_index = position.board[des];
                if (des_piece_index == ChessGameData.ILLEGAL) {
                    break;
                }
                boolean safe_move = game.safeMove(ChessGameData.HUMAN,location,des);
                if (des_piece_index == ChessGameData.EMPTY || des_piece_index < 0){
                    if(safe_move){
                        positions.add(new ChessPosition(position,new Move(location,des)));
                        if (des_piece_index != ChessGameData.EMPTY || !safe_move) {                        
                            break;
                        }
                    }else if(des_piece_index != ChessGameData.EMPTY) break;
                } else if(des_piece_index>0 && des_piece_index != ChessGameData.EMPTY){
                    break;
                }
                des += deltas[i];
            }
        }
	}
	
	public void humanRookMoves(Piece rook) {
		int location = rook.location;
        int[] deltas = {1,-1,10,-10};
        for (int i = 0; i < deltas.length; i++) {
            int des = location + deltas[i];            
            while (true) {
                int des_piece_index = position.board[des];
                if (des_piece_index == ChessGameData.ILLEGAL) {
                    break;
                }
                boolean safe_move = game.safeMove(ChessGameData.HUMAN,location,des);
                if (des_piece_index == ChessGameData.EMPTY || des_piece_index < 0) {
                    if(safe_move){
                        positions.add(new ChessPosition(position,new Move(location,des)));
                        if (des_piece_index != ChessGameData.EMPTY) {          
                            break;
                        }
                    }else if(des_piece_index != ChessGameData.EMPTY) break;
                } else if(des_piece_index>0 && des_piece_index != ChessGameData.EMPTY){
                    break;
                }
                des += deltas[i];
            }
        }
	}

	public void humanQueenMoves(Piece queen) {
		int location = queen.location;
        int[] deltas = {1,-1,10,-10,11,-11,9,-9};
        for (int i = 0; i < deltas.length; i++) {
            int des = location + deltas[i];            
            while (true) {
                int des_piece_index = position.board[des];
                if (des_piece_index == ChessGameData.ILLEGAL) {
                    break;
                }
                boolean safe_move = game.safeMove(ChessGameData.HUMAN,location,des);
                if (des_piece_index == ChessGameData.EMPTY || des_piece_index < 0) {
                    if(safe_move){
                        positions.add(new ChessPosition(position,new Move(location,des)));
                        if (des_piece_index != ChessGameData.EMPTY) {                        
                            break;
                        }
                    }else if(des_piece_index != ChessGameData.EMPTY) break;
                } else if(des_piece_index>0 && des_piece_index != ChessGameData.EMPTY){
                    break;
                }
                des += deltas[i];
            }
        }
	}

	public void humanKingMoves(Piece king) {
		int location = king.location;
        int[] destinations = {location+1,location-1,location+10,location-10,
            location+11,location-11,location+9,location-9};
        for(int i=0; i<destinations.length; i++){
            int des_piece_index = position.board[destinations[i]];
            if(des_piece_index != ChessGameData.ILLEGAL 
            		&& (des_piece_index == ChessGameData.EMPTY || des_piece_index<0)
                    && game.safeMove(ChessGameData.HUMAN,location,destinations[i])){
                positions.add(new ChessPosition(position,new Move(location,destinations[i])));
            }
        }
	}
	
	public void computerPawnMoves(Piece pawn) {
		int location = pawn.location;
        int forward_piece_index = position.board[location+10];
        if(forward_piece_index != ChessGameData.ILLEGAL){
            if(forward_piece_index == ChessGameData.EMPTY && game.safeMove(ChessGameData.COMPUTER,location,location+10)){ 
                positions.add(new ChessPosition(position,new Move(location,location+10)));
            }
        }
        
        if(location < 39 && forward_piece_index == ChessGameData.EMPTY && 
                position.board[location+20] == ChessGameData.EMPTY && game.safeMove(ChessGameData.COMPUTER,location,location+20)) {            
                positions.add(new ChessPosition(position,new Move(location,location+20)));
        }
        
        int right_piece_index = position.board[location+11];
        if(right_piece_index != ChessGameData.ILLEGAL) {
            if(right_piece_index>0 && right_piece_index != ChessGameData.EMPTY &&
                    game.safeMove(ChessGameData.COMPUTER,location,location+11))
                positions.add(new ChessPosition(position,new Move(location,location+11)));
        }
        int left_piece_index = position.board[location+9];
        if(left_piece_index != ChessGameData.ILLEGAL) {
            if(left_piece_index>0 && left_piece_index != ChessGameData.EMPTY &&
                    game.safeMove(ChessGameData.COMPUTER,location,location+9))
                positions.add(new ChessPosition(position,new Move(location,location+9)));
        }     
	}
	
	public void computerKnightMoves(Piece knight) {
		int location = knight.location;
		int[] destinations = { location-21, location+21, location-19, location+19, location-12, location+12, location-8, location+8 };
		for(int i = 0; i < destinations.length; i++){
			int des_piece_index = position.board[destinations[i]];
			if(des_piece_index != ChessGameData.ILLEGAL 
					&& (des_piece_index == ChessGameData.EMPTY || des_piece_index > 0) 
					&& game.safeMove(ChessGameData.COMPUTER, location, destinations[i])){
				positions.add(new ChessPosition(position,new Move(location,destinations[i])));
			}
		}
	}
	
	public void computerBishopMoves(Piece bishop) {
		int location = bishop.location;
        int[] deltas = {11,-11,9,-9};
        for (int i = 0; i < deltas.length; i++) {
            int des = location + deltas[i];            
            while (true) {
                int des_piece_index = position.board[des];
                if (des_piece_index == ChessGameData.ILLEGAL) {
                    break;
                }
                boolean safe_move = game.safeMove(ChessGameData.COMPUTER,location,des);
                if (des_piece_index == ChessGameData.EMPTY || des_piece_index > 0) {
                    if(safe_move){
                        positions.add(new ChessPosition(position,new Move(location,des)));
                        if (des_piece_index != ChessGameData.EMPTY || !safe_move) {                        
                            break;
                        }
                    }else if(des_piece_index != ChessGameData.EMPTY) break;
                } else if(des_piece_index<0){
                    break;
                }
                des += deltas[i];
            }
        }
	}
	
	public void computerRookMoves(Piece rook) {
		int location = rook.location;
        int[] deltas = {1,-1,10,-10};
        for (int i = 0; i < deltas.length; i++) {
            int des = location + deltas[i];           
            while (true) {
                 int des_piece_index = position.board[des];
                if (des_piece_index == ChessGameData.ILLEGAL) {
                    break;
                }
                boolean safe_move = game.safeMove(ChessGameData.COMPUTER,location,des);
                if (des_piece_index == ChessGameData.EMPTY || des_piece_index > 0) {
                    if(safe_move){
                        positions.add(new ChessPosition(position,new Move(location,des)));
                        if (des_piece_index != ChessGameData.EMPTY) {                        
                            break;
                        }
                    }else if(des_piece_index != ChessGameData.EMPTY) break;
                } else if(des_piece_index<0){
                    break;
                }
                des += deltas[i];
            }
        }
	}

	public void computerQueenMoves(Piece queen) {
		 int location = queen.location;
	        int[] deltas = {1,-1,10,-10,11,-11,9,-9};
	        for (int i = 0; i < deltas.length; i++) {
	            int des = location + deltas[i];            
	            while (true) {
	                int des_piece_index = position.board[des];
	                if (des_piece_index == ChessGameData.ILLEGAL) {
	                    break;
	                }
	                boolean safe_move = game.safeMove(ChessGameData.COMPUTER,location,des);
	                if (des_piece_index == ChessGameData.EMPTY || des_piece_index > 0) {
	                    if(safe_move){
	                        positions.add(new ChessPosition(position,new Move(location,des)));
	                        if (des_piece_index != ChessGameData.EMPTY) {                        
	                            break;
	                        }
	                    }else if(des_piece_index != ChessGameData.EMPTY) break;
	                } else if(des_piece_index<0){
	                    break;
	                }
	                des += deltas[i];
	            }
	        }
	}

	public void computerKingMoves(Piece king) {
		int location = king.location;
        int[] destinations = {location+1,location-1,location+10,location-10,
            location+11,location-11,location+9,location-9};
        for(int i=0; i<destinations.length; i++){
            int des_piece_index = position.board[destinations[i]];
            if(des_piece_index != ChessGameData.ILLEGAL 
            		&& (des_piece_index == ChessGameData.EMPTY || des_piece_index>0) &&
                    game.safeMove(ChessGameData.COMPUTER,location,destinations[i])){
                positions.add(new ChessPosition(position,new Move(location,destinations[i])));
            }
        }
	}
	//hàm đánh giá
	public int evaluate(ChessPosition position){
		int human_score = 0, computer_score = 0;
		for(int i = 0; i < position.human_pieces.length; i++){
			if(position.human_pieces[i] != null){
				Piece piece = position.human_pieces[i];
				int value = piece.value;
				human_score += value;
				int location = piece.location;
				int col = (location % 10) - 1;
				int row = ((location-col)/10) - 2;

				switch(value){
				case Piece.PAWN:
					if(row < 4) human_score += (8-row);
					if(col > 4){
						human_score -= (col-4);
					} else if(col < 3) {
						human_score -= (3 - col);
					}
					if(col > 1 && col < 6 && row <6) human_score += 2;
					if(row == 0) human_score += Piece.QUEEN;
					break;
				case Piece.KNIGHT:
					if(row == 7) human_score -= 1;
					if(col == 0 || col == 7) human_score -= 1;
					if(col > 1 && col < 6 && row > 1 && row < 6) human_score += 1;
					break;
				case Piece.BISHOP:
				case Piece.ROOK:
				case Piece.QUEEN:
					if(row == 7) human_score-=1;
                    if(col == 0 || col == 7) human_score -= 1;
                    if(col>0 && col<7 && row>0 && row<7) human_score +=1;
					break;
				}
			}
			if(position.computer_pieces[i] != null){
				Piece piece = position.computer_pieces[i];
				int value = piece.value;
				computer_score += value;
				int location = piece.location;
				int col = (location % 10) - 1;
				int row = ((location-col)/10) - 2;
				switch(value){
				case Piece.PAWN: 
                    if(row>3)computer_score+=row;
                    if(col>4){
                        computer_score -= ((col-4));
                    }else if(col<3) {
						computer_score -= ((3 - col));
					}
                    if(col>1 && col<6 && row>1) computer_score +=2;
                    if(row == 7) computer_score += Piece.QUEEN;
                    break;
                case Piece.KNIGHT: 
                    if(row == 0) computer_score-=1;
                    if(col == 0 || col == 7) computer_score -= 1;
                    if(col>1 && col<6 && row>1 && row<6) computer_score +=1;
                    break;
                case Piece.BISHOP:
				case Piece.ROOK:
				case Piece.QUEEN:
                    if(row == 0) computer_score-=1;
                    if(col == 0 || col == 7) computer_score -= 1;
                    if(col>0 && col<7 && row>0 && row<7) computer_score +=1;
                    break;
				}
			}
		}
		return human_score - computer_score;
	}
}
