package com.cer.device.utils;

import android.util.Log;

public final class Logger {

    private static final String ADB_LOG_CONTROL = "gmrz";

    private static boolean a = true;

    public static int i(String var0, String var1) {
        syncDebugStatus();
        if (!a) {
            return 8;
        } else {
            int var2;
            if ((var2 = var1.length()) <= 4000) {
                Log.i(var0, var1);
            } else {
                int var3 = 0;
                int var4 = 0;

                do {
                    var4 += Math.min(var2, 4000);
                    var2 -= var4 - var3;
                    Log.i(var0, var1.substring(var3, var4));
                    var3 += 4000;
                } while (var2 > 0);
            }

            return 4;
        }
    }

    public static int i(String var0, String var1, Throwable var2) {
        syncDebugStatus();
        return i(var0, var1 + ": " + var2.toString());
    }

    public static int i(String var0, String var1, byte[] var2) {
        syncDebugStatus();
        return !a ? 8 : i(var0, a(var1, var2));
    }

    public static int v(String var0, String var1) {
        syncDebugStatus();
        if (!a) {
            return 8;
        } else {
            int var2;
            if ((var2 = var1.length()) <= 4000) {
                Log.v(var0, var1);
            } else {
                int var3 = 0;
                int var4 = 0;

                do {
                    var4 += Math.min(var2, 4000);
                    var2 -= var4 - var3;
                    Log.v(var0, var1.substring(var3, var4));
                    var3 += 4000;
                } while (var2 > 0);
            }

            return 2;
        }
    }

    public static int v(String var0, String var1, Throwable var2) {
        syncDebugStatus();
        return v(var0, var1 + ": " + var2.toString());
    }

    public static int v(String var0, String var1, byte[] var2) {
        syncDebugStatus();
        return !a ? 8 : v(var0, a(var1, var2));
    }

    public static int w(String var0, String var1) {
        syncDebugStatus();
        if (!a) {
            return 8;
        } else {
            int var2;
            if ((var2 = var1.length()) <= 4000) {
                Log.w(var0, var1);
            } else {
                int var3 = 0;
                int var4 = 0;

                do {
                    var4 += Math.min(var2, 4000);
                    var2 -= var4 - var3;
                    Log.w(var0, var1.substring(var3, var4));
                    var3 += 4000;
                } while (var2 > 0);
            }

            return 5;
        }
    }

    public static int w(String var0, String var1, Throwable var2) {
        syncDebugStatus();
        return w(var0, var1 + ": " + var2.toString());
    }

    public static int w(String var0, String var1, byte[] var2) {
        syncDebugStatus();
        return !a ? 8 : w(var0, a(var1, var2));
    }

    public static int d(String var0, String var1) {
        syncDebugStatus();
        if (!a) {
            return 8;
        } else {
            int var2;
            if ((var2 = var1.length()) <= 4000) {
                Log.d(var0, var1);
            } else {
                int var3 = 0;
                int var4 = 0;

                do {
                    var4 += Math.min(var2, 4000);
                    var2 -= var4 - var3;
                    Log.d(var0, var1.substring(var3, var4));
                    var3 += 4000;
                } while (var2 > 0);
            }

            return 3;
        }
    }

    public static int d(String var0, String var1, Throwable var2) {
        syncDebugStatus();
        return d(var0, var1 + ": " + var2.toString());
    }

    public static int d(String var0, String var1, byte[] var2) {
        syncDebugStatus();
        return !a ? 8 : Log.d(var0, a(var1, var2));
    }

    public static int e(String var0, String var1) {
        syncDebugStatus();
        if (!a) {
            return 8;
        } else {
            int var2;
            if ((var2 = var1.length()) <= 4000) {
                Log.e(var0, var1);
            } else {
                int var3 = 0;
                int var4 = 0;

                do {
                    var4 += Math.min(var2, 4000);
                    var2 -= var4 - var3;
                    Log.e(var0, var1.substring(var3, var4));
                    var3 += 4000;
                } while (var2 > 0);
            }

            return 6;
        }
    }

    public static int e(String var0, String var1, Throwable var2) {
        syncDebugStatus();
        return !a ? 8 : Log.e(var0, var1, var2);
    }

    public static int e(String var0, String var1, byte[] var2) {
        syncDebugStatus();
        return !a ? 8 : e(var0, a(var1, var2));
    }

    private static String a(String var0, byte[] var1) {
        StringBuilder var2 = new StringBuilder();
        if (var0 != null) {
            var2.append(var0);
        }

        if (var1 == null) {
            var2.append(":null");
            return var2.toString();
        } else {
            int var3 = var1.length;
            var2.append("(").append(var3).append("):\n");

            for (int var4 = 0; var4 < var3; var4 += 16) {
                var2.append(String.format("%06x:", var4));

                int var5;
                for (var5 = 0; var5 < 16; ++var5) {
                    if (var4 + var5 < var3) {
                        var2.append(String.format("%02x ", var1[var4 + var5] & 255));
                    } else {
                        var2.append("   ");
                    }
                }

                var2.append(" ");

                for (var5 = 0; var5 < 16; ++var5) {
                    if (var4 + var5 < var3) {
                        char var6 = (char) (var1[var4 + var5] & 255);
                        var2.append(String.format("%c", var6 >= ' ' && var6 <= '~' ? var6 : '.'));
                    }
                }

                var2.append("\n");
            }

            return var2.toString();
        }
    }

    private static void syncDebugStatus() {
        if (Log.isLoggable(ADB_LOG_CONTROL, Log.VERBOSE))
            a = true;
    }
}
