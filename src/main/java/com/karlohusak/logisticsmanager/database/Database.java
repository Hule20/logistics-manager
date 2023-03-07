package com.karlohusak.logisticsmanager.database;

import com.karlohusak.logisticsmanager.entities.*;
import com.karlohusak.logisticsmanager.entities.Driver;
import com.karlohusak.logisticsmanager.exceptions.DatabaseException;
import javafx.scene.chart.PieChart;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database {

    public static Connection connectToDatabase() throws SQLException, IOException {

        Properties configuration = new Properties();
        configuration.load(new FileReader("dat/database.properties"));

        String databaseURL = configuration.getProperty("databaseUrl");
        String databaseUsername = configuration.getProperty("username");
        String databasePassword = configuration.getProperty("password");

        Connection connection = DriverManager
                .getConnection(databaseURL, databaseUsername, databasePassword);

        return connection;
    }

    //"Role" entity
    public static String getRole(String username)throws DatabaseException {
        String role = null;
        try (Connection connection = connectToDatabase()) {
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery("SELECT privilege FROM role WHERE username=" + "'" + username+ "'");
            while (resultSet.next()) {
                role = resultSet.getString("privilege");
            }
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while getting a user role!";
            throw new DatabaseException(errMsg, ex);
        }

        return role;
    }


    //"Driver" entity
    public static synchronized List<Driver> getAllDrivers() throws DatabaseException {

        List<Driver> driversList = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {

            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM DRIVER");

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String phone = resultSet.getString("phone_number");
                Long vehicle_id = resultSet.getLong("vehicle_id");
                Vehicle vehicle = getVehicleById(vehicle_id);
                boolean hasShipment = resultSet.getBoolean("has_shipment");

                Driver nextDriver = new Driver(id, firstName, lastName, phone, vehicle, hasShipment);

                driversList.add(nextDriver);
            }
        } catch (SQLException | IOException ex) {
            String errMsg =
                    "Error while fetching drivers list!";
            throw new DatabaseException(errMsg, ex);
        }

        return driversList;
    }

    public static synchronized void addNewDriver(Driver driver) throws DatabaseException {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO DRIVER(first_name, last_name, phone_number, vehicle_id) VALUES(?, ?, ?, ?)");

            preparedStatement.setString(1, driver.getName());
            preparedStatement.setString(2, driver.getLastname());
            preparedStatement.setString(3, driver.getPhoneNumber());
            preparedStatement.setLong(4, driver.getVehicle().getId());

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while adding a new driver!";
            throw new DatabaseException(errMsg, ex);
        }
    }

    public static synchronized void deleteDriver(Driver driver) throws DatabaseException{
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM driver WHERE id =" + driver.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while adding a new client!";
            throw new DatabaseException(errMsg, ex);
        }
    }

    public static void updateDriver(Driver driver, String firstName, String lastName,
                                    String phoneNumber, Long vehicleId) throws DatabaseException{
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE driver SET first_name = ?, last_name = ?, phone_number = ?, vehicle_id = ? WHERE id =" + driver.getId());

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.setLong(4, vehicleId);

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while adding a new client!";
            throw new DatabaseException(errMsg, ex);
        }
    }



    //"Vehicle" entity
    public static List<Vehicle> getAllVehicles() throws DatabaseException {
        List<Vehicle> vehicleList = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM VEHICLE");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String model = resultSet.getString("model");
                BigDecimal maxLoad = resultSet.getBigDecimal("max_load");
                String vim = resultSet.getString("vin");

                Vehicle nextVehicle = new Vehicle(id, model, maxLoad, vim);
                vehicleList.add(nextVehicle);
            }

        } catch (SQLException | IOException ex) {
            String errMsg = "Error while fetching all vehicles list!";
            throw new DatabaseException(errMsg, ex);
        }
        return vehicleList;
    }

    public static List<Vehicle> getAvailableVehicles() throws DatabaseException {
        List<Vehicle> vehicleList = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT *\n" +
                            "FROM vehicle\n" +
                            "WHERE id NOT IN (SELECT vehicle_id FROM driver)");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String model = resultSet.getString("model");
                BigDecimal maxLoad = resultSet.getBigDecimal("max_load");
                String vim = resultSet.getString("vin");

                Vehicle nextVehicle = new Vehicle(id, model, maxLoad, vim);
                vehicleList.add(nextVehicle);
            }

        } catch (SQLException | IOException ex) {
            String errMsg = "Error while fetching available vehicle list!";
            throw new DatabaseException(errMsg, ex);
        }
        return vehicleList;
    }

    public static Vehicle getVehicleById(Long vehicle_id) throws DatabaseException {

        Vehicle vehicle = null;
        try (Connection connection = connectToDatabase()) {
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery("SELECT * FROM VEHICLE WHERE id=" + vehicle_id);
            while (resultSet.next()) {
                vehicle = new Vehicle(
                        resultSet.getLong("id"),
                        resultSet.getString("model"),
                        resultSet.getBigDecimal("max_load"),
                        resultSet.getString("vin")
                );
            }
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while getting a vehicle by ID!";
            throw new DatabaseException(errMsg, ex);
        }

        return vehicle;
    }

    public static synchronized void addNewVehicle(Vehicle vehicle) throws DatabaseException {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO VEHICLE(model, max_load, vin) VALUES(?, ?, ?)");

            preparedStatement.setString(1, vehicle.getModel());
            preparedStatement.setBigDecimal(2, vehicle.getMaxLoad());
            preparedStatement.setString(3, vehicle.getVIN());

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while adding a new client!";
            throw new DatabaseException(errMsg, ex);
        }
    }

    public static synchronized void deleteVehicle(Vehicle vehicle) throws DatabaseException{
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM vehicle WHERE id =" + vehicle.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while deleting vehicle!";
            throw new DatabaseException(errMsg, ex);
        }
    }

    public static void updateVehicle(Vehicle vehicle, String model, String vin,
                                    String mass) throws DatabaseException{
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE vehicle SET model = ?, max_load = ?, vin = ? WHERE id =" + vehicle.getId());

            preparedStatement.setString(1, model);
            preparedStatement.setBigDecimal(2, new BigDecimal(mass));
            preparedStatement.setString(3, vin);

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while updating this vehicle!";
            throw new DatabaseException(errMsg, ex);
        }
    }

    //Cargo
    public static synchronized void addNewCargo(Cargo cargo) throws DatabaseException {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO CARGO(description, mass, client_id) VALUES(?, ?, ?)");

            preparedStatement.setString(1, cargo.getDescription());
            preparedStatement.setBigDecimal(2, cargo.getWeight());
            preparedStatement.setLong(3, cargo.getClient().getId());

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while adding a new cargo!";
            throw new DatabaseException(errMsg, ex);
        }
    }

    public static synchronized void deleteCargo(Cargo cargo) throws DatabaseException{
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM cargo WHERE id =" + cargo.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while adding a new client!";
            throw new DatabaseException(errMsg, ex);
        }
    }

    public static List<Client> getAvailableClients() throws DatabaseException {
        List<Client> clientList = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT *\n" +
                            "FROM client\n" +
                            "WHERE id NOT IN (SELECT client_id FROM cargo)");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String phone = resultSet.getString("phone_number");

                Client client = new Client(id, name, lastName, phone);
                clientList.add(client);
            }

        } catch (SQLException | IOException ex) {
            String errMsg = "Error while fetching available vehicle list!";
            throw new DatabaseException(errMsg, ex);
        }
        return clientList;
    }

    public static void updateCargo(Cargo cargo, String description, BigDecimal mass,
                                    Long clientId) throws DatabaseException{
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE cargo SET description = ?, mass = ?, client_id = ? WHERE id =" + cargo.getId());

            preparedStatement.setString(1, description);
            preparedStatement.setBigDecimal(2, mass);
            preparedStatement.setLong(3, clientId);

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while adding a new cargo!";
            throw new DatabaseException(errMsg, ex);
        }
    }

    //Client
    public static synchronized List<Client> getAllClients() throws DatabaseException {

        List<Client> clientList = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {

            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM CLIENT");

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String phone = resultSet.getString("phone_number");

                Client newClient = new Client(id, firstName, lastName, phone);

                clientList.add(newClient);
            }
        } catch (SQLException | IOException ex) {
            String errMsg =
                    "Error while fetching driver list!";
            throw new DatabaseException(errMsg, ex);
        }

        return clientList;
    }

    public static Client getClientById(Long client_id) throws DatabaseException {

        Client client = null;
        try (Connection connection = connectToDatabase()) {
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery("SELECT * FROM CLIENT WHERE id=" + client_id);
            while (resultSet.next()) {
                client = new Client(
                        resultSet.getLong("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("phone_number")
                );
            }
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while getting a client by ID!";
            throw new DatabaseException(errMsg, ex);
        }

        return client;
    }

    public static synchronized void addNewClient(Client client) throws DatabaseException {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO CLIENT(first_name, last_name, phone_number) VALUES(?, ?, ?)");

            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getLastname());
            preparedStatement.setString(3, client.getPhoneNumber());

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while adding a new client!";
            throw new DatabaseException(errMsg, ex);
        }
    }

    public static synchronized void deleteClient(Client client) throws DatabaseException{
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM client WHERE id =" + client.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while deleting client: " + client.getId();
            throw new DatabaseException(errMsg, ex);
        }
    }

    public static void updateClient(Client client, String name, String lastName,
                                   String phoneNumber) throws DatabaseException{
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE client SET first_name = ?, last_name = ?, phone_number = ? WHERE id =" + client.getId());

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, phoneNumber);

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while adding a new client!";
            throw new DatabaseException(errMsg, ex);
        }
    }

    //Shipment
    public static synchronized List<Cargo> getAllCargo() throws DatabaseException {

        List<Cargo> cargoList = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {

            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM CARGO");

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String description = resultSet.getString("description");
                BigDecimal weight = resultSet.getBigDecimal("mass");
                Long client_id = resultSet.getLong("client_id");
                Client client = getClientById(client_id);

                Cargo newCargo = new Cargo(id, description, weight, client);

                cargoList.add(newCargo);
            }
        } catch (SQLException | IOException ex) {
            String errMsg =
                    "Error while fetching driver list!";
            throw new DatabaseException(errMsg, ex);
        }

        return cargoList;
    }

    public static Cargo getCargoById(Long cargo_id) throws DatabaseException {

        Cargo cargo = null;
        try (Connection connection = connectToDatabase()) {
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery("SELECT * FROM CARGO WHERE id=" + cargo_id);
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String description = resultSet.getString("description");
                BigDecimal weight = resultSet.getBigDecimal("mass");
                Long clientId = resultSet.getLong("client_id");
                Client client = getClientById(clientId);

                cargo = new Cargo(
                        id,
                        description,
                        weight,
                        client
                );
            }
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while getting cargo by ID!";
            throw new DatabaseException(errMsg, ex);
        }

        return cargo;
    }

    public static synchronized void addNewShipment(Shipment shipment) throws DatabaseException {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO SHIPMENT(cargo_id, driver_id, start_address, finish_address, start_date_time, end_date_time, is_active) VALUES(?, ?, ?, ?, ?, ?, ?)");

            preparedStatement.setLong(1, shipment.getCargo().getId());
            preparedStatement.setLong(2, shipment.getDriver().getId());
            preparedStatement.setString(3, shipment.getAddressFrom());
            preparedStatement.setString(4, shipment.getAddressTo());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(shipment.getPickupDateTime()));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(shipment.getDeliveryDateTime()));
            preparedStatement.setBoolean(7, shipment.getActive());

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while adding shipment!";
            throw new DatabaseException(errMsg, ex);
        }
    }

    public static synchronized void updateShipment(Shipment shipment, Long cargoId, Long driverId,
                                      String startAddress, String finishAddress, boolean isActive,
                                      LocalDateTime startDate, LocalDateTime endDate) throws DatabaseException{
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE shipment SET cargo_id = ?, driver_id = ?, start_address = ?, finish_address = ?, " +
                            "start_date_time = ?, end_date_time = ?, is_active = ? WHERE id =" + shipment.getId());

            preparedStatement.setLong(1, cargoId);
            preparedStatement.setLong(2, driverId);
            preparedStatement.setString(3, startAddress);
            preparedStatement.setString(4, finishAddress);
            preparedStatement.setTimestamp(5, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(endDate));
            preparedStatement.setBoolean(7, isActive);

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while updating this shipment: " + shipment.getId();
            throw new DatabaseException(errMsg, ex);
        }
    }

    public static synchronized void deleteShipment(Shipment shipment) throws DatabaseException{
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM shipment WHERE id =" + shipment.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while deleting shipment: " + shipment.getId();
            throw new DatabaseException(errMsg, ex);
        }
    }


    public static synchronized List<Shipment> getAllShipments() throws DatabaseException {

        List<Shipment> shipmentList = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {

            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM SHIPMENT");

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long cargoId = resultSet.getLong("cargo_id");
                Cargo cargo = getCargoById(cargoId);
                Long driverId = resultSet.getLong("driver_id");
                Driver driver = getAllDrivers().stream().filter(dr -> dr.getId().equals(driverId)).findFirst().orElseThrow();
                String addressFrom = resultSet.getString("start_address");
                String addressTo = resultSet.getString("finish_address");
                Timestamp startDateTime = resultSet.getTimestamp("start_date_time");
                Timestamp endDateTime = resultSet.getTimestamp("end_date_time");
                Boolean isActive = resultSet.getBoolean("is_active");

                Shipment newShipment = new Shipment(
                        id,
                        cargo,
                        driver,
                        addressFrom,
                        addressTo,
                        isActive,
                        startDateTime.toLocalDateTime(),
                        endDateTime.toLocalDateTime()
                );
                shipmentList.add(newShipment);
            }
        } catch (SQLException | IOException ex) {
            String errMsg =
                    "Error while fetching driver list!";
            throw new DatabaseException(errMsg, ex);
        }

        return shipmentList;
    }
    //TODO upit za revers
    public static void setDriverShipmentStatus(Driver driver) throws DatabaseException {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE driver\n" +
                            "SET has_shipment = 1\n" +
                            "WHERE id =" + driver.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String errMsg = "Error while adding a new shipment!";
            throw new DatabaseException(errMsg, ex);
        }
    }
}
