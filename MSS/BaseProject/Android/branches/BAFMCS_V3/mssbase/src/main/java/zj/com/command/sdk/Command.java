package zj.com.command.sdk;

public class Command {

    public static final byte PIECE = (byte) 0xFF;
    public static final byte NUL = (byte) 0x00;
    private static final byte ESC = 0x1B;
    private static final byte FS = 0x1C;
    private static final byte GS = 0x1D;
    private static final byte US = 0x1F;
    private static final byte DLE = 0x10;
    private static final byte DC4 = 0x14;
    private static final byte DC1 = 0x11;
    private static final byte SP = 0x20;
    private static final byte NL = 0x0A;
    private static final byte FF = 0x0C;
    //打�?�机�?始化
    public static byte[] ESC_Init = new byte[]{ESC, '@'};

    /**
     * 打�?�命令
     */
    //打�?�并�?�行
    public static byte[] LF = new byte[]{NL};

    //打�?�并走纸
    public static byte[] ESC_J = new byte[]{ESC, 'J', 0x00};
    public static byte[] ESC_d = new byte[]{ESC, 'd', 0x00};

    //打�?�自检页
    public static byte[] US_vt_eot = new byte[]{US, DC1, 0x04};

    //蜂鸣指令
    public static byte[] ESC_B_m_n = new byte[]{ESC, 'B', 0x00, 0x00};

    //切刀指令
    public static byte[] GS_V_n = new byte[]{GS, 'V', 0x00};
    public static byte[] GS_V_m_n = new byte[]{GS, 'V', 'B', 0x00};
    public static byte[] GS_i = new byte[]{ESC, 'i'};
    public static byte[] GS_m = new byte[]{ESC, 'm'};

    /**
     * 字符设置命令
     */
    //设置字符�?�间�?
    public static byte[] ESC_SP = new byte[]{ESC, SP, 0x00};

    //设置字符打�?�字体格�?
    public static byte[] ESC_ExclamationMark = new byte[]{ESC, '!', 0x00};

    //设置字体�?高�?宽
    public static byte[] GS_ExclamationMark = new byte[]{GS, '!', 0x00};

    //设置�??显打�?�
    public static byte[] GS_B = new byte[]{GS, 'B', 0x00};

    //�?�消/选择90度旋转打�?�
    public static byte[] ESC_V = new byte[]{ESC, 'V', 0x00};

    //选择字体字型(主�?是ASCII�?)
    public static byte[] ESC_M = new byte[]{ESC, 'M', 0x00};

    //选择/�?�消加粗指令
    public static byte[] ESC_G = new byte[]{ESC, 'G', 0x00};
    public static byte[] ESC_E = new byte[]{ESC, 'E', 0x00};

    //选择/�?�消倒置打�?�模�?
    public static byte[] ESC_LeftBrace = new byte[]{ESC, '{', 0x00};

    //设置下划线点高度(字符)
    public static byte[] ESC_Minus = new byte[]{ESC, 45, 0x00};

    //字符模�?
    public static byte[] FS_dot = new byte[]{FS, 46};

    //汉字模�?
    public static byte[] FS_and = new byte[]{FS, '&'};

    //设置汉字打�?�模�?
    public static byte[] FS_ExclamationMark = new byte[]{FS, '!', 0x00};

    //设置下划线点高度(汉字)
    public static byte[] FS_Minus = new byte[]{FS, 45, 0x00};

    //设置汉字左�?�间�?
    public static byte[] FS_S = new byte[]{FS, 'S', 0x00, 0x00};

    //选择字符代�?页
    public static byte[] ESC_t = new byte[]{ESC, 't', 0x00};

    /**
     * 格�?设置指令
     */
    //设置默认行间�?
    public static byte[] ESC_Two = new byte[]{ESC, 50};

    //设置行间�?
    public static byte[] ESC_Three = new byte[]{ESC, 51, 0x00};

    //设置对�?模�?
    public static byte[] ESC_Align = new byte[]{ESC, 'a', 0x00};

    //设置左边�?
    public static byte[] GS_LeftSp = new byte[]{GS, 'L', 0x00, 0x00};

    //设置�?对打�?��?置
    //将当�?�?置设置到�?离行首（nL + nH x 256）处。
    //如果设置�?置在指定打�?�区域外，该命令被忽略
    public static byte[] ESC_Relative = new byte[]{ESC, '$', 0x00, 0x00};

    //设置相对打�?��?置
    public static byte[] ESC_Absolute = new byte[]{ESC, 92, 0x00, 0x00};

    //设置打�?�区域宽度
    public static byte[] GS_W = new byte[]{GS, 'W', 0x00, 0x00};

    /**
     * 状�?指令
     */
    //实时状�?传�?指令
    public static byte[] DLE_eot = new byte[]{DLE, 0x04, 0x00};

    //实时弹钱箱指令
    public static byte[] DLE_DC4 = new byte[]{DLE, DC4, 0x00, 0x00, 0x00};

    //标准弹钱箱指令
    public static byte[] ESC_p = new byte[]{ESC, 'F', 0x00, 0x00, 0x00};

    /**
     * �?��?设置指令
     */
    //选择HRI打�?�方�?
    public static byte[] GS_H = new byte[]{GS, 'H', 0x00};

    //设置�?��?高度
    public static byte[] GS_h = new byte[]{GS, 'h', (byte) 0xa2};

    //设置�?��?宽度
    public static byte[] GS_w = new byte[]{GS, 'w', 0x00};

    //设置HRI字符字体字型
    public static byte[] GS_f = new byte[]{GS, 'f', 0x00};

    //�?��?左�??移指令
    public static byte[] GS_x = new byte[]{GS, 'x', 0x00};

    //打�?��?��?指令
    public static byte[] GS_k = new byte[]{GS, 'k', 'A', FF};

    //二维�?相关指令
    public static byte[] GS_k_m_v_r_nL_nH = new byte[]{ESC, 'Z', 0x03, 0x03, 0x08, 0x00, 0x00};

}
