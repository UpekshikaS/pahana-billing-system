    (() => {
        const API_BASE_URL = 'http://localhost:8080/PahanaEdu-BookShop-Server/api';
        let allCustomers = [];
        let allProducts = [];
        let invoiceItems = [];
        let selectedCustomer = null;
        const loggedInUser = JSON.parse(sessionStorage.getItem('loggedInUser'));
        const customerSearchInput = document.getElementById('customer-search');
        const customerSearchResults = document.getElementById('customer-search-results');
        const selectedCustomerInfo = document.getElementById('selected-customer-info');
        const itemScanInput = document.getElementById('item-scan');
        const addItemBtn = document.getElementById('add-item-btn');
        const productSuggestionsContainer = document.getElementById('product-suggestions');
        const itemFeedback = document.getElementById('item-feedback');
        const invoiceItemsBody = document.getElementById('invoice-items-body');
        const emptyCartRow = document.getElementById('empty-cart-row');
        const subtotalEl = document.getElementById('summary-subtotal');
        const discountEl = document.getElementById('summary-discount');
        const totalEl = document.getElementById('summary-total');
        const processPaymentBtn = document.getElementById('process-payment-btn');
        const clearBillBtn = document.getElementById('clear-bill-btn');
        const generalErrorDiv = document.getElementById('general-error');
        const paymentMethodEl = document.getElementById('payment-method');
        const paidAmountEl = document.getElementById('paid-amount');
        const balanceDisplayEl = document.getElementById('balance-display');
        const cashFields = document.getElementById('cash-fields');
        const refNumberField = document.getElementById('ref-number');
        const refNumberContainer = document.getElementById('ref-number-field');
        const formatCurrency = (amount) => `LKR ${Number(amount).toFixed(2)}`;
        const showGeneralError = (message) => {
generalErrorDiv.textContent = message;
        generalErrorDiv.classList.remove('hidden');
};
        const hideGeneralError = () => generalErrorDiv.classList.add('hidden');
        const updateSummary = () => {
const subtotal = invoiceItems.reduce((acc, item) => acc + item.price * item.quantity, 0);
        const discount = parseFloat(discountEl.value) || 0;
        const total = Math.max(subtotal - discount, 0);
        subtotalEl.textContent = formatCurrency(subtotal);
        totalEl.textContent = formatCurrency(total);
//                    processPaymentBtn.disabled = invoiceItems.length === 0 || !selectedCustomer;
        processPaymentBtn.disabled = invoiceItems.length === 0;
        const paymentMethod = paymentMethodEl.value;
        if (paymentMethod === 'Cash') {
const paid = parseFloat(paidAmountEl.value) || 0;
        const balance = Math.max(paid - total, 0);
        balanceDisplayEl.textContent = formatCurrency(balance);
} else {
// Clear balance display if not cash
balanceDisplayEl.textContent = formatCurrency(0);
}
};
        const renderInvoiceItems = () => {
invoiceItemsBody.innerHTML = '';
        if (invoiceItems.length === 0) {
invoiceItemsBody.appendChild(emptyCartRow.cloneNode(true));
} else {
invoiceItems.forEach((item, index) => {
const row = document.createElement('tr');
        row.innerHTML = `
            <td class="py-4 pl-4 pr-3 text-sm sm:pl-0">
                <div class="font-medium text-gray-900">${item.name}</div>
                <div class="text-gray-500">${item.itemId}</div>
            </td>
            <td class="px-3 py-4 text-sm text-gray-500">${formatCurrency(item.price)}</td>
            <td class="px-3 py-4 text-sm text-gray-500 text-center">
                <input type="number" value="${item.quantity}" min="1" max="${item.stockQuantity}" data-index="${index}" class="table-cell-input quantity-input" />
            </td>
            <td class="px-3 py-4 text-sm text-gray-700 font-medium">${formatCurrency(item.price * item.quantity)}</td>
            <td class="relative py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-0">
                <button class="text-red-600 hover:text-red-900 remove-item-btn" data-index="${index}">
                    <i class="fas fa-trash-alt"></i>
                </button>
            </td>
        `;
        invoiceItemsBody.appendChild(row);
});
}
updateSummary();
};
        const clearBill = () => {
invoiceItems = [];
        selectedCustomer = null;
        discountEl.value = 0;
        itemScanInput.value = '';
        customerSearchInput.value = '';
        selectedCustomerInfo.classList.add('hidden');
        itemFeedback.textContent = '\u00A0';
        hideGeneralError();
        paidAmountEl.value = '';
        balanceDisplayEl.textContent = formatCurrency(0);
        refNumberField.value = '';
        paymentMethodEl.value = 'Cash';
        cashFields.classList.remove('hidden');
        refNumberContainer.classList.add('hidden');
        renderInvoiceItems();
};
        const fetchCustomers = async () => {
try {
const res = await fetch(`${API_BASE_URL}/customers`);
        if (!res.ok)
        throw new Error("Customer fetch failed");
        allCustomers = await res.json();
} catch (err) {
console.error(err);
}
};
        const fetchProducts = async () => {
try {
const res = await fetch(`${API_BASE_URL}/products`);
        if (!res.ok)
        throw new Error("Product fetch failed");
        allProducts = await res.json();
} catch (err) {
console.error(err);
}
};
        const fetchMatchingProducts = (query) => {
productSuggestionsContainer.innerHTML = '';
        if (!query) {
productSuggestionsContainer.classList.add('hidden');
        return;
}

const lowerCaseQuery = query.toLowerCase();
        const matched = allProducts.filter(p =>
                p.itemId.toLowerCase().includes(lowerCaseQuery) ||
                p.name.toLowerCase().includes(lowerCaseQuery)
                ).slice(0, 10); // Limit suggestions to 10

        if (matched.length > 0) {
matched.forEach(p => {
const div = document.createElement('div');
        div.className = 'p-2 hover:bg-indigo-100 cursor-pointer text-sm';
        div.textContent = `${p.name} (${p.itemId}) - Stock: ${p.stockQuantity}`;
        div.onclick = () => {
itemScanInput.value = p.itemId;
        productSuggestionsContainer.classList.add('hidden');
        itemScanInput.focus();
};
        productSuggestionsContainer.appendChild(div);
});
        productSuggestionsContainer.classList.remove('hidden');
} else {
productSuggestionsContainer.classList.add('hidden');
}
};
        const handleCustomerSearch = (query) => {
customerSearchResults.innerHTML = '';
        if (!query)
        return customerSearchResults.classList.add('hidden');
        const matched = allCustomers.filter(c =>
                c.name.toLowerCase().includes(query) ||
                c.accountNumber.toLowerCase().includes(query)
                ).slice(0, 5);
        if (matched.length > 0) {
matched.forEach(c => {
const div = document.createElement('div');
        div.className = 'p-2 hover:bg-indigo-100 cursor-pointer';
        div.textContent = `${c.name} (${c.accountNumber})`;
        div.onclick = () => selectCustomer(c);
        customerSearchResults.appendChild(div);
});
        customerSearchResults.classList.remove('hidden');
} else {
customerSearchResults.classList.add('hidden');
}
};
        const selectCustomer = (customer) => {
selectedCustomer = customer;
        customerSearchInput.value = customer.name;
        customerSearchResults.classList.add('hidden');
        selectedCustomerInfo.innerHTML = `Selected: <strong>${customer.name}</strong>`;
        selectedCustomerInfo.classList.remove('hidden');
        updateSummary();
};
        const handleAddItem = () => {
const itemId = itemScanInput.value.trim().toUpperCase();
        if (!itemId)
        return;
        const product = allProducts.find(p => p.itemId.toUpperCase() === itemId);
        if (!product) {
itemFeedback.textContent = 'Product not found.';
        itemFeedback.classList.add('text-red-500');
        itemFeedback.classList.remove('text-green-500');
        return;
}

if (product.stockQuantity <= 0) {
itemFeedback.textContent = 'Out of stock.';
        itemFeedback.classList.add('text-red-500');
        itemFeedback.classList.remove('text-green-500');
        return;
}

const existingItem = invoiceItems.find(i => i.productId === product.productId);
        if (existingItem) {
if (existingItem.quantity < product.stockQuantity) {
existingItem.quantity++;
} else {
itemFeedback.textContent = 'Reached stock limit.';
        itemFeedback.classList.add('text-red-500');
        itemFeedback.classList.remove('text-green-500');
        return;
}
} else {
invoiceItems.push({...product, quantity: 1});
}

itemScanInput.value = '';
        itemFeedback.textContent = `Added: ${product.name}`;
        itemFeedback.classList.add('text-green-500');
        itemFeedback.classList.remove('text-red-500');
        productSuggestionsContainer.classList.add('hidden');
        renderInvoiceItems();
        discountEl.focus();
};
        function validatePaidAmount() {
        const paidAmountStr = paidAmountEl.value.trim();
                const paidAmount = parseFloat(paidAmountStr);
                const totalStr = totalEl.textContent.replace(/[^\d.-]/g, '');
                const total = parseFloat(totalStr);
                const paymentMethod = paymentMethodEl.value;
                if (paymentMethod === "Cash" && (isNaN(paidAmount) || paidAmount <= 0)) {
        Swal.fire({
        icon: "error",
                title: "Invalid Amount",
                text: "Please enter a valid numeric amount paid by the customer for Cash payment."
        });
                paidAmountEl.focus();
                return false;
        }


        if (paymentMethod === "Cash" && paidAmount < total) {
        Swal.fire({
        icon: "error",
                title: "Insufficient Amount",
                text: "Customer paid amount must be greater than or equal to the total bill."
        });
                paidAmountEl.focus();
                return false;
        }

        return true;
        }

const handleProcessPayment = async () => {
hideGeneralError();
        if (invoiceItems.length === 0) {
showGeneralError('Please add items to the bill before processing payment.');
        return;
}

if (paymentMethodEl.value === 'Cash' && !validatePaidAmount()) {
return;
}
if (paymentMethodEl.value !== 'Cash' && refNumberField.value.trim() === '') {
Swal.fire({
icon: 'error',
        title: 'Reference Required',
        text: 'Please enter a reference number for Card/Online Transfer payments.',
});
        refNumberField.focus();
        return;
}


processPaymentBtn.disabled = true;
        processPaymentBtn.querySelector('span').textContent = 'Processing...';
        const invoiceData = {
        customerId: selectedCustomer ? selectedCustomer.customerId : null,
                userId: loggedInUser.userId,
                totalAmount: parseFloat(subtotalEl.textContent.replace('LKR ', '')),
                discountAmount: parseFloat(discountEl.value) || 0,
                netAmount: parseFloat(totalEl.textContent.replace('LKR ', '')),
                paymentStatus: 'PAID',
                paymentMethod: paymentMethodEl.value,
                paidAmount: parseFloat(paidAmountEl.value) || parseFloat(totalEl.textContent.replace('LKR ', '')), // For non-cash, assume paid == netAmount
                balanceAmount: paymentMethodEl.value === 'Cash' ? parseFloat(balanceDisplayEl.textContent.replace('LKR ', '')) : 0,
                referenceNumber: paymentMethodEl.value !== 'Cash' ? refNumberField.value.trim() : null,
                invoiceItems: invoiceItems.map(item => ({
                productId: item.productId,
                        quantity: item.quantity,
                        unitPrice: item.price,
                        itemTotal: item.price * item.quantity,
                        name: item.name
                }))
        };
        try {
        const response = await fetch(`${API_BASE_URL}/invoices`, {
        method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(invoiceData),
        });
                if (!response.ok) {
        const errorText = await response.text();
                throw new Error(`Failed to create invoice: ${errorText}`);
        }

        const createdInvoice = await response.json();
                invoiceData.invoiceNumber = createdInvoice.invoiceNumber || createdInvoice.invoiceId || 'N/A';
                await Swal.fire({
                icon: 'success',
                        title: 'Invoice Created',
                        confirmButtonColor: '#10b981',
                        text: 'The invoice was successfully processed!',
                }).then(() => {
        generatePrintableBill(invoiceData);
                clearBill();
                itemScanInput.focus();
        });
        } catch (err) {
console.error(err);
        await Swal.fire({
        icon: 'error',
                title: 'Error',
                confirmButtonColor: '#ef4444',
                text: err.message || 'Invoice process failed.',
        });
} finally {
processPaymentBtn.disabled = false;
        processPaymentBtn.querySelector('span').textContent = 'Process Payment';
}
};
        customerSearchInput.addEventListener('input', e => handleCustomerSearch(e.target.value.toLowerCase()));
        itemScanInput.addEventListener('keyup', e => {
        if (e.key !== 'Enter') {
        itemFeedback.textContent = '\u00A0'; // Clear feedback on typing
                itemFeedback.classList.remove('text-red-500', 'text-green-500');
                fetchMatchingProducts(e.target.value.trim());
        }
        });
        itemScanInput.addEventListener('keydown', e => {
        if (e.key === 'Enter') {
        e.preventDefault();
                handleAddItem();
        }
        });
        addItemBtn.addEventListener('click', e => {
        e.preventDefault();
                handleAddItem();
        });
        document.addEventListener('click', e => {
        if (!productSuggestionsContainer.contains(e.target) && e.target !== itemScanInput) {
        productSuggestionsContainer.classList.add('hidden');
        }
        if (!customerSearchResults.contains(e.target) && e.target !== customerSearchInput) {
        customerSearchResults.classList.add('hidden');
        }
        });
        invoiceItemsBody.addEventListener('input', (e) => {
        if (e.target.classList.contains('quantity-input')) {
        const idx = e.target.dataset.index;
                const val = parseInt(e.target.value);
                if (val > 0 && val <= invoiceItems[idx].stockQuantity) {
        invoiceItems[idx].quantity = val;
                renderInvoiceItems();
        } else if (val <= 0) {
        Swal.fire({
        icon: 'warning',
                title: 'Invalid Quantity',
                text: 'Quantity must be at least 1.',
                confirmButtonColor: '#f59e0b',
        });
                e.target.value = invoiceItems[idx].quantity;
        } else {
        Swal.fire({
        icon: 'warning',
                title: 'Insufficient Stock',
                text: `Only ${invoiceItems[idx].stockQuantity} available for ${invoiceItems[idx].name}.`,
                confirmButtonColor: '#f59e0b',
        });
                e.target.value = invoiceItems[idx].quantity;
        }
        }
        });
        invoiceItemsBody.addEventListener('click', (e) => {
        if (e.target.closest('.remove-item-btn')) {
        const idx = e.target.closest('.remove-item-btn').dataset.index;
                invoiceItems.splice(idx, 1);
                renderInvoiceItems();
        }
        });
        discountEl.addEventListener('input', updateSummary);
        paidAmountEl.addEventListener('input', updateSummary);
        paymentMethodEl.addEventListener('change', () => {
        const method = paymentMethodEl.value;
                cashFields.classList.toggle('hidden', method !== 'Cash');
                refNumberContainer.classList.toggle('hidden', method === 'Cash');
                updateSummary();
        });
        processPaymentBtn.addEventListener('click', handleProcessPayment);
        clearBillBtn.addEventListener('click', () => {
        Swal.fire({
        title: 'Are you sure?',
                text: "Do you want to clear the current bill?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#6b7280',
                confirmButtonText: 'Yes, clear it!'
        }).then((result) => {
        if (result.isConfirmed) {
        clearBill();
                Swal.fire(
                        'Cleared!',
                        'The bill has been cleared.',
                        'success'
                        )
        }
        })
        });
        discountEl.addEventListener('keydown', e => {
        if (e.key === 'Enter') {
        e.preventDefault();
                paidAmountEl.focus();
        }
        });
        paidAmountEl.addEventListener('keydown', e => {
        if (e.key === 'Enter') {
        e.preventDefault();
                if (paymentMethodEl.value === 'Cash') {
        processPaymentBtn.focus();
        } else {
        refNumberField.focus();
        }
        }
        });
        refNumberField.addEventListener('keydown', e => {
        if (e.key === 'Enter') {
        e.preventDefault();
                processPaymentBtn.focus();
        }
        });
        processPaymentBtn.addEventListener('keydown', e => {
        if (e.key === 'Enter') {
        e.preventDefault();
                processPaymentBtn.click();
        }
        });
        (async () => {
        await fetchCustomers();
                await fetchProducts();
                clearBill();
        })();
        let productList = [];
        fetch(`${API_BASE_URL}/products`)
        .then(res => res.json())
        .then(data => {
        productList = data;
        });
            

    function generatePrintableBill(invoiceData) {
        console.log(invoiceData);
        const win = window.open('', '_blank', 'width=300,height=600');
        const date = new Date().toLocaleString();
        const paymentMethod = document.getElementById("payment-method").value;
        const paidAmount = parseFloat(document.getElementById("paid-amount").value || 0);
        const refNumber = document.getElementById("ref-number").value || '-';
        const balance = Math.max(paidAmount - invoiceData.netAmount, 0);
        let html = `
<html>
<head>
<title>Receipt Preview</title>
<style>
    body { font-family: monospace; font-size: 12px; padding: 10px; }
    .center { text-align: center; }
    .right { text-align: right; }
    table { width: 100%; border-collapse: collapse; }
    td { padding: 2px 0; }
    .line { border-top: 1px dotted black; margin: 10px 0; }
    button {
      margin: 10px auto;
      display: block;
      padding: 8px 16px;
      font-size: 14px;
      cursor: pointer;
    }
</style>
</head>
<body>
<div class="center">
    <h2>Pahana Edu Book Shop</h2>
    <div>ABC Road, Colombo</div>
    <div>Tel: 011-55 66 777 | Reg: 1123636564</div>
    <div>Email: pahanaedu@gmail.com</div>
    <div>${date}</div>
    <div>Invoice #: ${invoiceData.invoiceNumber || invoiceData.invoiceId || 'N/A'}</div>
</div>
<div class="line"></div>
<table>
    <thead>
        <tr>
            <td><strong>Item</strong></td>
            <td class="right"><strong>Qty</strong></td>
            <td class="right"><strong>Unit Price</strong></td>
        </tr>
    </thead>
    <tbody>`;
        invoiceData.invoiceItems.forEach(item => {
        html += `
    <tr>
        <td>${item.name}</td>
        <td class="center">${item.quantity}</td>
        <td class="right">LKR ${item.unitPrice.toFixed(2)}</td>
    </tr>`;
        });
        html += `
    </tbody>
</table>
<div class="line"></div>
<table>
    <tr><td>Total</td><td class="right">LKR ${invoiceData.totalAmount.toFixed(2)}</td></tr>
    <tr><td>Discount</td><td class="right">LKR ${invoiceData.discountAmount.toFixed(2)}</td></tr>
    <tr><td><strong>Net Total</strong></td><td class="right"><strong>LKR ${invoiceData.netAmount.toFixed(2)}</strong></td></tr>`;
        if (paymentMethod === "Cash") {
html += `
    <tr><td>Paid</td><td class="right">LKR ${paidAmount.toFixed(2)}</td></tr>
    <tr><td>Balance</td><td class="right">LKR ${balance.toFixed(2)}</td></tr>`;
} else {
html += `<tr><td>Reference</td><td class="right">${refNumber}</td></tr>`;
}

html += `
    <tr><td>Method</td><td class="right">${paymentMethod}</td></tr>
</table>
<div class="line"></div>
<div class="center">
    <p>Thank you for your choice.<br />Visit us again!
    <br />* No exchange without receipt</p>
</div>
<div class="line"></div>
<button onclick="window.print()">Print</button>
</body>
</html>`;
        win.document.write(html);
        win.document.close();
        win.focus();
    }
 })();