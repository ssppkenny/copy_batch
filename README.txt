#    /$$$$$$                                      /$$$$$$$$        /$$       /$$          
#   /$$__  $$                                    |__  $$__/       | $$      | $$          
#  | $$  \__/  /$$$$$$   /$$$$$$  /$$   /$$         | $$  /$$$$$$ | $$$$$$$ | $$  /$$$$$$ 
#  | $$       /$$__  $$ /$$__  $$| $$  | $$         | $$ |____  $$| $$__  $$| $$ /$$__  $$
#  | $$      | $$  \ $$| $$  \ $$| $$  | $$         | $$  /$$$$$$$| $$  \ $$| $$| $$$$$$$$
#  | $$    $$| $$  | $$| $$  | $$| $$  | $$         | $$ /$$__  $$| $$  | $$| $$| $$_____/
#  |  $$$$$$/|  $$$$$$/| $$$$$$$/|  $$$$$$$         | $$|  $$$$$$$| $$$$$$$/| $$|  $$$$$$$
#   \______/  \______/ | $$____/  \____  $$         |__/ \_______/|_______/ |__/ \_______/
#                      | $$       /$$  | $$                                               
#                      | $$      |  $$$$$$/                                               
#                      |__/       \______/                                                

------------------------------
How to run the program
------------------------------

One has to define at least 2 configuration files to be able to run the program.
1. application-profile-name.properties
2. copy-tables-profile-name.sql

There are 2 optional files preprocess-profile-name.sql and postprocess-profile-name.sql.

Example: mvn spring-boot:run -Dspring-boot.run.profiles=profile-name

-Dspring-boot.run.profiles=profile-name

------------------------------
Profile property files
------------------------------
1. application-profile-name.properties
2. copy-tables-profile-name.sql
3. preprocess-profile-name.sql
4. postprocess-profile-name.sql

-------------------------------
application-profile-name.properties
-------------------------------

In the application-profile-name.properties file one has to define
four data sources:
1. Source data source
2. Destination data source
3. Preprocess data source
4. Postprocess data source

-------------------------------
copy-tables-profile-name.sql
-------------------------------

This file contains select and insert statements separated by : (colon).
Example:
SELECT pk, name FROM T1:INSERT INTO T2 VALUES (?, ?)



