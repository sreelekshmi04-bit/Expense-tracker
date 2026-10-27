/**
 * Centralized API Client for Expense Tracker System
 * Handles communication with Java Servlets and Standalone Server.
 */
const API = {
    baseUrl: '/api',

    async request(endpoint, options = {}) {
        const url = `${this.baseUrl}${endpoint}`;
        const headers = {
            'Accept': 'application/json',
            ...(options.headers || {})
        };

        if (options.body && typeof options.body === 'object' && !(options.body instanceof FormData)) {
            headers['Content-Type'] = 'application/json';
            options.body = JSON.stringify(options.body);
        }

        try {
            const response = await fetch(url, {
                ...options,
                headers,
                credentials: 'include' // Sends session cookie
            });

            if (response.status === 401) {
                return { success: false, authenticated: false, message: 'Unauthorized' };
            }

            // Check if downloading file (CSV)
            const contentType = response.headers.get('content-type') || '';
            if (contentType.includes('text/csv') || contentType.includes('text/plain')) {
                return await response.text();
            }

            return await response.json();
        } catch (error) {
            console.error(`API Error on ${url}:`, error);
            return { success: false, message: 'Network or server connection failed. Make sure the Java server is running.' };
        }
    },

    // Auth endpoints
    getCurrentUser() {
        return this.request('/auth');
    },

    login(email, password) {
        return this.request('/auth?action=login', {
            method: 'POST',
            body: { email, password }
        });
    },

    register(name, email, password) {
        return this.request('/auth?action=register', {
            method: 'POST',
            body: { name, email, password }
        });
    },

    logout() {
        return this.request('/auth?action=logout', { method: 'POST' });
    },

    // Dashboard endpoints
    getDashboard() {
        return this.request('/dashboard');
    },

    resetSampleData() {
        return this.request('/dashboard?action=resetSample', { method: 'POST' });
    },

    clearAllData() {
        return this.request('/dashboard?action=clearAll', { method: 'POST' });
    },

    // Transaction endpoints
    getTransactions(filters = {}) {
        const query = new URLSearchParams(filters).toString();
        return this.request(`/transactions?${query}`);
    },

    getTransaction(id) {
        return this.request(`/transactions?action=get&id=${id}`);
    },

    addTransaction(tx) {
        return this.request('/transactions?action=add', {
            method: 'POST',
            body: tx
        });
    },

    updateTransaction(tx) {
        return this.request('/transactions?action=update', {
            method: 'POST',
            body: tx
        });
    },

    deleteTransaction(id) {
        return this.request('/transactions?action=delete', {
            method: 'POST',
            body: { id }
        });
    },

    // Budget endpoints
    getBudgets(month) {
        const q = month ? `?month=${encodeURIComponent(month)}` : '';
        return this.request(`/budgets${q}`);
    },

    setBudget(category, amountLimit, monthYear) {
        return this.request('/budgets?action=set', {
            method: 'POST',
            body: { category, amountLimit, monthYear }
        });
    },

    deleteBudget(id) {
        return this.request('/budgets?action=delete', {
            method: 'POST',
            body: { id }
        });
    },

    // Smart Spending Alert endpoints
    getAlerts(unreadOnly = false) {
        return this.request(`/alerts?unreadOnly=${unreadOnly}`);
    },

    acknowledgeAlert(id) {
        return this.request('/alerts?action=read', {
            method: 'POST',
            body: { id }
        });
    },

    acknowledgeAllAlerts() {
        return this.request('/alerts?action=readAll', { method: 'POST' });
    },

    deleteAlert(id) {
        return this.request('/alerts?action=delete', {
            method: 'POST',
            body: { id }
        });
    },

    // Reports endpoints
    getReport(filters = {}) {
        const query = new URLSearchParams(filters).toString();
        return this.request(`/reports?${query}`);
    },

    downloadReportCsv(filters = {}) {
        const query = new URLSearchParams({ ...filters, format: 'csv' }).toString();
        window.location.href = `${this.baseUrl}/reports?${query}`;
    },

    exportCsv(filters = {}) {
        const query = new URLSearchParams({ ...filters, format: 'csv' }).toString();
        return this.request(`/reports?${query}`);
    }
};

/**
 * Toast Notification Utility
 */
function showToast(message, isError = false) {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast ${isError ? 'toast-error' : ''}`;
    toast.innerHTML = `
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            ${isError 
                ? '<circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line>' 
                : '<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline>'}
        </svg>
        <span>${escapeHtml(message)}</span>
    `;

    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

function formatCurrency(val) {
    const num = Number(val) || 0;
    return '₹' + num.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}
