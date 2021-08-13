INSERT INTO Address(street_number, street_name, city, region, country, postcode)
VALUES (1, 'Chocolate Drive', 'Chocolate', 'Tasty', 'New Zealand', 9762);

INSERT INTO User(first_name, last_name, middle_name, nick_name, bio, email, date_of_birth, created, phone_number,
                 home_address, password, role, HAS_CardS_DELETED)
VALUES ('Willy', 'Wonka', 'Chocolate', 'Chocolate Man', 'The Lord of Chocolate', 'willy@wonka.com', '1990-07-15',
        '2019-12-03', '(946) 570-2232', 1, '$2a$10$sE1aW/VTng8e71r.oaTzw.3hZtOpVymFcbqsnxmb22BlOsbHfbTjC', 0, 0);

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

INSERT INTO Inventory(best_before, businessid, expires, manufactured, price_per_item, quantity, sell_by, total_price,
                      product_id, quantity_in_listing)
VALUES ('2021-09-27', 1, '2022-05-27', '2021-03-01', 2.5, 5, '2021-09-26', 12.5, 1, 2);
INSERT INTO Inventory(best_before, businessid, expires, manufactured, price_per_item, quantity, sell_by, total_price,
                      product_id, quantity_in_listing)
VALUES ('2021-09-27', 1, '2022-05-27', '2021-03-01', 2.5, 2, '2021-09-26', 5, 2, 2);
INSERT INTO Inventory(best_before, businessid, expires, manufactured, price_per_item, quantity, sell_by, total_price,
                      product_id, quantity_in_listing)
VALUES ('2021-09-27', 1, '2022-05-27', '2021-03-01', 2.5, 8, '2021-09-26', 20, 3, 6);
INSERT INTO Inventory(best_before, businessid, expires, manufactured, price_per_item, quantity, sell_by, total_price,
                      product_id, quantity_in_listing)
VALUES ('2021-09-27', 1, '2022-05-27', '2021-03-01', 3.5, 10, '2021-09-26', 35, 4, 1);
INSERT INTO Inventory(best_before, businessid, expires, manufactured, price_per_item, quantity, sell_by, total_price,
                      product_id, quantity_in_listing)
VALUES ('2021-09-27', 1, '2022-05-27', '2021-03-01', 9.5, 3, '2021-09-26', 28.5, 5, 2);

INSERT INTO Listing(business_id, closes, created, moreInfo, price, quantity, inventory_id)
VALUES (1, '2022-03-27', '2021-09-20', 'Willing to see offers.', 2.5, 2, 1);
INSERT INTO Listing(business_id, closes, created, moreInfo, price, quantity, inventory_id)
VALUES (1, '2021-12-15', '2021-09-20', 'Limited edition flavour!', 2.5, 2, 2);
INSERT INTO Listing(business_id, closes, created, moreInfo, price, quantity, inventory_id)
VALUES (1, '2022-02-27', '2021-09-20', 'Get now while stocks last!', 2.5, 6, 3);
INSERT INTO Listing(business_id, closes, created, moreInfo, price, quantity, inventory_id)
VALUES (1, '2021-11-30', '2021-09-20', 'No DISCOUNTS!', 9.5, 1, 5);
INSERT INTO Listing(business_id, closes, created, moreInfo, price, quantity, inventory_id)
VALUES (1, '2022-02-27', '2021-09-20', '10% discount!', 2.25, 2, 3);

INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-08-02 11:30:53.523531',
        'A nice long description, a nice long description, a nice long description, a nice long description',
        '2021-08-16 11:30:53.523531', 0, 'Sugar Grinder', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-08-02 11:30:53.523585', 'A great quality machine for all your confectinary producing needs',
        '2021-08-16 11:30:53.523585', 0, 'Chocolate Mixer', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-08-01 11:30:53.523595', 'Will trade for one golden ticket', '2021-08-15 11:30:53.523595', 0,
        'Melting Tank', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-08-01 11:30:53.523603', 'Will trade for one golden ticket', '2021-08-15 11:30:53.523603', 0,
        'Candy Drop Machine', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-31 11:30:53.523610', 'Selling as I am shutting down my factory.', '2021-08-14 11:30:53.523610', 0,
        'Bean Roaster', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-31 11:30:53.523616', 'A bargain at this price', '2021-08-14 11:30:53.523616', 0, 'Chocolate Mixer', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-30 11:30:53.523623', 'Selling as I am shutting down my factory.', '2021-08-13 11:30:53.523623', 0,
        'Chocolate Mixer', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-30 11:30:53.523629',
        'A nice long description, a nice long description, a nice long description, a nice long description',
        '2021-08-13 11:30:53.523629', 0, 'Nutcracking Machine', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-29 11:30:53.523636', 'Barely used, near-mint condition. Pick up ony.', '2021-08-12 11:30:53.523636', 0,
        'Chocolate Concher', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-29 11:30:53.523642', 'Selling as I am shutting down my factory.', '2021-08-12 11:30:53.523642', 0,
        'Sugar Grinder', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-28 11:30:53.523648', 'Barely used, near-mint condition. Pick up ony.', '2021-08-11 11:30:53.523648', 0,
        'Melting Tank', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-28 11:30:53.523655', 'Will trade for one golden ticket', '2021-08-11 11:30:53.523655', 0,
        'Melting Tank', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-27 11:30:53.523662', 'A great quality machine for all your confectinary producing needs',
        '2021-08-10 11:30:53.523662', 0, 'Candy Drop Machine', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-27 11:30:53.523670', 'A bargain at this price', '2021-08-10 11:30:53.523670', 0, 'Bean Roaster', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-26 11:30:53.523682', 'A bargain at this price', '2021-08-09 11:30:53.523682', 0, 'Sugar Grinder', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-26 11:30:53.523692', 'A bargain at this price', '2021-08-09 11:30:53.523692', 0,
        'Chocolate Fish Mould', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-25 11:30:53.523702', 'Selling as I am shutting down my factory.', '2021-08-08 11:30:53.523702', 0,
        'Chocolate Fish Mould', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-25 11:30:53.523710', 'Selling as I am shutting down my factory.', '2021-08-08 11:30:53.523710', 0,
        'Nutcracking Machine', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-24 11:30:53.523719', 'Selling as I am shutting down my factory.', '2021-08-07 11:30:53.523719', 0,
        'Nutcracking Machine', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-24 11:30:53.523726', 'Selling as I am shutting down my factory.', '2021-08-07 11:30:53.523726', 0,
        'Sugar Grinder', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-23 11:30:53.523734', 'Selling as I am shutting down my factory.', '2021-08-06 11:30:53.523734', 0,
        'Chocolate Fish Mould', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-23 11:30:53.523740',
        'A nice long description, a nice long description, a nice long description, a nice long description',
        '2021-08-06 11:30:53.523740', 0, 'Chocolate Mixer', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-22 11:30:53.523747', 'Selling as I am shutting down my factory.', '2021-08-05 11:30:53.523747', 0,
        'Melting Tank', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-22 11:30:53.523754',
        'A nice long description, a nice long description, a nice long description, a nice long description',
        '2021-08-05 11:30:53.523754', 0, 'Melting Tank', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-21 11:30:53.523760', 'Barely used, near-mint condition. Pick up ony.', '2021-08-04 11:30:53.523760', 0,
        'Chocolate Mixer', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-21 11:30:53.523767', 'A bargain at this price', '2021-08-04 11:30:53.523767', 0, 'Candy Drop Machine',
        1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-20 11:30:53.523774',
        'A nice long description, a nice long description, a nice long description, a nice long description',
        '2021-08-03 11:30:53.523774', 0, 'Sugar Grinder', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-20 11:30:53.523780',
        'A nice long description, a nice long description, a nice long description, a nice long description',
        '2021-08-03 11:30:53.523780', 0, 'Chocolate Concher', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-19 11:30:53.523786',
        'A nice long description, a nice long description, a nice long description, a nice long description',
        '2021-08-02 11:30:53.523786', 0, 'Chocolate Concher', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-19 11:30:53.523792', 'Barely used, near-mint condition. Pick up ony.', '2021-08-02 11:30:53.523792', 0,
        'Chocolate Fish Mould', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-18 11:30:53.523799', 'A bargain at this price', '2021-08-01 11:30:53.523799', 0,
        'Chocolate Fish Mould', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-18 11:30:53.523805', 'Will trade for one golden ticket', '2021-08-01 11:30:53.523805', 0,
        'Bean Roaster', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-17 11:30:53.523810', 'Barely used, near-mint condition. Pick up ony.', '2021-07-31 11:30:53.523810', 0,
        'Candy Drop Machine', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-17 11:30:53.523816', 'A bargain at this price', '2021-07-31 11:30:53.523816', 0, 'Sugar Grinder', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-16 11:30:53.523821', 'A great quality machine for all your confectinary producing needs',
        '2021-07-30 11:30:53.523821', 0, 'Nutcracking Machine', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-16 11:30:53.523837', 'Will trade for one golden ticket', '2021-07-30 11:30:53.523837', 0,
        'Sugar Grinder', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-15 11:30:53.523844', 'Will trade for one golden ticket', '2021-07-29 11:30:53.523844', 0,
        'Chocolate Mixer', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-15 11:30:53.523850', 'Will trade for one golden ticket', '2021-07-29 11:30:53.523850', 0,
        'Chocolate Mixer', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-14 11:30:53.523855',
        'A nice long description, a nice long description, a nice long description, a nice long description',
        '2021-07-28 11:30:53.523855', 0, 'Chocolate Fish Mould', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-14 11:30:53.523861',
        'A nice long description, a nice long description, a nice long description, a nice long description',
        '2021-07-28 11:30:53.523861', 0, 'Chocolate Mixer', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-13 11:30:53.523866', 'Barely used, near-mint condition. Pick up ony.', '2021-07-27 11:30:53.523866', 0,
        'Chocolate Fish Mould', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-13 11:30:53.523871', 'Will trade for one golden ticket', '2021-07-27 11:30:53.523871', 0,
        'Chocolate Mixer', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-12 11:30:53.523876', 'A great quality machine for all your confectinary producing needs',
        '2021-07-26 11:30:53.523876', 0, 'Bean Roaster', 1);
INSERT INTO Card (created, description, displayperiodend, section, title, creator_id)
values ('2021-07-12 11:30:53.523882', 'Barely used, near-mint condition. Pick up ony.', '2021-07-26 11:30:53.523882', 0,
        'Candy Drop Machine', 1);
