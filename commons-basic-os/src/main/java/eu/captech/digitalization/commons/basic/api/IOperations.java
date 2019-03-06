package eu.captech.digitalization.commons.basic.api;

import org.jetbrains.annotations.Nullable;

public interface IOperations extends Comparable<IOperations> {
    public static final String IMAPS_PROTOCOL_STRING = "imaps";
    public static final String WARNING_DAYS_STRING = "warningDays";
    public static final String COMMAND_STRING = "command";
    String OPERATIONS_PROTOCOL_UNKNOWN = "unknown";
    String OPERATIONS_PROTOCOL_JDBC = "jdbc";
    String OPERATIONS_PROTOCOL_SFTP = "sftp";
    String OPERATIONS_PROTOCOL_FS = "fs";
    String OPERATIONS_PROTOCOL_REST = "rest";
    String OPERATIONS_PROTOCOL_EMAIL = "email";
    String OPERATIONS_PROTOCOL_OS = "os";
    String OPERATIONS_PROTOCOL_KEYTOOL = "keytool";
    String SPLIT_STRING = "[\\s]";
    String EMPTY_STRING = "";
    String SPACE_STRING = " ";
    String COLON_STRING = ":";
    String SEMI_COLON_STRING = ";";
    String EQUAL_STRING = "=";
    String FORWARD_SLASH_STRING = "/";

    String getInitialParameter();

    String getProtocol();

    boolean isConnected(@Nullable String parameter);

    String checkProtocolPatterns(String evaluate);

    String getOperationPatternForm();

    String getOperationsInfo(@Nullable String parameter);
}
