package ru.itis.repository.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.itis.model.State;
import ru.itis.model.User;
import ru.itis.repository.UserRepository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryJdbcTemplateImpl implements UserRepository {
    //language=SQL
    private static final String SQL_SELECT_BY_ID = "select * from users where id = ?";
    //language=SQL
    private static final String SQL_SELECT_ALL = "select * from users";
    //language=SQL
    private static final String SQL_INSERT = "insert into users(email, password, state, confirmcode, login) values (?, ?, ?, ?,?)";
    //language=SQL
    private static final String SQL_SELECT_BY_LOGIN = "select * from users where login = ?";
    //language=SQL
    private static final String SQL_UPDATE = "update users set email = ?, password = ?,  state = ?, confirmcode = ?, login = ? where id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private RowMapper<User> userRowMapper = (row, rowNumber) ->
            User.builder()
                    .id(row.getInt("id"))
                    .email(row.getString("email"))
                    .password(row.getString("password"))
                    .login(row.getString("login"))
                    .state(State.valueOf(row.getString("state")))
                    .confirmCode(row.getString("confirmcode"))
                    .build();

    public Optional<User> find(Integer id) {
        try {
            User user = jdbcTemplate.queryForObject(SQL_SELECT_BY_ID, new Object[]{id}, userRowMapper);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<User> findAll() {
        return jdbcTemplate.query(SQL_SELECT_ALL, userRowMapper);
    }

    public void save(User entity) {

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection
                    .prepareStatement(SQL_INSERT);
            statement.setString(1, entity.getEmail());
            statement.setString(2, entity.getPassword());
            statement.setString(3, String.valueOf(entity.getState()));
            statement.setString(4, entity.getConfirmCode());
            statement.setString(5, entity.getLogin());
            return statement;
        });
    }

    public void delete(Integer id) {

    }

    @Override
    public void update(User entity) {
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection
                    .prepareStatement(SQL_UPDATE);
            statement.setString(1, entity.getEmail());
            statement.setString(2, entity.getPassword());
            statement.setString(3, String.valueOf(entity.getState()));
            statement.setString(4, entity.getConfirmCode());
            statement.setString(5, entity.getLogin());
            statement.setInt(6, entity.getId());
            return statement;
        });
    }

    @Override
    public Optional<User> findByLogin(String login) {
        try {
            User user = jdbcTemplate.queryForObject(SQL_SELECT_BY_LOGIN, new Object[]{login}, userRowMapper);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
