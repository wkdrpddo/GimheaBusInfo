package TeamProject;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

import javax.swing.JFrame;

//�ҷ��� HTML������ �Ľ��� ���� �ܺ� ���̺귯�� ȣ�� - jsoup
import org.jsoup.*;
import org.jsoup.select.*;

/*
 * ���ؽ��� ���������� �˻� �� �����庰 ������������ �˻��� ���� Ŭ����
 * getStationInfo�� ������ ������ ��������
 * searchGimhaeBusByName���� ������ �̸����� �˻��Ͽ� ������������ �����Ѵ�.
 * 20153235 �ڿ���
 */

public class gimhaeBusPageParse {
   private static URL url;
   private static BufferedReader br;
   private static URLConnection conn;
   // �߰�1 ) 2�� .java���Ϸ� �����Ͽ� �����ϱ� ���� Vector ����
   public Vector<String> publicV = new Vector<String>();
   public Vector<String> busV = new Vector<String>();
   
   //���� ���͸� �̿��Ͽ� ��ȯ�ϰ� ��ȯ�ޱ�
   //���� ��������� �Է�. �� �κ��� JTextField�� �Ἥ ��ġ ����   
   public Vector<Vector<String>> getStationInfo(String stationName) {
      StringBuffer sb = new StringBuffer();
      Vector<Vector<String>> v = null;
      
      //����� ���� �̿�. https �ϸ� �ȵ� ���� https ���� ����.
      String Address = "http://bus.gimhae.go.kr/mobile/bus_realtime/bus_arrive2.php?hdMode=NAME_B&txtKeyword=";
      try {
         //EUC-KR ���ڴ� (���ڴ� �ϸ� �ȉ�)
         Address += URLEncoder.encode(stationName, "EUC-KR");
         
         url = new URL(Address);
         conn = (URLConnection) url.openConnection();
         //���� ���� �ϵ� ���� conn
         br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         
         String line;
         while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
            //append ���̱�.���ο� \n �ְ� �;
         }
         
      } catch (Exception e) { }
      
      return searchGimhaeBusByName(sb.toString());
   }
   
   private Vector<Vector<String>> searchGimhaeBusByName(String html) {
      Vector<Vector<String>> v1 = new Vector<Vector<String>>();
      br = new BufferedReader(new StringReader(html)); // ���ڿ� �� �� ������ �о��.
      try {
         String line;
         while ((line = br.readLine()) != null) {
            if (line.contains("ic2 pd3")) {
               Vector<String> vRow = new Vector<String>();
               
               //title �κи� ��������, �ڵ� �κ� �������ºκ�
               vRow.add(line.substring(line.indexOf("title=") + 7, line.indexOf("\"><a href")));
               vRow.add(line.substring(line.indexOf("\"f_org1\">[") + 10, line.indexOf("]</span>")));
               v1.add(vRow);
            }
         }

      } catch (Exception e) { System.out.println("���� �߻�1");}
      
      // ����1 ) ���ʿ��� �κ� ����, gimhaeBusProgram���� JList�� Vector�� ����Ͽ��⿡ �̰������� ���� ���������� Vector�� �޾Ƴ־���.
      for(int i = 0; i < v1.size(); i++) {
          String s = "";
    	  for(int j = 0; j < v1.elementAt(i).size(); j++) {
    		  s += v1.elementAt(i).elementAt(j);
    	  }
    	  publicV.add(s);
      }
      return v1;
   }
   
   //�Ʊ� ������� �˻��ϸ� ���� �ڵ�� ���ڳ� �װ� �Է½� �� �ڵ� ���� ���� �ð� �˷���
   public Vector<Vector<String>> getBusInfo(String stationId) {
      StringBuffer sb = new StringBuffer();
      Vector<Vector<String>> v2 = null;
      String Address = "http://bus.gimhae.go.kr/mobile/bus_realtime/bus_arrive3.php?bID=";
      //Address += stationId.substring(3);
      
      //������ �˻� �κ� - 
      if (stationId.length() == 3)
         Address += stationId;
      else
         Address += (Integer.parseInt(stationId.substring(0, 1)) - 1) + stationId.substring(1);

      try {
         url = new URL(Address);
         conn = (URLConnection) url.openConnection();
         
         br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         
         String line;
         while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
         }
         
         v2 = solveGimhaeBus(sb.toString());
      } catch (Exception e) { }
      
      // ����2 ) ���� 1�� ������ ����
      for(int i = 0; i < v2.size(); i++) {
    	  String s = "";
    	  for(int j = 0; j < v2.elementAt(i).size(); j++) {
    		  s += v2.elementAt(i).elementAt(j);
    	  }
    	  busV.add(s);
      }
      return v2;
   }
   
   private Vector<Vector<String>> solveGimhaeBus(String html) {
      Vector<Vector<String>> v = new Vector<Vector<String>>();
      org.jsoup.nodes.Document doc = Jsoup.parse(html);
      Elements rows = doc.select("div#m_table1 table:last-child tbody tr td");

      try {
         int idx = 0;
         Vector<String> vRow = new Vector<String>();
         for (org.jsoup.nodes.Element row : rows) { // for (int i = 0; i < rows.size(); i++) { }�� ����� �ǹ̸� ������.
            idx++;
            if (idx == 3) {
               idx = 0;
               vRow.add(row.text().replace("������ġ : ", ""));
               v.add(vRow);
               vRow = new Vector<String>();
            }
            else { vRow.add(row.text()); }
         }
      } catch (Exception e) { }
      //jlabel3.setText("jlabel3solveGimhaeBus : "+ v + "\n");
      return v;
   }
   
   //v2,v�� ���� ��� �����ϱ� �ϳ��� Ȱ���ؼ� �ص� �� �� ����
}