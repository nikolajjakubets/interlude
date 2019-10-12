//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.math;

public class SafeMath {
    public SafeMath() {
    }

    public static int addAndCheck(int a, int b) throws ArithmeticException {
        return addAndCheck(a, b, "overflow: add", false);
    }

    public static int addAndLimit(int a, int b) {
        return addAndCheck(a, b, (String)null, true);
    }

    private static int addAndCheck(int a, int b, String msg, boolean limit) {
        int ret;
        if (a > b) {
            ret = addAndCheck(b, a, msg, limit);
        } else if (a < 0) {
            if (b < 0) {
                if (-2147483648 - b <= a) {
                    ret = a + b;
                } else {
                    if (!limit) {
                        throw new ArithmeticException(msg);
                    }

                    ret = -2147483648;
                }
            } else {
                ret = a + b;
            }
        } else if (a <= 2147483647 - b) {
            ret = a + b;
        } else {
            if (!limit) {
                throw new ArithmeticException(msg);
            }

            ret = 2147483647;
        }

        return ret;
    }

    public static long addAndLimit(long a, long b) {
        return addAndCheck(a, b, "overflow: add", true);
    }

    public static long addAndCheck(long a, long b) throws ArithmeticException {
        return addAndCheck(a, b, "overflow: add", false);
    }

    private static long addAndCheck(long a, long b, String msg, boolean limit) {
        long ret;
        if (a > b) {
            ret = addAndCheck(b, a, msg, limit);
        } else if (a < 0L) {
            if (b < 0L) {
                if (-9223372036854775808L - b <= a) {
                    ret = a + b;
                } else {
                    if (!limit) {
                        throw new ArithmeticException(msg);
                    }

                    ret = -9223372036854775808L;
                }
            } else {
                ret = a + b;
            }
        } else if (a <= 9223372036854775807L - b) {
            ret = a + b;
        } else {
            if (!limit) {
                throw new ArithmeticException(msg);
            }

            ret = 9223372036854775807L;
        }

        return ret;
    }

    public static int mulAndCheck(int a, int b) throws ArithmeticException {
        return mulAndCheck(a, b, "overflow: mul", false);
    }

    public static int mulAndLimit(int a, int b) {
        return mulAndCheck(a, b, "overflow: mul", true);
    }

    private static int mulAndCheck(int a, int b, String msg, boolean limit) {
        int ret;
        if (a > b) {
            ret = mulAndCheck(b, a, msg, limit);
        } else if (a < 0) {
            if (b < 0) {
                if (a >= 2147483647 / b) {
                    ret = a * b;
                } else {
                    if (!limit) {
                        throw new ArithmeticException(msg);
                    }

                    ret = 2147483647;
                }
            } else if (b > 0) {
                if (-2147483648 / b <= a) {
                    ret = a * b;
                } else {
                    if (!limit) {
                        throw new ArithmeticException(msg);
                    }

                    ret = -2147483648;
                }
            } else {
                ret = 0;
            }
        } else if (a > 0) {
            if (a <= 2147483647 / b) {
                ret = a * b;
            } else {
                if (!limit) {
                    throw new ArithmeticException(msg);
                }

                ret = 2147483647;
            }
        } else {
            ret = 0;
        }

        return ret;
    }

    public static long mulAndCheck(long a, long b) throws ArithmeticException {
        return mulAndCheck(a, b, "overflow: mul", false);
    }

    public static long mulAndLimit(long a, long b) {
        return mulAndCheck(a, b, "overflow: mul", true);
    }

    private static long mulAndCheck(long a, long b, String msg, boolean limit) {
        long ret;
        if (a > b) {
            ret = mulAndCheck(b, a, msg, limit);
        } else if (a < 0L) {
            if (b < 0L) {
                if (a >= 9223372036854775807L / b) {
                    ret = a * b;
                } else {
                    if (!limit) {
                        throw new ArithmeticException(msg);
                    }

                    ret = 9223372036854775807L;
                }
            } else if (b > 0L) {
                if (-9223372036854775808L / b <= a) {
                    ret = a * b;
                } else {
                    if (!limit) {
                        throw new ArithmeticException(msg);
                    }

                    ret = -9223372036854775808L;
                }
            } else {
                ret = 0L;
            }
        } else if (a > 0L) {
            if (a <= 9223372036854775807L / b) {
                ret = a * b;
            } else {
                if (!limit) {
                    throw new ArithmeticException(msg);
                }

                ret = 9223372036854775807L;
            }
        } else {
            ret = 0L;
        }

        return ret;
    }
}
