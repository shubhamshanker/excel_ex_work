package com.example.excel_ex_work.exception;

public class PilotPayoutError extends RuntimeException{

    private String message;

    public PilotPayoutError(String s){
        super(s);
        this.message = s;
    }

    @Override
    public String getMessage(){return message;}
}
