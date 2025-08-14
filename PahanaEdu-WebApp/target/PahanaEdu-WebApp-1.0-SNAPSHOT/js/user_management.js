(() => {
    const API_BASE_URL = 'http://localhost:8080/PahanaEdu-BookShop-Server/api';
    let allUsers = [];

    const tableBody = document.getElementById('users-table-body');
    const searchInput = document.getElementById('user-search');
    const generalErrorDiv = document.getElementById('general-error');
    const notificationArea = document.getElementById('notification-area');

    const addUserBtn = document.getElementById('add-user-btn');
    const cancelUserBtn = document.getElementById('cancel-user-btn');
    const saveUserBtn = document.getElementById('save-user-btn');
    const userModal = document.getElementById('user-modal');
    const userForm = document.getElementById('user-form');
    const passwordInput = document.getElementById('user-password');
    const modalTitle = document.getElementById('user-modal-title');

    const userFullnameInput = document.getElementById('user-fullname');
    const userUsernameInput = document.getElementById('user-username');
    const userEmailInput = document.getElementById('user-email');
    const userPhoneInput = document.getElementById('user-phone');
    const userAddressInput = document.getElementById('user-address');
    const userPasswordInput = document.getElementById('user-password');


    const errorUserFullname = document.getElementById('error-user-fullname');
    const errorUserUsername = document.getElementById('error-user-username');
    const errorUserEmail = document.getElementById('error-user-email');
    const errorUserPhone = document.getElementById('error-user-phone');
    const errorUserAddress = document.getElementById('error-user-address');
    const errorUserPassword = document.getElementById('error-user-password');

    const viewUserModal = document.getElementById('view-user-modal');
    const closeViewUserModalBtn = document.getElementById('close-view-user-modal');
    const viewUserPhoneSpan = document.getElementById('view-user-phone');
    const viewUserPhoneErrorSpan = document.getElementById('view-user-phone-error');

    const showGeneralError = (msg) => {
        generalErrorDiv.textContent = msg;
        generalErrorDiv.classList.remove('hidden');
    };

    const hideGeneralError = () => generalErrorDiv.classList.add('hidden');


    const getRoleBadge = (role) => {
        const cls = role === 'ADMIN' ? 'bg-blue-100 text-blue-800' : 'bg-green-100 text-green-800';
        return `<span class="px-2 py-1 rounded text-xs ${cls}">${role}</span>`;
    };

    const renderTable = (users) => {
        tableBody.innerHTML = '';
        if (users.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-10 text-gray-500"><i class="fas fa-users-slash fa-2x"></i><p class="mt-2">No users found</p></td></tr>`;
            return;
        }
        users.forEach(user => {
            tableBody.innerHTML += `
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-indigo-600 hover:underline cursor-pointer account-no" data-id="${user.userId}">${user.username}</td>
                                <td class="px-6 py-4 text-sm">${user.name}</td>
                                <td class="px-6 py-4 text-sm">${getRoleBadge(user.role)}</td>
                                <td class="px-6 py-4 text-sm">${user.email || 'N/A'}</td>
                                <td class="px-6 py-4 text-sm">${user.phone || 'N/A'}</td>
                                <td class="px-6 py-4 text-sm text-right">
                                    <button class="text-indigo-600 hover:text-indigo-900 edit-btn" data-id="${user.userId}">Edit</button>
                                </td>
                            </tr>
                        `;
        });
    };

    const fetchUsers = async () => {
        hideGeneralError();
        try {
            const res = await fetch(`${API_BASE_URL}/users`);
            if (!res.ok)
                throw new Error('Failed to fetch users');
            allUsers = await res.json();
            renderTable(allUsers);
        } catch (err) {
            showGeneralError('Could not load users from the server.');
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

        setTimeout(() => {
            notificationDiv.classList.remove('show');
            notificationDiv.classList.add('opacity-0', 'translate-x-full');
            notificationDiv.addEventListener('transitionend', () => {
                notificationDiv.remove();
            }, {once: true});
        }, 5000);
    };

    const validateInput = (inputElement, errorElement, regex, errorMessage, isRequired = true) => {
        const value = inputElement.value.trim();
        let isValid = true;

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

    const validateUserForm = (isEditMode) => {
        let isValid = true;

        if (!validateInput(userFullnameInput, errorUserFullname, null, 'Full Name is required.', true)) {
            isValid = false;
        }

        if (!isEditMode && !validateInput(userUsernameInput, errorUserUsername, null, 'Account No is required.', true)) {
            isValid = false;
        }

        if (!validateInput(userEmailInput, errorUserEmail, /^[^\s@]+@[^\s@]+\.[^\s@]+$/, 'Please enter a valid email address.', false)) {
            isValid = false;
        }

        if (!validateInput(userPhoneInput, errorUserPhone, /^\d{10}$/, 'Please enter a valid 10-digit phone number (e.g., 0712345678).', false)) {
            isValid = false;
        }

        if (!isEditMode && !validateInput(userPasswordInput, errorUserPassword, null, 'Password is required.', true)) {
            isValid = false;
        } else if (isEditMode && userPasswordInput.value.trim() !== '' && userPasswordInput.value.trim().length < 6) { // Example: min 6 chars for password if changed
            isValid = false;
            errorUserPassword.textContent = 'Password must be at least 6 characters long.';
            userPasswordInput.classList.add('input-invalid');
            errorUserPassword.classList.remove('hidden');
        }

        validateInput(userAddressInput, errorUserAddress, null, '', false);


        return isValid;
    };

    const validateViewUserPhoneNumber = (phoneNumber) => {
        viewUserPhoneErrorSpan.textContent = '';
        viewUserPhoneErrorSpan.classList.add('hidden');

        const cleanPhoneNumber = String(phoneNumber || '').replace(/\s|-|\(|\)/g, '');

        if (!cleanPhoneNumber || cleanPhoneNumber === 'N/A') {
            return true;
        }

        if (!/^\d{10}$/.test(cleanPhoneNumber)) {
            viewUserPhoneErrorSpan.textContent = 'Invalid Phone No Format (Expected 10 digits).';
            viewUserPhoneErrorSpan.classList.remove('hidden');
            return false;
        }
        return true;
    };

    const openUserModal = (user = null) => {
        userForm.reset();
        userFullnameInput.classList.remove('input-invalid');
        userUsernameInput.classList.remove('input-invalid');
        userEmailInput.classList.remove('input-invalid');
        userPhoneInput.classList.remove('input-invalid');
        userAddressInput.classList.remove('input-invalid');
        userPasswordInput.classList.remove('input-invalid');

        errorUserFullname.classList.add('hidden');
        errorUserUsername.classList.add('hidden');
        errorUserEmail.classList.add('hidden');
        errorUserPhone.classList.add('hidden');
        errorUserAddress.classList.add('hidden');
        errorUserPassword.classList.add('hidden');

        passwordInput.required = !user;
        modalTitle.textContent = user ? 'Edit User' : 'Add New User';

        if (user) {
            document.getElementById('user-id-hidden').value = user.userId;
            userFullnameInput.value = user.name;
            userUsernameInput.value = user.username;
            userUsernameInput.disabled = true;
            userEmailInput.value = user.email;
            userPhoneInput.value = user.phone;
            userAddressInput.value = user.address;
            document.getElementById('user-role').value = user.role;
        } else {
            document.getElementById('user-id-hidden').value = '';
            userUsernameInput.disabled = false; // Enable username for new user
        }

        userModal.classList.remove('hidden');
    };

    const closeUserModal = () => userModal.classList.add('hidden');

    const handleSaveUser = async (event) => {
        event.preventDefault(); // Prevent default form submission

        const id = document.getElementById('user-id-hidden').value;
        const isEdit = !!id;

        if (!validateUserForm(isEdit)) {
            showNotification('Please correct the errors in the form.', 'error');
            return;
        }

        saveUserBtn.disabled = true;
        saveUserBtn.textContent = 'Saving...';

        const userData = {
            username: userUsernameInput.value,
            name: userFullnameInput.value,
            email: userEmailInput.value,
            phone: userPhoneInput.value,
            address: userAddressInput.value,
            role: document.getElementById('user-role').value,
            passwordHash: passwordInput.value || null // Send null if password is empty (for update)
        };

        const url = isEdit ? `${API_BASE_URL}/users/${id}` : `${API_BASE_URL}/users`;
        const method = isEdit ? 'PUT' : 'POST';

        try {
            const res = await fetch(url, {
                method,
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(userData)
            });

            if (!res.ok) {
                const errorText = await res.text();
                let errorMessage = `Failed to ${isEdit ? 'update' : 'add'} user.`;
                try {
                    const errorJson = JSON.parse(errorText);
                    errorMessage = errorJson.message || errorMessage;
                } catch (jsonParseError) {
                    errorMessage = errorText || errorMessage;
                }
                throw new Error(errorMessage);
            }

            closeUserModal();
            await fetchUsers();
            const successMessage = isEdit ? 'User updated successfully!' : 'User added successfully!';
            showNotification(successMessage, 'success');
        } catch (e) {
            showNotification(`Error: ${e.message}`, 'error');
            console.error('Save failed:', e);
        } finally {
            saveUserBtn.disabled = false;
            saveUserBtn.textContent = 'Save';
        }
    };

    const showUserDetailsPopup = (user) => {
        document.getElementById('view-user-username').textContent = user.username;
        document.getElementById('view-user-name').textContent = user.name;
        document.getElementById('view-user-role').textContent = user.role;
        document.getElementById('view-user-email').textContent = user.email || 'N/A';
        viewUserPhoneSpan.textContent = user.phone || 'N/A';
        document.getElementById('view-user-address').textContent = user.address || 'N/A';

        validateViewUserPhoneNumber(user.phone);

        viewUserModal.classList.remove('hidden');
    };

    closeViewUserModalBtn.addEventListener('click', () => {
        viewUserModal.classList.add('hidden');
        viewUserPhoneErrorSpan.classList.add('hidden');
    });


    searchInput.addEventListener('input', (e) => {
        const val = e.target.value.toLowerCase();
        const filtered = allUsers.filter(u =>
            u.name.toLowerCase().includes(val) || u.username.toLowerCase().includes(val)
        );
        renderTable(filtered);
    });

    addUserBtn.addEventListener('click', () => openUserModal());
    cancelUserBtn.addEventListener('click', () => closeUserModal());

    userForm.addEventListener('submit', handleSaveUser); // Changed to submit event

    // Add event listeners for real-time validation feedback
    userFullnameInput.addEventListener('input', () => validateInput(userFullnameInput, errorUserFullname, null, 'Full Name is required.', true));
    userUsernameInput.addEventListener('input', () => {
        const isEdit = !!document.getElementById('user-id-hidden').value;
        if (!isEdit) {
            validateInput(userUsernameInput, errorUserUsername, null, 'Account No is required.', true);
        }
    });
    userEmailInput.addEventListener('input', () => validateInput(userEmailInput, errorUserEmail, /^[^\s@]+@[^\s@]+\.[^\s@]+$/, 'Please enter a valid email address.', false));
    userPhoneInput.addEventListener('input', () => validateInput(userPhoneInput, errorUserPhone, /^\d{10}$/, 'Please enter a valid 10-digit phone number (e.g., 0712345678).', false));
    userAddressInput.addEventListener('input', () => validateInput(userAddressInput, errorUserAddress, null, '', false));
    userPasswordInput.addEventListener('input', () => {
        const isEdit = !!document.getElementById('user-id-hidden').value;
        if (!isEdit || userPasswordInput.value.trim() !== '') {
            validateInput(userPasswordInput, errorUserPassword, null, 'Password is required.', !isEdit);
            if (isEdit && userPasswordInput.value.trim() !== '' && userPasswordInput.value.trim().length < 6) {
                errorUserPassword.textContent = 'Password must be at least 6 characters long.';
                userPasswordInput.classList.add('input-invalid');
                errorUserPassword.classList.remove('hidden');
            }
        }
    });


    tableBody.addEventListener('click', (e) => {
        const id = e.target.dataset.id;
        if (e.target.classList.contains('edit-btn')) {
            const user = allUsers.find(u => u.userId == id);
            openUserModal(user);
        } else if (e.target.classList.contains('account-no')) {
            const user = allUsers.find(u => u.userId == id);
            showUserDetailsPopup(user);
        }
    });

    fetchUsers();
})();