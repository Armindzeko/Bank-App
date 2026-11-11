CREATE SCHEMA Banking;
SET search_path TO Banking;

--CREATE TABLES--
CREATE TABLE Branches (
branchID INT PRIMARY KEY,
branchName TEXT,
location TEXT,
isActive BOOLEAN
);

CREATE TABLE Accounts(
accountID INT PRIMARY KEY,
accountType TEXT,
branchID INT,
FOREIGN KEY (branchID) REFERENCES Branches(branchID),
balance NUMERIC(12,2),
interestRate NUMERIC(5,2),
accountDescription TEXT,
openDate DATE,
minimumBalance NUMERIC(12,2),
isActive BOOLEAN
);

CREATE TABLE Customers(
customerID INT PRIMARY KEY,
firstName TEXT,
lastName TEXT,
email VARCHAR(150) UNIQUE,
phone VARCHAR(20),
dateOfBirth DATE,
registrationDate DATE,
isActive BOOLEAN
);
ALTER TABLE Customers ADD COLUMN password VARCHAR(255) DEFAULT 'unf123';

CREATE TABLE Transactions(
transactionID INT PRIMARY KEY,
accountID INT,
FOREIGN KEY (accountID) REFERENCES Accounts(accountID),
transactionDate DATE,
amount NUMERIC(12,2),
transactionType VARCHAR(100),
recipient VARCHAR(100),
paymentMethod VARCHAR(100)
);

CREATE TABLE AccountHolders(
accountID INT NOT NULL,
customerID INT NOT NULL,
FOREIGN KEY (accountID) REFERENCES Accounts(accountID),
FOREIGN KEY (customerID) REFERENCES Customers(customerID),
accountRole VARCHAR(100),
joinDate DATE,
depositAmount NUMERIC(12,2),
PRIMARY KEY(accountID, customerID)
);

CREATE TABLE AccountReviews(
reviewID INT PRIMARY KEY,
accountID INT,
customerID INT,
FOREIGN KEY (accountID) REFERENCES Accounts(accountID),
FOREIGN KEY (customerID) REFERENCES Customers(customerID),
rating INT CHECK (rating >= 1 AND rating <= 5),
reviewText TEXT,
reviewDate DATE,
isVerified BOOLEAN
);

CREATE TABLE admins (
adminID INT PRIMARY KEY,
username varchar(50) UNIQUE,
password varchar(250)
);

INSERT INTO Admins (adminID, username, password) VALUES
(1, 'admin1', 'admin123');
SELECT * FROM ADMINS;

--INSERT/POPULATE TABLES--
-- Insert data into Branches table
INSERT INTO Branches (branchID, branchName, location, isActive) VALUES
(1, 'Downtown Branch', '123 Main Street, Jacksonville, FL 32202', TRUE),
(2, 'Northside Branch', '456 North Avenue, Jacksonville, FL 32206', TRUE),
(3, 'Beachside Branch', '789 Beach Boulevard, Jacksonville Beach, FL 32250',TRUE),
(4, 'Westside Branch', '321 West Road, Jacksonville, FL 32210', TRUE),
(5, 'Southside Branch', '654 South Street, Jacksonville, FL 32216', FALSE);

-- Insert data into Accounts table
INSERT INTO Accounts (accountID, accountType, branchID, balance, interestRate,
accountDescription, openDate, minimumBalance, isActive) VALUES
(1001, 'Checking', 1, 2500.00, 0.10, 'Standard checking account with no monthly
fees', '2022-01-15', 100.00, TRUE),
(1002, 'Savings', 1, 15000.00, 2.50, 'High-yield savings account', '2022-02-20',
500.00, TRUE),
(1003, 'Business', 2, 50000.00, 1.75, 'Business checking account with transaction
limits', '2021-11-10', 1000.00, TRUE),
(1004, 'Checking', 3, 3200.00, 0.10, 'Standard checking account', '2023-03-05',
100.00, TRUE),
(1005, 'Savings', 2, 8500.00, 2.50, 'Regular savings account', '2023-01-12', 500.00,
TRUE),
(1006, 'Checking', 1, 1200.00, 0.10, 'Student checking account', '2023-06-18', 50.00,
TRUE),
(1007, 'Savings', 3, 25000.00, 3.00, 'Premium savings account', '2022-05-22',
1000.00, TRUE),
(1008, 'Business', 4, 75000.00, 1.50, 'Corporate business account', '2021-08-30',
2000.00, TRUE),
(1009, 'Checking', 2, 450.00, 0.10, 'Basic checking account', '2023-09-14', 100.00,
TRUE),
(1010, 'Savings', 4, 12000.00, 2.25, 'Standard savings account', '2022-12-01', 500.00,
TRUE),
(1011, 'Checking', 3, 5600.00, 0.10, 'Premium checking account', '2023-02-28',
500.00, TRUE),
(1012, 'Business', 1, 32000.00, 1.75, 'Small business account', '2022-07-19', 1000.00,
TRUE),
(1013, 'Savings', 2, 6800.00, 2.50, 'Family savings account', '2023-04-10', 500.00,
TRUE),
(1014, 'Checking', 4, 2100.00, 0.10, 'Standard checking account', '2023-08-25',
100.00, TRUE),
(1015, 'Savings', 1, 18500.00, 2.75, 'High-interest savings account', '2022-10-05',
1000.00, TRUE);

SELECT * FROM Accounts ORDER BY accountID ASC;

-- Insert data into Customers table
INSERT INTO Customers (customerID, firstName, lastName, email, phone,
dateOfBirth, registrationDate, isActive) VALUES
(5001, 'John', 'Smith', 'john.smith@email.com', '904-555-0101', '1985-03-15', '2022-01-10', TRUE),
(5002, 'Emily', 'Johnson', 'emily.johnson@email.com', '904-555-0102', '1990-07-22',
'2022-02-15', TRUE),
(5003, 'Michael', 'Williams', 'michael.williams@email.com', '904-555-0103', '1978-11-30', '2021-11-05', TRUE),
(5004, 'Sarah', 'Brown', 'sarah.brown@email.com', '904-555-0104', '1992-05-18',
'2023-03-01', TRUE),
(5005, 'David', 'Jones', 'david.jones@email.com', '904-555-0105', '1988-09-12',
'2023-01-08', TRUE),
(5006, 'Jessica', 'Garcia', 'jessica.garcia@email.com', '904-555-0106', '1995-12-25',
'2023-06-15', TRUE),
(5007, 'Robert', 'Martinez', 'robert.martinez@email.com', '904-555-0107', '1982-04-08', '2022-05-20', TRUE),
(5008, 'Lisa', 'Anderson', 'lisa.anderson@email.com', '904-555-0108', '1975-08-14',
'2021-08-25', TRUE),
(5009, 'James', 'Taylor', 'james.taylor@email.com', '904-555-0109', '1998-01-30',
'2023-09-10', TRUE),
(5010, 'Maria', 'Thomas', 'maria.thomas@email.com', '904-555-0110', '1987-06-20',
'2022-11-28', TRUE),
(5011, 'William', 'Moore', 'william.moore@email.com', '904-555-0111', '1991-10-05', '2023-02-22', TRUE),
(5012, 'Jennifer', 'Jackson', 'jennifer.jackson@email.com', '904-555-0112', '1984-03-17', '2022-07-15', TRUE),
(5013, 'Christopher', 'White', 'christopher.white@email.com', '904-555-0113', '1993-11-28', '2023-04-05', TRUE),
(5014, 'Amanda', 'Harris', 'amanda.harris@email.com', '904-555-0114', '1989-02-14', '2023-08-20', TRUE),
(5015, 'Daniel', 'Martin', 'daniel.martin@email.com', '904-555-0115', '1980-07-09',
'2022-10-01', TRUE);

SELECT * FROM CUSTOMERS;

-- Insert data into Transactions table
INSERT INTO Transactions (transactionID, accountID, transactionDate, amount,
transactionType, recipient, paymentMethod) VALUES
(9001, 1001, '2024-01-05', 1500.00, 'Deposit', 'Self', 'Branch'),
(9002, 1001, '2024-01-10', -250.00, 'Withdrawal', 'Self', 'ATM'),
(9003, 1002, '2024-01-15', 5000.00, 'Deposit', 'Self', 'Online'),
(9004, 1003, '2024-01-18', -2500.00, 'Payment', 'ABC Supplies Inc', 'Wire Transfer'),
(9005, 1004, '2024-01-20', 800.00, 'Deposit', 'Self', 'ATM'),
(9006, 1001, '2024-01-22', -500.00, 'Transfer', 'Account 1002', 'Online'),
(9007, 1002, '2024-01-22', 500.00, 'Transfer', 'From Account 1001', 'Online'),
(9008, 1005, '2024-01-25', 2000.00, 'Deposit', 'Self', 'Branch'),
(9009, 1006, '2024-01-28', -150.00, 'Withdrawal', 'Self', 'ATM'),
(9010, 1007, '2024-02-01', 10000.00, 'Deposit', 'Self', 'Wire Transfer'),
(9011, 1008, '2024-02-03', -15000.00, 'Payment', 'XYZ Corporation', 'Online'),
(9012, 1004, '2024-02-05', -300.00, 'Withdrawal', 'Self', 'Branch'),
(9013, 1009, '2024-02-08', 500.00, 'Deposit', 'Self', 'ATM'),
(9014, 1010, '2024-02-10', 3000.00, 'Deposit', 'Self', 'Online'),
(9015, 1011, '2024-02-12', -1200.00, 'Payment', 'City Utilities', 'Online'),
(9016, 1003, '2024-02-15', 8000.00, 'Deposit', 'Client Payment', 'Wire Transfer'),
(9017, 1012, '2024-02-18', -5000.00, 'Transfer', 'Account 1013', 'Online'),
(9018, 1013, '2024-02-18', 5000.00, 'Transfer', 'From Account 1012', 'Online'),
(9019, 1002, '2024-02-20', -1000.00, 'Withdrawal', 'Self', 'Branch'),
(9020, 1014, '2024-02-22', 1500.00, 'Deposit', 'Self', 'ATM'),
(9021, 1015, '2024-02-25', 7500.00, 'Deposit', 'Self', 'Online'),
(9022, 1001, '2024-02-28', -180.00, 'Payment', 'Electric Company', 'Online'),
(9023, 1006, '2024-03-01', 400.00, 'Deposit', 'Self', 'Branch'),
(9024, 1007, '2024-03-05', -2000.00, 'Withdrawal', 'Self', 'Branch'),
(9025, 1011, '2024-03-08', 2500.00, 'Deposit', 'Self', 'Online'),
(9026, 1004, '2024-03-10', -450.00, 'Transfer', 'Account 1005', 'Online'),
(9027, 1005, '2024-03-10', 450.00, 'Transfer', 'From Account 1004', 'Online'),
(9028, 1008, '2024-03-12', 25000.00, 'Deposit', 'Business Revenue', 'Wire Transfer'),
(9029, 1014, '2024-03-15', -800.00, 'Payment', 'Insurance Premium', 'Online'),
(9030, 1010, '2024-03-18', -500.00, 'Withdrawal', 'Self', 'ATM');

SELECT balance FROM accounts WHERE accountID = 1001;

-- Insert data into AccountHolders table
INSERT INTO AccountHolders (accountID, customerID, accountRole, joinDate,
depositAmount) VALUES
(1001, 5001, 'Primary', '2022-01-15', 1000.00),
(1002, 5001, 'Primary', '2022-02-20', 10000.00),
(1003, 5003, 'Primary', '2021-11-10', 45000.00),
(1004, 5004, 'Primary', '2023-03-05', 2500.00),
(1005, 5005, 'Primary', '2023-01-12', 6000.00),
(1006, 5006, 'Primary', '2023-06-18', 1000.00),
(1007, 5007, 'Primary', '2022-05-22', 20000.00),
(1008, 5008, 'Primary', '2021-08-30', 50000.00),
(1009, 5009, 'Primary', '2023-09-14', 500.00),
(1010, 5010, 'Primary', '2022-12-01', 9000.00),
(1011, 5011, 'Primary', '2023-02-28', 5000.00),
(1012, 5012, 'Primary', '2022-07-19', 25000.00),
(1013, 5013, 'Primary', '2023-04-10', 5000.00),
(1014, 5014, 'Primary', '2023-08-25', 2000.00),
(1015, 5015, 'Primary', '2022-10-05', 15000.00),
(1002, 5002, 'Joint', '2022-03-01', 5000.00),
(1007, 5002, 'Joint', '2022-06-15', 5000.00),
(1012, 5003, 'Authorized User', '2022-08-01', 0.00),
(1004, 5005, 'Joint', '2023-04-20', 1000.00),
(1011, 5010, 'Joint', '2023-03-15', 1000.00);

-- Insert data into AccountReviews table
INSERT INTO AccountReviews (reviewID, customerID, accountID, rating,
reviewText, reviewDate, isVerified) VALUES
(7001, 5001, 1001, 5, 'Excellent checking account with no hidden fees. Very satisfied
with the service.', '2024-02-01', TRUE),
(7002, 5002, 1002, 4, 'Good interest rates on savings. The online banking could be
improved.', '2024-02-05', TRUE),
(7003, 5003, 1003, 5, 'Perfect for my business needs. Transaction limits are
reasonable and fees are low.', '2024-02-10', TRUE),
(7004, 5004, 1004, 5, 'Great customer service at the Beachside branch. Quick
account setup.', '2024-03-08', TRUE),
(7005, 5005, 1005, 3, 'Average savings account. Interest rates could be better.', 
'2024-02-15', TRUE),
(7006, 5006, 1006, 5, 'Perfect student account! No maintenance fees and easy to use
mobile app.', '2024-07-20', TRUE),
(7007, 5007, 1007, 4, 'High interest rates are great, but minimum balance
requirement is steep.', '2024-06-10', TRUE),
(7008, 5008, 1008, 5, 'Excellent business banking experience. Wire transfers are fast
and reliable.', '2024-03-15', TRUE),
(7009, 5009, 1009, 2, 'Had some issues with ATM withdrawals. Customer service
was slow to respond.', '2024-10-01', FALSE),
(7010, 5010, 1010, 4, 'Solid savings account with decent rates. Branch staff is very
helpful.', '2024-01-20', TRUE),
(7011, 5011, 1011, 5, 'Love the premium checking features. Free checks and
overdraft protection.', '2024-03-25', TRUE),
(7012, 5001, 1002, 5, 'Best savings account I have had. Interest compounds
monthly.', '2024-03-01', TRUE),
(7013, 5013, 1013, 4, 'Good family savings option. Easy to track goals.', 
'2024-05-15', TRUE),
(7014, 5014, 1014, 3, 'Standard checking account. Nothing special but gets the job
done.', '2024-09-10', TRUE),
(7015, 5015, 1015, 5, 'Fantastic high-interest savings! Rates beat all competitors.',
'2024-11-05', TRUE);

SELECT * FROM Branches;
SELECT * FROM Accounts;
SELECT * FROM Customers;
SELECT * FROM Transactions;
SELECT * FROM AccountHolders;
SELECT * FROM AccountReviews;


--Task 1: Modifying the Schema with Constraints 

--1)
ALTER TABLE AccountHolders
ADD CONSTRAINT FK_Account
FOREIGN KEY (accountID)
REFERENCES Accounts (accountID)
ON DELETE CASCADE;

ALTER TABLE AccountHolders
ADD CONSTRAINT FK_Customer
FOREIGN KEY (customerID)
REFERENCES Customers (customerID)
ON DELETE CASCADE;

--2)
ALTER TABLE Accounts
ADD CONSTRAINT check_balance
CHECK (balance >= minimumBalance);

--3)
ALTER TABLE Accounts
ADD CONSTRAINT check_interestRate
CHECK (interestRate >= 0);

--4)
ALTER TABLE Transactions
ADD CONSTRAINT check_amount
CHECK (amount <> 0);

--5)
--Already added from Assignment #2

--6)
ALTER TABLE Customers
ADD CONSTRAINT check_date
CHECK (registrationDate <= CURRENT_DATE); 

--7)
--Already added from Assignment #2

--Task 2: Unit Testing Constraints 
--1) Attempt to insert an Account where balance is less than minimumBalance (e.g.,
--balance = 10.00, minimumBalance = 500.00) → should fail
INSERT INTO Accounts (accountID, accountType, branchID, balance, interestRate,
accountDescription, openDate, minimumBalance, isActive) VALUES
(1099, 'TEST', 1, 10.00, 0.10, 'TEST', '2023-01-15', 500.00, TRUE);
SELECT * FROM Accounts;

--2) Attempt to insert an Account with interestRate = -1.5 → should fail
INSERT INTO Accounts (accountID, accountType, branchID, balance, interestRate,
accountDescription, openDate, minimumBalance, isActive) VALUES
(1099, 'TEST', 1, 1000.00, -1.5, 'TEST', '2023-01-15', 500.00, TRUE);
SELECT * FROM Accounts;

--3) Attempt to insert a Transaction with amount = 0 → should fail
INSERT INTO Transactions (transactionID, accountID, transactionDate, amount,
transactionType, recipient, paymentMethod) VALUES
(9999, 9999, '2020-01-05', 0, 'Deposit', 'Self', 'Branch');
SELECT * FROM Transactions;

--4) Attempt to insert an AccountReview with rating = 7 → should fail
-- Insert data into AccountReviews table
INSERT INTO AccountReviews (reviewID, customerID, accountID, rating,
reviewText, reviewDate, isVerified) VALUES
(9999, 9999, 9999, 7, 'TEST', '2024-02-01', TRUE);
SELECT * FROM AccountReviews;

--5) Attempt to insert a Customer with registrationDate = '2027-06-01' (a future date)
--→ should fail
INSERT INTO Customers (customerID, firstName, lastName, email, phone,
dateOfBirth, registrationDate, isActive) VALUES
(9999, 'TEST', 'TEST', 'test.test@email.com', '904-111-0000', '1985-01-19', '2027-06-01', TRUE);
SELECT * FROM  Customers;

--6) Attempt to insert a duplicate email in the Customers table (use any existing email,
--e.g., john.smith@email.com) → should fail
INSERT INTO Customers (customerID, firstName, lastName, email, phone,
dateOfBirth, registrationDate, isActive) VALUES
(9999, 'TEST', 'TEST', 'john.smith@email.com', '904-111-0000', '1985-01-19', '2025-06-01', TRUE);
SELECT * FROM  Customers;

-- Task 3: Create and Query Views (05 points)
--Part A: High-Value Accounts View
--1. Create a view called HighValueAccountsView that shows:
CREATE VIEW HighValueAccountsView AS
SELECT a.accountID, a.accountType, b.branchName, a.balance, a.interestRate, COUNT(t.transactionID) AS transactionCount
FROM Accounts a
JOIN Transactions t ON a.accountID = t.accountID
JOIN branches b ON a.branchID = b.branchID
WHERE a.balance > 10000
GROUP BY a.accountID, a.accountType, b.branchName, a.balance, a.interestRate;

--2) Write a query using this view to list all Savings accounts that appear in the view, ordered
--by balance descending.
SELECT * 
FROM HighValueAccountsView
WHERE accountType = 'Savings'
ORDER BY balance DESC;

--Part B: Customer Activity Summary View
--3. Create a view called CustomerActivityView
CREATE VIEW CustomerActivityView AS
SELECT c.customerID, c.firstName, c.lastName, c.email, COUNT(DISTINCT ah.accountID) AS accountCount, 
SUM(ah.depositAmount) AS totalDeposited, COUNT(DISTINCT ar.reviewID) AS reviewCount
FROM Customers c
JOIN AccountHolders ah ON c.customerID = ah.customerID
JOIN AccountReviews ar ON c.customerID = ar.customerID
GROUP BY c.customerID, c.firstName, c.lastName, c.email;

--4. Write a query using this view to find customers who have written at least 2 reviews OR
--hold more than 1 account, ordered by accountCount descending
SELECT * 
FROM CustomerActivityView
WHERE reviewCount >= 2 OR accountCount > 1
ORDER BY accountCount DESC;

--Task 4: Transactions (SQL Transaction Blocks) (05 points)

--1. Write a transaction that performs a fund transfer between two accounts:
-- Deduct $1,000.00 from account 1007 (insert a Withdrawal transaction record)
-- Add $1,000.00 to account 1010 (insert a Deposit transaction record)
-- Both operations must succeed together, or neither should apply.
-- Use transactionID values of 9031 and 9032

BEGIN;
	UPDATE Accounts 
	SET balance = balance - 1000
	WHERE accountID = 1007;
	
	INSERT INTO Transactions (transactionID, accountID, transactionDate, amount,
	transactionType, recipient, paymentMethod) VALUES
	(9031, 1007, '2026-03-10', -1000, 'Transfer', 'Account 1010', 'Online');
	
	UPDATE Accounts 
	SET balance = balance + 1000
	WHERE accountID = 1010;
	
	INSERT INTO Transactions (transactionID, accountID, transactionDate, amount,
	transactionType, recipient, paymentMethod) VALUES
	(9032, 1010, '2026-03-10', 1000, 'Transfer', 'From Account 1007', 'Online');

	SELECT *
	FROM Transactions
	WHERE transactionID IN (9031, 9032);
	
	COMMIT;
ROLLBACK;
END;


--2. Write a transaction that attempts to:
-- Insert a new customer (customerID = 5016, name: 'Alex', 'Turner', email:
--'alex.turner@email.com', phone: '904-555-0116', dateOfBirth: '1997-04-22',
--registrationDate: CURRENT_DATE)
-- Insert an AccountHolder record linking customer 5016 to a non-existent accountID
--(e.g., 9999)
-- The second INSERT should fail due to a foreign key violation.

BEGIN;

INSERT INTO Customers (customerID, firstName, lastName, email, phone,
dateOfBirth, registrationDate, isActive) VALUES
(5016, 'Alex', 'Turner', 'alex.turner@email.com','904-555-0116','1997-04-22', CURRENT_DATE, true);

INSERT INTO AccountHolders (accountID, customerID, accountRole, joinDate,
depositAmount) VALUES
(9999, 5016, 'Primary', '2022-01-15', 1000.00);

COMMIT;
ROLLBACK;
END;

SELECT * 
FROM Customers
WHERE customerID = 5016;


--3. Write a transaction that:
-- Updates the balance of account 1009 from 450.00 to 1500.00
-- Inserts a corresponding Deposit transaction record (transactionID = 9033, amount =
--1050.00, type = 'Deposit', paymentMethod = 'Branch')
-- Commits only if both operations succeed.

BEGIN;

UPDATE Accounts 
SET balance = 450 + 1050
WHERE accountID = 1009;

INSERT INTO Transactions (transactionID, accountID, transactionDate, amount,
transactionType, recipient, paymentMethod) VALUES
(9033, 1009, '2026-10-03', 1050, 'Deposit', 'Self', 'Branch');

COMMIT;
ROLLBACK;
END;

SELECT accountID, balance
FROM Accounts
WHERE accountID = 1009;

SELECT *
FROM Transactions
WHERE transactionID = 9033;


--Task 5: Create Indexes (05 points)
--1. Create an index on the email column in the Customers table.
--Explain in a comment how this index improves performance when a customer logs in using
--their email address.

CREATE INDEX CustomerEmailIndex ON Customers(email);
/*
This index imrpoves performance when a customer logs in using their email 
address signiciantly. Instead of the database searching through multiple
records of customer addresses and other records stored, the index provides a 
faster way to match the corresponding email to customer. 
*/

--2. Create a composite index on Transactions (accountID, transactionDate) columns.
--Explain in a comment why this composite index benefits queries that retrieve transaction
--history for a specific account within a date range
CREATE INDEX Transaction_Index ON Transactions(accountID, transactionDate);
/*
By creating a composite index on Transactions, grouping
accountID and transactionDate will be sorted together
for faster access. This benefits queries that retrieve transaction
history for a specific data range and account because both ID and 
data are already sorted so the database can quickly identify and
match the correct data faster then searching independently for both in
a unsorted fashion.
*/

--3. Create an index on the accountID column in the AccountReviews table.
--Write a SELECT query that retrieves all reviews for account 1002 (join with Customers
--to show reviewer name, rating, reviewText, and reviewDate).
--Explain in a comment how the index improves this query.
CREATE INDEX account_ID_AccountReviews_Index ON AccountReviews(accountID);

SELECT ar.reviewID, c.firstName, c.lastName, ar.rating, ar.reviewText, ar.reviewDate, ar.isVerified
FROM AccountReviews ar
JOIN Customers c ON c.customerID = ar.customerID
WHERE ar.accountID = 1002;
/*
This index also helps find the corresponding account with review.
Account is automatically sorted in a group when put into an index.
This means the database does not need to scan through the whole
database to find the correct account with review instead of scanning the whole 
accountReviews table which can be multiple rows of reviews.
*/

--4. Create a composite index on AccountHolders (customerID, accountID)l columns.
--Explain in a comment when this index is most beneficial and write a SELECT query that
--makes direct use of it (e.g., find all accounts held by customer 5001 along with their role
--and joinDate).
CREATE INDEX composite_AccountHolders_Index ON AccountHolders (customerID, accountID);
SELECT ah.accountID, ah.accountRole, ah.joinDate, ah.depositAmount, a.accountType, a.balance
FROM AccountHolders ah
JOIN Accounts a ON ah.accountID = a.accountID
WHERE ah.customerID = 5001;
/*
This index is most useful for when a bank needs to 
quickly identify a customer based off of their customerID.
With the composite accountID, the bank can also quickly identify
the customers accounts. The select query clearly shows the number of
different accounts that customerID 5001 has at their bank. One way
I think a bank may need this is when a customer is trying to deposit/withdrawal 
money from their account. When I go to my bank the teller asks me for me accountID
then procides to tell me would I like to use my checkings or savings and it is all
instantly located.
*/


--STORED PROCEDURES
CREATE OR REPLACE FUNCTION update_AccountBalance()
RETURNS TRIGGER AS $$
BEGIN
	IF NEW.transactionType ILIKE 'Deposit' THEN
		UPDATE Accounts
		SET balance = balance + NEW.amount
		WHERE accountID = NEW.accountID;

	ELSEIF NEW.transactionType ILIKE 'Withdrawal' OR
	NEW.transactionType ILIKE 'Payment' THEN
		UPDATE Accounts
		SET balance = balance - ABS(NEW.amount)
		WHERE accountID = NEW.accountID;

	ELSEIF NEW.transactionType ILIKE 'Transfer' 
	OR NEW.transactionType ILIKE 'Withdrawal' THEN
	UPDATE Accounts
		SET balance = balance - ABS(NEW.amount)
		WHERE accountID = NEW.accountID;
		
	ELSEIF NEW.transactionType ILIKE 'Internal Transfer' 
	OR NEW.transactionType ILIKE 'Deposit' THEN
	UPDATE Accounts
		SET balance = balance + ABS(NEW.amount)
		WHERE accountID =  NEW.accountID;
	END IF;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_balance
AFTER INSERT ON Transactions
FOR EACH ROW 
EXECUTE FUNCTION update_AccountBalance();


CREATE OR REPLACE FUNCTION performTransaction(
p_transactionID INT, -- Added this in order to autogenerate the IDs in my java code
p_accountID INT,
p_amount NUMERIC,
p_transactionType VARCHAR,
p_recipient VARCHAR,
p_paymentMethod VARCHAR
)
RETURNS NUMERIC AS $$
DECLARE 
	v_new_balance NUMERIC;
BEGIN
	INSERT INTO Transactions(transactionID, accountID, transactionDate, amount, transactionType, recipient, paymentMethod)
	VALUES (p_transactionID, p_accountID, CURRENT_DATE, p_amount, p_transactionType, p_recipient, p_paymentMethod);

	SELECT balance INTO v_new_balance FROM Accounts WHERE accountID = p_accountID;
	RETURN v_new_balance;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION getAccountSummary(p_accountID INT)
RETURNS TABLE(balance NUMERIC, total_deposits NUMERIC, total_withdrawals NUMERIC, transactions_count INT) AS $$
BEGIN 
	RETURN QUERY 
	SELECT a.balance::NUMERIC, 
	COALESCE(SUM(CASE WHEN t.transactionType ILIKE 'Deposit' THEN t.amount ELSE 0 END), 0)::NUMERIC,
	COALESCE(SUM(CASE WHEN t.transactionType NOT ILIKE 'Deposit' THEN ABS(t.amount) ELSE 0 END), 0)::NUMERIC,
	COUNT(t.transactionID)::INT
	FROM Accounts a
	LEFT JOIN Transactions t ON a.accountID = t.accountID
	WHERE a.accountID = p_accountID
	GROUP BY a.accountID, a.balance;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION checkSufficientBalance(p_accountID INT, p_requestedAmount NUMERIC)
RETURNS BOOLEAN AS $$
DECLARE 
	v_current_balance NUMERIC;
BEGIN
	SELECT balance INTO v_current_balance FROM Accounts WHERE accountID = p_accountID;
	RETURN COALESCE(v_current_balance, 0) >= p_requestedAmount;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION getLowBalanceAlerts(p_threshold NUMERIC)
RETURNS TABLE(
accountID INT, 
accountType VARCHAR,
branchID INT,
balance NUMERIC,
interestRate NUMERIC,
accountDescription VARCHAR,
openDate DATE,
minimumBalance NUMERIC,
isActive BOOLEAN
) AS $$
BEGIN
	RETURN QUERY
	SELECT a.accountID::INT, a.accountType::VARCHAR, 
        a.branchID::INT, 
        a.balance::NUMERIC, 
        a.interestRate::NUMERIC, 
        a.accountDescription::VARCHAR, 
        a.openDate::DATE, 
        a.minimumBalance::NUMERIC, 
        a.isActive::BOOLEAN 
		FROM Accounts a 
		WHERE a.balance < p_threshold;
END;
$$ LANGUAGE plpgsql;











