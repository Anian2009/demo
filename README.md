#**APIdoc for Oligarch**
***
AdminController
---------------------------
***
- ###The whole list of factories
GET `api/admin/factory-list`

    headers: {token : "admin-token"}

Response 200

    {
      "fabrics":[
        {
          "id":1,
          "price":1.0,
          "fabricName":"firstFabric",
          "upgrade":3.0,
          "miningPerSecond":1.0E-5,
          "img":"image-1",
        },
        {
          "id":2,
          "price":5.0,
          "fabricName":"secondFabric",
          "upgrade":15.0,
          "miningPerSecond":6.0E-5,
          "img":"image-2"
        },
        {
          "id":3,
          "price":10.0,
          "fabricName":"threadFabric",
          "upgrade":30.0,
          "miningPerSecond":1.5E-4,
          "img":"image-3",
        },
        {
          "id":4,
          "price":50.0,
          "fabricName":"forthFabric",
          "upgrade":150.0,
          "miningPerSecond":8.0E-4,
          "img":"image-4"
        }
      ]
    }

- ### Create a new factory
POST `api/admin/add-factory`

    headers: {token : "admin-token"}
    data: JSON.stringify({
                    newPrice: "10",
                    newName: "Microsoft corp",
                    newUpgrade: "30",
                    newMining: "0.005",
                    image: "../image/image-8",
                }),
                
Response 200

    {
      "message": {
        "id": 5,
        "price": 10.0,
        "fabricName": "Microsoft corp",
        "upgrade": 30.0,
        "miningPerSecond": 0.005,
        "img": "../image/image-8"
      }
    }
                
Response 400 - When enough data is not transmitted to create a new object.

    {
      "timestamp": "2019-02-05T16:23:21.586+0000",
      "status": 400,
      "error": "Bad Request",
      "message": "Lack of transmitted data to create an object.",
      "path": "/api/admin/add-factory"
    }
***
DashboardController
---------------------------
***
- ###Get information for the main page: `"user"` - to output your own data; `"users"` - to form a board of leaders, `"fabrics"` - for the withdrawal of their own factories.
GET `api/user/dashboard`

    headers: {token : "user-token"}

Response 200

    {
          "fabrics": [
            {
              "id": 1,
              "master": {
                "id": null,
                "name": "SomeUser",
                "silverBalance": 0.0,
                "goldBalance": 0.0,
                "increase": 1.0E-5
              },
              "fabric": {
                "id": 2,
                "price": 1.0,
                "fabricName": "firstFabric",
                "upgrade": 3.0,
                "miningPerSecond": 1.0E-5,
                "img": "image-1"
              },
              "fabricLevel": 1,
              "miningPerSecond": 1.0E-5
            },
            {
              "id": 1,
              "master": {
                "id": 2,
                "name": "SomeUser",
                "silverBalance": 0.0,
                "goldBalance": 0.0,
                "increase": 1.0E-5
              },
              "fabric": {
                "id": 2,
                "price": 5.0,
                "fabricName": "secondFabric",
                "upgrade": 15.0,
                "miningPerSecond": 6.0E-5,
                "img": "image-2"
              },
              "fabricLevel": 2,
              "miningPerSecond": 1.2E-4
            }
          ],
          "user": {
            "id": 1,
            "name": "SomeUser",
            "silverBalance": 0.0,
            "goldBalance": 0.0,
            "increase": 1.0E-5
          },
          "users": [
            {
              "id": 1,
              "name": "firstUser",
              "silverBalance": 0.0,
              "goldBalance": 0.0,
              "increase": 1.0E-5
            },
            {
              "id": 2,
              "name": "secondUser",
              "silverBalance": 0.0,
              "goldBalance": 0.0,
              "increase": 1.0E-5
            },
            {
              "id": 3,
              "name": "threadUser",
              "silverBalance": 0.0,
              "goldBalance": 0.0,
              "increase": 1.0E-5
            },
            {
              "id": 4,
              "name": "forthUser",
              "silverBalance": 0.0,
              "goldBalance": 0.0,
              "increase": 1.0E-5
            }
          ]
        }

- ###Get a list of factories available for purchase.
GET `api/user/factory-market`

    headers: {token : "user-token"}

Response 200

    {
      "fabrics": [
        {
          "id":1,
          "price": 1.0,
          "fabricName": "firstFabric",
          "upgrade": 3.0,
          "miningPerSecond": 1.0E-5,
          "img": "image-1"
        },
        {
          "id": 2,
          "price": 5.0,
          "fabricName": "secondFabric",
          "upgrade": 15.0,
          "miningPerSecond": 6.0E-5,
          "img": "image-2"
        },
        {
          "id": 3,
          "price": 10.0,
          "fabricName": "threadFabric",
          "upgrade": 30.0,
          "miningPerSecond": 1.5E-4,
          "img": "image-3"
        },
        {
          "id": 4,
          "price": 50.0,
          "fabricName": "forthFabric",
          "upgrade": 150.0,
          "miningPerSecond": 8.0E-4,
          "img": "image-4"
        }
      ]
    }

- ###Buy a plant.
POST `api/user/buy-factory`

    headers: {token : "user-token"}
    data: JSON.stringify({id: 2})

Response 200

    {
      "userFabric": {
        "id": 1,
        "master": {
          "id": null,
          "name": "SomeUser",
          "silverBalance": 9.0,
          "goldBalance": 0.0,
          "increase": 2.0E-5
        },
        "fabric": {
          "id": 2,
          "price": 1.0,
          "fabricName": "OneOfFabric",
          "upgrade": 3.0,
          "miningPerSecond": 1.0E-5,
          "img": "image-3"
        },
        "fabricLevel": 1,
        "miningPerSecond": 1.0E-5
      },
      "user": {
        "id": 3,
        "name": "SomeUser",
        "silverBalance": 9.0,
        "goldBalance": 0.0,
        "increase": 2.0E-5
      }
    }

Response 400, 404

    {
      "timestamp": "2019-02-05T17:48:18.135+0000",
      "status": 400,
      "error": "Bad Request",
      "message": "The user lacks money.",
      "path": "/api/user/buy-factory"
    }

- ###Upgrade plant. Returns a list of its own factories
PUT `api/user/upgrade-factory/{id}`

    headers: {token : "user-token"}

Response 200

    {
      "fabrics": [
        {
          "id": 1,
          "master": {
            "id": 2,
            "name": "SomeUser",
            "silverBalance": 7.0,
            "goldBalance": 0.0,
            "increase": 2.0E-5
          },
          "fabric": {
            "id": 1,
            "price": 1.0,
            "fabricName": "firstFabric",
            "upgrade": 3.0,
            "miningPerSecond": 1.0E-5,
            "img": "image-1"
          },
          "fabricLevel": 1,
          "miningPerSecond": 1.0E-5
        },
        {
          "id": 2,
          "master": {
            "id": 1,
            "name": "SomeUser",
            "silverBalance": 7.0,
            "goldBalance": 0.0,
            "increase": 2.0E-5
          },
          "fabric": {
            "id": null,
            "price": 5.0,
            "fabricName": "secondFabric",
            "upgrade": 15.0,
            "miningPerSecond": 6.0E-5,
            "img": "image-2"
          },
          "fabricLevel": 2,
          "miningPerSecond": 1.2E-4
        }
      ]
    }

Response 400, 404

    {
      "timestamp": "2019-02-05T18:00:42.185+0000",
      "status": 400,
      "error": "Bad Request",
      "message": "The user does not have enough money to complete the operation.",
      "path": "/api/user/upgrade-factory/2"
    }

- ###Buy gold status
GET `api/user/buy-gold-status`

    headers: {token : "user-token"},
    data: {stripeToken: "tok_1E0YLpAlgkTJhFEJsRGxHh9f"}
    
Response 200

    {
      "message": "Ok"
    }
    
Response 400

        {
          "timestamp": "2019-02-05T18:00:42.185+0000",
          "status": 400,
          "error": "Bad Request",
          "message": "Payment failed.",
          "path": "/api/user/buy-gold-status"
        }

- ###Exchange currency
GET `api/user/exchange`

    headers: {token : "user-token"},
    data: {myGoldCoins: - "-2",
           mySilverCoins: "400",
    
Response 200

    {
      "user": {
        "id": 5,
        "name": "SomeUser",
        "silverBalance": 100.0,
        "goldBalance": 1.0,
        "increase": 1.0E-5
      }
    }
    
Response 400

    {
        "timestamp": "2019-02-05T18:27:50.153+0000",
        "status": 400,
        "error": "Bad Request",
        "message": "The user does not have the amount specified in the account.",
        "path": "/api/user/exchange"
    }

RegistrationControllerTest
---------------------------
***
- ###Log in to game
POST `/api/guest/log-in`

    headers: {"Authorization":"Basic U29tZVVzZXJAc29tZS5uZXQ6c29tZVBhc3N3b3Jk"}

Response 200

      {
        "rateSilver":200,
        "role":"USER",
        "rateGold":100,
        "price":2000,
        "message":"user-token",
        "email":"SomeUser@some.net"
      }

Response 401

    {
      "timestamp": "2019-02-06T09:16:57.432+0000",
      "status": 401,
      "error": "Not Found",
      "message": "Empty username or password",
      "path": "/api/guest/log-in"
    }

- ###Registration
POST `/api/guest/registration`

    data: JSON.stringify({
                    name: "UserName",
                    email: "SomeUser@some.net",
                    password: "SomePassword"
                })

Response 200

      {"user":UserName}
      
Response 400

    {
        "timestamp": "2019-02-05T18:27:50.153+0000",
        "status": 400,
        "error": "Bad Request",
        "message": "SomeUser@some.net - Account was suspended due to inactivity",
        "path": "/api/guest/registration"
    }
    
- ###Activation code
PUT `activation-code/{code}`

Response 200

      {
        "message": {
          "id": 3,
          "name": "SomeUser",
          "silverBalance": 0.0,
          "goldBalance": 0.0,
          "increase": 1.0E-5
        }
      }
      
Response 400

    {
        "timestamp": "2019-02-05T18:27:50.153+0000",
        "status": 400,
        "error": "Bad Request",
        "message": "A user with such an activation key was not found in the database.",
        "path": "activation-code/code"
    }