package com.sietecerouno.atlantetransportador.utils;


/**
 * Created by Gio on 7/2/17.
 */


public class Message extends Object
{
    public String BODY_KEY     = "body";

    public Message(String BODY_KEY, String WHO_WRITE, String ID_REQUEST, String ID_USER, String ID_HELP) {
        this.BODY_KEY = BODY_KEY;
        this.WHO_WRITE = WHO_WRITE;
        this.ID_REQUEST = ID_REQUEST;
        this.ID_USER = ID_USER;
        this.ID_HELP = ID_HELP;
    }

    public String WHO_WRITE    = "tipoMensaje";
    public String ID_REQUEST   = "idPedido";
    public String ID_USER      = "userid";
    public String ID_HELP      = "idempleada";

    public String getBODY_KEY() {
        return BODY_KEY;
    }

    public String getWHO_WRITE() {
        return WHO_WRITE;
    }

    public String getID_REQUEST() {
        return ID_REQUEST;
    }

    public String getID_USER() {
        return ID_USER;
    }

    public String getID_HELP() {
        return ID_HELP;
    }

    public void setBODY_KEY(String BODY_KEY) {
        this.BODY_KEY = BODY_KEY;
    }

    public void setWHO_WRITE(String WHO_WRITE) {
        this.WHO_WRITE = WHO_WRITE;
    }

    public void setID_REQUEST(String ID_REQUEST) {
        this.ID_REQUEST = ID_REQUEST;
    }

    public void setID_USER(String ID_USER) {
        this.ID_USER = ID_USER;
    }

    public void setID_HELP(String ID_HELP) {
        this.ID_HELP = ID_HELP;
    }









}