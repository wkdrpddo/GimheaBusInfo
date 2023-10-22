package TeamProject;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

import javax.swing.JFrame;

//불러온 HTML페이지 파싱을 위한 외부 라이브러리 호출 - jsoup
import org.jsoup.*;
import org.jsoup.select.*;

/*
 * 김해시의 버스정류장 검색 및 정류장별 버스도착정보 검색을 위한 클래스
 * getStationInfo로 정류장 정보를 가져오고
 * searchGimhaeBusByName으로 정류장 이름으로 검색하여 벡터형식으로 리턴한다.
 * 20153235 박원종
 */

public class gimhaeBusPageParse {
   private static URL url;
   private static BufferedReader br;
   private static URLConnection conn;
   // 추가1 ) 2개 .java파일로 분할하여 연동하기 위한 Vector 변수
   public Vector<String> publicV = new Vector<String>();
   public Vector<String> busV = new Vector<String>();
   
   //이중 벡터를 이용하여 반환하고 반환받기
   //버스 정류장명을 입력. 이 부분은 JTextField를 써서 배치 하자   
   public Vector<Vector<String>> getStationInfo(String stationName) {
      StringBuffer sb = new StringBuffer();
      Vector<Vector<String>> v = null;
      
      //모바일 김해 이용. https 하면 안돼 여기 https 인증 끝남.
      String Address = "http://bus.gimhae.go.kr/mobile/bus_realtime/bus_arrive2.php?hdMode=NAME_B&txtKeyword=";
      try {
         //EUC-KR 인코더 (디코더 하면 안됌)
         Address += URLEncoder.encode(stationName, "EUC-KR");
         
         url = new URL(Address);
         conn = (URLConnection) url.openConnection();
         //버퍼 리드 하디 위해 conn
         br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         
         String line;
         while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
            //append 붙이기.라인에 \n 넣고 싶어서
         }
         
      } catch (Exception e) { }
      
      return searchGimhaeBusByName(sb.toString());
   }
   
   private Vector<Vector<String>> searchGimhaeBusByName(String html) {
      Vector<Vector<String>> v1 = new Vector<Vector<String>>();
      br = new BufferedReader(new StringReader(html)); // 문자열 한 줄 단위로 읽어옴.
      try {
         String line;
         while ((line = br.readLine()) != null) {
            if (line.contains("ic2 pd3")) {
               Vector<String> vRow = new Vector<String>();
               
               //title 부분만 가져오고, 코드 부분 가져오는부분
               vRow.add(line.substring(line.indexOf("title=") + 7, line.indexOf("\"><a href")));
               vRow.add(line.substring(line.indexOf("\"f_org1\">[") + 10, line.indexOf("]</span>")));
               v1.add(vRow);
            }
         }

      } catch (Exception e) { System.out.println("에러 발생1");}
      
      // 수정1 ) 불필요한 부분 제거, gimhaeBusProgram에서 JList에 Vector를 사용하였기에 이곳에서도 각각 정보단위로 Vector에 받아넣어줌.
      for(int i = 0; i < v1.size(); i++) {
          String s = "";
    	  for(int j = 0; j < v1.elementAt(i).size(); j++) {
    		  s += v1.elementAt(i).elementAt(j);
    	  }
    	  publicV.add(s);
      }
      return v1;
   }
   
   //아까 정류장명 검색하면 버스 코드들 뜨자나 그거 입력시 그 코드 버스 도착 시간 알려줌
   public Vector<Vector<String>> getBusInfo(String stationId) {
      StringBuffer sb = new StringBuffer();
      Vector<Vector<String>> v2 = null;
      String Address = "http://bus.gimhae.go.kr/mobile/bus_realtime/bus_arrive3.php?bID=";
      //Address += stationId.substring(3);
      
      //정류장 검색 부분 - 
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
      
      // 수정2 ) 수정 1과 동일한 내용
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
         for (org.jsoup.nodes.Element row : rows) { // for (int i = 0; i < rows.size(); i++) { }와 비슷한 의미를 가진다.
            idx++;
            if (idx == 3) {
               idx = 0;
               vRow.add(row.text().replace("현재위치 : ", ""));
               v.add(vRow);
               vRow = new Vector<String>();
            }
            else { vRow.add(row.text()); }
         }
      } catch (Exception e) { }
      //jlabel3.setText("jlabel3solveGimhaeBus : "+ v + "\n");
      return v;
   }
   
   //v2,v랑 같은 출력 나오니까 하나만 활용해서 해도 될 거 같아
}