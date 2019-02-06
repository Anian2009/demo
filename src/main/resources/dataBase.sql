-- Table: fabrics
CREATE TABLE `fabrics` (
   id int(11) NOT NULL AUTO_INCREMENT,
   fabric_name text NOT NULL,
   img text NOT NULL,
   price double NOT NULL,
   mining_per_second double(18,5) NOT NULL,
   upgrade double NOT NULL,
   PRIMARY KEY (id)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

 -- Table: users
  CREATE TABLE `users` (
   id int(11) NOT NULL AUTO_INCREMENT,
   activation_code text NOT NULL,
   email text NOT NULL,
   gold_balance double(18,5) NOT NULL,
   gold_status int(11) NOT NULL,
   increase double(18,5) NOT NULL,
   name text NOT NULL,
   password text NOT NULL,
   silver_balance double(18,5) NOT NULL,
   silver_status int(11) NOT NULL,
   token text NOT NULL,
   total_balance double(18,5) NOT NULL,
   user_role text NOT NULL,
   PRIMARY KEY (id)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

 -- Table: user_fabrics_info
 CREATE TABLE user_fabrics_info (
   id int(11) NOT NULL AUTO_INCREMENT,
   fabric_level int(11) DEFAULT NULL,
   mining_per_second double(18,5) DEFAULT NULL,
   fabric_id int(11) DEFAULT NULL,
   user_id int(11) DEFAULT NULL,
   name text DEFAULT NULL,
   img text DEFAULT NULL,
   upgrade double(18,5) DEFAULT NULL,
   PRIMARY KEY (id)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

 -- Insert some data
  INSERT INTO fabrics (fabric_name, img, price, mining_per_second, upgrade)
  VALUES ('Motorcycle factory','../immage/fab_none-1.jpg',1.0,0.00001,3.0);
  INSERT INTO fabrics(fabric_name, img, price, mining_per_second, upgrade)
  VALUES ('Automobile Building Plant','../immage/fab_none-2.jpg', 5.0,0.00006,15.0);
  INSERT INTO fabrics(fabric_name, img, price, mining_per_second, upgrade)
  VALUES ('Instrument-making plant','../immage/fab_none-3.jpg', 10.0,0.00015,30.0);
  INSERT INTO fabrics(fabric_name, img, price, mining_per_second, upgrade)
  VALUES ('Shipbuilding Plant','../immage/fab_none-4.jpg', 50.0,0.0008,150.0);

  INSERT INTO users (activation_code, email, gold_balance, gold_status, increase, name, password, silver_balance,
                     silver_status, token, total_balance, user_role)
  VALUES ('true','admin@deneg.net', 0.0,0,0.0,'Admin','$2a$04$JY5UWNsOuLI7uCugTcSfV.9NM6wx5IIG7zRwcnpxE.53zGcTRuXDi',
          0.0,0,'9NM6wx5IIG7zRwcnpxE.53zGcTRuXDi',0.0,'ADMIN');

  -- create event ich every second increase gold, silver and total balances
  SET GLOBAL event_scheduler=ON;

  CREATE EVENT myEvent ON SCHEDULE
       EVERY 1 second COMMENT ''
       DO
			UPDATE db.users SET gold_balance = gold_balance+(increase*gold_status),
            silver_balance = silver_balance+(increase*silver_status),
            total_balance = silver_balance+(100*gold_balance)
