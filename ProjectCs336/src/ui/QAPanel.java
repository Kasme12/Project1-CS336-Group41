package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import model.Customer;
import model.QA;
import dao.QADAO;

public class QAPanel {

    private Customer currentCustomer;

    public QAPanel(Customer customer) {
        this.currentCustomer = customer;
    }

    public JPanel createQAPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton askButton = new JButton("Ask Question");
        JButton detailsButton = new JButton("View Details");

        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(askButton);
        topPanel.add(detailsButton);

        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Customer", "Question", "Answer"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
    @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };


        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);

        loadTableData(model);

        searchButton.addActionListener(e -> {
    performSearch(model, searchField.getText());
});

detailsButton.addActionListener(e -> {
    int selectedRow = table.getSelectedRow();
    if (selectedRow >= 0) {
        String question = (String) model.getValueAt(selectedRow, 1);
        String answer = (String) model.getValueAt(selectedRow, 2);
        JOptionPane.showMessageDialog(panel, "Question: " + question + "\nAnswer: " + answer);
    } else {
        JOptionPane.showMessageDialog(panel, "Please select a question to view details.");
    }
});

        askButton.addActionListener(e -> {
            String question = JOptionPane.showInputDialog(panel, "Enter your question:");

            if (question != null && !question.trim().isEmpty()) {
                try {
                    QADAO dao = new QADAO();
                    dao.addQuestion(currentCustomer.getCustomerID(), question);

                    JOptionPane.showMessageDialog(panel, "Question submitted!");

                    model.setRowCount(0);
                    loadTableData(model);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        return panel;
    }

    private void loadTableData(DefaultTableModel model) {
        try {
            QADAO dao = new QADAO();
            List<QA> list = dao.getAllQA();

            for (QA qa : list) {
                model.addRow(new Object[]{
                    qa.getCustomerName(),
                    qa.getQuestion(),
                    qa.getAnswer() == null ? "Pending..." : qa.getAnswer()
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void performSearch(DefaultTableModel model, String keyword) {
    try {
        QADAO dao = new QADAO();

        // if empty → show all
        if (keyword == null || keyword.trim().isEmpty()) {
            model.setRowCount(0);
            loadTableData(model);
        } else {
            model.setRowCount(0);
            List<QA> list = dao.searchQA(keyword);

            for (QA qa : list) {
                model.addRow(new Object[]{
                    qa.getCustomerName(),
                    qa.getQuestion(),
                    qa.getAnswer() == null ? "Pending..." : qa.getAnswer()
                });
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}