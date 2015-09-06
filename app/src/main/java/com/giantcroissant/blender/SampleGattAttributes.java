package com.giantcroissant.blender;

import java.util.HashMap;

/**
 * Created by liyihao on 15/8/20.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String BLENDER_CONTROL = "00001523-1212-efde-1523-785feabcd123";
    public static String BLENDER_SETTING = "00001525-1212-efde-1523-785feabcd123";

    static {
        attributes.put(BLENDER_CONTROL, "Blender Control");
        attributes.put(BLENDER_SETTING, "Blender Setting");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
