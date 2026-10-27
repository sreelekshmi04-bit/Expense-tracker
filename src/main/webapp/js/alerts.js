/**
 * Smart Spending Alert Notification Manager (Star Feature)
 */
document.addEventListener('DOMContentLoaded', () => {
    initAlertSystem();
});

function initAlertSystem() {
    loadAlertNotifications();

    const bellBtn = document.getElementById('btn-header-alert-bell');
    const modal = document.getElementById('modal-alerts-ledger');
    const closeBtns = document.querySelectorAll('.btn-close-alert-modal');
    const markAllBtn = document.getElementById('btn-mark-all-alerts-read');

    if (bellBtn) {
        bellBtn.addEventListener('click', (e) => {
            e.preventDefault();
            openAlertModal();
        });
    }

    closeBtns.forEach(btn => btn.addEventListener('click', closeAlertModal));

    if (markAllBtn) {
        markAllBtn.addEventListener('click', handleMarkAllAlertsRead);
    }

    // Refresh alerts whenever a transaction changes
    window.addEventListener('transaction-changed', loadAlertNotifications);
}

async function loadAlertNotifications() {
    const badgeEl = document.getElementById('header-alert-badge');
    const res = await API.getAlerts(true); // unread only for badge

    if (res.success) {
        const count = res.unreadCount || 0;
        if (badgeEl) {
            if (count > 0) {
                badgeEl.textContent = count;
                badgeEl.style.display = 'flex';
            } else {
                badgeEl.style.display = 'none';
            }
        }
    }
}

async function openAlertModal() {
    const modal = document.getElementById('modal-alerts-ledger');
    const listContainer = document.getElementById('alerts-modal-list');
    const emptyState = document.getElementById('alerts-modal-empty');

    if (!modal || !listContainer) return;

    modal.classList.add('open');
    listContainer.innerHTML = '<p style="text-align:center; padding:1.5rem; color:var(--ink-muted);">Checking alert registers...</p>';

    const res = await API.getAlerts(false); // all alerts
    if (!res.success) {
        listContainer.innerHTML = '<p style="color:var(--terracotta-700); padding:1rem;">Error loading alerts.</p>';
        return;
    }

    const alerts = res.alerts || [];
    listContainer.innerHTML = '';

    if (alerts.length === 0) {
        if (emptyState) emptyState.style.display = 'block';
        return;
    }

    if (emptyState) emptyState.style.display = 'none';

    alerts.forEach(a => {
        const isOverspent = a.alertType === 'OVERSPENT';
        const card = document.createElement('div');
        card.className = `alert-ledger-banner ${isOverspent ? '' : 'warning-level'}`;
        card.style.marginBottom = '0.75rem';
        card.innerHTML = `
            <div class="alert-banner-content">
                <svg class="alert-banner-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="12"></line>
                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                </svg>
                <div class="alert-banner-text">
                    <div style="display:flex; align-items:baseline; gap:0.5rem; margin-bottom:0.2rem;">
                        <h4>${escapeHtml(a.category)} (${escapeHtml(a.monthYear)})</h4>
                        <span class="envelope-status-badge ${isOverspent ? 'status-exceeded' : 'status-warning'}">${isOverspent ? 'Over Budget' : '80% Warning'}</span>
                    </div>
                    <p>${escapeHtml(a.message)}</p>
                    <div style="font-size:0.75rem; color:var(--ink-muted); margin-top:0.35rem;">
                        Recorded: ${escapeHtml(a.createdAt ? a.createdAt.substring(0, 16) : '')}
                    </div>
                </div>
            </div>
            <div class="alert-banner-actions">
                ${!a.isRead ? `<button class="btn btn-sm btn-secondary" onclick="acknowledgeAlertItem(${a.id})">Acknowledge</button>` : '<span style="font-size:0.75rem; color:var(--ink-muted);">Acknowledged</span>'}
            </div>
        `;
        listContainer.appendChild(card);
    });
}

function closeAlertModal() {
    const modal = document.getElementById('modal-alerts-ledger');
    if (modal) modal.classList.remove('open');
}

async function acknowledgeAlertItem(id) {
    const res = await API.acknowledgeAlert(id);
    if (res.success) {
        showToast('Alert acknowledged.');
        openAlertModal(); // Refresh modal view
        loadAlertNotifications(); // Refresh badge
    }
}

async function handleMarkAllAlertsRead() {
    const res = await API.acknowledgeAllAlerts();
    if (res.success) {
        showToast('All spending alerts marked as read.');
        openAlertModal();
        loadAlertNotifications();
    }
}
