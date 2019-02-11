$(document).ready(function () {

    console.log("Hello buyFabric.js");

    $.ajax('/api/user/factory-market', {
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        headers: {token: sessionStorage.getItem("token")},
        success: function (data) {
            model.fabrics = data.fabrics;
            initialize();
        },
        error: function () {
            alert("No server connection");
        }
    });

    var Vm = function (model) {
        this.model = model;
        var self = this;

        this.fabricName = ko.observable('');
        this.price = ko.observable('');
        this.miningPerSecond = ko.observable('');
        this.upgrade = ko.observable('');
        this.fabrics = ko.observableArray();

        this.buyFabric = function(vieModel){
            self.model.buyFabric(vieModel);
        };

        initialize = function () {
            self.fabrics(self.model.fabrics);
        }
    };

    var Model = function () {
        this.fabrics = [];

        this.buyFabric = function (vieModel) {
            $.ajax('/api/user/buy-factory', {
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                headers: {token: sessionStorage.getItem("token")},
                data: JSON.stringify({id: vieModel.id}),
                success: function (data) {
                    alert(data.message);
                },
                error: function (jqXHR) {
                    alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
                },
            })
        }
    };

    var model = new Model();
    ko.applyBindings(new Vm(model));
});