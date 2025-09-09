package avemujica.entrance.filter;

import avemujica.common.utils.Const;
import jakarta.servlet.http.HttpFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(Const.ORDER_FLOW_LIMIT)
public class FlowLimitFilter extends HttpFilter {
}
