/**
 * Expense Tracker — Personal Finance & Budget Manager
 * Light Theme & Violet Fintech Design System
 * Simplified, Intuitive, Beginner-Friendly Application Flow
 */

const CATEGORIES = {
    INCOME: ['Allowance', 'Salary', 'Freelance', 'Gift', 'Scholarship', 'Other'],
    EXPENSE: ['Food', 'Transport', 'Shopping', 'Bills', 'Education', 'Entertainment', 'Healthcare', 'Other']
};

/**
 * Consistent Professional SVG Category Icons
 */
function getCategoryIconSvg(category, size = 15) {
    switch (category) {
        case 'Food':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8h1a4 4 0 0 1 0 8h-1"></path><path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"></path><line x1="6" y1="1" x2="6" y2="4"></line><line x1="10" y1="1" x2="10" y2="4"></line><line x1="14" y1="1" x2="14" y2="4"></line></svg>`;
        case 'Transport':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="13" rx="2"></rect><circle cx="7" cy="17" r="2"></circle><circle cx="17" cy="17" r="2"></circle><line x1="3" y1="10" x2="21" y2="10"></line></svg>`;
        case 'Shopping':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path><line x1="3" y1="6" x2="21" y2="6"></line><path d="M16 10a4 4 0 0 1-8 0"></path></svg>`;
        case 'Bills':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"></polygon></svg>`;
        case 'Education':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path></svg>`;
        case 'Entertainment':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="2" width="20" height="20" rx="2.18"></rect><line x1="7" y1="2" x2="7" y2="22"></line><line x1="17" y1="2" x2="17" y2="22"></line><line x1="2" y1="12" x2="22" y2="12"></line></svg>`;
        case 'Healthcare':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"></path></svg>`;
        case 'Allowance':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><path d="M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H8"></path><line x1="12" y1="18" x2="12" y2="20"></line><line x1="12" y1="4" x2="12" y2="6"></line></svg>`;
        case 'Salary':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2"></rect><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path></svg>`;
        case 'Scholarship':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="7"></circle><polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"></polyline></svg>`;
        case 'Freelance':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="16 18 22 12 16 6"></polyline><polyline points="8 6 2 12 8 18"></polyline></svg>`;
        case 'Gift':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 12 20 22 4 22 4 12"></polyline><rect x="2" y="7" width="20" height="5"></rect><line x1="12" y1="22" x2="12" y2="7"></line></svg>`;
        default:
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>`;
    }
}

function getPaymentMethodIconSvg(method, size = 13) {
    switch (method) {
        case 'UPI':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="5" y="2" width="14" height="20" rx="2"></rect><line x1="12" y1="18" x2="12.01" y2="18"></line></svg>`;
        case 'Cash':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="6" width="20" height="12" rx="2"></rect><circle cx="12" cy="12" r="2"></circle></svg>`;
        case 'Card':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2"></rect><line x1="1" y1="10" x2="23" y2="10"></line></svg>`;
        case 'Bank Transfer':
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="3" y1="22" x2="21" y2="22"></line><line x1="6" y1="18" x2="6" y2="11"></line><line x1="10" y1="18" x2="10" y2="11"></line><line x1="14" y1="18" x2="14" y2="11"></line><line x1="18" y1="18" x2="18" y2="11"></line><polygon points="12 2 20 7 4 7"></polygon></svg>`;
        default:
            return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle></svg>`;
    }
}

// Global Application State
let appState = {
    currentTab: 'dashboard',
    activeMonth: new Date().toISOString().substring(0, 7), // YYYY-MM
    summary: null,
    transactions: [],
    budgets: [],
    alerts: [],
    categoryBreakdown: [],
    monthlyComparison: {},
    txPendingDeleteId: null,
    mysqlConnected: false
};

document.addEventListener('DOMContentLoaded', () => {
    initApp();
});

async function initApp() {
    setupTabNavigation();
    setupModals();
    setupForms();
    setupFilters();
    setupHeaderButtons();

    // Set today's date in forms by default
    const today = new Date().toISOString().split('T')[0];
    const expDate = document.getElementById('expense-date');
    const incDate = document.getElementById('income-date');
    if (expDate) expDate.value = today;
    if (incDate) incDate.value = today;

    // Set month inputs default
    const monthSelects = ['budget-month-select', 'reports-month-select', 'budget-month-input'];
    monthSelects.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = appState.activeMonth;
    });

    // Check URL hash for initial tab
    const hash = window.location.hash.replace('#', '');
    if (hash && ['dashboard', 'transactions', 'budgets', 'reports'].includes(hash)) {
        switchTab(hash);
    }

    // Load live telemetry from backend
    await refreshAllData();
}

/**
 * =========================================================================
 * NAVIGATION (Only 4 Clean Destinations: Dashboard, Transactions, Budgets, Reports)
 * =========================================================================
 */
function setupTabNavigation() {
    const tabBtns = document.querySelectorAll('.nav-tab-btn');
    tabBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabName = btn.getAttribute('data-tab');
            if (tabName) {
                switchTab(tabName);
            }
        });
    });

    const budgetMonthInput = document.getElementById('budget-month-select');
    if (budgetMonthInput) {
        budgetMonthInput.addEventListener('change', (e) => {
            appState.activeMonth = e.target.value || new Date().toISOString().substring(0, 7);
            loadBudgetsForMonth(appState.activeMonth);
        });
    }

    const reportsMonthInput = document.getElementById('reports-month-select');
    if (reportsMonthInput) {
        reportsMonthInput.addEventListener('change', (e) => {
            appState.activeMonth = e.target.value || new Date().toISOString().substring(0, 7);
            renderReportsView();
        });
    }
}

function switchTab(tabName) {
    if (!['dashboard', 'transactions', 'budgets', 'reports'].includes(tabName)) {
        tabName = 'dashboard';
    }
    if (appState.currentTab === tabName) return; // no-op if already on this tab
    appState.currentTab = tabName;
    window.location.hash = tabName;

    // Update active button
    document.querySelectorAll('.nav-tab-btn').forEach(btn => {
        btn.classList.toggle('active', btn.getAttribute('data-tab') === tabName);
    });

    // Update view visibility
    document.querySelectorAll('.tab-view').forEach(view => {
        view.classList.toggle('active-view', view.id === `view-${tabName}`);
    });

    // Refresh view specific components
    if (tabName === 'dashboard') {
        renderDashboard();
    } else if (tabName === 'transactions') {
        renderTransactionsTable();
    } else if (tabName === 'budgets') {
        loadBudgetsForMonth(appState.activeMonth);
    } else if (tabName === 'reports') {
        renderReportsView();
    }
}

/**
 * Header Buttons: Load Demo Data & Reset (with confirmation modal)
 */
function setupHeaderButtons() {
    const sampleBtn = document.getElementById('btn-load-sample');
    if (sampleBtn) {
        sampleBtn.addEventListener('click', loadSampleData);
    }

    const clearBtn = document.getElementById('btn-clear-data');
    if (clearBtn) {
        clearBtn.addEventListener('click', () => {
            openModal('modal-confirm-reset');
        });
    }

    const resetConfirmBtn = document.getElementById('btn-confirm-reset-action');
    if (resetConfirmBtn) {
        resetConfirmBtn.addEventListener('click', executeClearAllData);
    }

    // CSV Download buttons
    document.querySelectorAll('.btn-download-csv').forEach(btn => {
        btn.addEventListener('click', exportCsvData);
    });
}

async function loadSampleData() {
    showToast('Loading realistic demo data...');
    try {
        const res = await API.resetSampleData();
        if (res && res.success) {
            showToast('Demo data loaded successfully!');
            await refreshAllData();
        } else {
            showToast('Failed to load demo data', true);
        }
    } catch (e) {
        showToast('Server connection error', true);
    }
}

async function executeClearAllData() {
    closeModal('modal-confirm-reset');
    showToast('Clearing all records...');
    try {
        const res = await API.clearAllData();
        if (res && res.success) {
            showToast('All records cleared successfully.');
            await refreshAllData();
        } else {
            showToast('Failed to clear records', true);
        }
    } catch (e) {
        showToast('Server error clearing records', true);
    }
}

/**
 * =========================================================================
 * DATA SYNCHRONIZATION WITH BACKEND
 * =========================================================================
 */
async function refreshAllData() {
    try {
        // Fetch fresh dashboard data & all transactions in parallel (no waiting on each other)
        const [dashRes, txRes] = await Promise.all([
            API.getDashboard(),
            API.getTransactions()
        ]);

        if (dashRes && dashRes.success) {
            appState.summary = dashRes.summary;
            appState.alerts = dashRes.unreadAlerts || [];
            appState.budgets = dashRes.budgetProgress || [];
            appState.categoryBreakdown = dashRes.categoryBreakdown || [];
            appState.monthlyComparison = dashRes.monthlyComparison || {};
            appState.mysqlConnected = dashRes.mysqlConnected;
            updateDatabaseStatus(dashRes.mysqlConnected, dashRes.dbUser);
        }

        if (txRes && txRes.success) {
            appState.transactions = txRes.transactions || [];
        }

        // Use requestAnimationFrame to batch DOM updates — avoids forced reflow
        requestAnimationFrame(() => {
            if (appState.currentTab === 'dashboard') {
                renderDashboard();
            } else if (appState.currentTab === 'transactions') {
                renderTransactionsTable();
            } else if (appState.currentTab === 'budgets') {
                loadBudgetsForMonth(appState.activeMonth);
            } else if (appState.currentTab === 'reports') {
                renderReportsView();
            }
        });

        // After data is fresh, check for exceeded budgets and queue popups
        checkAndTriggerExceededBudgetAlerts();
    } catch (e) {
        console.error('Error refreshing application data:', e);
    }
}

function updateDatabaseStatus(connected, dbUser) {
    const badge = document.getElementById('db-status-badge');
    const dot = document.getElementById('db-status-dot');
    const text = document.getElementById('db-status-text');

    if (!badge || !dot || !text) return;

    if (connected) {
        dot.className = 'status-dot connected';
        text.textContent = 'MySQL Connected';
        badge.title = `Connected to local MySQL database as '${dbUser || 'root'}'`;
    } else {
        dot.className = 'status-dot';
        text.textContent = 'In-Memory Active';
        badge.title = 'Running on high-speed in-memory store. Updates take effect immediately.';
    }
}

/**
 * =========================================================================
 * 1. DASHBOARD VIEW RENDERING
 * =========================================================================
 */
function renderDashboard() {
    if (!appState.summary) return;

    const s = appState.summary;

    // 1. Available Balance
    const balanceEl = document.getElementById('metric-balance');
    if (balanceEl) {
        balanceEl.textContent = formatCurrency(s.currentBalance);
        if (s.currentBalance < 0) {
            balanceEl.style.color = 'var(--expense)';
        } else {
            balanceEl.style.color = 'var(--text-primary)';
        }
    }

    // 2. Total Income This Month
    const incEl = document.getElementById('metric-income');
    if (incEl) {
        incEl.textContent = formatCurrency(s.monthlyIncome);
    }

    // 3. Total Expenses This Month
    const expEl = document.getElementById('metric-expense');
    if (expEl) {
        expEl.textContent = formatCurrency(s.monthlyExpense);
    }

    // 4. Remaining Monthly Budget
    const budgetRemainingEl = document.getElementById('metric-budget-remaining');
    const budgetSubEl = document.getElementById('metric-budget-sub');

    // Calculate budget totals for current month
    let totalBudgetLimit = 0;
    let totalBudgetSpent = 0;
    if (Array.isArray(appState.budgets) && appState.budgets.length > 0) {
        appState.budgets.forEach(b => {
            totalBudgetLimit += (b.amountLimit || 0);
            totalBudgetSpent += (b.spentAmount || 0);
        });
    }

    if (totalBudgetLimit > 0) {
        const remaining = totalBudgetLimit - totalBudgetSpent;
        if (budgetRemainingEl) {
            budgetRemainingEl.textContent = formatCurrency(remaining);
            if (remaining < 0) {
                budgetRemainingEl.style.color = 'var(--expense)';
            } else {
                budgetRemainingEl.style.color = 'var(--savings)';
            }
        }
        if (budgetSubEl) {
            if (remaining < 0) {
                budgetSubEl.textContent = `₹${formatNumber(Math.abs(remaining))} over monthly budget`;
                budgetSubEl.style.color = 'var(--expense)';
            } else {
                budgetSubEl.textContent = `Spending limit left • Not balance deduction`;
                budgetSubEl.style.color = 'var(--text-secondary)';
            }
        }
    } else {
        if (budgetRemainingEl) {
            budgetRemainingEl.textContent = 'No budget set';
            budgetRemainingEl.style.color = 'var(--text-secondary)';
            budgetRemainingEl.style.fontSize = '1.35rem';
        }
        if (budgetSubEl) {
            budgetSubEl.textContent = 'Set a monthly budget to track spending';
            budgetSubEl.style.color = 'var(--text-secondary)';
        }
    }

    // 5. Monthly Spending Progress Indicator
    renderMonthlySpendingProgress(totalBudgetLimit, totalBudgetSpent);

    // 6. Overspending / Budget Alert Banner on Dashboard
    renderDashboardAlertsBanner();

    // 7. Recent 5 Transactions Table
    renderDashboardRecentTransactions();
}

/**
 * Monthly Spending Progress Indicator
 */
function renderMonthlySpendingProgress(totalLimit, totalSpent) {
    const summaryText = document.getElementById('dashboard-progress-summary-text');
    const badge = document.getElementById('dashboard-progress-status-badge');
    const bar = document.getElementById('dashboard-budget-progress-bar');
    const spentDetail = document.getElementById('dashboard-progress-spent-detail');
    const limitDetail = document.getElementById('dashboard-progress-limit-detail');
    const remDetail = document.getElementById('dashboard-progress-remaining-detail');

    if (!summaryText || !badge || !bar) return;

    if (totalLimit <= 0) {
        badge.className = 'badge-tag badge-subtle';
        badge.textContent = 'No Budget';
        summaryText.innerHTML = `No monthly category budgets set yet. <a href="javascript:void(0)" onclick="openBudgetModal()" style="color:var(--primary); font-weight:700; text-decoration:none;">Set your first budget limit →</a>`;
        bar.style.width = '0%';
        bar.className = 'progress-bar-fill fill-safe';
        if (spentDetail) spentDetail.textContent = `Spent this month: ${formatCurrency(appState.summary?.monthlyExpense || 0)}`;
        if (limitDetail) limitDetail.textContent = 'Budget Limit: None';
        if (remDetail) remDetail.textContent = 'Remaining: —';
        return;
    }

    const remaining = totalLimit - totalSpent;
    const pct = totalLimit > 0 ? (totalSpent / totalLimit) * 100 : 0;
    const clampedPct = Math.min(100, Math.max(0, pct));

    if (spentDetail) spentDetail.textContent = `Spent: ${formatCurrency(totalSpent)}`;
    if (limitDetail) limitDetail.textContent = `Budget Limit: ${formatCurrency(totalLimit)}`;

    if (pct > 100) {
        badge.className = 'badge-tag badge-expense';
        badge.textContent = 'Budget Exceeded';
        summaryText.innerHTML = `Spent <strong>${formatCurrency(totalSpent)}</strong> of <strong>${formatCurrency(totalLimit)}</strong> budget (<span style="color:var(--expense); font-weight:700;">${pct.toFixed(1)}% used</span>) • <strong style="color:var(--expense);">${formatCurrency(Math.abs(remaining))} over budget</strong>`;
        bar.className = 'progress-bar-fill fill-exceeded';
        bar.style.width = '100%';
        if (remDetail) remDetail.innerHTML = `<span style="color:var(--expense); font-weight:700;">Over Budget: +${formatCurrency(Math.abs(remaining))}</span>`;
    } else if (pct >= 80) {
        badge.className = 'badge-tag badge-warning';
        badge.textContent = 'Almost at Limit';
        summaryText.innerHTML = `Spent <strong>${formatCurrency(totalSpent)}</strong> of <strong>${formatCurrency(totalLimit)}</strong> budget (${pct.toFixed(1)}% used) • <strong>${formatCurrency(remaining)} remaining</strong>`;
        bar.className = 'progress-bar-fill fill-warning';
        bar.style.width = `${clampedPct}%`;
        if (remDetail) remDetail.textContent = `Remaining: ${formatCurrency(remaining)}`;
    } else {
        badge.className = 'badge-tag badge-income';
        badge.textContent = 'Within Budget';
        summaryText.innerHTML = `Spent <strong>${formatCurrency(totalSpent)}</strong> of <strong>${formatCurrency(totalLimit)}</strong> budget (${pct.toFixed(1)}% used) • <strong>${formatCurrency(remaining)} remaining</strong>`;
        bar.className = 'progress-bar-fill fill-safe';
        bar.style.width = `${clampedPct}%`;
        if (remDetail) remDetail.textContent = `Remaining: ${formatCurrency(remaining)}`;
    }
}

/**
 * Dashboard Alert Banner (Integrated into Dashboard)
 */
function renderDashboardAlertsBanner() {
    const container = document.getElementById('dashboard-alerts-container');
    if (!container) return;

    if (!appState.alerts || appState.alerts.length === 0) {
        container.style.display = 'none';
        container.innerHTML = '';
        return;
    }

    // Sort: Overspent first
    const sorted = [...appState.alerts].sort((a, b) => (b.thresholdPercent || 0) - (a.thresholdPercent || 0));
    const topAlert = sorted[0];
    const isOverspent = topAlert.alertType === 'OVERSPENT' || topAlert.spentAmount > topAlert.budgetLimit;

    container.style.display = 'block';
    container.innerHTML = `
        <div class="smart-alert-card ${!isOverspent ? 'warning-threshold' : ''}">
            <div class="smart-alert-header">
                <div class="smart-alert-title">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="${isOverspent ? 'var(--expense)' : 'var(--warning)'}" stroke-width="2">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="12" y1="8" x2="12" y2="12"></line>
                        <line x1="12" y1="16" x2="12.01" y2="16"></line>
                    </svg>
                    <h4>${isOverspent ? 'Budget Exceeded Warning' : 'Budget Limit Notice'}</h4>
                </div>
                <button class="btn btn-sm btn-secondary" onclick="switchTab('budgets')">
                    Adjust in Budgets →
                </button>
            </div>
            <div class="smart-alert-desc">
                ${escapeHtml(topAlert.message)}
            </div>
        </div>
    `;
}

/**
 * The 5 Most Recent Transactions
 */
function renderDashboardRecentTransactions() {
    const tbody = document.getElementById('dashboard-recent-tx-tbody');
    if (!tbody) return;

    const list = appState.transactions.slice(0, 5);

    if (list.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" style="padding: 0;">
                    <div class="empty-state-box">
                        <svg class="empty-state-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <rect x="2" y="5" width="20" height="14" rx="2"></rect>
                            <line x1="2" y1="10" x2="22" y2="10"></line>
                        </svg>
                        <div class="empty-state-title">No transactions yet</div>
                        <div class="empty-state-desc">Add your first income or expense to get started tracking your money.</div>
                        <div style="display: flex; gap: 0.75rem; justify-content: center; flex-wrap: wrap;">
                            <button class="btn btn-income" onclick="openAddIncomeModal()">+ Add Income</button>
                            <button class="btn btn-expense" onclick="openAddExpenseModal()">− Add Expense</button>
                        </div>
                    </div>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = list.map(tx => {
        const isIncome = tx.type === 'INCOME';
        const catSvg = getCategoryIconSvg(tx.categoryOrSource, 14);
        const paySvg = getPaymentMethodIconSvg(tx.paymentMethod, 12);

        return `
            <tr>
                <td style="white-space:nowrap; color:var(--text-secondary); font-size:0.84rem;">
                    ${formatDate(tx.transactionDate)}
                </td>
                <td>
                    <span class="badge-tag ${isIncome ? 'badge-income' : 'badge-expense'}">
                        ${isIncome ? '+ Income' : '− Expense'}
                    </span>
                </td>
                <td>
                    <span style="display:inline-flex; align-items:center; gap:0.4rem; font-weight:600;">
                        <span style="color:var(--primary);">${catSvg}</span>
                        ${escapeHtml(tx.categoryOrSource)}
                    </span>
                </td>
                <td style="color:var(--text-primary); max-width:220px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap;">
                    ${escapeHtml(tx.description || '—')}
                </td>
                <td>
                    <span class="badge-tag badge-payment">
                        ${paySvg}
                        ${escapeHtml(tx.paymentMethod || 'Cash')}
                    </span>
                </td>
                <td class="num ${isIncome ? 'color-income' : 'color-expense'}" style="text-align: right; font-weight:700;">
                    ${isIncome ? '+' : '−'}${formatCurrency(tx.amount)}
                </td>
                <td style="text-align: center;">
                    <div class="action-btn-group">
                        <button class="action-btn edit" onclick="openEditModal(${tx.id})" title="Edit transaction">
                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M12 20h9"></path>
                                <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path>
                            </svg>
                        </button>
                        <button class="action-btn delete" onclick="promptDeleteTransaction(${tx.id})" title="Delete transaction">
                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="3 6 5 6 21 6"></polyline>
                                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                            </svg>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * =========================================================================
 * 2. TRANSACTIONS TABLE VIEW (Filters, Search, Sorting, Edit, Delete)
 * =========================================================================
 */
function setupFilters() {
    const typeFilter = document.getElementById('ledger-type-filter');
    const catFilter = document.getElementById('ledger-cat-filter');
    const fromDate = document.getElementById('ledger-date-from');
    const toDate = document.getElementById('ledger-date-to');
    const sortFilter = document.getElementById('ledger-sort');
    const searchInput = document.getElementById('ledger-search');

    const trigger = () => renderTransactionsTable();

    if (typeFilter) typeFilter.addEventListener('change', trigger);
    if (catFilter) catFilter.addEventListener('change', trigger);
    if (fromDate) fromDate.addEventListener('change', trigger);
    if (toDate) toDate.addEventListener('change', trigger);
    if (sortFilter) sortFilter.addEventListener('change', trigger);
    if (searchInput) searchInput.addEventListener('input', trigger);
}

function renderTransactionsTable() {
    const tbody = document.getElementById('full-ledger-tbody');
    const countEl = document.getElementById('ledger-total-count');
    const netEl = document.getElementById('ledger-filtered-net');

    if (!tbody) return;

    const typeVal = document.getElementById('ledger-type-filter')?.value || 'ALL';
    const catVal = document.getElementById('ledger-cat-filter')?.value || 'ALL';
    const fromVal = document.getElementById('ledger-date-from')?.value || '';
    const toVal = document.getElementById('ledger-date-to')?.value || '';
    const sortVal = document.getElementById('ledger-sort')?.value || 'date_desc';
    const searchVal = (document.getElementById('ledger-search')?.value || '').toLowerCase().trim();

    // Filter
    let filtered = appState.transactions.filter(tx => {
        if (typeVal !== 'ALL' && tx.type !== typeVal) return false;
        if (catVal !== 'ALL' && tx.categoryOrSource !== catVal) return false;
        if (fromVal && tx.transactionDate < fromVal) return false;
        if (toVal && tx.transactionDate > toVal) return false;
        if (searchVal) {
            const desc = (tx.description || '').toLowerCase();
            const cat = (tx.categoryOrSource || '').toLowerCase();
            const pay = (tx.paymentMethod || '').toLowerCase();
            if (!desc.includes(searchVal) && !cat.includes(searchVal) && !pay.includes(searchVal)) {
                return false;
            }
        }
        return true;
    });

    // Sort
    filtered.sort((a, b) => {
        if (sortVal === 'date_desc') return b.transactionDate.localeCompare(a.transactionDate) || b.id - a.id;
        if (sortVal === 'date_asc') return a.transactionDate.localeCompare(b.transactionDate) || a.id - b.id;
        if (sortVal === 'amount_desc') return b.amount - a.amount;
        if (sortVal === 'amount_asc') return a.amount - b.amount;
        return 0;
    });

    // Compute Net
    let net = 0;
    filtered.forEach(tx => {
        net += tx.type === 'INCOME' ? tx.amount : -tx.amount;
    });

    if (countEl) countEl.textContent = `${filtered.length} transaction${filtered.length === 1 ? '' : 's'} found`;
    if (netEl) {
        netEl.textContent = formatCurrency(net);
        netEl.style.color = net < 0 ? 'var(--expense)' : 'var(--text-primary)';
    }

    if (filtered.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" style="padding: 0;">
                    <div class="empty-state-box">
                        <svg class="empty-state-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <circle cx="11" cy="11" r="8"></circle>
                            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                        </svg>
                        <div class="empty-state-title">No transactions match your search</div>
                        <div class="empty-state-desc">Try clearing filters or record a new income or expense transaction.</div>
                        <div style="display: flex; gap: 0.75rem; justify-content: center;">
                            <button class="btn btn-income" onclick="openAddIncomeModal()">+ Add Income</button>
                            <button class="btn btn-expense" onclick="openAddExpenseModal()">− Add Expense</button>
                        </div>
                    </div>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = filtered.map(tx => {
        const isIncome = tx.type === 'INCOME';
        const catSvg = getCategoryIconSvg(tx.categoryOrSource, 14);
        const paySvg = getPaymentMethodIconSvg(tx.paymentMethod, 12);

        return `
            <tr>
                <td style="white-space:nowrap; color:var(--text-secondary); font-size:0.84rem;">
                    ${formatDate(tx.transactionDate)}
                </td>
                <td>
                    <span class="badge-tag ${isIncome ? 'badge-income' : 'badge-expense'}">
                        ${isIncome ? '+ Income' : '− Expense'}
                    </span>
                </td>
                <td>
                    <span style="display:inline-flex; align-items:center; gap:0.45rem; font-weight:600;">
                        <span style="color:var(--primary);">${catSvg}</span>
                        ${escapeHtml(tx.categoryOrSource)}
                    </span>
                </td>
                <td style="color:var(--text-primary);">
                    ${escapeHtml(tx.description || '—')}
                </td>
                <td>
                    <span class="badge-tag badge-payment">
                        ${paySvg}
                        ${escapeHtml(tx.paymentMethod || 'Cash')}
                    </span>
                </td>
                <td class="num ${isIncome ? 'color-income' : 'color-expense'}" style="text-align: right; font-weight:700;">
                    ${isIncome ? '+' : '−'}${formatCurrency(tx.amount)}
                </td>
                <td style="text-align: center;">
                    <div class="action-btn-group">
                        <button class="action-btn edit" onclick="openEditModal(${tx.id})" title="Edit transaction">
                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M12 20h9"></path>
                                <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path>
                            </svg>
                        </button>
                        <button class="action-btn delete" onclick="promptDeleteTransaction(${tx.id})" title="Delete transaction">
                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="3 6 5 6 21 6"></polyline>
                                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                            </svg>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * =========================================================================
 * 3. BUDGETS VIEW RENDERING
 * =========================================================================
 */
async function loadBudgetsForMonth(month) {
    try {
        const res = await API.getBudgets(month);
        if (res && res.success) {
            appState.budgets = res.budgets || [];
            renderBudgetsView(res);
        }
    } catch (e) {
        console.error('Error loading budgets:', e);
    }
}

function renderBudgetsView(data) {
    const grid = document.getElementById('budget-cards-grid');
    const totalLimitEl = document.getElementById('budget-summary-total-limit');
    const totalSpentEl = document.getElementById('budget-summary-total-spent');
    const totalRemainingEl = document.getElementById('budget-summary-total-remaining');

    if (!grid) return;

    const budgets = appState.budgets || [];
    let sumLimit = 0;
    let sumSpent = 0;

    budgets.forEach(b => {
        sumLimit += b.amountLimit;
        sumSpent += b.spentAmount;
    });

    const sumRemaining = sumLimit - sumSpent;

    if (totalLimitEl) totalLimitEl.textContent = formatCurrency(sumLimit);
    if (totalSpentEl) totalSpentEl.textContent = formatCurrency(sumSpent);
    if (totalRemainingEl) {
        totalRemainingEl.textContent = formatCurrency(sumRemaining);
        totalRemainingEl.style.color = sumRemaining < 0 ? 'var(--expense)' : 'var(--savings)';
    }

    if (budgets.length === 0) {
        grid.innerHTML = `
            <div class="empty-state-box">
                <svg class="empty-state-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <circle cx="12" cy="12" r="10"></circle>
                    <circle cx="12" cy="12" r="6"></circle>
                    <circle cx="12" cy="12" r="2"></circle>
                </svg>
                <div class="empty-state-title">No category budgets for this month</div>
                <div class="empty-state-desc">Set spending limits for categories like Food, Transport, or Shopping to prevent overspending.</div>
                <button class="btn btn-primary" onclick="openBudgetModal()">+ Create Your First Budget</button>
            </div>
        `;
        return;
    }

    // Sort: Exceeded first, then Warning, then Safe
    const sorted = [...budgets].sort((a, b) => b.percentageUsed - a.percentageUsed);

    grid.innerHTML = sorted.map(b => {
        const isOver = b.spentAmount > b.amountLimit;
        const pct = b.percentageUsed || 0;
        const clampedPct = Math.min(100, Math.max(0, pct));
        const rem = b.amountLimit - b.spentAmount;
        const catSvg = getCategoryIconSvg(b.category, 18);

        let badgeClass = 'badge-income';
        let statusText = 'Within Budget';
        let fillClass = 'fill-safe';

        if (pct > 100) {
            badgeClass = 'badge-expense';
            statusText = 'Budget Exceeded';
            fillClass = 'fill-exceeded';
        } else if (pct >= 80) {
            badgeClass = 'badge-warning';
            statusText = 'Almost at Limit';
            fillClass = 'fill-warning';
        }

        return `
            <div class="card" style="padding: 1.25rem 1.45rem;">
                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.75rem; flex-wrap:wrap; gap:0.5rem;">
                    <div style="display:flex; align-items:center; gap:0.6rem;">
                        <span style="color:var(--primary); background:var(--primary-light); padding:0.45rem; border-radius:var(--radius-sm); display:inline-flex;">${catSvg}</span>
                        <div>
                            <h4 style="font-size:1.05rem; font-weight:700; color:var(--text-primary); margin:0;">${escapeHtml(b.category)}</h4>
                            <span style="font-size:0.78rem; color:var(--text-secondary);">Monthly Spending Ceiling</span>
                        </div>
                    </div>
                    <div style="display:flex; align-items:center; gap:0.65rem;">
                        <span class="badge-tag ${badgeClass}">${statusText}</span>
                        <div class="action-btn-group">
                            <button class="action-btn edit" onclick="openEditBudgetModal('${escapeHtml(b.category)}', ${b.amountLimit}, '${b.monthYear}')" title="Edit limit">
                                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M12 20h9"></path>
                                    <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path>
                                </svg>
                            </button>
                            <button class="action-btn delete" onclick="deleteCategoryBudget(${b.budgetId})" title="Delete budget">
                                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <polyline points="3 6 5 6 21 6"></polyline>
                                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                </svg>
                            </button>
                        </div>
                    </div>
                </div>

                <div class="progress-bar-bg" style="height: 8px;">
                    <div class="progress-bar-fill ${fillClass}" style="width: ${clampedPct}%;"></div>
                </div>

                <div style="display:flex; justify-content:space-between; align-items:center; margin-top:0.65rem; font-size:0.86rem; flex-wrap:wrap; gap:0.5rem;">
                    <span>Spent: <strong style="color:var(--text-primary);">${formatCurrency(b.spentAmount)}</strong> of <strong>${formatCurrency(b.amountLimit)}</strong> (${pct.toFixed(1)}%)</span>
                    <span>
                        ${isOver 
                            ? `<strong style="color:var(--expense);">+${formatCurrency(Math.abs(rem))} Over Budget</strong>`
                            : `<strong style="color:var(--savings);">${formatCurrency(rem)} Remaining</strong>`
                        }
                    </span>
                </div>
            </div>
        `;
    }).join('');
}

/**
 * =========================================================================
 * 4. REPORTS VIEW RENDERING
 * =========================================================================
 */
function renderReportsView() {
    const incEl = document.getElementById('report-metric-income');
    const expEl = document.getElementById('report-metric-expense');
    const savEl = document.getElementById('report-metric-savings');
    const rateEl = document.getElementById('report-metric-savings-rate');
    const chartContainer = document.getElementById('monthly-trend-chart');
    const breakdownContainer = document.getElementById('reports-category-breakdown');
    const momContainer = document.getElementById('report-mom-comparison-card');
    const momText = document.getElementById('report-mom-comparison-text');

    if (!incEl || !expEl || !savEl) return;

    if (appState.transactions.length < 2) {
        if (breakdownContainer) {
            breakdownContainer.innerHTML = `
                <div class="empty-state-box" style="padding: 2rem 1rem;">
                    <div class="empty-state-title" style="font-size:0.98rem;">Insufficient Data</div>
                    <div class="empty-state-desc" style="font-size:0.82rem; margin-bottom:0.75rem;">
                        Record at least a few transactions to generate category spending reports.
                    </div>
                </div>
            `;
        }
        if (chartContainer) {
            chartContainer.innerHTML = '<p style="text-align:center; padding:2rem; color:var(--text-secondary); font-size:0.88rem;">Need at least 2 transactions to display monthly comparison trend.</p>';
        }
        if (momContainer) momContainer.style.display = 'none';
        return;
    }

    if (momContainer) momContainer.style.display = 'block';

    const s = appState.summary || { monthlyIncome: 0, monthlyExpense: 0, monthlySavings: 0, savingsRate: 0 };
    incEl.textContent = formatCurrency(s.monthlyIncome);
    expEl.textContent = formatCurrency(s.monthlyExpense);
    savEl.textContent = formatCurrency(s.monthlySavings);
    if (rateEl) rateEl.textContent = `${(s.savingsRate || 0).toFixed(1)}% of income saved this month`;

    // 1. Render 6-Month Income vs Expense Bar Chart
    renderMonthlyChart();

    // 2. Render Spending by Category Breakdown
    if (breakdownContainer) {
        const catList = appState.categoryBreakdown || [];
        if (catList.length === 0) {
            breakdownContainer.innerHTML = '<p style="text-align:center; padding:1.5rem; color:var(--text-secondary); font-size:0.86rem;">No expenses recorded for this month yet.</p>';
        } else {
            breakdownContainer.innerHTML = catList.map(cat => {
                const catSvg = getCategoryIconSvg(cat.category, 14);
                return `
                    <div style="margin-bottom: 0.85rem;">
                        <div style="display:flex; justify-content:space-between; font-size:0.84rem; margin-bottom:0.25rem;">
                            <span style="display:inline-flex; align-items:center; gap:0.4rem; font-weight:600; color:var(--text-primary);">
                                <span style="color:var(--primary);">${catSvg}</span>
                                ${escapeHtml(cat.category)}
                            </span>
                            <span><strong>${formatCurrency(cat.totalAmount)}</strong> <span style="color:var(--text-secondary); font-size:0.76rem;">(${cat.percentageOfTotal}%)</span></span>
                        </div>
                        <div class="progress-bar-bg" style="height:6px; margin:0;">
                            <div class="progress-bar-fill fill-safe" style="width:${Math.min(100, cat.percentageOfTotal)}%;"></div>
                        </div>
                    </div>
                `;
            }).join('');
        }
    }

    // 3. Month-over-Month Comparison
    if (momText && appState.monthlyComparison) {
        const months = Object.keys(appState.monthlyComparison).sort();
        if (months.length >= 2) {
            const currentM = months[months.length - 1];
            const prevM = months[months.length - 2];
            const currPair = appState.monthlyComparison[currentM] || [0, 0];
            const prevPair = appState.monthlyComparison[prevM] || [0, 0];
            const prevExp = prevPair[1];
            const currExp = currPair[1];
            const diff = currExp - prevExp;

            let changeHtml = '';
            if (prevExp > 0) {
                const pctChange = ((diff / prevExp) * 100).toFixed(1);
                if (diff > 0) {
                    changeHtml = `Spending increased by <strong style="color:var(--expense);">+${formatCurrency(diff)} (+${pctChange}%)</strong> compared to last month (${formatMonthFriendly(prevM)}).`;
                } else if (diff < 0) {
                    changeHtml = `Spending decreased by <strong style="color:var(--income);">−${formatCurrency(Math.abs(diff))} (${pctChange}%)</strong> compared to last month (${formatMonthFriendly(prevM)}). Great job!`;
                } else {
                    changeHtml = `Spending is exactly the same as last month (${formatMonthFriendly(prevM)}).`;
                }
            } else {
                changeHtml = `Total spending for this month is <strong>${formatCurrency(currExp)}</strong>.`;
            }

            momText.innerHTML = `
                <div style="display:flex; align-items:center; gap:0.5rem;">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="color:var(--primary);"><polyline points="23 6 13.5 15.5 8.5 10.5 1 18"></polyline><polyline points="17 6 23 6 23 12"></polyline></svg>
                    <span><strong>Month-Over-Month Comparison:</strong> ${changeHtml}</span>
                </div>
            `;
        } else {
            momText.innerHTML = `Tracking started. Record next month's transactions to compare month-over-month trends.`;
        }
    }
}

function renderMonthlyChart() {
    const chartContainer = document.getElementById('monthly-trend-chart');
    if (!chartContainer) return;

    const data = appState.monthlyComparison;
    if (!data || Object.keys(data).length === 0) {
        chartContainer.innerHTML = '<p style="text-align:center; padding:2rem; color:var(--text-secondary); font-size:0.86rem;">No comparison trend data available.</p>';
        return;
    }

    const months = Object.keys(data).sort();
    let maxVal = 1000;
    months.forEach(m => {
        const pair = data[m] || [0, 0];
        if (pair[0] > maxVal) maxVal = pair[0];
        if (pair[1] > maxVal) maxVal = pair[1];
    });

    const height = 170;
    const barW = 12;
    const gap = 44;
    const startX = 30;
    const totalW = Math.max(340, startX + months.length * gap);

    const axisColor = '#E7E3F2';
    const labelColor = '#77758B';
    const incColor = '#16865C';
    const expColor = '#D95364';

    let bars = '';
    const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

    months.forEach((m, i) => {
        const pair = data[m] || [0, 0];
        const inc = pair[0];
        const exp = pair[1];

        const incH = (inc / maxVal) * (height - 35);
        const expH = (exp / maxVal) * (height - 35);

        const x = startX + i * gap;
        const incY = height - 22 - incH;
        const expY = height - 22 - expH;

        const parts = m.split('-');
        const label = monthNames[parseInt(parts[1], 10) - 1] || m;

        bars += `
            <g>
                <title>${m}: Income ${formatCurrency(inc)}, Expense ${formatCurrency(exp)}</title>
                <rect x="${x}" y="${incY}" width="${barW}" height="${incH}" rx="2" fill="${incColor}"></rect>
                <rect x="${x + barW + 3}" y="${expY}" width="${barW}" height="${expH}" rx="2" fill="${expColor}"></rect>
                <text x="${x + barW}" y="${height - 6}" text-anchor="middle" font-size="10" font-weight="600" fill="${labelColor}">${label}</text>
            </g>
        `;
    });

    chartContainer.innerHTML = `
        <svg viewBox="0 0 ${totalW} ${height}" style="width:100%; height:auto;">
            <line x1="16" y1="${height - 22}" x2="${totalW}" y2="${height - 22}" stroke="${axisColor}" stroke-width="1"></line>
            ${bars}
        </svg>
        <div style="display:flex; justify-content:center; gap:1.5rem; font-size:0.78rem; margin-top:0.5rem;">
            <span style="display:flex; align-items:center; gap:0.4rem; color:#16865C; font-weight:700;">
                <span style="width:9px; height:9px; background:#16865C; border-radius:2px;"></span> Income
            </span>
            <span style="display:flex; align-items:center; gap:0.4rem; color:#D95364; font-weight:700;">
                <span style="width:9px; height:9px; background:#D95364; border-radius:2px;"></span> Expenses
            </span>
        </div>
    `;
}

/**
 * =========================================================================
 * 5. MODALS & FORMS (Add Expense, Add Income, Edit, Budget, Confirmations)
 * =========================================================================
 */
function setupModals() {
    // Close button handlers
    document.querySelectorAll('.btn-close-modal').forEach(btn => {
        btn.addEventListener('click', () => {
            const overlay = btn.closest('.modal-overlay');
            if (overlay) overlay.classList.remove('show');
        });
    });

    // Close on clicking backdrop
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                overlay.classList.remove('show');
            }
        });
    });
}

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('show');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('show');
    }
}

function setupForms() {
    // Form: Add Expense
    const expenseForm = document.getElementById('form-add-expense');
    if (expenseForm) {
        expenseForm.addEventListener('submit', handleAddExpenseSubmit);
    }

    // Form: Add Income
    const incomeForm = document.getElementById('form-add-income');
    if (incomeForm) {
        incomeForm.addEventListener('submit', handleAddIncomeSubmit);
    }

    // Form: Edit Transaction
    const editForm = document.getElementById('form-edit-tx');
    if (editForm) {
        editForm.addEventListener('submit', handleEditTxSubmit);
    }

    // Form: Set Budget
    const budgetForm = document.getElementById('form-save-budget');
    if (budgetForm) {
        budgetForm.addEventListener('submit', handleSaveBudgetSubmit);
    }
}

function openAddExpenseModal() {
    const form = document.getElementById('form-add-expense');
    if (form) form.reset();
    const err = document.getElementById('error-add-expense');
    if (err) err.style.display = 'none';

    const today = new Date().toISOString().split('T')[0];
    const dateInput = document.getElementById('expense-date');
    if (dateInput) dateInput.value = today;

    openModal('modal-add-expense');
    setTimeout(() => {
        document.getElementById('expense-amount')?.focus();
    }, 100);
}

function openAddIncomeModal() {
    const form = document.getElementById('form-add-income');
    if (form) form.reset();
    const err = document.getElementById('error-add-income');
    if (err) err.style.display = 'none';

    const today = new Date().toISOString().split('T')[0];
    const dateInput = document.getElementById('income-date');
    if (dateInput) dateInput.value = today;

    openModal('modal-add-income');
    setTimeout(() => {
        document.getElementById('income-amount')?.focus();
    }, 100);
}

async function handleAddExpenseSubmit(e) {
    e.preventDefault();
    const errEl = document.getElementById('error-add-expense');
    if (errEl) errEl.style.display = 'none';

    const amountStr = document.getElementById('expense-amount')?.value?.trim();
    const category = document.getElementById('expense-category')?.value;
    const date = document.getElementById('expense-date')?.value?.trim();
    const paymentMethod = document.getElementById('expense-payment')?.value || 'Cash';
    const description = document.getElementById('expense-desc')?.value?.trim() || '';

    // Validation
    const amount = parseFloat(amountStr);
    if (!amountStr || isNaN(amount) || amount <= 0) {
        showFormError(errEl, 'Please enter a valid expense amount greater than ₹0.');
        return;
    }
    if (!category) {
        showFormError(errEl, 'Please select an expense category.');
        return;
    }
    if (!date) {
        showFormError(errEl, 'Please select a valid date.');
        return;
    }

    const payload = {
        type: 'EXPENSE',
        amount: amount,
        categoryOrSource: category,
        date: date,
        paymentMethod: paymentMethod,
        description: description
    };

    const submitBtn = document.getElementById('btn-save-expense-submit');
    if (submitBtn) submitBtn.disabled = true;

    try {
        const res = await API.addTransaction(payload);
        if (res && res.success) {
            closeModal('modal-add-expense');
            showToast(`Expense of ${formatCurrency(amount)} saved successfully.`);
            await refreshAllData();
        } else {
            showFormError(errEl, res?.message || 'Failed to save expense. Please check input.');
        }
    } catch (err) {
        showFormError(errEl, 'Network or server error while saving expense.');
    } finally {
        if (submitBtn) submitBtn.disabled = false;
    }
}

async function handleAddIncomeSubmit(e) {
    e.preventDefault();
    const errEl = document.getElementById('error-add-income');
    if (errEl) errEl.style.display = 'none';

    const amountStr = document.getElementById('income-amount')?.value?.trim();
    const source = document.getElementById('income-source')?.value;
    const date = document.getElementById('income-date')?.value?.trim();
    const description = document.getElementById('income-desc')?.value?.trim() || '';

    // Validation
    const amount = parseFloat(amountStr);
    if (!amountStr || isNaN(amount) || amount <= 0) {
        showFormError(errEl, 'Please enter a valid income amount greater than ₹0.');
        return;
    }
    if (!source) {
        showFormError(errEl, 'Please select an income source.');
        return;
    }
    if (!date) {
        showFormError(errEl, 'Please select a valid date.');
        return;
    }

    const payload = {
        type: 'INCOME',
        amount: amount,
        categoryOrSource: source,
        date: date,
        paymentMethod: 'Bank Transfer',
        description: description
    };

    const submitBtn = document.getElementById('btn-save-income-submit');
    if (submitBtn) submitBtn.disabled = true;

    try {
        const res = await API.addTransaction(payload);
        if (res && res.success) {
            closeModal('modal-add-income');
            showToast(`Income of ${formatCurrency(amount)} saved successfully.`);
            await refreshAllData();
        } else {
            showFormError(errEl, res?.message || 'Failed to save income. Please check input.');
        }
    } catch (err) {
        showFormError(errEl, 'Network or server error while saving income.');
    } finally {
        if (submitBtn) submitBtn.disabled = false;
    }
}

function showFormError(el, msg) {
    if (!el) return;
    el.textContent = msg;
    el.style.display = 'flex';
}

/**
 * Edit Transaction Flow
 */
function openEditModal(txId) {
    const tx = appState.transactions.find(t => t.id === txId);
    if (!tx) return;

    const idInput = document.getElementById('edit-tx-id');
    const typeInput = document.getElementById('edit-tx-type');
    const amountInput = document.getElementById('edit-tx-amount');
    const catSelect = document.getElementById('edit-tx-category');
    const catLabel = document.getElementById('edit-tx-cat-label');
    const dateInput = document.getElementById('edit-tx-date');
    const payGroup = document.getElementById('edit-tx-payment-group');
    const paySelect = document.getElementById('edit-tx-payment');
    const descInput = document.getElementById('edit-tx-desc');
    const errEl = document.getElementById('error-edit-tx');

    if (errEl) errEl.style.display = 'none';

    if (idInput) idInput.value = tx.id;
    if (typeInput) typeInput.value = tx.type;
    if (amountInput) amountInput.value = tx.amount;
    if (dateInput) dateInput.value = tx.transactionDate;
    if (descInput) descInput.value = tx.description || '';

    // Populate category dropdown based on type
    const isIncome = tx.type === 'INCOME';
    if (catLabel) catLabel.textContent = isIncome ? 'Income Source *' : 'Expense Category *';
    if (payGroup) payGroup.style.display = isIncome ? 'none' : 'flex';

    const options = isIncome ? CATEGORIES.INCOME : CATEGORIES.EXPENSE;
    if (catSelect) {
        catSelect.innerHTML = options.map(c => `
            <option value="${c}" ${c === tx.categoryOrSource ? 'selected' : ''}>${c}</option>
        `).join('');
    }

    if (paySelect && tx.paymentMethod) {
        paySelect.value = tx.paymentMethod;
    }

    openModal('modal-edit-tx');
}

async function handleEditTxSubmit(e) {
    e.preventDefault();
    const errEl = document.getElementById('error-edit-tx');
    if (errEl) errEl.style.display = 'none';

    const id = document.getElementById('edit-tx-id')?.value;
    const type = document.getElementById('edit-tx-type')?.value;
    const amountStr = document.getElementById('edit-tx-amount')?.value?.trim();
    const category = document.getElementById('edit-tx-category')?.value;
    const date = document.getElementById('edit-tx-date')?.value?.trim();
    const paymentMethod = document.getElementById('edit-tx-payment')?.value || 'Cash';
    const description = document.getElementById('edit-tx-desc')?.value?.trim() || '';

    const amount = parseFloat(amountStr);
    if (!amountStr || isNaN(amount) || amount <= 0) {
        showFormError(errEl, 'Please enter a valid amount greater than ₹0.');
        return;
    }
    if (!category) {
        showFormError(errEl, 'Please select a category or source.');
        return;
    }
    if (!date) {
        showFormError(errEl, 'Please select a date.');
        return;
    }

    const payload = {
        id: parseInt(id, 10),
        type: type,
        amount: amount,
        categoryOrSource: category,
        date: date,
        paymentMethod: paymentMethod,
        description: description
    };

    try {
        const res = await API.updateTransaction(payload);
        if (res && res.success) {
            closeModal('modal-edit-tx');
            showToast('Transaction updated successfully.');
            await refreshAllData();
        } else {
            showFormError(errEl, res?.message || 'Failed to update transaction.');
        }
    } catch (err) {
        showFormError(errEl, 'Network or server error updating transaction.');
    }
}

/**
 * Delete Transaction Flow (With Clean Confirmation Dialog)
 */
function promptDeleteTransaction(txId) {
    const tx = appState.transactions.find(t => t.id === txId);
    if (!tx) return;

    appState.txPendingDeleteId = txId;
    const confirmText = document.getElementById('delete-tx-confirm-text');
    if (confirmText) {
        const isIncome = tx.type === 'INCOME';
        confirmText.textContent = `Are you sure you want to delete this ${isIncome ? 'income' : 'expense'} transaction of ${formatCurrency(tx.amount)} for "${tx.categoryOrSource}"? This cannot be undone.`;
    }

    const actionBtn = document.getElementById('btn-confirm-delete-action');
    if (actionBtn) {
        actionBtn.onclick = executeDeleteTransaction;
    }

    openModal('modal-confirm-delete');
}

async function executeDeleteTransaction() {
    if (!appState.txPendingDeleteId) return;
    const txId = appState.txPendingDeleteId;
    closeModal('modal-confirm-delete');

    try {
        const res = await API.deleteTransaction(txId);
        if (res && res.success) {
            showToast('Transaction deleted successfully.');
            await refreshAllData();
        } else {
            showToast('Failed to delete transaction.', true);
        }
    } catch (e) {
        showToast('Server error deleting transaction.', true);
    } finally {
        appState.txPendingDeleteId = null;
    }
}

/**
 * Budget Management Flow
 */
function openBudgetModal() {
    const catSelect = document.getElementById('budget-cat-select');
    const limitInput = document.getElementById('budget-limit-val');
    const monthInput = document.getElementById('budget-month-input');
    const errEl = document.getElementById('error-save-budget');

    if (errEl) errEl.style.display = 'none';
    if (limitInput) limitInput.value = '';
    if (monthInput) monthInput.value = appState.activeMonth;

    openModal('modal-budget');
}

function openEditBudgetModal(category, currentLimit, monthYear) {
    openBudgetModal();
    const catSelect = document.getElementById('budget-cat-select');
    const limitInput = document.getElementById('budget-limit-val');
    const monthInput = document.getElementById('budget-month-input');

    if (catSelect) catSelect.value = category;
    if (limitInput) limitInput.value = currentLimit;
    if (monthInput) monthInput.value = monthYear || appState.activeMonth;
}

async function handleSaveBudgetSubmit(e) {
    e.preventDefault();
    const errEl = document.getElementById('error-save-budget');
    if (errEl) errEl.style.display = 'none';

    const category = document.getElementById('budget-cat-select')?.value;
    const limitStr = document.getElementById('budget-limit-val')?.value?.trim();
    const monthYear = document.getElementById('budget-month-input')?.value?.trim();

    const limit = parseFloat(limitStr);
    if (!limitStr || isNaN(limit) || limit <= 0) {
        showFormError(errEl, 'Please enter a valid monthly limit amount greater than ₹0.');
        return;
    }
    if (!monthYear) {
        showFormError(errEl, 'Please select a valid month & year.');
        return;
    }

    try {
        const res = await API.setBudget(category, limit, monthYear);
        if (res && res.success) {
            closeModal('modal-budget');
            showToast(`Monthly budget for ${category} set to ${formatCurrency(limit)}.`);
            appState.activeMonth = monthYear;
            await refreshAllData();
        } else {
            showFormError(errEl, res?.message || 'Failed to save budget limit.');
        }
    } catch (err) {
        showFormError(errEl, 'Network or server error saving budget.');
    }
}

async function deleteCategoryBudget(budgetId) {
    if (!confirm('Are you sure you want to remove this category budget limit?')) return;

    try {
        const res = await API.deleteBudget(budgetId);
        if (res && res.success) {
            showToast('Budget limit removed.');
            await refreshAllData();
        } else {
            showToast('Failed to remove budget limit.', true);
        }
    } catch (e) {
        showToast('Server error deleting budget.', true);
    }
}

/**
 * =========================================================================
 * AUTOMATIC BUDGET EXCEEDED ALERT SYSTEM
 * =========================================================================
 * - Checks appState.budgets for exceeded categories (spentAmount > amountLimit)
 * - Queues multiple alerts; shows one at a time (no overlapping modals)
 * - Uses sessionStorage per-month to prevent repeated popups after refresh
 * - Clears sessionStorage for a category when it is no longer exceeded
 *   (so it can alert again if it exceeds the limit again in the same month)
 */

/** Internal queue of budget objects waiting to be shown */
const _alertQueue = [];
let _alertShowing = false;

function _getAlertedCategoriesKey() {
    return `exceeded_alerted_${appState.activeMonth}`;
}

function _getAlertedCategories() {
    try {
        return JSON.parse(sessionStorage.getItem(_getAlertedCategoriesKey()) || '[]');
    } catch (e) {
        return [];
    }
}

function _markCategoryAlerted(category) {
    try {
        const key = _getAlertedCategoriesKey();
        const list = _getAlertedCategories();
        if (!list.includes(category)) {
            list.push(category);
            sessionStorage.setItem(key, JSON.stringify(list));
        }
    } catch (e) { /* sessionStorage may be unavailable */ }
}

function _clearCategoryAlert(category) {
    try {
        const key = _getAlertedCategoriesKey();
        const list = _getAlertedCategories().filter(c => c !== category);
        sessionStorage.setItem(key, JSON.stringify(list));
    } catch (e) { /* sessionStorage may be unavailable */ }
}

function _buildAlertFromBudget(budget) {
    const over = budget.spentAmount - budget.amountLimit;
    const pct = budget.amountLimit > 0
        ? ((budget.spentAmount / budget.amountLimit) * 100)
        : 0;
    return {
        category: budget.category,
        budgetLimit: budget.amountLimit,
        spentAmount: budget.spentAmount,
        differenceAmount: over,
        thresholdPercent: pct,
        alertType: 'OVERSPENT',
        message: `You have spent ${formatCurrency(budget.spentAmount)} against a ` +
                 `${formatCurrency(budget.amountLimit)} budget for ${budget.category}. ` +
                 `You are ${formatCurrency(over)} over your monthly limit.`
    };
}

/**
 * Check all budgets and queue popup alerts for every newly-exceeded category.
 * Call this after refreshAllData() resolves so appState.budgets is fresh.
 */
function checkAndTriggerExceededBudgetAlerts() {
    if (!appState.budgets || !appState.budgets.length) return;

    const alerted = _getAlertedCategories();
    const exceededCategories = new Set(
        appState.budgets
            .filter(b => b.spentAmount > b.amountLimit)
            .map(b => b.category)
    );

    // Remove sessionStorage entries for categories that are no longer exceeded
    // so they can alert again if re-exceeded in the future this month
    alerted.forEach(cat => {
        if (!exceededCategories.has(cat)) {
            _clearCategoryAlert(cat);
        }
    });

    // Queue alerts for newly exceeded categories (not previously alerted this session)
    const currentAlerted = _getAlertedCategories(); // re-read after potential removals
    appState.budgets.forEach(budget => {
        if (
            budget.spentAmount > budget.amountLimit &&
            !currentAlerted.includes(budget.category)
        ) {
            _alertQueue.push(_buildAlertFromBudget(budget));
        }
    });

    _drainAlertQueue();
}

function _drainAlertQueue() {
    if (_alertShowing || _alertQueue.length === 0) return;
    const next = _alertQueue.shift();
    if (next) {
        _alertShowing = true;
        triggerAlertPopup(next);
    }
}

/**
 * Real-time Overspending Alert Pop
 */
function triggerAlertPopup(alert) {
    if (!alert) return;

    const modal = document.getElementById('modal-alert-popup');
    const card = document.getElementById('alert-pop-card-inner');
    const badge = document.getElementById('alert-pop-badge');
    const title = document.getElementById('alert-pop-title');
    const message = document.getElementById('alert-pop-message');
    const spentEl = document.getElementById('alert-pop-spent');
    const limitEl = document.getElementById('alert-pop-limit');
    const diffEl = document.getElementById('alert-pop-diff');
    const diffLbl = document.getElementById('alert-pop-diff-lbl');

    if (!modal) return;

    // Mark category as alerted in sessionStorage (prevent repeat on refresh)
    if (alert.category) {
        _markCategoryAlerted(alert.category);
    }

    const isOverspent = alert.alertType === 'OVERSPENT' || alert.spentAmount > alert.budgetLimit;

    if (card) card.classList.toggle('warning-pop', !isOverspent);
    if (badge) {
        badge.textContent = isOverspent 
            ? `OVER BUDGET (${(alert.thresholdPercent || 0).toFixed(1)}%)` 
            : `BUDGET WARNING (${(alert.thresholdPercent || 0).toFixed(1)}%)`;
    }
    if (title) {
        title.textContent = isOverspent 
            ? `${alert.category} Budget Exceeded!` 
            : `${alert.category} Budget Warning!`;
    }
    if (message) message.textContent = alert.message;
    if (spentEl) spentEl.textContent = formatCurrency(alert.spentAmount);
    if (limitEl) limitEl.textContent = formatCurrency(alert.budgetLimit);

    if (diffEl && diffLbl) {
        const diff = alert.differenceAmount;
        if (isOverspent) {
            diffLbl.textContent = 'Over Budget';
            diffEl.textContent = `+${formatCurrency(Math.abs(diff))}`;
        } else {
            diffLbl.textContent = 'Remaining';
            diffEl.textContent = formatCurrency(Math.abs(diff));
        }
    }

    // Wire "View Budget" button to navigate to Budgets tab
    const viewBudgetBtn = document.getElementById('alert-pop-view-budget');
    if (viewBudgetBtn) {
        viewBudgetBtn.onclick = () => {
            closeAlertPopup();
            switchTab('budgets');
        };
    }

    modal.classList.add('show');
}

function closeAlertPopup() {
    _alertShowing = false;
    closeModal('modal-alert-popup');
    // Show next queued alert (if any) after a short delay for smooth UX
    setTimeout(_drainAlertQueue, 350);
}

/**
 * CSV Export
 */
async function exportCsvData() {
    showToast('Preparing CSV spreadsheet...');
    try {
        const csvContent = await API.exportCsv();
        if (typeof csvContent === 'string' && csvContent.length > 0) {
            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `transactions_${new Date().toISOString().split('T')[0]}.csv`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
            showToast('CSV downloaded successfully!');
        } else {
            showToast('No transaction data to export.', true);
        }
    } catch (e) {
        showToast('Error exporting CSV.', true);
    }
}

/**
 * =========================================================================
 * HELPER FORMATTERS
 * =========================================================================
 */
function formatCurrency(val) {
    if (val === null || val === undefined || isNaN(val)) return '₹0.00';
    const num = Number(val);
    const sign = num < 0 ? '−₹' : '₹';
    const absVal = Math.abs(num);

    const parts = absVal.toFixed(2).split('.');
    let integerPart = parts[0];
    const decimalPart = parts[1];

    // Indian numbering format: last 3 digits, then groups of 2
    let lastThree = integerPart.substring(integerPart.length - 3);
    let otherNumbers = integerPart.substring(0, integerPart.length - 3);
    if (otherNumbers !== '') {
        lastThree = ',' + lastThree;
    }
    const formatted = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + '.' + decimalPart;
    return sign + formatted;
}

function formatNumber(val) {
    const num = Math.abs(Number(val) || 0);
    return num.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function formatDate(dateStr) {
    if (!dateStr) return '—';
    try {
        const parts = dateStr.split('-');
        if (parts.length === 3) {
            const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
            const d = parseInt(parts[2], 10);
            const m = months[parseInt(parts[1], 10) - 1] || parts[1];
            const y = parts[0];
            return `${d < 10 ? '0' + d : d} ${m} ${y}`;
        }
        return dateStr;
    } catch (e) {
        return dateStr;
    }
}

function formatMonthFriendly(monthStr) {
    if (!monthStr) return '';
    try {
        const parts = monthStr.split('-');
        const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
        const m = months[parseInt(parts[1], 10) - 1] || parts[1];
        return `${m} ${parts[0]}`;
    } catch (e) {
        return monthStr;
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function showToast(message, isError = false) {
    const container = document.getElementById('toast-container');
    if (!container) return;

    container.style.display = 'flex';
    const toast = document.createElement('div');
    toast.className = `toast${isError ? ' toast-error' : ''}`;
    toast.textContent = message;

    container.appendChild(toast);
    setTimeout(() => {
        toast.remove();
        if (!container.childElementCount) container.style.display = 'none';
    }, 2800);
}
