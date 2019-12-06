package com.viktorvilmusenaho.asteroidsgl.GL;

import android.opengl.GLES20;

import com.viktorvilmusenaho.asteroidsgl.utils.Utils;

import java.util.Arrays;

public class GLPixelFont {
    public static final int WIDTH = 5; //characters are 5 units wide
    public static final int HEIGHT = 7; //characters are 7 units tall
    private static final int CHAR_COUNT = 46; //the font definition contains 46 entries
    private static final int OFFSET = 45; //it start at ASCII code 45 "-", and ends at 90 "Z".
    private Mesh[] _glyphs = new Mesh[CHAR_COUNT]; //a vertex buffer for each glyph, for rendering with OpenGL.

    public GLPixelFont() {
        for (int c = 0; c < CHAR_COUNT; c++) {
            _glyphs[c] = null;
        }
    }

    public Mesh[] getString(final String s){
        final int count = s.length();
        Mesh[] result = new Mesh[count];
        for(int i = 0; i < count; i++){
            char c = s.charAt(i);
            result[i] = getChar(c);
        }
        return result;
    }

    public Mesh getChar(char c){
        c = Character.toUpperCase(c);
        if(c < OFFSET || c >= OFFSET+CHAR_COUNT){
            return null;
        }
        int i = c - OFFSET;
        if(_glyphs[i] == null){
            _glyphs[i] = createMeshForGlyph(c);
            _glyphs[i].setWidthHeight(WIDTH, HEIGHT);
        }
        return _glyphs[i];
    }

    private Mesh createMeshForGlyph(final char c){
        Utils.require(c >= OFFSET && c < OFFSET+CHAR_COUNT);
        float[] vertices = new float[HEIGHT*WIDTH*Mesh.COORDS_PER_VERTEX];
        final float z = 0;
        final int charIndex = c-OFFSET;
        int i = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                final int index = (HEIGHT*WIDTH) * charIndex + WIDTH * y + x;
                if(FONT_DEFINITION.charAt(index) == '0'){ continue; }
                vertices[i++] =  x;
                vertices[i++] =  y;
                vertices[i++] =  z;
            }
        }
        float[] clean = Arrays.copyOfRange(vertices, 0, i);
        return new Mesh(clean, GLES20.GL_POINTS);
    }

    //FONT_DEFINITION contains most of basic ASCII characters 45-90:
    //Specifically: [0,9], uppercase [A,Z] and - . : = ?
    private static final String FONT_DEFINITION =
            /*[ind	asc	sym]*/
            /*[0	45	'-']*/	    "00000" + "00000" + "00000" + "11111" + "00000" + "00000" + "00000" + //-
            /*[1	46	'.']*/	    "00000" + "00000" + "00000" + "00000" + "00000" + "01100" + "01100" + //.
            /*[2	47	'/']*/	    "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + //
            /*[3	48	'0']*/	    "01110" + "10001" + "10011" + "10101" + "11001" + "10001" + "01110" + //0
            /*[4	49	'1']*/	    "00100" + "01100" + "00100" + "00100" + "00100" + "00100" + "11111" + //1
            /*[5	50	'2']*/	    "01110" + "10001" + "00001" + "00010" + "00100" + "01000" + "11111" + //2
            /*[6	51	'3']*/	    "01110" + "10001" + "00001" + "00110" + "00001" + "10001" + "01110" + //3
            /*[7	52	'4']*/	    "00010" + "00110" + "01010" + "10010" + "11111" + "00010" + "00111" + //4
            /*[8	53	'5']*/	    "11111" + "10000" + "11110" + "00001" + "00001" + "10001" + "01110" + //5
            /*[9	54	'6']*/	    "01110" + "10001" + "10000" + "11110" + "10001" + "10001" + "01110" + //6
            /*[10	55	'7']*/	    "11111" + "10001" + "00010" + "00010" + "00100" + "00100" + "00100" + //7
            /*[11	56	'8']*/	    "01110" + "10001" + "10001" + "01110" + "10001" + "10001" + "01110" + //8
            /*[12	57	'9']*/	    "01110" + "10001" + "10001" + "01111" + "00001" + "00001" + "01110" + //9
            /*[13	58	':']*/	    "00000" + "01100" + "01100" + "00000" + "01100" + "01100" + "00000" + //:
            /*[14	59	';']*/	    "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + //
            /*[15	60	'<']*/	    "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + //
            /*[16	61	'=']*/	    "00000" + "00000" + "11111" + "00000" + "11111" + "00000" + "00000" + //=
            /*[17	62	'>']*/	    "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + //
            /*[18	63	'?']*/	    "01110" + "10001" + "10001" + "00010" + "00100" + "00000" + "00100" + //?
            /*[19	64	'@']*/	    "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + //
            /*[20	65	'A']*/	    "01110" + "10001" + "10001" + "11111" + "10001" + "10001" + "10001" + //A
            /*[21	66	'B']*/	    "11110" + "10001" + "10001" + "11110" + "10001" + "10001" + "11110" + //B
            /*[22	67	'C']*/	    "01110" + "10001" + "10000" + "10000" + "10000" + "10001" + "01110" + //C
            /*[23	68	'D']*/	    "11110" + "10001" + "10001" + "10001" + "10001" + "10001" + "11110" + //D
            /*[24	69	'E']*/	    "11111" + "10000" + "10000" + "11110" + "10000" + "10000" + "11111" + //E
            /*[25	70	'F']*/	    "11111" + "10000" + "10000" + "11110" + "10000" + "10000" + "10000" + //F
            /*[26	71	'G']*/	    "01110" + "10001" + "10000" + "10111" + "10001" + "10001" + "01110" + //G
            /*[27	72	'H']*/	    "10001" + "10001" + "10001" + "11111" + "10001" + "10001" + "10001" + //H
            /*[28	73	'I']*/	    "11111" + "00100" + "00100" + "00100" + "00100" + "00100" + "11111" + //I
            /*[29	74	'J']*/	    "00001" + "00001" + "00001" + "00001" + "10001" + "10001" + "01110" + //J
            /*[30	75	'K']*/	    "10001" + "10010" + "10100" + "11000" + "10100" + "10010" + "10001" + //K
            /*[31	76	'L']*/	    "10000" + "10000" + "10000" + "10000" + "10000" + "10000" + "11111" + //L
            /*[32	77	'M']*/	    "10001" + "11011" + "10101" + "10101" + "10001" + "10001" + "10001" + //M
            /*[33	78	'N']*/	    "10001" + "10001" + "11001" + "10101" + "10011" + "10001" + "10001" + //N
            /*[34	79	'O']*/	    "01110" + "10001" + "10001" + "10001" + "10001" + "10001" + "01110" + //O
            /*[35	80	'P']*/	    "11110" + "10001" + "10001" + "11110" + "10000" + "10000" + "10000" + //P
            /*[36	81	'Q']*/	    "01110" + "10001" + "10001" + "10001" + "10101" + "10010" + "01101" + //Q
            /*[37	82	'R']*/	    "11110" + "10001" + "10001" + "11110" + "10100" + "10010" + "10001" + //R
            /*[38	83	'S']*/	    "01111" + "10000" + "10000" + "01110" + "00001" + "00001" + "11110" + //S
            /*[39	84	'T']*/	    "11111" + "00100" + "00100" + "00100" + "00100" + "00100" + "00100" + //T
            /*[40	85	'U']*/	    "10001" + "10001" + "10001" + "10001" + "10001" + "10001" + "01110" + //U
            /*[41	86	'V']*/	    "10001" + "10001" + "10001" + "10001" + "10001" + "01010" + "00100" + //V
            /*[42	87	'W']*/	    "10001" + "10001" + "10001" + "10101" + "10101" + "10101" + "01010" + //W
            /*[43	88	'X']*/	    "10001" + "10001" + "01010" + "00100" + "01010" + "10001" + "10001" + //X
            /*[44	89	'Y']*/	    "10001" + "10001" + "10001" + "01010" + "00100" + "00100" + "00100" + //Y
            /*[45	90	'Z']*/	    "10001" + "10010" + "00010" + "00100" + "01000" + "01001" + "10001";  //%
}
