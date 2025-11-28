import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/*
  Combined controller + UI + file manager + report in one class for simplicity.
  Keeps Transaction model separate for clarity.
*/
public class ExpenseTracker {

    private JFrame frame;
    private DefaultTableModel tableModel;
    private List<Transaction> items = new ArrayList<>();
    private File lastDir = null;

    public void createAndShowUI() {
        frame = new JFrame("Expense Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        JToolBar toolbar = new JToolBar();
        JButton addBtn = new JButton("Add");
        JButton removeBtn = new JButton("Remove");
        JButton saveBtn = new JButton("Save");
        JButton loadBtn = new JButton("Load");
        JButton reportBtn = new JButton("Report");

        toolbar.add(addBtn);
        toolbar.add(removeBtn);
        toolbar.add(saveBtn);
        toolbar.add(loadBtn);
        toolbar.addSeparator();
        toolbar.add(reportBtn);

        frame.add(toolbar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Type","Category","Description","Amount","Date"}, 0);
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(new EmptyBorder(10,10,10,10));
        frame.add(sc, BorderLayout.CENTER);

        JPanel status = new JPanel(new GridLayout(1,3));
        status.setBorder(new EmptyBorder(5,10,5,10));
        JLabel incomeLbl = new JLabel("Income: 0.00");
        JLabel expenseLbl = new JLabel("Expense: 0.00");
        JLabel balanceLbl = new JLabel("Balance: 0.00");
        status.add(incomeLbl);
        status.add(expenseLbl);
        status.add(balanceLbl);
        frame.add(status, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            TransactionDialog d = new TransactionDialog(frame);
            d.setVisible(true);
            if (d.isOk()) {
                Transaction t = new Transaction(d.getTransType(), d.getCategory(), d.getDescription(), d.getAmount(), d.getDate());
                items.add(t);
                refreshTable();
            }
        });

        removeBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0 && r < items.size()) {
                items.remove(r);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(frame, "Select a row to remove");
            }
        });

        saveBtn.addActionListener(e -> saveToCsv());
        loadBtn.addActionListener(e -> loadFromCsv());
        reportBtn.addActionListener(e -> showReport());

        refreshTable();
        frame.setVisible(true);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Transaction t : items) {
            tableModel.addRow(new Object[]{
                    t.getType().name(),
                    t.getCategory(),
                    t.getDescription(),
                    String.format("%.2f", t.getAmount()),
                    t.getDate().toString()
            });
        }
        updateStatus();
    }

    private void updateStatus() {
        double inc = items.stream().filter(i -> i.getType() == Transaction.Type.INCOME).mapToDouble(Transaction::getAmount).sum();
        double exp = items.stream().filter(i -> i.getType() == Transaction.Type.EXPENSE).mapToDouble(Transaction::getAmount).sum();
        double bal = inc - exp;

        JPanel south = (JPanel) frame.getContentPane().getComponent(2);
        JLabel incL = (JLabel) south.getComponent(0);
        JLabel expL = (JLabel) south.getComponent(1);
        JLabel balL = (JLabel) south.getComponent(2);
        incL.setText("Income: " + String.format("%.2f", inc));
        expL.setText("Expense: " + String.format("%.2f", exp));
        balL.setText("Balance: " + String.format("%.2f", bal));
    }

    private void saveToCsv() {
        JFileChooser chooser = new JFileChooser(lastDir);
        int res = chooser.showSaveDialog(frame);
        if (res != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();
        lastDir = f.getParentFile();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            for (Transaction t : items) {
                String line = String.format("%s,%s,%s,%.2f,%s",
                        t.getType().name(),
                        escape(t.getCategory()),
                        escape(t.getDescription()),
                        t.getAmount(),
                        t.getDate().toString());
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error saving file");
            ex.printStackTrace();
        }
    }

    private void loadFromCsv() {
        JFileChooser chooser = new JFileChooser(lastDir);
        int res = chooser.showOpenDialog(frame);
        if (res != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();
        lastDir = f.getParentFile();
        List<Transaction> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 5) continue;
                Transaction.Type type = parts[0].equals("INCOME") ? Transaction.Type.INCOME : Transaction.Type.EXPENSE;
                String cat = unescape(parts[1]);
                String desc = unescape(parts[2]);
                double amt = Double.parseDouble(parts[3]);
                java.time.LocalDate date = java.time.LocalDate.parse(parts[4]);
                list.add(new Transaction(type, cat, desc, amt, date));
            }
            items = list;
            refreshTable();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading file");
            ex.printStackTrace();
        }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace(",", ";");
    }

    private String unescape(String s) {
        return s == null ? "" : s.replace(";", ",");
    }

    private void showReport() {
        JDialog d = new JDialog(frame, "Report", true);
        d.setSize(500,400);
        d.setLocationRelativeTo(frame);

        JPanel top = new JPanel(new GridLayout(1,3));
        double income = items.stream().filter(i -> i.getType() == Transaction.Type.INCOME).mapToDouble(Transaction::getAmount).sum();
        double expense = items.stream().filter(i -> i.getType() == Transaction.Type.EXPENSE).mapToDouble(Transaction::getAmount).sum();
        double balance = income - expense;
        top.add(new JLabel("Income: " + String.format("%.2f", income)));
        top.add(new JLabel("Expense: " + String.format("%.2f", expense)));
        top.add(new JLabel("Balance: " + String.format("%.2f", balance)));
        d.add(top, BorderLayout.NORTH);

        JPanel chartPanel = new PieChartPanel(items);
        d.add(chartPanel, BorderLayout.CENTER);

        d.setVisible(true);
    }

    // small custom pie chart panel for expense categories
    private static class PieChartPanel extends JPanel {
        private Map<String, Double> byCategory = new LinkedHashMap<>();
        private double total = 0;

        PieChartPanel(List<Transaction> all) {
            for (Transaction t : all) {
                if (t.getType() == Transaction.Type.EXPENSE) {
                    byCategory.put(t.getCategory(), byCategory.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
                    total += t.getAmount();
                }
            }
            setPreferredSize(new Dimension(400,300));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w,h) - 40;
            int x = (w-size)/2;
            int y = (h-size)/2 + 20;

            if (total <= 0) {
                g2.drawString("No expense data to show", x + 10, y + 20);
                return;
            }

            int start = 0;
            int i = 0;
            for (Map.Entry<String, Double> e : byCategory.entrySet()) {
                double val = e.getValue();
                int angle = (int) Math.round((val / total) * 360);
                g2.setColor(getColor(i));
                g2.fillArc(x, y, size, size, start, angle);
                start += angle;
                i++;
            }

            // legend
            int lx = 10;
            int ly = 10;
            i = 0;
            for (Map.Entry<String, Double> e : byCategory.entrySet()) {
                g2.setColor(getColor(i));
                g2.fillRect(lx, ly + i*20, 12, 12);
                g2.setColor(Color.BLACK);
                String label = String.format("%s (%.2f)", e.getKey(), e.getValue());
                g2.drawString(label, lx+18, ly + 12 + i*20);
                i++;
            }
        }

        private Color getColor(int i) {
            Color[] palette = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.MAGENTA, Color.PINK, Color.LIGHT_GRAY};
            return palette[i % palette.length];
        }
    }

    // Dialog to add a transaction (renamed accessor to avoid Window.getType() conflict)
    private static class TransactionDialog extends JDialog {
        private JComboBox<String> typeBox;
        private JTextField categoryField;
        private JTextField descField;
        private JTextField amountField;
        private JTextField dateField;
        private boolean ok = false;

        TransactionDialog(Frame owner) {
            super(owner, "Add Transaction", true);
            setLayout(new GridLayout(6,2,5,5));
            add(new JLabel("Type:"));
            typeBox = new JComboBox<>(new String[]{"INCOME","EXPENSE"});
            add(typeBox);

            add(new JLabel("Category:"));
            categoryField = new JTextField();
            add(categoryField);

            add(new JLabel("Description:"));
            descField = new JTextField();
            add(descField);

            add(new JLabel("Amount:"));
            amountField = new JTextField();
            add(amountField);

            add(new JLabel("Date (YYYY-MM-DD):"));
            dateField = new JTextField(LocalDate.now().toString());
            add(dateField);

            JButton okBtn = new JButton("OK");
            JButton cancelBtn = new JButton("Cancel");
            add(okBtn);
            add(cancelBtn);

            okBtn.addActionListener((ActionEvent e) -> {
                ok = true;
                setVisible(false);
            });
            cancelBtn.addActionListener((ActionEvent e) -> {
                ok = false;
                setVisible(false);
            });

            pack();
            setLocationRelativeTo(owner);
        }

        boolean isOk() { return ok; }

        // RENAMED to avoid conflict with Window.getType()
        Transaction.Type getTransType() { 
            return "INCOME".equals(typeBox.getSelectedItem()) ? Transaction.Type.INCOME : Transaction.Type.EXPENSE; 
        }

        String getCategory() { return categoryField.getText().trim(); }
        String getDescription() { return descField.getText().trim(); }
        double getAmount() { try { return Double.parseDouble(amountField.getText().trim()); } catch (Exception e) { return 0.0; } }
        LocalDate getDate() { try { return LocalDate.parse(dateField.getText().trim()); } catch (Exception e) { return LocalDate.now(); } }
    }
}
