package View;

import com.uz.emojione.EmojiOne;

import java.util.Queue;

/* used for set bubble length and width
 * one Chinese character is sized 17, otherwise 9
 * max len of each line is 300, min len is 5, height is 20
 *
 */
public class bubbleTool
{
    //check whether the char is a Chinese character
    public static boolean isCharacter(char c)
    {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if(ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
        )
            return true;
        return false;
    }

    public static double getWidth(String s)
    {
        double width = 10;   // wid:px ratio: 8:7
        Queue<Object> obs = EmojiOne.getInstance().toEmojiAndText(s);
        while(!obs.isEmpty()) {
            Object ob = obs.poll();
            if(ob instanceof String) {
                int len =((String)ob).length();
                for(int i=0;i<len;++i)
                {
                    if(isCharacter(s.charAt(i)))
                        width += 21.5;
                    else
                        width += 11.5;
                }
            }
            else
            {
                width+=21;
            }
        }

        if(width>=300)
            return 300;
        return width;
    }

    public static double getHeight(String s)
    {
        Queue<Object> obs = EmojiOne.getInstance().toEmojiAndText(s);
        double width = 5;
        double height = 25;
        while(!obs.isEmpty()) {
            Object ob = obs.poll();
            if(ob instanceof String) {
                int len =((String)ob).length();
                for(int i=0;i<len;++i)
                {
                    if(s.charAt(i)=='\n')
                    {
                        height += 20;
                        width = 25;
                    }
                    else
                    {
                        if(isCharacter(s.charAt(i)))
                            width += 21.5;
                        else
                            width += 11.5;
                        if(width >= 300)
                        {
                            height += 20;
                            width = 20;
                        }
                    }

                }
            }
            else
            {
                width+=21;
                if(width >= 300)
                {
                    height += 20;
                    width = 20;
                }
            }
        }
        return height;
    }
}
