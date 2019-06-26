package ChessProject.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChessPreferencesPanel extends JFrame implements ActionListener {
	/**
	 * 
	 */
	ChessProject chessproject;
	JRadioButton btnEasy;
	JRadioButton btnNormal;
	JRadioButton btnHard;
	JRadioButton btnWhite;
	JRadioButton btnBlack;
	JButton btnOk;
	JButton btnCancel;
	public final static int WHITE = 0, BLACK = 1;
	
	public ChessPreferencesPanel(ChessProject chessproject){
        super("Options");
        this.chessproject = chessproject;
        JPanel mainPane = new JPanel(new BorderLayout());
        mainPane.add(createDifficultyPanel(),BorderLayout.NORTH);
        mainPane.add(createColorPane(),BorderLayout.CENTER);
        mainPane.add(createButtonPane(),BorderLayout.SOUTH);
        mainPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setContentPane(mainPane);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        btnOk.addActionListener(this);
        btnCancel.addActionListener(this);
    }
	
	public JPanel createDifficultyPanel(){
		JPanel levelPanel = new JPanel();
		btnEasy = new JRadioButton("Easy");
		btnNormal = new JRadioButton("Normal", true);
		btnHard = new JRadioButton("Hard");
		ButtonGroup group = new ButtonGroup();
		group.add(btnEasy);
		group.add(btnNormal);
		group.add(btnHard);

		levelPanel.add(btnEasy);
		levelPanel.add(btnNormal);
		levelPanel.add(btnHard);
		levelPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5,5,5,5),
                BorderFactory.createTitledBorder("Difficulty")));
		return levelPanel;
	}
	
	public JPanel createColorPane(){
        JPanel colorPane = new JPanel(new GridLayout(1,2));
        btnWhite = new JRadioButton("White",true);
        btnBlack = new JRadioButton("Black");
        ButtonGroup group = new ButtonGroup();
        group.add(btnWhite);
        group.add(btnBlack);
        colorPane.add(btnWhite);
        colorPane.add(btnBlack);
        colorPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5,5,5,5),
                BorderFactory.createTitledBorder("Color")));
        return colorPane;
    }
    public JPanel createButtonPane(){
        JPanel buttonPane = new JPanel(new BorderLayout());
        JPanel pane = new JPanel(new GridLayout(1,2,5,0));
        pane.add(btnOk = new JButton("OK"));
        pane.add(btnCancel = new JButton("Cancel"));
        buttonPane.add(pane,BorderLayout.EAST);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        return buttonPane;
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnOk){
			chessproject.state = ChessGameData.GAME_ENDED;
			chessproject.newGame();
		}
		setVisible(false);
	}
	
}
