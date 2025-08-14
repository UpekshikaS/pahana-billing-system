(() => {
    const API_BASE_URL = 'http://localhost:8080/PahanaEdu-BookShop-Server/api';
    let allCustomers = [];

    const tableBody = document.getElementById('customers-table-body');
    const searchInput = document.getElementById('customer-search');
    const generalErrorDiv = document.getElementById('general-error');
    const notificationArea = document.getElementById('notification-area');

    const customerModal = document.getElementById('customer-modal');
    const addCustomerBtn = document.getElementById('add-customer-btn');
    const cancelCustomerBtn = document.getElementById('cancel-customer-btn');
    const saveCustomerBtn = document.getElementById('save-customer-btn');
    const customerForm = document.getElementById('customer-form');
    const modalTitle = document.getElementById('customer-modal-title');

    const custNameInput = document.getElementById('cust-name');
    const custAddressInput = document.getElementById('cust-address');
    const custPhoneInput = document.getElementById('cust-phone');

    const errorCustName = document.getElementById('error-cust-name');
    const errorCustAddress = document.getElementById('error-cust-address');
    const errorCustPhone = document.getElementById('error-cust-phone');


    const viewCustomerModal = document.getElementById('view-customer-modal');
    const closeViewModalBtn = document.getElementById('close-view-modal');
    const viewPhoneSpan = document.getElementById('view-phone');
    const viewPhoneErrorSpan = document.getElementById('view-phone-error');
    const viewUnitsConsumedSpan = document.getElementById('view-units-consumed');

    const showGeneralError = (msg) => {
        generalErrorDiv.textContent = msg;
        generalErrorDiv.classList.remove('hidden');
    };

    const hideGeneralError = () => generalErrorDiv.classList.add('hidden');

    showLoading = () => {
        tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-10"><div class="table-loader mx-auto"></div></td></tr>`;
    };

    const getNextAccountNumber = () => {
        if (allCustomers.length === 0)
            return 'CUST-001';
        const numbers = allCustomers.map(c => {
            const m = c.accountNumber.match(/^CUST-(\d+)$/);
            return m ? parseInt(m[1]) : 0;
        });
        return `CUST-${(Math.max(...numbers) + 1).toString().padStart(3, '0')}`;
    };

    const renderTable = (customers) => {
        tableBody.innerHTML = '';
        if (customers.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-10 text-gray-500"><i class="fas fa-users-slash fa-2x"></i><p class="mt-2">No customers found</p></td></tr>`;
            return;
        }

        const loggedInUser = JSON.parse(sessionStorage.getItem('loggedInUser'));
        const isAdmin = loggedInUser && loggedInUser.role === 'ADMIN';

        customers.forEach(customer => {
            const row = document.createElement('tr');
            row.innerHTML = `
    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-indigo-600 cursor-pointer view-customer" data-id="${customer.customerId}">
      ${customer.accountNumber}
    </td>
    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">${customer.name}</td>
    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">${customer.address || 'N/A'}</td>
    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">${customer.telephone || 'N/A'}</td>
    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">${customer.unitsConsumed || '0'}</td>
    <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
      <button class="text-indigo-600 hover:text-indigo-900 edit-btn" data-id="${customer.customerId}">Edit</button>
      ${isAdmin ? `<button class="text-red-600 hover:text-red-900 ml-4 delete-btn" data-id="${customer.customerId}">Delete</button>` : ''}
    </td>
`;
            tableBody.appendChild(row);
        });
    };

    const fetchCustomers = async () => {
        showLoading();
        hideGeneralError();
        try {
            const res = await fetch(`${API_BASE_URL}/customers`);
            if (!res.ok)
                throw new Error('Failed to fetch customers');
            allCustomers = await res.json();
            renderTable(allCustomers);
        } catch (err) {
            console.error(err);
            showGeneralError('Could not load customers from the server.');
            tableBody.innerHTML = '';
        }
    };

    const showNotification = (message, type = 'success') => {
        const notificationDiv = document.createElement('div');
        notificationDiv.className = `notification p-4 rounded-lg shadow-lg flex items-center text-white min-w-[250px] transition-all duration-300 ease-out`;

        let bgColor = 'bg-green-500';
        let icon = '<i class="fas fa-check-circle mr-2"></i>';

        if (type === 'error') {
            bgColor = 'bg-red-600';
            icon = '<i class="fas fa-times-circle mr-2"></i>';
        } else if (type === 'info') {
            bgColor = 'bg-blue-500';
            icon = '<i class="fas fa-info-circle mr-2"></i>';
        }

        notificationDiv.classList.add(bgColor);
        notificationDiv.innerHTML = `${icon}<span>${message}</span>`;

        notificationArea.prepend(notificationDiv);

        setTimeout(() => {
            notificationDiv.classList.add('show');
        }, 10);

        // Hide after 5 seconds
        setTimeout(() => {
            notificationDiv.classList.remove('show');
            notificationDiv.classList.add('opacity-0', 'translate-x-full');
            notificationDiv.addEventListener('transitionend', () => {
                notificationDiv.remove();
            }, {once: true});
        }, 3000);
    };


    const validateInput = (inputElement, errorElement, regex, errorMessage, isRequired = true) => {
        const value = inputElement.value.trim();
        let isValid = true;

        // Clear previous error
        inputElement.classList.remove('input-invalid');
        errorElement.classList.add('hidden');
        errorElement.textContent = '';

        if (isRequired && value === '') {
            isValid = false;
            errorElement.textContent = errorMessage || 'This field is required.';
        } else if (value !== '' && regex && !regex.test(value)) {
            isValid = false;
            errorElement.textContent = errorMessage;
        }

        if (!isValid) {
            inputElement.classList.add('input-invalid');
            errorElement.classList.remove('hidden');
        }
        return isValid;
    };

    const validateCustomerForm = () => {
        let isValid = true;

        if (!validateInput(custNameInput, errorCustName, null, 'Full Name is required.', true)) {
            isValid = false;
        }

        if (!validateInput(custAddressInput, errorCustAddress, null, 'Address is required.', false)) {

        }

        if (!validateInput(custPhoneInput, errorCustPhone, /^\d{10}$/, 'Please enter a valid 10-digit telephone number (e.g., 0712345678).', false)) {
            isValid = false;
        }

        return isValid;
    };

    const validateViewPhoneNumber = (phoneNumber) => {
        viewPhoneErrorSpan.textContent = '';
        viewPhoneErrorSpan.classList.add('hidden');

        const cleanPhoneNumber = String(phoneNumber || '').replace(/\s|-|\(|\)/g, '');

        if (!cleanPhoneNumber || cleanPhoneNumber === 'N/A') {
            return true;
        }

        if (!/^\d{10}$/.test(cleanPhoneNumber)) {
            viewPhoneErrorSpan.textContent = 'Invalid Phone No Format (Expected 10 digits).';
            viewPhoneErrorSpan.classList.remove('hidden');
            return false;
        }
        return true;
    };


    const handleSaveCustomer = async (event) => {
        event.preventDefault();

        if (!validateCustomerForm()) {
            showNotification('Please correct the errors in the form.', 'error');
            return;
        }

        saveCustomerBtn.disabled = true;
        saveCustomerBtn.textContent = 'Saving...';

        const customerData = {
            accountNumber: document.getElementById('cust-acc-no').value,
            name: custNameInput.value.trim(),
            address: custAddressInput.value.trim(),
            telephone: custPhoneInput.value.trim()
        };

        const id = document.getElementById('customer-id-hidden').value;
        const url = id ? `${API_BASE_URL}/customers/${id}` : `${API_BASE_URL}/customers`;
        const method = id ? 'PUT' : 'POST';

        try {
            const res = await fetch(url, {
                method,
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(customerData)
            });

            if (!res.ok) {
                const errorText = await res.text();
                let errorMessage = `Failed to ${id ? 'update' : 'add'} customer.`;
                try {
                    const errorJson = JSON.parse(errorText);
                    errorMessage = errorJson.message || errorMessage;
                } catch (jsonParseError) {
                    errorMessage = errorText || errorMessage;
                }
                throw new Error(errorMessage);
            }

            closeCustomerModal();
            await fetchCustomers();
            const successMessage = id ? 'Customer updated successfully!' : 'Customer added successfully!';
            showNotification(successMessage, 'success');

        } catch (e) {
            showNotification(`Error: ${e.message}`, 'error');
            console.error('Save failed:', e);
        } finally {
            saveCustomerBtn.disabled = false;
            saveCustomerBtn.textContent = 'Save';
        }
    };

    const handleDeleteCustomer = async (id) => {
        if (!confirm('Are you sure you want to delete this customer? This action cannot be undone.'))
            return;
        try {
            const res = await fetch(`${API_BASE_URL}/customers/${id}`, {method: 'DELETE'});
            if (!res.ok) {
                const errorText = await res.text();
                let errorMessage = 'Failed to delete customer.';
                try {
                    const errorJson = JSON.parse(errorText);
                    errorMessage = errorJson.message || errorMessage;
                } catch (jsonParseError) {
                    errorMessage = errorText || errorMessage;
                }
                throw new Error(errorMessage);
            }
            await fetchCustomers();
            showNotification('Customer deleted successfully!', 'success');
        } catch (e) {
            showNotification(`Error: ${e.message}`, 'error');
            console.error('Delete failed:', e);
        }
    };

    const openCustomerModal = (c = null) => {
        customerForm.reset();
        custNameInput.classList.remove('input-invalid');
        custAddressInput.classList.remove('input-invalid');
        custPhoneInput.classList.remove('input-invalid');
        errorCustName.classList.add('hidden');
        errorCustAddress.classList.add('hidden');
        errorCustPhone.classList.add('hidden');


        if (c) {
            modalTitle.textContent = 'Edit Customer';
            document.getElementById('customer-id-hidden').value = c.customerId;
            document.getElementById('cust-acc-no').value = c.accountNumber;
            custNameInput.value = c.name;
            custAddressInput.value = c.address;
            custPhoneInput.value = c.telephone;
        } else {
            modalTitle.textContent = 'Add New Customer';
            document.getElementById('customer-id-hidden').value = '';
            document.getElementById('cust-acc-no').value = getNextAccountNumber();
        }
        customerModal.classList.remove('hidden');
    };

    const closeCustomerModal = () => customerModal.classList.add('hidden');

    openViewCustomerModal = (c) => {
        document.getElementById('view-acc-no').textContent = c.accountNumber;
        document.getElementById('view-name').textContent = c.name;
        document.getElementById('view-address').textContent = c.address || 'N/A';
        viewPhoneSpan.textContent = c.telephone || 'N/A';
        viewUnitsConsumedSpan.textContent = c.unitsConsumed || '0';

        validateViewPhoneNumber(c.telephone);

        viewCustomerModal.classList.remove('hidden');
    };

    closeViewModalBtn.addEventListener('click', () => {
        viewCustomerModal.classList.add('hidden');
        viewPhoneErrorSpan.classList.add('hidden');
    });

    searchInput.addEventListener('input', (e) => {
        const term = e.target.value.toLowerCase();
        const filtered = allCustomers.filter(c =>
            c.name.toLowerCase().includes(term) ||
                    c.accountNumber.toLowerCase().includes(term) ||
                    (c.telephone && c.telephone.includes(term))
        );
        renderTable(filtered);
    });

    addCustomerBtn.addEventListener('click', () => openCustomerModal());
    cancelCustomerBtn.addEventListener('click', closeCustomerModal);

    customerForm.addEventListener('submit', handleSaveCustomer);

    custNameInput.addEventListener('input', () => validateInput(custNameInput, errorCustName, null, 'Full Name is required.', true));
    custAddressInput.addEventListener('input', () => validateInput(custAddressInput, errorCustAddress, null, 'Address is required.', false));
    custPhoneInput.addEventListener('input', () => validateInput(custPhoneInput, errorCustPhone, /^\d{10}$/, 'Please enter a valid 10-digit telephone number (e.g., 0712345678).', false));


    tableBody.addEventListener('click', (e) => {
        const target = e.target;
        const id = target.dataset.id;
        if (target.classList.contains('edit-btn')) {
            openCustomerModal(allCustomers.find(c => c.customerId == id));
        }
        if (target.classList.contains('delete-btn')) {
            handleDeleteCustomer(id);
        }
        if (target.classList.contains('view-customer')) {
            openViewCustomerModal(allCustomers.find(c => c.customerId == id));
        }
    });

    fetchCustomers();
})();