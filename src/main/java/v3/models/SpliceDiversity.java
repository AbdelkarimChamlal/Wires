package v3.models;

public class SpliceDiversity {
    String internName;
    String externName;
    String matrix;
    String wiresAtRight;
    String wiresAtLeft;
    String check;
    String wires;

    public String getWires() {
        return wires;
    }

    public void setWires(String wires) {
        this.wires = wires;
    }

    public String getInternName() {
        return internName;
    }

    public void setInternName(String internName) {
        this.internName = internName;
    }

    public String getExternName() {
        return externName;
    }

    public void setExternName(String externName) {
        this.externName = externName;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public String getWiresAtRight() {
        return wiresAtRight;
    }

    public void setWiresAtRight(String wiresAtRight) {
        this.wiresAtRight = wiresAtRight;
    }

    public String getWiresAtLeft() {
        return wiresAtLeft;
    }

    public void setWiresAtLeft(String wiresAtLeft) {
        this.wiresAtLeft = wiresAtLeft;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }
}
