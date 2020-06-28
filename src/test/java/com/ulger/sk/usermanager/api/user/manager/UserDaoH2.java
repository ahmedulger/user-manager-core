package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.exception.DataAccessException;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

public class UserDaoH2 implements UserDao<Integer> {

    public UserDaoH2() {
        generateSchema();
    }

    @Override
    public User findById(Integer id) throws DataAccessException {
        String query = generateSqlQuery("SELECT * FROM user where id=%s", id.toString());
        return null;
    }

    @Override
    public User findByEmail(String email) throws DataAccessException {
        return null;
    }

    @Override
    public List<User> find() throws DataAccessException {
        return null;
    }

    @Override
    public User save(User user) throws DataAccessException {
        return null;
    }

    private String generateSqlQuery(String template, String...params) {
        return String.format(template, params);
    }

    private void executeStatement() {

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
            String dropStatement = "DROP TABLE user";
            createStatement().execute(dropStatement);

            InputStream inputStream = UserDaoH2.class.getClassLoader().getResourceAsStream("schema.sql");
            String schema = IOUtils.toString(inputStream);
            createStatement().execute(schema);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
