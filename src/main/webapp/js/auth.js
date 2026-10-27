/**
 * Authentication & Session Management
 */
document.addEventListener('DOMContentLoaded', async () => {
    // Check session on load
    const userRes = await API.getCurrentUser();
    const isAuthPage = window.location.pathname.endsWith('login.html') || window.location.pathname.endsWith('index.html') || window.location.pathname === '/';

    if (userRes.authenticated && userRes.user) {
        // User is logged in
        updateUserUI(userRes.user);
        if (isAuthPage && window.location.pathname.endsWith('login.html')) {
            window.location.href = 'dashboard.html';
        }
    } else {
        // User is not logged in
        if (!isAuthPage) {
            window.location.href = 'login.html';
        }
    }

    // Attach Logout Handlers
    const logoutBtns = document.querySelectorAll('.btn-logout-action');
    logoutBtns.forEach(btn => {
        btn.addEventListener('click', async (e) => {
            e.preventDefault();
            const res = await API.logout();
            if (res.success) {
                window.location.href = 'login.html';
            }
        });
    });

    // Login Form
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('login-email').value;
            const password = document.getElementById('login-password').value;
            const errorEl = document.getElementById('auth-error-msg');
            const submitBtn = loginForm.querySelector('button[type="submit"]');

            submitBtn.disabled = true;
            submitBtn.textContent = 'Opening Ledger...';
            if (errorEl) errorEl.style.display = 'none';

            const res = await API.login(email, password);
            if (res.success) {
                showToast(res.message);
                setTimeout(() => window.location.href = 'dashboard.html', 400);
            } else {
                if (errorEl) {
                    errorEl.textContent = res.message;
                    errorEl.style.display = 'block';
                } else {
                    showToast(res.message, true);
                }
                submitBtn.disabled = false;
                submitBtn.textContent = 'Open Journal';
            }
        });
    }

    // Register Form
    const registerForm = document.getElementById('register-form');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const name = document.getElementById('reg-name').value;
            const email = document.getElementById('reg-email').value;
            const password = document.getElementById('reg-password').value;
            const confirmPass = document.getElementById('reg-confirm-password')?.value;
            const errorEl = document.getElementById('auth-error-msg');
            const submitBtn = registerForm.querySelector('button[type="submit"]');

            if (confirmPass && password !== confirmPass) {
                if (errorEl) {
                    errorEl.textContent = 'Passwords do not match.';
                    errorEl.style.display = 'block';
                }
                return;
            }

            submitBtn.disabled = true;
            submitBtn.textContent = 'Creating Journal...';
            if (errorEl) errorEl.style.display = 'none';

            const res = await API.register(name, email, password);
            if (res.success) {
                showToast(res.message);
                setTimeout(() => window.location.href = 'dashboard.html', 500);
            } else {
                if (errorEl) {
                    errorEl.textContent = res.message;
                    errorEl.style.display = 'block';
                } else {
                    showToast(res.message, true);
                }
                submitBtn.disabled = false;
                submitBtn.textContent = 'Create My Journal';
            }
        });
    }

    // Demo Fill Shortcut Button
    const demoFillBtn = document.getElementById('btn-demo-fill');
    if (demoFillBtn) {
        demoFillBtn.addEventListener('click', (e) => {
            e.preventDefault();
            const emailInput = document.getElementById('login-email');
            const passInput = document.getElementById('login-password');
            if (emailInput && passInput) {
                emailInput.value = 'demo@expensetracker.com';
                passInput.value = 'Password@123';
                showToast('Filled demo credentials. Click "Open Journal" to enter!');
            }
        });
    }
});

function updateUserUI(user) {
    const nameEls = document.querySelectorAll('.user-name-display');
    nameEls.forEach(el => el.textContent = user.name);

    const initialEls = document.querySelectorAll('.user-avatar-initials');
    const initials = user.name ? user.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() : 'U';
    initialEls.forEach(el => el.textContent = initials);
}
