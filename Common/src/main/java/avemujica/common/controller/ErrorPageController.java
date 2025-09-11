package avemujica.common.controller;

import avemujica.common.entity.RestBean;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
//自己获取系统配置的错误路径
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ErrorPageController extends AbstractErrorController {

    public ErrorPageController(ErrorAttributes errorAttributes) {super(errorAttributes);}

    @RequestMapping
    public RestBean<Void> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        Map<String,Object> errorAttributes = this.getErrorAttributes(request,this.getAttributeOptions());
        //这几个常见的我们输出提前设定的错误信息，其它的就输出自己的错误信息
        String message = this.convertErrorMessage(status).orElse(errorAttributes.get("message").toString());
        return RestBean.failure(status.value(), message);
    }

    private Optional<String> convertErrorMessage(HttpStatus status) {
        String value = switch(status.value()){
            case 400 -> "请求参数有误";
            case 404 -> "请求的接口不存在";
            case 405 -> "请求方法错误";
            case 500 -> "内部错误,请联系管理员";
            default -> null;
        };
        return Optional.ofNullable(value);
    }

    //显示配置Message和Exception之后不受其它配置影响，错误都会输出到我们的Map中
    private ErrorAttributeOptions getAttributeOptions() {
        return ErrorAttributeOptions
                .defaults()
                .including(ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.EXCEPTION);
    }
}
