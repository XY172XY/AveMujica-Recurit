package avemujica.entrance.configuration;

import org.springframework.amqp.core.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean("mailQueue")
    public Queue queue() {
        return QueueBuilder
                .durable("mailQueue")
                .build();
    }

    @Bean("mailExchange")
    public Exchange exchange() {
        return ExchangeBuilder
                .directExchange("mailExchange")
                .build();
    }

    @Bean("mailBinding")
    public Binding binding(@Qualifier("mailQueue") Queue queue,
                           @Qualifier("mailExchange") Exchange exchange ) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("mailQueue")
                .noargs();
    }

}
