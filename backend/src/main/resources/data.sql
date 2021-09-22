INSERT INTO Address(street_number, street_name, city, region, country, postcode)
VALUES (1, 'Chocolate Drive', 'Chocolate', 'Tasty', 'New Zealand', 9762);

INSERT INTO User(first_name, last_name, middle_name, nick_name, bio, email, date_of_birth, created, phone_number,
                 home_address, password, role)
VALUES ('Willy', 'Wonka', 'Chocolate', 'Chocolate Man', 'The Lord of Chocolate', 'willy@wonka.com', '1990-07-15',
        '2019-12-03', '(946) 570-2232', 1, '$2a$10$sE1aW/VTng8e71r.oaTzw.3hZtOpVymFcbqsnxmb22BlOsbHfbTjC', 0);

INSERT INTO Business(business_type, created, description, name, address, primary_administrator)
VALUES (2, '2015-03-22', 'We make the best chocolate in the world', 'Wonka Chocolate', 1, 1);

INSERT INTO Business_administrators(administrators_id, business_id)
VALUES (1, 1);

INSERT INTO User_businessesPrimarilyAdministered(User_id, businessesPrimarilyAdministered_id)
VALUES (1, 1);

INSERT INTO Product(business_id, created, description, manufacturer, name, recommended_retail_price, code)
VALUES (1, '2017-08-08', 'A tasty dark chocolate bar', 'The Chocolate Factory', 'Dark Chocolate Bar', 2.5,
        '1-WONKA-DARK');
INSERT INTO Product(business_id, created, description, manufacturer, name, recommended_retail_price, code)
VALUES (1, '2017-08-08', 'A tasty milk chocolate bar', 'The Chocolate Factory', 'Milk Chocolate Bar', 2.5,
        '1-WONKA-MILK');
INSERT INTO Product(business_id, created, description, manufacturer, name, recommended_retail_price, code)
VALUES (1, '2017-08-08', 'A tasty white chocolate bar', 'The Chocolate Factory', 'White Chocolate Bar', 2.5,
        '1-WONKA-WHITE');
INSERT INTO Product(business_id, created, description, manufacturer, name, recommended_retail_price, code)
VALUES (1, '2017-08-08', 'An exploding candy', 'The Chocolate Factory', 'Exploding Candy', 3.5, '1-WONKA-EXPLODE');
INSERT INTO Product(business_id, created, description, manufacturer, name, recommended_retail_price, code)
VALUES (1, '2017-08-08', 'A treat that never ends', 'The Chocolate Factory', 'Ever-Lasting Gobstopper', 8.99,
        '1-WONKA-ELG');
INSERT INTO Product(business_id, created, description, manufacturer, name, recommended_retail_price, code)
VALUES (1, '2017-08-08', 'NO SUGAR?!?!?!', 'The Chocolate Factory', 'Black Water No Sugar', 8.99,
        '1-WONKA-BLACK-WATER');
INSERT INTO Product(business_id, created, description, manufacturer, name, recommended_retail_price, code)
VALUES (1, '2017-08-08', 'Easter Egg', 'The Chocolate Factory', 'Easter Egg', 8.99,
        '1-WONKA-EGG');

INSERT INTO Inventory(best_before, businessid, expires, manufactured, price_per_item, quantity, sell_by, total_price,
                      product_id, quantity_unlisted)
VALUES ('2021-09-27', 1, '2022-05-27', '2021-03-01', 2.5, 5, '2021-09-26', 12.5, 1, 3);
INSERT INTO Inventory(best_before, businessid, expires, manufactured, price_per_item, quantity, sell_by, total_price,
                      product_id, quantity_unlisted)
VALUES ('2021-09-27', 1, '2022-05-27', '2021-03-01', 2.5, 2, '2021-09-26', 5, 2, 0);
INSERT INTO Inventory(best_before, businessid, expires, manufactured, price_per_item, quantity, sell_by, total_price,
                      product_id, quantity_unlisted)
VALUES ('2021-09-27', 1, '2022-05-27', '2021-03-01', 2.5, 10, '2021-09-26', 20, 3, 2);
INSERT INTO Inventory(best_before, businessid, expires, manufactured, price_per_item, quantity, sell_by, total_price,
                      product_id, quantity_unlisted)
VALUES ('2021-09-27', 1, '2022-05-27', '2021-03-01', 3.5, 10, '2021-09-26', 35, 4, 10);
INSERT INTO Inventory(best_before, businessid, expires, manufactured, price_per_item, quantity, sell_by, total_price,
                      product_id, quantity_unlisted)
VALUES ('2021-09-27', 1, '2022-05-27', '2021-03-01', 9.5, 3, '2021-09-26', 28.5, 5, 1);

INSERT INTO Listing(business_id, closes, created, moreInfo, price, quantity, inventory_id)
VALUES (1, '2022-03-27', '2021-03-20', 'Willing to see offers.', 2.5, 2, 1);
INSERT INTO Listing(business_id, closes, created, moreInfo, price, quantity, inventory_id)
VALUES (1, '2021-12-15', '2021-03-20', 'Limited edition flavour!', 2.5, 2, 2);
INSERT INTO Listing(business_id, closes, created, moreInfo, price, quantity, inventory_id)
VALUES (1, '2022-02-27', '2021-03-20', 'Get now while stocks last!', 2.5, 6, 3);
INSERT INTO Listing(business_id, closes, created, moreInfo, price, quantity, inventory_id)
VALUES (1, '2021-11-30', '2021-03-20', 'No DISCOUNTS!', 9.5, 2, 5);
INSERT INTO Listing(business_id, closes, created, moreInfo, price, quantity, inventory_id)
VALUES (1, '2022-02-27', '2021-03-20', '10% discount!', 2.25, 2, 3);