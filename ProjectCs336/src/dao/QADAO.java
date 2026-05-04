package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.QA;

public class QADAO {
    
    public void addQuestion(int customerID, String question) throws Exception {
    String sql = "INSERT INTO QA (customer_id, question) VALUES (?, ?)";

    Connection conn = DBConnection.getConnection();
    PreparedStatement pstmt = conn.prepareStatement(sql);

    pstmt.setInt(1, customerID);
    pstmt.setString(2, question);

    pstmt.executeUpdate();

    pstmt.close();
    conn.close();
    }

    public List<QA> getAllQA() throws Exception {
    List<QA> list = new ArrayList<>();

    String sql = "SELECT q.qa_id, q.question, q.answer, c.name " +
                 "FROM QA q " +
                 "JOIN Customer c ON q.customer_id = c.customerID " +
                 "ORDER BY q.created_at DESC";

    Connection conn = DBConnection.getConnection();
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();

    while (rs.next()) {
        QA qa = new QA();

        qa.setId(rs.getInt("qa_id"));
        qa.setCustomerName(rs.getString("name"));
        qa.setQuestion(rs.getString("question"));
        qa.setAnswer(rs.getString("answer"));

        list.add(qa);
    }

    rs.close();
    pstmt.close();
    conn.close();

    return list;
}
}
