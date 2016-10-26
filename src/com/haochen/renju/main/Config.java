package com.haochen.renju.main;

import com.haochen.renju.storage.PieceColor;

import java.io.*;

/**
 * Created by Haochen on 2016/9/23.
 */
public class Config {

    public static void init() {
        BufferedReader fBr = null;
        BufferedReader tBr = null;
        try {
            fBr = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(new File(Config.Test.Path.VCF, "vcf_count.txt"))));
            Config.Test.QuesCount.vcf = Integer.parseInt(fBr.readLine());

            tBr = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(new File(Config.Test.Path.VCT, "vct_count.txt"))));
            Config.Test.QuesCount.vct = Integer.parseInt(tBr.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fBr != null) {
                try {
                    fBr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (tBr != null) {
                try {
                    tBr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class AILevel {
        public static int level;
        public static final int NONE = -1;
        public static final int LOW = 1;
        public static final int NORMAL = 2;
        public static final int HIGH = 3;
    }

    public static class Test {
        public static class Path {
            public static final File VCF = new File("renju_test/vcf");
            public static final File VCT = new File("renju_test/vct");
        }
        public static PieceColor color = PieceColor.BLACK;
        public static class QuesCount {
            public static int vcf;
            public static int vct;
        }

        public static ObjectInputStream createVCFStream(String fileName) {
            File file = new File(Path.VCF, fileName);
            ObjectInputStream stream = null;
            try {
                stream = new ObjectInputStream(new FileInputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stream;
        }

        public static ObjectInputStream createVCTStream(String fileName) {
            File file = new File(Path.VCT, fileName);
            ObjectInputStream stream = null;
            try {
                stream = new ObjectInputStream(new FileInputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stream;
        }
    }
}
