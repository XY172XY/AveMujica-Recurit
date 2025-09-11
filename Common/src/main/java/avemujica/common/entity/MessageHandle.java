package avemujica.common.entity;

import java.util.function.Supplier;
public interface MessageHandle {
     default <T> RestBean<T> messageHandle(Supplier<String> action){
        String message = action.get();
        if(message == null)
            return RestBean.success();
        else
            return RestBean.failure(400, message);
    }
}
