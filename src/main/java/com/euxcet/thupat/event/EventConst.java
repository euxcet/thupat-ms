package com.euxcet.thupat.event;

public interface EventConst {

    interface HEADERS {
        String ACTION = "action";
    }

    interface THUPAT {
        interface REQ {
            String ID = "thupat";

            interface ACTIONS {
                String REVERSE = "reverse";
            }

            interface KEYS {
                String STR = "str";
            }
        }
    }
}
