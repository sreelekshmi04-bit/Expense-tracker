/**
 * Reports, Financial Auditing & CSV Export
 */
document.addEventListener('DOMContentLoaded', () => {
    if (!document.getElementById('reports-view')) return;

    initReportsPage();
});

function initReportsPage() {
    loadReportData();

    // Attach Filter Listeners
    const filterMonth = document.getElementById('report-filter-month');
    const filterType = document.getElementById('report-filter-type');
    const filterCat = document.getElementById('report-filter-category');
    const filterStart = document.getElementById('report-filter-start');
    const filterEnd = document.getElementById('report-filter-end');
    const applyBtn = document.getElementById('btn-apply-report-filters');
    const exportCsvBtn = document.getElementById('btn-export-csv');

    if (applyBtn) applyBtn.addEventListener('click', loadReportData);
    if (filterMonth) filterMonth.addEventListener('change', () => {
        // Clear custom dates if selecting month
        if (filterMonth.value !== 'ALL' && filterStart && filterEnd) {
            filterStart.value = '';
            filterEnd.value = '';
        }
        loadReportData();
    });
    if (filterType) filterType.addEventListener('change', loadReportData);
    if (filterCat) filterCat.addEventListener('change', loadReportData);

    if (exportCsvBtn) {
        exportCsvBtn.addEventListener('click', handleExportCsv);
    }
}

function getReportFilters() {
    return {
        month: document.getElementById('report-filter-month')?.value || 'ALL',
        type: document.getElementById('report-filter-type')?.value || 'ALL',
        category: document.getElementById('report-filter-category')?.value || 'ALL',
        startDate: document.getElementById('report-filter-start')?.value || '',
        endDate: document.getElementById('report-filter-end')?.value || ''
    };
}

async function loadReportData() {
    const filters = getReportFilters();
    const res = await API.getReport(filters);

    if (!res.success) {
        showToast(res.message || 'Error generating report', true);
        return;
    }

    renderReportSummary(res);
    renderReportTable(res.transactions);
    renderCategoryVisual(res.transactions);
}

function renderReportSummary(data) {
    const incEl = document.getElementById('report-total-income');
    const expEl = document.getElementById('report-total-expense');
    const netEl = document.getElementById('report-net-savings');
    const countEl = document.getElementById('report-entry-count');

    if (incEl) incEl.textContent = formatCurrency(data.totalIncome);
    if (expEl) expEl.textContent = formatCurrency(data.totalExpense);
    if (netEl) {
        netEl.textContent = formatCurrency(data.netSavings);
        netEl.className = `metric-value ${data.netSavings >= 0 ? 'income-color' : 'expense-color'}`;
    }
    if (countEl) countEl.textContent = `${data.count} items in ledger audit`;
}

function renderReportTable(transactions) {
    const tbody = document.getElementById('report-transactions-tbody');
    const emptyEl = document.getElementById('report-empty-state');
    if (!tbody) return;

    tbody.innerHTML = '';

    if (!transactions || transactions.length === 0) {
        if (emptyEl) emptyEl.style.display = 'block';
        return;
    }

    if (emptyEl) emptyEl.style.display = 'none';

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
        tbody.appendChild(tr);
    });
}

function renderCategoryVisual(transactions) {
    const container = document.getElementById('report-category-chart');
    if (!container) return;

    if (!transactions || transactions.length === 0) {
        container.innerHTML = '<p class="text-muted" style="text-align: center; padding: 2rem;">No data to analyze.</p>';
        return;
    }

    // Aggregate category totals for expenses
    const categoryTotals = {};
    let totalExpense = 0;

    transactions.forEach(tx => {
        if (tx.type === 'EXPENSE') {
            const cat = tx.categoryOrSource || 'Other';
            categoryTotals[cat] = (categoryTotals[cat] || 0) + tx.amount;
            totalExpense += tx.amount;
        }
    });

    const entries = Object.entries(categoryTotals).sort((a, b) => b[1] - a[1]);

    if (entries.length === 0) {
        container.innerHTML = '<p class="text-muted" style="text-align: center; padding: 2rem;">No expense entries in filtered range.</p>';
        return;
    }

    let html = '<div style="display: flex; flex-direction: column; gap: 0.85rem;">';
    entries.forEach(([cat, amount]) => {
        const pct = Math.round((amount / totalExpense) * 100);
        html += `
            <div>
                <div style="display: flex; justify-content: space-between; font-size: 0.84rem; margin-bottom: 0.35rem;">
                    <span style="font-weight: 600; color: var(--ink-solid);">${escapeHtml(cat)}</span>
                    <span style="font-weight: 600;">${formatCurrency(amount)} <span style="font-size: 0.75rem; color: var(--ink-muted);">(${pct}%)</span></span>
                </div>
                <div class="progress-track" style="height: 6px; margin: 0;">
                    <div class="progress-fill fill-safe" style="width: ${pct}%; background-color: var(--forest-700);"></div>
                </div>
            </div>
        `;
    });
    html += '</div>';

    container.innerHTML = html;
}

function handleExportCsv() {
    const filters = getReportFilters();
    showToast('Exporting transaction ledger to CSV...');
    API.downloadReportCsv(filters);
}
