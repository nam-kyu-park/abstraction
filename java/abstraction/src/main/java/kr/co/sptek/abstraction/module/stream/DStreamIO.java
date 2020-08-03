package kr.co.sptek.abstraction.module.stream;

import kr.co.sptek.abstraction.module.stream.connector.DConnector;

import java.util.Queue;
import java.util.LinkedList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DStreamIO implements DStream {

    private static final Logger logger = LogManager.getLogger(DStreamIO.class);

    Queue<Object> buffer;

    public DStreamIO() {
    }

    public String toString() {
        return "DStreamIO [ buffer: " + buffer.toString() + "]";
    }

    @Override
    public void open() throws Exception {
        buffer = new LinkedList<>();
    }

    @Override
    public void send(Object data) throws Exception {
        buffer.add(data);
    }

    @Override
    public Object recv() throws Exception {
        while (buffer.isEmpty())
            Thread.yield();
        return poll();
    }

    @Override
    public void seek() throws Exception {
        logger.warn("The seek() function is not supported.");
    }

    @Override
    public Object poll() throws Exception {
        return buffer.poll();
    }

    @Override
    public boolean isEmpty() throws Exception {
        return buffer.isEmpty();
    }
}
