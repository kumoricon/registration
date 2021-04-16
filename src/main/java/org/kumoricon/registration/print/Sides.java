package org.kumoricon.registration.print;

/*
For double-sided badges, represent whether to print the front, back, or both sides. This is because some
printers have a duplexer and others don't, meaning that you have to print one side, put the printed
sheet back in the printer, and print the other side
 */
public enum Sides {
    FRONT,
    BACK,
    BOTH;

    public static Sides from(String val) {
        if (val == null) {
            return Sides.BOTH;
        }

        if (val.equalsIgnoreCase("front")) {
            return Sides.FRONT;
        } else if (val.equalsIgnoreCase("back")) {
            return Sides.BACK;
        } else {
            return Sides.BOTH;
        }
    }
}
