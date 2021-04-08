package v2.exceptions;

import java.io.IOException;

public class TemplateNotValid extends IOException {
    public TemplateNotValid(String err,Throwable throwable){
        super(err,throwable);
    }
}
