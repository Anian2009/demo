$(document).ready(function () {

        console.log("Hello addFabric.js");

        var previousItem;
        var selected = 'img m-1 border border-danger rounded-circle';
        var unselected = 'img m-1 border border-success rounded';

        var VewModel = function () {
            var self = this;

            this.fabrics = ko.observableArray();
            this.image = ko.observableArray();

            $.ajax('/api/admin/factory-list', {
                type: 'GET',
                dataType: 'json',
                contentType: 'application/json',
                headers: {token: sessionStorage.getItem("token")},
                success: function (data) {
                    self.fabrics(data.fabrics);
                    self.image(data.image);

                },
                error: function () {
                    alert("No server connection");
                }
            });

            this.fabricName = ko.observable('');
            this.price = ko.observable('');
            this.upgrade = ko.observable('');
            this.selectBord = ko.observable(unselected);
            this.miningPerSecond = ko.observable('');
            this.img = ko.observable('');

            this.addFabric = function () {
                if (validation(this.fabricName(), this.price(), this.miningPerSecond(), this.upgrade(), this.img())) {
                    this.fabrics.push({
                        price: this.price(),
                        fabricName: this.fabricName(),
                        upgrade: this.upgrade(),
                        miningPerSecond: this.miningPerSecond(),
                        img: this.img()
                    });

                    $('.collapse').collapse('hide');

                    $.ajax('/api/admin/add-factory', {
                        type: 'POST',
                        dataType: 'json',
                        contentType: 'application/json',
                        headers: {token: sessionStorage.getItem("token")},
                        data: JSON.stringify({
                            newPrice: this.price(),
                            newName: this.fabricName(),
                            newUpgrade: this.upgrade(),
                            newMining: this.miningPerSecond(),
                            image: this.img(),
                        }),
                        success: function () {
                            self.price('');
                            self.fabricName('');
                            self.upgrade('');
                            self.miningPerSecond('');
                            self.img('');
                        },
                        error: function (jqXHR) {
                            alert(jqXHR.responseJSON.status + " - " + jqXHR.responseJSON.message);
                            self.fabrics.splice(self.fabrics.length - 1, 1);
                        },
                    });
                }
            }
            ;
            this.imageClick = function (vieModel, some) {
                self.img(vieModel);
                if (previousItem !== undefined)
                    previousItem.setAttribute('class', unselected);
                previousItem = some.target;
                some.target.setAttribute('class', selected);
            }
        };

        validation = function (newName, newPrice, newMining, newUpgrade, newImage) {
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
            return true;
        };

        ko.applyBindings(new VewModel());
    }
);