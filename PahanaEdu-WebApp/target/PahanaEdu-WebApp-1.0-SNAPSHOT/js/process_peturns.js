(() => {
    const API_BASE_URL = 'http://localhost:8080/PahanaEdu-BookShop-Server/api';
    const loggedInUser = JSON.parse(sessionStorage.getItem('loggedInUser'));

    const searchInput = document.getElementById('invoice-search');
    const searchBtn = document.getElementById('search-btn');
    const messageArea = document.getElementById('message-area');
    const itemsTableContainer = document.getElementById('items-table-container');
    const tableBody = document.getElementById('invoice-items-table-body');
    const invoiceNumberDisplay = document.getElementById('invoice-number-display');

    const returnModal = document.getElementById('return-modal');
    const modalItemName = document.getElementById('modal-item-name');
    const modalInvoiceItemId = document.getElementById('modal-invoice-item-id');
    const modalReturnQuantity = document.getElementById('modal-return-quantity');
    const returnReasonInput = document.getElementById('return-reason');
    const confirmReturnBtn = document.getElementById('confirm-return-btn');
    const cancelReturnBtn = document.getElementById('cancel-return-btn');

    const showMessage = (text, isError = false) => {
        messageArea.innerHTML = `<i class="fas ${isError ? 'fa-exclamation-circle text-red-500' : 'fa-info-circle'} fa-2x"></i><p class="mt-2">${text}</p>`;
        messageArea.classList.remove('hidden');
        itemsTableContainer.classList.add('hidden');
    };

    const renderTable = (items) => {
        tableBody.innerHTML = '';

        const returnableItems = items.filter(item => item.quantityReturned < item.quantityPurchased);

        if (returnableItems.length === 0) {
            showMessage('All items on this invoice have already been returned.', true);
            return;
        }

        returnableItems.forEach(item => {
            const maxQty = item.quantityPurchased - item.quantityReturned;
            const row = document.createElement('tr');
            row.innerHTML = `
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${item.productName}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">${item.quantityPurchased}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-center">
                    <input type="number" class="w-20 p-1 border rounded-md text-center return-qty-input" value="1" min="1" max="${maxQty}" data-id="${item.invoiceItemId}">
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button class="bg-blue-600 text-white px-3 py-1 rounded-md hover:bg-blue-700 process-return-btn" data-id="${item.invoiceItemId}" data-name="${item.productName}">Return</button>
                </td>
            `;
            tableBody.appendChild(row);
        });

        invoiceNumberDisplay.textContent = returnableItems[0].invoiceNumber;
        messageArea.classList.add('hidden');
        itemsTableContainer.classList.remove('hidden');
    };

    const handleSearchInvoice = async () => {
        const invoiceNumber = searchInput.value.trim();
        if (!invoiceNumber) {
            showMessage('Please enter an invoice number.');
            return;
        }
        showMessage('Searching...', false);

        try {
            const response = await fetch(`${API_BASE_URL}/invoices/search/${invoiceNumber}`);
            const data = await response.json();
            if (!response.ok)
                throw new Error(data.error || 'Invoice not found');

            renderTable(data);

        } catch (error) {
            showMessage(error.message, true);
        }
    };

    const openReturnModal = (invoiceItemId, productName) => {
        const returnQtyInput = document.querySelector(`.return-qty-input[data-id='${invoiceItemId}']`);
        const quantityToReturn = parseInt(returnQtyInput.value);
        const maxQuantity = parseInt(returnQtyInput.max);

        if (isNaN(quantityToReturn) || quantityToReturn <= 0 || quantityToReturn > maxQuantity) {
            alert(`Invalid quantity. Please enter a number between 1 and ${maxQuantity}.`);
            return;
        }

        modalItemName.textContent = `${quantityToReturn} x ${productName}`;
        modalInvoiceItemId.value = invoiceItemId;
        modalReturnQuantity.value = quantityToReturn;
        returnReasonInput.value = '';
        returnModal.classList.remove('hidden');
    };

    const closeReturnModal = () => returnModal.classList.add('hidden');

    const handleConfirmReturn = async () => {
        confirmReturnBtn.disabled = true;
        confirmReturnBtn.textContent = 'Processing...';

        const returnData = {
            invoiceItemId: parseInt(modalInvoiceItemId.value),
            quantityReturned: parseInt(modalReturnQuantity.value),
            reason: returnReasonInput.value,
            processedByUserId: loggedInUser.userId
        };

        alert('alert:::', returnData);

        try {
            const response = await fetch(`${API_BASE_URL}/returns`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(returnData)
            });
            const result = await response.json();
            if (!response.ok)
                throw new Error(result.error || 'Failed to process return.');

            alert('Return processed successfully!');
            closeReturnModal();
            // Refresh the search to show updated quantities or remove item
            handleSearchInvoice();

        } catch (error) {
            alert(`Error: ${error.message}`);
        } finally {
            confirmReturnBtn.disabled = false;
            confirmReturnBtn.textContent = 'Confirm Return';
        }
    };

    searchBtn.addEventListener('click', handleSearchInvoice);
    searchInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter')
            handleSearchInvoice();
    });

    tableBody.addEventListener('click', (e) => {
        if (e.target.classList.contains('process-return-btn')) {
            const itemId = e.target.dataset.id;
            const itemName = e.target.dataset.name;
            openReturnModal(itemId, itemName);
        }
    });

    cancelReturnBtn.addEventListener('click', closeReturnModal);
    confirmReturnBtn.addEventListener('click', handleConfirmReturn);

    if (!loggedInUser) {
        alert('You are not logged in. Redirecting to login page.');
        window.location.href = 'login.html';
    }
})();