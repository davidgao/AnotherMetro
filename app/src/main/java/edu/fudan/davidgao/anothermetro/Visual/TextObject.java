package edu.fudan.davidgao.anothermetro.Visual;

/**
 * Created by FS on 15/12/22.
 */
public class TextObject {

    public String text;
    public float x;
    public float y;
    public float[] color;

    public TextObject()
    {
        text = "default";
        x = 0f;
        y = 0f;
        color = new float[] {0.5f, 0.5f, 0.5f, 0.5f};
    }

    public TextObject(String txt, float xcoord, float ycoord)
    {
        text = txt;
        x = xcoord;
        y = ycoord;
        color = new float[] {0.5f, 0.5f, 0.5f, 0.5f};
    }
}