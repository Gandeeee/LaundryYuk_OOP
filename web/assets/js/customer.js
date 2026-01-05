/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


$(document).ready(function() {
    
    // Event Klik Navbar
    $('.nav-link').on('click', function(e) {
        e.preventDefault();
        const targetId = $(this).attr('id');
        
        // Update Active Class
        $('.nav-link').removeClass('active');
        $(this).addClass('active');
        
        // Show/Hide Section
        $('.page-section').hide();
        if(targetId === 'nav-dashboard') $('#dashboard-page').show();
        else if(targetId === 'nav-order') $('#order-page').show();
        else if(targetId === 'nav-history') $('#history-page').show();
    });
});