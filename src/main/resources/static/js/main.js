$(document).ready(function () {

    console.log("Hello main.js");

    // Stripes. card verification; Creating and sending card token to server
    function sendToken(token) {
        $.ajax('/api/user/buy-gold-status', {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            data: {stripeToken: token.id},
            success: function (data) {
                $('#myModalPay').modal('show');
            },
            error: function (jqXHR) {
                alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
            },
        });
    }

    var handler = StripeCheckout.configure({
        key: 'pk_test_Kp54gxVu3wLXwB24pYX6Iyf2',
        image: 'https://stripe.com/img/documentation/checkout/marketplace.png',
        locale: 'auto',
        token: function (token) {
            sendToken(token);
        }
    });

    document.getElementById('customButton').addEventListener('click', function (e) {
        // Open Checkout with further options:
        handler.open({
            name: "Anian",
            email: sessionStorage.getItem("email"),
            description: 'Gold status',
            amount: +sessionStorage.getItem("price"),
        });
        e.preventDefault();
    });

    // Close Checkout on page navigation:
    window.addEventListener('popstate', function () {
        handler.close();
    });
});