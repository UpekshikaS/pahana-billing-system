(() => {
    const loggedInUser = JSON.parse(sessionStorage.getItem('loggedInUser'));
    const API_BASE_URL = 'http://localhost:8080/PahanaEdu-BookShop-Server/api';
    const form = document.getElementById('profile-form');
    const messageArea = document.getElementById('message-area');

    if (!loggedInUser) {
        window.location.href = 'login.html';
        return;
    }

    document.getElementById('username').value = loggedInUser.username;
    document.getElementById('name').value = loggedInUser.name || '';
    document.getElementById('email').value = loggedInUser.email || '';
    document.getElementById('phone').value = loggedInUser.phone || '';
    document.getElementById('address').value = loggedInUser.address || '';

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const button = form.querySelector('button[type="submit"]');
        button.disabled = true;
        button.textContent = 'Saving...';

        const requestBody = {
            name: document.getElementById('name').value,
            email: document.getElementById('email').value,
            phone: document.getElementById('phone').value,
            address: document.getElementById('address').value,
            newPassword: document.getElementById('new-password').value || null
        };

        try {
            const response = await fetch(`${API_BASE_URL}/users/profile/${loggedInUser.userId}`, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(requestBody)
            });
            const result = await response.json();
            if (!response.ok)
                throw new Error(result.error);

            loggedInUser.name = requestBody.name;
            loggedInUser.email = requestBody.email;
            loggedInUser.phone = requestBody.phone;
            loggedInUser.address = requestBody.address;
            sessionStorage.setItem('loggedInUser', JSON.stringify(loggedInUser));

            messageArea.textContent = 'Profile updated successfully!';
            messageArea.className = 'mb-4 p-3 rounded-md bg-green-100 text-green-800';
            messageArea.classList.remove('hidden');
            document.getElementById('new-password').value = '';
        } catch (error) {
            messageArea.textContent = `Error: ${error.message}`;
            messageArea.className = 'mb-4 p-3 rounded-md bg-red-100 text-red-800';
            messageArea.classList.remove('hidden');
        } finally {
            button.disabled = false;
            button.textContent = 'Save Changes';
        }
    });

})();