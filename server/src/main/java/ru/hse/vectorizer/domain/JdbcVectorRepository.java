package ru.hse.vectorizer.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.hse.vectorizer.utils.Constants.*;

@RequiredArgsConstructor
@Repository
@Slf4j
public class JdbcVectorRepository implements SimpleVectorRepository {
    private final DataSource dataSource;

    @Override
    public Optional<Vector> findByName(String login, String token) {
        Vector result = null;
        ResultSet resultSet = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME)) {
            statement.setString(1, token);
            statement.setString(2, login);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                result = new Vector(name, x, y, z);
            }
        } catch (SQLException e) {
            log.error("Error in findByName: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return Optional.ofNullable(result);
    }

    @Override
    public boolean deleteByName(String login, String name) {
        boolean deleted = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_NAME)) {
            statement.setString(1, name);
            statement.setString(2, login);
            int affected = statement.executeUpdate();
            if (affected != 0) {
                deleted = true;
            }
        } catch (SQLException e) {
            log.error("Error in deleteByName: " + e.getMessage());
        }
        return deleted;
    }

    @Override
    public Optional<Vector> save(String login, Vector vector) {
        Vector result = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement createStatement = connection.prepareStatement(CREATE);
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE)) {
            createStatement.setDouble(2, vector.getX());
            createStatement.setDouble(3, vector.getY());
            createStatement.setDouble(4, vector.getZ());
            createStatement.setString(1, vector.getName());
            createStatement.setString(5, login);
            updateStatement.setDouble(1, vector.getX());
            updateStatement.setDouble(2, vector.getY());
            updateStatement.setDouble(3, vector.getZ());
            updateStatement.setString(4, vector.getName());
            updateStatement.setString(5, login);
            connection.setAutoCommit(false);
            int affected = updateStatement.executeUpdate();
            if (affected == 0) {
                createStatement.executeUpdate();
            }
            connection.commit();
            connection.setAutoCommit(true);
            result = vector;
        } catch (SQLException e) {
            log.error("Error in save: " + e.getMessage());
        }
        return Optional.ofNullable(result);
    }

    @Override
    public int numberOfRows(String login) {
        int result = 0;
        ResultSet resultSet = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(COUNT_ROWS)) {
            ps.setString(1, login);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error in numberOfRows: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public Iterable<Vector> findAll(String login) {
        List<Vector> res = new ArrayList<>();
        ResultSet resultSet = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL)) {
            statement.setString(1, login);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                res.add(new Vector(name, x, y, z));
            }
        } catch (SQLException e) {
            log.error("Error in findAll: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return res;
    }

    @Override
    public Iterable<Vector> findPage(String login, int pageSize, int pageNumber) {
        if (pageSize <= 0 || pageNumber <= 0) {
            return new ArrayList<>();
        }
        int offset = pageSize * (pageNumber - 1);
        List<Vector> result = new ArrayList<>();
        ResultSet resultSet = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_PAGE)) {
            statement.setString(1, login);
            statement.setInt(2, offset);
            statement.setInt(3, pageSize);

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(new Vector(
                        resultSet.getString("name"),
                        resultSet.getDouble("x"),
                        resultSet.getDouble("y"),
                        resultSet.getDouble("z")
                ));
            }
        } catch (SQLException e) {
            log.error("Error in findPage: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return result;
    }
}
