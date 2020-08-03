package kr.co.sptek.abstraction.common;

public enum ProxyRules {
    INBOUND {
        public String toString() {
            return "DCIM_INBOUND";
        }
    },
    OUTBOUND {
        public String toString() {
            return "DCIM_OUTBOUND";
        }
    };

    ProxyRules() {
    }
}
