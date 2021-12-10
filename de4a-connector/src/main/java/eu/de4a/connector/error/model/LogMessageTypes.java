package eu.de4a.connector.error.model;

public enum LogMessageTypes {
    
    SERVICES("Services", "01"),
    CLIENT("Client", "02"),
    AS4("AS4", "03"),
    ERROR("Error", "04");
    
    String name;
    String code;
    
    LogMessageTypes(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
