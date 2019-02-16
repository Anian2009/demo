$(document).ready(function () {

    console.log("Hello main-1.js");

    $.ajax('/api/user/myFabric', {
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        headers: {token: sessionStorage.getItem("token")},
        success: function (data) {
            model.myFabrics = data.fabrics;
            initializeMyFabrics();
        },
        error: function (jqXHR) {
            alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
        },
    });

    let getUsersInfo = function () {
        $.ajax('/api/user/dashboard', {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            success: function (data) {
                model.userInfo = data.user;
                model.usersInfo = data.users;
                initialize();
            },
            error: function (jqXHR) {
                alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
            },
        });
    };

    var Vm = function (model) {
        this.model = model;
        var self = this;

        this.userInfo = ko.observableArray();
        this.usersInfo = ko.observableArray();
        this.myFabrics = ko.observableArray();

        this.goldToSilverGoldCoins = ko.observable('0');
        this.goldToSilverSilverCoins = ko.observable('0');
        this.silverToGoldSilverCoins = ko.observable('0');
        this.silverToGoldGoldCoins = ko.observable('0');

        this.goldToSilverGoldCoinsChange = function () {
            self.goldToSilverSilverCoins(sessionStorage.getItem("rateGold")*self.goldToSilverGoldCoins());
        };

        this.silverToGoldSilverCoinsChange = function () {
            self.silverToGoldGoldCoins(self.silverToGoldSilverCoins()/sessionStorage.getItem("rateSilver"));
        };

        this.exchangeBuyGold = function () {
            console.log("Buy Gold");
            self.model.exchangeBuyGold(self.silverToGoldSilverCoins(),self.silverToGoldGoldCoins());
            self.silverToGoldSilverCoins('0');
            self.silverToGoldGoldCoins('0');
        };

        this.exchangeSellGold = function () {
            console.log("Sell Gold");
            self.model.exchangeSellGold(self.goldToSilverGoldCoins(),self.goldToSilverSilverCoins());
            self.goldToSilverGoldCoins('0');
            self.goldToSilverSilverCoins('0');

        };

        this.upgradeThisFabric = function(vieModel){
            self.model.upgradeThisFabric(vieModel);
        };

        this.leftMenuButton = function(){
            $('.sidebar').toggleClass('active-sidebar');
            $('.content').toggleClass('active-content-right');
            $('.icon-menu-right').toggleClass('icon-menu-right-action');
        };

        this.rightMenuButton = function(){
            $('.user-info').toggleClass('user-info-active');
            $('.content').toggleClass('active-content-left');
            $('.icon-menu-left').toggleClass('icon-menu-left-action');
        };

        initialize = function () {
            self.userInfo(self.model.userInfo);
            self.usersInfo(self.model.usersInfo);
        };

        initializeMyFabrics = function () {
            self.myFabrics(self.model.myFabrics);
        };

        this.logOut = function () {
            sessionStorage.clear();
            window.location = '../index.html';
        };
    };

    var Model = function () {
        var self = this;
        this.userInfo = [];
        this.usersInfo = [];
        this.myFabrics = [];

        this.upgradeThisFabric = function (vieModel) {
            $.ajax('/api/user/upgrade-factory/'+vieModel.id, {
                type: 'PUT',
                dataType: 'json',
                contentType: 'application/json',
                headers: {token: sessionStorage.getItem("token")},
                success: function (data) {
                    self.myFabrics = data.fabrics;
                    initializeMyFabrics();
                    alert("Congratulations! You upgraded the plant. Your profit is increasing.");
                },
                error: function (jqXHR) {
                    alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
                },
            })
        };

        this.exchangeBuyGold = function (silver, gold) {
            $.ajax('/api/user/exchange', {
                type: 'GET',
                dataType: 'json',
                contentType: 'application/json',
                headers: {token: sessionStorage.getItem("token")},
                data: {
                    mySilverCoins: - silver,
                    myGoldCoins: gold},
                success: function (data) {
                },
                error: function (jqXHR) {
                    alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
                },
            });
        };

        this.exchangeSellGold = function (gold, silver) {
            $.ajax('/api/user/exchange', {
                type: 'GET',
                dataType: 'json',
                contentType: 'application/json',
                headers: {token: sessionStorage.getItem("token")},
                data: {
                    myGoldCoins: - gold,
                    mySilverCoins: silver},
                success: function (data) {
                },
                error: function (jqXHR) {
                    alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
                },
            });
        };
    };

    var model = new Model();
    ko.applyBindings(new Vm(model));

    setInterval(function () {
        getUsersInfo();
    }, 1000);

    getUsersInfo();
});