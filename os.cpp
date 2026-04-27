#include <iostream>
#include <vector>
#include <cstdlib>
#include <ctime>

using namespace std;

// Structure for Memory Block
struct MemoryBlock {
    int node_id;   // Which NUMA node owns this block
};

// Structure for Process
struct Process {
    int id;
    int home_node;   // Node where process runs
};

// NUMA Simulator Class
class NUMASimulator {
private:
    int num_nodes;
    int memory_per_node;
    vector<vector<MemoryBlock> > memory;
    vector<Process> processes;

    int local_latency;
    int remote_latency;

    int total_accesses;
    int local_accesses;
    int remote_accesses;

public:
    NUMASimulator(int nodes, int mem_size) {
        num_nodes = nodes;
        memory_per_node = mem_size;

        local_latency = 1;
        remote_latency = 5;

        total_accesses = 0;
        local_accesses = 0;
        remote_accesses = 0;

        // Initialize memory for each node
        memory.resize(num_nodes);
        for (int i = 0; i < num_nodes; i++) {
            for (int j = 0; j < memory_per_node; j++) {
                MemoryBlock block;
                block.node_id = i;
                memory[i].push_back(block);
            }
        }
    }

    // Create processes
    void createProcesses(int num_processes) {
        for (int i = 0; i < num_processes; i++) {
            Process p;
            p.id = i;
            p.home_node = rand() % num_nodes;
            processes.push_back(p);
        }
    }

    // Simulate memory access
    void simulateAccess(int num_requests) {
        for (int i = 0; i < num_requests; i++) {

            int proc_index = rand() % processes.size();
            Process p = processes[proc_index];

            int target_node = rand() % num_nodes;
            int mem_index = rand() % memory_per_node;

            MemoryBlock block = memory[target_node][mem_index];

            total_accesses++;

            if (block.node_id == p.home_node) {
                local_accesses++;
                cout << "Process " << p.id << " accessed LOCAL memory on Node "
                     << p.home_node << " (Latency: " << local_latency << ")\n";
            } else {
                remote_accesses++;
                cout << "Process " << p.id << " accessed REMOTE memory from Node "
                     << block.node_id << " (Latency: " << remote_latency << ")\n";
            }
        }
    }

    // Print statistics
    void printStats() {
        cout << "\n===== Simulation Results =====\n";

        cout << "Total Accesses: " << total_accesses << endl;
        cout << "Local Accesses: " << local_accesses << endl;
        cout << "Remote Accesses: " << remote_accesses << endl;

        double avg_latency = ((local_accesses * local_latency) +
                             (remote_accesses * remote_latency)) 
                             / (double) total_accesses;

        cout << "Average Memory Access Latency: " << avg_latency << endl;
    }
};

int main() {
    srand(time(0));

    int nodes = 4;
    int memory_size = 16;
    int processes = 5;
    int requests = 20;

    NUMASimulator sim(nodes, memory_size);

    sim.createProcesses(processes);
    sim.simulateAccess(requests);
    sim.printStats();

    return 0;
}