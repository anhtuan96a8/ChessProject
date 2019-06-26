package ChessProject.Main;

public class ChessGame {
	ChessPosition position;
	Piece humanKing, computerKing;
	//Hàm khởi tạo
	public ChessGame(ChessPosition position) {
		humanKing = position.human_pieces[8];
		computerKing = position.computer_pieces[8];
		this.position = position;
	}
	//lấy kết quả
	public int getResult(int player){
		int state = -1;
		ChessGameEngine engine = new ChessGameEngine(position, player);
		engine.generateMoves();
		ChessPosition[] positions = engine.getPositions();
		if(positions.length == 0){
            if(isChecked(player)) {
                state = ChessGameData.CHECKMATE;  //nếu player bị ăn tướng thì state=1
            }
            else state = ChessGameData.DRAW; //nếu không thì state=0
        }
		return state;
	}

	//kiểm tra xem nước đi tiếp theo có phải nước đi an toàn hay không?(Liệu rằng sau nước đi đó có bị mất tướng hay không)
	public boolean safeMove(int player, int source, int destination){
		Move _move = new Move(source, destination);
		ChessPosition _position = new ChessPosition(position, _move);//tạo một bàn cờ mới với các quân đã được sắp xếp lại theo nước đi
		ChessGame game = new ChessGame(_position);
		return !game.isChecked(player);
	}
	//kiểm tra xem tướng của player có bị ăn hay không
	public boolean isChecked(int player){
		boolean checked = false;
		Piece king = (player == ChessGameData.HUMAN)?humanKing:computerKing;
		checked = checkedByPawn(king);
		if(!checked) checked = checkedByKnight(king);
		if(!checked) checked = checkedByBishop(king);
		if(!checked) checked = checkedByRook(king);
		if(!checked) checked = checkedByQueen(king);
		if(!checked) checked = checkedByKing(king);
		return checked;
	}
	//kiểm tra xem vua có bị quân tốt ăn không
	private boolean checkedByPawn(Piece king) {
		boolean checked = false;
		int location = king.location;
		if(king == humanKing){
			int right_square = position.board[location-9];
			int lelf_square = position.board[location-11];
			if(right_square < 0 && position.computer_pieces[-right_square].value == Piece.PAWN) checked = true;
			if(lelf_square < 0 && position.computer_pieces[-lelf_square].value == Piece.PAWN) checked = true;
		} else {
			int right_square = position.board[location+11];
			int left_square = position.board[location+9];
			if(right_square != ChessGameData.ILLEGAL){
				if(right_square > 0 && right_square != ChessGameData.EMPTY && position.human_pieces[right_square].value == Piece.PAWN)
					checked = true;
			}
			if(left_square != ChessGameData.ILLEGAL){
				if(left_square > 0 && left_square != ChessGameData.EMPTY && position.human_pieces[left_square].value == Piece.PAWN)
					checked = true;
			}
		}
		return checked;
	}
	//kiểm tra xem vua có bị quân mã ăn không
	public boolean checkedByKnight(Piece king) {
		boolean checked = false;
		int location = king.location;
		int[] destinations = {location-21, location+21,location-19,location+19,location-12,location+12,location-8,location+8};
		for(int destination : destinations){
			int des_square = position.board[destination];
			if(des_square == ChessGameData.ILLEGAL) continue;
			if(king == humanKing){
				if(des_square < 0 && position.computer_pieces[-des_square].value == Piece.KNIGHT){
					checked = true;
					break;
				}
			} else {
				if(des_square > 0 && des_square != ChessGameData.EMPTY && position.human_pieces[des_square].value == Piece.KNIGHT){
					checked = true;
					break;
				}
			}
		}
		return checked;
	}
	//kiểm tra xem vua có bị quân tượng ăn không
	public boolean checkedByBishop(Piece king) {
		boolean checked = false;
		int[] deltas = {11,-11,9,-9};
		for(int i = 0; i < deltas.length; i++){
			int delta = king.location + deltas[i];
			while(true){
				int des_square = position.board[delta];
				if(des_square == ChessGameData.ILLEGAL) {
					checked = false;
					break;
				}
				if(king == humanKing){
					if(des_square < 0 && position.computer_pieces[-des_square].value == Piece.BISHOP){
						checked = true;
						break;
					} else if(des_square != ChessGameData.EMPTY) break;
				} else if(king == computerKing) {
					if(des_square > 0 && des_square != ChessGameData.EMPTY && position.human_pieces[des_square].value == Piece.BISHOP){
						checked = true;
						break;
					} else if(des_square != ChessGameData.EMPTY) break;
				}
				delta += deltas[i];
			}
			if(checked) break;
		}
		return checked;
	}
	//kiểm tra xem vua có bị xe ăn không
	public boolean checkedByRook(Piece king) {
		boolean checked = false;
		int[] deltas = {1,-1,10,-10};
		for(int i = 0; i < deltas.length; i++){
			int delta = king.location + deltas[i];
			while(true){
				int des_square = position.board[delta];
				if(des_square == ChessGameData.ILLEGAL){
					checked = false;
					break;
				}
				if(king == humanKing){
					if(des_square < 0 && position.computer_pieces[-des_square].value == Piece.ROOK){
						checked =true;
						break;
					} else if(des_square != ChessGameData.EMPTY) break;
				} else if(king == computerKing){
					if(des_square > 0 && des_square != ChessGameData.EMPTY && position.human_pieces[des_square].value == Piece.ROOK){
						checked = true;
						break;
					} else if(des_square != ChessGameData.EMPTY) break;
				}
				delta += deltas[i];
			}
			if(checked) break;
		}
		return checked;
	}
	//kiểm tra xem vua có bị hậu ăn không
	public boolean checkedByQueen(Piece king) {
		boolean checked = false;
		int[] deltas = {1,-1,10,-10,9,-9,11,-11};
		for(int i = 0; i < deltas.length; i++){
			int delta = king.location + deltas[i];
			while(true){
				int des_square = position.board[delta];
				if(des_square == ChessGameData.ILLEGAL){
					checked = false;
					break;
				}
				if(king == humanKing){
					if(des_square < 0 && position.computer_pieces[-des_square].value == Piece.QUEEN){
						checked = true;
						break;
					} else if(des_square != ChessGameData.EMPTY) break;
				} else if(king == computerKing){
					if(des_square>0 && des_square != ChessGameData.EMPTY &&
                            position.human_pieces[des_square].value == Piece.QUEEN){
						checked = true;
						break;
					} else if(des_square != ChessGameData.EMPTY) break;
				}
				delta += deltas[i];
			}
			if(checked) break;
		}
		return checked;
	}
	//kiểm tra xem vua có bị tướng ăn hay không
	public boolean checkedByKing(Piece king) {
		boolean checked = false;
		int location = king.location;
		int[] destinations = {location+1,location-1,location+10,location-10,location+11,location-11,location+9,location-9};
		for(int destination : destinations){
			int des_square = position.board[destination];
			if(des_square == ChessGameData.ILLEGAL) continue;
			if(king == humanKing){
				if(des_square < 0 && position.computer_pieces[-des_square].value == Piece.KING){
					checked = true;
					break;
				}
			} else {
				if(des_square > 0 && des_square != ChessGameData.EMPTY && position.human_pieces[des_square].value == Piece.KING){
					checked = true;
					break;
				}
			}
		}
		return checked;
	}
	
}
