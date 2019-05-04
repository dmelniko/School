import java.sql.*;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;

//import com.mysql.cj.protocol.Resultset;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;

public class Lab5 {

    public static String execQuery(String type, String query){
        Statement stmt;
        ResultSet rs=null;
        Connection con = null;
        String ret="";

        try {


            // Register the JDBC driver for MySQL.
//            Class.forName("com.mysql.jdbc.Driver");

            // Define URL of database server for
            // database named 'user' on the faure.
            String url =
                    "jdbc:mysql://localhost:8080/dmelniko?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

            // Get a connection to the database for a
            // user named 'user' with the password
            // 123456789.
            con = DriverManager.getConnection(
                    url,"dmelniko", "831209674");

            // Display URL and connection information
//            System.out.println("URL: " + url);
//            System.out.println("Connection: " + con);

            // Get a Statement object
            stmt = con.createStatement();

            if(type.equals("Query")){
                try{

                    //process query
                    rs = stmt.executeQuery(query);
                    //if empty return
                    if (!rs.isBeforeFirst() ) {
                        System.out.println("No data");
                        ret= "No data";
                    }
                    else{
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnsNumber = rsmd.getColumnCount();
                        for (int i = 1; i <= columnsNumber; i++) {
                            System.out.print(rsmd.getColumnName(i)+",   ");
                        }
                        System.out.println();
                        while (rs.next()) {
                            for (int i = 1; i <= columnsNumber; i++) {
                                if (i > 1) System.out.print(",  ");
                                String columnValue = rs.getString(i);
                                System.out.print(columnValue );
                            }
                            System.out.println("");
                        }
                    }



                }catch(Exception e){
                    System.out.print(e);
                }//end catch
            }

            if(type.equals("Update")){
                try{
                    int rows = stmt.executeUpdate(query);
                    ret="Updated "+rows+" rows";

                }catch(Exception e){
                    System.out.print(e);
                    ret="fail update";
                }//end catch
            }



            con.close();
        }catch( Exception e ) {
            e.printStackTrace();

        }//end catch

        return ret;
    }


    public void readXML(String fileName)
    {
        try {
            File file = new File(fileName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("Borrowed_by");

            for (int s = 0; s < nodeLst.getLength(); s++) {

                Node fstNode = nodeLst.item(s);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element sectionNode = (Element) fstNode;

                    NodeList memberIdElementList = sectionNode.getElementsByTagName("MemberID");
                    Element memberIdElmnt = (Element) memberIdElementList.item(0);
                    NodeList memberIdNodeList = memberIdElmnt.getChildNodes();
                    String idString=((Node) memberIdNodeList.item(0)).getNodeValue().trim();

                    NodeList secnoElementList = sectionNode.getElementsByTagName("ISBN");
                    Element secnoElmnt = (Element) secnoElementList.item(0);
                    NodeList secno = secnoElmnt.getChildNodes();
                    String isbnString = ((Node) secno.item(0)).getNodeValue().trim();


                    NodeList codateElementList = sectionNode.getElementsByTagName("Checkout_date");
                    Element codElmnt = (Element) codateElementList.item(0);
                    NodeList cod = codElmnt.getChildNodes();
                    String codString=((Node) cod.item(0)).getNodeValue().trim();



                    NodeList cidateElementList = sectionNode.getElementsByTagName("Checkin_date");
                    Element cidElmnt = (Element) cidateElementList.item(0);
                    NodeList cid = cidElmnt.getChildNodes();
                    String cidString=((Node) cid.item(0)).getNodeValue().trim();

                    SimpleDateFormat sqlDate=new SimpleDateFormat("MM/dd/yyyy");
                    DateFormat sqlDateToStr = new SimpleDateFormat("yyyy-MM-dd");

                    //if checking in a book update record if checkout record exists for this book
                    if(!cidString.equals("N/A")){
                        Date cidDate=sqlDate.parse(cidString);
                        String cidFormated = sqlDateToStr.format(cidDate);
                        System.out.println("Checking IN a book "+ isbnString);
                        String ret=execQuery("Update","update BorrowedBy SET Checkindate= '"+cidFormated+"' WHERE ISBN = '"+isbnString
                                +"'");
                    }
                    //if checking out a book veryfy book exists in create new record
                    if(!codString.equals("N/A")){
                        Date codDate=sqlDate.parse(codString);
                        String codFormated = sqlDateToStr.format(codDate);
                        System.out.println("Checking OUT a book "+ isbnString);
                        String ret=execQuery("Update","INSERT INTO BorrowedBy VALUES( '"+idString+"',  '"+isbnString
                                +"' , '"+codFormated+"', NULL)");
                    }
                    System.out.println();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String args[]){
        String ret;

        String input1="";
        String input2="";
        String input3 ="";
        String input4="";
        String input5="";

        JTextField field1 = new JTextField(15);
        JTextField field2 = new JTextField(15);
        JTextField field3 = new JTextField(15);
        JTextField field4 = new JTextField(15);
        JTextField field5 = new JTextField(15);

        JPanel myPanel = new JPanel();

//        It should first ask for the members id and verify that the member has a valid entry.
//        If the id is not currently in the system, it should ask the questions to add the member.
//        It should have a termination condition here as well.

        myPanel.setLayout((new BoxLayout(myPanel, BoxLayout.Y_AXIS)));
        myPanel.add(new JLabel("Enter member ID:"));
        myPanel.add(field1);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Enter Info ", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            input1=field1.getText();
            System.out.println("Member Id entered: " + input1);
        }
         ret=execQuery("Query", "Select * from Member where Member.MemberID = "+input1);

        //if no existing member ask to enter new member
        if(ret.equalsIgnoreCase("No Data")){

            myPanel.removeAll();
            myPanel.setLayout((new BoxLayout(myPanel, BoxLayout.Y_AXIS)));
            myPanel.add(new JLabel("Enter new member ID:"));
            myPanel.add(field1);
            myPanel.add(new JLabel("Enter new member First Name:"));
            myPanel.add(field2);
            myPanel.add(new JLabel("Enter new member Last Name:"));
            myPanel.add(field3);
            myPanel.add(new JLabel("Enter new member Gender:"));
            myPanel.add(field4);
            myPanel.add(new JLabel("Enter new member DOB FORMAT yyyy-MM-dd:"));
            myPanel.add(field5);

            result = JOptionPane.showConfirmDialog(null, myPanel,
                    "Create New Member: Enter Info ", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                input1=field1.getText();
                input2=field2.getText();
                input3=field3.getText();
                input4=field4.getText();
                input5=field5.getText();
                ret=execQuery("Update","INSERT INTO Member VALUES( '"+input1+"',  '"+input2
                        +"' , '"+input3+"', '"+input4+"', '"+input5+"')");
                System.out.println("Created new Member with info: " + input1+" "+input2+" "+input3+" "+input4+" "+input5);

            }

        }
//        It should then ask for the book they want to check out. This can be done one of three ways.
//        ISBN
//        Name - this can be a partial name, if more than one name matches, allow the user to select.
//        Author, then choosing from a list of books by that author

        myPanel.removeAll();
        myPanel.setLayout((new BoxLayout(myPanel, BoxLayout.Y_AXIS)));

        String[] select = new String[] {"ISBN", "Book Name",
                "Author"};

        JComboBox<String> selectList = new JComboBox<>(select);

        myPanel.add(selectList);
        myPanel.add(new JLabel("Enter book ISBN:"));
        myPanel.add(field1);
        myPanel.add(new JLabel("OR Enter book name, partial OK:"));
        myPanel.add(field2);
        myPanel.add(new JLabel("OR Enter author name:"));
        myPanel.add(field3);
        String selectedBook="";
        result = JOptionPane.showConfirmDialog(null, myPanel,
                "Choose which way to find book: ", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // get the selected item:
             selectedBook = (String) selectList.getSelectedItem();
            System.out.println("You are searching by "+selectedBook);

        }
        switch(selectedBook){
            case("ISBN"):
                ret=execQuery("Query","SELECT * from Book where ISBN = '"+field1.getText()+"'");
                System.out.print("SELECT * from Book where ISBN = "+field1.getText());
                break;
            case("Book Name"):
                ret=execQuery("Query","SELECT * from Book where Title LIKE '%"+field2.getText()+"%'");
                System.out.print("SELECT * from Book where Title LIKE '%"+field2.getText()+"%'");
                break;
            case("Author"):
                ret=execQuery("Query","SELECT * from Book where ISBN = "+field1.getText());
                break;
        }
        if(!ret.equalsIgnoreCase("No Data")) {
            System.out.println("Found book");
        }




//        try {
//
//            Lab4 showXML = new Lab4();
//            //parse xml and update table
//            showXML.readXML ("Libdata.xml");
//            //print all of BorrowedBy table
//            System.out.println();
//            ResultSet rs=execQuery("Query", "Select * from BorrowedBy");
//            System.out.println();
//            //get list of books currently checked out for each member:
//            ResultSet rs2=execQuery("Query", "SELECT m.MemberID, m.First_Name, m.Last_Name, b.Title FROM Book b INNER JOIN BorrowedBy r INNER JOIN Member m ON b.ISBN=r.ISBN AND r.MemberID=m.MemberID WHERE CheckinDate IS NULL ORDER BY m.MemberID");
//
//        }catch( Exception e ) {
//            e.printStackTrace();
//
//        }//end catch

    }//end main

}//end class Lab5
