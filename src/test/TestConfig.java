package test;

import com.haochen.renju.storage.Cell;

import java.io.*;

/**
 * Created by Haochen on 2017/3/21.
 * TODO:
 */
public class TestConfig {
    public static void init() {
        BufferedReader fBr = null;
        BufferedReader tBr = null;
        try {
            fBr = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(new File(Test.Path.VCF, "vcf_count.txt"))));
            Test.QuesCount.vcf = Integer.parseInt(fBr.readLine());

            tBr = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(new File(Test.Path.VCT, "vct_count.txt"))));
            Test.QuesCount.vct = Integer.parseInt(tBr.readLine());
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

    public static class Test {
        public static class Path {
            public static final File VCF = new File("renju_test/vcf");
            public static final File VCT = new File("renju_test/vct");
        }
        public static int color = Cell.BLACK;
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
