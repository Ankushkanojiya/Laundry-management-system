
//   Login  🔒

const BASE_URL = 'http://localhost:8080';


document.addEventListener('DOMContentLoaded', loadStats());
document.addEventListener('DOMContentLoaded', populateCustomerFilter());


async function loadStats() {

    try {
        const response = await fetch(`${BASE_URL}/api/stats`);
        const data = await response.json();

        document.getElementById('pending-count').textContent = data.pendingOrders;
        document.getElementById('business-revenue').textContent = `₹${data.businessRevenueToday.toFixed(2)}`;
        document.getElementById('daily-revenue').textContent = `₹${data.revenueToday.toFixed(2)}`;

    } catch (error) {
        console.error("Something Going wrong", error);
    }
}

// Login function
async function login() {
    console.log("Login initiated....");
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();

    if (!username || !password) {
        showMessage('Please enter credentials', 'error', 'login-message');
        return;
    }

    try {
        const response = await fetch(`${BASE_URL}/api/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            throw new Error('Login failed - Incorrect username or password ' + response.status);
        }

        // Only execute if login is successful
        document.getElementById('login-section').classList.add('hidden');
        document.getElementById('admin-dashboard').classList.remove('hidden');
        hideAllSections(); // Hide all sections initially
        await refreshCustomers(); // Load initial customer data
    } catch (error) {
        showMessage(error.message, 'error', 'login-message');
        console.error('Login error:', error);
    }
}

//  <!--    Add new customer  🧑➕➕➕-->
async function addCustomer() {

    const name = document.getElementById('customer-name').value.trim();
    const phone = document.getElementById('customer-phone').value.trim();


    if (!name || !phone) {
        showMessage('Please fill all fields', 'error');
        return;
    }

    if (!/^\d{10}$/.test(phone)) {
        showMessage('Phone must be 10 digits', 'error');
        return;
    }
    try {
        const response = await fetch(`${BASE_URL}/api/customers`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, phoneNumber: phone })
        });

        const responseData = await response.text()

        if (!response.ok) {
            if (response.status === 400 && responseData.includes("already exists")) {
                showMessage("The number is already exists", 'error');
                highlightDuplicatePhone();
                console.warn("Handled 400 error");
                return;
            }
            throw new Error(responseData || 'Failed to add customer');


        }
        showMessage('Customer added successfully', 'success');
        resetForm();
        await refreshCustomers();
    } catch (error) {
        if (error.message.includes("already exists")) {
            showMessage('Phone number already registered!', 'error');
            highlightDuplicatePhone();
        } else {
            showMessage(error.message, 'error');
        }
    }
}
function highlightDuplicatePhone() {
    const phoneInput = document.getElementById('customer-phone');
    phoneInput.classList.add('input-error');


    // Clear error after 3 seconds
    setTimeout(() => {
        phoneInput.classList.remove('input-error');
    }, 3000);
}

// <!--   Edit customer  ✍️✍️✍️-->
let currentCustomerId = null;
async function editCustomer(id) {
    try {
        showMessage('', 'clear', 'edit-customer-message');
        const response = await fetch(`${BASE_URL}/api/customers/${id}`);
        if (!response.ok) throw new Error('Failed to fetch customer');

        const customer = await response.json();
        document.getElementById('edit-customer-name').value = customer.name;
        document.getElementById('edit-customer-phone').value = customer.phoneNumber;
        currentCustomerId = id;

        document.getElementById('edit-customer-form').classList.remove('hidden');
        document.getElementById('edit-customer-name').value = customer.name;
        document.getElementById('edit-customer-phone').value = customer.phoneNumber;
    } catch (error) {
        showMessage(error.message, 'error');
    }
}

// <!--   Update customer 🛠️🛠️🛠️🛠️🛠️🛠️  -->
async function updateCustomer() {

    const name = document.getElementById('edit-customer-name').value.trim();
    const phone = document.getElementById('edit-customer-phone').value.trim();
    try {

        const response = await fetch(`${BASE_URL}/api/customers/${currentCustomerId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, phoneNumber: phone })
        });

        if (!response.ok) {
            throw new Error('Failed to update customer');
        }

        // Show success message and hide form after 2 seconds
        showMessage('Customer updated successfully', 'success', 'edit-customer-message');
        setTimeout(() => {
            document.getElementById('edit-customer-form').classList.add('hidden');
            showMessage('', 'clear', 'edit-customer-message');
            refreshCustomers();
        }, 2000);


    } catch (error) {
        showMessage(error.message, 'error', 'edit-customer-message');
    }
}

// Delete customer  ️🗑️🗑️🗑️
async function deleteCustomer(id) {
    if (!confirm('Are you sure you want to delete this customer?')) return;

    try {
        const response = await fetch(`${BASE_URL}/api/customers/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Failed to delete customer');
        }

        showMessage('Customer deleted successfully', 'success');
        await refreshCustomers();
    } catch (error) {
        showMessage(error.message, 'error');
    }
    loadStats();
}

//  Refresh customer list 🔄🔄🔄🔄

async function refreshCustomers() {
    try {
        const response = await fetch(`${BASE_URL}/api/customers`);
        if (!response.ok) throw new Error('Failed to load customers');


        const customers = await response.json();
        const tableBody = document.querySelector('#customer-table tbody');
        if (customers.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" style="text-align:center">No Customers found</td></tr>`;
            return;
        }
        tableBody.innerHTML = customers.map(customer => `
                <tr>
                    <td>${customer.id}</td>
                    <td>${customer.name}</td>
                    <td>${customer.phoneNumber}</td>
                    <td>
                        <button onclick="editCustomer(${customer.id})">Edit</button>
                        <button onclick="deleteCustomer(${customer.id})">Delete</button>
                        <button class="history-btn" 
                        onclick="viewOrders(${customer.id}, '${customer.name}')">📜 Orders</button>
                        <button onClick="viewTransactions(${customer.id}, '${customer.name}')"> Transactions</button>
                    </td>
                </tr>
            `).join('');
    } catch (error) {
        showMessage(error.message, 'error');
    }
}

//  Cancel Edit ❌❌❌
function cancelEdit() {
    document.getElementById('edit-customer-form').classList.add('hidden');
    currentCustomerId = null;
}

//  Reset form
function resetForm() {
    document.getElementById('customer-name').value = '';
    document.getElementById('customer-phone').value = '';
}

//   Show status message
function showMessage(message, type = 'info', elementId = 'action-message') {
    const element = document.getElementById(elementId);
    element.textContent = message;
    element.className = type;

    // Auto-clear success messages after 3 seconds
    if (type === 'success') {
        setTimeout(() => {
            element.textContent = '';
            element.className = '';
        }, 3000);
    }
}


function showTakeOrder() {
    hideAllSections();
    document.getElementById('take-order-section').classList.remove('hidden');
    loadCustomersForOrder(); // Load customers in dropdown
}

//  populate the customer in Take order
async function loadCustomersForOrder() {
    try {
        const response = await fetch(`${BASE_URL}/api/customers`);
        const customers = await response.json();
        const select = document.getElementById('customer-select', '');

        select.innerHTML = '<option value="">Select Customer</option>';
        customers.forEach(customer => {
            const option = document.createElement('option');
            option.value = customer.id;
            option.textContent = `${customer.name} (${customer.phoneNumber})`;
            select.appendChild(option);
        });
    } catch (error) {
        showMessage('Failed to load customers', 'error');
    }
}
//  Submit the order ☑️☑️
async function submitOrder() {
    const customerSelect = document.getElementById('customer-select');
    const clothCountInput = document.getElementById('cloth-count');
    const serviceType = document.querySelector('input[name="serviceType"]:checked').value;

    if (!customerSelect.value || !clothCountInput.value) {
        showMessage('Please select customer and enter cloth count', 'error');
        return;
    }

    try {
        const response = await fetch(`${BASE_URL}/api/orders`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                customerId: parseInt(customerSelect.value),
                totalClothes: parseInt(clothCountInput.value),
                serviceType: serviceType
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to create order');
        }

        const order = await response.json();
        showOrderPopup(order);
        resetOrderForm();
        loadStats();
    } catch (error) {
        showMessage(error.message, 'error');
    }
}
//  Receipt 🧾🧾🧾
function showOrderPopup(order) {
    const orderDate = new Date(order.orderDate).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
    document.getElementById('popup-order-id').textContent = order.id;
    document.getElementById('popup-customer-name').textContent = order.customerName;
    document.getElementById('popup-customer-phone').textContent = order.customerPhone;
    document.getElementById('popup-cloth-count').textContent = order.totalClothes;
    document.getElementById('popup-total-amount').textContent = order.totalAmount;
    document.getElementById('popup-order-date').textContent = orderDate;
    document.getElementById('popup-status').textContent = order.status;
    document.getElementById('order-popup').classList.remove('hidden');
}

function closeOrderPopup() {
    document.getElementById('order-popup').classList.add('hidden');
}

function resetOrderForm() {
    document.getElementById('customer-select').value = '';
    document.getElementById('cloth-count').value = '';
}


function addNavigationButtons() {
    const nav = document.createElement('div');
    nav.innerHTML = `
            <div class="navigation">
                <button onclick="showCustomerManagement()">Manage Customers</button>
                <button onclick="showTakeOrder()">Take Order</button>
            </div>
        `;
    document.getElementById('admin-dashboard').prepend(nav);
}

function showCustomerManagement() {
    document.getElementById('order-section').classList.add('hidden');
    document.getElementById('customer-table').classList.remove('hidden');
}

function showAddCustomer() {
    hideAllSections();
    document.getElementById('add-customer-section').classList.remove('hidden');
}

function showManageCustomers() {
    hideAllSections();
    document.getElementById('manage-customers-section').classList.remove('hidden');
    refreshCustomers();
}

function hideAllSections() {
    const sections = document.querySelectorAll('#admin-dashboard > div:not(.nav-buttons)');
    sections.forEach(section => section.classList.add('hidden'));
}

async function showManageOrders() {
    hideAllSections();
    document.getElementById('manage-order-section').classList.remove('hidden');
    await populateCustomerFilter()
    await refreshOrders();
}



function gatherFilterValues() {
    return {
        status: document.getElementById('filter-status').value,
        customerId: document.getElementById('filter-customer').value,
        startDate: document.getElementById('filter-start-date').value,
        endDate: document.getElementById('filter-end-date').value
    };
}

async function applyFilters() {
    refreshOrders();
}
async function clearFilters() {
    document.getElementById('filter-status').value = '';
    document.getElementById('filter-customer').value = '';
    document.getElementById('filter-start-date').value = '';
    document.getElementById('filter-end-date').value = '';
    refreshOrders();
}
async function refreshOrders() {
    try {

        const filters = gatherFilterValues();

        // this is something new which converts objects into array of [key,value]
        const cleanFilters = Object.fromEntries(
            Object.entries(filters).filter(([_, v]) => v !== '' && v !== null)
        );



        const response = await fetch(`${BASE_URL}/api/orders/filter?${new URLSearchParams(cleanFilters)}`);
        if (!response.ok) throw new Error('Failed to load orders');

        const orders = await response.json();
        const tbody = document.querySelector('#orders-table tbody');
        tbody.innerHTML = '';

        if (orders.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" style="text-align:center">No orders found</td></tr>`;
            return;
        }

        orders.forEach(order => {

            let statusCell;
            switch (order.status) {
                case 'PENDING':
                    statusCell = `
                        <button class="status-btn pending"
                                onclick="updateStatus(${order.id}, 'IN_PROGRESS')">
                            Start
                        </button>`;
                    break;
                case 'IN_PROGRESS':
                    statusCell = `
                        <button class="status-btn in-progress"
                                onclick="updateStatus(${order.id}, 'COMPLETED')">
                            Mark Complete
                        </button>`;
                    break;
                case 'COMPLETED':
                    statusCell = `<span class="status-badge completed">Done</span>`;
                    break;
                default:
                    statusCell = order.status;
            }

            // Format date
            const orderDate = new Date(order.orderDate).toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            });

            // Create table 
            tbody.innerHTML += `
                <tr>
                    <td>#${order.id}</td>
                    <td>${order.customerName}</td>
                    <td>${order.totalClothes} items (₹${order.totalAmount})</td>
                    <td>${orderDate}</td>
                    <td class="status-cell">
                        <span class="status-badge ${order.status.toLowerCase()}">
                            ${order.status.replace('_', ' ')}
                        </span>
                    </td>
                    <td class="action-cell">
                        ${statusCell}
                    </td>
                </tr>
            `;
        });

        loadStats();

    } catch (error) {
        console.error('Order refresh failed:', error);
        showMessage('Failed to load orders: ' + error.message, 'error');
    }
}

// Status update function
async function updateStatus(orderId, newStatus) {
    await updateOrderStatus(orderId, newStatus, false);
    // if (!confirm(`Change order status to ${newStatus}?`)) return;

    try {
        const response = await fetch(
            `${BASE_URL}/api/orders/${orderId}/status?newStatus=${newStatus}`,
            { method: 'PATCH' }
        );

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Status update failed');
        }

        showMessage(`Status updated to ${newStatus}`, 'success');
        await refreshOrders();

    } catch (error) {
        showMessage(error.message, 'error');
    }
}

async function populateCustomerFilter() {
    try {
        const response = await fetch(`${BASE_URL}/api/customers`);

        if (!response.ok) throw new Error("Customer not found");
        const customers = await response.json();

        const select = document.getElementById('filter-customer', '');

        select.innerHTML = '<option value="">Select customer</option>';
        customers.forEach(customer => {
            const option = document.createElement('option');
            option.value = customer.id;
            option.textContent = `${customer.name} (${customer.phoneNumber})`;
            select.appendChild(option);
        });
    } catch (error) {
        showMessage("failed to load customer", 'error');
    }
}

let currentCustomerInModal = {
    id: null,
    name: null
};

async function viewOrders(customerId, customerName) {
    try {
        console.log('Opening history for:', customerName);
        currentCustomerInModal = { id: customerId, name: customerName };
        document.getElementById('customer-order-history-name').textContent = customerName;

        const modal = document.getElementById('customer-order-history');
        modal.classList.remove('hidden');
        modal.classList.add('modal--active');

        const response = await fetch(`${BASE_URL}/api/orders/customer/${customerId}`);
        if (!response.ok) throw new Error("Failed to fetch");

        const orders = await response.json();
        renderCustomerOrders(orders)
    } catch (error) {
        showMessage(`Error loading history: ${error.message}`, 'error');
        closeHistory();
    }
}


function renderCustomerOrders(orders) {
    const tbody = document.querySelector('#customer-orders-table tbody');
    tbody.innerHTML = '';

    if (orders.length === 0) {
        tbody.innerHTML = `<tr>
            <td colspan="5" style="text-align:center">No orders found</td>
        </tr>`;
        return;
    }



    orders.forEach(order => {
        // Generate status action button based on order status
        let actionButton = '';
        switch (order.status) {
            case 'PENDING':
                actionButton = `
                    <button class="status-btn pending small"
                            onclick="updateOrderStatus(${order.id}, 'IN_PROGRESS', true)">
                        Start
                    </button>`;
                break;
            case 'IN_PROGRESS':
                actionButton = `
                    <button class="status-btn in-progress small"
                            onclick="updateOrderStatus(${order.id}, 'COMPLETED', true)">
                        Mark Complete
                    </button>`;
                break;
            case 'COMPLETED':
                actionButton = `<span class="status-badge completed">Done</span>`;
                break;
            default:
                actionButton = '';
        }

        const orderDate = new Date(order.orderDate).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
        tbody.innerHTML += `
            <tr class="history-order-row">
                <td>#${order.id}</td>
                <td>${orderDate}</td>
                <td>${order.totalClothes} items (₹${order.totalAmount})</td>
                <td>
                    <span class="status-badge ${order.status.toLowerCase()}">
                        ${order.status.replace('_', ' ')}
                    </span>
                </td>
                
                <td class="action-cell">${actionButton}</td>
            </tr>
        `;
    });
    loadStats();
}

async function updateOrderStatus(orderId, newStatus, isInModal = false) {
    if (!confirm(`Change order status to ${newStatus}?`)) return;

    try {
        const response = await fetch(
            `${BASE_URL}/api/orders/${orderId}/status?newStatus=${newStatus}`,
            { method: 'PATCH' }
        );

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Status update failed');
        }

        showMessage(`Status updated to ${newStatus}`, 'success');


        if (isInModal && currentCustomerInModal.id) {
            // Refresh modal view
            await viewOrders(currentCustomerInModal.id, currentCustomerInModal.name);
        } else {
            // Refresh main orders table
            await refreshOrders();
        }

    } catch (error) {
        showMessage(error.message, 'error');
    }
}

function closeHistory() {
    const modal = document.getElementById('customer-order-history');
    modal.classList.add('hidden');
    modal.classList.remove('modal--active');

}


async function showPayments() {
    console.log(document.getElementById("payments-section"));
    hideAllSections();
    document.getElementById('payments-section').classList.remove('hidden');
    refreshPayments();

}

async function refreshPayments() {
    try {
        console.log("Fetching payment data...");
        const response = await fetch(`${BASE_URL}/api/payments`);
        console.log("Response status:", response.status);
        if (!response.ok) throw new Error("Failed to fetch the payments");
        const data = await response.json();
        console.log("Received data:", data);

        renderPaymentSummary(data);
    } catch (error) {
        showMessage("Something went wrong", "error");
    }
}


function renderPaymentSummary(data) {
    const tbody = document.querySelector('#payments-table tbody');
    tbody.innerHTML = '';

    console.log("Rendering data:", data);

    if (!data || data.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="no-data">
                    ${data ? "No pending payments found" : "Failed to load data"}
                </td>
            </tr>
        `;
        return;
    }

    data.forEach(account => {
        tbody.innerHTML += `
            <tr>
                <td>${account.customerId}</td>
                <td>${account.customerName}</td>
                <td>₹${account.balance.toFixed(2)}</td>
                <td>
                    <button onclick="viewOrders(${account.customerId},'${account.customerName}')">
                        View Orders
                    </button>
                </td>
                <td>
                    <button onclick="showPaymentModal(
                        ${account.customerId}, 
                        '${account.customerName}', 
                        ${account.balance}
                    )">
                        Pay
                    </button>
                </td>
            </tr>
        `;
    });
}




// Track current payment context
let currentPayment = {
    customerId: null,
    customerName: '',
    balance: 0
};

// Show payment modal
function showPaymentModal(customerId, customerName, balance) {
    currentPayment = {
        customerId,
        customerName,
        balance
    };

    // Update modal UI
    document.getElementById('payment-customer-name').textContent = customerName;
    document.getElementById('payment-total-due').textContent = `₹${balance.toFixed(2)}`;
    document.getElementById('payment-amount').value = '';

    // Show modal
    const modal = document.getElementById('payment-modal');
    modal.classList.remove('hidden');
    modal.classList.add('modal--active');
}

// Close modal
function closePaymentModal() {
    const modal = document.getElementById('payment-modal');
    modal.classList.add('hidden');
    modal.classList.remove('modal--active');
}

// Process payment
const paymentMessage = document.getElementById('payment-message');
async function processPayment() {
    const amountInput = document.getElementById('payment-amount');
    const { customerId, balance } = currentPayment;
    const amount = parseFloat(amountInput.value);

    paymentMessage.textContent = '0';
    paymentMessage.className = 'payment-message';

    // Validation
    if (isNaN(amount) || amount <= 0) {
        showPaymentMessage('Please enter a valid positive amount', 'error');
        amountInput.focus();
        return;
    }

    if (amount > balance) {
        showPaymentMessage(`Amount cannot exceed ₹${balance.toFixed(2)}`, 'error');
        amountInput.focus();
        return;
    }

    try {
        // Call backend API
        const response = await fetch(`${BASE_URL}/api/payments`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                customerId: customerId,
                amount: amount
            })
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        showPaymentMessage(`Payment of ₹${amount.toFixed(2)} recorded!`, 'success');
        setTimeout(() => {
            closePaymentModal();
            refreshPayments();
        }, 1500);
        closePaymentModal();

        refreshPayments();
        loadStats();
    } catch (error) {
        showPaymentMessage(`Payment failed: ${error.message}`, 'error');
    }
}



function showPaymentMessage(message, type) {
    paymentMessage.textContent = message;
    paymentMessage.className = `payment-message ${type}`;

    // Auto-hide after 4 seconds
    setTimeout(() => {
        paymentMessage.textContent = '';
        paymentMessage.className = 'payment-message';
    }, 4000);
}

async function viewTransactions(customerId, customerName) {
    try {
        console.log("Opening transaction history for ", customerName, customerId);

        document.getElementById('transaction-customer-name').textContent = customerName;
        const modal = document.getElementById('payment-transaction-history');
        modal.classList.remove('hidden');
        modal.classList.add('modal--active');
        const response = await fetch(`${BASE_URL}/api/payments/${customerId}/history`);

        const transactionData = await response.json();
        showTransactionHistory(transactionData);
    } catch (error) {
        showMessage("Something went wrong", "error");
    }
}

async function showTransactionHistory(transactionData) {


    const tbody = document.querySelector('#transaction-table tbody');
    tbody.innerHTML = '';

    if (transactionData === 0) {
        tbody.innerHTML = `
        <tr>
            <td colspan="4" class="no-data">No pending payments found</td>
        </tr>
        `;
        return;
    }

    tbody.innerHTML = transactionData.map(tData => {

        const timeDateStamp = new Date(tData.timestamp).toLocaleString('en-GB', {
            day: '2-digit',
            month: 'long',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            hour12: true
        });

        return `
        <tr>
            <td>${tData.transactionId}</td>
            <td>${tData.amount.toFixed(2)}</td>
            <td>${timeDateStamp}</td>
        </tr>
    `;
    }).join('');


}

function closeTransactionModal() {
    const modal = document.getElementById('payment-transaction-history');
    modal.classList.add('hidden');
    modal.classList.remove('modal--active');
}