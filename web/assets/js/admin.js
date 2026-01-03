/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

document.addEventListener('DOMContentLoaded', function() {
    
    // 1. SIDEBAR TOGGLE (MURNI UI)
    const sidebarToggleBtn = document.getElementById('sidebarToggleBtn');
    const sidebarOverlay = document.getElementById('sidebarOverlay');
    
    if (sidebarToggleBtn) {
        sidebarToggleBtn.addEventListener('click', function() {
            document.body.classList.toggle('sidebar-toggled');
        });
    }
    if (sidebarOverlay) {
        sidebarOverlay.addEventListener('click', function() {
            document.body.classList.remove('sidebar-toggled');
        });
    }

    // 2. TAB NAVIGATION (MURNI UI)
    const navLinks = document.querySelectorAll('#sidebar-nav .nav-link');
    const pages = document.querySelectorAll('.page-content');
    const pageTitle = document.getElementById('page-title');

    function activateTab(pageId) {
        pages.forEach(p => p.classList.remove('active'));
        navLinks.forEach(n => n.classList.remove('active'));
        
        const targetPage = document.getElementById('page-' + pageId);
        const targetLink = document.querySelector(`[data-page="${pageId}"]`);
        
        if (targetPage) targetPage.classList.add('active');
        if (targetLink) {
            targetLink.classList.add('active');
            if(pageTitle) pageTitle.textContent = targetLink.innerText.trim();
        }
        if (window.innerWidth < 992) {
            document.body.classList.remove('sidebar-toggled');
        }
    }

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const pageId = link.getAttribute('data-page');
            activateTab(pageId);
            const url = new URL(window.location);
            url.searchParams.set('page', pageId);
            window.history.pushState({}, '', url);
        });
    });

    const urlParams = new URLSearchParams(window.location.search);
    const activePage = urlParams.get('page') || 'dashboard';
    activateTab(activePage);


    // 3. MODAL HANDLERS (UI HELPER - COPY PASTE TEKS SAJA)

    // A. Modal Assign Driver (Jemput & Antar) - REVISI DINAMIS
    const assignDriverModalEl = document.getElementById('assignDriverModal');
    if (assignDriverModalEl) {
        assignDriverModalEl.addEventListener('show.bs.modal', function(event) {
            // 1. Tangkap tombol yang diklik (bisa tombol 'Jemput' atau 'Antar')
            const button = event.relatedTarget;
            
            // 2. Ambil data dari atribut data-* di tombol tersebut
            const orderId = button.getAttribute('data-order-id');
            const statusTarget = button.getAttribute('data-status-target'); // 'DRIVER_OTW' atau 'DIKIRIM'
            const modalTitle = button.getAttribute('data-modal-title');
            const modalInfo = button.getAttribute('data-modal-info');
            
            // 3. Isi nilai ke dalam Input Form di Modal
            document.getElementById('assign_order_id').value = orderId;
            document.getElementById('assign_order_id_view').value = "#" + orderId;
            
            // PENTING: Mengisi hidden input agar Controller tahu ini Jemput atau Antar
            const targetStatusInput = document.getElementById('assign_target_status');
            if (targetStatusInput) {
                targetStatusInput.value = statusTarget;
            }
            
            // 4. Update Tampilan Teks Modal (Judul & Info Alert)
            const titleEl = document.getElementById('assign_modal_title');
            const infoEl = document.getElementById('assign_modal_info');
            
            // Gunakan innerHTML karena kita mengirim tag <b> dari JSP
            if (titleEl) titleEl.innerHTML = '<i class="bi bi-truck"></i> ' + (modalTitle || "Tugaskan Driver");
            if (infoEl) infoEl.innerHTML = (modalInfo || "Status otomatis berubah.");
        });
    }

    const driverModalEl = document.getElementById('driverModal');
    if (driverModalEl) {
        driverModalEl.addEventListener('show.bs.modal', function(e) {
            const button = e.relatedTarget;
            const action = button.getAttribute('data-action');
            const form = document.getElementById('formDriver');
            form.reset();

            if (action === 'update') {
                document.getElementById('driverModalTitle').textContent = "Edit Driver";
                document.getElementById('driver_id_input').value = button.getAttribute('data-driver-id');
                document.getElementById('driverName').value = button.getAttribute('data-name');
                document.getElementById('driverPhone').value = button.getAttribute('data-phone');
                const isAvail = button.getAttribute('data-available') === 'true';
                document.getElementById('driverAvailable').checked = isAvail;
            } else {
                document.getElementById('driverModalTitle').textContent = "Tambah Driver Baru";
                document.getElementById('driver_id_input').value = "";
                document.getElementById('driverAvailable').checked = true;
            }
        });
    }
    
    // 4. CHART JS (Visualisasi REAL TIME - DUAL LOCATION)
    const chartLabelsEl = document.getElementById('chartDataLabels');
    const chartOrderEl = document.getElementById('chartDataOrder');
    const chartRevenueEl = document.getElementById('chartDataRevenue');

    if (chartLabelsEl && chartOrderEl && chartRevenueEl) {
        
        // 1. Ambil Data Sekali Saja
        const labels = JSON.parse(chartLabelsEl.value);
        const orderData = JSON.parse(chartOrderEl.value);
        const revenueData = JSON.parse(chartRevenueEl.value);

        // 2. Fungsi Pembantu: Render Grafik Tren Order
        function renderTrenChart(canvasId) {
            const ctx = document.getElementById(canvasId);
            if (ctx) {
                new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Total Order',
                            data: orderData,
                            borderColor: '#0d6efd',
                            backgroundColor: 'rgba(13, 110, 253, 0.1)',
                            fill: true, tension: 0.4
                        }]
                    },
                    options: { 
                        responsive: true, maintainAspectRatio: false,
                        scales: { y: { beginAtZero: true, suggestedMax: 5, ticks: { stepSize: 1, precision: 0 } } }
                    }
                });
            }
        }

        // 3. Fungsi Pembantu: Render Grafik Pendapatan
        function renderRevenueChart(canvasId) {
            const ctx = document.getElementById(canvasId);
            if (ctx) {
                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Pendapatan (Rupiah)',
                            data: revenueData,
                            backgroundColor: '#198754',
                            borderRadius: 5
                        }]
                    },
                    options: { 
                        responsive: true, maintainAspectRatio: false,
                        scales: { y: { ticks: { callback: function(val) { return 'Rp ' + (val/1000) + 'k'; } } } }
                    }
                });
            }
        }

        // 4. EKSEKUSI: Gambar di Dashboard
        renderTrenChart('dash_trenChart');
        renderRevenueChart('dash_revChart');

        // 5. EKSEKUSI: Gambar di Laporan
        renderTrenChart('lap_trenChart');
        renderRevenueChart('lap_revChart');
    }
    
    

    const pendCtx = document.getElementById('pendapatanChart');
    if (pendCtx) {
        new Chart(pendCtx, {
            type: 'bar',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'Mei', 'Jun'],
                datasets: [{
                    label: 'Pendapatan (Juta)',
                    data: [1.5, 2.0, 1.2, 1.8, 2.5, 3.0],
                    backgroundColor: '#198754',
                    borderRadius: 5
                }]
            },
            options: { responsive: true, maintainAspectRatio: false }
        });
    }
});