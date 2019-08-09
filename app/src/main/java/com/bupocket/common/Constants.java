package com.bupocket.common;

public class Constants {


    public static String WEB_SERVER_DOMAIN = MainNetConfig.WEB_SERVER_DOMAIN.getValue();
    public static String BUMO_NODE_URL = MainNetConfig.BUMO_NODE_URL.getValue();
    public static String BUMO_NODE_URL_BASE=BUMO_NODE_URL;
    public static String PUSH_MESSAGE_SOCKET_URL = WEB_SERVER_DOMAIN;

    public static String IMAGE_PATH = "img_dpos/";
    public static String NODE_PLAN_IMAGE_URL_PREFIX = WEB_SERVER_DOMAIN + IMAGE_PATH;

    public static final Integer SEND_TOKEN_NOTE_MAX_LENGTH = 20;
    public static final Integer HELP_FEEDBACK_CONTENT_LENGTH = 100;
    public static final Integer HELP_FEEDBACK_CONTACT_LENGTH = 20;
    public static final double MIN_FEE = 0.01;
    public static final double MAX_FEE = 10;
    public static final double VOUCHER_MIN_FEE = 0.1;
    public static final double RESERVE_AMOUNT = 0.02;
    public static final Double ACTIVE_AMOUNT_FEE = 0.02;

    public static final Double NODE_COMMON_FEE = 0.15;
    public static final Double NODE_CO_BUILD_PURCHASE_FEE = 10.30;
    public static final Double NODE_CO_BUILD_AMOUNT_FEE = 0.01;

    public static final double MIN_SEND_AMOUNT = 0.00000001;
    public static final double MAX_SEND_AMOUNT = 100000000;
    public static final String REGISTER_TOKEN_FEE = "0.02";
    public static final String ISSUE_TOKEN_FEE = "50.01";
    public static final String ACCOUNT_NOT_ACTIVATED_SEND_FEE = "0.03";
    public static final String ACCOUNT_ACTIVATED_SEND_FEE = "0.01";
    public static final Integer DEFAULT_BUMO_NODE = 1;
    public static final Integer APP_TYPE_CODE = 1;
    public static final Integer BU_DECIMAL = 8;

    public static final Integer TX_REQUEST_TIMEOUT_TIMES = 30;

    public static final int REQUEST_READ_STORAGE_PERMISSION = 100;
    public static final int REQUEST_IMAGE = 300;

    public static final String CONTRACT_ADDRESS = "buQqzdS9YSnokDjvzg4YaNatcFQfkgXqk6ss";

    public static final String QR_LOGIN_PREFIX = "/xDnAs_login/";
    public static final String QR_NODE_PLAN_PREFIX = "/xDnAs_dpos/";
    public static final CharSequence INFO_UDCBU = "###UDCBU###";
    public static final String VOUCHER_QRCODE = "/xDnAs_rv/";


    public static final String WeChat_APPID = "wxaecf7ac4085fd34a";
    public static final String XB_YOUPING_USERNAME = "gh_545e659b7dcd";


    public static final String UM_APPID = "5cc7a5e90cafb257820006d9";
    public static final String NORMAL_WALLET_NAME = "Wallet_1";

    public static final String SHARE_URL = "https://bumo.io/technology";

    public static final String VALIDATE_PATH = "supernodes/detail/validate/";
    public static final String KOL_PATH = "supernodes/detail/kol/";
    public static final String BUMO_NODE_URL_PATH="/getLedger";

    public static final String BUMO_TIMEOUT_MAIN_URL="https://explorer.bumo.io/";
    public static final String BUMO_TIMEOUT_TEST_URL="https://explorer.bumotest.io/";

    public static final String GREEN_DAO_NAME="bu_packet.db";


    public static enum MainNetConfig {
        WEB_SERVER_DOMAIN("https://api-bp.bumo.io/"),
        BUMO_NODE_URL("http://wallet-node.bumo.io"),
        PUSH_MESSAGE_SOCKET_URL("https://ws-tools.bumo.io");
        private String value;

        MainNetConfig(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static enum TestNetConfig {

        //test
//        WEB_SERVER_DOMAIN("http://test-bupocket-api.bumocdn.com/"),
//        // WEB_SERVER_DOMAIN("http://192.168.3.53:8180/"),
//        BUMO_NODE_URL("http://192.168.3.65:36002"),


        //development
//        WEB_SERVER_DOMAIN("http://dev-bp-api.bumocdn.com/"),
//        WEB_SERVER_DOMAIN("http://192.168.6.97:5648/"),
//        BUMO_NODE_URL("http://192.168.21.35:36002"),


//        test-main
        WEB_SERVER_DOMAIN("http://api-bp.bumotest.io/"),
        BUMO_NODE_URL("http://wallet-node.bumotest.io"),


        PUSH_MESSAGE_SOCKET_URL("https://ws-tools.bumotest.io");


        private String value;

        TestNetConfig(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


    public static final String ADDRESS = "address";
    public static final String NODE_ID = "nodeId";
    public static final String TYPE = "type";
    public static final String PATH = "path";
}
