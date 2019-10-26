//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ArabicConv {
  private static final char ALF_UPPER_MDD = 'آ';
  private static final char ALF_UPPER_HAMAZA = 'أ';
  private static final char ALF_LOWER_HAMAZA = 'إ';
  private static final char ALF = 'ا';
  private static final char LAM = 'ل';
  private static final char[][] ARABIC_GLPHIES = new char[][]{{'آ', 'ﺁ', 'ﺁ', 'ﺂ', 'ﺂ', '\u0002'}, {'أ', 'ﺂ', 'ﺃ', 'ﺄ', 'ﺄ', '\u0002'}, {'ؤ', 'ﺅ', 'ﺅ', 'ﺆ', 'ﺆ', '\u0002'}, {'إ', 'ﺇ', 'ﺇ', 'ﺈ', 'ﺈ', '\u0002'}, {'ئ', 'ﺉ', 'ﺋ', 'ﺌ', 'ﺊ', '\u0004'}, {'ا', 'ا', 'ا', 'ﺎ', 'ﺎ', '\u0002'}, {'ب', 'ﺏ', 'ﺑ', 'ﺒ', 'ﺐ', '\u0004'}, {'ة', 'ﺓ', 'ﺓ', 'ﺔ', 'ﺔ', '\u0002'}, {'ت', 'ﺕ', 'ﺗ', 'ﺘ', 'ﺖ', '\u0004'}, {'ث', 'ﺙ', 'ﺛ', 'ﺜ', 'ﺚ', '\u0004'}, {'ج', 'ﺝ', 'ﺟ', 'ﺠ', 'ﺞ', '\u0004'}, {'ح', 'ﺡ', 'ﺣ', 'ﺤ', 'ﺢ', '\u0004'}, {'خ', 'ﺥ', 'ﺧ', 'ﺨ', 'ﺦ', '\u0004'}, {'د', 'ﺩ', 'ﺩ', 'ﺪ', 'ﺪ', '\u0002'}, {'ذ', 'ﺫ', 'ﺫ', 'ﺬ', 'ﺬ', '\u0002'}, {'ر', 'ﺭ', 'ﺭ', 'ﺮ', 'ﺮ', '\u0002'}, {'ز', 'ﺯ', 'ﺯ', 'ﺰ', 'ﺰ', '\u0002'}, {'س', 'ﺱ', 'ﺳ', 'ﺴ', 'ﺲ', '\u0004'}, {'ش', 'ﺵ', 'ﺷ', 'ﺸ', 'ﺶ', '\u0004'}, {'ص', 'ﺹ', 'ﺻ', 'ﺼ', 'ﺺ', '\u0004'}, {'ض', 'ﺽ', 'ﺿ', 'ﻀ', 'ﺾ', '\u0004'}, {'ط', 'ﻁ', 'ﻃ', 'ﻂ', 'ﻄ', '\u0004'}, {'ظ', 'ﻅ', 'ﻇ', 'ﻆ', 'ﻆ', '\u0004'}, {'ع', 'ﻉ', 'ﻋ', 'ﻌ', 'ﻊ', '\u0004'}, {'غ', 'ﻍ', 'ﻏ', 'ﻐ', 'ﻎ', '\u0004'}, {'ف', 'ﻑ', 'ﻓ', 'ﻔ', 'ﻒ', '\u0004'}, {'ق', 'ﻕ', 'ﻗ', 'ﻘ', 'ﻖ', '\u0004'}, {'ك', 'ﻙ', 'ﻛ', 'ﻜ', 'ﻚ', '\u0004'}, {'ل', 'ﻝ', 'ﻟ', 'ﻠ', 'ﻞ', '\u0004'}, {'م', 'ﻡ', 'ﻣ', 'ﻤ', 'ﻢ', '\u0004'}, {'ن', 'ﻥ', 'ﻧ', 'ﻨ', 'ﻦ', '\u0004'}, {'ه', 'ﻩ', 'ﻫ', 'ﻬ', 'ﻪ', '\u0004'}, {'و', 'ﻭ', 'ﻭ', 'ﻮ', 'ﻮ', '\u0002'}, {'ى', 'ﻯ', 'ﻯ', 'ﻰ', 'ﻰ', '\u0002'}, {'ٱ', 'ٱ', 'ٱ', 'ﭑ', 'ﭑ', '\u0002'}, {'ي', 'ﻱ', 'ﻳ', 'ﻴ', 'ﻲ', '\u0004'}, {'ٮ', 'ﯤ', 'ﯨ', 'ﯩ', 'ﯥ', '\u0004'}, {'ٱ', 'ٱ', 'ٱ', 'ﭑ', 'ﭑ', '\u0002'}, {'ڪ', 'ﮎ', 'ﮐ', 'ﮑ', 'ﮏ', '\u0004'}, {'ہ', 'ﮦ', 'ﮨ', 'ﮩ', 'ﮧ', '\u0004'}, {'ۤ', 'ۤ', 'ۤ', 'ۤ', 'ﻮ', '\u0002'}, {'چ', 'ﭺ', 'ﭼ', 'ﭽ', 'ﭻ', '\u0004'}, {'پ', 'ﭖ', 'ﭘ', 'ﭙ', 'ﭗ', '\u0004'}, {'ژ', 'ﮊ', 'ﮊ', 'ﮋ', 'ﮋ', '\u0002'}, {'گ', 'ﮒ', 'ﮔ', 'ﮕ', 'ﮓ', '\u0004'}, {'ی', 'ﻯ', 'ﻳ', 'ﻴ', 'ﻰ', '\u0004'}, {'ک', 'ﮎ', 'ﮐ', 'ﮑ', 'ﮏ', '\u0004'}};
  private static final Map<Character, char[]> ARABIC_GLPHIES_MAP;
  private static final char[] HARAKATE = new char[]{'ً', 'ٌ', 'ٍ', 'َ', 'ُ', 'ِ', 'ّ', 'ْ', 'ٓ', 'ٔ', 'ٕ', 'ٖ'};
  private static final char[][] LAM_ALEF_GLPHIES = new char[][]{{'㮦', 'ﻶ', 'ﻵ'}, {'㮧', 'ﻸ', 'ﻷ'}, {'إ', 'ﻺ', 'ﻹ'}, {'ا', 'ﻼ', 'ﻻ'}};

  public ArabicConv() {
  }

  private static char getLamAlef(char AlefCand, char LamCand, boolean isEnd) {
    int shiftRate = 1;
    if (isEnd) {
      ++shiftRate;
    }

    if (1604 == LamCand) {
      switch(AlefCand) {
        case 'آ':
          return LAM_ALEF_GLPHIES[0][shiftRate];
        case 'أ':
          return LAM_ALEF_GLPHIES[1][shiftRate];
        case 'ؤ':
        case 'ئ':
        default:
          break;
        case 'إ':
          return LAM_ALEF_GLPHIES[2][shiftRate];
        case 'ا':
          return LAM_ALEF_GLPHIES[3][shiftRate];
      }
    }

    return '\u0000';
  }

  private static final char getReshapedGlphy(char ch, int off) {
    char[] forms = (char[])ARABIC_GLPHIES_MAP.get(ch);
    if (forms != null) {
      if (ch != forms[0]) {
        throw new RuntimeException();
      } else {
        return forms[off];
      }
    } else {
      return ch;
    }
  }

  private static final char getGlphyType(char ch) {
    char[] forms = (char[])ARABIC_GLPHIES_MAP.get(ch);
    if (forms != null) {
      if (ch != forms[0]) {
        throw new RuntimeException();
      } else {
        return forms[5];
      }
    } else {
      return '\u0002';
    }
  }

  private static String shapeArabic0(String src) {
    if (src.isEmpty()) {
      return "";
    } else {
      char currLetter;
      switch(src.length()) {
        case 0:
          return "";
        case 1:
          return new String(new char[]{getReshapedGlphy(src.charAt(0), 0)});
        case 2:
          char lam = src.charAt(0);
          currLetter = src.charAt(1);
          char lam_alif = getLamAlef(currLetter, lam, true);
          if (lam_alif > 0) {
            return new String(new char[]{lam_alif});
          }
        default:
          char[] reshapedLetters = new char[src.length()];
          currLetter = src.charAt(0);
          reshapedLetters[0] = getReshapedGlphy(currLetter, 2);

          char lam_alif;
          int i;
          for(i = 1; i < src.length() - 1; ++i) {
            lam_alif = getLamAlef(src.charAt(i), currLetter, true);
            if (lam_alif <= 0) {
              if (getGlphyType(src.charAt(i - 1)) == 2) {
                reshapedLetters[i] = getReshapedGlphy(src.charAt(i), 2);
              } else {
                reshapedLetters[i] = getReshapedGlphy(src.charAt(i), 3);
              }
            } else if (i - 2 >= 0 && (i - 2 < 0 || getGlphyType(src.charAt(i - 2)) != 2)) {
              reshapedLetters[i - 1] = 0;
              reshapedLetters[i] = getLamAlef(src.charAt(i), currLetter, false);
            } else {
              reshapedLetters[i - 1] = 0;
              reshapedLetters[i] = lam_alif;
            }

            currLetter = src.charAt(i);
          }

          i = src.length();
          lam_alif = getLamAlef(src.charAt(i - 1), src.charAt(i - 2), true);
          if (lam_alif > 0) {
            if (i > 3 && getGlphyType(src.charAt(i - 3)) == 2) {
              reshapedLetters[i - 2] = 0;
              reshapedLetters[i - 1] = lam_alif;
            } else {
              reshapedLetters[i - 2] = 0;
              reshapedLetters[i - 1] = getLamAlef(src.charAt(i - 1), src.charAt(i - 2), false);
            }
          } else if (getGlphyType(src.charAt(i - 2)) == 2) {
            reshapedLetters[i - 1] = getReshapedGlphy(src.charAt(i - 1), 1);
          } else {
            reshapedLetters[i - 1] = getReshapedGlphy(src.charAt(i - 1), 4);
          }

          StringBuilder sb = new StringBuilder();
          char[] var6 = reshapedLetters;
          int var7 = reshapedLetters.length;

          for(int var8 = 0; var8 < var7; ++var8) {
            char ch = var6[var8];
            if (ch != 0) {
              sb.append(ch);
            }
          }

          return sb.toString();
      }
    }
  }

  public static boolean isArChar(char ch) {
    char[] form = (char[])ARABIC_GLPHIES_MAP.get(ch);
    return form != null;
  }

  public static String shapeArabic(String src) {
    StringBuilder sb = new StringBuilder();
    int i = 0;

    for(int len = src.length(); i < len; ++i) {
      if (!isArChar(src.charAt(i))) {
        sb.append(src.charAt(i));
      } else {
        int arStart;
        for(arStart = i; i < len && isArChar(src.charAt(i)); ++i) {
        }

        sb.append(shapeArabic0(src.substring(arStart, i)));
        if (i < len) {
          sb.append(src.charAt(i));
        }
      }
    }

    return sb.toString();
  }

  public static final void main(String... args) {
  }

  static {
    Map<Character, char[]> arabivGlphiesMap = new HashMap();
    char[][] var1 = ARABIC_GLPHIES;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      char[] forms = var1[var3];
      arabivGlphiesMap.put(forms[0], forms);
    }

    ARABIC_GLPHIES_MAP = Collections.unmodifiableMap(arabivGlphiesMap);
  }
}
