package com.unated.askincht_beta.Utils;


public class Constants {
    public static class API {
        public static final String BASE_URL = "http://beta.api.askin.chat/api/v2/";
        public static final String ART_URL = "http://beta.askinchat.ru/artapi/v2/";
        public static final String SIMPLE_URL = "http://beta.app.askin.chat/";
        public static final String SOCKET_URL = "http://beta.askinchat.ru/";

        public static final String AUTH = "auth/login";
        public static final String REFRESH = "auth/refresh";
        public static final String LOGOUT = "auth/logout";
        public static final String REGISTER_DEVICE = "auth/registerDevice";
        public static final String GET_MY_REQUESTS = "conversations/getMyRequests";
        public static final String GET_REQUESTS_MESSAGES = "conversations/getRequestMessages";
        public static final String GET_REQUESTS = "conversations/getRequests";
        public static final String SAVE_TOKEN = "push/setAndroidToken";
        public static final String GET_MY_SHOP = "shops/getShop";
        public static final String SET_READ = "conversations/setRead";
        public static final String GET_PROFILE = "auth/getProfile";
        public static final String GET_REQUEST_SHOPS = "conversations/getMyRequestShops";
        public static final String DELETE_MY_REQUEST = "requests/deleteRequest";
        public static final String DELETE_REQUEST = "requests/deleteResult";
        public static final String GET_UNREADCOUNTS = "conversations/getUnreadCounts";
        public static final String GET_EXACTREQUESTS = "getExactRequests";
        public static final String GET_ELECT = "favorites/getFavorites";
        public static final String SET_ELECT = "favorites/setFavorite";
        public static final String UNSET_ELECT = "favorites/unsetFavorite";
        public static final String REQUEST= "requests/getRequest";
        public static final String GET_RECALL= "shops/getReview";
        public static final String SET_RECALL= "shops/setReview";
        public static final String AUTH_REGISTER= "auth/register";
        public static final String AUTH_SOCIAL= "auth/bindSocialNetwork";
        public static final String AUTH_isSOCIAL= "auth/isBindSocialNetwork";
        public static final String BALANCE= "accounts/getUserBalance";
        public static final String BALANCE_SHOP= "accounts/getShopBalance";
        public static final String RESTORE= "auth/restore";
        public static final String UPLOAD= "storage/uploadChatFile";
        public static final String LOAD= "storage/loadChatFile";
        public static final String DEV= "devlogin";
        public static final String EDIT_PROFILE= "auth/editProfile";
        public static final String CLOSE= "requests/closeRequest";
        public static final String GET_REQUEST_SUPPORT= "conversations/getRequestSupport";
    }

    public static class PREFERENCES {
        public static final String BALANCE= "balance";
        public static final String TOKEN= "token";

        public static final String PREFERENCES_SID = "sid";
        public static final String PREFERENCES_UID = "user_id";
        public static final String PREFERENCES_SPID = "shop_id";
        public static final String PREFERENCES_LAST_MSG_ID = "last_message_id";
        public static final String PREFERENCES_LAST_REQ_ID = "last_request_id";
        public static final String PREFERENCES_DEVICE_ID = "device_id";
        public static final String PREFERENCES_DEVICE_AUTH = "is_device_auth";
        public static final String PREFERENCES_NOTIFICATIONS = "notifications_count";
        public static final String PREFERENCES_MY_NOTIFICATIONS = "my_notifications_count";
        public static final String PREFERENCES_SHOP = "preferences_shop";
        public static final String LATITUDE = "lat";
        public static final String LONGITUDE = "lng";
        public static final String PHONE = "phone";
    }
}
