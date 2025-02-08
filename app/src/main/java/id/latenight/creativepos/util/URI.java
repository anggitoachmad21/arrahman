package id.latenight.creativepos.util;

public class URI {
    static final String BASE_URL = "https://devcarwash.cizypos.com/";
//    static final String BASE_URL = "https://arrahman.cizypos.com/";

    public static String API_LOGIN = BASE_URL+"api/cashier/login/";
    public static String CHECK_OPEN_REGISTER = BASE_URL+"api/cashier/checkOpenRegistration/";

    public static String TYPE_MEMBER = BASE_URL+"api/member/type_member_list/";
    public static String TYPE_MEMBER_BY_NAME = BASE_URL+"api/member/type_member_name/";
    public static String TYPE_CAR_BY_NAME = BASE_URL+"api/member/type_car_name/";

    public static String DETAIL_MEMBER = BASE_URL+"api/member/detail_member";

    public static String GENERATE_DATE = BASE_URL+"api/member/generate_date";

    public static String MEMBER_BY_CUSTOMER_NAME = BASE_URL+"api/member/memberByCustomerName";
    public static String CHECK_AND_UPDATE = BASE_URL+"api/member/check_and_update";
    public static String BALANCE_OPEN_REGISTER = BASE_URL+"api/cashier/addBalance/";
    public static String BALANCE_CLOSE_REGISTER = BASE_URL+"api/cashier/closeRegister/";
    public static String API_MENU = BASE_URL+"api/cashier/menu/";
    public static String API_CUSTOMER = BASE_URL+"api/cashier/customer/";
    public static String API_CUSTOMER_DETAIL = BASE_URL+"api/cashier/customer_detail/";
    public static String API_ADD_CUSTOMER = BASE_URL+"api/cashier/add_customer_new/";
    public static String API_CUSTOMER_LIST_HISTORIES = BASE_URL+"api/cashier/customer_list_histories/";
    public static String API_MEMBER_LIST = BASE_URL+"api/member/get_member_list/";
    public static String API_CUSTOMER_SALES_HISTORIES = BASE_URL+"api/cashier/customer_sales_histories/";
    public static String API_TABLE = BASE_URL+"api/cashier/table/";
    public static String API_PLACE_ORDER = BASE_URL+"api/cashier/place_order/";
    public static String API_HOLD_ORDER = BASE_URL+"api/cashier/hold_order/";
    public static String API_INVOICE = BASE_URL+"api/cashier/invoice/";
    public static String API_QUEUE = BASE_URL+"api/cashier/queue/";
    public static String API_WASHING_PROCESS = BASE_URL+"api/cashier/washing_process/";
    public static String API_WASHING_FINISH = BASE_URL+"api/cashier/washing_finish/";
    public static String API_SALON_PROCESS = BASE_URL+"api/cashier/salon_process/";
    public static String API_JOK_PROCESS = BASE_URL+"api/cashier/jok_process/";
    public static String API_DAILY_REPORT = BASE_URL+"api/cashier/daily_report/";
    public static String API_DAILY_STOCK_REPORT = BASE_URL+"api/cashier/daily_stock_report/";
    public static String API_DAILY_PRODUCT_REPORT = BASE_URL+"api/cashier/daily_product_report/";
    public static String API_DAILY_SALES_REPORT = BASE_URL+"api/cashier/daily_sales_report/";
    public static String API_DAILY_RECEIPABLE_REPORT = BASE_URL+"api/cashier/daily_receipable_report/";
    public static String API_FINISH_ORDER = BASE_URL+"api/cashier/update_order_status/";
    public static String API_NEW_ORDER = BASE_URL+"api/cashier/new_orders";
    public static String API_ALL_ORDER = BASE_URL+"api/cashier/all_orders";
    public static String API_TEN_SALES = BASE_URL+"api/cashier/ten_sales";
    public static String API_PAYMENT_METHOD = BASE_URL+"api/cashier/payment_methods";
    public static String API_LOGISTIC = BASE_URL+"api/cashier/logistics";
    public static String API_WASHERS = BASE_URL+"api/cashier/washers/";
    public static String API_MAIN_CATEGORIES = BASE_URL+"api/cashier/main_categories/";
    public static String API_CATEGORIES = BASE_URL+"api/cashier/categories/";
    public static String API_LABELS = BASE_URL+"api/cashier/labels/";
    public static String API_PPN = BASE_URL+"api/cashier/ppn";
    public static String API_DETAIL_ORDER = BASE_URL+"api/cashier/all_information_of_a_sale/";
    public static String API_DELETE_SALES = BASE_URL+"api/cashier/delete_sale/";
    public static String PATH_IMAGE = BASE_URL+"assets/POS/images/";
    public static String API_STOCK_OUT = BASE_URL+"api/cashier/total_stock_keluar/";
    public static String API_TOTAL_SALES_TODAY = BASE_URL+"api/cashier/total_sales_today/";
    public static String API_IMAGE_BILL = "assets/images/logo_print.png";
    public static String API_EMPLOYEES = BASE_URL+"api/cashier/employees";
    public static String SAVE_TOKEN_FCM = BASE_URL+"api/cashier/save_token_fcm/";

    public static String SIMPAN_MEMBER = BASE_URL+"api/member/simpan_member";

    public static String UPDATE_MEMBER = BASE_URL+"api/member/update_member/";

    public static String API_RIWAYAT_LIST = BASE_URL+"api/member/riwayat_member";

    public static String API_CUSTOMER_LIST_NEW = BASE_URL+"api/member/list_customer_new";

    // download item
    public static String DOWNLOAD_CUSTOMER = BASE_URL+"api/download/customers";
    public static String DOWNLOAD_CATEGORIES = BASE_URL+"api/download/categories";
    public static String DOWNLOAD_SUBCATEGORIES = BASE_URL+"api/download/sub_categories";
    public static String DOWNLOAD_LABELS = BASE_URL+"api/download/labels";
    public static String DOWNLOAD_MENUS = BASE_URL+"api/download/menus";
    public static String DOWNLOAD_TABLES = BASE_URL+"api/download/tables";
    public static String DOWNLOAD_PAYMENT_METHOD = BASE_URL+"api/download/payment_method";
    public static String CHECK_COUNT_CUSTOMER = BASE_URL+"api/download/check_count";
    public static String CHECK_COUNT_MENUS = BASE_URL+"api/download/check_menus";
    public static String CHECK_INGREDIENT_STOCK = BASE_URL+"api/download/checkIngredient/";
} 