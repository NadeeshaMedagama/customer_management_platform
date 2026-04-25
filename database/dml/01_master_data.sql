USE customer_db;

INSERT INTO countries (code, name)
VALUES
    ('LK', 'Sri Lanka'),
    ('IN', 'India'),
    ('US', 'United States')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO cities (code, name)
VALUES
    ('CMB', 'Colombo'),
    ('KDY', 'Kandy'),
    ('JFN', 'Jaffna'),
    ('BLR', 'Bengaluru')
ON DUPLICATE KEY UPDATE name = VALUES(name);

