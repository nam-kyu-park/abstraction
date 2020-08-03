package kr.co.sptek.abstraction.module.ansible;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Ansible extends Object {

    private static final Logger logger = LogManager.getLogger(Ansible.class);

    private List<Object> sequences;
    private Map<Object, Object> elements;

    public static class AnsibleBuilder {

        private static final Logger logger = LogManager.getLogger(Ansible.AnsibleBuilder.class);

        private List<Object> sequences;
        private Map<Object, Object> elements;

        public AnsibleBuilder() {
            this.sequences = new LinkedList<>();
            this.elements = new LinkedHashMap<>();
        }

        public AnsibleBuilder put(String key, Object value) {
            this.elements.put(key, value);
            return this;
        }

        public AnsibleBuilder sub(String key, String value) {
            Ansible as = new Ansible.AnsibleBuilder()
                    .put(key, value)
                    .build();
            this.sequences.add(as);
            return this;
        }

        public AnsibleBuilder sub(String key, Ansible value) {
            this.elements.put(key, value.element());
            return this;
        }

        public AnsibleBuilder sub(Ansible value) {
            this.sequences.add(value);
            return this;
        }

        public AnsibleBuilder sub(String value) {
            this.sequences.add(value);
            return this;
        }

        public AnsibleBuilder clone() {
            AnsibleBuilder builder = new AnsibleBuilder();
            builder.sequences = this.sequences;
            builder.elements = this.elements;
            return builder;
        }

        public Ansible build() {
            return new Ansible(this);
        }
    }

    private Ansible(AnsibleBuilder builder) {
        this.sequences = builder.sequences;
        this.elements = builder.elements;
    }

    public Map<Object, Object> element() {
        return this.elements;
    }

    public boolean isSequence() {
        return (this.sequences.isEmpty() == false);
    }

    public List<Object> sequence() {
        List<Object> node = new LinkedList<>();

        Map<Object, Object> value = this.element();
        if(!value.isEmpty())
            node.add(value);

        //for (Ansible task : this.sequences) {
            //node.add(task.element());
        //}
        for (Object task : this.sequences) {
            if (task instanceof Ansible) {
                node.add(((Ansible)task).element());
            } else {
                node.add(task);
            }
        }

        return node;
    }
}
