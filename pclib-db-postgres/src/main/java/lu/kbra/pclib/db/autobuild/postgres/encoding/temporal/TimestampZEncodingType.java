package lu.kbra.pclib.db.autobuild.postgres.encoding.temporal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class TimestampZEncodingType implements FixedEncodingType<OffsetDateTime> {

    @Override
    public OffsetDateTime getObject(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getObject(columnIndex, OffsetDateTime.class);
    }

    @Override
    public OffsetDateTime getObject(ResultSet rs, String columnName) throws SQLException {
        return rs.getObject(columnName, OffsetDateTime.class);
    }

    @Override
    public void setObject(PreparedStatement stmt, int index, OffsetDateTime value) throws SQLException {
        stmt.setObject(index, value);
    }

    @Override
    public String getTypeName() {
        return "TIMESTAMP WITH TIME ZONE";
    }

}