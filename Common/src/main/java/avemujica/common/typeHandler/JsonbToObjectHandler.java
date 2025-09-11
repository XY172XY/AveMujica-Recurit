package avemujica.common.typeHandler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

//这是一个针对postgresql Json Jsonb对象的万能转换器
@MappedTypes(value={Object.class})
@MappedJdbcTypes({JdbcType.OTHER})
@Slf4j
public class JsonbToObjectHandler extends JacksonTypeHandler {
    private static final PGobject jsonObject = new PGobject();
    private static final String JSONB = "jsonb";
    private static final String JSON = "json";

    public JsonbToObjectHandler(Class<?> type) {
        super(type);
    }


     //写数据库时，把java对象转成JSONB类型
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if (ps != null) {
            jsonObject.setType(JSONB);
            jsonObject.setValue(toJson(parameter));
            ps.setObject(i, jsonObject);
        }
    }

    //读数据时，把JSONB类型的字段转成java对象
    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object v = rs.getObject(columnName);
        return convertDbToJavaObject(v);
    }

     //读数据时，把JSONB类型的字段转成java对象
    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object v = rs.getObject(columnIndex);
        return convertDbToJavaObject(v);
    }

    /**
     * 读数据时，把JSONB类型的字段转成java对象
     */
    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object v = cs.getObject(columnIndex);
        return convertDbToJavaObject(v);
    }


    //读数据时，把JSONB类型的字段转成java对象
    private Object convertDbToJavaObject(Object v) {
        //不是null
        if (Objects.isNull(v)) {
            return null;
        }
        //是PGobject
        if (!PGobject.class.isAssignableFrom(v.getClass())) {
            return v;
        }
        PGobject p = (PGobject) v;
        String type = p.getType();
        //有type
        if (type == null) {
            return v;
        }
        //类型是json或者jsonb
        if (!JSONB.equalsIgnoreCase(type) && !JSON.equalsIgnoreCase(type)) {
            return v;
        }
        String pv = p.getValue();
        //值不为空
        if (StringUtils.isBlank(pv)) {
            return v;
        }

        //解析json
        try {
            return parse(pv);
        } catch (Exception e) {
            log.warn("can not parse Jsonb or Json to any Java Object:{}", e.getMessage());
            return v;
        }
    }


}
