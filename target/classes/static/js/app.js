// API Base URL
const API_BASE_URL = '/api/invoices';

// Theme Management
const themeToggle = document.getElementById('themeToggle');
const body = document.body;

const savedTheme = localStorage.getItem('theme') || 'light';
body.setAttribute('data-theme', savedTheme);
updateThemeIcon(savedTheme);

if (themeToggle) {
    themeToggle.addEventListener('click', () => {
        const currentTheme = body.getAttribute('data-theme');
        const newTheme = currentTheme === 'light' ? 'dark' : 'light';
        body.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        updateThemeIcon(newTheme);
    });
}

function updateThemeIcon(theme) {
    if (!themeToggle) return;
    const icon = themeToggle.querySelector('i');
    if (theme === 'dark') {
        icon.setAttribute('data-lucide', 'sun');
    } else {
        icon.setAttribute('data-lucide', 'moon');
    }
    if (window.lucide) lucide.createIcons();
}

// Data Fetching and UI Rendering (Index Page)
const invoiceTableBody = document.getElementById('invoiceTableBody');
if (invoiceTableBody) {
    fetchInvoices();
}

async function fetchInvoices() {
    try {
        const response = await fetch(API_BASE_URL);
        const invoices = await response.json();
        renderInvoices(invoices);
    } catch (error) {
        console.error('Error fetching invoices:', error);
        invoiceTableBody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--danger-color);">Error loading invoices. Please check if the server is running.</td></tr>`;
    }
}

function renderInvoices(invoices) {
    if (invoices.length === 0) {
        invoiceTableBody.innerHTML = `<tr><td colspan="6" style="text-align: center; padding: 2rem; color: var(--text-muted);">No invoices found.</td></tr>`;
        return;
    }

    invoiceTableBody.innerHTML = invoices.map(invoice => `
        <tr>
            <td style="font-weight: 600;">#${invoice.invoiceNo}</td>
            <td>${invoice.emailId || 'N/A'}</td>
            <td>${invoice.serviceDetails || 'N/A'}</td>
            <td>${invoice.dateOfService ? new Date(invoice.dateOfService).toLocaleDateString() : 'N/A'}</td>
            <td style="font-weight: 600;">₹${(invoice.amountPayable || 0).toFixed(2)}</td>
            <td class="actions">
                <button class="action-btn" onclick="downloadPdf(${invoice.id})" title="Download PDF">
                    <i data-lucide="download"></i>
                </button>
                <button class="action-btn" onclick="sendEmail(${invoice.id})" title="Send via Email">
                    <i data-lucide="mail"></i>
                </button>
                <button class="action-btn delete" onclick="deleteInvoice(${invoice.id})" title="Delete">
                    <i data-lucide="trash-2"></i>
                </button>
            </td>
        </tr>
    `).join('');
    
    if (window.lucide) lucide.createIcons();
}

// Invoice Creation Logic
const createInvoiceForm = document.getElementById('createInvoiceForm');
const qtyInput = document.getElementById('qty');
const costInput = document.getElementById('costPerQty');
const totalDisplay = document.getElementById('calculatedTotal');

if (createInvoiceForm) {
    [qtyInput, costInput].forEach(input => {
        input.addEventListener('input', calculateTotal);
    });

    createInvoiceForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const invoiceData = {
            invoiceNo: document.getElementById('invoiceNo').value,
            estimatedId: document.getElementById('estimatedId').value,
            chainId: document.getElementById('chainId').value,
            emailId: document.getElementById('emailId').value,
            dateOfService: document.getElementById('dateOfService').value,
            deliveryDetails: document.getElementById('deliveryDetails').value,
            serviceDetails: document.getElementById('serviceDetails').value,
            qty: document.getElementById('qty').value,
            costPerQty: document.getElementById('costPerQty').value,
            dateOfPayment: new Date().toISOString() // Default to now
        };

        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(invoiceData)
            });

            if (response.ok) {
                alert('Invoice created successfully!');
                window.location.href = 'index.html';
            } else {
                alert('Failed to create invoice.');
            }
        } catch (error) {
            console.error('Error creating invoice:', error);
            alert('An error occurred. Is the backend running?');
        }
    });

    function calculateTotal() {
        const qty = parseFloat(qtyInput.value) || 0;
        const cost = parseFloat(costInput.value) || 0;
        const subtotal = qty * cost;
        const total = subtotal * 1.18; // 18% GST
        totalDisplay.textContent = `₹${total.toFixed(2)}`;
    }
}

// Utility Actions
async function downloadPdf(id) {
    window.open(`${API_BASE_URL}/${id}/pdf`, '_blank');
}

async function sendEmail(id) {
    const btn = event.currentTarget;
    const originalIcon = btn.innerHTML;
    btn.innerHTML = '<i data-lucide="loader" class="spin"></i>';
    if (window.lucide) lucide.createIcons();

    try {
        const response = await fetch(`${API_BASE_URL}/${id}/send-email`, { method: 'POST' });
        const result = await response.text();
        alert(result);
    } catch (error) {
        console.error('Error sending email:', error);
        alert('Failed to send email.');
    } finally {
        btn.innerHTML = originalIcon;
        if (window.lucide) lucide.createIcons();
    }
}

async function deleteInvoice(id) {
    if (!confirm('Are you sure you want to delete this invoice?')) return;

    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, { method: 'DELETE' });
        if (response.ok) {
            fetchInvoices();
        } else {
            alert('Failed to delete invoice.');
        }
    } catch (error) {
        console.error('Error deleting invoice:', error);
    }
}

// Add spinning animation for loader
const style = document.createElement('style');
style.textContent = `
    @keyframes spin {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
    }
    .spin {
        animation: spin 1s linear infinite;
    }
`;
document.head.appendChild(style);
