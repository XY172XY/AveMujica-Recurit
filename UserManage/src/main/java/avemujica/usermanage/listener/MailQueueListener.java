package avemujica.usermanage.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;
@RabbitListener(queues = "mailQueue",messageConverter = "jacksonConverter")
@Component
public class MailQueueListener {
    @Resource
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String hostMail;

    @RabbitHandler
    public void processMailQueue(Map<String,Object> data) throws Exception {
        System.out.println("我发送了邮件");
        String email = data.get("email").toString();
        Integer code = (Integer) data.get("code");
        SimpleMailMessage message = switch (data.get("type").toString()){
            case "reset" ->
                createMessage("您的密码重置邮件","你好，您正在重置密码，验证码为："+code+",有效时间为3分钟，如非本人操作请无视",email);
            case "modify" ->
                createMessage("您的邮箱绑定修改操作","你好，您正在修改绑定邮箱，验证码为："+code+",有效时间为3分钟，如非本人操作请无视",email);
            case "register"->
                createMessage("您的账号注册邮件","你好，欢迎参加AveMujica乐队的招新，您的验证码为："+code+"有效时间为3分钟，如非本人操作请无视",email);
            default -> null;
        };
        if(message == null){
            return;
        }
        mailSender.send(message);
    }

    private SimpleMailMessage createMessage(String title, String content, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(hostMail);
        return message;
    }
}
