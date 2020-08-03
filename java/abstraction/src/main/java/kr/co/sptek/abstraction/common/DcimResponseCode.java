package kr.co.sptek.abstraction.common;

public enum DcimResponseCode {

    SUCCESS {
        public String toString() {
            return "DCIM_00000";
        }
    },
    FAIL {
        public String toString() {
            return "DCIM_00900";
        }
    },
    ERROR_UNKNOWN {
        public String toString() {
            return "DCIM_00404";
        }
    },
    ERROR_SERVICE {
        public String toString() {
            return "DCIM_00500";
        }
    };

    DcimResponseCode() {
    }
}
