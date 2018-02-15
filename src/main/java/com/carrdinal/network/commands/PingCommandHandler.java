package com.carrdinal.network.commands;

public class PingCommandHandler extends Command {
    @Override public String execute(String[] args) {
        return "pong";
    }
}
