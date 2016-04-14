package com.example.mghlcs.login.Objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mghlcs on 4/14/16.
 */
public class SubApp {

    private String brandName;
    private String brandIcon;
    private String brandDescription;
    private String brandSortOrder;

    public SubApp(JSONObject item) {
        try {
            this.brandName = item.getString("name");
            this.brandIcon = item.getString("iconPath");
            this.brandDescription = item.getString("description");
            this.brandSortOrder = item.getString("sortOrder");
        }  catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandIcon() {
        return brandIcon;
    }

    public void setBrandIcon(String brandIcon) {
        this.brandIcon = brandIcon;
    }

    public String getBrandSortOrder() {
        return brandSortOrder;
    }

    public void setBrandSortOrder(String brandSortOrder) {
        this.brandSortOrder = brandSortOrder;
    }

    public String getBrandDescription() {
        return brandDescription;
    }

    public void setBrandDescription(String brandDescription) {
        this.brandDescription = brandDescription;
    }
}
