package gsshop.mobile.v2.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.BitSet;

/**
 * 웹프레임워크 아이스버그에서 제공하는 인코딩 유틸리티
 */
public class IceBergEncoder
{

    public static String encode(String s)
    {
        byte byte0 = 10;
        StringBuffer stringbuffer = new StringBuffer(s.length());
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(byte0);
        OutputStreamWriter outputstreamwriter = new OutputStreamWriter(bytearrayoutputstream);
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(dontNeedEncoding.get(c))
            {
                stringbuffer.append(Integer.toHexString(c));
                continue;
            }
            try
            {
                outputstreamwriter.write(c);
                outputstreamwriter.flush();
            }
            catch(IOException ex)
            {
                bytearrayoutputstream.reset();
                continue;
            }
            byte abyte0[] = bytearrayoutputstream.toByteArray();
            for(int j = 0; j < abyte0.length; j++)
            {
                char c1 = Character.forDigit(abyte0[j] >> 4 & 0xf, 16);
                if(Character.isLetter(c1))
                    c1 -= ' ';
                stringbuffer.append(c1);
                c1 = Character.forDigit(abyte0[j] & 0xf, 16);
                if(Character.isLetter(c1))
                    c1 -= ' ';
                stringbuffer.append(c1);
            }

            bytearrayoutputstream.reset();
        }

        return stringbuffer.toString();
    }

    public IceBergEncoder()
    {
    }

    static BitSet dontNeedEncoding;
    static final int caseDiff = 32;

    static
    {
        dontNeedEncoding = new BitSet(256);
        for(int i = 97; i <= 122; i++)
            dontNeedEncoding.set(i);

        for(int j = 65; j <= 90; j++)
            dontNeedEncoding.set(j);

        for(int k = 48; k <= 57; k++)
            dontNeedEncoding.set(k);

        dontNeedEncoding.set(32);
        dontNeedEncoding.set(45);
        dontNeedEncoding.set(95);
        dontNeedEncoding.set(46);
        dontNeedEncoding.set(42);
    }
}
