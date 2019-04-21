import java.io.*;
import java.util.Map;
import java.nio.file.*;

import org.docopt.Docopt;

public class JPBO {

    private static final double VERSION = 0.1;

    private static final String DOC = "JPBO Version " + VERSION + ".\n"
            + "\n"
            + "Usage:\n"
            + "  JPBO <filepath> [--output=<outDir>] [--threads=<n>]\n"
            + "  JPBO (-h | --help)\n"
            + "  JPBO (-v | --version)\n"
            + "\n"
            + "Options:\n"
            + "  --output=<outDir>  Directory for unpacking into.\n"
            + "  --threads=<n>      Number of threads to use [default: 1].\n"
            + "  -h --help          Show this screen.\n"
            + "  -v --version       Show version.\n"
            + "\n";
    
    public static void main(String[] args) throws IOException, InterruptedException {
        final Map<String, Object> opts = new Docopt(DOC)
                .withVersion("JPBO " + VERSION)
                .parse(args);
        System.out.println(opts);

        String filepath = opts.get("<filepath>").toString();
        if (PBO.validPBOFile(filepath)) {
            long startTime = System.currentTimeMillis();
            PBO pbo = PBO.read(filepath);
            System.out.println(pbo);

            int threadCount = Integer.parseInt(opts.get("--threads").toString());
            Object outputArg = opts.get("--output");
            String outputDir = outputArg == null ? pbo.getFilename() : outputArg.toString() + File.separator + pbo.getFilename();

            pbo.unpack(outputDir, threadCount);

            long endTime = System.currentTimeMillis();
            System.out.println("\nPBO file read and unpacked in: " + (float) (endTime - startTime) / 1000 + " seconds" );
        } else {
            System.err.println("Please input a valid PBO file");
        }
    }
}
