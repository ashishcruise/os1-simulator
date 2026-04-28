import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class NUMAGUI {

    // Simulation Logic (same idea as your C++ code)
    static String runSimulation(int nodes, int processes, int requests) {

        Random rand = new Random();

        int localLatency = 1;
        int remoteLatency = 5;

        int total = 0;
        int local = 0;
        int remote = 0;

        int[] processNode = new int[processes];

        // Assign processes to nodes
        for (int i = 0; i < processes; i++) {
            processNode[i] = rand.nextInt(nodes);
        }

        String result = "";

        for (int i = 0; i < requests; i++) {

            int p = rand.nextInt(processes);
            int memNode = rand.nextInt(nodes);

            total++;

            if (processNode[p] == memNode) {
                local++;
                result += "Process " + p + " → LOCAL access (Latency 1)\n";
            } else {
                remote++;
                result += "Process " + p + " → REMOTE access (Latency 5)\n";
            }
        }

        double avg = ((local * localLatency) + (remote * remoteLatency)) / (double) total;

        result += "\n--- RESULTS ---\n";
        result += "Total: " + total + "\n";
        result += "Local: " + local + "\n";
        result += "Remote: " + remote + "\n";
        result += "Average Latency: " + avg + "\n";

        return result;
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("NUMA Simulator");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JLabel l1 = new JLabel("Nodes:");
        JTextField t1 = new JTextField(5);

        JLabel l2 = new JLabel("Processes:");
        JTextField t2 = new JTextField(5);

        JLabel l3 = new JLabel("Requests:");
        JTextField t3 = new JTextField(5);

        JButton btn = new JButton("Run Simulation");

        JTextArea output = new JTextArea(20, 40);
        JScrollPane scroll = new JScrollPane(output);

        // Button Action
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int nodes = Integer.parseInt(t1.getText());
                int processes = Integer.parseInt(t2.getText());
                int requests = Integer.parseInt(t3.getText());

                String result = runSimulation(nodes, processes, requests);
                output.setText(result);
            }
        });

        frame.add(l1);
        frame.add(t1);
        frame.add(l2);
        frame.add(t2);
        frame.add(l3);
        frame.add(t3);
        frame.add(btn);
        frame.add(scroll);

        frame.setVisible(true);
    }
}