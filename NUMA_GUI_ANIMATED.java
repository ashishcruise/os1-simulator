import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class NUMA_GUI_ANIMATED {

    static int local = 0, remote = 0;
    static int[] processNode;
    static int nodes, processes, requests;
    static int step = 0;

    static JTextArea output;
    static Timer timer;

    public static void main(String[] args) {

        JFrame frame = new JFrame("NUMA Simulator");
        frame.setSize(750, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(new Color(24, 24, 24));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("NUMA Memory Simulator", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        // Inputs
        JTextField nodeField = new JTextField();
        JTextField processField = new JTextField();
        JTextField requestField = new JTextField();

        styleField(nodeField);
        styleField(processField);
        styleField(requestField);

        JPanel input = new JPanel(new GridLayout(3, 2, 10, 10));
        input.setBackground(new Color(40, 40, 40));
        input.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        input.add(label("Nodes")); input.add(nodeField);
        input.add(label("Processes")); input.add(processField);
        input.add(label("Requests")); input.add(requestField);

        // Buttons
        JButton startBtn = new JButton("▶ Start Simulation");
        JButton resultBtn = new JButton("⚡ Show Result");

        styleButton(startBtn);
        styleButton(resultBtn);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(24, 24, 24));
        btnPanel.add(startBtn);
        btnPanel.add(resultBtn);

        // Output
        output = new JTextArea();
        output.setBackground(new Color(30, 30, 30));
        output.setForeground(Color.WHITE);
        output.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(output);

        // Start Animation
        startBtn.addActionListener(e -> {
            try {
                nodes = Integer.parseInt(nodeField.getText());
                processes = Integer.parseInt(processField.getText());
                requests = Integer.parseInt(requestField.getText());

                local = 0;
                remote = 0;
                step = 0;

                processNode = new int[processes];
                Random rand = new Random();

                for (int i = 0; i < processes; i++) {
                    processNode[i] = rand.nextInt(nodes);
                }

                output.setText("🚀 Simulation Started...\n\n");

                timer = new Timer(300, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {

                        if (step >= requests) {
                            timer.stop();
                            output.append("\n✅ Simulation Finished!\n");
                            return;
                        }

                        Random r = new Random();
                        int p = r.nextInt(processes);
                        int mem = r.nextInt(nodes);

                        if (processNode[p] == mem) {
                            local++;
                            output.append("🟢 P" + p + " → LOCAL (1)\n");
                        } else {
                            remote++;
                            output.append("🔴 P" + p + " → REMOTE (5)\n");
                        }

                        step++;
                    }
                });

                timer.start();

            } catch (Exception ex) {
                output.setText("⚠ Enter valid numbers!");
            }
        });

        // Show Final Result
        resultBtn.addActionListener(e -> {
            int total = local + remote;
            if (total == 0) {
                output.append("\n⚠ Run simulation first!\n");
                return;
            }

            double avg = ((local * 1) + (remote * 5)) / (double) total;

            output.append("\n━━━━━━━━ RESULT ━━━━━━━━\n");
            output.append("Total: " + total + "\n");
            output.append("Local: " + local + "\n");
            output.append("Remote: " + remote + "\n");
            output.append("Avg Latency: " + avg + "\n");
        });

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(new Color(24, 24, 24));
        center.add(input, BorderLayout.NORTH);
        center.add(btnPanel, BorderLayout.CENTER);

        main.add(title, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);
        main.add(scroll, BorderLayout.SOUTH);

        frame.add(main);
        frame.setVisible(true);
    }

    static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.LIGHT_GRAY);
        return l;
    }

    static void styleField(JTextField f) {
        f.setBackground(new Color(60, 60, 60));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
    }

    static void styleButton(JButton b) {
        b.setBackground(new Color(0, 150, 136));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(0, 180, 160));
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(new Color(0, 150, 136));
            }
        });
    }
}