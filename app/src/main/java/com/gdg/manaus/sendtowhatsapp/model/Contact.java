package com.gdg.manaus.sendtowhatsapp.model;

/**
 * Created by Michnnick on 04/04/2017.
 */

public class Contact {
    private String name;
    private String number;
    private boolean checked;

    public Contact() {
        this.checked = true;
    }

    public String getName() {
        return name;
    }

    /**
     * Extract the first name from the complete nama
     * @param completeName
     */
    public void setName(String completeName) {
        String[] names = completeName.split(" ");
        // Get First and second names.
        if (names.length > 1) {
            this.name = names[0] + " " + names[1];
        } else if (names.length == 1) {
            this.name = names[0];
        }else {
            name = completeName;
        }
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        String temp = number.replace("+", "").replace("-","").replace(" ", "").trim();

        // Must remove the ninety digit.
        if (temp.length() == 13) {
            char[] tempArray = temp.toCharArray();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tempArray.length; i++) {
                if (i == 4) {
                    continue;
                }

                sb.append(tempArray[i]);
            }

            this.number = sb.toString();
        } else {
            this.number = temp;
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /*public void setNumber(String number) {
        String temp = number.replace("+", "").replace("-","").replace(" ", "").trim();
        this.number = temp;
    }*/
}
