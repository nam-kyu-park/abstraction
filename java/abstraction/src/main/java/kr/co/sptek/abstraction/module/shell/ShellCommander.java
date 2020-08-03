package kr.co.sptek.abstraction.module.shell;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;


public class ShellCommander {

    private static final Logger logger = LogManager.getLogger(ShellCommander.class);

    public ShellCommander() {
    }

    /**
     * Execute shell command
     * @param task
     * @throws IOException
     * @throws InterruptedException
     */

    @Async
    public void execute(String task) throws IOException, InterruptedException {
        logger.info("================= ShellCommander start ===================");
        logger.info("> command: " + task);
        CommandLine commandLine = CommandLine.parse(task);
        logger.info("> parseing command: " + commandLine.toString());
        DefaultExecutor executor = new DefaultExecutor();
        executor.execute(commandLine);
        logger.info("================= ShellCommander end   ===================");
    }
}
