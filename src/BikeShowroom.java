import java.sql.*;
import java.util.Scanner;

public class BikeShowroom {
    private static final String URL = "jdbc:mysql://localhost:3306/showroomDB";
    private static final String USER = "root";
    private static final String PASSWORD = "svc9090"; // change this to your MySQL password

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connected to MySQL database!");

            int choice;
            do {
                System.out.println("\n|--------- BIKE SHOWROOM MENU --------|");
                System.out.println("1. Insert New Bike");
                System.out.println("2. View All Bikes");
                System.out.println("3. View Bike by ID");
                System.out.println("4. Update Bike by ID");
                System.out.println("5. Delete Bike by ID");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> insertBike(connection, scanner);
                    case 2 -> readAllBikes(connection);
                    case 3 -> {
                        System.out.print("Enter Bike ID: ");
                        int id = scanner.nextInt();
                        readBikeById(connection, id);
                    }
                    case 4 -> {
                        System.out.print("Enter Bike ID to Update: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        updateBikeById(connection, scanner, id);
                    }
                    case 5 -> {
                        System.out.print("Enter Bike ID to Delete: ");
                        int id = scanner.nextInt();
                        deleteBikeById(connection, id);
                    }
                    case 6 -> System.out.println("--> Exitred!!");
                    default -> System.out.println("--> Invalid choice.");
                }

            } while (choice != 6);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertBike(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Bike ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Model: ");
        String model = scanner.nextLine();

        System.out.print("Enter Company: ");
        String company = scanner.nextLine();

        System.out.print("Enter Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter Color: ");
        String color = scanner.nextLine();

        System.out.print("Enter Engine Capacity (cc): ");
        int engine = scanner.nextInt();

        System.out.print("Enter Stock: ");
        int stock = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Mode (Petrol/Electric/CNG): ");
        String mode = scanner.nextLine();

        String sql = "INSERT INTO bikes (bike_id, model, company, price, color, engine_capacity, stock, mode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, model);
            pstmt.setString(3, company);
            pstmt.setDouble(4, price);
            pstmt.setString(5, color);
            pstmt.setInt(6, engine);
            pstmt.setInt(7, stock);
            pstmt.setString(8, mode);
            pstmt.executeUpdate();
            System.out.println("--> Bike inserted successfully!");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("--> Error: Bike ID already exists.");
        }
    }

    private static void readAllBikes(Connection connection) throws SQLException {
        String query = "SELECT * FROM bikes";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n----- Bike Inventory -----");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("bike_id") +
                        ", Model: " + rs.getString("model") +
                        ", Company: " + rs.getString("company") +
                        ", Price: ₹" + rs.getDouble("price") +
                        ", Color: " + rs.getString("color") +
                        ", Engine: " + rs.getInt("engine_capacity") + "cc" +
                        ", Stock: " + rs.getInt("stock") +
                        ", Mode: " + rs.getString("mode"));
            }
        }
    }

    private static void readBikeById(Connection connection, int id) throws SQLException {
        String query = "SELECT * FROM bikes WHERE bike_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("\nBike Details:");
                System.out.println("ID: " + rs.getInt("bike_id"));
                System.out.println("Model: " + rs.getString("model"));
                System.out.println("Company: " + rs.getString("company"));
                System.out.println("Price: ₹" + rs.getDouble("price"));
                System.out.println("Color: " + rs.getString("color"));
                System.out.println("Engine: " + rs.getInt("engine_capacity") + "cc");
                System.out.println("Stock: " + rs.getInt("stock"));
                System.out.println("Mode: " + rs.getString("mode"));
            } else {
                System.out.println("--> Bike not found.");
            }
        }
    }

    private static void updateBikeById(Connection connection, Scanner scanner, int id) throws SQLException {
        System.out.print("Enter New Price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter New Stock: ");
        int stock = scanner.nextInt();
        scanner.nextLine(); // consume newline

        String updateSQL = "UPDATE bikes SET price = ?, stock = ? WHERE bike_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setDouble(1, price);
            pstmt.setInt(2, stock);
            pstmt.setInt(3, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("--> Bike updated successfully!");
            } else {
                System.out.println("--> Bike not found.");
            }
        }
    }

    private static void deleteBikeById(Connection connection, int id) throws SQLException {
        String deleteSQL = "DELETE FROM bikes WHERE bike_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("--> Bike deleted successfully!");
            } else {
                System.out.println("--> Bike not found.");
            }
        }
    }
}
