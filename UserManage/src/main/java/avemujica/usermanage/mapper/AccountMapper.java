package avemujica.usermanage.mapper;

import avemujica.usermanage.entity.dto.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
    @Select("SELECT COUNT(*) from am_account")
    int getAccountCount();
}