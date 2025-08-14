document.addEventListener('DOMContentLoaded', () => {
    const receiptData = JSON.parse(sessionStorage.getItem('lastReceiptData'));
    const loggedInUser = JSON.parse(sessionStorage.getItem('loggedInUser'));

    if (!receiptData || !loggedInUser) {
        document.body.innerHTML = '<p class="text-center text-red-500">No receipt data found. Please generate an invoice first.</p>';
        return;
    }

    const formatCurrency = (amount) => `LKR ${Number(amount).toFixed(2)}`;

    // Populate receipt details
    document.getElementById('receipt-invoice-number').textContent = receiptData.invoice.invoiceNumber;
    document.getElementById('receipt-date').textContent = new Date().toLocaleString();
    document.getElementById('receipt-cashier').textContent = loggedInUser.name || loggedInUser.username;
    document.getElementById('receipt-customer').textContent = receiptData.customer.name;

    const itemsBody = document.getElementById('receipt-items-body');
    itemsBody.innerHTML = '';
    receiptData.invoice.invoiceItems.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
                    <td class="py-1 text-left">${item.name}</td>
                    <td class="py-1 text-center">${item.quantity}</td>
                    <td class="py-1 text-right">${formatCurrency(item.unitPrice)}</td>
                    <td class="py-1 text-right">${formatCurrency(item.itemTotal)}</td>
                `;
        itemsBody.appendChild(row);
    });

    document.getElementById('receipt-subtotal').textContent = formatCurrency(receiptData.invoice.totalAmount);
    document.getElementById('receipt-discount').textContent = formatCurrency(receiptData.invoice.discountAmount);
    document.getElementById('receipt-total').textContent = formatCurrency(receiptData.invoice.netAmount);

    // Auto-trigger print dialog after a short delay
    setTimeout(() => {
        window.print();
    }, 500);

    // Clean up session storage after printing is done
    window.onafterprint = () => {
        sessionStorage.removeItem('lastReceiptData');
        window.close();
    };

    document.getElementById('print-btn').addEventListener('click', () => window.print());
});