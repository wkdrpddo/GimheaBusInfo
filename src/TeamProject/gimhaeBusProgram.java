package TeamProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.util.*;

public class gimhaeBusProgram extends JFrame {
	gimhaeBusPageParse parse = new gimhaeBusPageParse();
	//배경에 이미지를 받아온 JPanel을 상속한 bgPanel
	bgPanel panel = new bgPanel();
	//각각 기능을 해줄 Panel들을 따로 사용, setVisible을 통해 출력 여부 정함
	JPanel mainPage = new JPanel();
	JPanel serchPage = new JPanel();
	JPanel busStop = new JPanel();
	
	JLabel pageName = new JLabel("김해시 버스조회 시스템");
	JLabel stopInfoText = new JLabel();
	
	JTextField typeIn = new JTextField();
	//정거장 이름 검색 시의 List
	Vector<String> busStopInfo = new Vector<String>();
	JList<String> serchList = new JList<String>(busStopInfo);
	//각 정거장의 버스노선 정보를 보여 줄 List
	Vector<String> busInfo = new Vector<String>();
	JList<String> listBus = new JList<String>(busInfo);
	
	gimhaeBusProgram() {
		setTitle("김해시 버스 조회 시스템");
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(panel);
		setLayout(null);
		setResizable(false);
		Container c = getContentPane();
		
		pageName.setSize(300, 40);
		pageName.setFont(new Font("", Font.PLAIN, 25));
		Border bder1 = new EtchedBorder(EtchedBorder.RAISED);
		
		//mainPage
		mainPage.setSize(400, 250);
		mainPage.setBackground(Color.white);
		mainPage.setLayout(null);
		c.add(mainPage).setLocation(50, 50);
		
		JLabel WaytoSerch[] = new JLabel[2];
		String sName[] = {"정류장명으로 조회", "버스 번호로 조회"};
		clickWay cw = new clickWay();
		for(int i = 0; i < 2; i++) {
			WaytoSerch[i] = new JLabel(sName[i]);
			WaytoSerch[i].setSize(180, 80);
			WaytoSerch[i].setFont(new Font("", Font.BOLD, 20));
			WaytoSerch[i].setHorizontalAlignment(JLabel.CENTER);
			WaytoSerch[i].setBorder(bder1);
			WaytoSerch[i].addMouseListener(cw);
			mainPage.add(WaytoSerch[i]).setLocation(i*180+10*(i+1), 10);
		}
		
		//serchPage
		serchPage.setSize(400, 250);
		serchPage.setBackground(Color.white);
		serchPage.setLayout(null);
		c.add(serchPage).setLocation(50, 50);
		
		ImageIcon homeIcon = new ImageIcon("C:\\Users\\SAMSUNG\\Desktop\\JAVA Team Project\\Home.png");
		Image homeImg = homeIcon.getImage();
		homeImg = homeImg.getScaledInstance(30,  30, Image.SCALE_SMOOTH);
		homeIcon.setImage(homeImg);
		
		JButton goHome = new JButton(homeIcon);
		goHome.setSize(30, 30);
		goHome.addActionListener(new actionHome());
		serchPage.add(goHome).setLocation(330, 10);
		
		actionSerch ac = new actionSerch();
		JButton Serch = new JButton("검색");
		Serch.setSize(100, 30);
		Serch.addActionListener(ac);
		serchPage.add(Serch).setLocation(220, 10);
		typeIn.setSize(200, 30);
		typeIn.addActionListener(ac);
		typeIn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				typeIn.setText("");
			}
		});
		serchPage.add(typeIn).setLocation(10, 10);
		serchList.addListSelectionListener(new selectList());
		JScrollPane scrollList = new JScrollPane(serchList);
		scrollList.setSize(380, 180);
		serchPage.add(scrollList).setLocation(10, 50);
		
		//BusStop
		busStop.setSize(400, 250);
		busStop.setBackground(Color.white);
		busStop.setLayout(null);
		c.add(busStop).setLocation(50, 50);
		
		ImageIcon backIcon = new ImageIcon("C:\\Users\\SAMSUNG\\Desktop\\JAVA Team Project\\Back.jpg");
		Image backImg = backIcon.getImage();
		backImg = backImg.getScaledInstance(30,  30, Image.SCALE_SMOOTH);
		backIcon.setImage(backImg);
		
		JButton goBack = new JButton(backIcon);
		goBack.setSize(30, 30);
		goBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pageName.setText("정류장명 조회");
				busStop.setVisible(false);
				serchPage.setVisible(true);
				busInfo.clear();
			}
		});
		busStop.add(goBack).setLocation(10, 10);
		JScrollPane scrollListTwo = new JScrollPane(listBus);
		scrollListTwo.setSize(380, 180);
		busStop.add(scrollListTwo).setLocation(10, 50);
		stopInfoText.setSize(350, 30);
		stopInfoText.setFont(new Font("", Font.PLAIN, 20));
		busStop.add(stopInfoText).setLocation(50, 5);
		
		
		c.add(pageName).setLocation(10, 10);
		setVisible(true);
		serchPage.setVisible(false);
		busStop.setVisible(false);
	}
	
	class selectList implements ListSelectionListener {
		int bool = 0;
		public void valueChanged(ListSelectionEvent e) {
			JList l = (JList)e.getSource();
			int click[] = {0, 0};
			int count[] = new int[busStopInfo.size()];
			
			try {
				String s = (String) l.getSelectedValue();
				char[] c = s.toCharArray();
				String name = "";
				s = "";
				for(int i = 0; i < c.length; i++) {
					if(i > c.length-5)
						s += c[i];
					else
						name += c[i];
				}
				stopInfoText.setText(name);
				parse.getBusInfo(s);
				busInfo = parse.busV;
				listBus.setListData(busInfo);
				busStop.setVisible(true);
				serchPage.setVisible(false);
			} catch (NullPointerException n) {
			} catch (NumberFormatException n) {
			}
		}
	}
	
	class actionSerch implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String s = typeIn.getText();
			busStopInfo.clear();
			if(pageName.getText().equals("정류장명 조회")) {
				parse.getStationInfo(s);
				busStopInfo = parse.publicV;
				if(busStopInfo.size() == 0)
					busStopInfo.add("검색 결과가 없습니다.");
				serchList.setListData(busStopInfo);
			}
		}	
	}
	
	class actionHome implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			pageName.setText("김해시 버스조회 시스템");
			mainPage.setVisible(true);
			serchPage.setVisible(false);
			busStopInfo.clear();
		}
	}
	
	class clickWay extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			JLabel j = (JLabel)e.getSource();
			String s = j.getText();
			if(s.equals("정류장명으로 조회")) {
				pageName.setText("정류장명 조회");
				typeIn.setText("정류장 명을 입력하세요");
			}
			else if(s.contentEquals("버스 번호로 조회")) {
				pageName.setText("버스번호 조회");
				typeIn.setText("버스 번호를 입력하세요");
			}
			mainPage.setVisible(false);
			serchPage.setVisible(true);
		}
	}
	
	class bgPanel extends JPanel {
		ImageIcon icon = new ImageIcon("C:\\Users\\SAMSUNG\\Desktop\\JAVA Team Project\\bgImage.jpg");
		Image img = icon.getImage();
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		}
	}
	
	public static void main(String[] args) {
		new gimhaeBusProgram();
	}
}
