package server;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

public class GameJDBCTemplate {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }
    public int create() {
        String SQL = "INSERT INTO Games (id) values (null)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplateObject.update(connection -> connection
                .prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS), keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }
    public void insertPlayer(Integer id, String username, int position) {
        String SQL = "insert into `Games-Players` (gameId, player, position) values (?, ?, ?)";
        jdbcTemplateObject.update( SQL, id, username, position);
    }
    public void insertMove(Integer gameId, int move, String username, int x1, int y1, int x2, int y2) {
        String SQL = "insert into Moves (gameId, move, player, x1, y1, x2, y2) values (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplateObject.update( SQL, gameId, move, username, x1, y1, x2, y2);
    }
    public String getPlayerByPosition(Integer id, int position) {
        String SQL = "select player from `Games-Players` where gameId = ? and position = ?";
        return jdbcTemplateObject.queryForObject(SQL, new Object[]{id, position}, String.class);
    }
    public List<Integer> listGames(String username) {
        String SQL = "select gameId from `Games-Players` WHERE player = ?";
        return jdbcTemplateObject.queryForList(SQL, new Object[]{username}, Integer.class);
    }
    public void delete(Integer id) {
        String SQL = "delete from Games where id = ?";
        jdbcTemplateObject.update(SQL, id);
        System.out.println("Deleted Record with ID = " + id );
    }
    public int getPlayersNumber(Integer gameId) {
        String SQL = "select count(*) from `Games-Players` where gameId = ?";
        Integer num = jdbcTemplateObject.queryForObject(SQL, new Object[]{gameId}, Integer.class);
        return Objects.requireNonNullElse(num, -1);
    }
    public Move getMove(Integer gameId, int move) {
        String SQL = "select count(*) from Moves where gameId = ? and move = ?";
        Integer num = jdbcTemplateObject.queryForObject(SQL, new Object[]{gameId, move}, Integer.class);
        if (num != null && num > 0) {
            SQL = "select * from Moves where gameId = ? and move = ?";
            return jdbcTemplateObject.queryForObject(SQL, new Object[]{gameId, move}, new MoveMapper());
        }
        return null;
    }
}
