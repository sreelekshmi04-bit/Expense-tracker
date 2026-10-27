/**
 * Transaction Management & Ledger Folio
 */

const CATEGORIES_MAP = {
    INCOME: ['Salary', 'Scholarship', 'Freelance', 'Gift', 'Investment', 'Other'],
    EXPENSE: ['Food', 'Transport', 'Shopping', 'Bills', 'Education', 'Entertainment', 'Healthcare', 'Other']
};

let currentEditingId = null;

document.addEventListener('DOMContentLoaded', () => {
    // If not on a transaction-enabled page, return
    if (!document.getElementById('transactions-table-tbody') && !document.getElementById('modal-transaction-voucher')) return;

    initTransactionPage();
});

function initTransactionPage() {
    loadTransactions();

    // Attach Filter Listeners
    const filterType = document.getElementById('filter-type');
    const filterCat = document.getElementById('filter-category');
    const filterStart = document.getElementById('filter-start-date');
    const filterEnd = document.getElementById('filter-end-date');
    const resetBtn = document.getElementById('btn-reset-filters');

    if (filterType) filterType.addEventListener('change', () => { updateCategoryFilterOptions(); loadTransactions(); });
    if (filterCat) filterCat.addEventListener('change', loadTransactions);
    if (filterStart) filterStart.addEventListener('change', loadTransactions);
    if (filterEnd) filterEnd.addEventListener('change', loadTransactions);
    if (resetBtn) resetBtn.addEventListener('click', resetFilters);

    // Modal Triggers
    const openAddBtn = document.getElementById('btn-open-add-modal');
    if (openAddBtn) openAddBtn.addEventListener('click', () => openTransactionModal());

    const closeModalBtns = document.querySelectorAll('.btn-close-modal');
    closeModalBtns.forEach(btn => btn.addEventListener('click', closeTransactionModal));

    // Type Toggle inside Modal (Income vs Expense)
    const toggleIncome = document.getElementById('toggle-type-income');
    const toggleExpense = document.getElementById('toggle-type-expense');
    if (toggleIncome && toggleExpense) {
        toggleIncome.addEventListener('click', () => setModalTransactionType('INCOME'));
        toggleExpense.addEventListener('click', () => setModalTransactionType('EXPENSE'));
    }

    // Modal Form Submission
    const modalForm = document.getElementById('transaction-voucher-form');
    if (modalForm) {
        modalForm.addEventListener('submit', handleSaveTransaction);
    }
}

async function loadTransactions() {
    const tableBody = document.getElementById('transactions-table-tbody');
    const emptyState = document.getElementById('ledger-empty-state');
    const totalCountEl = document.getElementById('filtered-count');
    const netBalanceEl = document.getElementById('filtered-net-balance');

    if (!tableBody) return;

    const filters = {
        type: document.getElementById('filter-type')?.value || 'ALL',
        category: document.getElementById('filter-category')?.value || 'ALL',
        startDate: document.getElementById('filter-start-date')?.value || '',
        endDate: document.getElementById('filter-end-date')?.value || ''
    };

    const res = await API.getTransactions(filters);
    if (!res.success) {
        showToast(res.message || 'Failed to load ledger', true);
        return;
    }

    const txs = res.transactions || [];
    tableBody.innerHTML = '';

    if (totalCountEl) totalCountEl.textContent = `${txs.length} entries found`;
    if (netBalanceEl) netBalanceEl.textContent = formatCurrency(res.netBalance);

    if (txs.length === 0) {
        if (emptyState) emptyState.style.display = 'block';
        return;
    }

    if (emptyState) emptyState.style.display = 'none';

    txs.forEach(tx => {
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
            <td style="text-align: center;">
                <div class="row-actions" style="justify-content: center;">
                    <button class="action-btn" title="Edit Entry" onclick="editTransaction(${tx.id})">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                        </svg>
                    </button>
                    <button class="action-btn delete-btn" title="Delete Entry" onclick="confirmDeleteTransaction(${tx.id}, '${escapeHtml(tx.description)}')">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="3 6 5 6 21 6"></polyline>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                        </svg>
                    </button>
                </div>
            </td>
        `;
        tableBody.appendChild(tr);
    });
}

function updateCategoryFilterOptions() {
    const type = document.getElementById('filter-type')?.value || 'ALL';
    const catSelect = document.getElementById('filter-category');
    if (!catSelect) return;

    let options = '<option value="ALL">All Categories / Sources</option>';
    if (type === 'INCOME') {
        CATEGORIES_MAP.INCOME.forEach(c => options += `<option value="${c}">${c}</option>`);
    } else if (type === 'EXPENSE') {
        CATEGORIES_MAP.EXPENSE.forEach(c => options += `<option value="${c}">${c}</option>`);
    } else {
        options += '<optgroup label="Income Sources">';
        CATEGORIES_MAP.INCOME.forEach(c => options += `<option value="${c}">${c}</option>`);
        options += '</optgroup><optgroup label="Expense Categories">';
        CATEGORIES_MAP.EXPENSE.forEach(c => options += `<option value="${c}">${c}</option>`);
        options += '</optgroup>';
    }
    catSelect.innerHTML = options;
}

function resetFilters() {
    if (document.getElementById('filter-type')) document.getElementById('filter-type').value = 'ALL';
    if (document.getElementById('filter-start-date')) document.getElementById('filter-start-date').value = '';
    if (document.getElementById('filter-end-date')) document.getElementById('filter-end-date').value = '';
    updateCategoryFilterOptions();
    loadTransactions();
}

// Modal Voucher Handling
function openTransactionModal(editData = null) {
    const modal = document.getElementById('modal-transaction-voucher');
    const titleEl = document.getElementById('modal-voucher-title');
    const dateInput = document.getElementById('voucher-date');
    const amountInput = document.getElementById('voucher-amount');
    const descInput = document.getElementById('voucher-desc');

    if (!modal) return;

    currentEditingId = editData ? editData.id : null;

    if (editData) {
        if (titleEl) titleEl.textContent = 'Edit Ledger Entry';
        setModalTransactionType(editData.type);
        if (dateInput) dateInput.value = editData.transactionDate;
        if (amountInput) amountInput.value = editData.amount;
        if (descInput) descInput.value = editData.description;
        const catSelect = document.getElementById('voucher-category');
        if (catSelect) catSelect.value = editData.categoryOrSource;
    } else {
        if (titleEl) titleEl.textContent = 'Record New Entry';
        setModalTransactionType('EXPENSE'); // Default to expense
        if (dateInput) dateInput.value = new Date().toISOString().split('T')[0];
        if (amountInput) amountInput.value = '';
        if (descInput) descInput.value = '';
    }

    modal.classList.add('open');
}

function closeTransactionModal() {
    const modal = document.getElementById('modal-transaction-voucher');
    if (modal) modal.classList.remove('open');
    currentEditingId = null;
}

let activeModalType = 'EXPENSE';

function setModalTransactionType(type) {
    activeModalType = type;
    const toggleIncome = document.getElementById('toggle-type-income');
    const toggleExpense = document.getElementById('toggle-type-expense');
    const labelCat = document.getElementById('label-voucher-category');
    const catSelect = document.getElementById('voucher-category');

    if (type === 'INCOME') {
        toggleIncome?.classList.add('active', 'income');
        toggleExpense?.classList.remove('active', 'expense');
        if (labelCat) labelCat.textContent = 'Income Source';
        if (catSelect) {
            catSelect.innerHTML = CATEGORIES_MAP.INCOME.map(c => `<option value="${c}">${c}</option>`).join('');
        }
    } else {
        toggleExpense?.classList.add('active', 'expense');
        toggleIncome?.classList.remove('active', 'income');
        if (labelCat) labelCat.textContent = 'Expense Category';
        if (catSelect) {
            catSelect.innerHTML = CATEGORIES_MAP.EXPENSE.map(c => `<option value="${c}">${c}</option>`).join('');
        }
    }
}

async function handleSaveTransaction(e) {
    e.preventDefault();
    const amount = document.getElementById('voucher-amount')?.value;
    const date = document.getElementById('voucher-date')?.value;
    const desc = document.getElementById('voucher-desc')?.value;
    const cat = document.getElementById('voucher-category')?.value;

    const payload = {
        type: activeModalType,
        amount,
        date,
        description: desc,
        categoryOrSource: cat
    };

    let res;
    if (currentEditingId) {
        payload.id = currentEditingId;
        res = await API.updateTransaction(payload);
    } else {
        res = await API.addTransaction(payload);
    }

    if (res.success) {
        showToast(res.message);
        closeTransactionModal();
        loadTransactions();
        window.dispatchEvent(new CustomEvent('transaction-changed'));
    } else {
        showToast(res.message || 'Validation error', true);
    }
}

async function editTransaction(id) {
    const res = await API.getTransaction(id);
    if (res.success && res.transaction) {
        openTransactionModal(res.transaction);
    } else {
        showToast(res.message || 'Failed to retrieve transaction details', true);
    }
}

function confirmDeleteTransaction(id, desc) {
    if (confirm(`Are you sure you want to delete "${desc}" from the ledger? This will permanently affect your balance and recalculate spending alerts.`)) {
        deleteTransaction(id);
    }
}

async function deleteTransaction(id) {
    const res = await API.deleteTransaction(id);
    if (res.success) {
        showToast(res.message);
        loadTransactions();
        window.dispatchEvent(new CustomEvent('transaction-changed'));
    } else {
        showToast(res.message || 'Failed to delete transaction', true);
    }
}
