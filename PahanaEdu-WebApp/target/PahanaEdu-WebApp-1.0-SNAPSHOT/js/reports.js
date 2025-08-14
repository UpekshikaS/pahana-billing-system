(() => {
    const API_BASE = 'http://localhost:8080/PahanaEdu-BookShop-Server/api/reports';
                    const reportContent = document.getElementById('report-content');
                    const tabButtons = document.querySelectorAll('.tab-button');
                    let currentTab = 'sales';

                    const fetchSalesData = async (startDate, endDate) => {
                            const res = await fetch(`${API_BASE}/sales-summary?startDate=${startDate}&endDate=${endDate}`);
                            if (!res.ok)
                                    return;
                            const data = await res.json();
                            const revenue = Number(data.totalRevenue ?? 0);
                            document.getElementById('total-revenue').textContent = `LKR ${revenue.toLocaleString(undefined, {minimumFractionDigits: 2})}`;
                            document.getElementById('total-invoices').textContent = data.totalInvoices ?? 0;
                            document.getElementById('best-item').textContent = data.bestSellingItemName || '-';

                            renderSalesGrowthChart(data.salesOverTime || []);
                    };

                    const renderSalesGrowthChart = (salesData) => {
                            const ctx = document.getElementById('sales-growth-chart').getContext('2d');

                            // Format dates as DD/MM/YYYY
                            const labels = salesData.map(item => {
                                    const d = new Date(item.date);
                                    return d.toLocaleDateString(undefined, {day: '2-digit', month: '2-digit', year: 'numeric'});
                            });

                            const values = salesData.map(item => Number(item.revenue ?? 0));

                            new Chart(ctx, {
                                    type: 'line',
                                    data: {
                                            labels,
                                            datasets: [{
                                                            label: 'Sales Growth (LKR)',
                                                            data: values,
                                                            borderColor: '#4f46e5',
                                                            backgroundColor: 'rgba(79, 70, 229, 0.1)',
                                                            tension: 0.4,
                                                            fill: true,
                                                            pointRadius: 4,
                                                            pointHoverRadius: 6,
                                                    }]
                                    },
                                    options: {
                                            responsive: true,
                                            plugins: {
                                                    legend: {
                                                            display: true,
                                                            position: 'top'
                                                    },
                                                    tooltip: {
                                                            callbacks: {
                                                                    label: ctx => `LKR ${ctx.parsed.y.toLocaleString(undefined, {minimumFractionDigits: 2})}`
                                                            }
                                                    }
                                            },
                                            scales: {
                                                    x: {
                                                            title: {display: true, text: 'Date'}
                                                    },
                                                    y: {
                                                            title: {display: true, text: 'Revenue (LKR)'},
                                                            beginAtZero: true
                                                    }
                                            }
                                    }
                            });
                    };

                    const fetchTopCustomers = async (startDate, endDate) => {
                            const res = await fetch(`${API_BASE}/top-customers?startDate=${startDate}&endDate=${endDate}`);
                            if (!res.ok)
                                    return;
                            const customers = await res.json();
                            const tbody = document.getElementById('customer-body');
                            tbody.innerHTML = '';
                            customers.forEach(c => {
                                    const row = `<tr>
                            <td class="px-6 py-4 text-sm font-medium text-gray-900">${c.accountNumber}</td>
                            <td class="px-6 py-4 text-sm text-gray-600">${c.name}</td>
                            <td class="px-6 py-4 text-sm text-gray-800 font-medium">LKR ${Number(c.totalSpent).toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
                            <td class="px-6 py-4 text-sm text-center font-bold">${c.totalInvoices}</td>
                        </tr>`;
                                    tbody.insertAdjacentHTML('beforeend', row);
                            });
                    };

                    const loadReport = () => {
                            reportContent.innerHTML = '';
                            const template = document.getElementById(`${currentTab}-report-template`).content.cloneNode(true);
                            reportContent.appendChild(template);

                            const startDate = document.getElementById('start-date').value;
                            const endDateInput = document.getElementById('end-date').value;

                            if (!startDate || !endDateInput)
                                    return;

                            const adjustedEndDate = new Date(endDateInput);
                            adjustedEndDate.setDate(adjustedEndDate.getDate() + 1);
                            const formattedEndDate = adjustedEndDate.toISOString().split('T')[0];


                            if (currentTab === 'sales') {
                                    fetchSalesData(startDate, formattedEndDate);
                            } else if (currentTab === 'customers') {
                                    fetchTopCustomers(startDate, formattedEndDate);
                            }
                    
                            const exportBtn = document.querySelector('.export-btn');
                            exportBtn?.addEventListener('click', () => {
                                    let endpoint = '';
                                    if (currentTab === 'sales') {
                                            endpoint = `${API_BASE}/export-sales?startDate=${startDate}&endDate=${formattedEndDate}`;
                                    } else if (currentTab === 'customers') {
                                            endpoint = `${API_BASE}/export-customers?startDate=${startDate}&endDate=${formattedEndDate}`;
                                    }
                                    if (endpoint) {
                                            window.open(endpoint, '_blank');
                                    }
                            });

                    };

                    tabButtons.forEach(btn => {
                            btn.addEventListener('click', () => {
                                    tabButtons.forEach(b => b.classList.remove('active'));
                                    btn.classList.add('active');
                                    currentTab = btn.dataset.tab;
                                    loadReport();
                            });
                    });

                    document.getElementById('generate-report-btn').addEventListener('click', loadReport);

                    const today = new Date().toISOString().split('T')[0];
                    document.getElementById('start-date').value = today;
                    document.getElementById('end-date').value = today;
                    loadReport('sales');
 })();