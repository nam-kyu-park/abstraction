package kr.co.sptek.abstraction.module.stream;

import kr.co.sptek.abstraction.module.stream.connector.DConnector;

public interface DStream {
    void open() throws Exception;
    void send(Object data) throws Exception;
    Object recv() throws Exception;
    void seek() throws Exception;
    Object poll() throws Exception;
    boolean isEmpty() throws Exception;
}
