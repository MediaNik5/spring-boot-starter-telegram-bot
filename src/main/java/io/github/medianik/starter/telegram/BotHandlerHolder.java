package io.github.medianik.starter.telegram;

import io.github.medianik.starter.telegram.annotation.BotHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BotHandlerHolder{
    private final List<Object> components;

    public BotHandlerHolder(@BotHandler List<Object> components){
        this.components = components;
    }

    public List<Object> getComponents(){
        return components;
    }
}
