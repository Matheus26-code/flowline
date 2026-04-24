// ═══════════════════════════════════════
//  auth.js — FlowLine JWT management
// ═══════════════════════════════════════

const AUTH = {
  TOKEN_KEY: 'fl_token',

  getToken() {
    return sessionStorage.getItem(this.TOKEN_KEY);
  },

  setToken(token) {
    sessionStorage.setItem(this.TOKEN_KEY, token);
  },

  removeToken() {
    sessionStorage.removeItem(this.TOKEN_KEY);
  },

  isLoggedIn() {
    return !!this.getToken();
  },

  // Headers padrão com Bearer token
  headers() {
    return {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${this.getToken()}`
    };
  },

  // Faz login e redireciona para dashboard
  async login(email, password) {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Invalid credentials');
    }

    const data = await res.json();
    this.setToken(data.token);
    window.location.href = '/dashboard.html';
  },

  // Logout e volta para login
  logout() {
    this.removeToken();
    window.location.href = '/index.html';
  },

  // Garante que está autenticado — redireciona se não estiver
  requireAuth() {
    if (!this.isLoggedIn()) {
      window.location.href = '/index.html';
      return false;
    }
    return true;
  }
};

// ─── SETUP DA PÁGINA DE LOGIN ─────────────────
if (document.getElementById('loginForm')) {
  // Se já está logado, vai direto pro dashboard
  if (AUTH.isLoggedIn()) {
    window.location.href = '/dashboard.html';
  }

  const form   = document.getElementById('loginForm');
  const btn    = document.getElementById('loginBtn');
  const errMsg = document.getElementById('errorMsg');

  function showError(msg) {
    errMsg.textContent = msg;
    errMsg.classList.add('visible');
  }

  function clearError() {
    errMsg.classList.remove('visible');
  }

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearError();

    const email    = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;

    if (!email || !password) {
      showError('Please fill in all fields.');
      return;
    }

    btn.classList.add('loading');
    btn.disabled = true;

    try {
      await AUTH.login(email, password);
    } catch (err) {
      showError(err.message || 'Authentication failed. Try again.');
      btn.classList.remove('loading');
      btn.disabled = false;
    }
  });
}

// ─── SETUP DO BOTÃO LOGOUT ───────────────────
if (document.getElementById('logoutBtn')) {
  document.getElementById('logoutBtn').addEventListener('click', () => {
    AUTH.logout();
  });
}
