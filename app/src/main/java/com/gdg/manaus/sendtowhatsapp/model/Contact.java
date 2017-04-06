package com.gdg.manaus.sendtowhatsapp.model;

/**
 * Created by Michnnick on 04/04/2017.
 */

public class Contact {
    private String firstName;
    private String number;

    public String getFirstName() {
        return firstName;
    }

    /**
     * Extract the first name from the complete nama
     * @param completeName
     */
    public void setFirstName(String completeName) {
        String[] names = completeName.split(" ");
        if (names.length > 0) {
            firstName = names[0];
        } else {
            firstName = completeName;
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

    /*public void setNumber(String number) {
        String temp = number.replace("+", "").replace("-","").replace(" ", "").trim();
        this.number = temp;
    }*/
}
