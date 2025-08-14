
(() => {
    const apiBaseUrl = "http://localhost:8080/PahanaEdu-BookShop-Server/api";

    async function fetchAllNotifications(userId) {
        try {
            const response = await fetch(`${apiBaseUrl}/notifications/all/${userId}`);
            if (!response.ok) {
                throw new Error('Failed to fetch notifications');
            }
            return await response.json();
        } catch (error) {
            console.error("Error fetching all notifications:", error);
            return [];
        }
    }

    async function markNotificationAsRead(notificationId) {
        try {
            await fetch(`${apiBaseUrl}/notifications/read/${notificationId}`, {
                method: "PUT"
            });
        } catch (error) {
            console.error("Error marking notification as read:", error);
        }
    }

    async function markAllNotificationsAsRead(userId) {
        try {
            await fetch(`${apiBaseUrl}/notifications/readAll/${userId}`, {
                method: "PUT"
            });
        } catch (error) {
            console.error("Error marking all notifications as read:", error);
        }
    }

    async function deleteSingleNotification(notificationId) {
        try {
            await fetch(`${apiBaseUrl}/notifications/delete/${notificationId}`, {
                method: "DELETE"
            });
        } catch (error) {
            console.error("Error deleting notification:", error);
        }
    }


    async function deleteAllNotifications(userId) {
        try {
            await fetch(`${apiBaseUrl}/notifications/deleteAll/${userId}`, {
                method: "DELETE"
            });
        } catch (error) {
            console.error("Error deleting all notifications:", error);
        }
    }

    async function renderNotifications() {
        const user = JSON.parse(sessionStorage.getItem("loggedInUser"));
        if (!user) {
            console.error("User not logged in.");
            return;
        }

        const notifications = await fetchAllNotifications(user.userId);
        const list = document.getElementById("notification-list");
        const markAllBtn = document.getElementById("mark-all-read-btn");
        const deleteAllBtn = document.getElementById("delete-all-btn");

        if (notifications.length === 0) {
            list.innerHTML = "<li class='text-gray-500'>No notifications</li>";
            markAllBtn.classList.add('hidden');
            deleteAllBtn.classList.add('hidden');
            return;
        } else {
            markAllBtn.classList.remove('hidden');
            deleteAllBtn.classList.remove('hidden');
        }

        list.innerHTML = "";
        notifications.forEach(n => {
            const item = document.createElement("li");
            item.className = `p-4 rounded-lg border notification-item ${n.read ? "read" : "unread"}`;

            let buttonHtml = '';
            if (!n.read) {
                buttonHtml += `<button class="text-indigo-500 hover:text-indigo-700 mark-as-read-btn p-2 rounded-full hover:bg-gray-200" data-id="${n.notificationId}" aria-label="Mark as Read"><i class="fas fa-eye"></i></button>`;
            }

            buttonHtml += `<button class="text-red-500 hover:text-red-700 delete-single-btn p-2 rounded-full hover:bg-gray-200" data-id="${n.notificationId}" aria-label="Remove Notification"><i class="fas fa-trash"></i></button>`;


            item.innerHTML = `
                    <div class="flex-grow">
                        <div class="notification-message font-medium">${n.message}</div>
                        <div class="notification-time text-xs text-gray-500">${new Date(n.createdAt).toLocaleString()}</div>
                    </div>
                    <div class="flex items-center space-x-2">
                        ${buttonHtml}
                    </div>
                `;
            list.appendChild(item);
        });

        document.querySelectorAll('.mark-as-read-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                e.stopPropagation();
                const notificationId = e.currentTarget.dataset.id;
                await markNotificationAsRead(notificationId);
                renderNotifications();
                if (window.parent && window.parent.fetchUnreadNotificationCount) {
                    window.parent.fetchUnreadNotificationCount();
                }
            });
        });

        document.querySelectorAll('.delete-single-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                e.stopPropagation();
                const notificationId = e.currentTarget.dataset.id;
                if (confirm("Are you sure you want to remove this notification?")) {
                    await deleteSingleNotification(notificationId);
                    renderNotifications();
                    if (window.parent && window.parent.fetchUnreadNotificationCount) {
                        window.parent.fetchUnreadNotificationCount();
                    }
                }
            });
        });

    }

    document.getElementById('mark-all-read-btn').addEventListener('click', async () => {
        const user = JSON.parse(sessionStorage.getItem("loggedInUser"));
        if (user) {
            await markAllNotificationsAsRead(user.userId);
            renderNotifications();
            if (window.parent && window.parent.fetchUnreadNotificationCount) {
                window.parent.fetchUnreadNotificationCount();
            }
        }
    });

    document.getElementById('delete-all-btn').addEventListener('click', async () => {
        const user = JSON.parse(sessionStorage.getItem("loggedInUser"));
        if (user) {
            if (confirm("Are you sure you want to delete all notifications? This action cannot be undone.")) {
                await deleteAllNotifications(user.userId);
                renderNotifications();
                if (window.parent && window.parent.fetchUnreadNotificationCount) {
                    window.parent.fetchUnreadNotificationCount();
                }
            }
        }
    });

    renderNotifications();
})();