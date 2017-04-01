package com.example.jacob.findmypharmacy;

/**
 * Created by Jacob on 26.03.2017.
 */

public class Network {
    private int net_id;
    private String net_name;
    private String net_label;
    private int net_pharms;

    public Network(int net_id, String net_name, String net_label, int net_pharms) {
        this.net_id = net_id;
        this.net_name = net_name;
        this.net_label = net_label;
        this.net_pharms = net_pharms;
    }

    public int getNet_id() {
        return net_id;
    }

    public void setNet_id(int net_id) {
        this.net_id = net_id;
    }

    public String getNet_name() {
        return net_name;
    }

    public void setNet_name(String net_name) {
        this.net_name = net_name;
    }

    public String getNet_label() {
        return net_label;
    }

    public void setNet_label(String net_label) {
        this.net_label = net_label;
    }

    public int getNet_pharms() { return net_pharms; }

    public void setNet_pharms(int net_pharms) { this.net_pharms = net_pharms; }
}
