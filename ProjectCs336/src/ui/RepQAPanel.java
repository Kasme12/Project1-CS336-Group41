package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import dao.QADAO;
import model.QA;

public class RepQAPanel {

    public JPanel createPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));

        // Table columns (ID hidden)
        String[] columns = {"ID", "Customer", "Question", "Answer"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton refreshButton = new JButton("Refresh");
        JButton answerButton = new JButton("Answer Selected");

        bottomPanel.add(refreshButton);
        bottomPanel.add(answerButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Load data
        loadData(model);

        // Refresh button
        refreshButton.addActionListener(e -> {
            model.setRowCount(0);
            loadData(model);
        });

        // Answer button
        answerButton.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(panel, "Select a question first!");
                return;
            }

            int qaID = (int) model.getValueAt(row, 0);

            String answer = JOptionPane.showInputDialog(panel, "Enter answer:");

            if (answer != null && !answer.trim().isEmpty()) {
                try {
                    QADAO dao = new QADAO();
                    dao.answerQuestion(qaID, answer);

                    JOptionPane.showMessageDialog(panel, "Answer saved!");

                    model.setRowCount(0);
                    loadData(model);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        return panel;
    }

    private void loadData(DefaultTableModel model) {
        try {
            QADAO dao = new QADAO();
            List<QA> list = dao.getAllQA();

            for (QA qa : list) {
                model.addRow(new Object[]{
                    qa.getId(),
                    qa.getCustomerName(),
                    qa.getQuestion(),
                    qa.getAnswer() == null ? "Pending..." : qa.getAnswer()
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}