package bmo.Cipher;

public enum ErrorCode {
    //App exit code
    EXIT_SUCCESS(0, "Completed successfully!"),
    EXIT_WARNING(99, "Completed with warnning!"),
    EXIT_FAILURE(2, "Run failed!"),

    CALC_FAILED(10000, "Calc Failed"),
    SQL_FAILED(10000, "SQL Failed"),
    CALC_DEFAULT(-20202, "Default value used"),

    //Database error types
    POSITN_CAL(-20201, "Error occurred during Positn CALCULATION."),

    MKD_NOTFOUND(-20203, "Market Data not found."),
    MKD_HISTDATA(-20530, "Historical Market Data used.")
    ;

    private final int code;
    private final String desc;

    /**
     * Constructor
     */
    private ErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    //getters
    public int getCode() {
        return this.code;
    }
    public String getDesc() {
        return this.desc;
    }

    @Override
    public String toString() {
        return Integer.toString(code) + " : " + desc;
    }

}
