package avemujica.entrance.configuration;

import org.springframework.amqp.core.*;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean("mailQueue")
    public Queue mailQueue() {
        return QueueBuilder
                .durable("mailQueue")
                .build();
    }

    @Bean("mailExchange")
    public Exchange mailExchange() {
        return ExchangeBuilder
                .directExchange("mailExchange")
                .build();
    }

    @Bean("mailBinding")
    public Binding mailBinding(@Qualifier("mailQueue") Queue queue,
                           @Qualifier("mailExchange") Exchange exchange ) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("mailQueue")
                .noargs();
    }

    @Bean("correctQueue")
    public Queue correctQueue() {
        return  QueueBuilder
                .durable("correctQueue")
                .build();
    }

    @Bean("correctExchange")
    public Exchange correctExchange() {
        return ExchangeBuilder
                .directExchange("correctExchange")
                .build();
    }

    @Bean("correctBinding")
    public Binding correctBinding(@Qualifier("correctQueue") Queue queue,
                                  @Qualifier("correctExchange") Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("correctQueue")
                .noargs();
    }


    @Bean("jacksonConverter")
    public Jackson2JsonMessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }
}
