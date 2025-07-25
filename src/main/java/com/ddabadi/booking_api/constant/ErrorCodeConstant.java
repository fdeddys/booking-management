package com.ddabadi.booking_api.constant;

public class ErrorCodeConstant {

    public static String CODE_SUCCESS = "0000";
    public static String CODE_SUCCESS_MESSAGE = "Success";

    public interface DATABASE {
        public static String CODE_FAILED_SAVE_DB = "0050";
        public static String CODE_FAILED_SAVE_DB_MESSAGE = "Failed save to database";
    }

    public interface USER {
        public static String CODE_USER_NOT_FOUND = "0100";
        public static String CODE_USER_NOT_FOUND_MESSAGE = "User not found";

        public static String CODE_USER_ALREADY_EXIST = "0110";
        public static String CODE_USER_ALREADY_EXIST_MESSAGE = "Username already exist";
    }


    public interface ROOM {
        public static String CODE_ROOM_NAME_EXIST = "0200";
        public static String CODE_ROOM_NAME_EXIST_MESSAGE = "Room name already exist";

        public static String CODE_ROOM_ID_NOT_EXIST = "0210";
        public static String CODE_ROOM_ID_NOT_EXIST_MESSAGE = "Room ID not exist";
    }

    public interface BOOKING {
        public static String CODE_FAILED_VALIDATION = "0300";

        public static String CODE_BOOKING_ID_NOT_EXIST = "0310";
        public static String CODE_BOOKING_ID_NOT_EXIST_MESSAGE = "Book ID not exist";
    }
}
