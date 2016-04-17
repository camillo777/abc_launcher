package com.millo.test1;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by camillo on 09/10/15.
 */
public class MyPackage{
    Texture _texture;
    String _packagename;
    String _name;

    public MyPackage(Texture texture, String packagename, String name){
        _texture = texture;
        _packagename = packagename;
        _name = name;
    }
}
