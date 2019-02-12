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

        this.name = ko.observable('');
        this.silverBalance = ko.observable('');
        this.goldBalance = ko.observable('');
        this.increase = ko.observable('');
        this.miningPerSecond = ko.observable('');
        this.fabricLevel = ko.observable('');
        this.upgrade = ko.observable('');
        this.img = ko.observable('');

        this.userInfo = ko.observableArray();
        this.usersInfo = ko.observableArray();
        this.myFabrics = ko.observableArray();

        this.upgradeThisFabric = function(vieModel){
            self.model.upgradeThisFabric(vieModel);
        };

        initialize = function () {
            self.userInfo(self.model.userInfo);
            self.usersInfo(self.model.usersInfo);
        };

        initializeMyFabrics = function () {
            self.myFabrics(self.model.myFabrics);
        }
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
        }
    };

    var model = new Model();
    ko.applyBindings(new Vm(model));

    $('#logOut').click(function () {
        sessionStorage.clear();
        window.location = '../index.html';
    });

    setInterval(function () {
        getUsersInfo();
    }, 1000);

    getUsersInfo();
});