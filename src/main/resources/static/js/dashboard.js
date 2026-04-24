// ═══════════════════════════════════════
//  dashboard.js — FlowLine Dashboard
// ═══════════════════════════════════════

// Verifica autenticação
if (!AUTH.requireAuth()) throw new Error('Not authenticated');

// ─── ESTADO ──────────────────────────────────
let ordersChart = null;

// ─── UTILITÁRIOS ─────────────────────────────
function setEl(id, value) {
  const el = document.getElementById(id);
  if (el) {
    el.classList.remove('skeleton');
    el.textContent = value ?? '—';
  }
}

function setLoading(ids) {
  ids.forEach(id => {
    const el = document.getElementById(id);
    if (el) {
      el.textContent = '···';
      el.classList.add('skeleton');
    }
  });
}

function formatDate() {
  return new Date().toLocaleDateString('en-US', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  });
}

// ─── DASHBOARD DATA ───────────────────────────
async function loadDashboard() {
  const metricIds = [
    'metricTotal','metricPending','metricDelivering',
    'metricDelivered','metricCancelled','metricToday',
    'metricProducts','metricUsers'
  ];
  setLoading(metricIds);

  try {
    const res = await fetch('/api/dashboard', { headers: AUTH.headers() });

    if (res.status === 401) { AUTH.logout(); return; }
    if (!res.ok) throw new Error('Failed to load dashboard');

    const data = await res.json();
    const o = data.orders;

    setEl('metricTotal',      o.total);
    setEl('metricPending',    o.pending);
    setEl('metricDelivering', o.delivering);
    setEl('metricDelivered',  o.delivered);
    setEl('metricCancelled',  o.cancelled);
    setEl('metricToday',      o.createdToday);
    setEl('metricProducts',   data.totalProducts);
    setEl('metricUsers',      data.totalUsers);

    renderChart(o);

  } catch (err) {
    console.error('Dashboard error:', err);
    metricIds.forEach(id => setEl(id, 'ERR'));
  }
}

// ─── CHART ────────────────────────────────────
function renderChart(orders) {
  const ctx = document.getElementById('ordersChart');
  if (!ctx) return;

  if (ordersChart) {
    ordersChart.destroy();
  }

  const labels = ['Pending', 'Delivering', 'Delivered', 'Cancelled'];
  const values = [
    orders.pending    || 0,
    orders.delivering || 0,
    orders.delivered  || 0,
    orders.cancelled  || 0,
  ];
  const colors = ['#ffd740', '#448aff', '#00e676', '#ff5252'];
  const borderColors = colors.map(c => c + 'cc');

  ordersChart = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels,
      datasets: [{
        data: values,
        backgroundColor: colors.map(c => c + '30'),
        borderColor: borderColors,
        borderWidth: 2,
        hoverBackgroundColor: colors.map(c => c + '55'),
        hoverBorderWidth: 3,
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      cutout: '72%',
      plugins: {
        legend: {
          position: 'right',
          labels: {
            color: '#7a8499',
            font: { family: 'Space Mono', size: 11 },
            padding: 16,
            boxWidth: 12,
            boxHeight: 12,
          }
        },
        tooltip: {
          backgroundColor: '#0f1117',
          borderColor: 'rgba(255,255,255,0.1)',
          borderWidth: 1,
          titleColor: '#e8eaf0',
          bodyColor: '#7a8499',
          titleFont: { family: 'Syne', weight: '700' },
          bodyFont: { family: 'Space Mono', size: 11 },
          padding: 12,
          callbacks: {
            label: (ctx) => {
              const total = ctx.dataset.data.reduce((a, b) => a + b, 0);
              const pct = total > 0 ? ((ctx.raw / total) * 100).toFixed(1) : 0;
              return `  ${ctx.raw} orders (${pct}%)`;
            }
          }
        }
      }
    }
  });
}

// ─── AI CHAT ─────────────────────────────────
const chatMessages = document.getElementById('chatMessages');
const chatInput    = document.getElementById('chatInput');
const sendBtn      = document.getElementById('sendBtn');

function addMessage(text, role) {
  const msgDiv = document.createElement('div');
  msgDiv.className = `chat-msg ${role}`;

  const bubble = document.createElement('div');
  bubble.className = 'chat-bubble';
  bubble.textContent = text;

  msgDiv.appendChild(bubble);
  chatMessages.appendChild(msgDiv);
  chatMessages.scrollTop = chatMessages.scrollHeight;
  return msgDiv;
}

function addLoadingMessage() {
  const msgDiv = document.createElement('div');
  msgDiv.className = 'chat-msg bot loading';
  const bubble = document.createElement('div');
  bubble.className = 'chat-bubble';
  msgDiv.appendChild(bubble);
  chatMessages.appendChild(msgDiv);
  chatMessages.scrollTop = chatMessages.scrollHeight;
  return msgDiv;
}

async function sendMessage() {
  const text = chatInput.value.trim();
  if (!text) return;

  chatInput.value = '';
  sendBtn.disabled = true;
  chatInput.disabled = true;

  addMessage(text, 'user');
  const loadingEl = addLoadingMessage();

  try {
    const res = await fetch('/api/chat', {
      method: 'POST',
      headers: AUTH.headers(),
      body: JSON.stringify({ ask: text })
    });

    if (res.status === 401) { AUTH.logout(); return; }

    const data = await res.json();
    loadingEl.remove();
    addMessage(data.answer || 'Sorry, I could not process that.', 'bot');

  } catch (err) {
    loadingEl.remove();
    addMessage('Connection error. Please try again.', 'bot');
  } finally {
    sendBtn.disabled = false;
    chatInput.disabled = false;
    chatInput.focus();
  }
}

sendBtn.addEventListener('click', sendMessage);
chatInput.addEventListener('keydown', (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault();
    sendMessage();
  }
});

// ─── REFRESH ─────────────────────────────────
document.getElementById('refreshBtn').addEventListener('click', () => {
  const btn = document.getElementById('refreshBtn');
  btn.classList.add('spinning');
  loadDashboard().finally(() => {
    setTimeout(() => btn.classList.remove('spinning'), 600);
  });
});

// ─── INIT ─────────────────────────────────────
document.getElementById('dashDate').textContent = formatDate();
loadDashboard();

// Auto-refresh a cada 60 segundos
setInterval(loadDashboard, 60_000);
