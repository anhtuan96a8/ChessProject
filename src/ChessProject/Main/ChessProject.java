package ChessProject.Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;

public class ChessProject extends JFrame {
	/**
	 * 
	 */
	Resource resource = new Resource();
	boolean is_white, piece_selected, castling;
	JPanel main_panel = new JPanel(new BorderLayout());
	ChessPreferencesPanel play_options;
	ChessBoardPanel board_panel;
	Move move = new Move();
	ChessPosition position;
	int state;
	ChessGame game;
	ChessMoveSearcher move_searcher;
	Color bg_color = Color.LIGHT_GRAY;
	Map<Integer, Image> images = new HashMap<Integer, Image>(); //đưa ảnh vào images,đi liền với nó là 1 key.khi images.key thì sẽ đưa ra hình ảnh
	
	public ChessProject(){
		this.setJMenuBar(createMenuBar());  //tạo menu bar
		setContentPane(main_panel);    //tạo bàn cờ(bàn cờ trống)
		position = new ChessPosition();
		board_panel = new ChessBoardPanel();
		loadBoardImage();
		
		main_panel.add(board_panel, BorderLayout.CENTER);
		main_panel.setBackground(bg_color);
		pack();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
                quit();
            }
		});
	}
	//ván mới
	public void newGame() {
		is_white = play_options.btnWhite.isSelected();	//Kiểm tra xem người chơi chọn quân trắng hay đen
		move.source_location = -1;
		move.des_location = -1;
		position = new ChessPosition();//khởi tạo bàn cờ mới
		position.init(is_white); //tạo mảng các quân cờ
		game = new ChessGame(position);
		loadPieceImages();
		board_panel.repaint();
		if(is_white) state = ChessGameData.HUMAN_MOVE;
		else state = ChessGameData.COMPUTER_MOVE;
		castling = false;
		play();
	}
	
	public void play() {
		Thread t = new Thread(){
			@Override
			public void run() {
				while(true){
					switch(state){
					case ChessGameData.HUMAN_MOVE:
						break;
					case ChessGameData.COMPUTER_MOVE:
						if(gameEnded(ChessGameData.COMPUTER)){
							state = ChessGameData.GAME_ENDED;
							break;
						}
						int depth = 0;
						if(play_options.btnEasy.isSelected()) depth = 4;
						if(play_options.btnNormal.isSelected()) depth = 5;
						if(play_options.btnHard.isSelected()) depth = 6   ;
						move = move_searcher.alphaBeta(ChessGameData.COMPUTER, position, Integer.MIN_VALUE, Integer.MAX_VALUE, depth).last_move;
						state = ChessGameData.PREPARE_ANIMATION;
						break;
					case ChessGameData.PREPARE_ANIMATION:
						prepareAnimated();
						break;
					case ChessGameData.ANIMATING:
						animate();
						break;
					case ChessGameData.GAME_ENDED:
						return;
					}
					try{                        
                        Thread.sleep(3);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
				}
			}
		};
		t.start();
	}
	//kiểm tra xem trận đấu đã kết thúc chưa
	public boolean gameEnded(int player){
		boolean end_game = false;
		int result = game.getResult(player);//lấy kết quả đối với lượt chơi của player
		String color = "";
		if(player == ChessGameData.COMPUTER){
			color = (is_white)?"White":"Black";//nếu lượt đi tiếp theo là của máy thì color= màu của người chơi
		} else {
			color = (is_white)?"Black":"White";//nếu lượt đi tiếp theo là của người chơi thì color= màu của máy
		}
		if(result == ChessGameData.CHECKMATE){
			showEndGameResult(color+" wins by CHECKMATE");//hiển thi ra kết quả người thắng cuộc
			end_game = true;
		} else if(result == ChessGameData.DRAW){
			showEndGameResult("DRAW");
            end_game = true;
		}
		return end_game;
	}
	// hiển thị thông báo khi trận đấu kết thúc
	public void showEndGameResult(String message){
		int option = JOptionPane.showOptionDialog(null,
                message,"Game Over",0,JOptionPane.PLAIN_MESSAGE,
                null,new Object[]{"Play again","Cancel"},"Play again");
        if(option == 0){
            play_options.setVisible(true);
        }
	}
	
	public void showNewGameWarning() {
		 JOptionPane.showMessageDialog(null,
	                "Start a new game after I made my move.\n",
	                "Message",JOptionPane.PLAIN_MESSAGE);
	}
	
	public int boardValue(int value){
		return value/45;
	}
	
	public void prepareAnimated() {
		int animating_image_key = 0;
        if(position.board[move.source_location]>0){
            animating_image_key = position.human_pieces[position.board[move.source_location]].value;
        }else {
            animating_image_key = -position.computer_pieces[-position.board[move.source_location]].value;
        }        
        board_panel.animating_image = images.get(animating_image_key);
        int x = move.source_location%10;        
        int y = (move.source_location-x)/10;
        board_panel.desX = move.des_location%10;
        board_panel.desY = (move.des_location-board_panel.desX)/10;
        int dX = board_panel.desX-x;
        int dY = board_panel.desY-y;           
        board_panel.movingX = x*45;
        board_panel.movingY = y*45-45;
        if(Math.abs(dX)>Math.abs(dY)){
            if(dY == 0){
                board_panel.deltaX = (dX>0)?1:-1;
                board_panel.deltaY = 0;
            }else{
                board_panel.deltaX = (dX>0)?Math.abs(dX/dY):-(Math.abs(dX/dY));
                board_panel.deltaY = (dY>0)?1:-1;
            }
        }else{
            if(dX == 0){
                board_panel.deltaY = (dY>0)?1:-1;
                board_panel.deltaX = 0;
            }else{
                board_panel.deltaX = (dX>0)?1:-1;
                board_panel.deltaY = (dY>0)?Math.abs(dY/dX):-(Math.abs(dY/dX));
            }
        }          
        state = ChessGameData.ANIMATING;
	}
	
	public void animate() {
		if(board_panel.movingX == board_panel.desX*45 && board_panel.movingY == board_panel.desY*45-45){
			board_panel.repaint();
			int source_square = position.board[move.source_location];
			if(source_square>0){
				state = ChessGameData.COMPUTER_MOVE;
			} else {
				if(move.des_location > 90 && move.des_location < 98 
						&& position.computer_pieces[-source_square].value == Piece.PAWN)
					promoteComputerPawn();
				state = ChessGameData.HUMAN_MOVE;
			}
			position.update(move);
			
			if(source_square>0){
				if(castling){
					prepareCastlingAnimation();
					state = ChessGameData.PREPARE_ANIMATION;
				} else if(move.des_location > 20 && move.des_location < 29
						&& position.human_pieces[source_square].value == Piece.PAWN){
					promoteHumanPawn();
				}
			} else {
				if(gameEnded(ChessGameData.HUMAN)){
					state = ChessGameData.GAME_ENDED;
					return;
				}
			}
			
			if(castling) castling = false;
		}
		board_panel.movingX += board_panel.deltaX;
		board_panel.movingY += board_panel.deltaY;
		board_panel.repaint();
	}
	// người chơi phong hậu
	public void promoteHumanPawn() {

		String strNewPiece;
		int piece_index = position.board[move.des_location];
		String[] strPieces = {"ROOK", "KNIGHT", "BISHOP", "QUEEN"};
		JOptionPane digBox = new JOptionPane("Choose the piece to change your pawn into", JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, strPieces, "XE");
		JDialog dig = digBox.createDialog(null, "pawn at end of board");
		dig.setVisible(true);
		strNewPiece = digBox.getValue().toString();
		if(strNewPiece == "ROOK")  position.human_pieces[piece_index] = new Piece(Piece.ROOK ,move.des_location);
		if(strNewPiece == "KNIGHT")  position.human_pieces[piece_index] = new Piece(Piece.KNIGHT ,move.des_location);
		if(strNewPiece == "BISHOP")  position.human_pieces[piece_index] = new Piece(Piece.BISHOP ,move.des_location);
		if(strNewPiece == "QUEEN")  position.human_pieces[piece_index] = new Piece(Piece.QUEEN ,move.des_location);

	}
	//máy phong hậu
	public void promoteComputerPawn() {
		int piece_index = position.board[move.source_location];
		position.computer_pieces[-piece_index] = new Piece(Piece.QUEEN, move.des_location);
	}
	//nhập thành
	public void prepareCastlingAnimation() {
		if(move.des_location == 97 || move.des_location == 96){
            move.source_location = 98;
            move.des_location -= 1;
        }else if(move.des_location == 92 || move.des_location == 93){
            move.source_location = 91;
            move.des_location += 1;
        }
	}
	//kiểm tra nước đi xem có an toàn(tướng không bị ăn) và nước đi đó có chính xác hay không hay không
	public boolean validMove(int destination) {
		int source = move.source_location;
        int destination_square = position.board[destination];
        if(destination_square == ChessGameData.ILLEGAL) return false;
        if(!game.safeMove(ChessGameData.HUMAN,source,destination)) return false;
        boolean valid = false;
        int piece_value = position.human_pieces[position.board[source]].value;                        
        switch(piece_value){
            case Piece.PAWN:
                if(destination == source-10 && destination_square == ChessGameData.EMPTY) valid = true;
                if(destination == source-20 && position.board[source-10] == ChessGameData.EMPTY &&
                        destination_square == ChessGameData.EMPTY && source>80) valid = true;
                if(destination == source-9 && destination_square<0) valid = true;
                if(destination == source-11 && destination_square<0) valid = true;
                break;
            case Piece.KNIGHT:
            case Piece.KING:
                if(piece_value == Piece.KING) valid = checkCastling(destination);
                int[] destinations = null;
                if(piece_value == Piece.KNIGHT) destinations = new int[]{source-21,source+21,source+19,source-19,                    
                    source-12,source+12,source-8,source+8};
                else destinations = new int[]{source+1,source-1,source+10,source-10,
                    source+11,source-11,source+9,source-9};
                for(int i=0; i<destinations.length; i++){
                    if(destinations[i] == destination){
                        if(destination_square == ChessGameData.EMPTY || destination_square<0){
                            valid = true;
                            break;
                        }
                    }
                }                
                break;
            case Piece.BISHOP:
            case Piece.ROOK:
            case Piece.QUEEN:
                int[] deltas = null;
                if(piece_value == Piece.BISHOP) deltas = new int[]{11,-11,9,-9};
                if(piece_value == Piece.ROOK) deltas = new int[]{1,-1,10,-10};
                if(piece_value == Piece.QUEEN) deltas = new int[]{1,-1,10,-10,11,-11,9,-9};
                for (int i = 0; i < deltas.length; i++) {
                    int des = source + deltas[i]; 
                    valid = true;
                    while (destination != des) { 
                        destination_square = position.board[des];  
                        if(destination_square != ChessGameData.EMPTY){
                            valid = false;
                            break;
                        }                        
                        des += deltas[i];
                    }
                    if(valid) break;
                }
                break;
        }        
        return valid;
	}
	
	public boolean checkCastling(int destination) {
		Piece king = position.human_pieces[8];
        Piece right_rook = position.human_pieces[6];
        Piece left_rook = position.human_pieces[5];
        
        if(king.has_moved) return false;              
        int source = move.source_location;
        
        if(right_rook == null && left_rook == null) return false;
        if(right_rook != null && right_rook.has_moved && 
                left_rook != null && left_rook.has_moved) return false;
            
        if(is_white){            
            if(source != 95) return false;            
            if(destination != 97 && destination != 93) return false;
            if(destination == 97){
                if(position.board[96] != ChessGameData.EMPTY) return false;
                if(position.board[97] != ChessGameData.EMPTY) return false;
                if(!game.safeMove(ChessGameData.HUMAN,source,96)) return false;
                if(!game.safeMove(ChessGameData.HUMAN,source,97)) return false;
            }else if(destination == 93){
                if(position.board[94] != ChessGameData.EMPTY) return false;
                if(position.board[93] != ChessGameData.EMPTY) return false;
                if(!game.safeMove(ChessGameData.HUMAN,source,94)) return false;
                if(!game.safeMove(ChessGameData.HUMAN,source,93)) return false;
            }
        }else{
            if(source != 94) return false;            
            if(destination != 92 && destination != 96) return false;
            if(destination == 92){
                if(position.board[93] != ChessGameData.EMPTY) return false;
                if(position.board[92] != ChessGameData.EMPTY) return false;
                if(!game.safeMove(ChessGameData.HUMAN,source,93)) return false;
                if(!game.safeMove(ChessGameData.HUMAN,source,92)) return false;
            }else if(destination == 96){
                if(position.board[95] != ChessGameData.EMPTY) return false;
                if(position.board[96] != ChessGameData.EMPTY) return false;
                if(!game.safeMove(ChessGameData.HUMAN,source,95)) return false;
                if(!game.safeMove(ChessGameData.HUMAN,source,96)) return false;
            }
        }        
        return castling=true;
	}
	//vẽ bàn cờ
	public class ChessBoardPanel extends JPanel implements MouseListener {

		/**
		 * 
		 */
		//private static final long serialVersionUID = 1L;
		Image animating_image;
		int movingX, movingY, desX, desY, deltaX, deltaY;
		
		public ChessBoardPanel() {
			setPreferredSize(new Dimension(450, 495));
			setBackground(bg_color);
			addMouseListener(this);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			if(position.board == null) return;
			super.paintComponent(g);
			g.drawImage(images.get(ChessGameData.BOARD_IMAGE), 45, 45, this);
			
			for (int i = 0; i < position.board.length-11; i++) {
                if (position.board[i] == ChessGameData.ILLEGAL) continue;                                                                
                int x = i%10;
                int y = (i-x)/10;
                //nêu quân cờ được chọn thì ô bàn cờ sẽ hiển thị màu khác
                if (piece_selected && i == move.source_location) {                
                    g.drawImage(images.get(ChessGameData.GLOW), x * 45, y * 45 - 45,this);
                }else if(!piece_selected && move.des_location == i &&
                        (position.board[i]==ChessGameData.EMPTY || position.board[i]<0)){
                    g.drawImage(images.get(ChessGameData.GLOW2), x * 45, y * 45 - 45, this);
                }
                
                if (position.board[i] == ChessGameData.EMPTY) continue;
                
                if(state == ChessGameData.ANIMATING && i==move.source_location) continue;
				//vẽ quân cờ người chơi và máy lên bàn cờ
                if (position.board[i] > 0) {          
                    int piece = position.human_pieces[position.board[i]].value;
                    g.drawImage(images.get(piece),x*45, y*45 - 45 ,this);
                }else{
                    int piece = position.computer_pieces[-position.board[i]].value;
                    g.drawImage(images.get(-piece),x*45, y*45 - 45,this);
                }               
            }
			//chưa hiểu
            if(state == ChessGameData.ANIMATING){
                g.drawImage(animating_image,movingX,movingY,this);
            }
		}
			//sự kiện click chuột
		@Override
		public void mouseClicked(MouseEvent e) {
			if(state != ChessGameData.HUMAN_MOVE) return;
            int location = boardValue(e.getY()+45)*10+boardValue(e.getX());              
            if(position.board[location] == ChessGameData.ILLEGAL) return;
            if((!piece_selected || position.board[location]>0) && position.board[location] != ChessGameData.EMPTY){
                if(position.board[location]>0){
                    piece_selected = true;
                    move.source_location = location;
                }
            }else if(piece_selected && validMove(location)){
                piece_selected = false;
                move.des_location = location;     
                state = ChessGameData.PREPARE_ANIMATION;
            }
            repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
		
	}
	//rời khỏi cuộc chơi
	public void quit(){
		int option = JOptionPane.showConfirmDialog(null, "Are you sure want to quit?", "ChessProject", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(option == JOptionPane.YES_OPTION) System.exit(0);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	// lấy hình ảnh các quân cờ cho vào images
	public void loadPieceImages(){
		char[] resource_key = {'p','n','b','r','q','k'};
		int[] images_keys = {Piece.PAWN, Piece.KNIGHT, Piece.BISHOP, Piece.ROOK, Piece.QUEEN, Piece.KING};
		try{
			for(int i = 0; i<resource_key.length; i++){
				images.put(images_keys[i], ImageIO.read(resource.getResource((is_white?"w":"b")+resource_key[i])));
				images.put(-images_keys[i], ImageIO.read(resource.getResource((is_white?"b":"w")+resource_key[i])));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	//Hiển thị hình ảnh bàn cờ trên jframe và khi click chuột vào một ô cờ thì ô cờ đó sẽ có một màu khác
	public void loadBoardImage(){
		try{
			images.put(ChessGameData.BOARD_IMAGE,ImageIO.read(resource.getResource("chessboard")));
			images.put(ChessGameData.GLOW, ImageIO.read(resource.getResource("glow")));
			images.put(ChessGameData.GLOW2, ImageIO.read(resource.getResource("glow2")));
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void showPlayOption(){
		if(state == ChessGameData.COMPUTER_MOVE){
			showNewGameWarning();
			return;
		}
		if(play_options == null){
			play_options = new ChessPreferencesPanel(this);
			move_searcher = new ChessMoveSearcher(this);
		}
		play_options.setVisible(true);
	}
	//tạo thanh menu
	public JMenuBar createMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu menu;
		JMenuItem menuItem;
		
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("New Game");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPlayOption();
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Quit");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		menu.add(menuItem);
		
		return menuBar;
	}




	//MAIN:
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {	
			@Override
			public void run() {
				try{
					ChessProject chesspj = new ChessProject();
					chesspj.setLocationRelativeTo(null);   //Hiển thị ra giữa màn hình
					chesspj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //khi nhấn X thì tắt toàn bộ chương trình
					chesspj.setResizable(false); //Không cho thay đổi kích thước cửa sổ
					chesspj.setVisible(true);  //Có hiển thị cửa số hay không
				}catch(Exception e){
					JOptionPane.showMessageDialog(null, e.getStackTrace()); //Tao một hộp thoại và e.getStackTrace có tác dụng in lỗi lên trên hộp thoại đó
                    e.printStackTrace();
				}
			}
		});
	}
}
