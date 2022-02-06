package com.euxcet.thupat.event;

public interface EventConst {

    interface HEADERS {
        String ACTION = "action";
    }

    interface THUPAT_DB {
        String ID = "thupat_db";
        interface REQ {
            interface ACTIONS {
                String ADD_ONE = "add_one";
                String DELETE_ONE = "delete_one";
                String GET_ONE = "get_one";
                String GET_SERVICES = "get_services";
            }

            interface KEYS {
                String ID = "id";
                String DATA = "data";
                String TIME = "time";
                String LOCATION = "location";

                String TYPE = "type";
                String NAME = "name";
            }
        }

        interface REPLY {
            interface COMMON_KEYS {
                String RESULT = "result";
            }
        }
    }

    interface THUPAT_WEB {
        String ID = "thupat_web";
        interface REQ {
            interface ACTIONS {
                String REVERSE = "reverse";
            }

            interface KEYS {
                String STR = "str";
            }
        }

        interface REPLY {
            interface COMMON_KEYS {
                String RESULT = "result";
            }
        }
    }
}
