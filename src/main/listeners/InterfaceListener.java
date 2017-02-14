package main.listeners;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;

/**
 * Created by Vincent on 2/14/2017.
 */
public class InterfaceListener implements IListener<ReadyEvent>{

    @Override
    public void handle(ReadyEvent readyEvent) {
        // Do something I guess?
    }
}
