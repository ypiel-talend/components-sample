package org.talend.components.widget.source;

import java.io.Serializable;

public class WidgetInputRecord implements Serializable {

    private String c1;

    public String getC1() {
        return c1;
    }

    public void setC1(final String c1) {
        this.c1 = c1;
    }

    private boolean c2;

    public boolean getC2() {
        return c2;
    }

    public void setC2(final boolean c2) {
        this.c2 = c2;
    }

    private double c3;

    public double getC3() {
        return c3;
    }

    public void setC3(final double c3) {
        this.c3 = c3;
    }

    private int c4;

    public int getC4() {
        return c4;
    }

    public void setC4(final int c4) {
        this.c4 = c4;
    }

}