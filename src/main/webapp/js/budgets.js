/**
 * Budget Management & Envelope System
 */
document.addEventListener('DOMContentLoaded', () => {
    if (!document.getElementById('budgets-view')) return;

    initBudgetPage();
});

function initBudgetPage() {
    const monthPicker = document.getElementById('budget-month-picker');
    if (monthPicker) {
        // Set default to current month YYYY-MM
        const now = new Date();
        const curYm = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
        monthPicker.value = curYm;
        monthPicker.addEventListener('change', loadBudgets);
    }

    loadBudgets();

    // Modal Triggers
    const btnSetBudget = document.getElementById('btn-open-set-budget-modal');
    if (btnSetBudget) btnSetBudget.addEventListener('click', openBudgetModal);

    const closeModalBtns = document.querySelectorAll('.btn-close-budget-modal');
    closeModalBtns.forEach(btn => btn.addEventListener('click', closeBudgetModal));

    const form = document.getElementById('set-budget-form');
    if (form) form.addEventListener('submit', handleSaveBudget);
}

async function loadBudgets() {
    const monthPicker = document.getElementById('budget-month-picker');
    const month = monthPicker ? monthPicker.value : '';

    const res = await API.getBudgets(month);
    if (!res.success) {
        showToast(res.message || 'Failed to load budgets', true);
        return;
    }

    renderBudgetsSummary(res);
    renderBudgetCards(res.budgets, res.month);
}

function renderBudgetsSummary(data) {
    const totalBudgetedEl = document.getElementById('summary-total-budgeted');
    const totalSpentEl = document.getElementById('summary-total-spent-budgeted');
    const friendlyMonthEl = document.getElementById('budget-friendly-month');

    if (totalBudgetedEl) totalBudgetedEl.textContent = formatCurrency(data.totalBudgeted);
    if (totalSpentEl) totalSpentEl.textContent = formatCurrency(data.totalSpentInBudgets);
    if (friendlyMonthEl) friendlyMonthEl.textContent = data.friendlyMonth || data.month;
}

function renderBudgetCards(budgets, currentMonth) {
    const container = document.getElementById('budget-envelopes-grid');
    const emptyState = document.getElementById('budget-empty-state');
    if (!container) return;

    container.innerHTML = '';

    if (!budgets || budgets.length === 0) {
        if (emptyState) emptyState.style.display = 'block';
        return;
    }

    if (emptyState) emptyState.style.display = 'none';

    budgets.forEach(b => {
        let statusClass = 'status-safe';
        let fillClass = 'fill-safe';
        let statusLabel = 'Safe';

        if (b.status === 'EXCEEDED') {
            statusClass = 'status-exceeded';
            fillClass = 'fill-exceeded';
            statusLabel = 'Exceeded Limit';
        } else if (b.status === 'WARNING') {
            statusClass = 'status-warning';
            fillClass = 'fill-warning';
            statusLabel = 'Warning (80%+)';
        }

        const percentage = Math.min(b.percentageUsed, 100);
        const isOverspent = b.spentAmount > b.amountLimit;
        const diffAmount = Math.abs(b.remainingAmount);

        const card = document.createElement('div');
        card.className = 'content-card';
        card.style.marginBottom = '1.25rem';
        card.innerHTML = `
            <div class="card-header-bar">
                <div class="card-title-group">
                    <h3 style="font-size: 1.1rem; display: flex; align-items: center; gap: 0.5rem;">
                        ${escapeHtml(b.category)}
                        <span class="envelope-status-badge ${statusClass}">${statusLabel}</span>
                    </h3>
                    <p>Budget for ${escapeHtml(b.monthYear)}</p>
                </div>
                <div class="row-actions">
                    <button class="action-btn" title="Edit Budget" onclick="quickEditBudget('${escapeHtml(b.category)}', ${b.amountLimit}, '${escapeHtml(b.monthYear)}')">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                        </svg>
                    </button>
                    <button class="action-btn delete-btn" title="Delete Budget" onclick="confirmDeleteBudget(${b.budgetId}, '${escapeHtml(b.category)}')">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="3 6 5 6 21 6"></polyline>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                        </svg>
                    </button>
                </div>
            </div>
            <div class="card-body">
                <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 0.5rem;">
                    <div>
                        <span style="font-size: 0.74rem; text-transform: uppercase; color: var(--ink-muted); font-weight: 700;">Spent</span>
                        <div class="amount ${isOverspent ? 'amount-expense' : ''}" style="font-size: 1.35rem; font-weight: 600;">
                            ${formatCurrency(b.spentAmount)}
                        </div>
                    </div>
                    <div style="text-align: right;">
                        <span style="font-size: 0.74rem; text-transform: uppercase; color: var(--ink-muted); font-weight: 700;">Monthly Limit</span>
                        <div class="amount" style="font-size: 1.15rem; color: var(--ink-muted);">
                            ${formatCurrency(b.amountLimit)}
                        </div>
                    </div>
                </div>

                <div class="progress-track" style="height: 9px;">
                    <div class="progress-fill ${fillClass}" style="width: ${percentage}%;"></div>
                </div>

                <div style="display: flex; justify-content: space-between; font-size: 0.82rem; margin-top: 0.65rem;">
                    <span style="color: var(--ink-muted);">
                        ${b.percentageUsed}% utilized
                    </span>
                    <span style="font-weight: 600; color: ${isOverspent ? 'var(--terracotta-700)' : 'var(--forest-800)'};">
                        ${isOverspent ? 'Exceeded by ' + formatCurrency(diffAmount) : formatCurrency(diffAmount) + ' remaining'}
                    </span>
                </div>
            </div>
        `;
        container.appendChild(card);
    });
}

function openBudgetModal() {
    const modal = document.getElementById('modal-set-budget');
    const monthPicker = document.getElementById('budget-month-picker');
    const targetMonth = document.getElementById('budget-target-month');

    if (targetMonth && monthPicker) {
        targetMonth.value = monthPicker.value;
    }

    if (modal) modal.classList.add('open');
}

function closeBudgetModal() {
    const modal = document.getElementById('modal-set-budget');
    if (modal) modal.classList.remove('open');
}

function quickEditBudget(category, limit, monthYear) {
    const catSelect = document.getElementById('budget-category-select');
    const limitInput = document.getElementById('budget-limit-input');
    const monthInput = document.getElementById('budget-target-month');

    if (catSelect) catSelect.value = category;
    if (limitInput) limitInput.value = limit;
    if (monthInput) monthInput.value = monthYear;

    openBudgetModal();
}

async function handleSaveBudget(e) {
    e.preventDefault();
    const category = document.getElementById('budget-category-select')?.value;
    const amountLimit = document.getElementById('budget-limit-input')?.value;
    const monthYear = document.getElementById('budget-target-month')?.value;

    const res = await API.setBudget(category, amountLimit, monthYear);
    if (res.success) {
        showToast(res.message);
        closeBudgetModal();
        loadBudgets();
    } else {
        showToast(res.message || 'Failed to set budget', true);
    }
}

function confirmDeleteBudget(budgetId, category) {
    if (confirm(`Remove the monthly budget envelope for "${category}"? Alerts for this category will be cleared.`)) {
        deleteBudget(budgetId);
    }
}

async function deleteBudget(id) {
    const res = await API.deleteBudget(id);
    if (res.success) {
        showToast(res.message);
        loadBudgets();
    } else {
        showToast(res.message || 'Failed to delete budget', true);
    }
}
