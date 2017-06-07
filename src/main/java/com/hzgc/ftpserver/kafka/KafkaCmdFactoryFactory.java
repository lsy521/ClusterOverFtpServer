package com.hzgc.ftpserver.kafka;

import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.command.impl.*;

import java.util.HashMap;
import java.util.Map;

public class KafkaCmdFactoryFactory {
    private static final HashMap<String, Command> DEFAULT_COMMAND_MAP = new HashMap<String, Command>();

    static {
        // first populate the default command list
        DEFAULT_COMMAND_MAP.put("ABOR", new ABOR());
        DEFAULT_COMMAND_MAP.put("ACCT", new ACCT());
        DEFAULT_COMMAND_MAP.put("APPE", new APPE());
        DEFAULT_COMMAND_MAP.put("AUTH", new AUTH());
        DEFAULT_COMMAND_MAP.put("CDUP", new CDUP());
        DEFAULT_COMMAND_MAP.put("CWD", new CWD());
        DEFAULT_COMMAND_MAP.put("DELE", new DELE());
        DEFAULT_COMMAND_MAP.put("EPRT", new EPRT());
        DEFAULT_COMMAND_MAP.put("EPSV", new EPSV());
        DEFAULT_COMMAND_MAP.put("FEAT", new FEAT());
        DEFAULT_COMMAND_MAP.put("HELP", new HELP());
        DEFAULT_COMMAND_MAP.put("LANG", new LANG());
        DEFAULT_COMMAND_MAP.put("LIST", new LIST());
        DEFAULT_COMMAND_MAP.put("MD5", new MD5());
        DEFAULT_COMMAND_MAP.put("MFMT", new MFMT());
        DEFAULT_COMMAND_MAP.put("MMD5", new MD5());
        DEFAULT_COMMAND_MAP.put("MDTM", new MDTM());
        DEFAULT_COMMAND_MAP.put("MLST", new MLST());
        DEFAULT_COMMAND_MAP.put("MKD", new MKD());
        DEFAULT_COMMAND_MAP.put("MLSD", new MLSD());
        DEFAULT_COMMAND_MAP.put("MODE", new MODE());
        DEFAULT_COMMAND_MAP.put("NLST", new NLST());
        DEFAULT_COMMAND_MAP.put("NOOP", new NOOP());
        DEFAULT_COMMAND_MAP.put("OPTS", new OPTS());
        DEFAULT_COMMAND_MAP.put("PASS", new PASS());
        DEFAULT_COMMAND_MAP.put("PASV", new PASV());
        DEFAULT_COMMAND_MAP.put("PBSZ", new PBSZ());
        DEFAULT_COMMAND_MAP.put("PORT", new PORT());
        DEFAULT_COMMAND_MAP.put("PROT", new PROT());
        DEFAULT_COMMAND_MAP.put("PWD", new PWD());
        DEFAULT_COMMAND_MAP.put("QUIT", new QUIT());
        DEFAULT_COMMAND_MAP.put("REIN", new REIN());
        DEFAULT_COMMAND_MAP.put("REST", new REST());
        DEFAULT_COMMAND_MAP.put("RETR", new RETR());
        DEFAULT_COMMAND_MAP.put("RMD", new RMD());
        DEFAULT_COMMAND_MAP.put("RNFR", new RNFR());
        DEFAULT_COMMAND_MAP.put("RNTO", new RNTO());
        DEFAULT_COMMAND_MAP.put("SITE", new SITE());
        DEFAULT_COMMAND_MAP.put("SIZE", new SIZE());
        DEFAULT_COMMAND_MAP.put("STAT", new STAT());
        DEFAULT_COMMAND_MAP.put("STOR", new KafkaSTOR());
        DEFAULT_COMMAND_MAP.put("STOU", new STOU());
        DEFAULT_COMMAND_MAP.put("STRU", new STRU());
        DEFAULT_COMMAND_MAP.put("SYST", new SYST());
        DEFAULT_COMMAND_MAP.put("TYPE", new TYPE());
        DEFAULT_COMMAND_MAP.put("USER", new USER());
    }

    private Map<String, Command> commandMap = new HashMap<String, Command>();

    private boolean useDefaultCommands = true;

    public CommandFactory createCommandFactory() {

        Map<String, Command> mergedCommands = new HashMap<String, Command>();
        if(useDefaultCommands) {
            mergedCommands.putAll(DEFAULT_COMMAND_MAP);
        }

        mergedCommands.putAll(commandMap);

        return new DefaultCommandFactory(mergedCommands);
    }

    public boolean isUseDefaultCommands() {
        return useDefaultCommands;
    }

    public void setUseDefaultCommands(final boolean useDefaultCommands) {
        this.useDefaultCommands = useDefaultCommands;
    }

    public Map<String, Command> getCommandMap() {
        return commandMap;
    }

    public void addCommand(String commandName, Command command) {
        if(commandName == null) {
            throw new NullPointerException("commandName can not be null");
        }
        if(command == null) {
            throw new NullPointerException("command can not be null");
        }

        commandMap.put(commandName.toUpperCase(), command);
    }

    public void setCommandMap(final Map<String, Command> commandMap) {
        if (commandMap == null) {
            throw new NullPointerException("commandMap can not be null");
        }

        this.commandMap.clear();

        for (Map.Entry<String, Command> entry : commandMap.entrySet()) {
            this.commandMap.put(entry.getKey().toUpperCase(), entry.getValue());
        }
    }
}
