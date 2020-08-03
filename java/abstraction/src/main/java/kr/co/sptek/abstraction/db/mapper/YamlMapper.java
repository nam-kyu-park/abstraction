package kr.co.sptek.abstraction.db.mapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class YamlMapper {
    private static final Logger logger = LogManager.getLogger(YamlMapper.class);
    private List<Object> sequence;
    private Map<Object, Object> map;

    public static class YamlBuilder {
        private List<Object> sequence;
        private Map<Object, Object> map;

        public YamlBuilder() {
            this.sequence = new LinkedList<>();
            this.map = new LinkedHashMap<>();
        }

        public YamlBuilder sequences(Object...items) {
            for(Object obj : items)
                this.sequence.add(obj);
            return this;
        }

        public YamlBuilder sequences(List<Object> items) {
            this.sequence.add(items);
            return this;
        }

        public YamlBuilder map(String key, Object item) {
            this.map.put(key, item);
            return this;
        }

        public YamlBuilder sequences(YamlMapper...items) {
            for(YamlMapper item : items) {
                if(item.getSequences().isEmpty())
                    this.sequence.add(item.getMap());
                else
                    this.sequence.add(item.getSequences());
            }
            return this;
        }

        public YamlBuilder map(String key, YamlMapper item) {
            if(item.getSequences().isEmpty())
                this.map.put(key, item.getMap());
            else
                 this.map.put(key, item.getSequences());
            return this;
        }

        public YamlMapper build() {
            return new YamlMapper(this);
        }
    }

    private YamlMapper(YamlBuilder builder) {
        this.sequence = builder.sequence;
        this.map = builder.map;
    }

    public List<Object> getSequences() {
        return sequence;
    }

    public Map<Object, Object> getMap() {
        return map;
    }
}
