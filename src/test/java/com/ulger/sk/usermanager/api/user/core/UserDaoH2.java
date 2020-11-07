package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.exception.DataAccessException;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class UserDaoH2 implements UserDao {

    public UserDaoH2() {
        generateSchema();
    }

    @Override
    public User findByUsername(String username) throws DataAccessException {
        String query = generateSqlQuery("SELECT * FROM user where username=%s", username);

        try (Statement stmt = createStatement()) {
            ResultSet rs = stmt.executeQuery(query);

            if (rs.getRow() > 1) {
                throw new SQLDataException("Expected only one row but returned " + rs.getRow() + " rows");
            }

            List<User> result = extractResultSet(rs);
            if (result.size() == 1) {
                return result.get(0);
            }

            return null;

        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public User findByEmail(String email) throws DataAccessException {
        String query = generateSqlQuery("SELECT * FROM user where email=%s", email);

        try (Statement stmt = createStatement()) {
            ResultSet rs = stmt.executeQuery(query);

            if (rs.getRow() > 1) {
                throw new SQLDataException("Expected only one row but returned " + rs.getRow() + " rows");
            }

            List<User> result = extractResultSet(rs);
            if (result.size() == 1) {
                return result.get(0);
            }

            return null;
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public List<User> find() throws DataAccessException {
        String query = generateSqlQuery("SELECT * FROM user");

        try (Statement stmt = createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            return extractResultSet(rs);
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public User create(User user) throws DataAccessException {
        String selectQuery = generateSqlQuery(
                "SELECT * FROM user where username=%s",
                user.getUsername());

        String insertQuery = generateSqlQuery(
                "INSERT INTO user (username, email, first_name, last_name, credential) VALUES (%s, %s, %s, %s, %s)",
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCredential());

        try (Statement stmt = createStatement()) {
            stmt.execute(insertQuery);
            ResultSet rs = stmt.executeQuery(selectQuery);
            return extractResultSet(rs).get(0);
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public User updateByUsername(String username, User user) throws DataAccessException {
        String selectQuery = generateSqlQuery(
                "SELECT * FROM user where username=%s",
                username);

        String updateQuery = generateSqlQuery(
                "UPDATE user SET email = %s, first_name = %s, last_name = %s, credential = %s WHERE username=%s",
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCredential(),
                username);

        try (Statement stmt = createStatement()) {
            stmt.execute(updateQuery);
            ResultSet rs = stmt.executeQuery(selectQuery);
            return extractResultSet(rs).get(0);
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public User updatePasswordByUsername(String username, String password) throws DataAccessException {
        return null;
    }

    private List<User> extractResultSet(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();

        while (rs.next()) {
            User user = DefaultUser.Builder.anUserImp()
                    .withUsername(rs.getString("username"))
                    .withEmail(rs.getString("email"))
                    .withFirstName(rs.getString("first_name"))
                    .withLastName(rs.getString("last_name"))
                    .withCredential(rs.getString("credential"))
                    .build();

            users.add(user);
        }

        return users;
    }

    private String generateSqlQuery(String template, String...params) {
        params = Arrays
                .stream(params)
                .map(s -> s == null ? null : String.format("'%s'", s))
                .toArray(String[]::new);

        return String.format(template, params);
    }

    private Statement createStatement() {
        try {
            return getConnection().createStatement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() {
        try {
            InputStream input = UserDaoH2.class.getClassLoader().getResourceAsStream("application.properties");
            Properties properties = new Properties();
            properties.load(input);

            String url = (String) properties.get("jdbc.url");
            String username = "sa";
            String password = "";

            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateSchema() {
        try {
            String dropStatement = "DROP TABLE IF EXISTS user";
            createStatement().execute(dropStatement);

            InputStream inputStream = UserDaoH2.class.getClassLoader().getResourceAsStream("schema.sql");
            String schema = IOUtils.toString(inputStream);
            createStatement().execute(schema);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
