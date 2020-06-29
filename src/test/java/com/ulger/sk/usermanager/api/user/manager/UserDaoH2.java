package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.exception.DataAccessException;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class UserDaoH2 implements UserDao<Integer> {

    public UserDaoH2() {
        generateSchema();
    }

    @Override
    public User findById(Integer id) throws DataAccessException {
        String query = generateSqlQuery("SELECT * FROM user where id=%s", id.toString());

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
        String query = generateSqlQuery("SELECT * FROM user where email='%s'", email);

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
    public User save(User user) throws DataAccessException {
        if (Objects.isNull(user.getId())) {
            return create(user);
        }

        return updateUser(user);
    }

    private User create(User user) {
        String selectQuery = generateSqlQuery("SELECT * FROM user where email='%s'", user.getEmail());
        String insertQuery = generateSqlQuery(
                "INSERT INTO user (email, first_name, last_name, credential) VALUES ('%s', '%s', '%s', '%s')",
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

    private User updateUser(User user) {
        String selectQuery = generateSqlQuery("SELECT * FROM user where email='%s'", user.getEmail());
        String updateQuery = generateSqlQuery(
                "UPDATE user SET email = '%s', first_name = '%s', last_name = '%s', credential = '%s' WHERE ID=%s",
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCredential(),
                user.getId().toString());

        try (Statement stmt = createStatement()) {
            stmt.execute(updateQuery);
            ResultSet rs = stmt.executeQuery(selectQuery);
            return extractResultSet(rs).get(0);
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    private List<User> extractResultSet(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();

        while (rs.next()) {
            User user = UserImp.Builder.anUserImp()
                    .withId(rs.getInt("id"))
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
