$(document).ready(function () {

    console.log("Hello addFabric.js");

    $.ajax('/api/admin/factory-list', {
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        headers: {token: sessionStorage.getItem("token")},
        success: function (data) {
            model.fabrics = data.fabrics;
            toDoImageTable(data.imageC);
            initialize();
        },
        error: function () {
            alert("No server connection");
        }
    });

    var Vm = function (model) {
        this.model = model;
        var imgEdit = $('#newImage');
        var self = this;

        this.fabricName = ko.observable('');
        this.price = ko.observable('');
        this.miningPerSecond = ko.observable('');
        this.upgrade = ko.observable('');
        this.fabrics = ko.observableArray();

        this.addFabric = function () {
            this.model.addFabric(this.fabricName(), this.price(), this.miningPerSecond(), this.upgrade(), imgEdit.val());
            this.fabricName('');
            this.upgrade('');
            this.price('');
            this.miningPerSecond('');
            $('#new_im').html(null);
            imgEdit.val(null);
            this.fabrics(this.model.fabrics);
        };

        initialize = function () {
            self.fabrics(self.model.fabrics);
        }
    };

    var Model = function () {
        var self = this;
        this.fabrics = [];

        this.addFabric = function (newName, newPrice, newMining, newUpgrade, newImage) {
            validation(newName, newPrice, newMining, newUpgrade, newImage);

            this.fabrics.push({
                price: newPrice,
                fabricName: newName,
                upgrade: newUpgrade,
                miningPerSecond: newMining,
                img: newImage
            });
            $.ajax('/api/admin/add-factory', {
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                headers: {token: sessionStorage.getItem("token")},
                data: JSON.stringify({
                    newPrice: newPrice,
                    newName: newName,
                    newUpgrade: newUpgrade,
                    newMining: newMining,
                    image: newImage,
                }),
                success: function () {
                },
                error: function (jqXHR) {
                    alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
                    self.fabrics.splice(self.fabrics.length-1,1);
                },
            });
        };
    };

    var model = new Model();
    ko.applyBindings(new Vm(model));

    toDoImageTable = function (count) {
        var tbl = document.getElementById("tbl");
        var tr;
        var colCount = 2;
        var col = 0;
        for (var i = 1; i < count+1; i++) {
            var img = new Image();
            img.width = 130;
            img.height = 100;
            img.id = "../immage/fabricsImage/fab_none-"+i+".jpg";
            img.src = "../immage/fabricsImage/fab_none-"+i+".jpg";
            if (col == 0) {
                tr = document.createElement("TR");
                tbl.appendChild(tr);
            }
            var td = document.createElement("TD");
            td.align = "center";
            td.appendChild(img);
            tr.appendChild(td);
            col++;
            if (col >= colCount)
                col = 0;
        }
    };

    validation = function (newName, newPrice, newMining, newUpgrade, newImage){
        if (newPrice === null || newPrice.replace(/\s+/g, '') === "") {
            alert("Any factory is worth something!");
            return false;
        }

        if (newName === null || newName.replace(/\s+/g, '') === "") {
            alert("How do you call the plant - so it will work.");
            return false
        }
        if (newUpgrade === null || newUpgrade.replace(/\s+/g, '') === "") {
            alert("Any equipment worth the money.");
            return false
        }

        if (newMining === null || newMining.replace(/\s+/g, '') === "") {
            alert("Why do we need the plant - which does not make a profit?");
            return false
        }

        if (newImage === null || newImage.replace(/\s+/g, '') === "") {
            alert("Selected a factory photo.");
            return false
        }
    };

    $(document).on('click', 'img[id^="../immage/fabricsImage/fab_none-"]', function (e) {
        e.preventDefault();
        $('#newImage').val(this.id);
        $('#new_im').html(null);
        var img = new Image();
        img.width = 70;
        img.height = 50;
        img.src = this.id;
        $('#new_im').append(img);
    });

    $('#log_out').click(function () {
        sessionStorage.clear();
        window.location = '../index.html';
    });

});