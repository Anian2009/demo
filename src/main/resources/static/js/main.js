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
                alert("Congratulations! \n " +
                    "From now all your mining will come in gold coins. " +
                    "At any time, you can exchange gold coins for silver at an advantageous rate.");
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

    $('#goldSellGold').change(function () {
        var goldCoins = Math.trunc($('#goldBal').text());
        if ($('#goldSellGold').val() > goldCoins) {
            alert("You're not as rich as you want.");
            $('#goldSellGold').val(goldCoins);
        }
        $('#silverSellGold').val($('#goldSellGold').val() * sessionStorage.getItem("rateGold"));
    });

    $('#silverBuyGold').change(function () {

        var silverCoins = Math.trunc($('#silverBal').text());
        if ($('#silverBuyGold').val() > silverCoins) {
            alert("You're not as rich as you want.");
            $('#silverBuyGold').val(silverCoins);
        }
        $('#goldBuyGold').val($('#silverBuyGold').val() / sessionStorage.getItem("rateSilver"));
    });

    $('#exchangeSellGold').click(function () {
        $.ajax('/api/user/exchange', {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            data: {
                myGoldCoins: - $('#goldSellGold').val(),
                mySilverCoins: $('#silverSellGold').val()},
            success: function (data) {
            },
            error: function (jqXHR) {
                alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
            },
        });
        $('#goldSellGold').val(0);
        $('#silverSellGold').val(0);
    });

    $('#exchangeBuyGold').click(function () {
        $.ajax('/api/user/exchange', {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            data: {
                mySilverCoins: - $('#silverBuyGold').val(),
                myGoldCoins: $('#goldBuyGold').val()},
            success: function (data) {
            },
            error: function (jqXHR) {
                alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
            },
        });
        $('#goldBuyGold').val(0);
        $('#silverBuyGold').val(0);
    });
});