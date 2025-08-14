(() => {
    // --- CONFIG & STATE ---
    const API_BASE_URL = 'http://localhost:8080/PahanaEdu-BookShop-Server/api';
    let allProducts = [];

    const tableBody = document.getElementById('products-table-body');
    const searchInput = document.getElementById('product-search');
    const generalErrorDiv = document.getElementById('general-error');
    const notificationArea = document.getElementById('notification-area');

    const productModal = document.getElementById('product-modal');
    const addProductBtn = document.getElementById('add-product-btn');
    const cancelProductBtn = document.getElementById('cancel-product-btn');
    const saveProductBtn = document.getElementById('save-product-btn');
    const productForm = document.getElementById('product-form');
    const modalTitle = document.getElementById('modal-title');

    const viewProductModal = document.getElementById('view-product-modal');
    const closeViewProductModalBtn = document.getElementById('close-view-product-modal');
    const viewProductItemId = document.getElementById('view-product-item-id');
    const viewProductName = document.getElementById('view-product-name');
    const viewProductDesc = document.getElementById('view-product-desc');
    const viewProductPrice = document.getElementById('view-product-price');
    const viewProductStock = document.getElementById('view-product-stock');
    const viewProductStatus = document.getElementById('view-product-status');


    const showGeneralError = (message) => {
        generalErrorDiv.textContent = message;
        generalErrorDiv.classList.remove('hidden');
    };

    const hideGeneralError = () => {
        generalErrorDiv.classList.add('hidden');
    };

    const showLoading = () => {
        tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-10"><div class="table-loader mx-auto"></div></td></tr>`;
    };

    const formatCurrency = (amount) => `LKR ${Number(amount).toFixed(2)}`;

    const getStockStatus = (stock) => {
        if (stock <= 0)
            return `<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">Out of Stock</span>`;
        if (stock < 20)
            return `<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">Low Stock</span>`;
        return `<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">In Stock</span>`;
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

    const renderTable = (products) => {
        tableBody.innerHTML = '';
        if (products.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-10 text-gray-500"><i class="fas fa-box-open fa-2x"></i><p class="mt-2">No products found</p></td></tr>`;
            return;
        }

        products.forEach(product => {
            const row = document.createElement('tr');
            row.innerHTML = `
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-indigo-600 hover:underline cursor-pointer item-id-link" data-id="${product.productId}">${product.itemId}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">${product.name}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">${formatCurrency(product.price)}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-800 text-center font-bold">${product.stockQuantity}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${getStockStatus(product.stockQuantity)}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                    <button class="text-indigo-600 hover:text-indigo-900 edit-btn" data-id="${product.productId}">Edit</button>
                                    <button class="text-red-600 hover:text-red-900 ml-4 delete-btn" data-id="${product.productId}">Delete</button>
                                </td>
                            </tr>
                        `;
            tableBody.appendChild(row);
        });
    };

    const uploadInput = document.getElementById('excel-upload');
    const uploadButton = document.getElementById('bulk-upload-btn');

    uploadButton.addEventListener('click', () => uploadInput.click());

    uploadInput.addEventListener('change', async () => {
        const file = uploadInput.files[0];
        if (!file)
            return;

        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await fetch(`${API_BASE_URL}/products/upload`, {
                method: 'POST',
                body: formData
            });

            const result = await response.json();

            if (!response.ok)
                throw new Error(result.error || 'Upload failed.');

            showNotification(result.message, 'success'); // Use new notification
            fetchProducts();
        } catch (error) {
            showNotification(`Upload Failed: ${error.message}`, 'error');
        } finally {
            uploadInput.value = '';
        }
    });


    const fetchProducts = async () => {
        showLoading();
        hideGeneralError();
        try {
            const response = await fetch(`${API_BASE_URL}/products`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            allProducts = await response.json();

            const hasLowStock = allProducts.some(product => product.stockQuantity > 0 && product.stockQuantity < 20);
            const lowStockDiv = document.getElementById('low-stock-notification');
            if (hasLowStock) {
                lowStockDiv.classList.remove('hidden');
            } else {
                lowStockDiv.classList.add('hidden');
            }

            renderTable(allProducts);
        } catch (error) {
            console.error('Failed to fetch products:', error);
            showGeneralError('Could not load products from the server. Please try again later.');
            tableBody.innerHTML = '';
        }
    };


    const handleSaveProduct = async () => {
        if (!productForm.checkValidity()) {
            productForm.reportValidity();
            showNotification('Please fill in all required fields.', 'error');
            return;
        }

        saveProductBtn.disabled = true;
        saveProductBtn.textContent = 'Saving...';

        const productData = {
            itemId: document.getElementById('item-id').value.toUpperCase(),
            name: document.getElementById('product-name').value,
            description: document.getElementById('product-desc').value,
            price: parseFloat(document.getElementById('product-price').value),
            stockQuantity: parseInt(document.getElementById('product-stock').value)
        };

        const hiddenId = document.getElementById('product-id-hidden').value;
        const isEditing = !!hiddenId;

        const url = isEditing ? `${API_BASE_URL}/products/${hiddenId}` : `${API_BASE_URL}/products`;
        const method = isEditing ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: method,
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(productData)
            });
            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.error || `Failed to ${isEditing ? 'update' : 'add'} product.`);
            }
            closeProductModal();
            await fetchProducts(); // Refresh the table
            showNotification(`Product ${isEditing ? 'updated' : 'added'} successfully!`, 'success');
        } catch (error) {
            showNotification(`Error: ${error.message}`, 'error');
        } finally {
            saveProductBtn.disabled = false;
            saveProductBtn.textContent = 'Save';
        }
    };

    const handleDeleteProduct = async (productId) => {
        Swal.fire({
            title: 'Are you sure?',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Yes, delete it!'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const response = await fetch(`${API_BASE_URL}/products/${productId}`, {
                        method: 'DELETE'
                    });
                    if (!response.ok) {
                        const errData = await response.json();
                        throw new Error(errData.error || 'Failed to delete product.');
                    }
                    await fetchProducts(); // Refresh the table
                    showNotification('Product deleted successfully!', 'success');
                } catch (error) {
                    showNotification(`Error deleting product: ${error.message}`, 'error');
                }
            }
        });
    };

    const openProductModal = (product = null) => {
        productForm.reset();
        if (product) {
            modalTitle.textContent = 'Edit Product';
            document.getElementById('product-id-hidden').value = product.productId;
            document.getElementById('item-id').value = product.itemId;
            document.getElementById('item-id').disabled = true;
            document.getElementById('product-name').value = product.name;
            document.getElementById('product-desc').value = product.description;
            document.getElementById('product-price').value = product.price;
            document.getElementById('product-stock').value = product.stockQuantity;
        } else {
            modalTitle.textContent = 'Add New Product';
            document.getElementById('product-id-hidden').value = '';
            document.getElementById('item-id').disabled = false;
        }
        productModal.classList.remove('hidden');
    };

    const closeProductModal = () => productModal.classList.add('hidden');

    const openViewProductModal = (product) => {
        viewProductItemId.textContent = product.itemId;
        viewProductName.textContent = product.name;
        viewProductDesc.textContent = product.description || 'N/A';
        viewProductPrice.textContent = formatCurrency(product.price);
        viewProductStock.textContent = product.stockQuantity;
        viewProductStatus.innerHTML = getStockStatus(product.stockQuantity);

        viewProductModal.classList.remove('hidden');
    };

    const closeViewProductModal = () => {
        viewProductModal.classList.add('hidden');
    };

    searchInput.addEventListener('input', (e) => {
        const searchTerm = e.target.value.toLowerCase();
        const filteredProducts = allProducts.filter(p =>
            p.name.toLowerCase().includes(searchTerm) ||
                    p.itemId.toLowerCase().includes(searchTerm)
        );
        renderTable(filteredProducts);
    });

    addProductBtn.addEventListener('click', () => openProductModal());
    cancelProductBtn.addEventListener('click', closeProductModal);
    saveProductBtn.addEventListener('click', handleSaveProduct);
    closeViewProductModalBtn.addEventListener('click', closeViewProductModal);


    tableBody.addEventListener('click', (e) => {
        const target = e.target;
        const productId = target.dataset.id;

        if (target.classList.contains('edit-btn')) {
            const productToEdit = allProducts.find(p => p.productId == productId);
            openProductModal(productToEdit);
        } else if (target.classList.contains('delete-btn')) {
            handleDeleteProduct(productId);
        } else if (target.classList.contains('item-id-link')) {
            const productToView = allProducts.find(p => p.productId == productId);
            openViewProductModal(productToView);
        }
    });

    fetchProducts();
})();