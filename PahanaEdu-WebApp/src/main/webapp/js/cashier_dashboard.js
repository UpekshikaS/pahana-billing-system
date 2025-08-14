(() => {
    const mainContent = document.getElementById('main-content');
    const pageTitle = document.getElementById('page-title');
    const navLinks = document.querySelectorAll('.nav-link');
    const sidebar = document.getElementById('sidebar');
    const menuToggle = document.getElementById('menu-toggle');
    const notificationBell = document.getElementById('notification-bell');
    const notificationBadge = document.getElementById('notification-badge');
    const logoutButtonHeader = document.getElementById('logout-button');
    const logoutButtonSidebar = document.getElementById('logout-button-sidebar');
    const helpButton = document.getElementById('help-button');

    const loggedInUser = JSON.parse(sessionStorage.getItem('loggedInUser'));
    const apiBaseUrl = "http://localhost:8080/PahanaEdu-BookShop-Server/api";

    if (!loggedInUser) {
        alert('You are not logged in. Redirecting to login page.');
        window.location.href = 'login.html';
        return;
    }

    document.getElementById('profile-name').textContent = loggedInUser.name || loggedInUser.username;

    const loadPage = async (pageUrl) => {
        mainContent.innerHTML = '<div class="flex justify-center items-center h-64"><div class="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-sky-500"></div></div>';
        try {
            const response = await fetch(`${pageUrl}?_=${Date.now()}`);
            if (!response.ok) throw new Error(`Could not load page. Status: ${response.status}`);
            const pageContent = await response.text();
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = pageContent;
            const bodyElement = tempDiv.querySelector('body');
            mainContent.innerHTML = bodyElement ? bodyElement.innerHTML : pageContent;
            const scripts = tempDiv.querySelectorAll('script');
            scripts.forEach(script => {
                const newScript = document.createElement('script');
                if (script.src) newScript.src = script.src;
                else newScript.textContent = script.textContent;
                [...script.attributes].forEach(attr => newScript.setAttribute(attr.name, attr.value));
                document.body.appendChild(newScript);
                if (!script.src) newScript.remove();
            });
        } catch (error) {
            console.error('Failed to load page:', error);
            mainContent.innerHTML = `<div class="text-center text-red-500"><p>Error loading page.</p><p>${error.message}</p></div>`;
        }
    };

    window.fetchUnreadNotificationCount = async () => {
        if (!loggedInUser?.userId) {
            notificationBadge.classList.add('hidden');
            return;
        }
        try {
            const response = await fetch(`${apiBaseUrl}/notifications/unread/${loggedInUser.userId}`);
            if (!response.ok) throw new Error('Failed to fetch unread notifications');
            const notifications = await response.json();
            const unreadCount = notifications.length;
            if (unreadCount > 0) {
                notificationBadge.textContent = unreadCount;
                notificationBadge.classList.remove('hidden');
            } else {
                notificationBadge.classList.add('hidden');
            }
        } catch (error) {
            console.error("Error fetching unread notifications:", error);
        }
    };

    document.getElementById('sidebar-nav').addEventListener('click', (e) => {
        const link = e.target.closest('.nav-link');
        if (!link) return;
        e.preventDefault();
        const pageUrl = link.dataset.page;
        if (!pageUrl) return;
        sessionStorage.setItem('currentPage', pageUrl);
        const pageName = link.querySelector('span').textContent;
        sessionStorage.setItem('currentPageTitle', pageName);
        navLinks.forEach(l => l.classList.remove('active'));
        link.classList.add('active');
        pageTitle.textContent = pageName;
        loadPage(pageUrl);
        if (window.innerWidth < 768) sidebar.classList.add('-translate-x-full');
    });

    document.getElementById('profile-name').closest('.nav-link').addEventListener('click', (e) => {
        const link = e.currentTarget;
        e.preventDefault();
        const pageUrl = link.dataset.page;
        if (!pageUrl) return;
        sessionStorage.setItem('currentPage', pageUrl);
        const pageName = link.querySelector('span').textContent;
        sessionStorage.setItem('currentPageTitle', pageName);
        navLinks.forEach(l => l.classList.remove('active'));
        link.classList.add('active');
        pageTitle.textContent = pageName;
        loadPage(pageUrl);
        if (window.innerWidth < 768) sidebar.classList.add('-translate-x-full');
    });

    menuToggle.addEventListener('click', () => {
        sidebar.classList.toggle('-translate-x-full');
    });

    const logout = () => {
        sessionStorage.clear();
        window.location.href = "login.html";
    };
    logoutButtonHeader.addEventListener('click', logout);
    logoutButtonSidebar.addEventListener('click', logout);

    notificationBell.addEventListener('click', () => {
        document.querySelector('.nav-link[data-page="notifications.html"]')?.click();
    });
    helpButton.addEventListener('click', () => {
        document.querySelector('.nav-link[data-page="help.html"]')?.click();
    });

    const storedPage = sessionStorage.getItem('currentPage');
    const storedPageTitle = sessionStorage.getItem('currentPageTitle');
    let pageToLoad = 'billing_page_integrated.html';
    let titleToSet = 'Billing / Invoicing';
    if (storedPage && storedPageTitle) {
        pageToLoad = storedPage;
        titleToSet = storedPageTitle;
        const activeLink = document.querySelector(`.nav-link[data-page="${storedPage}"]`);
        if (activeLink) {
            navLinks.forEach(l => l.classList.remove('active'));
            activeLink.classList.add('active');
        }
    } else {
        document.querySelector('.nav-link[data-page="billing_page_integrated.html"]')?.classList.add('active');
    }
    pageTitle.textContent = titleToSet;
    loadPage(pageToLoad);
    window.fetchUnreadNotificationCount();
    setInterval(window.fetchUnreadNotificationCount, 60000);
})();