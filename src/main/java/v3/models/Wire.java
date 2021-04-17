package v3.models;

public class Wire {
    Module callingModule; // the module which called this wire
    String wireCustomerName; // the customer name of this wire
    String crimpingType; // the crimping type of this wire
    String crimpingDouble; // the crimping double which this wire crimp with in case of double
}
