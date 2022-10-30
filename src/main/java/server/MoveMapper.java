package server;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MoveMapper implements RowMapper<Move> {
    public Move mapRow(ResultSet rs, int rowNum) throws SQLException {
        Move move = new Move();
        move.setX1(rs.getInt("x1"));
        move.setY1(rs.getInt("y1"));
        move.setX2(rs.getInt("x2"));
        move.setY2(rs.getInt("y2"));
        move.setPlayer(rs.getString("player"));
        return move;
    }
}
