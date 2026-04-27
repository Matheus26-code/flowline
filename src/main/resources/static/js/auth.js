const AUTH = {
  TOKEN_KEY: 'fl_token',
  ROLE_KEY:  'fl_role',

  getToken() { return sessionStorage.getItem(this.TOKEN_KEY); },
  getRole()  { return sessionStorage.getItem(this.ROLE_KEY); },
  setToken(token) { sessionStorage.setItem(this.TOKEN_KEY, token); },
  setRole(role)   { sessionStorage.setItem(this.ROLE_KEY, role); },
  removeToken() { sessionStorage.removeItem(this.TOKEN_KEY); },
  removeRole()  { sessionStorage.removeItem(this.ROLE_KEY); },

  isLoggedIn() { return !!this.getToken(); },
  hasRole(role) { return this.getRole() === role; },
  hasAnyRole(roles) { return roles.includes(this.getRole()); },
  isAdmin()    { return this.hasRole('ROLE_ADMIN'); },
  isManage()   { return this.hasRole('ROLE_MANAGE'); },
  isOperator() { return this.hasRole('ROLE_OPERATOR'); },
  isAssistant(){ return this.hasRole('ROLE_ASSISTANT'); },

  headers() {
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + this.getToken()
    };
  },

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
    this.setRole(data.role);
    window.location.href = '/app.html';
  },

  logout() {
    this.removeToken();
    this.removeRole();
    window.location.href = '/index.html';
  },

  requireAuth() {
    if (!this.isLoggedIn()) {
      window.location.href = '/index.html';
      return false;
    }
    return true;
  }
};

if (document.getElementById('loginForm')) {
  if (AUTH.isLoggedIn()) {
    window.location.href = '/app.html';
  }
  const form   = document.getElementById('loginForm');
  const btn    = document.getElementById('loginBtn');
  const errMsg = document.getElementById('errorMsg');

  function showError(msg) { errMsg.textContent = msg; errMsg.classList.add('visible'); }
  function clearError()   { errMsg.classList.remove('visible'); }

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearError();
    const email    = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    if (!email || !password) { showError('Please fill in all fields.'); return; }
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

if (document.getElementById('logoutBtn')) {
  document.getElementById('logoutBtn').addEventListener('click', () => AUTH.logout());
}
