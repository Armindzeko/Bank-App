
import java.sql.*;
import java.util.Scanner;

public class BankApp {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Banking_DB";
    private static final String USER = "postgres";
    private static final String PASS = "your_password";


    public static Connection getConnection() {
        try {
            // TODO: Establish connection
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            stmt.execute("SET search_path TO Banking");
            System.out.println("Connected to database");
            return conn;
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
            return null;
        }
    }


    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // TODO: Create all required tables
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Branches (
                        branchID INTEGER PRIMARY KEY,
                        branchName TEXT,
                        location TEXT,
                        isActive BOOLEAN
                    );
                """);
        // TODO: Add remaining tables (Accounts, Customers, Transactions, etc.)
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Accounts(
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
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Customers(
                    customerID INT PRIMARY KEY,
                                firstName TEXT,
                                lastName TEXT,
                                email VARCHAR(150) UNIQUE,
                                phone VARCHAR(20),
                                dateOfBirth DATE,
                                registrationDate DATE,
                                isActive BOOLEAN
                                );
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Transactions(
                            transactionID INT PRIMARY KEY,
                            accountID INT,
                            FOREIGN KEY (accountID) REFERENCES Accounts(accountID),
                            transactionDate DATE,
                            amount NUMERIC(12,2),
                            transactionType VARCHAR(100),
                            recipient VARCHAR(100),
                            paymentMethod VARCHAR(100)
                            );
                """);

        stmt.execute("""
                   CREATE TABLE IF NOT EXISTS AccountHolders(
                           accountID INT NOT NULL,
                           customerID INT NOT NULL,
                           FOREIGN KEY (accountID) REFERENCES Accounts(accountID),
                           FOREIGN KEY (customerID) REFERENCES Customers(customerID),
                           accountRole VARCHAR(100),
                           joinDate DATE,
                           depositAmount NUMERIC(12,2),
                           PRIMARY KEY(accountID, customerID)
                           );
                """);

        stmt.execute("""
                  CREATE TABLE IF NOT EXISTS AccountReviews(
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
                """);
    }

/* DELETED EXPLAINED IN DOCUMENTATION
    public static void insertSampleData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // TODO: Insert sample branch, customer, account
        //BRANCHES
        stmt.execute("""
                INSERT INTO Branches (branchID, branchName, location, isActive)
                VALUES
                (1, 'Downtown Branch', '123 Main Street, Jacksonville, FL 32202', TRUE),
                ON CONFLICT (branchID) DO NOTHING;
                """);

        //ACCOUNTS
        stmt.execute("""
                INSERT INTO Accounts (accountID, accountType, branchID, balance, interestRate, accountDescription, openDate, minimumBalance, isActive) 
                VALUES
                (1001, 'Checking', 1, 2500.00, 0.10, 'Standard checking account with no monthly fees', '2022-01-15', 100.00, TRUE),
                (1002, 'Savings', 1, 15000.00, 2.50, 'High-yield savings account', '2022-02-20', 500.00, TRUE);
                        """);

        stmt.execute("""
                INSERT INTO Transactions (transactionID, accountID, transactionDate, amount, transactionType, recipient, paymentMethod)
                VALUES
                (9001, 1001, '2024-01-05', 1500.00, 'Deposit', 'Self', 'Branch'),
                (9030, 1010, '2024-03-18', -500.00, 'Withdrawal', 'Self', 'ATM');
                """);

        stmt.execute("""
                INSERT INTO AccountHolders (accountID, customerID, accountRole, joinDate, depositAmount)
                VALUES
                (1001, 5001, 'Primary', '2022-01-15', 1000.00),
                (1002, 5001, 'Primary', '2022-02-20', 10000.00);
                """);

        stmt.execute("""
                INSERT INTO AccountReviews (reviewID, customerID, accountID, rating, reviewText, reviewDate, isVerified)
                VALUES
                (7001, 5001, 1001, 5, 'Excellent checking account with no hidden fees. Very satisfied with the service.', '2024-02-01', TRUE),
                (7015, 5015, 1015, 5, 'Fantastic high-interest savings! Rates beat all competitors.', '2024-11-05', TRUE);
                """);


        //stmt.execute("INSERT OR IGNORE INTO Branches VALUES (1, 'Main Branch', 'City', 1)");

        System.out.println("Sample data inserted (partial)");
    }
*/
    /*
    HELPER METHODS
     */

    /**
     * Helper method #1... This method helps get the current balance from a specific account that is passed
     * in from the user or the method.
     * @param conn The database connection.
     * @param accountID Unique identifier for the accountID passed in.
     * @return The current balance of accountID passed in or -1 if account not found.
     * @throws SQLException If database error occurs throw exception.
     */
    public static double getBalance(Connection conn, int accountID) throws SQLException {
        // TODO: Write query to get balance
        Statement stmt = conn.createStatement();

        String query = "SELECT balance FROM Accounts WHERE accountID = " + accountID;
        ResultSet rs = stmt.executeQuery(query);

        if (rs.next()) {
            double balance = rs.getDouble("Balance");
            System.out.println("Balance: " + balance);
            return balance;
        }
        return -1; // placeholder
    }

    // Set the Account Minimum ID the starting point from database 1000
    private static final int ACCOUNT_MIN_ID = 1000;

    /**
     * Helper Method #2... This method helps auto generate an accountID. AccountIDs start at 1001
     * in the database and this method was created when user specifies they want to add an account.
     * So in return this method auto increments from the last known ID in the database by 1.
     * @param conn The database connection.
     * @return A new unique AccountID Int
     * @throws SQLException If database error occurs throw exception.
     */
    private static int generateAccountID(Connection conn) throws SQLException {
        String findMaxIDA = "SELECT MAX(accountID) FROM Accounts";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(findMaxIDA)) {
            if (rs.next()) {
                int currentMax = rs.getInt(1);

                if (currentMax < ACCOUNT_MIN_ID) {
                    return ACCOUNT_MIN_ID + 1;
                } else {
                    return currentMax + 1;
                }
            } else {
                return ACCOUNT_MIN_ID + 1;
            }
        }
    }
    // Set the Review Minimum ID the starting point from database 7000
    private static final int REVIEW_MIN_ID = 7000;
    /**
     * Helper Method #3... This method helps auto generate an reviewID. ReviewIDs start at 7001
     * in the database and this method was created when user specifies they want to add a review.
     * So in return this method auto increments from the last known ID in the database by 1.
     *@param conn The database connection.
     * @return A new unique ReviewID Int
     * @throws SQLException If database error occurs throw exception.
     */
    private static int generateReviewID(Connection conn) throws SQLException {
        String findMaxIDA = "SELECT MAX(reviewID) FROM AccountReviews";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(findMaxIDA)) {
            if (rs.next()) {
                int currentMax = rs.getInt(1);

                if (currentMax < REVIEW_MIN_ID) {
                    return REVIEW_MIN_ID + 1;
                } else {
                    return currentMax + 1;
                }
            } else {
                return REVIEW_MIN_ID + 1;
            }
        }
    }

    // Set the Customer Minimum ID the starting point from database 5000
    private static final int CUSTOMER_MIN_ID = 5000;

    /**
     * Helper Method #4... This method helps auto generate an customerID. CustomerIDs start at 5001
     * in the database and this method was created when user specifies they want to add a customer.
     * So in return this method auto increments from the last known ID in the database by 1.
     * @param conn The database connection.
     * @return A new unique CustomerID Int
     * @throws SQLException If database error occurs throw exception.
     */
    private static int generateCustomerID(Connection conn) throws SQLException {
        String findMaxIDC = "SELECT MAX(customerID) FROM Customers";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(findMaxIDC)) {
            if (rs.next()) {
                int currentMax = rs.getInt(1);

                if (currentMax < CUSTOMER_MIN_ID) { // Handle dirty customer records reset to 5001
                    return CUSTOMER_MIN_ID + 1;
                } else {
                    return currentMax + 1; // Add on to the current max
                }
            } else {
                return CUSTOMER_MIN_ID + 1; // If table is completely empty start 5001
            }
        }
    }

    static String userRole = null; // Keep track of Customer or Admin after the login process
    static int userID = -1; // Placeholder updates with customerID

    /**
     * Helper Method #5... This method handles the login process for both types of users which
     * are customers and admins. The whole session is then created and maintained so other functions
     * can work as intended based on the role of the user. If the user inputs invalid credentials,
     * this function will return the user to the login page and prompt user for credentials once more.
     * @param conn The database connection.
     * @param sc Scanner Object for input.
     * @return Return True for successful login or False if user quits the app or provides invalid credentials.
     * @throws SQLException Throw exception for database errors.
     */
    public static boolean login(Connection conn, Scanner sc) throws SQLException {

        while (true) {
            System.out.println("Welcome to Banking App!");
            System.out.println("1 == Customer Login");
            System.out.println("2 == Admin Login");
            System.out.println("0 == Exit Bank App");
            int type = sc.nextInt();
            sc.nextLine();

            if (type == 0) {
                System.out.println("Exiting Application... Thank you for banking with us!");
                return false;
            }

            System.out.println("Please enter username / email: ");
            String user = sc.nextLine();
            System.out.println("Please enter password: ");
            String password = sc.nextLine();


            String customerSQL = "SELECT * FROM Customers WHERE email = ? AND password = ?";
            String adminSQL = "SELECT * FROM Admins WHERE username = ? AND password = ?";

            if (type == 1) {
                PreparedStatement ps1 = conn.prepareStatement(customerSQL);
                ps1.setString(1, user);
                ps1.setString(2, password);

                ResultSet rs = ps1.executeQuery();

                if (rs.next()) {
                    userID = rs.getInt("customerID");
                    userRole = "CUSTOMER";
                    System.out.println("Welcome back Customer #" + userID);
                    System.out.println();
                    return true;
                }
            } else if (type == 2) {
                PreparedStatement ps2 = conn.prepareStatement(adminSQL);
                ps2.setString(1, user);
                ps2.setString(2, password);

                ResultSet rs = ps2.executeQuery();

                if (rs.next()) {
                    userID = rs.getInt("adminID");
                    userRole = "ADMIN";
                    System.out.println("Welcome back Admin #" + userID);
                    System.out.println();
                    return true;
                }
            }

            System.out.println("ERROR: Invalid Login (username/password) Please try again");
        }
    }

    /**
     * Helper Method #6... This method further validates what a user can do after logging in.
     * In this method the userRole created from logIn is used to define what admins and customers
     * can do throughout the program. Admins have universal access and customers have restricted access
     * to make changes only to the accounts they own.
     * @param conn The database connection.
     * @param accountID The accountID to check.
     * @return Return True if access is permitted or False if denied.
     * @throws SQLException Throw exception for database errors.
     */
    public static boolean hasAccess(Connection conn, int accountID) throws SQLException {
        if (userRole.equalsIgnoreCase("admin"))
            return true;

        String sql = "SELECT * FROM AccountHolders WHERE accountID = ? AND customerID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountID);
            ps.setInt(2, userID);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    /*
    MAIN MENU THEN CONTINUES TO MODULES
     */

    /**
     * This method is the main menu both admins and customers see when
     * they log in. Once passed through the main menu customers and users are
     * verified for what permissions they have then prompted to individual menus.
     * @param conn The database connection.
     * @throws SQLException Throw exception for database errors.
     */
    public static void mainMenu(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("||| WELCOME TO THE BANK APP |||");
            System.out.println("1. Branch Management Module");
            System.out.println("2. Account Management Module");
            System.out.println("3. Customer Management Module");
            System.out.println("4. Transaction Processing Module");
            System.out.println("5. Review Management Module");
            System.out.println("0. Logout");

            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {

                case 1 -> branchModule(conn, sc);
                case 2 -> accountModule(conn, sc);
                case 3 -> customerModule(conn, sc);
                case 4 -> transactionModule(conn, sc);
                case 5 -> reviewModule(conn, sc);

                case 0 -> {return;}

                default -> System.out.println("Please select the correct choice (0-5)");
            }

        }
    }

    /**
     * This method is the branch module. Here users are checked if customer or admin
     * role and provided their individual menus for what they can perform in this module.
     * @param conn Database Connection
     * @param sc Scanner to get input from the user
     * @throws SQLException Throw any database error.
     */
    public static void branchModule(Connection conn, Scanner sc) throws SQLException {
        System.out.println("BRANCH MANAGEMENT MODULE");
        if (userRole.equalsIgnoreCase("admin")) {
            System.out.println("1. View All Branches Summary Report");
            System.out.println("2. Add Branch");
            System.out.println("3. Update Branch");
            System.out.println("4. Activate / Deactivate Branch");
            System.out.println("0. Return");
        } else {
            System.out.println("1. View all active branches & details including total accounts and total balance held");
            System.out.println("2. Search branches by name or location");
            System.out.println("0. Return");
        }

        int choice = sc.nextInt();
        sc.nextLine();
        if (choice == 0) return;

        if (userRole.equalsIgnoreCase("admin")) {
            switch (choice) {
                case 1 -> viewBranches(conn, userRole);
                case 2 -> addBranch(conn, sc);
                case 3 -> {
                    System.out.println("What is the current BranchID?");
                    int branchID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Branch Name (Update): ");
                    String branchName = sc.nextLine();
                    System.out.println("Branch Location (Update): ");
                    String branchLoc = sc.nextLine();
                    updateBranch(conn, branchID, branchName, branchLoc);
                }
                case 4 -> {
                    System.out.println("What is the BranchID?");
                    int branchID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Activate or Inactivate Branch (True/False)?");
                    boolean status = sc.nextBoolean();

                    String sql = "UPDATE Branches SET isActive = ? WHERE branchID = " + branchID;
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setBoolean(1, status);
                        ps.executeUpdate();

                        if (!status) System.out.println("Branch " + branchID + " is set to Inactive.");
                        else System.out.println("Branch " + branchID + " is set to Active.");
                    }
                }

                default -> System.out.println("Invalid Choice");
            }
        } else {
            if (choice == 1) {
                viewBranches(conn, userRole);
            }
            if (choice == 2) {
                viewBranchesBySpecs(conn, sc);
            }
         else{
            System.out.println("Invalid Choice");
            }
        }
    }

    /**
     * This method Searches and displays branches based on specific user-defined criteria.
     * Allows filtering by either branch name or physical location.
     * @param conn The active database connection.
     * @param sc   Scanner object to capture the user's filter choice and search term.
     * @throws SQLException If a database access error occurs during the query.
     */
    public static void viewBranchesBySpecs(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Filter by: ");
        System.out.println("1. Branch Name");
        System.out.println("2. Branch Location");
        System.out.println("0. Return");
        int choice = sc.nextInt();
        sc.nextLine();


        String sql = "SELECT * FROM Branches WHERE ";
        if (choice == 1) sql += " branchName = ?";
        if (choice == 2) sql += " location = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (choice == 1) {
                System.out.println("Enter Branch Name: ");
                String branchName = sc.nextLine();
                ps.setString(1, branchName);
            }
            else if (choice == 2) {
                System.out.println("Enter Branch Location: ");
                String branchLoc = sc.nextLine();
                ps.setString(1, branchLoc);
            }
            if (choice == 0) return;

            ResultSet rs = ps.executeQuery();
            String row = ("%-10s %-18s %-45s %-10s%n");
            System.out.printf(row, "branchID", "branchName", "location", "isActive");

            while (rs.next()) {
                System.out.printf(row, rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getBoolean(4));
            }
        }
    }

    /**
     * ADMIN ONLY METHOD Updates the details of an existing branch.
     * This method modifies both the name and the location for a specific branch ID.
     * @param conn The active database connection.
     * @param branchID The unique ID of the branch to be updated.
     * @param branchName The new name to assign to the branch.
     * @param branchLoc The new location address to assign to the branch.
     * @throws SQLException If a database access error occurs or the SQL syntax is invalid.
     */
    public static void updateBranch(Connection conn, int branchID, String branchName, String branchLoc) throws SQLException {
        String sql = "UPDATE Branches SET branchName = ?, location = ? WHERE branchID = " + branchID;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, branchName);
            ps.setString(2, branchLoc);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Successful Update On BranchID = " + branchID);
            }
            else {
                System.out.println("ERROR: Unable to find BranchID = " + branchID);
            }
        }
    }

    /**
     * ADMIN ONLY Adds a new branch to the system.
     * Automatically calculates the next available Branch ID using a subquery and
     * sets the new branch to 'Active' by default.
     * @param conn The active database connection.
     * @param sc   Scanner object to capture the new branch's name and location.
     * @throws SQLException If a database access error occurs or ID generation fails.
     */
    public static void addBranch(Connection conn, Scanner sc) throws SQLException {
        String sql = "INSERT INTO Branches (branchID, branchName, location, isActive) " +
                "VALUES ((SELECT COALESCE(MAX(branchID), 0) + 1 FROM Branches), ?, ?, true)";

        System.out.println("Branch Name: ");
        String branchName = sc.nextLine();
        System.out.println("Branch Location: ");
        String branchLoc = sc.nextLine();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, branchName);
            ps.setString(2, branchLoc);
            ps.executeUpdate();

            System.out.println("Branch " + branchName + " was successfully added!");
        }
    }

    /**
     * ADMIN AND CUSTOMER Generates and displays a full report of all branches.
     * Behavior varies by role:
     * Admin: Sees all branches (active/inactive), including total account counts,
     * sum of balances, and average interest rates per branch.
     * Customer: Sees only active branches with account counts and total balances.
     * @param conn The active database connection.
     * @param userRole The role of the logged-in user (ADMIN OR CUSTOMER).
     * @throws SQLException If a database access error occurs or aggregate functions fail.
     */
    public static void viewBranches(Connection conn, String userRole) throws SQLException {

        String sql = userRole.equalsIgnoreCase("admin")
                ? "SELECT b.branchID, b.branchName, COUNT(a.accountID) AS total_accounts, " +
                "SUM(a.balance) AS total_balance, AVG(a.interestRate) AS avg_interestrate, " +
                " b.isActive " +
                "FROM Branches b " +
                "LEFT JOIN accounts a ON b.branchID = a.branchID " +
                "GROUP BY b.branchID, b.branchName " +
                "ORDER BY b.branchID"
                : "SELECT b.branchID, b.branchName, b.isActive, COUNT(a.accountID) AS total_accounts, " +
                "SUM(a.balance) AS total_balance " +
                "FROM Branches b " +
                "LEFT JOIN accounts a ON b.branchID = a.branchID " +
                "WHERE b.isActive = " + true +
                " GROUP BY b.branchID, b.branchName " +
                "ORDER BY b.branchID";

        String column = "%-25s ";
        String row = column + column + column + column + column + column + "%n";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (userRole.equalsIgnoreCase("admin")) {
                System.out.printf(row, "BranchID", "BranchName", "Total_Accounts", "Total_Balance", "AVG_Interest", "isActive");
            }
            else {
                System.out.printf(row, "BranchID", "BranchName", "isActive", "Total_Accounts", "Total_Balance");
            }

            while (rs.next()) {
                if (userRole.equalsIgnoreCase("admin")) {
                    System.out.printf(row, rs.getInt(1), rs.getString(2),
                            rs.getInt(3), "$" + String.format("%.2f", rs.getDouble(4)),
                            String.format("%.2f", rs.getDouble(5)), rs.getBoolean(6));
                } else {
                    System.out.printf(row, rs.getInt(1), rs.getString(2),
                            rs.getBoolean(3), rs.getInt(4),
                            "$" + String.format("%.2f", rs.getDouble(5)));
                }

            }

        }
    }

    /**
     * This method is the account module. Here users are checked if customer or admin
     * role and provided their individual menus for what they can perform in this module.
     * @param conn Database Connection
     * @param sc Scanner to get input from the user
     * @throws SQLException Throw any database error.
     */
    public static void accountModule(Connection conn, Scanner sc) throws SQLException {
        System.out.println("WELCOME TO ACCOUNT MODULE");

        if (userRole.equalsIgnoreCase("admin")) {
            System.out.println("1. Open a new account for a customer");
            System.out.println("2. Update account information (interest rate, description)");
            System.out.println("3. Deactivate/reactivate an account");
            System.out.println("4. View full inventory of accounts grouped by type with balances");
            System.out.println("5. View All Accounts");
            System.out.println("6. View Accounts With Low Balance");
            System.out.println("7. View specific Accounts summary");
        } else {
            System.out.println("1. View My Accounts & Details (INCLUDES LOW BALANCE ACCOUNTS)");
            System.out.println("2. View my Accounts summary");
        }
        System.out.println("0. Exit");
        int choice = sc.nextInt();
        sc.nextLine();
        if (choice == 0) return;

        if (userRole.equalsIgnoreCase("admin")) {
            switch (choice) {

                case 1 -> addAccount(conn, sc);
                case 2 -> {
                    System.out.println("What is the current AccountID?");
                    int accountID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Interest Rate (Update): ");
                    double interestRate = sc.nextDouble();
                    sc.nextLine();
                    System.out.println("Account Description (Update): ");
                    String desc = sc.nextLine();
                    updateAccount(conn, accountID, interestRate, desc);
                }
                case 3 -> {
                    System.out.println("What is the AccountID?");
                    int accountID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Activate or Inactivate Account (True/False)?");
                    boolean status = sc.nextBoolean();

                    String sql = "UPDATE Accounts SET isActive = ? WHERE accountID = " + accountID;
                    try(PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setBoolean(1, status);
                        ps.executeUpdate();

                        if (!status) System.out.println("Account " + accountID + " is set to Inactive.");
                        else System.out.println("Account " + accountID + " is set to Active.");
                    }
                }
                case 4 -> viewAccountInventory(conn);

                case 5 -> viewAccounts(conn, userRole);

                case 6 -> {
                    System.out.println("Threshold: ");
                    double threshold = sc.nextDouble();
                    sc.nextLine();
                    lowBalanceAlert(conn, threshold);
                }

                case 7 -> {
                    System.out.println("Please enter the AccountID: ");
                    int accountID = sc.nextInt();
                    accountSummary(conn, accountID);
                }

                default -> System.out.println("Invalid Choice");
            }
        }
        else {
            if (choice == 1) { viewAccounts(conn, userRole); }
            if (choice == 2) {
                System.out.println("Please enter the AccountID: ");
                int accountID = sc.nextInt();
                if (hasAccess(conn, accountID)) {
                    accountSummary(conn, accountID);
                }
                else { System.out.println("ERROR: ACCESS DENIED");}
            }
            else { System.out.println("Invalid Choice"); }
        }
    }

    /**
     * This method creates new bank account and links it to a customer.
     * This method performs a two table insertion:
     * 1. Inserts the core account details into the Accounts table.
     * 2. Insert a relationship record in the AccountHolders table
     * to associate the account with a specific CustomerID and role.
     * @param conn The active database connection.
     * @param sc   Scanner object used to collect account details (type, branch, balance, etc.).
     * @throws SQLException If a database access error occurs or a foreign key constraint is violated.
     */
    public static void addAccount(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Which CustomerID is creating a new Account?");
        int customerID = sc.nextInt();

        int accountID = generateAccountID(conn);
        sc.nextLine();
        System.out.println("Account Type: ");
        String accountType = sc.nextLine();
        System.out.println("BranchID: ");
        int branchID = sc.nextInt();
        System.out.println("Balance: ");
        double balance = sc.nextDouble();
        System.out.println("Interest Rate (.2): ");
        double interestRate = sc.nextDouble();
        sc.nextLine();
        System.out.println("Account Description: ");
        String accountDesc = sc.nextLine();
        System.out.println("MIN Balance: ");
        double minBalance = sc.nextDouble();
        sc.nextLine();
        System.out.println("Account Role: ");
        String accountRole = sc.nextLine();
        System.out.println("Deposit Amount: ");
        double depositAmt = sc.nextDouble();

        String insertAccounts = "INSERT INTO Accounts (accountID, accountType, branchID, balance, interestRate, " +
                "accountDescription, openDate, minimumBalance, isActive) VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, ?, TRUE)";

        try (PreparedStatement ps = conn.prepareStatement(insertAccounts)) {
            ps.setInt(1, accountID);
            ps.setString(2, accountType);
            ps.setInt(3, branchID);
            ps.setDouble(4, balance);
            ps.setDouble(5, interestRate);
            ps.setString(6, accountDesc);
            ps.setDouble(7, minBalance);

            ps.executeUpdate();
            System.out.println("Account was successfully added with ID = " + accountID);
        }

        String insertAccountHolders = "INSERT INTO AccountHolders (accountID, customerID, accountRole, joinDate, depositAmount) " +
                " VALUES (?, ?, ?, CURRENT_DATE, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertAccountHolders)) {
            ps.setInt(1, accountID);
            ps.setInt(2, customerID);
            ps.setString(3, accountRole);
            ps.setDouble(4, depositAmt);

            ps.executeUpdate();
            System.out.println("Account " + accountID + " was successfully added into the AccountHolders table with CustomerID = " + customerID);
        }
    }

    /**
     * This method updates specific fields of an existing account.
     * Modification includes the interest rate and the account description.
     * @param conn         The active database connection.
     * @param accountID    The ID of the account to be modified.
     * @param interestRate The new interest rate to apply.
     * @param desc         The updated text description for the account.
     * @throws SQLException If a database access error occurs.
     */
    public static void updateAccount(Connection conn, int accountID, double interestRate, String desc) throws SQLException {
        String sql = "UPDATE Accounts SET interestRate = ?, accountDescription = ? WHERE accountID = " + accountID;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, interestRate);
            ps.setString(2, desc);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Successful Update On accountID = " + accountID);
            }
            else {
                System.out.println("ERROR: Unable to find accountID = " + accountID);
            }
        }
    }

    /**
     * Generates an account summary of all accounts in the system which are grouped by type.
     * Provides aggregate data including the total count of accounts per type,
     * total balance held, and the average balance for that category.
     * @param conn The active database connection.
     * @throws SQLException If a database access error occurs during aggregation.
     */
    public static void viewAccountInventory(Connection conn) throws SQLException {
        String sql = "SELECT accountType, " +
                "COUNT(accountID) AS total_count, " +
                "SUM(balance) AS total_balance, " +
                "AVG(balance) AS avg_balance " +
                "FROM Accounts " +
                "GROUP BY accountType " +
                "ORDER BY total_balance DESC";

        String row = "%-15s %-15s %-15s %-15s%n";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            System.out.printf(row, "AccountType", "total_count", "total_balance", "avg_balance");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String accountType = rs.getString("accountType");
                int count = rs.getInt("total_count");
                double sum = rs.getDouble("total_balance");
                double avg = rs.getDouble("avg_balance");

                System.out.printf(row, accountType, count, "$" + String.format("%.2f", sum), "$" + String.format("%.2f", avg));
            }
            if (!hasData) {
                System.out.println("ERROR: No Accounts found in the system");
            }
        }
    }

    /**
     * This method displays a detailed full list of accounts.
     * Admins and Customer have different functions here:
     * Admin View: Returns every account in the system ordered by ID.
     * Customer View: Returns only accounts where the logged-in user is an owner.
     * Also, includes a calculated 'ALERT' column to flag accounts that fall below their
     * required minimum balance.
     * </p>
     * @param conn     The active database connection.
     * @param userRole The role of the current session user.
     * @throws SQLException If a database access error occurs.
     */
    public static void viewAccounts(Connection conn, String userRole) throws SQLException {

        String sql = userRole.equalsIgnoreCase("admin")
                ? "SELECT * FROM Accounts ORDER BY accountID ASC"
                : "SELECT * FROM ACCOUNTS a JOIN AccountHolders ah ON a.accountID = ah.accountID WHERE ah.customerID = " + userID
                + " ORDER BY a.accountID ASC";

        String column = "%-15s ";
        String descriptionCol = "%-50s";
        String row = column + column + column + column + column + descriptionCol + column + column + column + column + "%n";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf(row, "accountID", "accountType", "branchID", "balance",
                    "interestRate", "accountDescription", "openDate", "minimumBalance", "isActive", "ALERTS");

            while (rs.next()) {
                String description = rs.getString("accountDescription");
                if (description == null) description = " ";
                description = description.replaceAll("\\s", " ");
                if (description.length() > 65) description = description.substring(0, 62) + "...";

                double balance = rs.getDouble("balance");
                double minBalance = rs.getDouble("minimumBalance");

                String alert = (balance < minBalance) ? " (LOW BALANCE ACCOUNT)" : "";

                System.out.printf(row, rs.getInt(1), rs.getString(2),
                        rs.getInt(3), rs.getDouble(4), rs.getDouble(5),
                        description, rs.getDate(7).toLocalDate(),
                        rs.getDouble("minimumBalance"), rs.getBoolean("isActive"), alert);

            }

        }
    }

    /**
     * This method retrieves a financial summary for a specific account using a PostgreSQL
     * stored function called getAccountSummary.
     * Displays current balance, total deposits, total withdrawals, and total number of transactions.
     * @param conn The active database connection.
     * @param accountID The ID of the account to do summary on.
     * @throws SQLException If a database access error occurs or the function does not exist.
     */
    public static void accountSummary(Connection conn, int accountID) throws SQLException {

        String sql = "SELECT * FROM getAccountSummary(?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                double totalOfDeposits = rs.getDouble("total_deposits");
                double totalOfWithdrawals = rs.getDouble("total_withdrawals");
                int transactionCount = rs.getInt("transactions_count");

                System.out.println();
                System.out.println("Account (" + accountID + ") Summary");
                System.out.println("Balance: " + balance);
                System.out.println("Total Number Of Transactions: " + transactionCount);
                System.out.println("Total Deposits: " + totalOfDeposits);
                System.out.println("Total Withdrawals: " + totalOfWithdrawals);
                System.out.println();
            } else {
                System.out.println("ERROR: Account #" + accountID + " not found");
            }
        }
    }

    /**
     * This method identifies accounts that have a balance below a specific threshold.
     * Utilizes the getLowBalanceAlerts database stored function to help filter records.
     * @param conn      The active database connection.
     * @param threshold The numeric balance threshold for triggering an alert.
     * @throws SQLException If a database access error occurs.
     */
    public static void lowBalanceAlert(Connection conn, double threshold) throws SQLException {

        String selectStatement = "SELECT * FROM getLowBalanceAlerts(?::NUMERIC)";
        try ( PreparedStatement ps = conn.prepareStatement(selectStatement)) {

            ps.setDouble(1, threshold);
            ResultSet rs = ps.executeQuery();

            boolean belowAccounts = false;
            while (rs.next()) {

                int accountID = rs.getInt("accountID");
                String accountType = rs.getString("accountType");
                double balance = rs.getDouble("balance");

                System.out.println("ALERT: Account: " + accountID + " [Account Type] " + accountType + " [Balance] $" + balance);
                belowAccounts = true;
            }

            if (!belowAccounts) {
                System.out.println("No Accounts fall under the threshold: " + threshold);
            }
        }
    }

    /**
     * This method is the customer module. Here users are checked if customer or admin
     * role and provided their individual menus for what they can perform in this module.
     * @param conn Database Connection
     * @param sc Scanner to get input from the user
     * @throws SQLException Throw any database error.
     */
    public static void customerModule(Connection conn, Scanner sc) throws SQLException {
        System.out.println("WELCOME TO CUSTOMER MODULE");

        if (userRole.equalsIgnoreCase("admin")) {
            System.out.println("1. List all customers with registration date");
            System.out.println("2. View a customer's full account history");
            System.out.println("3. View a customer's transaction history across all accounts");
            System.out.println("4. View a customer's review history");
            System.out.println("5. Add new customer");
        } else {
            System.out.println("1. Register as a new customer");
            System.out.println("2. Update personal information (phone, email)");
            System.out.println("3. View personal profile");
            System.out.println("4. Deactivate own account");
        }

        System.out.println("0. Return");

        int choice = sc.nextInt();
        sc.nextLine();
        if (choice == 0) return;
        if (userRole.equalsIgnoreCase("admin")) {
            switch (choice) {

                case 1 -> {
                    System.out.println("Please enter the registration date (YYYY-MM-DD) to search: ");

                    String date = sc.nextLine();

                    String dateSQL = "SELECT customerID, firstName, lastName, email, registrationDate " +
                            "FROM Customers " +
                            "WHERE registrationDate = CAST(? AS DATE) " +
                            "ORDER BY CustomerID ASC";
                    String rowD = "%-18s %-18s %-18s %-25s %-25s%n";

                    try (PreparedStatement ps = conn.prepareStatement(dateSQL)) {
                        ps.setString(1, date);
                        ResultSet rs = ps.executeQuery();

                        boolean found = false;
                        System.out.printf(rowD, "customerID", "firstName", "lastName", "email", "registrationDate");
                        while (rs.next()) {
                            found = true;
                            System.out.printf(rowD, rs.getInt(1), rs.getString(2),
                                    rs.getString(3), rs.getString(4), rs.getDate(5).toLocalDate());
                        }
                        if (!found) {
                            System.out.println("ERROR: No Customer registered on " + date);
                        }
                    }
                }
                case 2 -> {
                    System.out.println("Please enter CustomerID for search: ");
                    int customerID = sc.nextInt();

                    String rowA = "%-18s %-18s %-18s %-18s %-18s%n";

                    System.out.println("CUSTOMER " + customerID + " FULL REPORT");
                    System.out.println();

                    //Account History
                    System.out.println("FULL ACCOUNT HISTORY");
                    String accountSQL = "SELECT a.accountID, a.accountType, a.balance, ah.accountRole " +
                            "FROM Accounts a " +
                            "JOIN AccountHolders ah ON ah.accountID = a.accountID " +
                            "WHERE ah.customerID = ?";


                    try (PreparedStatement ps = conn.prepareStatement(accountSQL)) {
                        ps.setInt(1, customerID);
                        ResultSet rs = ps.executeQuery();

                        System.out.printf(rowA, "AccountID", "AccountType", "Balance", "AccountRole");
                        while (rs.next()) {
                            System.out.printf(rowA, rs.getInt(1), rs.getString(2),
                                    rs.getDouble(3), rs.getString(4));
                        }
                    }
                }
                case 3 -> {
                    System.out.println("Please enter CustomerID for search: ");
                    int customerID = sc.nextInt();

                    String rowT = "%-18s %-18s %-18s %-18s %-18s%n";

                    System.out.println("FULL TRANSACTION HISTORY");

                    String transactionsSQL = "SELECT t.transactionDate, t.amount, t.transactionType, t.accountID " +
                            "FROM Transactions t " +
                            "JOIN AccountHolders ah ON t.accountID = ah.accountID " +
                            "WHERE ah.customerID = ? " +
                            "ORDER BY t.transactionDate DESC";

                    try (PreparedStatement ps = conn.prepareStatement(transactionsSQL)) {
                        ps.setInt(1, customerID);
                        ResultSet rs = ps.executeQuery();

                        System.out.printf(rowT, "transactionDate", "amount", "transactionType", "accountID");
                        while (rs.next()) {
                            System.out.printf(rowT, rs.getDate(1).toLocalDate(), rs.getDouble(2),
                                    rs.getString(3), rs.getInt(4));
                        }
                    }
                }
                case 4 -> {
                    System.out.println("Please enter CustomerID for search: ");
                    int customerID = sc.nextInt();

                    System.out.println("FULL REVIEW HISTORY");

                    String reviewsSQL = "SELECT * FROM AccountReviews WHERE customerID = ?";

                    String reviewRow = "%-10s %-12s %-12s %-8s %-15s %-10s%n";

                    try (PreparedStatement ps = conn.prepareStatement(reviewsSQL)) {
                        ps.setInt(1, customerID);
                        ResultSet rs = ps.executeQuery();


                        System.out.printf(reviewRow, "reviewID", "customerID", "accountID", "rating", "reviewDate", "isVerified");
                        while (rs.next()) {

                            System.out.printf(reviewRow, rs.getInt("reviewID"), rs.getInt("customerID"),
                                    rs.getInt("accountID"),
                                    rs.getInt("rating"), rs.getDate("reviewDate").toLocalDate(), rs.getBoolean("isVerified"));

                            String reviewText = rs.getString("reviewText");
                            if (reviewText == null || reviewText.trim().isEmpty()) {
                                reviewText = "No Text Review";
                            }
                            System.out.print("Review Text: " + reviewText);
                            System.out.println();
                        }
                    }
                }

                case 5 -> addCustomer(conn, sc);

                default -> System.out.println("Invalid Choice");
            }
        } else {
            switch (choice) {

                case 1 -> addCustomer(conn, sc);
                case 2 -> {
                    System.out.println("What is the CustomerID?");
                    int customerID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Phone Number --> (###-###-####):  ");
                    String phoneNumber = sc.nextLine();
                    System.out.println("Email:  ");
                    String email = sc.nextLine();

                    String sql = "UPDATE Customers SET phone = ?, email = ? WHERE customerID = " + customerID;
                    try(PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, phoneNumber);
                        ps.setString(2, email);
                        ps.executeUpdate();

                        System.out.println("Customer " + customerID + " updated phone number " + phoneNumber + " and email " + email);
                    } catch (SQLException e) {
                        System.out.println("ERROR: Customer " + customerID + " unable to update");
                        System.out.println("ERROR: " + e.getMessage());
                    }
                }
                case 3 -> viewPersonalCustomerInfo(conn);
                case 4 -> {
                    System.out.println("What is the AccountID?");
                    int accountID = sc.nextInt();
                    sc.nextLine();
                    if (hasAccess(conn, accountID)) {
                       String sql = "SELECT isActive FROM Accounts WHERE accountID = ?";

                       try (PreparedStatement ps = conn.prepareStatement(sql)) {
                           ps.setInt(1, accountID);
                           ResultSet rs = ps.executeQuery();

                           if (rs.next()) {
                               boolean currentActive = rs.getBoolean("isActive");

                               if (!currentActive) {
                                   System.out.println("Account #" + accountID + " is already inactive. Please speak with representative to reactivate.");
                               } else {
                                   String updateSQL = "UPDATE Accounts SET isActive = FALSE WHERE accountID = ?";
                                   try (PreparedStatement updatePS = conn.prepareStatement(updateSQL)) {
                                       updatePS.setInt(1, accountID);
                                       ps.executeUpdate();

                                       System.out.println("Account #" + accountID + " is set to inactive!");
                                       System.out.println("Please speak to a representative if you wish to reactivate your account.");
                                   }
                               }
                           } else {
                               System.out.println("ERROR: Account #" + accountID + " not found");
                           }
                       }
                    } else {
                        System.out.println("ERROR: ACCESS DENIED");
                    }
                }

                default -> System.out.println("Invalid Choice");
            }
        }
    }

    /**
     * This method registers a new customer into the banking app and database.
     * This method asks the user for personal information in the console, generates a unique
     * customerID using the helper method generateCustomerID(Connection),
     * and inserts the record into the database with an active status and the
     * current date as the registration date.
     * @param conn The active database connection.
     * @param sc   Scanner object used to capture user input for name, email, phone number, and DOB.
     * @throws SQLException If a database access error occurs or the date format is invalid for casting.
     */
    public static void addCustomer(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Registering New Customer");
        sc.nextLine();
        System.out.println("First Name: ");
        String firstName = sc.nextLine();
        System.out.println("Last Name: ");
        String lastName = sc.nextLine();
        System.out.println("Email:  ");
        String email = sc.nextLine();
        System.out.println("Phone Number --> (###-###-####):  ");
        String phoneNumber = sc.nextLine();
        System.out.println("Date Of Birth --> (YYYY-MM-DD):  ");
        String DOB = sc.nextLine();

        int customerID = generateCustomerID(conn);

        String insertCustomer = """
                INSERT INTO Customers (customerID, firstName, lastName, email, phone,
                dateOfBirth, registrationDate, isActive) VALUES (?, ?, ?, ?, ?, ?::DATE, CURRENT_DATE, TRUE)""";

        PreparedStatement ps = conn.prepareStatement(insertCustomer);

        ps.setInt(1, customerID);
        ps.setString(2, firstName);
        ps.setString(3, lastName);
        ps.setString(4, email);
        ps.setString(5, phoneNumber);
        ps.setString(6, DOB);
        ps.executeUpdate();

        System.out.println("Customer " + firstName + " " + lastName + " was successfully added with CustomerID: " + customerID);
    }

    /**
     * This method retrieves and displays the full profile details for the currently logged-in customer.
     * This method uses the session's userID to filter the Customers table,
     * ensuring users can only view their own personal information. The output is
     * formatted as a row containing first name, last name, contact info, date of birth, registration date, and account status.
     * @param conn The active database connection.
     * @throws SQLException If a database access error occurs or session data is invalid.
     */
    public static void viewPersonalCustomerInfo(Connection conn) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE customerID = " + userID
                + " ORDER BY customerID ASC";

        String column = "%-18s ";
        String emailCol = "%-25s";
        String row = column + column + column + emailCol + column + column + column + column + "%n";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf(row, "customerID", "firstName", "lastName", "email",
                    "phone", "dateOfBirth", "registrationDate", "isActive");

            while (rs.next()) {
                System.out.printf(row, rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getDate(6).toLocalDate(), rs.getDate(7).toLocalDate(), rs.getBoolean(8));

            }
        }
    }

    /**
     * This method is the transaction module. Here users are checked if customer or admin
     * role and provided their individual menus for what they can perform in this module.
     * @param conn Database Connection
     * @param sc Scanner to get input from the user
     * @throws SQLException Throw any database error.
     */
    public static void transactionModule(Connection conn, Scanner sc) throws SQLException {

        System.out.println("WELCOME TO TRANSACTION MODULE");
        while (true) {

            if (userRole.equalsIgnoreCase("admin")) {
                transactionAdminManagement(conn, sc);
            } else {
                System.out.println("1. Deposit / Withdrawal");
                System.out.println("2. Transfer");
                System.out.println("0. Return");
            }

            int choice = sc.nextInt();
            sc.nextLine();
            if (choice == 0) return;


            if (choice == 1) {
                System.out.print("Account ID: ");
                int id = sc.nextInt();
                System.out.print("Amount: ");
                double amt = sc.nextDouble();
                System.out.print("Type (Deposit/Withdrawal): ");
                String type = sc.next();
                performTransaction(conn, id, amt, type);
            }
            else if (choice == 2) {
                System.out.print("From Account: ");
                int from = sc.nextInt();
                System.out.print("To Account: ");
                int to = sc.nextInt();
                System.out.print("Amount: ");
                double amt = sc.nextDouble();
                transferMoney(conn, from, to, amt);
            } else {
                System.out.println("Invalid Choice");
                return;
            }
        }
    }

    /**
     * This method provides the admin interface for managing and auditing bank transactions.
     * This module includes features for:
     * Viewing transaction history for specific accounts with optional filters (Date/Type).
     * Aggregating financial data by payment method.
     * Deleting a transaction and restoring the
     * original balance using a manual SQL transaction block.
     * Handling deposits, withdrawals, and transfers on behalf of customers.
     * @param conn The active database connection.
     * @param sc   Scanner object for capturing menu choices and transaction details.
     * @throws SQLException If a database access error occurs or a transaction rollback fails.
     */
    public static void transactionAdminManagement(Connection conn, Scanner sc) throws SQLException {
        System.out.println("1. View all transactions for a given account");
        System.out.println("2. View transaction history filtered by date range or type");
        System.out.println("3. View total amounts grouped by payment method");
        System.out.println("4. Cancel/reverse a transaction (restore balance) — admin only");
        System.out.println("5. Deposit / Withdrawal for customer");
        System.out.println("6. Transfer for customer");
        System.out.println("0. Return");
        int userChoice = sc.nextInt();
        sc.nextLine();

        switch (userChoice) {
            case 1 -> {
                System.out.println("Enter an AccountID to view transaction: ");
                int accountID = sc.nextInt();

                String sql = "SELECT * FROM Transactions WHERE accountID = ?";
                String row = ("%-18s %-18s %-18s %-18s %-18s %-18s %-18s%n");
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, accountID);
                    ResultSet rs = ps.executeQuery();
                    System.out.printf(row, "transactionID", "accountID", "transactionDate", "amount",
                            "transactionType", "recipient", "paymentMethod");

                    while (rs.next()) {
                        System.out.printf(row, rs.getInt(1), rs.getInt(2), rs.getDate(3).toLocalDate(),
                                rs.getDouble(4), rs.getString(5), rs.getString(6), rs.getString(7));
                    }
                }
            }

            case 2 -> {
                System.out.println();
                System.out.println("Enter an AccountID to view transaction: ");
                int accountID = sc.nextInt();

                System.out.println("Filter by: ");
                System.out.println("1. Date Range");
                System.out.println("2. Transaction Type");
                int choice = sc.nextInt();
                sc.nextLine();

                String sql = "SELECT * FROM Transactions WHERE accountID = ?";
                if (choice == 1) sql += " AND transactionDate BETWEEN CAST(? AS DATE) AND CAST(? AS DATE)";
                if (choice == 2) sql += " AND LOWER(transactionType) = LOWER(?)";
                sql += " ORDER BY transactionDate DESC";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, accountID);
                    if (choice == 1) {
                        System.out.println("Start Date (YYYY-MM-DD): ");
                        String startDate = sc.nextLine();
                        ps.setString(2, startDate);

                        System.out.println("Stop Date (YYYY-MM-DD): ");
                        String stopDate = sc.nextLine();
                        ps.setString(3, stopDate);
                    }
                    else if (choice == 2) {
                        System.out.println("Enter transaction type (Deposit, Withdrawal, Transfer, Payment): ");
                        String transactionType = sc.nextLine();
                        ps.setString(2, transactionType);
                    }

                    ResultSet rs = ps.executeQuery();
                    String row = ("%-18s %-18s %-18s %-18s %-18s %-18s %-18s%n");
                    System.out.printf(row, "transactionID", "accountID", "transactionDate", "amount",
                            "transactionType", "recipient", "paymentMethod");
                    while (rs.next()) {
                        System.out.printf(row, rs.getInt(1), rs.getInt(2), rs.getDate(3).toLocalDate(),
                                rs.getDouble(4), rs.getString(5), rs.getString(6), rs.getString(7));
                    }
                }
            }

            case 3 -> {
                String sql = "SELECT paymentMethod, SUM(amount) AS total_amount, COUNT(*) as count" +
                        " FROM Transactions" +
                        " GROUP BY paymentMethod";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ResultSet rs = ps.executeQuery();
                    String row = "%-15s %-15s %-15s%n";
                    System.out.printf(row, "paymentMethod", "total_amount", "count");

                    while (rs.next()) {
                        System.out.printf(row, rs.getString(1), rs.getDouble(2), rs.getInt(3));
                    }
                }
            }

            case 4 -> {
                System.out.println("Enter TransactionID: ");
                int transactionID = sc.nextInt();

                String findSql = "SELECT accountID, amount, transactionType FROM Transactions WHERE transactionID = ?";
                try {
                    conn.setAutoCommit(false);

                    int accountID = 0;
                    double amount = 0;
                    try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                        ps.setInt(1, transactionID);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            accountID = rs.getInt("accountID");
                            amount = rs.getDouble("amount");
                        } else {
                            System.out.println("ERROR: Transaction " + transactionID + " not found");
                        }
                    }

                    String updateBalance = "UPDATE Accounts SET balance = balance - ? WHERE accountID = ?";
                    try (PreparedStatement ps = conn.prepareStatement(updateBalance)) {
                        ps.setDouble(1, amount);
                        ps.setInt(2, accountID);
                        ps.executeUpdate();
                    }

                    String deleteTransaction = "DELETE FROM Transactions WHERE transactionID = ?";
                    try (PreparedStatement ps = conn.prepareStatement(deleteTransaction)) {
                        ps.setInt(1, transactionID);
                        ps.executeUpdate();
                    }

                    conn.commit();
                    System.out.println("Transaction #" + transactionID + " has been deleted and balanced recovered");
                } catch (SQLException e) {
                    conn.rollback();
                    System.out.println("ERROR: Reverse Transaction Failed " + e.getMessage());
                }
                conn.setAutoCommit(true);
            }

            case 5 -> {
                System.out.print("Account ID: ");
                int id = sc.nextInt();
                System.out.print("Amount: ");
                double amt = sc.nextDouble();
                System.out.print("Type (Deposit/Withdrawal): ");
                String type = sc.next();
                performTransaction(conn, id, amt, type);
            }

            case 6 -> {
                System.out.print("From Account: ");
                int from = sc.nextInt();
                System.out.print("To Account: ");
                int to = sc.nextInt();
                System.out.print("Amount: ");
                double amt = sc.nextDouble();
                transferMoney(conn, from, to, amt);
            }

            case 0 -> {
                System.out.println("Exiting this module...returning to main screen");
                mainMenu(conn);
            }

            default -> System.out.println("Invalid choice");
        }
    }

    /**
     * This method executes Deposit or Withdrawal for an account.
     * This method validates funds using the database stored function checkSufficientBalance
     * before calling the performTransaction stored procedure. Also, handles
     * automatic ID generation and returns the updated balance to the console.
     * @param conn The active database connection.
     * @param accountID The ID of the account performing the transaction.
     * @param amount The monetary value of the transaction.
     * @param type The type of transaction Deposit Withdrawal
     * @throws SQLException If the stored procedure fails or the account is invalid.
     */
    public static void performTransaction(Connection conn, int accountID, double amount, String type) throws SQLException {

        int transactionID = 0;
        String transSql = "SELECT COALESCE(MAX(transactionID), 0) + 1 FROM Transactions";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(transSql);
            if (rs.next()) transactionID = rs.getInt(1);
        }

        String checkBalance = "SELECT checkSufficientBalance(?, ?::NUMERIC)";
        try (PreparedStatement ps = conn.prepareStatement(checkBalance)) {
            ps.setInt(1, accountID);
            ps.setDouble(2, amount);

            ResultSet rs = ps.executeQuery();

            if (rs.next() && !rs.getBoolean(1) && !type.equalsIgnoreCase("deposit")) {
                System.out.println("ERROR: Account #" + accountID + " has insufficient funds Available balance: $" + getBalance(conn, accountID));
                return;
            }
        }

        String sql = "SELECT performTransaction(?, ?, ?::NUMERIC, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionID);
            ps.setInt(2, accountID);
            ps.setDouble(3, amount);
            ps.setString(4, type);
            ps.setString(5, "Self");
            ps.setString(6, "Branch");

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double transBalance = rs.getDouble(1);
                System.out.println(type + " transaction complete.. NEW BALANCE: $" + transBalance);
            } else {
                System.out.println("ERROR: TRANSACTION FAILED");
            }

        }
    }

    /**
     * This method executes a secure funds transfer between two internal accounts.
     * To ensure data integrity, this method uses ACID principles:
     * 1. Disables autoCommit
     * 2. Deducts funds from the source account.
     * 3. Adds funds to the destination account.
     * 4. Commits both actions simultaneously.
     * If any step fails, the entire operation is rolled back to prevent money loss.
     * @param conn The active database connection.
     * @param fromAcc The source account ID.
     * @param toAcc The destination account ID.
     * @param amount The amount of money to transfer.
     * @throws SQLException If a database error occurs or insufficient funds are available.
     */
    public static void transferMoney(Connection conn, int fromAcc, int toAcc, double amount) throws SQLException {

        double fromBalance = getBalance(conn, fromAcc);

        if (fromBalance < 0) {
            System.out.println("Error: Account Not Found");
            return;
        }
        if (fromBalance < amount) {
            System.out.println("Error: Transfer Failed Insufficient Funds");
            return;
        }

        try {
            conn.setAutoCommit(false);

            performTransaction(conn, fromAcc, amount, "Transfer");
            performTransaction(conn, toAcc, amount, "Internal Transfer");

            conn.commit();
            System.out.println("SUCCESSFUL TRANSFER: Account " + fromAcc + " transferred " + "$" + amount + " to Account: " + toAcc);

        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Error: Transaction Failed " + e.getMessage());
        }

        conn.setAutoCommit(true);
    }

    /**
     * This method is the review module. Here users are checked if customer or admin
     * role and provided their individual menus for what they can perform in this module.
     * @param conn Database Connection
     * @param sc Scanner to get input from the user
     * @throws SQLException Throw any database error.
     */
    private static void reviewModule(Connection conn, Scanner sc) throws SQLException {

        System.out.println("WELCOME TO REVIEW MODULE");

        while (true) {
            if (userRole.equalsIgnoreCase("admin")) {
                System.out.println("1. Verify or unverify a review");
                System.out.println("2. View all reviews for a specific account");
                System.out.println("3. View average rating per account type");
            } else {
                System.out.println("1. Write a review");
                System.out.println("2. View my reviews");
            }
            System.out.println("0. Return");
            int choice = sc.nextInt();
            sc.nextLine();
            if (choice == 0) return;

            if (userRole.equalsIgnoreCase("admin")) {

                switch (choice) {
                    case 1 -> {
                        System.out.println("What ReviewID to verify?");
                        int reviewID = sc.nextInt();
                        sc.nextLine();
                        updateReviewVerification(conn, reviewID, sc);
                    }

                    case 2 -> viewReviews(conn, userRole);

                    case 3 -> viewAverageRatings(conn);

                    default -> System.out.println("Invalid Choice");
                }

            } else {
                switch (choice) {
                    case 1 -> writeReview(conn, sc);
                    case 2 -> viewReviews(conn, userRole);

                    default -> System.out.println("Invalid Choice");
                }
            }
        }
    }

    /**
     * This method generates an analytical report of average customer ratings grouped by account type.
     * This method joins the Accounts and AccountReviews tables to calculate
     * the average rating rounded to two decimal places and the total number of reviews
     * for each account category (Checking, Savings, etc.).
     * @param conn The active database connection.
     * @throws SQLException If a database access error occurs during the aggregate query.
     */
    public static void viewAverageRatings(Connection conn) throws SQLException {
        String sql = "SELECT a.accountType, ROUND(AVG(ar.rating), 2) AS average_rating, " +
                "COUNT(ar.reviewID) AS total_reviews " +
                "FROM Accounts a " +
                "JOIN AccountReviews ar ON a.accountID = ar.accountID " +
                "GROUP BY a.accountType " +
                "ORDER BY average_rating DESC";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            String row = "%-15s %-15s %-15s%n";
            System.out.printf(row, "accountType", "average_rating", "total_reviews");
            boolean foundReviews = false;
            while (rs.next()) {
                foundReviews = true;
                System.out.printf(row, rs.getString("accountType"),
                        rs.getDouble("average_rating"), rs.getInt("total_reviews"));
            }
            if (!foundReviews) System.out.println("ERROR: No reviews in system");
        }
    }

    /**
     * This method updates the verification status of a specific account review.
     * This method is an Admin only menu option used to flag reviews as "Verified" (true) or
     * "Unverified" (false) to maintain data integrity.
     * @param conn The active database connection.
     * @param reviewID The unique identifier ID of the review to be updated.
     * @param sc Scanner object to capture the boolean verification status.
     * @throws SQLException If a database access error occurs or the reviewID does not exist.
     */
    public static void updateReviewVerification(Connection conn, int reviewID, Scanner sc) throws SQLException {
        String sql = "UPDATE AccountReviews SET isVerified = ? WHERE reviewID = " + reviewID;


        System.out.println("Set verified / unverified (true/false)");
        boolean isVerified = sc.nextBoolean();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isVerified);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Successful Update On reviewID = " + reviewID);
            }
            else {
                System.out.println("ERROR: Unable to find reviewID = " + reviewID);
            }
        }
    }

    /**
     * This method helps the user write and submit a review.
     * Security Check: Before proceeding, it calls hasAccess(Connection, int)
     * to ensure the customer actually owns the account they are reviewing.
     * The review is initialized with the current date and a default isVerified
     * status of false which the admin can later verify.
     * @param conn The active database connection.
     * @param sc Scanner object to collect the AccountID, rating (1-5), and review text.
     * @throws SQLException If a database access error occurs or the customer violates
     * the foreign key constraints.
     */
    private static void writeReview(Connection conn, Scanner sc) throws SQLException {
        System.out.println("Enter AccountID for the account you want to leave a review for: ");
        int accountID = sc.nextInt();
        sc.nextLine();

        if (!hasAccess(conn, accountID)) {
            System.out.println("ERROR: Access Denied");
            return;
        }

        System.out.println("Rating (1-5): ");
        int rating = sc.nextInt();
        sc.nextLine();
        System.out.println("Write your review now: ");
        String reviewText = sc.nextLine();

        String insert = "INSERT INTO AccountReviews (reviewID, customerID, accountID, rating, reviewText, reviewDate, isVerified) " +
                "VALUES (?, ?, ?, ?, ?, CURRENT_DATE, false)";
        int reviewID = generateReviewID(conn);
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, reviewID);
            ps.setInt(2, userID);
            ps.setInt(3, accountID);
            ps.setInt(4, rating);
            ps.setString(5, reviewText);

            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Your review has been posted! Awaiting verification...");
        }
    }

    /**
     * This method displays a list of reviews based on the current user's permissions.
     * Admin features includes a prompt for an AccountID and displays all reviews associated
     * with that specific account.
     * Customer features an automatic filter by the session's userID to
     * show only reviews the customer has personally written.
     * @param conn The active database connection.
     * @param userRole The role of the current user ADMIN or CUSTOMER
     * @throws SQLException If a database access error occurs.
     */
    public static void viewReviews(Connection conn, String userRole) throws SQLException {

        String sql;
        int findID;
        Scanner sc = new Scanner(System.in);

        if (userRole.equalsIgnoreCase("admin")) {
            System.out.println("Enter the AccountID to view reviews: ");
            findID = sc.nextInt();
            sql = "SELECT * FROM AccountReviews WHERE accountID = ? ORDER BY reviewDate DESC";
        } else {
            findID = userID;
            sql = "SELECT * FROM AccountReviews WHERE customerID = ? ORDER BY reviewDate DESC";
        }

        String row = "%-15s %-15s %-15s %-15s %-15s %-15s%n";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, findID);
            ResultSet rs = ps.executeQuery();

            System.out.printf(row, "reviewID", "customerID", "accountID", "rating", "reviewDate", "isVerified");

            while (rs.next()) {

                System.out.printf(row, rs.getInt("reviewID"), rs.getInt("customerID"),
                        rs.getInt("accountID"), rs.getInt("rating"),
                        rs.getDate("reviewDate").toLocalDate(),
                         rs.getBoolean("isVerified"));

                String reviewText = rs.getString("reviewText");
                if (reviewText == null || reviewText.trim().isEmpty()) reviewText = "(No Review Text)";
                System.out.println("Review: " + reviewText);
                System.out.println();
            }
            System.out.println();
        }
    }


    public static void main(String[] args) {
        try {
            Connection conn = getConnection();

            if (conn != null) {
                createTables(conn);
               // insertSampleData(conn); DELETED EXPLAINED IN DOCUMENTATION
                // TODO: Uncomment when ready
                Scanner sc = new Scanner(System.in);
                if (login(conn, sc)) {
                    mainMenu(conn);
                }
                else {
                    System.out.println("Disconnected From Bank App");
                }
                conn.close();
            }
        } catch (Exception e) {
            System.out.println("ERROR: UNABLE TO CONNECT TO APP " + e.getMessage());
        }
    }
}

