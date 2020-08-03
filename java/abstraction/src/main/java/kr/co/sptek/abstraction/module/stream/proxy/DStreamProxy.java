package kr.co.sptek.abstraction.module.stream.proxy;

import kr.co.sptek.abstraction.module.stream.DStream;
import kr.co.sptek.abstraction.module.stream.connector.DConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DStreamProxy implements DStream {

    private static final Logger logger = LogManager.getLogger(DStreamProxy.class);

    private DStream stream;

    public DStreamProxy() {}

    public DStreamProxy(DStream stream) {
        setBuffer(stream);
    }

    public String toString() {
        return "DStreamProxy[ stream-buffer: " + stream.toString() + " ]";
    }

    public void setBuffer(DStream stream) {
        this.stream = stream;
    }

    public DStream buffer() {
        return this.stream;
    }

    public boolean isBuffer(){
        return (this.stream != null);
    }

    @Override
    public void open() throws Exception {
        stream.open();
    }

    @Override
    public void send(Object data) throws Exception {
        stream.send(data);
    }

    @Override
    public Object recv() throws Exception {
        return stream.recv();
    }

    @Override
    public void seek() throws Exception {
        logger.warn("The seek() function is not supported.");
    }

    @Override
    public Object poll() throws Exception {
         return stream.poll();
    }

    @Override
    public boolean isEmpty() throws Exception {
        return stream.isEmpty();
    }
}
