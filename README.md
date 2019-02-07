#APIdoc for Oligarch
***
For admin account
---------------------------
***
- ### Get a full list of existing factories

GET `api/admin/factory-list`

    HEADER: token:"admin-token"

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
        }
      ]
    }

- ### Create a new factory  

POST `api/admin/add-factory`

    HEADER: token:"admin-token"
             
    DATA:   {
                "newPrice"   : 10,
                "newName"    : "Microsoft corp",
                "newUpgrade" : 30,
                "newMining"  : 0.005,
                "image"      : "../image/image-8"
             }
                
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

  For user account
  ---------------------------
  
***

- ### Get information for the main page: `"user"` - to output your own data; `"users"` - to form a board of leaders.

GET `api/user/dashboard`

    HEADER: token:"user-token"

Response 200

    {
      "user": {
        "id": 3,
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
          "name": "SomeUser",
          "silverBalance": 0.0,
          "goldBalance": 0.0,
          "increase": 1.0E-5
        }
      ]
    }

- ### Get a list of own factories.

GET `api/user/myFabric`

    HEADER: token:"user-token"

Response 200

    {
      "fabrics": [
        {
          "id": 1,
          "master": 4,
          "fabric": 1,
          "fabricLevel": 1,
          "miningPerSecond": 1.0E-5,
          "name": "First",
          "img": "img-1",
          "upgrade": 3.0
        },
        {
          "id": 2,
          "master": 4,
          "fabric": 2,
          "fabricLevel": 1,
          "miningPerSecond": 6.0E-5,
          "name": "Second",
          "img": "img-2",
          "upgrade": 15.0
        }
      ]
    }



- ### Get a list of factories available for purchase.

GET `api/user/factory-market`

    HEADER: token:"user-token"

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
        }
      ]
    }

- ### Buy a plant.

POST `api/user/buy-factory`

    HEADER: token:"user-token"
    
    DATA: {id: 1}

Response 200

       {"message": "Congratulations! You have become the owner of a new plant. Information about your factories is on the main page."}

Response 400, 404

    {
      "timestamp": "2019-02-05T17:48:18.135+0000",
      "status": 400,
      "error": "Bad Request",
      "message": "The user lacks money.",
      "path": "/api/user/buy-factory"
    }

- ### Upgrade plant. Returns a list of its own factories

PUT `api/user/upgrade-factory/{id}`

    HEADER: token:"user-token"

Response 200

    {
      "fabrics": [
        {
          "id": 1,
          "master": 5,
          "fabric": 1,
          "fabricLevel": 2,
          "miningPerSecond": 2.0E-5,
          "name": "First",
          "img": "img-1",
          "upgrade": 3.0
        },
        {
          "id": 2,
          "master": 5,
          "fabric": 2,
          "fabricLevel": 1,
          "miningPerSecond": 6.0E-5,
          "name": "Second",
          "img": "img-2",
          "upgrade": 15.0
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

- ### Buy gold status

GET `api/user/buy-gold-status?stripeToken=tok_1E0YLpAlgkTJhFEJsRGxHh9f`

    HEADER: token:"user-token"
    
    
Response 200

    {
      "message": "succeeded"
    }
    
Response 400

        {
          "timestamp": "2019-02-05T18:00:42.185+0000",
          "status": 400,
          "error": "Bad Request",
          "message": "Payment failed.",
          "path": "/api/user/buy-gold-status"
        }

- ### Exchange currency

GET `api/user/exchange?myGoldCoins=-2&mySilverCoins=400`

    HEADER: token:"user-token"
    
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

For the guest
---------------------------

***

- ### Log in to game

POST `/api/guest/log-in`

    HEADER: {"Authorization":"Basic U29tZVVzZXJAc29tZS5uZXQ6c29tZVBhc3N3b3Jk"}

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

- ### Registration

POST `/api/guest/registration`

    DATA: {
            name: "UserName",
            email: "SomeUser@some.net",
            password: "SomePassword"
          }

Response 200

      {
        "message":"A message was sent to your email with further instructions"
      }
      
Response 400

    {
        "timestamp": "2019-02-05T18:27:50.153+0000",
        "status": 400,
        "error": "Bad Request",
        "message": "SomeUser@some.net - Account was suspended due to inactivity",
        "path": "/api/guest/registration"
    }
    
- ### Activation code

PUT `activation-code/{code}`

Response 200

      {
        "message":"Activation completed successfully. You can enter the game using your email address and password"
      }
      
Response 400

    {
        "timestamp": "2019-02-05T18:27:50.153+0000",
        "status": 400,
        "error": "Bad Request",
        "message": "A user with such an activation key was not found in the database.",
        "path": "activation-code/code"
    }