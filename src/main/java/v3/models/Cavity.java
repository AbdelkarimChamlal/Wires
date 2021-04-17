package v3.models;

import java.util.List;

public class Cavity {
    Connector container; //the parent connector which "this" cavity belongs to
    List<Wire> takesInWires; //the wires the this cavity calls or takes in
    String cavityName; //cavity name or Number

}
