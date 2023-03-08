package com.adivii.companymanagement.views.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;

public class CustomAvatar extends Div {
    public final static String COLOR_RED = "#FD8A8A";
    public final static String COLOR_GREEN = "#7AD61E";
    public final static String COLOR_BLUE = "#54BAB9";
    public final static String COLOR_PURPLE = "#AF7AB3";

    private final static String DEFAULT_SIZE = "36px";

    private String height;
    private String width;

    private String name;
    private Image avatar;

    public CustomAvatar(String name) {
        this(name, null);
    }

    public CustomAvatar(String name, Image image) {
        this.getStyle().set("color", "white");
        this.setName(name);
        this.setAvatar(image);
        this.setSize(DEFAULT_SIZE);
        updateAvatar();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateAvatar();
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
        updateAvatar();
    }

    // Custom Function
    public void setColor(String color) {
        this.getStyle().set("background-color", color);
    }

    public void setColor(int colorIndex) {
        switch (colorIndex) {
            case 0:
                this.setColor(COLOR_BLUE);
                break;
            case 1:
                this.setColor(COLOR_GREEN);
                break;
            case 2:
                this.setColor(COLOR_RED);
                break;
            case 3:
                this.setColor(COLOR_PURPLE);
                break;
            default:
                break;
        }
    }

    public String getAbbreviation() {
        List<String> nameSplitted = new ArrayList<>(Arrays.asList(this.name.split(" ")));
        String abbr = new String();

        for (String string : nameSplitted) {
            if(abbr.length() < 2) {
                abbr = abbr.concat(Character.toString(string.charAt(0)));
            } else {
                break;
            }
        }

        return abbr;
    }

    public void updateAvatar() {
        this.removeAll();

        this.getStyle()
                .set("display", "flex")
                .set("width", width)
                .set("height", height)
                .set("border-radius", "50%")
                .set("overflow", "hidden")
                .set("align-item", "center")
                .set("justify-content", "center");

        if(this.avatar != null) {
            this.avatar.getStyle()
                    .set("object-fit", "contain")
                    .set("height", height);
            add(this.avatar);
        }else{
            Paragraph abbr = new Paragraph(getAbbreviation());
            abbr.setHeight("auto");
            this.add(abbr);
        }
    }

    // TODO: Can't center image when resized
    public void setSize(String size) {
        this.height = size;
        this.width = size;
        updateAvatar();
    }
}
