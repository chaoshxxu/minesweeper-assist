package org.minesweeperassist;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * 主UI
 * @author Isun
 *
 */
public class MainPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	static MainPanel instance;
	
	Controller controller;
	GridNumberRecognizer recognizer;
	Clicker clicker;
	
	JComboBox skinComb;
	JTextField xGridsTF;
	JTextField yGridsTF;
	JLabel ltGridOriginLocationLabel;
	JLabel rbGridOriginLocationLabel;
	JButton ltCaptureBtn;
	JButton rbCaptureBtn;
	JRadioButton flagBtn;
	JRadioButton noFlagBtn;
	JTextField reactionTimeTF;
	JTextField move1GridTimeTF;
	JTextField move10GridTimeTF;
	JTextField move20GridTimeTF;
	JTextField speedRatioTF;
	JButton startBtn;
	
	
	public MainPanel() {
		this.addKeyListener(new KeyAdapter() { 
			@Override
			public void keyPressed(KeyEvent e) {
				Point location = MouseInfo.getPointerInfo().getLocation();
				if (e.getKeyCode() == KeyEvent.VK_Q) {
					MineFieldInfo.ltOriginCoord = location;
					ltGridOriginLocationLabel.setText(location.x + "," + location.y);
				} else if (e.getKeyCode() == KeyEvent.VK_M) {
					MineFieldInfo.rbOriginCoord = location;
					rbGridOriginLocationLabel.setText(location.x + "," + location.y);
				} else {
					return;
				}
				if (MineFieldInfo.ltOriginCoord != null && MineFieldInfo.rbOriginCoord != null) {
					MineFieldInfo.gridWidth = (MineFieldInfo.rbOriginCoord.x - MineFieldInfo.ltOriginCoord.x) / (MineFieldInfo.xGrids - 1);
					MineFieldInfo.gridHeight = (MineFieldInfo.rbOriginCoord.y - MineFieldInfo.ltOriginCoord.y) / (MineFieldInfo.yGrids - 1);
				}
			}
		});
		setLayout(new GridLayout(13, 2, 2, 2));
		initComponents();
	}
	
	
	private void initComponents() {
		add(new JLabel("Skin:"));
		
		skinComb = new JComboBox();
        File skinFiles[] = new File("skin").listFiles();
        for (File file : skinFiles) {
			System.out.println(file.getName());
			skinComb.addItem(file.getName());
		}
		add(skinComb);
		
		////////////////////////////
		
		add(new JLabel("X grids number:"));
		
		xGridsTF = new JTextField(5);
		xGridsTF.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (xGridsTF.getText().matches("\\d+")) {
					MineFieldInfo.xGrids = Integer.parseInt(xGridsTF.getText());
					if (MineFieldInfo.xGrids < 2) {
						MineFieldInfo.xGrids = null;
					}
				} else {
					MineFieldInfo.xGrids = null;
				}
				checkMineFieldInfo();
			}

			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
			}
		});
		add(xGridsTF);

		////////////////////////////

		add(new JLabel("Y grids number:"));

		yGridsTF = new JTextField(5);
		yGridsTF.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (yGridsTF.getText().matches("\\d+")) {
					MineFieldInfo.yGrids = Integer.parseInt(yGridsTF.getText());
					if (MineFieldInfo.yGrids < 2) {
						MineFieldInfo.yGrids = null;
					}
				} else {
					MineFieldInfo.yGrids = null;
				}
				checkMineFieldInfo();
			}

			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
			}
		});
		add(yGridsTF);
	
		////////////////////////////
		
		ltCaptureBtn = new JButton();
		ltCaptureBtn.setText("Catch left-top origin");
		ltCaptureBtn.setDisplayedMnemonicIndex(7);
		ltCaptureBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Point location = MouseInfo.getPointerInfo().getLocation();
				MineFieldInfo.ltOriginCoord = location;
				ltGridOriginLocationLabel.setText(location.x + "," + location.y);
				checkMineFieldInfo();
			}
		});
		add(ltCaptureBtn);
		
		ltGridOriginLocationLabel = new JLabel();
		add(ltGridOriginLocationLabel);
		
		////////////////////////////
		
		rbCaptureBtn = new JButton();
		rbCaptureBtn.setText("Catch right-bottom origin");
		rbCaptureBtn.setDisplayedMnemonicIndex(12);
		rbCaptureBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Point location = MouseInfo.getPointerInfo().getLocation();
				MineFieldInfo.rbOriginCoord = location;
				rbGridOriginLocationLabel.setText(location.x + "," + location.y);
				checkMineFieldInfo();
			}
		});
		add(rbCaptureBtn);
		
		rbGridOriginLocationLabel = new JLabel();
		add(rbGridOriginLocationLabel);
		
		////////////////////////////
		
		add(new JLabel("Click type:"));
		
		flagBtn = new JRadioButton("Flag", true);
		noFlagBtn = new JRadioButton("NF", false);
		ButtonGroup clickTypeButtonGroup = new ButtonGroup();
		clickTypeButtonGroup.add(flagBtn);
		clickTypeButtonGroup.add(noFlagBtn); 
		add(flagBtn);
		add(new JLabel());
		add(noFlagBtn);
		
		////////////////////////////
		
		add(new JLabel("Reaction time(ms):"));
		reactionTimeTF = new JTextField("30");
		add(reactionTimeTF);

		////////////////////////////
		
		add(new JLabel("Move 1 grids time(ms):"));
		move1GridTimeTF = new JTextField("100");
		add(move1GridTimeTF);

		////////////////////////////
		
		add(new JLabel("Move 10 grids time(ms):"));
		move10GridTimeTF = new JTextField("500");
		add(move10GridTimeTF);

		////////////////////////////
		
		add(new JLabel("Move 20 grids time(ms):"));
		move20GridTimeTF = new JTextField("600");
		add(move20GridTimeTF);

		////////////////////////////
		
		add(new JLabel("Speed change ratio:"));
		speedRatioTF = new JTextField("0.2");
		add(speedRatioTF);

		////////////////////////////
		
		add(new JLabel());
		
		startBtn = new JButton();
		startBtn.setText("Start");
		startBtn.setEnabled(false);
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					startBtn.setEnabled(false);
					startWork();
				} catch (Exception e1) {
					startBtn.setEnabled(true);
					e1.printStackTrace();
				}
			}
		});
		add(startBtn);
	}


	protected void checkMineFieldInfo() {
		if (MineFieldInfo.ltOriginCoord != null && MineFieldInfo.rbOriginCoord != null && MineFieldInfo.xGrids != null && MineFieldInfo.yGrids != null) {
			MineFieldInfo.gridWidth = (MineFieldInfo.rbOriginCoord.x - MineFieldInfo.ltOriginCoord.x + (MineFieldInfo.xGrids - 1) / 2) / (MineFieldInfo.xGrids - 1);
			MineFieldInfo.gridHeight = (MineFieldInfo.rbOriginCoord.y - MineFieldInfo.ltOriginCoord.y + (MineFieldInfo.yGrids - 1) / 2) / (MineFieldInfo.yGrids - 1);
			MainPanel.instance.startBtn.setEnabled(true);
			System.out.println("GridWidth: " + MineFieldInfo.gridWidth);
			System.out.println("GridHeight: " + MineFieldInfo.gridHeight);
		} else {
			MineFieldInfo.gridHeight = null;
			MineFieldInfo.gridWidth = null;
			MainPanel.instance.startBtn.setEnabled(false);
		}
	}


	public void startWork() throws Exception {
		File skinFile = new File("skin/" + skinComb.getSelectedItem());
		MineFieldInfo.xGrids = Integer.parseInt(xGridsTF.getText());
		MineFieldInfo.yGrids = Integer.parseInt(yGridsTF.getText());
		
		controller = new Controller();
		recognizer = new GridNumberRecognizer(skinFile);
		clicker = new Clicker(controller, recognizer);
		
		clicker.start();
	}
	
	public static void main (String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (Exception e) {
					System.out.println("坑爹了, something is wrong!");
					e.printStackTrace();
				}
			}
		});
	}

	protected static void createAndShowGUI() {
		JFrame frame = new JFrame("Minesweeper Assistant");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		instance = new MainPanel();
		instance.setOpaque(true);
		frame.setContentPane(instance);

		frame.setAlwaysOnTop(true); // 设置窗口置顶

		ImageIcon icon1 = new ImageIcon("image/finish.png");
		frame.setIconImage(icon1.getImage()); // 设置标题栏图标

		frame.setPreferredSize(new Dimension(400, 350));
		frame.pack(); // 不加只用setSize即可 加上需先设定setPreferredSize
		frame.setVisible(true);

	}

}
