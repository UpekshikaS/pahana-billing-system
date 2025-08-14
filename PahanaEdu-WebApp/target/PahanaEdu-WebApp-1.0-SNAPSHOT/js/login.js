
const loginForm = document.getElementById('login-form');
const errorMessage = document.getElementById('error-message');
const buttonText = document.getElementById('button-text');
const buttonSpinner = document.getElementById('button-spinner');
const passwordInput = document.getElementById('password');
const togglePasswordButton = document.getElementById('toggle-password-visibility');
const eyeOpenIcon = document.getElementById('eye-icon-open');
const eyeClosedIcon = document.getElementById('eye-icon-closed');

// Show/Hide password functionality
togglePasswordButton.addEventListener('click', () => {
    const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
    passwordInput.setAttribute('type', type);
    eyeOpenIcon.classList.toggle('hidden');
    eyeClosedIcon.classList.toggle('hidden');
});

const API_BASE_URL = 'http://localhost:8080/PahanaEdu-BookShop-Server/api';

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    errorMessage.classList.add('hidden');
    buttonText.classList.add('invisible');
    buttonSpinner.classList.remove('hidden');
    loginForm.querySelector('button[type="submit"]').disabled = true;

    const username = loginForm.username.value;
    const password = loginForm.password.value;

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({username, password}),
        });

        const data = await response.json();

        if (response.ok) {
            sessionStorage.setItem('loggedInUser', JSON.stringify(data));

            // Redirect based on role
            if (data.role === 'ADMIN') {
                window.location.href = 'admin_dashboard.html';
            } else if (data.role === 'CASHIER') {
                window.location.href = 'cashier_dashboard.html';
            } else {
                // Handle unknown roles if necessary
                errorMessage.textContent = 'Login successful, but role is undefined.';
                errorMessage.classList.remove('hidden');
            }
        } else {
            errorMessage.textContent = data.error || 'An unknown error occurred.';
            errorMessage.classList.remove('hidden');
        }

    } catch (error) {
        console.error('Login request failed:', error);
        errorMessage.textContent = 'Cannot connect to the server. Please try again later.';
        errorMessage.classList.remove('hidden');
    } finally {
        buttonText.classList.remove('invisible');
        buttonSpinner.classList.add('hidden');
        loginForm.querySelector('button[type="submit"]').disabled = false;
    }
});