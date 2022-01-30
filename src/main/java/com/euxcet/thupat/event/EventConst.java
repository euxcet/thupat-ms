package com.euxcet.thupat.event;

public interface EventConst {

    interface HEADERS {
        String ACTION = "action";
    }

    interface SMS {
        interface REQ {
            String ID = "sms";

            interface ACTIONS {
                String SEND_SMS = "send-check-no-sms";
                String QUERY_REPLY = "query-reply";
            }

            interface KEYS {
                String TEMPLATE_ID = "template-id";
                String SIGN_ID = "sign-id";
                String PHONE = "phone";
                String PARA = "para";
            }
        }
    }
}
