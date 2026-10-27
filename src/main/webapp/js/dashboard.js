/**
 * Dashboard Controller & Telemetry Visualization
 */
document.addEventListener('DOMContentLoaded', async () => {
    if (!document.getElementById('dashboard-view')) return;

    loadDashboardData();

    // Refresh trigger if returning from modal
    window.addEventListener('transaction-changed', loadDashboardData);
});

async function loadDashboardData() {
    const res = await API.getDashboard();
    if (!res.success) {
        showToast(res.message || 'Failed to load dashboard', true);
        return;
    }

    renderMetrics(res.summary);
    renderRecentTransactions(res.recentTransactions);
    renderCategoryBreakdown(res.categoryBreakdown);
    renderMonthlyChart(res.monthlyComparison);
    renderAlertBanner(res.unreadAlerts);
}

function renderMetrics(summary) {
    if (!summary) return;

    const balanceEl = document.getElementById('metric-current-balance');
    const incomeEl = document.getElementById('metric-total-income');
    const expenseEl = document.getElementById('metric-total-expense');
    const savingsEl = document.getElementById('metric-monthly-savings');
    const savingsRateEl = document.getElementById('metric-savings-rate');

    if (balanceEl) balanceEl.textContent = formatCurrency(summary.currentBalance);
    if (incomeEl) incomeEl.textContent = formatCurrency(summary.totalIncome);
    if (expenseEl) expenseEl.textContent = formatCurrency(summary.totalExpense);
    if (savingsEl) savingsEl.textContent = formatCurrency(summary.monthlySavings);
    if (savingsRateEl) savingsRateEl.textContent = `${summary.savingsRate}% of income saved this month`;
}

function renderRecentTransactions(transactions) {
    const tableBody = document.getElementById('recent-transactions-tbody');
    const emptyState = document.getElementById('recent-empty-state');
    if (!tableBody) return;

    tableBody.innerHTML = '';

    if (!transactions || transactions.length === 0) {
        if (emptyState) emptyState.style.display = 'block';
        return;
    }

    if (emptyState) emptyState.style.display = 'none';

    transactions.forEach(tx => {
        const isIncome = tx.type === 'INCOME';
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="ledger-date-cell">${escapeHtml(tx.friendlyDate || tx.transactionDate)}</td>
            <td>
                <span class="category-tag ${isIncome ? 'cat-income' : 'cat-expense'}">
                    ${escapeHtml(tx.categoryOrSource)}
                </span>
            </td>
            <td class="ledger-desc" title="${escapeHtml(tx.description)}">
                ${escapeHtml(tx.description)}
            </td>
            <td class="num ${isIncome ? 'amount-income' : 'amount-expense'}" style="text-align: right;">
                ${isIncome ? '+' : '-'}${formatCurrency(tx.amount)}
            </td>
        `;
        tableBody.appendChild(tr);
    });
}

function renderCategoryBreakdown(categories) {
    const container = document.getElementById('category-breakdown-list');
    const emptyEl = document.getElementById('category-breakdown-empty');
    if (!container) return;

    container.innerHTML = '';

    if (!categories || categories.length === 0) {
        if (emptyEl) emptyEl.style.display = 'block';
        return;
    }

    if (emptyEl) emptyEl.style.display = 'none';

    categories.forEach(cat => {
        const item = document.createElement('div');
        item.className = 'budget-envelope-item';
        item.innerHTML = `
            <div class="envelope-header">
                <span class="envelope-category">${escapeHtml(cat.category)}</span>
                <span style="font-weight: 600; font-size: 0.88rem;">${formatCurrency(cat.totalAmount)}</span>
            </div>
            <div class="progress-track">
                <div class="progress-fill fill-safe" style="width: ${Math.min(cat.percentageOfTotal, 100)}%;"></div>
            </div>
            <div class="envelope-footer">
                <span>${cat.transactionCount} transactions</span>
                <span>${cat.percentageOfTotal}% of monthly spend</span>
            </div>
        `;
        container.appendChild(item);
    });
}

function renderMonthlyChart(comparisonMap) {
    const chartContainer = document.getElementById('monthly-comparison-chart');
    if (!chartContainer) return;

    if (!comparisonMap || Object.keys(comparisonMap).length === 0) {
        chartContainer.innerHTML = '<p class="text-muted" style="text-align:center; padding: 2rem;">No historical data available.</p>';
        return;
    }

    const months = Object.keys(comparisonMap);
    let maxVal = 1000;
    months.forEach(m => {
        const pair = comparisonMap[m];
        if (pair[0] > maxVal) maxVal = pair[0];
        if (pair[1] > maxVal) maxVal = pair[1];
    });

    const height = 180;
    const barWidth = 14;
    const groupGap = 42;
    const startX = 35;
    const totalWidth = Math.max(380, startX + months.length * groupGap);

    let barsSvg = '';

    months.forEach((m, idx) => {
        const pair = comparisonMap[m];
        const income = pair[0];
        const expense = pair[1];

        const incomeH = (income / maxVal) * (height - 40);
        const expenseH = (expense / maxVal) * (height - 40);

        const x = startX + idx * groupGap;
        const incomeY = height - 25 - incomeH;
        const expenseY = height - 25 - expenseH;

        // Month short label e.g. "Oct 26"
        const labelParts = m.split('-');
        const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
        const mLabel = monthNames[parseInt(labelParts[1], 10) - 1] || m;

        barsSvg += `
            <g class="chart-group" tabindex="0">
                <title>${m}: Income ${formatCurrency(income)}, Expense ${formatCurrency(expense)}</title>
                <!-- Income bar (Forest Green) -->
                <rect x="${x}" y="${incomeY}" width="${barWidth}" height="${incomeH}" rx="2" fill="#1E4631"></rect>
                <!-- Expense bar (Terracotta) -->
                <rect x="${x + barWidth + 3}" y="${expenseY}" width="${barWidth}" height="${expenseH}" rx="2" fill="#C85A32"></rect>
                <!-- Month Label -->
                <text x="${x + barWidth}" y="${height - 8}" text-anchor="middle" font-size="10" fill="#727E77">${mLabel}</text>
            </g>
        `;
    });

    chartContainer.innerHTML = `
        <svg viewBox="0 0 ${totalWidth} ${height}" style="width: 100%; height: auto; overflow: visible;">
            <!-- Grid lines -->
            <line x1="20" y1="${height - 25}" x2="${totalWidth}" y2="${height - 25}" stroke="#E2DBD0" stroke-width="1.5"></line>
            <line x1="20" y1="${(height - 25) / 2}" x2="${totalWidth}" y2="${(height - 25) / 2}" stroke="#E2DBD0" stroke-width="1" stroke-dasharray="3 3"></line>
            ${barsSvg}
        </svg>
        <div style="display: flex; justify-content: center; gap: 1.5rem; margin-top: 0.5rem; font-size: 0.78rem;">
            <span style="display: flex; align-items: center; gap: 0.35rem; color: #1E4631; font-weight: 600;">
                <span style="width: 10px; height: 10px; background: #1E4631; border-radius: 2px;"></span> Income
            </span>
            <span style="display: flex; align-items: center; gap: 0.35rem; color: #C85A32; font-weight: 600;">
                <span style="width: 10px; height: 10px; background: #C85A32; border-radius: 2px;"></span> Expenses
            </span>
        </div>
    `;
}

function renderAlertBanner(alerts) {
    const bannerContainer = document.getElementById('dashboard-alert-banner-container');
    if (!bannerContainer) return;

    bannerContainer.innerHTML = '';

    if (!alerts || alerts.length === 0) {
        bannerContainer.style.display = 'none';
        return;
    }

    bannerContainer.style.display = 'block';
    const topAlert = alerts[0]; // Most urgent
    const isOverspent = topAlert.alertType === 'OVERSPENT';

    const banner = document.createElement('div');
    banner.className = `alert-ledger-banner ${isOverspent ? '' : 'warning-level'}`;
    banner.innerHTML = `
        <div class="alert-banner-content">
            <svg class="alert-banner-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="12"></line>
                <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
            <div class="alert-banner-text">
                <h4>${isOverspent ? 'Overspending Detected in ' + escapeHtml(topAlert.category) : 'Budget Warning for ' + escapeHtml(topAlert.category)}</h4>
                <p>${escapeHtml(topAlert.message)}</p>
            </div>
        </div>
        <div class="alert-banner-actions">
            <button class="btn btn-sm btn-secondary" onclick="dismissBannerAlert(${topAlert.id})">Acknowledge</button>
            <a href="budgets.html" class="btn btn-sm btn-primary">Review Budgets</a>
        </div>
    `;
    bannerContainer.appendChild(banner);
}

async function dismissBannerAlert(alertId) {
    const res = await API.acknowledgeAlert(alertId);
    if (res.success) {
        showToast('Alert acknowledged.');
        loadDashboardData();
    }
}
