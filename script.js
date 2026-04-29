const LOCAL_LATENCY = 1;
const REMOTE_LATENCY = 5;

let sim = null;
let simInterval = null;
let stepIndex = 0;
let chart = null;

class NUMASimulator {
  constructor(numNodes, memPerNode, numProcs, numReqs) {
    this.numNodes = numNodes;
    this.memPerNode = memPerNode;
    this.processes = [];
    this.events = [];
    this.stats = { total: 0, local: 0, remote: 0 };
    this.nodeStats = [];

    for (let i = 0; i < numNodes; i++) {
      this.nodeStats.push({ local: 0, remote: 0 });
    }

    for (let i = 0; i < numProcs; i++) {
      this.processes.push({
        id: i,
        homeNode: Math.floor(Math.random() * numNodes)
      });
    }

    for (let i = 0; i < numReqs; i++) {
      const proc = this.processes[Math.floor(Math.random() * numProcs)];
      const targetNode = Math.floor(Math.random() * numNodes);

      const isLocal = proc.homeNode === targetNode;

      this.events.push({
        proc,
        targetNode,
        isLocal
      });
    }
  }
}

function buildUI(nodes, procs) {
  const grid = document.getElementById('nodes-grid');
  grid.innerHTML = '';

  for (let n = 0; n < nodes; n++) {
    let memHTML = '';
    for (let m = 0; m < sim.memPerNode; m++) {
      memHTML += `<div class="mem-block" id="mb-${n}-${m}"></div>`;
    }

    let procHTML = '';
    for (let p = 0; p < procs; p++) {
      if (sim.processes[p].homeNode === n) {
        procHTML += `<div class="process-pill" id="pp-${p}">P${p}</div>`;
      }
    }

    grid.innerHTML += `
      <div class="node-card" id="node-${n}">
        <div class="node-header">
          <div class="node-name">Node ${n}</div>
          <div class="node-badge" id="nb-${n}">idle</div>
        </div>
        <div class="mem-grid">${memHTML}</div>
        <div>${procHTML}</div>
      </div>
    `;
  }
}

function startSim() {
  const nodes = +document.getElementById('cfg-nodes').value;
  const mem = +document.getElementById('cfg-mem').value;
  const procs = +document.getElementById('cfg-procs').value;
  const reqs = +document.getElementById('cfg-reqs').value;

  sim = new NUMASimulator(nodes, mem, procs, reqs);
  stepIndex = 0;

  buildUI(nodes, procs);
  initChart(nodes);

  document.getElementById('log-scroll').innerHTML = '';

  simInterval = setInterval(() => {
    if (stepIndex >= sim.events.length) {
      clearInterval(simInterval);
      return;
    }
    applyEvent(sim.events[stepIndex++]);
  }, 300);
}

function applyEvent(ev) {
  const { proc, targetNode, isLocal } = ev;

  sim.stats.total++;
  if (isLocal) {
    sim.stats.local++;
    sim.nodeStats[targetNode].local++;
  } else {
    sim.stats.remote++;
    sim.nodeStats[targetNode].remote++;
  }

  updateStats();
  flashNode(targetNode, isLocal);
  addLog(proc, targetNode, isLocal);
  updateChart(targetNode, isLocal);
}

function flashNode(node, isLocal) {
  const el = document.getElementById(`node-${node}`);
  const badge = document.getElementById(`nb-${node}`);

  badge.classList.remove("local", "remote");

  if (isLocal) {
    badge.classList.add("local");
    badge.textContent = "local";
  } else {
    badge.classList.add("remote");
    badge.textContent = "remote";
  }

  setTimeout(() => {
    badge.classList.remove("local", "remote");
    badge.textContent = "idle";
  }, 500);
}

function addLog(proc, node, isLocal) {
  const log = document.getElementById("log-scroll");

  const div = document.createElement("div");
  div.className = "log-entry";

  div.innerHTML = `
    <span>${isLocal ? "LOCAL" : "REMOTE"}</span>
    Process P${proc.id} → Node ${node}
  `;

  log.appendChild(div);
  log.scrollTop = log.scrollHeight;
}

function updateStats() {
  document.getElementById('s-total').textContent = sim.stats.total;
  document.getElementById('s-local').textContent = sim.stats.local;
  document.getElementById('s-remote').textContent = sim.stats.remote;

  const avg = (sim.stats.local * LOCAL_LATENCY +
               sim.stats.remote * REMOTE_LATENCY) / sim.stats.total;

  document.getElementById('s-latency').textContent = avg.toFixed(2);
}

function initChart(nodes) {
  const ctx = document.getElementById('chart-main').getContext('2d');

  chart = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: Array.from({length: nodes}, (_, i) => `Node ${i}`),
      datasets: [
        { label: 'Local', data: new Array(nodes).fill(0), backgroundColor: '#3ecf8e' },
        { label: 'Remote', data: new Array(nodes).fill(0), backgroundColor: '#ff5b6b' }
      ]
    }
  });
}

function updateChart(node, isLocal) {
  const idx = isLocal ? 0 : 1;
  chart.data.datasets[idx].data[node]++;
  chart.update();
}

function resetSim() {
  location.reload();
}

function flashMem(targetnode, isLocal) {
  const blocks = document.querySelectorAll(`#node-${node} .mem-block`);
  if (!blocks.length) return;

  // pick random block
  const index = Math.floor(Math.random() * blocks.length);
  const block = blocks[index];

  // remove previous highlight
  block.classList.remove("lit-local", "lit-remote");

  // force repaint (important for animation)
  void block.offsetWidth;

  // add correct color
  block.classList.add(isLocal ? "lit-local" : "lit-remote");

  setTimeout(() => {
    block.classList.remove("lit-local", "lit-remote");
  }, 500);
}